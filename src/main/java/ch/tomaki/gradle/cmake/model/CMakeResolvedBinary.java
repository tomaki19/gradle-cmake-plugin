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
import ch.tomaki.gradle.cmake.files.CMakeListsConventions;

public abstract class CMakeResolvedBinary extends CMakeResolvedObject {

  private final String buildConfig;
  private final CMakeResolvedToolchain toolchain;
  private final Set<String> sources;
  private final Set<String> privateCompileOptions;
  private final Set<String> privateCompileDefinitions;
  private final Set<String> privateLinkOptions;
  private final Set<CMakeResolvedFindPackageDependency> privateFindPackageDependencies;
  private final Set<CMakeResolvedProjectModuleDependency> privateProjectModuleDependencies;
  private final boolean buildStatic;
  private final boolean buildShared;
  private final boolean stripDebug;
  private final boolean packageBuildOutputs;

  public CMakeResolvedBinary(final CMakeObject object, final CMakeResolvedToolchain toolchain, final String buildConfig,
      final Map<String, CMakeFindPackage> findPackages, final Project project) throws IllegalArgumentException {
    super(object);
    this.sources = new HashSet<>(object.getSources().get());
    this.privateCompileOptions = new HashSet<>(object.getPrivateCompileOptions().get());
    this.privateCompileDefinitions = new HashSet<>(object.getPrivateCompileDefinitions().get());
    this.privateLinkOptions = resolveLinkOptions(object.getPrivateLinkDependencies().get());
    this.privateFindPackageDependencies = resolveFindPackageDependencies(
        object.getPrivateLinkDependencies().get(), findPackages, toolchain);
    this.privateProjectModuleDependencies = resolveProjectModuleDependencies(
        object.getPrivateLinkDependencies().get(), buildConfig, toolchain, project);
    this.buildStatic = object.getBuildStatic().getOrElse(Boolean.FALSE) || toolchain.isBuildStatic();
    this.buildShared = object.getBuildShared().getOrElse(Boolean.FALSE) || toolchain.isBuildShared();
    this.stripDebug = object.getStripDebug().getOrElse(Boolean.FALSE) || toolchain.isStripDebug();
    this.packageBuildOutputs = object.getPackageBuildOutputs().getOrElse(Boolean.FALSE)
        || toolchain.isPackageBuildOutputs();
    this.toolchain = toolchain;
    this.buildConfig = buildConfig;
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

  public void addLibraryDependencies(final Set<String> dependencies,
      final Map<String, CMakeFindPackage> findPackages, final Project project) throws IllegalArgumentException {
    privateLinkOptions.addAll(resolveLinkOptions(dependencies));
    privateFindPackageDependencies.addAll(resolveFindPackageDependencies(dependencies, findPackages, toolchain));
    privateProjectModuleDependencies
        .addAll(resolveProjectModuleDependencies(dependencies, buildConfig, toolchain, project));
  }

  public Set<String> getPrivateLinkOptions() {
    return privateLinkOptions;
  }

  public Set<CMakeResolvedFindPackageDependency> getPrivateFindPackageDependencies() {
    return privateFindPackageDependencies;
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

  protected Set<String> resolveLinkOptions(final Set<String> dependencies)
      throws IllegalArgumentException {
    final Set<String> resolvedLinkOptions = new HashSet<>();
    for (final String dependency : dependencies) {
      if (dependency.startsWith("-")) {
        final String[] dependencyTokens = dependency.split("::");
        if (dependencyTokens.length <= 2) {
          resolvedLinkOptions.add(dependency);
        } else {
          throw new IllegalArgumentException(
              "Invalid link option declaration: '%s'!".formatted(dependency));
        }
      }
    }
    return resolvedLinkOptions;
  }

  protected Set<CMakeResolvedFindPackageDependency> resolveFindPackageDependencies(final Set<String> dependencies,
      final Map<String, CMakeFindPackage> findPackages, final CMakeResolvedToolchain toolchain)
      throws IllegalArgumentException {
    final Set<CMakeResolvedFindPackageDependency> resolvedFindPackageDependencies = new HashSet<>();
    for (final String dependency : dependencies) {
      if (!dependency.startsWith("-")) {
        final String[] dependencyTokens = dependency.split("::");
        if (dependencyTokens.length <= 2) {
          if (findPackages.containsKey(dependencyTokens[0])) {
            final CMakeResolvedFindPackageDependency resolvedFindPackage = new CMakeResolvedFindPackageDependency(
                dependency, findPackages.get(dependencyTokens[0]), toolchain);
            resolvedFindPackageDependencies.add(resolvedFindPackage);
          } else {
            throw new IllegalArgumentException(
                "Missing find package declaration for '%s'!".formatted(dependency));
          }
        }
      }
    }
    return resolvedFindPackageDependencies;
  }

  protected Set<CMakeResolvedProjectModuleDependency> resolveProjectModuleDependencies(final Set<String> dependencies,
      final String buildConfig, final CMakeResolvedToolchain toolchain, final Project project)
      throws IllegalArgumentException {
    final Set<CMakeResolvedProjectModuleDependency> resolvedProjectModuleDependencies = new HashSet<>();
    for (final String dependency : dependencies) {
      if (!dependency.startsWith("-")) {
        final String[] dependencyTokens = dependency.split("::");
        if (dependencyTokens.length == 3) {
          final Project dependencyProject = project.findProject(":%s".formatted(dependencyTokens[0]));
          if (Objects.nonNull(dependencyProject)) {
            final String buildTarget = getBuildTarget(dependencyTokens[2], dependencyTokens[1], toolchain, buildConfig);
            final CMakeResolvedProjectModuleDependency resolvedProjectModule = new CMakeResolvedProjectModuleDependency(
                buildTarget, isBuildable(dependencyTokens[2]), toolchain, dependencyProject);
            resolvedProjectModuleDependencies.add(resolvedProjectModule);
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
    return resolvedProjectModuleDependencies;
  }

  private static String getBuildTarget(final String buildType, final String name, CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    return switch (buildType.toLowerCase()) {
      case "static" -> CMakeListsConventions.staticLibraryTarget(name, toolchain, buildConfig);
      case "shared" -> CMakeListsConventions.sharedLibraryTarget(name, toolchain, buildConfig);
      default -> CMakeListsConventions.interfaceLibraryTarget(name);
    };
  }

  private static boolean isBuildable(final String libraryType) {
    return switch (libraryType.toLowerCase()) {
      case "interface" -> false;
      default -> true;
    };
  }

  public CMakeResolvedToolchain getToolchain() {
    return toolchain;
  }

  public String getBuildConfig() {
    return buildConfig;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((toolchain == null) ? 0 : toolchain.hashCode());
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
    if (toolchain == null) {
      if (other.toolchain != null)
        return false;
    } else if (!toolchain.equals(other.toolchain))
      return false;
    if (buildConfig == null) {
      if (other.buildConfig != null)
        return false;
    } else if (!buildConfig.equals(other.buildConfig))
      return false;
    return true;
  }

}
