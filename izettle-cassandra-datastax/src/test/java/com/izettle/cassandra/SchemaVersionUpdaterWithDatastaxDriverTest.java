package com.izettle.cassandra;

import static com.google.common.truth.Truth.assertThat;
import static org.cassandraunit.utils.EmbeddedCassandraServerHelper.getSession;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.CQLDataLoader;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

public class SchemaVersionUpdaterWithDatastaxDriverTest {
    private static final String TABLE_NAME = "schema_scripts_version";

    @BeforeClass
    public static void beforeClass() throws InterruptedException, IOException, TTransportException {
        EmbeddedCassandraServerHelper.startEmbeddedCassandra();
    }

    @After
    public void after() {
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
    }

    @Test
    public void shouldRunMigrationCQLs() throws IOException, URISyntaxException {
        load("dataset-legacy.cql");

        Session session = getLocalSession();
        new SchemaVersionUpdaterWithDatastaxDriver(session)
            .applyFromResources(SchemaVersionUpdaterWithDatastaxDriverTest.class, "migrations");

        KeyspaceMetadata keyspaceMetadata =
            session.getCluster().getMetadata().getKeyspace(session.getLoggedKeyspace());
        assertThat(keyspaceMetadata.getTable("galaxies")).isNotNull();
        assertThat(keyspaceMetadata.getTable("planets")).isNotNull();
    }

    @Test
    public void doNotApplyScriptAlreadyApplied() throws IOException, URISyntaxException {
        load("dataset-empty.cql");

        Session session = getLocalSession();
        createSchemaMigrationTable(session);
        session.execute(QueryBuilder.insertInto(TABLE_NAME)
            .value("key", "0003-before-the-big-bang.cql")
            .value("executed", new Date())
        );

        SchemaVersionUpdaterWithDatastaxDriver updater = new SchemaVersionUpdaterWithDatastaxDriver(session);
        updater.applyFromResources(SchemaVersionUpdaterWithDatastaxDriverTest.class, "migrations");

        KeyspaceMetadata keyspaceMetadata =
            session.getCluster().getMetadata().getKeyspace(session.getLoggedKeyspace());
        assertThat(keyspaceMetadata.getTable("galaxies")).isNull();
    }

    private void load(String dataSetLocation) {
        CQLDataLoader dataLoader = new CQLDataLoader(getSession());
        dataLoader.load(new ClassPathCQLDataSet(dataSetLocation, true, true, "schema_migration_test"));
    }

    private static Session getLocalSession() {
        return Cluster.builder()
            .addContactPoint("127.0.0.1").withPort(9142)
            .build()
            .connect("schema_migration_test");
    }

    private static void createSchemaMigrationTable(Session session) {
        session.execute("CREATE TABLE " + TABLE_NAME + " ("
            + "key text PRIMARY KEY,"
            + "executed timestamp"
            + ");");
    }
}
