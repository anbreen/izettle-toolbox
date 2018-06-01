package com.izettle.jdbi.paging;

import java.util.List;
import java.util.function.Consumer;

public interface AllDao {
    void getAllStartingWith(final String prefix, final Consumer<List<Something>> consumer);
}
