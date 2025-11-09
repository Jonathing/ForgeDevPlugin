/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.forgedev.tasks.mcp

import groovy.transform.CompileStatic
import net.minecraftforge.forgedev.ForgeDevExtension
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

import javax.inject.Inject

@CompileStatic
abstract class MavenizerMCPMaven extends MavenizerExec {
    abstract @Input Property<String> getArtifact()
    abstract @Internal DirectoryProperty getOutput()

    @Inject
    MavenizerMCPMaven() {
        this.output.convention(this.project.extensions.getByType(ForgeDevExtension).mavenizerRepo)
    }

    @Override
    protected void addArguments() {
        super.addArguments()

        this.args('--maven', '--global-auxiliary-variants', '--dependencies-only')
        this.args('--output', output)

        if (!artifact.get().contains(':')) {
            this.args('--artifact', 'net.minecraft:joined')
            this.args('--version', artifact)
        } else {
            this.args('--artifact', artifact)
        }
    }
}
