package com.izettle.gdpr.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@AllArgsConstructor
public class MessagingConfiguration {

    @NotBlank
    private final String sqsEndpoint; //Service SQS
    @NotBlank
    private final String sqsQueueUrl;
    @NotEmpty
    private final String deadLetterQueueUrl; //Service SQS DLQ
    @NonNull
    private final String sqsPushTopicName = "iZettleEvents";
    private final String GdprHttpServiceUrl;
    private final String gdprSnsEndpoint; //TODO add real RTBF sns
    private final String gdprTopicARN;//TODO add real RTBF sns´ARN
}
