package com.izettle.cassandra.paging;

import static java.util.Objects.requireNonNull;

import com.izettle.java.ValueChecks;

public interface PagingInput<D> {

    int getFetchSize();

    D getData();

    static <D> PagingInput<D> createInput(D data, int fetchSize) {
        requireNonNull(data, "data must not be null");
        ValueChecks.assertTrue(fetchSize > 1, "FetchSize must at least 1");
        return new PagingInput<D>() {
            @Override
            public int getFetchSize() {
                return fetchSize;
            }

            @Override
            public D getData() {
                return data;
            }
        };
    }

    static PagingInput<Void> createVoidInput(int fetchSize) {
        final Void v = null;
        return createInput(v, fetchSize);
    }
}
