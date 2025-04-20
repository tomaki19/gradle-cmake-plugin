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
  private final Set<CMakeResolvedFindPackage> privateFindPackages;
  private final Set<CMakeResolvedFindPackageDependency> privateFindPackageDependencies;
  private final Set<CMakeResolvedProjectModule> privateProjectModules;
  private final Set<CMakeResolvedProjectModuleDependency> privateProjectModuleDependencies;
  private final boolean buildStatic;
  private final boolean buildShared;
  private final boolean stripDebug;
  private final boolean packageBuildOutputs;

  CMakeResolvedBinary(final CMakeObject object, final CMakeResolvedToolchain toolchain, final String buildConfig,
      final Map<String, CMakeFindPackage> findPackages, final Project project) throws IllegalArgumentException {
    super(object);
    this.sources = new HashSet<>(object.getSources().get());
    this.privateCompileOptions = new HashSet<>(object.getPrivateCompileOptions().get());
    this.privateCompileDefinitions = new HashSet<>(object.getPrivateCompileDefinitions().get());
    this.privateLinkOptions = resolveLinkOptions(object.getPrivateLinkDependencies().get());
    this.privateFindPackages = new HashSet<>();
    this.privateFindPackageDependencies = new HashSet<>();
    resolveFindPackageDependencies(privateFindPackages, privateFindPackageDependencies, toolchain, findPackages,
        object.getPrivateLinkDependencies().get());
    this.privateProjectModules = new HashSet<>();
    this.privateProjectModuleDependencies = new HashSet<>();
    resolveProjectModuleDependencies(privateProjectModules, privateProjectModuleDependencies, project, toolchain,
        buildConfig, object.getPrivateLinkDependencies().get());
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

  public void addPrivateLinkDependencies(final Set<String> dependencies,
      final Map<String, CMakeFindPackage> findPackages, final Project project) throws IllegalArgumentException {
    privateLinkOptions.addAll(resolveLinkOptions(dependencies));
    resolveFindPackageDependencies(privateFindPackages, privateFindPackageDependencies, toolchain, findPackages,
        dependencies);
    resolveProjectModuleDependencies(privateProjectModules, privateProjectModuleDependencies, project, toolchain,
        buildConfig, dependencies);
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
      final Set<CMakeResolvedProjectModuleDependency> projectModuleDependencies, final Project project,
      final CMakeResolvedToolchain toolchain, final String buildConfig, final Set<String> dependencies)
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
            final String buildTarget = getBuildTarget(dependencyTokens[2], dependencyTokens[1], toolchain, buildConfig);
            final CMakeResolvedProjectModuleDependency resolvedProjectModule = new CMakeResolvedProjectModuleDependency(
                buildTarget, isBuildable(dependencyTokens[2]), toolchain, dependencyProject);
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
