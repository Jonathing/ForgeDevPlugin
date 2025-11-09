/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.forgedev;

import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;

import javax.inject.Inject;

public abstract class LegacyMCPExtension {
    public static final String EXTENSION_NAME = "mcp";

    private final Property<String> config = this.getObjects().property(String.class);
    private final Property<String> version = this.getObjects().property(String.class).value(
        config.map(s -> (s.endsWith("@zip") ? s.substring(0, s.length() - "@zip".length()) : s).split(":")[2])
    );

    protected abstract @Inject ObjectFactory getObjects();

    protected abstract @Inject ProviderFactory getProviders();

    @Inject
    public LegacyMCPExtension() { }

    public Property<String> getConfig() {
        return this.config;
    }

    public Property<String> getVersion() {
        return this.version;
    }

    public void setConfig(Provider<String> value) {
        getConfig().set(value.map(s -> {
            if (s.indexOf(':') != -1) { // Full artifact
                return s;
            } else {
                return "de.oceanlabs.mcp:mcp_config:" + s + "@zip";
            }
        }));
    }

    public void setConfig(String value) {
        setConfig(this.getProviders().provider(() -> value));
    }

    public abstract Property<String> getPipeline();
}
