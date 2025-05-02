/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Project;
import org.gradle.internal.os.OperatingSystem;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import ch.tomaki.gradle.cmake.extension.CMakeExtension;
import ch.tomaki.gradle.cmake.extension.CMakeToolchain;
import ch.tomaki.gradle.cmake.model.CMakeResolvedBuild;
import ch.tomaki.gradle.cmake.model.CMakeResolver;

public class CMakeResolverTest {

  @Test
  void validateTests() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class);

    final String findPackageName = "FindPackage0";
    extension.getFindPackages()
        .register(findPackageName);

    final NamedDomainObjectProvider<CMakeToolchain> provider = extension.getToolchains()
        .register("t-win");
    final OperatingSystem operatingSystem = OperatingSystem.WINDOWS;
    provider.configure((toolchain) -> {
      toolchain.getOperatingSystem().set(operatingSystem);
    });

    final CMakeResolver cmakeResolver = new CMakeResolver(project, extension.getFindPackages(),
        extension.getToolchains());
    final CMakeResolvedBuild resolvedBuild = cmakeResolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    assertTrue(resolvedBuild.getResolvedFindPackages().isEmpty());
  }

}
