/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.bundling.Tar;

@CacheableTask
public abstract class CMakePackageTar extends Tar {

}
