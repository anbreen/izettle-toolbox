package model;

import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Paging {

    @Getter
    protected final int pagingSize;
    private String pagingState;

    public Optional<String> getPagingState() {
        return Optional.ofNullable(pagingState);
    }

    public Paging pagingState(final String pagingState) {
        this.pagingState = pagingState;
        return this;
    }
}
