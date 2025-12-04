/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.forgedev.legacy;

import net.minecraftforge.gradleutils.shared.EnhancedPlugin;
import net.minecraftforge.gradleutils.shared.EnhancedTask;
import org.gradle.api.Project;

public interface ForgeBuildTask extends EnhancedTask<ForgeBuildProblems> {
    @Override
    default Class<? extends EnhancedPlugin<? super Project>> pluginType() {
        return ForgeBuildPlugin.class;
    }

    @Override
    default Class<ForgeBuildProblems> problemsType() {
        return ForgeBuildProblems.class;
    }
}
