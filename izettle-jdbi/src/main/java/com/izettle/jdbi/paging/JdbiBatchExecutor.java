package com.izettle.jdbi.paging;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;
import java.util.function.Consumer;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.StatementLocator;

public class JdbiBatchExecutor<T> {

    private final int limit;

    public JdbiBatchExecutor(final int limit) {
        this.limit = limit;
    }

    public void execute(final Query<T> query, final Consumer<List<T>> consumer) {

        StatementContext ctx = query.getContext();
        String rawSql = ctx.getRawSql();
        checkArgument(!rawSql.toUpperCase().contains("LIMIT"), "Query should not specify a LIMIT.");
        checkArgument(!rawSql.toUpperCase().contains("OFFSET"), "Query should not specify an OFFSET.");

        query.setStatementLocator(new LimitOffsetStatementRewriter());

        List<T> result = null;
        int offset = 0;

        query.bind("limit", limit);
        query.setFetchSize(limit);

        while (true) {
            query.bind("offset", offset);

            result = query.list();

            if (!result.isEmpty()) {
                consumer.accept(result);
                offset += limit;
            } else {
                return;
            }
        }
    }

    /*
     StatementLocator is a way more simple API than the StatementRewriter. For instance, we don't need to handle param
     bindings. Also, setting the StatementRewriter seems to overwrite some of the default behaviour in JDBI that you
     then need to take care of your self.
     We use the StatementLocator to rewrite the query, not just find the query at some "location".
     */
    private static class LimitOffsetStatementRewriter implements StatementLocator {
        static final String LIMIT_OFFSET_STATEMENT = " LIMIT :limit OFFSET :offset";

        @Override
        public String locate(final String name, final StatementContext ctx) throws Exception {
            if (name.endsWith(";")) {
                return name.substring(0, name.length() - 1) + LIMIT_OFFSET_STATEMENT;
            }

            return name + LIMIT_OFFSET_STATEMENT;
        }
    }
}
