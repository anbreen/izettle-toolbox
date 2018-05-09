package com.izettle.gdpr.cassandra;

import com.izettle.gdpr.model.GdprEvent;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class PagingResult<T> extends Paging {

    @NonNull
    private final List<T> items;

    public PagingResult(final int pagingSize, final List<T> items, GdprEvent event) {
        super(pagingSize, event);
        this.items = items;
    }

    public boolean isFullyFetched() {
        return !getPagingState().isPresent();
    }
}
