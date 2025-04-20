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

  private final Set<CMakeResolvedFindPackage> findPackages = new HashSet<>();
  private final Set<CMakeResolvedToolchain> toolchains = new HashSet<>();
  private final Set<CMakeResolvedFindPackageDependency> findPackageDependencies = new HashSet<>();
  private final Set<CMakeResolvedProjectModuleDependency> projectModuleDependencies = new HashSet<>();
  private final Set<CMakeResolvedInterface> interfaces = new HashSet<>();
  private final Set<CMakeResolvedLibrary> libraries = new HashSet<>();
  private final Set<CMakeResolvedApplication> applications = new HashSet<>();
  private final Set<CMakeResolvedTest> tests = new HashSet<>();

  public void add(final CMakeResolvedToolchain toolchain) {
    this.toolchains.add(toolchain);
  }

  public Set<CMakeResolvedToolchain> getToolchains() {
    return toolchains;
  }

  public void addFindPackageDependency(final CMakeResolvedFindPackageDependency dependency) {
    this.findPackageDependencies.add(dependency);
  }

  public void addFindPackageDependencies(final Collection<CMakeResolvedFindPackageDependency> dependencies) {
    this.findPackageDependencies.addAll(dependencies);
  }

  public Set<CMakeResolvedFindPackageDependency> getFindPackageDependencies() {
    return findPackageDependencies;
  }

  public void addProjectModuleDependency(final CMakeResolvedProjectModuleDependency dependency) {
    this.projectModuleDependencies.add(dependency);
  }

  public void addProjectModuleDependencies(final Collection<CMakeResolvedProjectModuleDependency> dependencies) {
    this.projectModuleDependencies.addAll(dependencies);
  }

  public Set<CMakeResolvedProjectModuleDependency> getProjectModuleDependencies() {
    return projectModuleDependencies;
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
