package com.izettle.cassandra.paging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cassandraunit.utils.EmbeddedCassandraServerHelper.getSession;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.thrift.transport.TTransportException;
import org.assertj.core.api.Assertions;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PagingTest {

    private static MappingManager mappingManager;
    private static Mapper<Car> mapper;
    private static CarAccessor carAccessor;
    private static PagingFactory<Car> pagingFactory;
    private static Session session;

    private Statement statement;
    private String owner;

    @BeforeClass
    public static void beforeClass() throws InterruptedException, IOException, TTransportException {
        EmbeddedCassandraServerHelper.startEmbeddedCassandra();
        session = getSession();

        session.execute("CREATE KEYSPACE paging\n"
            + "  WITH REPLICATION = {\n"
            + "   'class' : 'SimpleStrategy',\n"
            + "   'replication_factor' : 1\n"
            + "  };");

        session.execute("CREATE TABLE paging.car (\n"
            + "  owner text,\n"
            + "  id timeuuid,\n"
            + "  brand text,\n"
            + "  year int,\n"
            + "  PRIMARY KEY (owner, id))\n"
            + " WITH CLUSTERING ORDER BY (id DESC);");

        mappingManager = new MappingManager(session);
        mapper = mappingManager.mapper(Car.class);
        carAccessor = mappingManager.createAccessor(CarAccessor.class);
        pagingFactory = new PagingFactory<>(session, mapper);

    }

    @Before
    public void before() {
        session.execute("Truncate paging.car;").wasApplied();
        owner = RandomStringUtils.randomAlphabetic(3);
        statement = carAccessor.getAll(owner).getExecutionInfo().getStatement();
    }

    @Test
    public void pageAllInOnePage() {
        final int numberOfCars = 10;
        final List<Car> expected = createCars(numberOfCars);
        final int pageSize = numberOfCars + 1;

        final PagingData<String> selectAll = new PagingData<>(owner, pageSize);
        final PagingResult<Car> result = pagingFactory.execute(statement, selectAll);

        assertThat(result.getResults()).containsAll(expected);
        assertThat(result.getPagingState()).isEmpty();

    }

    @Test
    public void pageExactResult() {

        final int numberOfCars = 4;
        final List<Car> expected = createCars(numberOfCars);

        final PagingData<String> selectAll = new PagingData<>(owner, numberOfCars);
        final PagingResult<Car> result = pagingFactory.execute(statement, selectAll);
        assertThat(result.getResults()).hasSize(numberOfCars);
        assertThat(result.getResults()).containsAll(expected);
        assertThat(result.getPagingState()).isEmpty();
    }

    @Test
    public void twoIteratorsExact() {
        final int numberOfCars = 4;
        final int pageSize = numberOfCars / 2;
        final List<Car> expected = createCars(numberOfCars);

        final PagingData<String> selectAll = new PagingData<>(owner, pageSize);
        final PagingResult<Car> first = pagingFactory.execute(statement, selectAll);
        final ArrayList<Car> cars = new ArrayList<>(first.getResults());
        Assertions.assertThat(first.getResults()).hasSize(pageSize);
        final PagingResult<Car> second = pagingFactory.execute(statement, first);
        cars.addAll(second.getResults());
        assertThat(cars).hasSize(numberOfCars);
        assertThat(cars).containsAll(expected);
        assertThat(second.getPagingState()).isEmpty();
    }

    @Test
    public void twoIterations() {
        final int numberOfCars = 9;
        final int pageSize = 5;
        final List<Car> expected = createCars(numberOfCars);

        final PagingData<String> selectAll = new PagingData<>(owner, pageSize);
        final PagingResult<Car> first = pagingFactory.execute(statement, selectAll);
        final ArrayList<Car> cars = new ArrayList<>(first.getResults());
        Assertions.assertThat(first.getResults()).hasSize(pageSize);
        final PagingResult<Car> second = pagingFactory.execute(statement, first);
        cars.addAll(second.getResults());

        assertThat(second.getResults()).hasSize(4);
        assertThat(cars).hasSize(numberOfCars);
        assertThat(cars).containsAll(expected);
        assertThat(second.getPagingState()).isEmpty();
    }

    @Test
    public void testConsumeFunction() {
        final int numberOfCars = 6;
        final int pageSize = 2;
        final List<Car> expected = createCars(numberOfCars);
        final List<Car> actual = new ArrayList<>();

        Consumer<List<Car>> resultConsumer = actual::addAll;

        pagingFactory.execute(statement, resultConsumer, pageSize);

        assertThat(actual).containsAll(expected);

    }

    private List<Car> createCars(int numberOfCars) {
        final List<Car> cars = Car.random(owner, numberOfCars);
        cars.forEach(mapper::save);
        return cars;
    }

}
