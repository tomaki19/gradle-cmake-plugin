/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;

import org.gradle.internal.os.OperatingSystem;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;

public final class CMakeResolvedToolchain extends CMakeResolvedName<CMakeResolvedToolchain> {

  private final OperatingSystem operatingSystem;
  private final Collection<String> buildConfigs;
  private final Map<String, String> environment;
  private final Optional<File> environmentFile;
  private final Optional<File> toolchainFile;
  private final Optional<String> generator;
  private final Collection<CMakeResolvedProject> projects = new TreeSet<>();
  private final Collection<CMakeResolvedPackage> packages = new TreeSet<>();
  private final Collection<CMakeResolvedLibrary> interfaceLibraries = new TreeSet<>();
  private final Collection<CMakeResolvedLibrary> staticLibraries = new TreeSet<>();
  private final Collection<CMakeResolvedLibrary> sharedLibraries = new TreeSet<>();
  private final Collection<CMakeResolvedExecutable> applications = new TreeSet<>();
  private final Collection<CMakeResolvedExecutable> tests = new TreeSet<>();

  public CMakeResolvedToolchain(final CMakeToolchain toolchain) {
    super(toolchain.getName());
    this.operatingSystem = toolchain.getOperatingSystem();
    this.buildConfigs = new TreeSet<>(toolchain.getBuildConfigs());
    this.environment = new TreeMap<>(toolchain.getEnvironment());
    this.environmentFile = toolchain.getEnvironmentFile();
    this.toolchainFile = toolchain.getToolchainFile();
    this.generator = toolchain.getGenerator();
  }

  public OperatingSystem getOperatingSystem() {
    return operatingSystem;
  }

  public Collection<String> getBuildConfigs() {
    return Collections.unmodifiableCollection(buildConfigs);
  }

  public Map<String, String> getEnvironment() {
    return Collections.unmodifiableMap(environment);
  }

  public Optional<File> getEnvironmentFile() {
    return environmentFile;
  }

  public Optional<File> getToolchainFile() {
    return toolchainFile;
  }

  public Optional<String> getGenerator() {
    return generator;
  }

  void addProject(final CMakeResolvedProject component) {
    projects.add(component);
  }

  public Collection<CMakeResolvedPackage> getPackages() {
    return Collections.unmodifiableCollection(packages);
  }

  void addPackage(final CMakeResolvedPackage component) {
    packages.add(component);
  }

  public Collection<CMakeResolvedProject> getProjects() {
    return Collections.unmodifiableCollection(projects);
  }

  void addInterfaceLibrary(final CMakeResolvedLibrary component) {
    interfaceLibraries.add(component);
  }

  public Collection<CMakeResolvedLibrary> getInterfaceLibraries() {
    return Collections.unmodifiableCollection(interfaceLibraries);
  }

  void addStaticLibrary(final CMakeResolvedLibrary component) {
    staticLibraries.add(component);
  }

  public Collection<CMakeResolvedLibrary> getStaticLibraries() {
    return Collections.unmodifiableCollection(staticLibraries);
  }

  void addSharedLibrary(final CMakeResolvedLibrary component) {
    sharedLibraries.add(component);
  }

  public Collection<CMakeResolvedLibrary> getSharedLibraries() {
    return Collections.unmodifiableCollection(sharedLibraries);
  }

  public boolean hasInterfaceLibraries() {
    return !interfaceLibraries.isEmpty();
  }

  public boolean hasBinaryLibraries() {
    return !staticLibraries.isEmpty() || !sharedLibraries.isEmpty();
  }

  void addApplication(final CMakeResolvedExecutable component) {
    applications.add(component);
  }

  public Collection<CMakeResolvedExecutable> getApplications() {
    return Collections.unmodifiableCollection(applications);
  }

  public boolean hasApplications() {
    return !applications.isEmpty();
  }

  void addTest(final CMakeResolvedExecutable component) {
    tests.add(component);
  }

  public Collection<CMakeResolvedExecutable> getTests() {
    return Collections.unmodifiableCollection(tests);
  }

  public boolean hasTests() {
    return !tests.isEmpty();
  }

  public boolean hasBinaries() {
    return hasBinaryLibraries() || hasApplications() || hasTests();
  }

}
