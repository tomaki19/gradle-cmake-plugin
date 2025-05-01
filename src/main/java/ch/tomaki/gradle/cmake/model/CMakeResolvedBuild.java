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
import java.util.Set;
import java.util.function.Consumer;

import ch.tomaki.gradle.cmake.extension.CMakeToolchain;

public class CMakeResolvedBuild {

  private final Map<String, CMakeResolvedToolchain> toolchains = new HashMap<>();
  private final Set<CMakeResolvedFindPackage> findPackages = new HashSet<>();
  private final Set<CMakeResolvedProjectModule> projectModules = new HashSet<>();
  private final Set<CMakeResolvedInterface> interfaces = new HashSet<>();
  private final Set<CMakeResolvedLibrary> libraries = new HashSet<>();
  private final Set<CMakeResolvedApplication> applications = new HashSet<>();
  private final Set<CMakeResolvedTest> tests = new HashSet<>();

  void add(final CMakeResolvedToolchain toolchain) {
    this.toolchains.put(toolchain.getName(), toolchain);
  }

  public boolean hasToolchain(final String name) {
    return toolchains.containsKey(name);
  }

  public CMakeResolvedToolchain getToolchain(final String name) {
    return toolchains.get(name);
  }

  public void forToolchains(final Consumer<CMakeResolvedToolchain> action) {
    toolchains.forEach((name, toolchain) -> action.accept(toolchain));
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
