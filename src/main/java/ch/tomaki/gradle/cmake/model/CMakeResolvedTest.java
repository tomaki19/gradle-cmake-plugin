/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.Map;
import java.util.Optional;

import org.gradle.api.Project;

import ch.tomaki.gradle.cmake.extension.api.CMakeFindPackage;
import ch.tomaki.gradle.cmake.extension.api.CMakeTest;
import ch.tomaki.gradle.cmake.extension.api.CMakeToolchain;

public final class CMakeResolvedTest extends CMakeResolvedBinary {

  private final boolean buildStatic;
  private final boolean buildShared;
  private final boolean stripDebug;
  private final boolean packageBuildOutputs;

  CMakeResolvedTest(final CMakeTest test, final CMakeToolchain toolchain, final String buildConfig,
      final Map<String, CMakeFindPackage> findPackages, final Project project) {
    super(test, toolchain, buildConfig, findPackages, project);
    CMakeResolvedBinary.resolveLinkOptions(getPrivateLinkOptions(), test.getPrivateLinkDependencies().get());
    CMakeResolvedFindPackage.resolveFindPackageDependencies(getPrivateFindPackages(),
        getPrivateFindPackageDependencies(), Optional.of(getToolchain()), findPackages,
        test.getPrivateLinkDependencies().get());
    CMakeResolvedProjectModule.resolveProjectModuleDependencies(getPrivateProjectModules(),
        getPrivateProjectModuleDependencies(), Optional.of(getToolchain()), Optional.of(buildConfig),
        test.getPrivateLinkDependencies().get(), project);
    this.buildStatic = test.getBuildStatic().getOrElse(Boolean.FALSE)
        || toolchain.getTests().getBuildStatic().getOrElse(Boolean.FALSE)
        || toolchain.getBinaries().getBuildStatic().getOrElse(Boolean.FALSE);
    this.buildShared = test.getBuildShared().getOrElse(Boolean.FALSE)
        || toolchain.getTests().getBuildShared().getOrElse(Boolean.FALSE)
        || toolchain.getBinaries().getBuildShared().getOrElse(Boolean.TRUE);
    this.stripDebug = test.getStripDebug().getOrElse(Boolean.FALSE)
        || toolchain.getTests().getStripDebug().getOrElse(Boolean.FALSE)
        || toolchain.getBinaries().getStripDebug().getOrElse(Boolean.FALSE);
    this.packageBuildOutputs = test.getPackageBuildOutputs().getOrElse(Boolean.FALSE)
        || toolchain.getTests().getPackageBuildOutputs().getOrElse(Boolean.FALSE)
        || toolchain.getBinaries().getPackageBuildOutputs().getOrElse(Boolean.FALSE);
    addPrivateLinkDependencies(toolchain.getTests().getPrivateLinkDependencies().get(), findPackages, project);
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
