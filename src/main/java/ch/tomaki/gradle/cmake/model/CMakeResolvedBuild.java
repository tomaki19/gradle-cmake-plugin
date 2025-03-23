
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

public class CMakeResolvedBuild {

  private final String name;
  private final Map<String, CMakeResolvedToolchain> toolchains = new HashMap<>();
  private final Set<CMakeResolvedFindPackage> findPackages = new HashSet<>();
  private final Set<CMakeResolvedFindPackageDependency> findPackageDependencies = new HashSet<>();
  private final Set<CMakeResolvedProjectModuleDependency> projectModuleDependencies =
      new HashSet<>();
  private final Set<CMakeResolvedApplication> applications = new HashSet<>();
  private final Set<CMakeResolvedLibrary> libraries = new HashSet<>();
  private final Set<CMakeResolvedTest> tests = new HashSet<>();

  public CMakeResolvedBuild(final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void put(final String name, final CMakeResolvedToolchain toolchain) {
    toolchains.put(name, toolchain);
  }

  public Map<String, CMakeResolvedToolchain> getToolchains() {
    return toolchains;
  }

  public void add(final CMakeResolvedFindPackage findPackage) {
    findPackages.add(findPackage);
  }

  public Set<CMakeResolvedFindPackage> getFindPackages() {
    return findPackages;
  }

  public void add(final CMakeResolvedFindPackageDependency findPackageDependency) {
    findPackageDependencies.add(findPackageDependency);
  }

  public Set<CMakeResolvedFindPackageDependency> getFindPackageDependencies() {
    return findPackageDependencies;
  }

  public void add(final CMakeResolvedProjectModuleDependency projectDependency) {
    projectModuleDependencies.add(projectDependency);
  }

  public void addAll(final Collection<CMakeResolvedProjectModuleDependency> projectDependencies) {
    projectModuleDependencies.addAll(projectDependencies);
  }

  public Set<CMakeResolvedProjectModuleDependency> getProjectModuleDependencies() {
    return projectModuleDependencies;
  }

  public void add(final CMakeResolvedApplication application) {
    applications.add(application);
  }

  public Set<CMakeResolvedApplication> getApplications() {
    return applications;
  }

  public void add(final CMakeResolvedLibrary library) {
    libraries.add(library);
  }

  public Set<CMakeResolvedLibrary> getLibraries() {
    return libraries;
  }

  public void add(final CMakeResolvedTest test) {
    tests.add(test);
  }

  public Set<CMakeResolvedTest> getTests() {
    return tests;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    CMakeResolvedBuild other = (CMakeResolvedBuild) obj;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    return true;
  }
}
