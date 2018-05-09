package com.izettle.gdpr.messaging;

import com.amazonaws.services.sns.AmazonSNSAsync;
import com.izettle.gdpr.model.MessagingConfiguration;
import com.izettle.messaging.AmazonSNSClientFactory;
import io.dropwizard.lifecycle.Managed;

public final class ManagedAmazonSNSClient {
    private ManagedAmazonSNSClient() {
    }

    public static AmazonSNSAsync getClient(MessagingConfiguration configuration) {
        return AmazonSNSClientFactory.createInstance(
            configuration.getGdprSnsEndpoint()
        );
    }

    public static Managed manage(final AmazonSNSAsync client) {
        return new Managed() {
            @Override
            public void start() throws Exception {
            }

            @Override
            public void stop() throws Exception {
                client.shutdown();
            }
        };
    }
}
