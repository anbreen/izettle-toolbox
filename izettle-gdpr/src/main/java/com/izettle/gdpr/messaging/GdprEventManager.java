package com.izettle.gdpr.messaging;

import com.izettle.gdpr.exception.GdprDataNotDeleteException;
import com.izettle.gdpr.model.GdprEvent;
import com.izettle.gdpr.model.GdprStatusReportMessage;

public interface GdprEventManager {

    void handleForget(GdprEvent event);

    void handleExport(GdprEvent event);

    void uploadToS3(GdprEvent event, Object data, String sequenceIdentifier);

    void publish(GdprStatusReportMessage status);

    void assertDataIsDeleted(GdprEvent event) throws GdprDataNotDeleteException;

}
