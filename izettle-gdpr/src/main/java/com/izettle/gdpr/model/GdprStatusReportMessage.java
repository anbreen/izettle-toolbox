package com.izettle.gdpr.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GdprStatusReportMessage {

    private static String EVENT_NAME = "GDPR_STATUS_UPDATE";
    //TODO add more metadata
    private String eventName = EVENT_NAME;
    private GdprStatus status;
    private GdprEvent event;

    public static GdprStatusReportMessage ack(GdprEvent event) {
        return GdprStatusReportMessage.builder()
            .event(event)
            .status(GdprStatus.ACK)
            .build();
    }

    public static GdprStatusReportMessage nack(GdprEvent event) {
        return GdprStatusReportMessage.builder()
            .event(event)
            .status(GdprStatus.NACK)
            .build();
    }

    public enum GdprStatus {
        ACK, NACK
    }
}
