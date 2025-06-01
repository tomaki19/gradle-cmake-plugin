/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class CMakeResolvedBuild {

  private final Map<String, CMakeResolvedToolchain> resolvedToolchains = new HashMap<>();
  private final Set<CMakeResolvedFindPackage> resolvedFindPackages = new HashSet<>();
  private final Set<CMakeResolvedProjectModule> resolvedProjectModules = new HashSet<>();
  private final Set<CMakeResolvedInterfaceLibrary> resolvedInterfaces = new HashSet<>();
  private final Set<CMakeResolvedBinaryLibrary> resolvedLibraries = new HashSet<>();
  private final Set<CMakeResolvedApplication> resolvedApplications = new HashSet<>();
  private final Set<CMakeResolvedTest> resolvedTests = new HashSet<>();

  void addToolchain(final CMakeResolvedToolchain toolchain) {
    this.resolvedToolchains.put(toolchain.getName(), toolchain);
  }

  public void forToolchain(final String name, final Consumer<CMakeResolvedToolchain> action) {
    Optional.ofNullable(resolvedToolchains.get(name)).ifPresent(action);
  }

  public void forToolchains(final Consumer<CMakeResolvedToolchain> action) {
    resolvedToolchains.forEach((name, toolchain) -> action.accept(toolchain));
  }

  public void addFindPackage(final CMakeResolvedFindPackage findPackage) {
    resolvedFindPackages.add(findPackage);
  }

  public void addFindPackages(final Collection<CMakeResolvedFindPackage> findPackages) {
    resolvedFindPackages.addAll(findPackages);
  }

  public Collection<CMakeResolvedFindPackage> getResolvedFindPackages() {
    return resolvedFindPackages;
  }

  public void addProjectModule(final CMakeResolvedProjectModule projectModule) {
    resolvedProjectModules.add(projectModule);
  }

  public void addProjectModules(final Collection<CMakeResolvedProjectModule> projectModules) {
    resolvedProjectModules.addAll(projectModules);
  }

  public Collection<CMakeResolvedProjectModule> getResolvedProjectModules() {
    return resolvedProjectModules;
  }

  public void add(final CMakeResolvedInterfaceLibrary object) {
    resolvedInterfaces.add(object);
  }

  public Collection<CMakeResolvedInterfaceLibrary> getResolvedInterfaces() {
    return resolvedInterfaces;
  }

  public void add(final CMakeResolvedBinaryLibrary object) {
    resolvedLibraries.add(object);
  }

  public Collection<CMakeResolvedBinaryLibrary> getResolvedLibraries() {
    return resolvedLibraries;
  }

  public void add(final CMakeResolvedApplication object) {
    resolvedApplications.add(object);
  }

  public Collection<CMakeResolvedApplication> getResolvedApplications() {
    return resolvedApplications;
  }

  public void add(final CMakeResolvedTest object) {
    resolvedTests.add(object);
  }

  public Collection<CMakeResolvedTest> getResolvedTests() {
    return resolvedTests;
  }

}
