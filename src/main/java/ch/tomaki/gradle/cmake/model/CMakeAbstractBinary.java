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

public abstract class CMakeAbstractBinary extends CMakeAbstractInterface
    implements CMakeResolvedPrivatePackageDependencies,
    CMakeResolvedPrivateProjectDependencies {

  private final CMakeResolvedToolchain toolchain;
  private final String buildConfig;
  private final Set<String> sources;
  private final Set<String> privateCompileOptions;
  private final Set<String> privateCompileDefinitions;
  private final Set<String> privateLinkOptions;
  private final Set<CMakeResolvedFindPackage> privateFindPackages;
  private final Set<CMakeResolvedFindPackageDependency> privateFindPackageDependencies;
  private final Set<CMakeResolvedProjectModule> privateProjectModules;
  private final Set<CMakeResolvedProjectModuleDependency> privateProjectModuleDependencies;

  CMakeAbstractBinary(final CMakeBinary object, final CMakeToolchain toolchain, final String buildConfig,
      final Map<String, CMakeFindPackage> findPackages, final Project project) throws IllegalArgumentException {
    super(object);
    this.toolchain = new CMakeResolvedToolchain(toolchain);
    this.buildConfig = buildConfig;
    this.sources = new HashSet<>(object.getSources().get());
    this.privateCompileOptions = new HashSet<>(object.getPrivateCompileOptions().get());
    this.privateCompileDefinitions = new HashSet<>(object.getPrivateCompileDefinitions().get());
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

  protected static void resolveLinkOptions(final Set<String> linkOptions, final Set<String> dependencies)
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
    result = prime * result + ((buildConfig == null) ? 0 : buildConfig.hashCode());
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
    CMakeAbstractBinary other = (CMakeAbstractBinary) obj;
    if (buildConfig == null) {
      if (other.buildConfig != null)
        return false;
    } else if (!buildConfig.equals(other.buildConfig))
      return false;
    return true;
  }

}
