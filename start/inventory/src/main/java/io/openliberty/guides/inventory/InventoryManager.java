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
package io.openliberty.guides.inventory;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.openliberty.guides.inventory.client.SystemClient;
import io.openliberty.guides.inventory.model.InventoryList;
import io.openliberty.guides.inventory.model.SystemData;

@ApplicationScoped
public class InventoryManager {

    @Inject
    @ConfigProperty(name = "system.http.port")
    private int SYSTEM_PORT;

    private Map<String, SystemData> systems = new ConcurrentHashMap<>();

    public Map<String, Object> getSystemLoad(String hostname) {
        try (SystemClient client = new SystemClient()) {
            client.init(hostname, SYSTEM_PORT);
            return client.getSystemLoad();
        }
    }

    public InventoryList list() {
        return new InventoryList(new ArrayList<>(systems.values()));
    }

    public void set(String host, Map<String, Object> systemLoad) {
        SystemData system = systems.get(host);
        if (system != null) {
            system.setSystemLoad(systemLoad);
        } else {
            systems.put(host, new SystemData(host, systemLoad));
        }
    }

    public void refreshSystemsLoads() {
        for (SystemData system : systems.values()) {
            String hostname = system.getHostname();
            Map<String, Object> systemLoad = getSystemLoad(hostname);
            system.setSystemLoad(systemLoad);
        }
    }

    int clear() {
        int propertiesClearedCount = systems.size();
        systems.clear();
        return propertiesClearedCount;
    }
}
