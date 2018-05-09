package com.izettle.gdpr.cassandra;

import com.izettle.gdpr.model.GdprEvent;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Paging {

    @Getter
    protected final int pagingSize;
    private String pagingState;
    private final GdprEvent event;

    public Optional<String> getPagingState() {
        return Optional.ofNullable(pagingState);
    }

    public Paging pagingState(final String pagingState) {
        this.pagingState = pagingState;
        return this;
    }
}
