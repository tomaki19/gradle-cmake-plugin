/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
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
  private final Set<CMakeResolvedPackage> resolvedFindPackages = new HashSet<>();
  private final Set<CMakeResolvedProject> resolvedProjectModules = new HashSet<>();
  private final Set<CMakeResolvedLibrary> resolvedLibraries = new HashSet<>();
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

  public void addFindPackage(final CMakeResolvedPackage findPackage) {
    resolvedFindPackages.add(findPackage);
  }

  public void addPackages(final Collection<CMakeResolvedPackage> findPackages) {
    resolvedFindPackages.addAll(findPackages);
  }

  public Collection<CMakeResolvedPackage> getResolvedPackages() {
    return resolvedFindPackages;
  }

  public void addProjectModule(final CMakeResolvedProject projectModule) {
    resolvedProjectModules.add(projectModule);
  }

  public void addProjects(final Collection<CMakeResolvedProject> projectModules) {
    resolvedProjectModules.addAll(projectModules);
  }

  public Collection<CMakeResolvedProject> getResolvedProjectModules() {
    return resolvedProjectModules;
  }

  public void add(final CMakeResolvedLibrary object) {
    resolvedLibraries.add(object);
  }

  public Collection<CMakeResolvedLibrary> getResolvedLibraries() {
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
