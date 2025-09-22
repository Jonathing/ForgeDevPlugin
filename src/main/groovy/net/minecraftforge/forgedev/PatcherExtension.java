/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.forgedev;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;

public interface PatcherExtension {
    Property<Boolean> getSrgPatches();

    Property<Boolean> getNotchObf();

    interface PatcherSources {
        RegularFileProperty getClean();

        DirectoryProperty getPatches();

        DirectoryProperty getOutput();
    }
}
