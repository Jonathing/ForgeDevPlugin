/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.forgedev.tasks.generation;

import net.minecraftforge.forgedev.ForgeDevTask;
import net.minecraftforge.forgedev.Util;
import net.minecraftforge.gradleutils.shared.Closures;
import net.minecraftforge.util.data.json.JsonData;
import net.minecraftforge.util.data.json.PatcherConfig;
import net.minecraftforge.util.data.json.RunConfig;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Named;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public abstract class GeneratePatcherConfigV2 extends DefaultTask implements ForgeDevTask {
    public abstract @OutputFile RegularFileProperty getOutput();

    public abstract @Input @Optional Property<PatcherConfig.V2.DataFunction> getProcessor();
    public abstract @Input @Optional MapProperty<String, File> getProcessorData();
    public abstract @Input @Optional Property<String> getPatchesOriginalPrefix();
    public abstract @Input @Optional Property<String> getPatchesModifiedPrefix();
    public abstract @Input @Optional Property<Boolean> getNotchObf();
    public abstract @Input @Optional Property<String> getSourceFileEncoding();
    public abstract @Input @Optional ListProperty<String> getUniversalFilters();

    public abstract @Input Property<String> getMCPConfig();
    public abstract @Input ListProperty<String> getLibraries();
    public abstract @Input ListProperty<String> getModules();
    public abstract @Input Property<String> getUniversal();
    public abstract @Input Property<String> getSource();
    public abstract @Input @Optional Property<String> getInject();
    public abstract @Input @Optional Property<String> getPatches();
    public abstract @InputFiles ConfigurableFileCollection getATs();
    public abstract @InputFiles ConfigurableFileCollection getSASs();
    public abstract @InputFiles ConfigurableFileCollection getSRGs();
    public abstract @Input @Optional ListProperty<String> getSRGLines();

    protected abstract @Input MapProperty<String, RunConfig> getRuns();

    public abstract @Input Property<String> getBinpatcherVersion();
    public abstract @Input ListProperty<String> getBinpatcherArguments();

    protected abstract @Inject ObjectFactory getObjects();

    @Inject
    public GeneratePatcherConfigV2() {
        this.getSourceFileEncoding().convention(StandardCharsets.UTF_8.name());
        this.getOutput().convention(this.getDefaultOutputFile("json"));
    }

    @TaskAction
    protected void exec() throws IOException {
        var config = new PatcherConfig();

        config.spec = 1;
        config.binpatches = "joined.lzma";
        config.sources = this.getSource().filter(Util.IS_NOT_BLANK).getOrNull();
        config.universal = this.getUniversal().filter(Util.IS_NOT_BLANK).getOrNull();
        config.patches = this.getPatches().filter(Util.IS_NOT_BLANK).getOrNull();
        config.inject = this.getInject().filter(Util.IS_NOT_BLANK).getOrNull();
        config.libraries = this.getLibraries().get();
        config.ats = DefaultGroovyMethods.collect(this.getATs(), Closures.<File, String>function(f -> "ats/" + f.getName()));
        config.sass = DefaultGroovyMethods.collect(this.getSASs(), Closures.<File, String>function(f -> "sas/" + f.getName()));
        config.srgs = DefaultGroovyMethods.collect(this.getSRGs(), Closures.<File, String>function(f -> "srgs/" + f.getName()));
        config.srgs.addAll(this.getSRGLines().get());
        config.mcp = this.getMCPConfig().filter(Util.IS_NOT_BLANK).get();

        config.runs = this.getRuns().get();

        config.binpatcher = new PatcherConfig.Function();
        config.binpatcher.version = this.getBinpatcherVersion().filter(Util.IS_NOT_BLANK).getOrNull();
        config.binpatcher.args = this.getBinpatcherArguments().getOrNull();

        if (this.isV2()) {
            var v2 = (PatcherConfig.V2) (config = new PatcherConfig.V2(config));
            v2.spec = 2;
            v2.modules = this.getModules().get();
            v2.processor = this.getProcessor().getOrNull();
            v2.patchesOriginalPrefix = this.getPatchesOriginalPrefix().filter(Util.IS_NOT_BLANK).getOrNull();
            v2.patchesModifiedPrefix = this.getPatchesModifiedPrefix().filter(Util.IS_NOT_BLANK).getOrNull();
            v2.notchObf = this.getNotchObf().filter(b -> b).getOrNull();
            v2.sourceFileCharset = this.getSourceFileEncoding().filter(Util.IS_NOT_BLANK).getOrNull();
            v2.universalFilters = this.getUniversalFilters().get();
        }

        JsonData.toJson(config, this.getOutput().getAsFile().get());
    }

    private boolean isV2() {
        return this.getNotchObf().getOrElse(false)
            || this.getProcessor().isPresent()
            || this.getUniversalFilters().isPresent()
            || !"a/".equals(getPatchesOriginalPrefix().get())
            || !"b/".equals(getPatchesModifiedPrefix().get());
    }

    public void runs(Action<? super NamedDomainObjectContainer<? extends RunConfig>> action) {
        var container = this.getObjects().domainObjectContainer(BeanRunConfig.class);
        action.execute(container);
        this.getRuns().set(container.getAsMap());
    }

    static abstract class BeanRunConfig extends RunConfig implements Named, Serializable {
        private static final @Serial long serialVersionUID = -7975487107404098482L;

        @Inject
        public BeanRunConfig(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }

        public void environment(String key, String value) {
            var map = this.env == null ? this.env = new HashMap<>() : this.env;
            map.put(key, value);
        }

        public void property(String key, String value) {
            var map = this.props == null ? this.props = new HashMap<>() : this.props;
            map.put(key, value);
        }

        public void args(String... args) {
            var list = this.args == null ? this.args = new ArrayList<>() : this.args;
            list.addAll(Arrays.asList(args));
        }

        public void jvmArgs(String... jvmArgs) {
            var list = this.jvmArgs == null ? this.jvmArgs = new ArrayList<>() : this.jvmArgs;
            list.addAll(Arrays.asList(jvmArgs));
        }
    }
}
