/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.Map;

import org.gradle.api.Project;

import ch.tomaki.gradle.cmake.extension.api.CMakeBinary;
import ch.tomaki.gradle.cmake.extension.api.CMakeFindPackage;
import ch.tomaki.gradle.cmake.extension.api.CMakeTest;
import ch.tomaki.gradle.cmake.extension.api.CMakeToolchain;

public final class CMakeResolvedTest extends CMakeResolvedBinary {

  CMakeResolvedTest(final CMakeTest test, final CMakeToolchain toolchain,
      final Map<String, CMakeFindPackage> findPackages, final Project project) {
    super(test, toolchain, findPackages, project);
    addPrivateLinkDependencies(toolchain.getTests().getPrivateLinkDependencies().get(), findPackages,
        project);
  }

  @Override
  protected boolean initBuildStatic(CMakeBinary binary, CMakeToolchain toolchain) {
    return binary.getBuildStatic().getOrElse(Boolean.FALSE)
        || toolchain.getTests().getBuildStatic().getOrElse(Boolean.FALSE)
        || toolchain.getBinaries().getBuildStatic().getOrElse(Boolean.FALSE);
  }

  @Override
  protected boolean initBuildShared(CMakeBinary binary, CMakeToolchain toolchain) {
    return binary.getBuildShared().getOrElse(Boolean.FALSE)
        || toolchain.getTests().getBuildShared().getOrElse(Boolean.FALSE)
        || toolchain.getBinaries().getBuildShared().getOrElse(Boolean.TRUE);
  }

  @Override
  protected boolean initStripDebug(CMakeBinary binary, CMakeToolchain toolchain) {
    return binary.getStripDebug().getOrElse(Boolean.FALSE)
        || toolchain.getTests().getStripDebug().getOrElse(Boolean.FALSE)
        || toolchain.getBinaries().getStripDebug().getOrElse(Boolean.FALSE);
  }

  @Override
  protected boolean initPackageBuildOutputs(CMakeBinary binary, CMakeToolchain toolchain) {
    return binary.getPackageBuildOutputs().getOrElse(Boolean.FALSE)
        || toolchain.getTests().getPackageBuildOutputs().getOrElse(Boolean.FALSE)
        || toolchain.getBinaries().getPackageBuildOutputs().getOrElse(Boolean.FALSE);
  }

}
