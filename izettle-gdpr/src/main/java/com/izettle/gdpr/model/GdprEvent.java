package com.izettle.gdpr.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GdprEvent {

    //TODO not finished design
    private GdprEventType type;
    private Long UserId;
    private UUID userUuid;
    private Long organizationId;
    private UUID organizationUuid;
    private String userEmail;
    private String retentionPeriod;
    private String path;
    private String serviceName;

    public enum GdprEventType {
        FORGET, PORT_DATA
    }
}
