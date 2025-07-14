/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.gradle.internal.os.OperatingSystem;

import ch.tomaki.gradle.cmake.extension.api.CMakeToolchain;

public final class CMakeResolvedToolchain extends CMakeResolvedName {

  private final OperatingSystem operatingSystem;
  private final String architecture;
  private final String compiler;
  private final String generator;
  private final Set<String> buildConfigs;
  private final Map<String, String> environment;
  private final Optional<File> environmentFile;
  private final Optional<File> toolchainFile;
  private final Set<CMakeResolvedSystemPackage> systemPackages = new HashSet<>();
  private final Set<CMakeResolvedProjectPackage> projectPackages = new HashSet<>();
  private final Set<CMakeResolvedLibrary> libraries = new HashSet<>();
  private final Set<CMakeResolvedApplication> applications = new HashSet<>();
  private final Set<CMakeResolvedTest> tests = new HashSet<>();

  public CMakeResolvedToolchain(final CMakeToolchain toolchain) {
    super(toolchain.getName());
    this.operatingSystem = toolchain.getOperatingSystem().get();
    this.architecture = toolchain.getArchitecture().getOrElse("").toLowerCase();
    this.compiler = toolchain.getCompiler().getOrElse("").toLowerCase();
    this.generator = toolchain.getGenerator().getOrElse("");
    this.buildConfigs = toolchain.getBuildConfigs().get();
    this.environment = toolchain.getEnvironment().getOrNull();
    this.environmentFile = Optional.ofNullable(toolchain.getEnvironmentFile().getOrNull());
    this.toolchainFile = Optional.ofNullable(toolchain.getToolchainFile().getOrNull());
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

  public Set<String> getBuildConfigs() {
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

  void addPackage(final CMakeResolvedSystemPackage object) {
    systemPackages.add(object);
  }

  public Set<CMakeResolvedSystemPackage> getSystemPackages() {
    return systemPackages;
  }

  void addModule(final CMakeResolvedProjectPackage object) {
    projectPackages.add(object);
  }

  public Set<CMakeResolvedProjectPackage> getProjectPackages() {
    return projectPackages;
  }

  void addLibrary(final CMakeResolvedLibrary object) {
    libraries.add(object);
  }

  public Set<CMakeResolvedLibrary> getLibraries() {
    return libraries;
  }

  void addApplication(final CMakeResolvedApplication object) {
    applications.add(object);
  }

  public Set<CMakeResolvedApplication> getApplications() {
    return applications;
  }

  void addTest(final CMakeResolvedTest object) {
    tests.add(object);
  }

  public Set<CMakeResolvedTest> getTests() {
    return tests;
  }

  public boolean isUsed() {
    return !libraries.isEmpty() || !applications.isEmpty() || !tests.isEmpty();
  }

}
