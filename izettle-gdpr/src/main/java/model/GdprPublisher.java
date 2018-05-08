package model;

import com.amazonaws.AmazonServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.izettle.messaging.MessagePublisher;
import com.izettle.messaging.MessagingException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GdprPublisher {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ExecutorService executorService;
    private final MessagePublisher publisher;
    private final ObjectMapper objectMapper;

    public GdprPublisher(
        ExecutorService executorService,
        MessagePublisher publisher,
        ObjectMapper objectMapper
    ) {
        this.publisher = publisher;
        this.objectMapper = objectMapper;
        this.executorService = executorService;
    }

    public void publish(GdprStatusReportMessage reportMessage) {

        final String eventName = reportMessage.getEventName();

        CompletableFuture.runAsync(() -> {
            try {
                final String message = objectMapper.writeValueAsString(reportMessage);
                log.info("Publishing event={}", eventName);
                log.debug("Successfully published entity: {}", message);
                publisher.post(message, eventName);
            } catch (JsonProcessingException | AmazonServiceException | MessagingException e) {
                log.error("Failed to publish {}, message={}", reportMessage, e);
            }
        }, executorService);
    }
}
