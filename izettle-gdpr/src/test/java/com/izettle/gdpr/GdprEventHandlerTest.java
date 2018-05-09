package com.izettle.gdpr;

import static com.izettle.java.UUIDFactory.createUUID1;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.izettle.gdpr.messaging.GdprEventMessageHandler;
import com.izettle.gdpr.model.GdprEvent;
import com.izettle.gdpr.model.GdprMessageProcessorConfiguration;
import com.izettle.gdpr.model.MessagingConfiguration;
import com.izettle.gdpr.model.S3Configuration;
import com.izettle.messaging.MessageQueueProcessor;
import com.izettle.messaging.QueueProcessor;
import com.izettle.messaging.handler.MessageDispatcher;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class GdprEventHandlerTest {
    private final AmazonSQS mockAmazonSQS = mock(AmazonSQS.class);
    @SuppressWarnings("unchecked")
    private final List<Message> receivedMessages = new ArrayList<>();
    private MessageQueueProcessor queueProcessor;
    private GdprEventMessageHandler gdprEventMessageHandler;
    private ObjectMapper objectMapper;

    @Before
    public final void before() throws Exception {
        objectMapper = new ObjectMapper();

        gdprEventMessageHandler = GdprHandlerFactory.create(
            createConfig(),
            objectMapper
        );

        MessageDispatcher messageDispatcher = MessageDispatcher.nonEncryptedMessageDispatcher();
        messageDispatcher.addHandler(GdprEvent.class, "GdprEvent", gdprEventMessageHandler);

        queueProcessor = QueueProcessor.createQueueProcessor(
            mockAmazonSQS,
            "UnitTestProcessor",
            "testurl",
            "deadLetterQueueUrl",
            messageDispatcher
        );

        ReceiveMessageResult messageResult = mock(ReceiveMessageResult.class);
        when(mockAmazonSQS.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(messageResult);
        when(messageResult.getMessages()).thenReturn(receivedMessages);
    }

    private Message createMessage(String messageId) {
        Message msg = new Message();
        msg.setMessageId(messageId);
        msg.setReceiptHandle(messageId);
        return msg;
    }

    private GdprEvent randomGdprEvent() {
        return new GdprEvent(
            GdprEvent.GdprEventType.FORGET,
            1L,
            createUUID1(),
            2L,
            createUUID1(),
            "email@invalid.invalid",
            "0",
            "s3",
            "toolbox"
        );
    }

    private GdprMessageProcessorConfiguration createConfig() {
        final S3Configuration s3Configuration = new S3Configuration("https://baseurl.invalid", "bucket");
        final MessagingConfiguration messagingConfiguration =
            new MessagingConfiguration("https://sqs.invalid", "queueUrl", "dlq", "https://rtbf.invalid"
                , "https://sns.invalid", "GdprEvent");
        return new GdprMessageProcessorConfiguration(s3Configuration, messagingConfiguration, "toolbox");
    }

    @Test
    public void shouldHandleGdprEvent() throws Exception {
        final Message msg1 = createMessage("msg1");
        msg1.setBody(objectMapper.writeValueAsString(randomGdprEvent()));
        receivedMessages.add(msg1);

        queueProcessor.poll();

        verify(gdprEventMessageHandler).handle(any());
    }

}
