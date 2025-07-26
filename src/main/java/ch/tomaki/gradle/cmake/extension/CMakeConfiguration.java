/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension;

import org.gradle.api.artifacts.Configuration;

public interface CMakeConfiguration extends Configuration {

  public static final String NAME = "cmakeModule";

}
