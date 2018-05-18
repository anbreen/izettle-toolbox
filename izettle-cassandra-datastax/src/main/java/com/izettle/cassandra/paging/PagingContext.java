package com.izettle.cassandra.paging;

import java.util.Optional;

public interface PagingContext {

    int getFetchSize();

    default Optional<String> getPagingState() {
        return Optional.empty();
    }

    static PagingContext create(int fetchSize) {
        return () -> fetchSize;
    }

}
