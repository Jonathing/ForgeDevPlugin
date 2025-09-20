/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.forgedev;

import net.minecraftforge.gradleutils.shared.EnhancedPlugin;
import net.minecraftforge.gradleutils.shared.EnhancedTask;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.Directory;
import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Internal;

public interface ForgeDevTask extends EnhancedTask<ForgeDevProblems> {
    @Override
    default Class<? extends EnhancedPlugin<? super Project>> pluginType() {
        return ForgeDevPlugin.class;
    }

    @Override
    default Class<ForgeDevProblems> problemsType() {
        return ForgeDevProblems.class;
    }
}
