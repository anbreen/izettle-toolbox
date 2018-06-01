package com.izettle.jdbi.paging;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import com.opentable.db.postgres.junit.EmbeddedPostgresRules;
import com.opentable.db.postgres.junit.SingleInstancePostgresRule;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class JdbiBatchExecutorTest {

    private DBI dbi;

    @Rule
    public SingleInstancePostgresRule pg = EmbeddedPostgresRules.singleInstance();

    @Before
    public void setup() {
        EmbeddedPostgres psql = pg.getEmbeddedPostgres();
        dbi = new DBI(psql.getDatabase("postgres", "postgres"));
        dbi.open().execute("create table something (id int primary key, name varchar(100))");
    }

    @Test
    public void pagingScenarioEvenNrOfPages() {
        createSomethings(9);
        SomeDao someDao = dbi.open(SomeDao.class);

        List<List<Something>> actual = new ArrayList<>();

        someDao.getAllStartingWith("Thing", actual::add);

        assertThat(actual.size(), is(3));
        assertThat(actual.get(0).size(), is(SomeDao.LIMIT));
        assertThat(actual.get(1).size(), is(SomeDao.LIMIT));
        assertThat(actual.get(2).size(), is(SomeDao.LIMIT));

        dbi.close(someDao);
    }

    @Test
    public void pagingScenarioOddNrOfPages() {
        createSomethings(11);
        SomeDao someDao = dbi.open(SomeDao.class);

        List<List<Something>> actual = new ArrayList<>();

        someDao.getAllStartingWith("Thing", actual::add);

        assertThat(actual.size(), is(4));
        assertThat(actual.get(0).size(), is(SomeDao.LIMIT));
        assertThat(actual.get(1).size(), is(SomeDao.LIMIT));
        assertThat(actual.get(2).size(), is(SomeDao.LIMIT));
        assertThat(actual.get(3).size(), is(2));

        dbi.close(someDao);
    }

    @Test
    public void shouldInsertLimitOffsetIfSemicolonExists() {
        Query<String> query = createMappedQuery("select name from something;");

        new JdbiBatchExecutor<String>(2).execute(query, s -> {
        }); // Just don't fail
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIAEIfLimitExists() {
        Query<String> query = createMappedQuery("select name from something lIMit 10");

        new JdbiBatchExecutor<String>(2).execute(query, s -> {
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIAEIfOffsetExists() {
        Query<String> query = createMappedQuery("select name from something Offset 10");

        new JdbiBatchExecutor<String>(2).execute(query, s -> {
        });
    }

    //
    // Private helpers
    //

    private Query<String> createMappedQuery(final String query) {
        return dbi.open().createQuery(query).map(new ResultSetMapper<String>() {
            @Override
            public String map(final int index, final ResultSet r, final StatementContext ctx) throws SQLException {
                return r.getString(1);
            }
        });
    }

    private void createSomethings(final int number) {
        final SomeDao someDao = dbi.open(SomeDao.class);
        IntStream.rangeClosed(1, number).boxed()
            .forEach(i -> someDao.insert(i, "Thing" + i));

        dbi.close(someDao);
    }

}
