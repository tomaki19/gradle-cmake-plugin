/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.helper;

import org.gradle.api.NamedDomainObjectProvider;

import ch.tomaki.gradle.cmake.extension.CMakeExtension;
import ch.tomaki.gradle.cmake.extension.api.CMakeSystemPackage;

public final class TestCMakePackage {

  public static NamedDomainObjectProvider<CMakeSystemPackage> register(final String name,
      final CMakeExtension extension) {
    return extension.getPackages().register(name);
  }

  private TestCMakePackage() {
  }

}
