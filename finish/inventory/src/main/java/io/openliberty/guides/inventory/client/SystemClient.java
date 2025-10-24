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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

public class SystemClient implements AutoCloseable {

    // tag::getLogger[]
    private static final Logger LOGGER = Logger.getLogger(SystemClient.class.getName());
    // end::getLogger[]

    private static final String PROTOCOL = "http";
    private static final String SYSTEM_LOAD = "/system/systemLoad";

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
            LOGGER.log(Level.WARNING,
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
            LOGGER.log(Level.WARNING,
                "Exception while creating REST client builder", e);
            // end::log2[]
            return null;
        }
    }

    public Map<String, Object> getSystemLoad() {
        String url = buildUrl(SYSTEM_LOAD);
        Builder builder = buildClientBuilder(url);
        if (builder == null) {
            return null;
        }

        try {
            Response response = builder.get();
            // tag::log3[]
            LOGGER.log(Level.INFO,
                "Received response with status: {0}", response.getStatus());
            // end::log3[]
            if (response.getStatus() == Status.OK.getStatusCode()) {
                JsonObject jsonResponse = response.readEntity(JsonObject.class);
                Map<String, Object> systemLoad = new HashMap<>();
                for (String key : jsonResponse.keySet()) {
                    systemLoad.put(key, jsonResponse.get(key));
                }
                return systemLoad;
            } else {
                // tag::log4[]
                LOGGER.log(Level.WARNING,
                    "Response Status is not OK: {0}", response.getStatus());
                // end::log4[]
            }
        } catch (RuntimeException e) {
            // tag::log5[]
            LOGGER.log(Level.WARNING,
                "Runtime exception while invoking system service", e);
            // end::log5[]
        } catch (Exception e) {
            // tag::log6[]
            LOGGER.log(Level.WARNING,
                "Unexpected exception while processing system service request", e);
            // end::log6[]
        }
        return null;
    }

    @Override
    public void close() {
        if (client != null) {
            client.close();
        }
    }
}
