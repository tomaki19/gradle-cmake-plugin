/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.gradle.api.Project;

import ch.tomaki.gradle.cmake.extension.api.CMakeBinary;
import ch.tomaki.gradle.cmake.extension.api.CMakeFindPackage;
import ch.tomaki.gradle.cmake.extension.api.CMakeToolchain;

public abstract class CMakeResolvedBinary extends CMakeResolvedNamedObject
    implements CMakeResolvedPrivatePackageDependencies, CMakeResolvedPrivateProjectDependencies {

  private final CMakeResolvedToolchain toolchain;
  private final Set<String> headers;
  private final Set<String> sources;
  private final Set<String> privateCompileOptions;
  private final Set<String> privateCompileDefinitions;
  private final Set<String> privateLinkOptions = new HashSet<>();
  private final Set<CMakeResolvedPackage> privatePackages = new HashSet<>();
  private final Set<CMakeResolvedPackageDependency> privatePackageDependencies = new HashSet<>();
  private final Set<CMakeResolvedProject> privateProjects = new HashSet<>();
  private final Set<CMakeResolvedProjectDependency> privateProjectDependencies = new HashSet<>();
  private final boolean buildStatic;
  private final boolean buildShared;
  private final boolean stripDebug;
  private final boolean packageBuildOutputs;

  CMakeResolvedBinary(final CMakeBinary binary, final CMakeToolchain toolchain,
      final Map<String, CMakeFindPackage> findPackages, final Project project) throws IllegalArgumentException {
    super(binary.getName());
    this.toolchain = new CMakeResolvedToolchain(toolchain);
    this.headers = binary.getHeaders().get();
    this.sources = binary.getSources().get();
    this.privateCompileOptions = binary.getPrivateCompileOptions().get();
    this.privateCompileDefinitions = binary.getPrivateCompileDefinitions().get();
    CMakeResolver.resolveLinkOptions(binary.getPrivateLinkDependencies().get(), privateLinkOptions);
    CMakeResolver.resolvePackageDependencies(binary.getPrivateLinkDependencies().get(), privatePackages,
        privatePackageDependencies, getToolchain(), findPackages);
    CMakeResolver.resolveProjectDependencies(binary.getPrivateLinkDependencies().get(), privateProjects,
        privateProjectDependencies, getToolchain(), project);
    addPrivateLinkDependencies(toolchain.getBinaries().getPrivateLinkDependencies().get(), findPackages,
        project);
    this.buildStatic = initBuildStatic(binary, toolchain);
    this.buildShared = initBuildShared(binary, toolchain);
    this.stripDebug = initStripDebug(binary, toolchain);
    this.packageBuildOutputs = initPackageBuildOutputs(binary, toolchain);
  }

  public CMakeResolvedToolchain getToolchain() {
    return toolchain;
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

  public Set<CMakeResolvedPackage> getPrivatePackages() {
    return privatePackages;
  }

  public Set<CMakeResolvedPackageDependency> getPrivatePackageDependencies() {
    return privatePackageDependencies;
  }

  public Set<CMakeResolvedProject> getPrivateProjects() {
    return privateProjects;
  }

  public Set<CMakeResolvedProjectDependency> getPrivateProjectDependencies() {
    return privateProjectDependencies;
  }

  protected abstract boolean initBuildStatic(final CMakeBinary binary, final CMakeToolchain toolchain);

  public boolean isBuildStatic() {
    return buildStatic;
  }

  protected abstract boolean initBuildShared(final CMakeBinary binary, final CMakeToolchain toolchain);

  public boolean isBuildShared() {
    return buildShared;
  }

  protected abstract boolean initStripDebug(final CMakeBinary binary, final CMakeToolchain toolchain);

  public boolean isStripDebug() {
    return stripDebug;
  }

  protected abstract boolean initPackageBuildOutputs(final CMakeBinary binary, final CMakeToolchain toolchain);

  public boolean isPackageBuildOutputs() {
    return packageBuildOutputs;
  }

  protected void addPrivateLinkDependencies(final Set<String> dependencies,
      final Map<String, CMakeFindPackage> findPackages, final Project project) throws IllegalArgumentException {
    CMakeResolver.resolveLinkOptions(dependencies, privateLinkOptions);
    CMakeResolver.resolvePackageDependencies(dependencies, privatePackages, privatePackageDependencies,
        getToolchain(), findPackages);
    CMakeResolver.resolveProjectDependencies(dependencies, privateProjects,
        privateProjectDependencies, getToolchain(), project);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((toolchain == null) ? 0 : toolchain.hashCode());
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
    return true;
  }

}
