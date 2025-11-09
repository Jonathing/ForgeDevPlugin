/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.forgedev;

import net.minecraftforge.util.os.OS;
import org.gradle.api.provider.ValueSource;
import org.gradle.api.provider.ValueSourceParameters;

import javax.inject.Inject;

abstract class OSValueSource implements ValueSource<String, ValueSourceParameters.None> {
    @Inject
    public OSValueSource() { }

    @Override
    public String obtain() {
        return OS.current(OS.WINDOWS, OS.MACOS, OS.LINUX).key();
    }
}
