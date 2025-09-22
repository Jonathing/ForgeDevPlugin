/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.forgedev.tasks.patching.diff

import groovy.transform.CompileStatic
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.problems.Problems
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.process.ExecResult

import javax.inject.Inject

@CompileStatic
abstract class ApplyPatches extends BasePatchTask {
    abstract @Input Property<Boolean> getFailOnError()

    private final RegularFileProperty input = this.objects.fileProperty()
    private final DirectoryProperty patches = this.objects.directoryProperty()
    private final RegularFileProperty output = this.objects.fileProperty()

    @InputFile RegularFileProperty getInput() { this.input }
    @InputDirectory @Optional DirectoryProperty getPatches() { this.patches }
    @OutputFile RegularFileProperty getOutput() { this.output }

    @Inject
    ApplyPatches() {
        this.failOnError.convention(true)

        this.logLevel.convention('warn')
    }

    @Override
    protected ExecResult exec() {
        if (!this.patches.isPresent()) {
            this.output.get().asFile.bytes = this.input.get().asFile.bytes
            return
        }

        var result = super.exec()
        var exitValue = result.exitValue
        if (exitValue !== 0) {
            // patches failed
            if (exitValue !== 1)
                result.rethrowFailure()

            // some other error
            if (this.failOnError.get())
                result.assertNormalExitValue()
        }
    }
}
