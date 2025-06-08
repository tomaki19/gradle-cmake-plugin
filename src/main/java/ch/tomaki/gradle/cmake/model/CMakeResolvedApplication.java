/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.Map;
import java.util.Optional;

import org.gradle.api.Project;

import ch.tomaki.gradle.cmake.extension.api.CMakeApplication;
import ch.tomaki.gradle.cmake.extension.api.CMakeFindPackage;
import ch.tomaki.gradle.cmake.extension.api.CMakeToolchain;

public final class CMakeResolvedApplication extends CMakeResolvedBinary {

  private final boolean buildStatic;
  private final boolean buildShared;
  private final boolean stripDebug;
  private final boolean packageBuildOutputs;

  CMakeResolvedApplication(final CMakeApplication application, final CMakeToolchain toolchain,
      final String buildConfig, final Map<String, CMakeFindPackage> findPackages, final Project project) {
    super(application, toolchain, buildConfig, findPackages, project);
    CMakeResolvedBinary.resolveLinkOptions(getPrivateLinkOptions(), application.getPrivateLinkDependencies().get());
    CMakeResolvedFindPackage.resolveFindPackageDependencies(getPrivateFindPackages(),
        getPrivateFindPackageDependencies(), Optional.of(getToolchain()), findPackages,
        application.getPrivateLinkDependencies().get());
    CMakeResolvedProjectModule.resolveProjectModuleDependencies(getPrivateProjectModules(),
        getPrivateProjectModuleDependencies(), Optional.of(getToolchain()), Optional.of(buildConfig),
        application.getPrivateLinkDependencies().get(), project);
    this.buildStatic = application.getBuildStatic().getOrElse(Boolean.FALSE)
        || toolchain.getApplications().getBuildStatic().getOrElse(Boolean.FALSE)
        || toolchain.getBinaries().getBuildStatic().getOrElse(Boolean.FALSE);
    this.buildShared = application.getBuildShared().getOrElse(Boolean.FALSE)
        || toolchain.getApplications().getBuildShared().getOrElse(Boolean.FALSE)
        || toolchain.getBinaries().getBuildShared().getOrElse(Boolean.TRUE);
    this.stripDebug = application.getStripDebug().getOrElse(Boolean.FALSE)
        || toolchain.getApplications().getStripDebug().getOrElse(Boolean.FALSE)
        || toolchain.getBinaries().getStripDebug().getOrElse(Boolean.FALSE);
    this.packageBuildOutputs = application.getPackageBuildOutputs().getOrElse(Boolean.FALSE)
        || toolchain.getApplications().getPackageBuildOutputs().getOrElse(Boolean.FALSE)
        || toolchain.getBinaries().getPackageBuildOutputs().getOrElse(Boolean.FALSE);
    addPrivateLinkDependencies(toolchain.getApplications().getPrivateLinkDependencies().get(), findPackages,
        project);
  }

  @Override
  public boolean isBuildStatic() {
    return buildStatic;
  }

  @Override
  public boolean isBuildShared() {
    return buildShared;
  }

  @Override
  public boolean isStripDebug() {
    return stripDebug;
  }

  @Override
  public boolean isPackageBuildOutputs() {
    return packageBuildOutputs;
  }

}
