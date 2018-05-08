package com.izettle.gdpr.dao;

import com.izettle.gdpr.cassandra.Paging;
import com.izettle.gdpr.cassandra.PagingResult;
import com.izettle.gdpr.model.GdprEvent;

public interface GdprDao {

    void deleteAll(GdprEvent event);

    PagingResult<?> getAll(Paging paging);

    String getDescription();

}
