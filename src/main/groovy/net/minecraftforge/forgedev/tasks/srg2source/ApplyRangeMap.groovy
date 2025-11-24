/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.forgedev.tasks.srg2source

import groovy.transform.CompileStatic
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.problems.Problems
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile

import javax.inject.Inject

@CompileStatic
abstract class ApplyRangeMap extends S2SExec {
    abstract @InputFiles ConfigurableFileCollection getSources()
    abstract @OutputFile RegularFileProperty getOutput()
    abstract @InputFiles @Optional ConfigurableFileCollection getExcFiles()
    abstract @InputFiles ConfigurableFileCollection getSrgFiles()

    abstract @InputFile RegularFileProperty getRangeMap()
    abstract @Input @Optional Property<Boolean> getKeepImports()
    abstract @Input @Optional @Deprecated Property<Boolean> getAnnotate()

    abstract @Input @Optional Property<Boolean> getSortImports()
    abstract @Input @Optional Property<Boolean> getGuessLambdas()
    abstract @Input @Optional Property<Boolean> getGuessLocals()

    @Inject
    ApplyRangeMap() {
        this.output.convention(this.defaultOutputFile)
    }

    @Override
    protected void addArguments() {
        this.args('--apply')

        this.args('--in', this.sources)
        this.args('--out', this.output)
        this.args('--exc', this.excFiles)
        this.args('--srg', this.srgFiles)

        this.args('--range', this.rangeMap)
        this.args('--keepImports', this.keepImports)
        //this.args('--annotate', this.annotate)

        this.args('--sortImports', this.sortImports)
        this.args('--guessLambdas', this.guessLambdas)
        this.args('--guessLocals', this.guessLocals)

        super.addArguments()
    }
}
