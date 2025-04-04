/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.extension;

import org.gradle.api.NamedDomainObjectContainer;

public interface CMakeExtension {

  public static String getName() {
    return "cmake";
  }

  public NamedDomainObjectContainer<CMakeFindPackage> getFindPackages();

  public NamedDomainObjectContainer<CMakeToolchain> getToolchains();

  public NamedDomainObjectContainer<CMakeLibrary> getLibraries();

  public NamedDomainObjectContainer<CMakeBinary> getApplications();

  public NamedDomainObjectContainer<CMakeTest> getTests();

}
