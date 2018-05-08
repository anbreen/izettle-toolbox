package model;

import java.util.UUID;

public interface GdprEventHandler {

    void handleForget(GdprEvent event);

    void handleExport(GdprEvent event);

    void uploadToS3(GdprEvent event, Object data, String sequenceIdentifier);

    void publish(GdprStatusReportMessage status);



}
