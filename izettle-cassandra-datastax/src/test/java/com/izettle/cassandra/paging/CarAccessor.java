package com.izettle.cassandra.paging;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;

@Accessor
@FunctionalInterface
public interface CarAccessor {

    @Query("SELECT * FROM paging.car where owner=?")
    Result<Car> getAll(String owner);

}
