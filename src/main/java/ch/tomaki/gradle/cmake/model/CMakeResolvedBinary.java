/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.gradle.api.Project;

import ch.tomaki.gradle.cmake.extension.CMakeFindPackage;
import ch.tomaki.gradle.cmake.extension.CMakeObject;
import ch.tomaki.gradle.cmake.extension.CMakeToolchain;
import ch.tomaki.gradle.cmake.files.CMakeLinkType;
import ch.tomaki.gradle.cmake.files.CMakeListsConventions;

public abstract class CMakeResolvedBinary extends CMakeResolvedObject {

  private final CMakeResolvedToolchain resolvedToolchain;
  private final String buildConfig;
  private final Set<String> sources;
  private final Set<String> privateCompileOptions;
  private final Set<String> privateCompileDefinitions;
  private final Set<String> privateLinkOptions;
  private final Set<CMakeResolvedFindPackage> privateFindPackages;
  private final Set<CMakeResolvedFindPackageDependency> privateFindPackageDependencies;
  private final Set<CMakeResolvedProjectModule> privateProjectModules;
  private final Set<CMakeResolvedProjectModuleDependency> privateProjectModuleDependencies;
  private final boolean buildStatic;
  private final boolean buildShared;
  private final boolean stripDebug;
  private final boolean packageBuildOutputs;

  CMakeResolvedBinary(final CMakeObject object, final CMakeToolchain toolchain, final String buildConfig,
      final Map<String, CMakeFindPackage> findPackages, final Project project) throws IllegalArgumentException {
    super(object);
    this.resolvedToolchain = new CMakeResolvedToolchain(toolchain);
    this.buildConfig = buildConfig;
    this.sources = new HashSet<>(object.getSources().get());
    this.privateCompileOptions = new HashSet<>(object.getPrivateCompileOptions().get());
    this.privateCompileDefinitions = new HashSet<>(object.getPrivateCompileDefinitions().get());
    this.privateLinkOptions = new HashSet<>();
    resolveLinkOptions(privateLinkOptions, object.getPrivateLinkDependencies().get());
    this.privateFindPackages = new HashSet<>();
    this.privateFindPackageDependencies = new HashSet<>();
    resolveFindPackageDependencies(privateFindPackages, privateFindPackageDependencies, resolvedToolchain, findPackages,
        object.getPrivateLinkDependencies().get());
    this.privateProjectModules = new HashSet<>();
    this.privateProjectModuleDependencies = new HashSet<>();
    resolveProjectModuleDependencies(privateProjectModules, privateProjectModuleDependencies, resolvedToolchain,
        object.getPrivateLinkDependencies().get(), buildConfig, project);
    this.buildStatic = object.getBuildStatic().getOrElse(Boolean.FALSE)
        || toolchain.getBuildStatic().getOrElse(Boolean.FALSE);
    this.buildShared = object.getBuildShared().getOrElse(Boolean.FALSE)
        || toolchain.getBuildShared().getOrElse(Boolean.TRUE);
    this.stripDebug = object.getStripDebug().getOrElse(Boolean.FALSE)
        || toolchain.getStripDebug().getOrElse(Boolean.FALSE);
    this.packageBuildOutputs = object.getPackageBuildOutputs().getOrElse(Boolean.FALSE)
        || toolchain.getPackageBuildOutputs().getOrElse(Boolean.FALSE);
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

  public boolean isBuildStatic() {
    return buildStatic;
  }

  public boolean isBuildShared() {
    return buildShared;
  }

  public boolean isStripDebug() {
    return stripDebug;
  }

  public boolean isPackageBuildOutputs() {
    return packageBuildOutputs;
  }

  protected void addPrivateLinkDependencies(final Set<String> dependencies,
      final Map<String, CMakeFindPackage> findPackages, final Project project)
      throws IllegalArgumentException {
    resolveLinkOptions(privateLinkOptions, dependencies);
    resolveFindPackageDependencies(privateFindPackages, privateFindPackageDependencies, getResolvedToolchain(),
        findPackages, dependencies);
    resolveProjectModuleDependencies(privateProjectModules, privateProjectModuleDependencies,
        getResolvedToolchain(), dependencies, buildConfig, project);
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

  protected static void resolveFindPackageDependencies(final Set<CMakeResolvedFindPackage> findPackages,
      final Set<CMakeResolvedFindPackageDependency> findPackageDependencies, final CMakeResolvedToolchain toolchain,
      final Map<String, CMakeFindPackage> availableFindPackages, final Set<String> dependencies)
      throws IllegalArgumentException {
    for (final String dependency : dependencies) {
      if (!dependency.startsWith("-")) {
        final String[] dependencyTokens = dependency.split("::");
        if (dependencyTokens.length <= 2) {
          if (availableFindPackages.containsKey(dependencyTokens[0])) {
            final CMakeFindPackage findPackage = availableFindPackages.get(dependencyTokens[0]);
            findPackages.add(new CMakeResolvedFindPackage(findPackage, toolchain));
            findPackageDependencies.add(new CMakeResolvedFindPackageDependency(dependency));
          } else {
            throw new IllegalArgumentException("Missing find package declaration for '%s'!".formatted(dependency));
          }
        }
      }
    }
  }

  protected static void resolveProjectModuleDependencies(final Set<CMakeResolvedProjectModule> projectModules,
      final Set<CMakeResolvedProjectModuleDependency> projectModuleDependencies, final CMakeResolvedToolchain toolchain,
      final Set<String> dependencies, final String buildConfig, final Project project)
      throws IllegalArgumentException {
    for (final String dependency : dependencies) {
      if (!dependency.startsWith("-")) {
        final String[] dependencyTokens = dependency.split("::");
        if (dependencyTokens.length == 3) {
          final Project dependencyProject = project.findProject(":%s".formatted(dependencyTokens[0]));
          if (Objects.nonNull(dependencyProject)) {
            if (!Objects.equals(project, dependencyProject)) {
              final CMakeResolvedProjectModule projectModule = new CMakeResolvedProjectModule(dependencyProject,
                  toolchain);
              projectModules.add(projectModule);
            }
            final CMakeLinkType type = CMakeLinkType.valueOf(dependencyTokens[2].toUpperCase());
            final String buildTarget = CMakeListsConventions.libraryTarget(dependencyTokens[1], toolchain, type,
                buildConfig);
            final CMakeResolvedProjectModuleDependency resolvedProjectModule = new CMakeResolvedProjectModuleDependency(
                dependencyProject, toolchain, type, buildTarget);
            projectModuleDependencies.add(resolvedProjectModule);
          } else {
            throw new IllegalArgumentException(
                "Missing local project: '%s'!".formatted(dependencyTokens[0]));
          }
        } else if (dependencyTokens.length > 3) {
          throw new IllegalArgumentException(
              "Invalid project dependency declaration: '%s'!".formatted(dependency));
        }
      }
    }
  }

  public CMakeResolvedToolchain getResolvedToolchain() {
    return resolvedToolchain;
  }

  public String getBuildConfig() {
    return buildConfig;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((resolvedToolchain == null) ? 0 : resolvedToolchain.hashCode());
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
    CMakeResolvedBinary other = (CMakeResolvedBinary) obj;
    if (resolvedToolchain == null) {
      if (other.resolvedToolchain != null)
        return false;
    } else if (!resolvedToolchain.equals(other.resolvedToolchain))
      return false;
    if (buildConfig == null) {
      if (other.buildConfig != null)
        return false;
    } else if (!buildConfig.equals(other.buildConfig))
      return false;
    return true;
  }

}
