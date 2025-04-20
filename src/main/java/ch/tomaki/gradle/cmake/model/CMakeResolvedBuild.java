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
  private final Map<String, CMakeResolvedFindPackage> findPackages = new HashMap<>();
  private final Set<CMakeResolvedToolchain> toolchains = new HashSet<>();
  private final Set<CMakeResolvedFindPackageDependency> findPackageDependencies = new HashSet<>();
  private final Set<CMakeResolvedProjectModuleDependency> projectModuleDependencies = new HashSet<>();
  private final Set<CMakeResolvedInterface> interfaces = new HashSet<>();
  private final Set<CMakeResolvedLibrary> libraries = new HashSet<>();
  private final Set<CMakeResolvedApplication> applications = new HashSet<>();
  private final Set<CMakeResolvedTest> tests = new HashSet<>();

  public CMakeResolvedBuild(final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void add(final CMakeResolvedFindPackage findPackage) {
    findPackages.put(findPackage.getName(), findPackage);
  }

  public CMakeResolvedFindPackage getFindPackage(final String name) {
    return findPackages.get(name);
  }

  public void add(final CMakeResolvedToolchain toolchain) {
    toolchains.add(toolchain);
  }

  public Set<CMakeResolvedToolchain> getToolchains() {
    return toolchains;
  }

  public void addFindPackageDependency(final CMakeResolvedFindPackageDependency dependency) {
    findPackageDependencies.add(dependency);
  }

  public void addFindPackageDependencies(final Collection<CMakeResolvedFindPackageDependency> dependencies) {
    findPackageDependencies.addAll(dependencies);
  }

  public Set<CMakeResolvedFindPackageDependency> getFindPackageDependencies() {
    return findPackageDependencies;
  }

  public void addProjectModuleDependency(final CMakeResolvedProjectModuleDependency dependency) {
    projectModuleDependencies.add(dependency);
  }

  public void addProjectModuleDependencies(final Collection<CMakeResolvedProjectModuleDependency> dependencies) {
    projectModuleDependencies.addAll(dependencies);
  }

  public Set<CMakeResolvedProjectModuleDependency> getProjectModuleDependencies() {
    return projectModuleDependencies;
  }

  public void add(final CMakeResolvedInterface object) {
    interfaces.add(object);
  }

  public Set<CMakeResolvedInterface> getInterfaces() {
    return interfaces;
  }

  public void add(final CMakeResolvedLibrary object) {
    libraries.add(object);
  }

  public Set<CMakeResolvedLibrary> getLibraries() {
    return libraries;
  }

  public void add(final CMakeResolvedApplication object) {
    applications.add(object);
  }

  public Set<CMakeResolvedApplication> getApplications() {
    return applications;
  }

  public void add(final CMakeResolvedTest object) {
    tests.add(object);
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
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CMakeResolvedBuild other = (CMakeResolvedBuild) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }
}
