package model;

import java.util.UUID;

public interface GdprDao {

    void deleteAll(GdprEvent event);

    PagingResult<?> getAll(Paging paging);

    String getDescription();

}
