/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.forgedev.tasks.sas;

import net.minecraftforge.forgedev.ForgeDevTask;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.workers.WorkAction;
import org.gradle.workers.WorkParameters;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

// TODO [ForgeDev] This does not account for incremental changes.
public abstract class CreateFakeSASPatches extends DefaultTask implements ForgeDevTask {
    public abstract @InputFiles ConfigurableFileCollection getFiles();

    public abstract @OutputDirectory DirectoryProperty getOutput();

    protected abstract @Inject WorkerExecutor getWorkerExecutor();

    public CreateFakeSASPatches() {
        getOutput().convention(getDefaultOutputDirectory().map(d -> d.dir("patches")));
    }

    @TaskAction
    public void apply() throws IOException {
        {
            var output = getOutput().get().getAsFile();
            if (!output.exists() && !output.mkdirs())
                throw new IllegalStateException("Failed to make directory: " + output);
        }

        var queue = getWorkerExecutor().noIsolation();

        for (var file : getFiles()) {
            for (var line : ResourceGroovyMethods.readLines(file)) {
                if (line.isBlank()) continue;

                queue.submit(Action.class, parameters -> {
                    parameters.getOutput().set(this.getOutput());
                    parameters.getLine().set(line);
                });
            }
        }

        queue.await();
    }

    static abstract class Action implements WorkAction<Action.Parameters> {
        interface Parameters extends WorkParameters {
            DirectoryProperty getOutput();

            Property<String> getLine();
        }

        @Inject
        public Action() { }

        @Override
        public void execute() {
            var output = getParameters().getOutput().getAsFile().get();
            var line = getParameters().getLine().get();

            int idx = line.indexOf('#');
            if (idx == 0) return;

            if (idx != -1) line = line.substring(0, idx - 1);
            if (line.charAt(0) == '\t') line = line.substring(1);
            var cls = (line.trim() + "    ").split(" ", -1)[0];
            var patch = new File(output, cls + ".java.patch");
            if (!patch.getParentFile().exists() && !patch.getParentFile().mkdirs())
                throw new IllegalStateException("Failed to make directory: " + patch.getParentFile());

            try {
                patch.createNewFile();
            } catch (IOException e) {
                throw new IllegalStateException("Failed to create empty file: " + patch, e);
            }
        }
    }
}
