/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension;

import org.gradle.api.Action;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.attributes.Attribute;

public enum CMakeConfigurations {

  CMAKE_COMPILE("cmakeCompile", (configuration) -> {
    configuration.setDescription("CMake compile declarations.");
    configuration.setCanBeDeclared(true);
    configuration.setCanBeResolved(false);
    configuration.setCanBeConsumed(false);
    configuration.getAttributes().attribute(Attribute.of("io.github.tomaki19.gradle.usage", String.class), "compile");
  }),
  CMAKE_COMPILE_CLASSPATH("cmakeCompileClasspath", (configuration) -> {
    configuration.setDescription("CMake compile classpath.");
    configuration.setCanBeDeclared(false);
    configuration.setCanBeResolved(true);
    configuration.setCanBeConsumed(false);
    configuration.getAttributes().attribute(Attribute.of("io.github.tomaki19.gradle.usage", String.class), "compile");
  }),
  CMAKE_COMPILE_ELEMENTS("cmakeCompileElements", (configuration) -> {
    configuration.setDescription("CMake compile elements.");
    configuration.setCanBeDeclared(false);
    configuration.setCanBeResolved(false);
    configuration.setCanBeConsumed(true);
    configuration.getAttributes().attribute(Attribute.of("io.github.tomaki19.gradle.usage", String.class), "compile");
  }),
  CMAKE_RUNTIME("cmakeRuntime", (configuration) -> {
    configuration.setDescription("CMake runtime declarations.");
    configuration.setCanBeDeclared(true);
    configuration.setCanBeResolved(false);
    configuration.setCanBeConsumed(false);
    configuration.getAttributes().attribute(Attribute.of("io.github.tomaki19.gradle.usage", String.class), "runtime");
  }),
  CMAKE_RUNTIME_CLASSPATH("cmakeRuntimeClasspath", (configuration) -> {
    configuration.setDescription("CMake runtime classpath.");
    configuration.setCanBeDeclared(false);
    configuration.setCanBeResolved(true);
    configuration.setCanBeConsumed(false);
    configuration.getAttributes().attribute(Attribute.of("io.github.tomaki19.gradle.usage", String.class), "runtime");
  }),
  CMAKE_RUNTIME_ELEMENTS("cmakeRuntimeElements", (configuration) -> {
    configuration.setDescription("CMake runtime elements.");
    configuration.setCanBeDeclared(false);
    configuration.setCanBeResolved(false);
    configuration.setCanBeConsumed(true);
    configuration.getAttributes().attribute(Attribute.of("io.github.tomaki19.gradle.usage", String.class), "runtime");
  });

  private final String name;
  private final Action<Configuration> action;

  CMakeConfigurations(final String name, final Action<Configuration> action) {
    this.name = name;
    this.action = action;
  }

  public Action<Configuration> configure() {
    return action;
  }

  @Override
  public String toString() {
    return name;
  }

}
