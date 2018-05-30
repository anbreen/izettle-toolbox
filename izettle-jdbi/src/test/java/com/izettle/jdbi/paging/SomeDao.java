package com.izettle.jdbi.paging;

import java.util.List;
import java.util.function.Consumer;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.mixins.GetHandle;

public abstract class SomeDao implements AllDao, GetHandle {

    static final int LIMIT = 3;

    @SqlUpdate("insert into something (id, name) values (:id, :name)")
    public abstract void insert(@Bind("id") int id, @Bind("name") String name);

    @SqlQuery("select name from something where id = :id")
    public abstract String findNameById(@Bind("id") int id);

    public void getAll(final Consumer<List<Something>> consumer) {
        Query<Something> query = getHandle()
            .createQuery("select * from something limit :limit offset :offset")
            .map((i, rs, statementContext) -> new Something(rs.getString("name")));

        new JDBIPagination<Something>(LIMIT)
            .execute(query, consumer);
    }

    ;
}
