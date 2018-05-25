package com.izettle.cassandra.paging;

import java.util.List;
import java.util.Optional;

public class PagingResult<R> implements PagingContext {

    private List<R> results;
    private String pagingState;
    private int fetchSize;

    public PagingResult(final List<R> results, final String pagingState, final int fetchSize) {
        this.results = results;
        this.pagingState = pagingState;
        this.fetchSize = fetchSize;
    }

    public int getFetchSize() {
        return fetchSize;
    }

    public List<R> getResults() {
        return results;
    }

    public Optional<String> getPagingState() {
        return Optional.ofNullable(pagingState);
    }

    public PagingResult<R> withResult(final List<R> results, final String pagingState) {
        return new PagingResult<>(results, pagingState, fetchSize);
    }
}
