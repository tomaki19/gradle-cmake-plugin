/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.Map;

import org.gradle.api.Project;

import ch.tomaki.gradle.cmake.extension.api.CMakeApplication;
import ch.tomaki.gradle.cmake.extension.api.CMakeBinary;
import ch.tomaki.gradle.cmake.extension.api.CMakeFindPackage;
import ch.tomaki.gradle.cmake.extension.api.CMakeToolchain;

public final class CMakeResolvedApplication extends CMakeResolvedBinary {

  CMakeResolvedApplication(final CMakeApplication application, final CMakeToolchain toolchain,
      final Map<String, CMakeFindPackage> findPackages, final Project project) {
    super(application, toolchain, findPackages, project);
    addPrivateLinkDependencies(toolchain.getApplications().getPrivateLinkDependencies().get(), findPackages,
        project);
  }

  @Override
  protected boolean initBuildStatic(CMakeBinary binary, CMakeToolchain toolchain) {
    return binary.getBuildStatic().getOrElse(Boolean.FALSE)
        || toolchain.getApplications().getBuildStatic().getOrElse(Boolean.FALSE)
        || toolchain.getBinaries().getBuildStatic().getOrElse(Boolean.FALSE);
  }

  @Override
  protected boolean initBuildShared(CMakeBinary binary, CMakeToolchain toolchain) {
    return binary.getBuildShared().getOrElse(Boolean.FALSE)
        || toolchain.getApplications().getBuildShared().getOrElse(Boolean.FALSE)
        || toolchain.getBinaries().getBuildShared().getOrElse(Boolean.TRUE);
  }

  @Override
  protected boolean initStripDebug(CMakeBinary binary, CMakeToolchain toolchain) {
    return binary.getStripDebug().getOrElse(Boolean.FALSE)
        || toolchain.getApplications().getStripDebug().getOrElse(Boolean.FALSE)
        || toolchain.getBinaries().getStripDebug().getOrElse(Boolean.FALSE);
  }

  @Override
  protected boolean initPackageBuildOutputs(CMakeBinary binary, CMakeToolchain toolchain) {
    return binary.getPackageBuildOutputs().getOrElse(Boolean.FALSE)
        || toolchain.getApplications().getPackageBuildOutputs().getOrElse(Boolean.FALSE)
        || toolchain.getBinaries().getPackageBuildOutputs().getOrElse(Boolean.FALSE);
  }

}
