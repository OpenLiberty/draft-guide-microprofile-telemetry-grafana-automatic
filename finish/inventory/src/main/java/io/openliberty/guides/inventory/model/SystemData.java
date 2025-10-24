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
package io.openliberty.guides.inventory.model;

import java.util.Map;

public class SystemData {

    private final String hostname;
    private Map<String, Object> systemLoad;

    public SystemData(String hostname, Map<String, Object> systemLoad) {
        this.hostname = hostname;
        this.systemLoad = systemLoad;
    }

    public String getHostname() {
        return hostname;
    }

    public Map<String, Object> getSystemLoad() {
        return systemLoad;
    }

    public void setSystemLoad(Map<String, Object> systemLoad) {
        this.systemLoad = systemLoad;
    }
}
