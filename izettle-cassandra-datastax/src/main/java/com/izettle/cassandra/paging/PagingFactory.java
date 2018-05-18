package com.izettle.cassandra.paging;

import static java.util.Objects.requireNonNull;

import com.datastax.driver.core.ExecutionInfo;
import com.datastax.driver.core.PagingIterable;
import com.datastax.driver.core.PagingState;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.Mapper;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.izettle.java.ValueChecks;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PagingFactory<R> {

    private Session session;
    private PagingResultMapper<R> mapper;
    private int fetchSize = 100;

    public PagingFactory(final Session session, final Function<Row, R> mapper) {
        this.session = requireNonNull(session, "session must not be null");
        this.mapper = new PagingResultMapper<>(mapper);
    }

    public PagingFactory(final Session session, final Function<Row, R> mapper, int fetchSize) {
        ValueChecks.assertTrue(fetchSize > 0, "Fetch size must be at least 1");
        requireNonNull(mapper, "mapper must not be null");
        this.fetchSize = fetchSize;
        this.session = requireNonNull(session, "session must not be null");
        this.mapper = new PagingResultMapper<>(mapper);
    }

    public PagingFactory(final Session session, final Mapper<R> mapper) {
        this.session = requireNonNull(session, "session must not be null");
        requireNonNull(mapper, "mapper must not be null");
        this.mapper = new PagingResultMapper<>(mapper);
    }

    public PagingResult<R> execute(Statement statement, PagingContext context) {
        requireNonNull(statement, "statement must not be null");
        requireNonNull(context, "context must not be null");
        return new CassandraPaging(this, statement, context).execute();
    }

    public void execute(Statement statement, Consumer<List<R>> resultConsumer) {
        execute(statement, resultConsumer, PagingContext.create(fetchSize));
    }

    public void execute(Statement statement, Consumer<List<R>> resultConsumer, int fetchSize) {
        ValueChecks.assertTrue(fetchSize > 0, "Fetch size must be at least 1");
        execute(statement, resultConsumer, PagingContext.create(fetchSize));
    }

    public void execute(Statement statement, Consumer<List<R>> resultConsumer, PagingContext context) {
        requireNonNull(statement, "statement must not be null");
        requireNonNull(resultConsumer, "resultConsumer must not be null");
        requireNonNull(context, "context must not be null");

        final CassandraPaging paging = new CassandraPaging(this, statement, context);
        paging.iterate(resultConsumer);
    }

    public class CassandraPaging {

        private final PagingFactory<R> factory;
        private final Statement statement;
        private PagingResult<R> result;

        CassandraPaging(PagingFactory<R> factory, Statement statement, PagingContext data) {
            this.statement = statement;
            this.factory = factory;
            result =
                new PagingResult<>(Collections.emptyList(), data.getPagingState().orElse(null), data.getFetchSize());
        }

        private void iterate(Consumer<List<R>> resultConsumer) {

            do {
                execute();
                resultConsumer.accept(result.getResults());
            } while (result.getPagingState().isPresent());
        }

        private PagingResult<R> execute() {
            statement.setFetchSize(result.getFetchSize());
            result.getPagingState().map(PagingState::fromString).ifPresent(statement::setPagingState);
            final List<R> results = new ArrayList<>();
            final ResultSet resultSet = session.execute(statement);
            final PagingState newPagingState = resultSet.getExecutionInfo().getPagingState();
            final PagingIterable<?, R> pagingIterable = factory.mapper.map(resultSet);
            final Iterator<R> iterator = pagingIterable.iterator();
            while (results.size() < result.getFetchSize() && iterator.hasNext()) {
                results.add(iterator.next());
            }
            final String pagingState = resultSet.isExhausted() ? null : newPagingState.toString();
            this.result = result.withResult(results, pagingState);
            return result;
        }
    }

    class PagingResultMapper<R> {

        private final Function<ResultSet, PagingIterable<?, R>> iterableFunction;

        PagingResultMapper(final Function<Row, R> mapper) {
            requireNonNull(mapper, "mapper must not be null");
            this.iterableFunction = new ManualMapper<>(mapper)::map;
        }

        PagingResultMapper(final Mapper<R> mapper) {
            this.iterableFunction = mapper::map;
        }

        public PagingIterable<?, R> map(ResultSet rs) {
            return iterableFunction.apply(rs);
        }

        class ManualMapper<R> {

            private final Function<Row, R> mapper;

            private ManualMapper(final Function<Row, R> mapper) {
                this.mapper = mapper;
            }

            private PagingIterable<?, R> map(ResultSet rs) {
                return new ManualPagingIterable<>(rs, mapper);

            }

            public class ManualPagingIterable<R> implements PagingIterable<ManualPagingIterable<R>, R> {

                private final ResultSet rs;
                private final Function<Row, R> mapper;

                ManualPagingIterable(final ResultSet rs, final Function<Row, R> mapper) {
                    this.rs = rs;
                    this.mapper = mapper;
                }

                @Override
                public boolean isExhausted() {
                    return rs.isExhausted();
                }

                @Override
                public boolean isFullyFetched() {
                    return rs.isFullyFetched();
                }

                @Override
                public int getAvailableWithoutFetching() {
                    return rs.getAvailableWithoutFetching();
                }

                @Override
                public ListenableFuture<ManualPagingIterable<R>> fetchMoreResults() {
                    return Futures.transform(rs.fetchMoreResults(), rs -> ManualMapper.ManualPagingIterable.this);
                }

                @Override
                public R one() {
                    return mapper.apply(rs.one());
                }

                @Override
                public List<R> all() {
                    return rs.all().stream().map(mapper).collect(Collectors.toList());
                }

                @Override
                public Iterator<R> iterator() {
                    return new Iterator<R>() {
                        private final Iterator<Row> rowIterator = rs.iterator();

                        @Override
                        public boolean hasNext() {
                            return rowIterator.hasNext();
                        }

                        @Override
                        public R next() {
                            return mapper.apply(rowIterator.next());
                        }

                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
                }

                @Override
                public ExecutionInfo getExecutionInfo() {
                    return rs.getExecutionInfo();
                }

                @Override
                public List<ExecutionInfo> getAllExecutionInfo() {
                    return rs.getAllExecutionInfo();
                }
            }
        }

    }
}
