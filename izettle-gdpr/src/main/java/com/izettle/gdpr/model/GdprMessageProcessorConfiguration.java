package com.izettle.gdpr.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data

public class GdprMessageProcessorConfiguration {

    @Valid
    @NotNull
    private final S3Configuration s3Configuration;

    @Valid
    @NotNull
    private final MessagingConfiguration messagingConfiguration;

    @NotBlank
    private final String serviceName;
}
