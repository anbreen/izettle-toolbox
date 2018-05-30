package com.izettle.jdbi.paging;

import java.util.List;
import java.util.function.Consumer;

public interface AllDao {
    void getAll(final Consumer<List<Something>> consumer);
}
