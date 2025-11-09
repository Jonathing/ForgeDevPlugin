/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.forgedev.tasks.jarcompat

import groovy.transform.CompileStatic;
import net.minecraftforge.forgedev.Tools
import net.minecraftforge.forgedev.tasks.ToolExec
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.process.ExecResult

import javax.inject.Inject

@CompileStatic
abstract class CheckJarCompatibility extends ToolExec {
    abstract @InputFile RegularFileProperty getBaseJar()
    abstract @InputFile RegularFileProperty getInputJar()

    abstract @InputFiles ConfigurableFileCollection getCommonLibraries()
    abstract @InputFiles ConfigurableFileCollection getBaseLibraries()
    abstract @InputFiles ConfigurableFileCollection getConcreteLibraries()

    abstract @Input @Optional Property<Boolean> getBinary()
    abstract @Input @Optional Property<String> getAnnotationCheckMode()

    @Inject
    CheckJarCompatibility() {
        super(Tools.JARCOMPATIBILITYCHECKER)
    }

    @Override
    protected ExecResult exec() {
        return super.exec().rethrowFailure().assertNormalExitValue()
    }

    @Override
    protected void addArguments() {
        super.addArguments()

        this.args(
            '--base-jar', baseJar,
            '--input-jar', inputJar
        )

        this.args('--lib', commonLibraries)
        this.args('--base-lib', baseLibraries)
        this.args('--concrete-lib', concreteLibraries)

        this.args(binary.getOrElse(false) ? '--binary' : '--api')
        this.args('--annotation-check-mode', annotationCheckMode)
    }
}
