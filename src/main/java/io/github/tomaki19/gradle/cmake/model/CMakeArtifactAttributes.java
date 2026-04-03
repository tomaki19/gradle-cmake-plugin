/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import org.gradle.api.attributes.Attribute;

public final class CMakeArtifactAttributes {

  public static final String CATEGORY = "gradle-cmake-dependencies";

  public static final Attribute<String> LINK_VARIANT_ATTRIBUTE = Attribute.of(
      "io.github.tomaki19.cmake.dependency.linkVariant", String.class);

  public static final Attribute<String> TOOLCHAIN_ATTRIBUTE = Attribute.of(
      "io.github.tomaki19.cmake.dependency.toolchain", String.class);

  public static final Attribute<String> BUILD_CONFIG_ATTRIBUTE = Attribute.of(
      "io.github.tomaki19.cmake.dependency.buildConfig", String.class);

  private CMakeArtifactAttributes() {
  }

}
