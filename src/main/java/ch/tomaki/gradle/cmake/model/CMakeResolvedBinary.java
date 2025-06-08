/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.gradle.api.Project;

import ch.tomaki.gradle.cmake.extension.api.CMakeBinary;
import ch.tomaki.gradle.cmake.extension.api.CMakeFindPackage;
import ch.tomaki.gradle.cmake.extension.api.CMakeToolchain;

public abstract class CMakeResolvedBinary implements CMakeResolvedPrivatePackageDependencies,
    CMakeResolvedPrivateProjectDependencies {

  private final CMakeResolvedToolchain toolchain;
  private final String buildConfig;
  private final String name;
  private final Set<String> headers;
  private final Set<String> sources;
  private final Set<String> privateCompileOptions;
  private final Set<String> privateCompileDefinitions;
  private final Set<String> privateLinkOptions;
  private final Set<CMakeResolvedFindPackage> privateFindPackages;
  private final Set<CMakeResolvedFindPackageDependency> privateFindPackageDependencies;
  private final Set<CMakeResolvedProjectModule> privateProjectModules;
  private final Set<CMakeResolvedProjectModuleDependency> privateProjectModuleDependencies;

  CMakeResolvedBinary(final CMakeBinary binary, final CMakeToolchain toolchain, final String buildConfig,
      final Map<String, CMakeFindPackage> findPackages, final Project project) throws IllegalArgumentException {
    this.toolchain = new CMakeResolvedToolchain(toolchain);
    this.buildConfig = buildConfig;
    this.name = binary.getName();
    this.headers = new HashSet<>(binary.getHeaders().get());
    this.sources = new HashSet<>(binary.getSources().get());
    this.privateCompileOptions = new HashSet<>(binary.getPrivateCompileOptions().get());
    this.privateCompileDefinitions = new HashSet<>(binary.getPrivateCompileDefinitions().get());
    this.privateLinkOptions = new HashSet<>();
    this.privateFindPackages = new HashSet<>();
    this.privateFindPackageDependencies = new HashSet<>();
    this.privateProjectModules = new HashSet<>();
    this.privateProjectModuleDependencies = new HashSet<>();
    addPrivateLinkDependencies(toolchain.getBinaries().getPrivateLinkDependencies().get(), findPackages,
        project);
  }

  public CMakeResolvedToolchain getToolchain() {
    return toolchain;
  }

  public String getBuildConfig() {
    return buildConfig;
  }

  public String getName() {
    return name;
  }

  public Set<String> getHeaders() {
    return headers;
  }

  public Set<String> getSources() {
    return sources;
  }

  public Set<String> getPrivateCompileOptions() {
    return privateCompileOptions;
  }

  public Set<String> getPrivateCompileDefinitions() {
    return privateCompileDefinitions;
  }

  public Set<String> getPrivateLinkOptions() {
    return privateLinkOptions;
  }

  public Set<CMakeResolvedFindPackage> getPrivateFindPackages() {
    return privateFindPackages;
  }

  public Set<CMakeResolvedFindPackageDependency> getPrivateFindPackageDependencies() {
    return privateFindPackageDependencies;
  }

  public Set<CMakeResolvedProjectModule> getPrivateProjectModules() {
    return privateProjectModules;
  }

  public Set<CMakeResolvedProjectModuleDependency> getPrivateProjectModuleDependencies() {
    return privateProjectModuleDependencies;
  }

  public abstract boolean isBuildStatic();

  public abstract boolean isBuildShared();

  public abstract boolean isStripDebug();

  public abstract boolean isPackageBuildOutputs();

  protected void addPrivateLinkDependencies(final Set<String> dependencies,
      final Map<String, CMakeFindPackage> findPackages, final Project project) throws IllegalArgumentException {
    resolveLinkOptions(privateLinkOptions, dependencies);
    CMakeResolvedFindPackage.resolveFindPackageDependencies(privateFindPackages, privateFindPackageDependencies,
        Optional.of(getToolchain()), findPackages, dependencies);
    CMakeResolvedProjectModule.resolveProjectModuleDependencies(privateProjectModules, privateProjectModuleDependencies,
        Optional.of(getToolchain()), Optional.of(buildConfig), dependencies, project);
  }

  public static void resolveLinkOptions(final Set<String> linkOptions, final Set<String> dependencies)
      throws IllegalArgumentException {
    for (final String dependency : dependencies) {
      if (dependency.startsWith("-")) {
        final String[] dependencyTokens = dependency.split("::");
        if (dependencyTokens.length <= 2) {
          linkOptions.add(dependency);
        } else {
          throw new IllegalArgumentException(
              "Invalid link option declaration: '%s'!".formatted(dependency));
        }
      }
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    CMakeResolvedBinary other = (CMakeResolvedBinary) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

}
