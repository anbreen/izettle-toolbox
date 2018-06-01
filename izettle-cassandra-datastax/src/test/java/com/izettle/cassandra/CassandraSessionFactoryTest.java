package com.izettle.cassandra;

import static com.google.common.truth.Truth.assertThat;
import static org.cassandraunit.utils.EmbeddedCassandraServerHelper.getSession;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.datastax.driver.core.Session;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Environment;
import java.util.Arrays;
import java.util.HashSet;
import org.cassandraunit.CQLDataLoader;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.Before;
import org.junit.Test;

public class CassandraSessionFactoryTest {
    private final Environment environment = mock(Environment.class);
    private final LifecycleEnvironment lifecycle = mock(LifecycleEnvironment.class);
    private final TestApplication application = new TestApplication();
    private final TestConfiguration config = new TestConfiguration();
    private final CassandraSessionFactory cassandraSessionFactory = new CassandraSessionFactory();

    @Before
    public void before() throws Exception {
        EmbeddedCassandraServerHelper.startEmbeddedCassandra();
        CQLDataLoader dataLoader = new CQLDataLoader(getSession());
        dataLoader.load(new ClassPathCQLDataSet("testDataSet.cql", true, true, "skyrim"));

        cassandraSessionFactory.setContactPoints(new HashSet<String>(Arrays.asList("localhost")));
        cassandraSessionFactory.setPort(9142);
        cassandraSessionFactory.setKeySpace("skyrim");
        config.setCassandraSessionFactory(cassandraSessionFactory);
        when(environment.lifecycle()).thenReturn(lifecycle);
    }

    @Test
    public void useConfiguredValues() throws Exception {
        application.run(config, environment);
        Session session = application.getCassandraSessionManaged().getSession();
        assertThat(session.getLoggedKeyspace()).isEqualTo("skyrim");
    }

    @Test
    public void manageCassandraSession() throws Exception {
        application.run(config, environment);
        verify(lifecycle).manage(application.getCassandraSessionManaged());
    }
}
