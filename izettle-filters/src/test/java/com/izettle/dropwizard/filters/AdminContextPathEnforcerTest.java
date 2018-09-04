package com.izettle.dropwizard.filters;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.Test;

public class AdminContextPathEnforcerTest {

    private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    @Test
    public void shouldNotChangeCorrectConfiguration() throws Exception {

        JsonNode config = objectMapper.readTree("server:\n  adminContextPath: /system");
        JsonNode originalConfig = config.deepCopy();

        AdminContextPathEnforcer.enforceAdminContextPath(config);

        assertThat(config)
            .isEqualTo(originalConfig);
    }

    @Test
    public void shouldSetAdminContextPathWhenUndefined() throws Exception {

        JsonNode config = objectMapper.readTree("server:\n  foo: bar");

        AdminContextPathEnforcer.enforceAdminContextPath(config);

        assertThat(config)
            .isEqualTo(objectMapper.readTree("server:\n  foo: bar\n  adminContextPath: /system"));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowOnIncorrectConfiguration() throws Exception {

        JsonNode config = objectMapper.readTree("server:\n  adminContextPath: /not-system");

        AdminContextPathEnforcer.enforceAdminContextPath(config);
    }
}
