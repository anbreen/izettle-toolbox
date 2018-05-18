package com.izettle.cassandra.paging;

import static java.util.Objects.requireNonNull;

import com.datastax.driver.core.PagingState;
import java.util.Optional;
import javax.annotation.Nullable;

public class PagingData<D> implements PagingContext, PagingInput<D> {

    private static final String EMPTY_PAGE = null;

    private D data;
    private String pagingState;
    private final int fetchSize;

    public PagingData(PagingData<D> other) {
        this.data = other.getData();
        setPaging(other.pagingState);
        this.fetchSize = other.getFetchSize();
    }

    public PagingData(PagingInput<D> input) {
        this.data = input.getData();
        this.fetchSize = input.getFetchSize();
    }

    public PagingData(final D input, int fetchSize) {
        this.data = requireNonNull(input, "input must not be null");
        this.fetchSize = fetchSize;
    }

    public PagingData(final D input, @Nullable final PagingState pagingState, int fetchSize) {
        setPaging(pagingState);
        this.data = input;
        this.fetchSize = fetchSize;
    }

    public PagingData(final D input, @Nullable final String pagingState, int fetchSize) {
        setPaging(pagingState);
        this.data = input;
        this.fetchSize = fetchSize;
    }

    public Optional<String> getPagingState() {
        return Optional.ofNullable(pagingState);
    }

    public D getData() {
        return data;
    }

    public PagingData<D> setPaging(@Nullable final PagingState page) {
        this.pagingState = page != null ? page.toString() : EMPTY_PAGE;
        return this;
    }

    public PagingData<D> setPaging(@Nullable final String page) {
        this.pagingState = page;
        return this;
    }

    public int getFetchSize() {
        return fetchSize;
    }
}
