/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.forgedev.tasks.patching.diff

import groovy.transform.CompileStatic
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.problems.Problems
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.process.ExecResult

import javax.inject.Inject
import java.util.zip.ZipOutputStream

@CompileStatic
abstract class BakePatches extends BasePatchBakingTask {
    private final DirectoryProperty input = this.objects.directoryProperty()
    private final RegularFileProperty output = this.objects.fileProperty()

    @InputDirectory @Optional DirectoryProperty getInput() { this.input }
    @OutputFile RegularFileProperty getOutput() { this.output }

    @Inject
    BakePatches() { }

    @Override
    protected ExecResult exec() {
        if (!this.input.present) {
            // create empty zip for output
            new ZipOutputStream(new FileOutputStream(this.output.get().asFile)).close()
            return null
        }

        super.exec()
    }
}
