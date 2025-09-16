// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2025 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
// end::copyright[]
package io.openliberty.guides.inventory.client;

import java.net.URI;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

public class SystemClient implements AutoCloseable {

    // tag::getLogger[]
    private static final Logger logger = Logger.getLogger(SystemClient.class.getName());
    // end::getLogger[]

    private static final String PROTOCOL = "http";
    private static final String SYSTEM_PROPERTIES = "/system/properties";
    private static final String SYSTEM_HEALTH = "/health";

    private String hostname;
    private int port;
    private Client client;

    public void init(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    private String buildUrl(String path) {
        try {
            URI uri = new URI(PROTOCOL, null, hostname, port, path, null, null);
            return uri.toString();
        } catch (Exception e) {
            // tag::log1[]
            logger.log(Level.WARNING,
                "URISyntaxException while building system service URL", e);
            // end::log1[]
            return null;
        }
    }

    private Builder buildClientBuilder(String urlString) {
        try {
            this.client = ClientBuilder.newClient();
            Builder builder = client.target(urlString).request();
            return builder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        } catch (Exception e) {
            // tag::log2[]
            logger.log(Level.WARNING,
                "Exception while creating REST client builder", e);
            // end::log2[]
            return null;
        }
    }

    public Properties getProperties() {
        String url = buildUrl(SYSTEM_PROPERTIES);
        Builder builder = buildClientBuilder(url);
        if (builder == null) return null;
        try {
            Response response = builder.get();
            // tag::log3[]
            logger.log(Level.INFO,
                "Received response with status: {0}", response.getStatus());
            // end::log3[]
            if (response.getStatus() == Status.OK.getStatusCode()) {
                return response.readEntity(Properties.class);
            } else {
                // tag::log4[]
                logger.log(Level.WARNING,
                    "Response Status is not OK: {0}", response.getStatus());
                // end::log4[]
            }
        } catch (RuntimeException e) {
            // tag::log5[]
            logger.log(Level.WARNING,
                "Runtime exception while invoking system service", e);
            // end::log5[]
        } catch (Exception e) {
            // tag::log6[]
            logger.log(Level.WARNING,
                "Unexpected exception while processing system service request", e);
            // end::log6[]
        }
        return null;
    }

    public String getHealth() {
        String url = buildUrl(SYSTEM_HEALTH);
        Builder builder = buildClientBuilder(url);
        if (builder == null) return "ERROR";
        try {
            Response response = builder.get();
            int statusCode = response.getStatus();
            if (statusCode == Status.OK.getStatusCode()) {
                return "UP";
            } else if (statusCode == Status.SERVICE_UNAVAILABLE.getStatusCode()) {
                return "DOWN";
            } else {
                return "ERROR";
            }
        } catch (Exception e) {
            // tag::log7[]
            logger.log(Level.WARNING,
                "Unexpected exception while invoking system health endpoint", e);
            // end::log7[]
        }
        return "ERROR";
    }

    @Override
    public void close() {
        if (client != null) {
            client.close();
        }
    }
}
