package com.izettle.gdpr;

import com.izettle.gdpr.model.GdprEvent;
import com.izettle.gdpr.model.GdprStatusReportMessage;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;
import org.glassfish.jersey.client.JerseyClientBuilder;

public class GdprHttpClient {

    private final String serviceUrl;
    private WebTarget target;

    public GdprHttpClient(String serviceUrl) {
        this.serviceUrl = serviceUrl;
        Client client = JerseyClientBuilder.createClient();
        this.target = client.target(serviceUrl);
    }

    public void ack(GdprEvent event) {
        target
            .path("gdpr")
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.json(GdprStatusReportMessage.ack(event)));
    }
}
