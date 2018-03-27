package com.izettle.data.migrator.dropwizard;

import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

import com.google.common.collect.ImmutableMultimap;
import com.izettle.cassandra.CassandraSessionManaged;
import com.izettle.data.migrator.cassandra.datastax.CassandraDataStaxMigratorParameters;
import com.izettle.data.migrator.core.BatchDataMigrator;
import com.izettle.data.migrator.core.MigrationThrottlingParameters;
import io.dropwizard.servlets.tasks.Task;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dropwizard task that runs {@link BatchDataMigrator}s based on Cassandra's DataStax driver.
 */
public class BatchDataMigratorTask extends Task {

    private static final Logger LOG = LoggerFactory.getLogger(BatchDataMigratorTask.class);

    private final Function<CassandraDataStaxMigratorParameters, List<BatchDataMigrator<?, ?>>>
        databaseMigratorsSupplier;
    private final AtomicReference<List<BatchDataMigrator<?, ?>>> currentMigrators = new AtomicReference<>();
    private final CassandraSessionManaged cassandraSession;
    private final AtomicReference<MigrationThrottlingParameters> migrationThrottlingParameters
        = new AtomicReference<>(MigrationThrottlingParameters.DEFAULT);

    /**
     * Create a task to run Cassandra-DataStax-based data migrators.
     * @param cassandraSession a Cassandra session to be used exclusively by the migrators. The session will be
     *                         managed by this task, being started and closed when appropriate.
     * @param databaseMigratorsSupplier supplier of migrators to be run.
     */
    public BatchDataMigratorTask(
        CassandraSessionManaged cassandraSession,
        Function<CassandraDataStaxMigratorParameters, List<BatchDataMigrator<?, ?>>> databaseMigratorsSupplier
    ) {
        super("batch-migrator");
        this.cassandraSession = cassandraSession;
        this.databaseMigratorsSupplier = databaseMigratorsSupplier;
    }

    @Override
    public void execute(ImmutableMultimap<String, String> parameters, PrintWriter out) {
        int batchSize, maxThreads, rowsToVerifyPerBatch;

        try {
            batchSize = Integer.parseInt(parameters.get("batchSize").iterator().next());
            maxThreads = Integer.parseInt(parameters.get("maxThreads").iterator().next());
            rowsToVerifyPerBatch = Integer.parseInt(parameters.get("rowsToVerifyPerBatch").iterator().next());
        } catch (Exception e) {
            out.println("ERROR (invalid argument): " + e);
            return;
        }

        stopPreviousCassandraSession();

        LOG.info("Starting a full Cassandra database data migration");

        // start a session just to run the migrators... only close this session in case the migration gets
        // cancelled (at the end of the batch migration, the migrators should continue active in order to
        // perform the migration of any new items that the old DAOs create or update).
        cassandraSession.start();

        List<BatchDataMigrator<?, ?>> migrators = databaseMigratorsSupplier.apply(
            new CassandraDataStaxMigratorParameters(
                cassandraSession.getSession(),
                batchSize,
                maxThreads,
                rowsToVerifyPerBatch
            ));

        synchronized (currentMigrators) {
            if (currentMigrators.get() == null) {
                currentMigrators.set(migrators);
            } else {
                out.println("Ignoring request to run BatchDataMigratorTask: task is already running.");
                return;
            }
        }

        // when one migrator is cancelled, all others should be also cancelled
        migrators.forEach(m -> m.setOnCancelCallback(() -> {
            cancel(null);
            // call is idempotent, so we can call it multiple times without error
            cassandraSession.stop();
        }));

        // let migrators know about the throttling policy
        migrators.forEach(m -> m.setThrottlingParameters(migrationThrottlingParameters.get()));

        out.println("Batch Data Migrator running with the following parameters:");
        out.println("  * batchSize: " + batchSize);
        out.println("  * maxThreads: " + maxThreads);
        out.println("  * rowsToVerifyPerBatch: " + rowsToVerifyPerBatch);

        // run migrators each in a different Thread
        final ExecutorService executor = Executors.newFixedThreadPool(migrators.size());
        final CompletionService<Void> completionService = new ExecutorCompletionService<>(executor);
        migrators.forEach(m -> completionService.submit(m::runFullMigration, null));

        // wait for all migrators to finish asynchronously
        out.println("The following database migrators have been scheduled: " + migrators
            .stream().map(BatchDataMigrator::getName).collect(joining(", ")));

        new Thread(() -> {
            int migratorCount = migrators.size();
            while (migratorCount > 0) {
                try {
                    completionService.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    migratorCount--;
                }
            }

            LOG.info("All database migrators have finished running");

            executor.shutdown();

            // mark as not executed so that the task can be re-run if necessary
            currentMigrators.set(null);
        }, "database-migrator-scheduler").start();
    }

    private void stopPreviousCassandraSession() {
        LOG.info("Stopping a previous data migration Cassandra Session");
        cassandraSession.stop();
    }

    public List<BatchDataMigrator<?, ?>> getCurrentMigrators() {
        List<BatchDataMigrator<?, ?>> activeMigrators = currentMigrators.get();
        if (activeMigrators == null) {
            return emptyList();
        }
        return activeMigrators;
    }

    public void setThrottling(MigrationThrottlingParameters parameters) {
        requireNonNull(parameters, "Cannot accept null MigrationThrottlingParameters");
        migrationThrottlingParameters.set(parameters);

        for (BatchDataMigrator<?, ?> batchDataMigrator : getCurrentMigrators()) {
            batchDataMigrator.setThrottlingParameters(parameters);
        }
    }

    public void cancel(@Nullable PrintWriter out) {
        Optional.ofNullable(currentMigrators.getAndSet(null))
            .ifPresent(migrators -> {
                if (out != null) {
                    out.println("Cancelled the following migrators: "
                        + migrators.stream().map(BatchDataMigrator::getName).collect(joining(", ")));
                }
                migrators.forEach(BatchDataMigrator::cancel);
            });
    }

}
