/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import java.util.Optional;

import org.gradle.api.file.RegularFile;
import org.gradle.api.tasks.CacheableTask;

import io.github.tomaki19.gradle.cmake.files.CMakeFileConventions;

@CacheableTask
public abstract class CMakeCustomExec extends CMakeExec {

    private static final String COMPILE_COMMANDS_FILE_NAME = "compile_commands.json";

    protected final String compileCommands;

    @javax.inject.Inject
    public CMakeCustomExec(final String toolchainName, final String buildConfig,
            final Optional<RegularFile> environmentFile) {
        super(toolchainName, buildConfig, environmentFile);
        this.compileCommands = CMakeFileConventions
                .targetConfigDirectory(getProject().getLayout().getBuildDirectory(), toolchainName, buildConfig)
                .file(COMPILE_COMMANDS_FILE_NAME).getAsFile().getAbsolutePath();
    }

}
