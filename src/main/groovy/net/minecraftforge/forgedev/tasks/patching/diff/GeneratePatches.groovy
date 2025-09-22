/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.forgedev.tasks.patching.diff

import groovy.transform.CompileStatic
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory

@CompileStatic
abstract class GeneratePatches extends BaseDiffTask {
    private final RegularFileProperty input = this.objects.fileProperty()
    private final RegularFileProperty modified = this.objects.fileProperty()
    private final DirectoryProperty output = this.objects.directoryProperty()

    @InputFile RegularFileProperty getInput() { this.input }
    @InputFile RegularFileProperty getModified() { this.modified }
    @OutputDirectory DirectoryProperty getOutput() { this.output }
}
