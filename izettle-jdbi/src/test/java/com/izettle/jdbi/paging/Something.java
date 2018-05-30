package com.izettle.jdbi.paging;

public class Something {
    private final String name;

    public Something(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
