/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CMakeResolvedBuild {

  private final Set<CMakeResolvedToolchain> toolchains = new HashSet<>();
  private final Set<CMakeResolvedFindPackage> findPackages = new HashSet<>();
  private final Set<CMakeResolvedProjectModule> projectModules = new HashSet<>();
  private final Set<CMakeResolvedInterface> interfaces = new HashSet<>();
  private final Set<CMakeResolvedLibrary> libraries = new HashSet<>();
  private final Set<CMakeResolvedApplication> applications = new HashSet<>();
  private final Set<CMakeResolvedTest> tests = new HashSet<>();

  void add(final CMakeResolvedToolchain toolchain) {
    this.toolchains.add(toolchain);
  }

  public Set<CMakeResolvedToolchain> getToolchains() {
    return toolchains;
  }

  public void addFindPackage(final CMakeResolvedFindPackage findPackage) {
    this.findPackages.add(findPackage);
  }

  public void addFindPackages(final Collection<CMakeResolvedFindPackage> findPackages) {
    this.findPackages.addAll(findPackages);
  }

  public Set<CMakeResolvedFindPackage> getFindPackages() {
    return findPackages;
  }

  public void addProjectModule(final CMakeResolvedProjectModule projectModule) {
    this.projectModules.add(projectModule);
  }

  public void addProjectModules(final Collection<CMakeResolvedProjectModule> projectModules) {
    this.projectModules.addAll(projectModules);
  }

  public Set<CMakeResolvedProjectModule> getProjectModules() {
    return projectModules;
  }

  public void add(final CMakeResolvedInterface object) {
    this.interfaces.add(object);
  }

  public Set<CMakeResolvedInterface> getInterfaces() {
    return interfaces;
  }

  public void add(final CMakeResolvedLibrary object) {
    this.libraries.add(object);
  }

  public Set<CMakeResolvedLibrary> getLibraries() {
    return libraries;
  }

  public void add(final CMakeResolvedApplication object) {
    this.applications.add(object);
  }

  public Set<CMakeResolvedApplication> getApplications() {
    return applications;
  }

  public void add(final CMakeResolvedTest object) {
    this.tests.add(object);
  }

  public Set<CMakeResolvedTest> getTests() {
    return tests;
  }

}
