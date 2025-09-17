/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;

import org.gradle.internal.os.OperatingSystem;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;

public final class CMakeResolvedToolchain extends CMakeResolvedName<CMakeResolvedToolchain> {

  private final OperatingSystem operatingSystem;
  private final String architecture;
  private final String compiler;
  private final String generator;
  private final Collection<String> buildConfigs;
  private final Map<String, String> environment;
  private final Optional<File> environmentFile;
  private final Optional<File> toolchainFile;
  private final Collection<CMakeResolvedSystemPackage> systemPackages;
  private final Collection<CMakeResolvedProject> projectPackages = new TreeSet<>();
  private final Collection<CMakeResolvedLibrary> libraries = new TreeSet<>();
  private final Collection<CMakeResolvedExecutable> applications = new TreeSet<>();
  private final Collection<CMakeResolvedExecutable> tests = new TreeSet<>();

  public CMakeResolvedToolchain(final CMakeToolchain toolchain,
      final Collection<CMakeResolvedSystemPackage> systemPackages) {
    super(toolchain.getName());
    this.operatingSystem = toolchain.getOperatingSystem().get();
    this.architecture = toolchain.getArchitecture().getOrElse("").toLowerCase();
    this.compiler = toolchain.getCompiler().getOrElse("").toLowerCase();
    this.generator = toolchain.getGenerator().getOrElse("");
    this.buildConfigs = new TreeSet<>(toolchain.getBuildConfigs().get());
    this.environment = new TreeMap<>(toolchain.getEnvironment().get());
    this.environmentFile = Optional.ofNullable(toolchain.getEnvironmentFile().getOrNull());
    this.toolchainFile = Optional.ofNullable(toolchain.getToolchainFile().getOrNull());
    this.systemPackages = new TreeSet<>(systemPackages);
  }

  public String getCompiler() {
    return compiler;
  }

  public OperatingSystem getOperatingSystem() {
    return operatingSystem;
  }

  public String getArchitecture() {
    return architecture;
  }

  public String getGenerator() {
    return generator;
  }

  public Collection<String> getBuildConfigs() {
    return buildConfigs;
  }

  public Map<String, String> getEnvironment() {
    return environment;
  }

  public Optional<File> getEnvironmentFile() {
    return environmentFile;
  }

  public Optional<File> getToolchainFile() {
    return toolchainFile;
  }

  public Collection<CMakeResolvedSystemPackage> getSystemPackages() {
    return systemPackages;
  }

  void addModule(final CMakeResolvedProject object) {
    projectPackages.add(object);
  }

  public Collection<CMakeResolvedProject> getProjectPackages() {
    return projectPackages;
  }

  void addLibrary(final CMakeResolvedLibrary object) {
    libraries.add(object);
  }

  public Collection<CMakeResolvedLibrary> getLibraries() {
    return libraries;
  }

  public boolean hasInterfaceLibraries() {
    return libraries.parallelStream()
        .anyMatch((library) -> library.getSources().isEmpty() && !library.getHeaders().isEmpty());
  }

  public boolean hasBinaryLibraries() {
    return libraries.parallelStream()
        .anyMatch((library) -> !library.getSources().isEmpty() && !library.getHeaders().isEmpty());
  }

  void addApplication(final CMakeResolvedExecutable object) {
    applications.add(object);
  }

  public Collection<CMakeResolvedExecutable> getApplications() {
    return applications;
  }

  public boolean hasApplications() {
    return !applications.isEmpty();
  }

  void addTest(final CMakeResolvedExecutable object) {
    tests.add(object);
  }

  public Collection<CMakeResolvedExecutable> getTests() {
    return tests;
  }

  public boolean hasTests() {
    return !tests.isEmpty();
  }

  public boolean hasBinaries() {
    return hasBinaryLibraries() || hasApplications() || hasTests();
  }

}
