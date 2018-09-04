package com.izettle.dropwizard.filters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AdminContextPathEnforcer implements ConfigurationSourceProvider {

    private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
    private final ConfigurationSourceProvider wrappedProvider;

    public AdminContextPathEnforcer(ConfigurationSourceProvider wrappedProvider) {
        this.wrappedProvider = wrappedProvider;
    }

    @Override
    public InputStream open(String path) throws IOException {
        JsonNode config = objectMapper.readTree(wrappedProvider.open(path));

        enforceAdminContextPath(config);

        return new ByteArrayInputStream(objectMapper.writeValueAsBytes(config));
    }

    static void enforceAdminContextPath(final JsonNode config) {
        String adminContextPath = config.at("/server/adminContextPath").textValue();
        if (adminContextPath != null && !adminContextPath.equals("/system")) {
            throw new RuntimeException("Conflict when enforcing that adminContextPath is set to '/system': "
                + "adminContextPath must be either undefined or set explicitly to /system");
        }
        ((ObjectNode) config.at("/server")).put("adminContextPath", "/system");
    }
}
