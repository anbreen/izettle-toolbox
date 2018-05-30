package com.izettle.jdbi.paging;

import java.util.List;
import java.util.function.Consumer;
import org.skife.jdbi.v2.Query;

public class JDBIPagination<T> {
    private final int limit;

    public JDBIPagination(final int limit) {
        this.limit = limit;
    }

    public void execute(final Query<T> query, final Consumer<List<T>> consumer) {
        List<T> result;
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
}
