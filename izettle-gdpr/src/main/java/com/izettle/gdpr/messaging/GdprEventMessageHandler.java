package com.izettle.gdpr.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izettle.gdpr.GdprHttpClient;
import com.izettle.gdpr.model.GdprEvent;
import com.izettle.gdpr.model.GdprMessageProcessorConfiguration;
import com.izettle.messaging.handler.MessageHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class GdprEventMessageHandler implements MessageHandler<GdprEvent> {

    private static final String EVENT_SUBJECT_NAME = "GdprEventMessage";
    private final GdprMessageProcessorConfiguration configuration;
    private final GdprEventManager handler;
    private final ObjectMapper objectMapper;
    private final GdprHttpClient gdprClient;

    @Override
    public void handle(GdprEvent gdprEvent) throws Exception {

        gdprEvent.setServiceName(configuration.getServiceName());
        gdprClient.ack(gdprEvent);

        switch (gdprEvent.getType()) {
            case FORGET:
                handler.handleForget(gdprEvent);
                break;
            case PORT_DATA:
                handler.handleExport(gdprEvent);
                break;
            default:
                log.warn("Unknown type {} on incoming GDPR event", gdprEvent.getType());
        }
    }
}
