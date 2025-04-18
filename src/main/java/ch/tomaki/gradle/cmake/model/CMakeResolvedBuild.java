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

public class CMakeResolvedBuild {

  private final String name;
  private final Map<String, CMakeResolvedToolchain> toolchains = new HashMap<>();
  private final Set<CMakeResolvedFindPackage> findPackages = new HashSet<>();
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

  public void put(final String name, final CMakeResolvedToolchain toolchain) {
    toolchains.put(name, toolchain);
  }

  public void forToolchain(final String name, final Consumer<CMakeResolvedToolchain> consumer) {
    toolchains.computeIfPresent(name, (key, toolchain) -> {
      consumer.accept(toolchain);
      return toolchain;
    });
  }

  public void add(final CMakeResolvedFindPackage findPackage) {
    findPackages.add(findPackage);
  }

  public Set<CMakeResolvedFindPackage> getFindPackages() {
    return findPackages;
  }

  public void add(final CMakeResolvedFindPackageDependency dependency) {
    findPackageDependencies.add(dependency);
  }

  public Set<CMakeResolvedFindPackageDependency> getFindPackageDependencies() {
    return findPackageDependencies;
  }

  public void add(final CMakeResolvedProjectModuleDependency dependency) {
    projectModuleDependencies.add(dependency);
  }

  public void addAll(final Collection<CMakeResolvedProjectModuleDependency> dependencies) {
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
