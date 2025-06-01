/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.helper;

import org.gradle.api.NamedDomainObjectProvider;

import ch.tomaki.gradle.cmake.extension.CMakeExtension;
import ch.tomaki.gradle.cmake.extension.api.CMakeFindPackage;

public final class TestCMakeFindPackage {

  public static NamedDomainObjectProvider<CMakeFindPackage> register(final String name,
      final CMakeExtension extension) {
    return extension.getFindPackages().register(name);
  }

  private TestCMakeFindPackage() {
  }

}
