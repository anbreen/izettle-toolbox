package com.izettle.gdpr.model;

import com.amazonaws.regions.Regions;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class S3Configuration {

    //TODO add stuff to YML
    @NotNull
    @JsonProperty
    private final String baseUrl;

    @NotNull
    @JsonProperty
    private final String bucket;

    @NotNull
    private final String region = Regions.EU_WEST_1.getName();

}
