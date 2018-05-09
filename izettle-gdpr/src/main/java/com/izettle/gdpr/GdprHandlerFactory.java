package com.izettle.gdpr;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.izettle.gdpr.aws.S3TransferManager;
import com.izettle.gdpr.dao.GdprDao;
import com.izettle.gdpr.manager.DefaultGdprEventManager;
import com.izettle.gdpr.messaging.GdprEventManager;
import com.izettle.gdpr.messaging.GdprEventMessageHandler;
import com.izettle.gdpr.messaging.GdprPublisher;
import com.izettle.gdpr.model.GdprMessageProcessorConfiguration;
import com.izettle.gdpr.model.MessagingConfiguration;
import com.izettle.gdpr.model.S3Configuration;
import com.izettle.messaging.AmazonSNSClientFactory;
import com.izettle.messaging.MessagePublisher;
import com.izettle.messaging.PublisherService;
import lombok.Getter;

@Getter
public class GdprHandlerFactory {

    public static GdprEventMessageHandler create(
        GdprMessageProcessorConfiguration configuration,
        ObjectMapper objectMapper,
        GdprEventManager gdprEventHandler
    ) {
        final GdprHttpClient gdprClient =
            new GdprHttpClient(configuration.getMessagingConfiguration().getGdprHttpServiceUrl());
        return new GdprEventMessageHandler(configuration, gdprEventHandler, objectMapper, gdprClient);
    }

    public static GdprEventMessageHandler create(
        GdprMessageProcessorConfiguration configuration,
        ObjectMapper objectMapper,
        GdprDao... daos
    ) {
        final S3TransferManager s3TransferManager =
            setupTransferManager(configuration.getS3Configuration(), objectMapper);
        final AmazonSNSAsync amazonSnsAsync = createAmazonSnsAsync(configuration.getMessagingConfiguration());
        final MessagePublisher defaultMessagePublisher = getDefaultMessagePublisher(configuration, amazonSnsAsync);
        final GdprPublisher gdprPublisher = new GdprPublisher(defaultMessagePublisher, objectMapper);
        final GdprHttpClient gdprClient =
            new GdprHttpClient(configuration.getMessagingConfiguration().getGdprHttpServiceUrl());
        return new GdprEventMessageHandler(
            configuration,
            new DefaultGdprEventManager(s3TransferManager, gdprPublisher, daos),
            objectMapper,
            gdprClient
        );
    }

    private static MessagePublisher getDefaultMessagePublisher(
        GdprMessageProcessorConfiguration configuration,
        AmazonSNSAsync amazonSNSAsync
    ) {
        return PublisherService.nonEncryptedPublisherService(
            amazonSNSAsync,
            configuration.getMessagingConfiguration().getGdprTopicARN()
        );
    }

    private static AmazonSNSAsync createAmazonSnsAsync(MessagingConfiguration configuration) {
        return AmazonSNSClientFactory.createInstance(
            configuration.getGdprSnsEndpoint()
        );
    }

    private static S3TransferManager setupTransferManager(S3Configuration configuration, ObjectMapper objectMapper) {
        AmazonS3 s3 = createS3Client(configuration);
        final TransferManager transferManager = TransferManagerBuilder.standard().withS3Client(s3).build();
        return new S3TransferManager(transferManager, configuration.getBucket(), objectMapper);
    }

    public static AmazonS3 createS3Client(S3Configuration configuration) {
        return AmazonS3ClientBuilder.standard().withEndpointConfiguration(
            new AwsClientBuilder.EndpointConfiguration(configuration.getBaseUrl(), configuration.getRegion())
        ).build();
    }

}
