/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import ch.tomaki.gradle.cmake.extension.CMakeExtension;
import ch.tomaki.gradle.cmake.helper.TestCMakeBinaryLibrary;
import ch.tomaki.gradle.cmake.helper.TestCMakeFindPackage;
import ch.tomaki.gradle.cmake.helper.TestCMakeInterfaceLibrary;
import ch.tomaki.gradle.cmake.helper.TestCMakeToolchain;
import ch.tomaki.gradle.cmake.model.CMakeResolvedBuild;
import ch.tomaki.gradle.cmake.model.CMakeResolver;

public class CMakeToolchainResolverTest {

        @Test
        void resolveNoLinkDependenciesTest() {
                final Project project = ProjectBuilder.builder().build();
                final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                                CMakeExtension.class);

                TestCMakeFindPackage.register("FindPackage0", extension);
                TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
                TestCMakeBinaryLibrary.register("BinaryLibrary0", extension, "Toolchain0");
                TestCMakeToolchain.register("Toolchain0", extension);

                final CMakeResolver cmakeResolver = new CMakeResolver(project, extension.getFindPackages(),
                                extension.getToolchains());
                final CMakeResolvedBuild resolvedBuild = cmakeResolver.process(extension.getLibraries(),
                                extension.getApplications(), extension.getTests());

                assertEquals(0, resolvedBuild.getResolvedPackages().size());
                assertEquals(2, resolvedBuild.getResolvedLibraries().size());
        }

        @Test
        void resolveLibraryLinkDependenciesTest() {
                final Project project = ProjectBuilder.builder().build();
                final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                                CMakeExtension.class);

                TestCMakeFindPackage.register("FindPackage0", extension);
                TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
                TestCMakeBinaryLibrary.register("BinaryLibrary0", extension, "Toolchain0");
                TestCMakeToolchain.registerWithLibraryDependencies("Toolchain0", extension,
                                "FindPackage0",
                                "%s::InterfaceLibrary0::interface".formatted(project.getName()),
                                "%s::BinaryLibrary0::shared".formatted(project.getName()));

                final CMakeResolver cmakeResolver = new CMakeResolver(project, extension.getFindPackages(),
                                extension.getToolchains());
                final CMakeResolvedBuild resolvedBuild = cmakeResolver.process(extension.getLibraries(),
                                extension.getApplications(), extension.getTests());

                assertEquals(1, resolvedBuild.getResolvedPackages().size());
                assertEquals(2, resolvedBuild.getResolvedLibraries().size());
        }

        @Test
        void resolveApplicationLinkDependenciesTest() {
                final Project project = ProjectBuilder.builder().build();
                final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                                CMakeExtension.class);

                TestCMakeFindPackage.register("FindPackage0", extension);
                TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
                TestCMakeBinaryLibrary.register("BinaryLibrary0", extension, "Toolchain0");
                TestCMakeToolchain.registerWithApplicationDependencies("Toolchain0", extension,
                                "FindPackage0",
                                "%s::InterfaceLibrary0::interface".formatted(project.getName()),
                                "%s::BinaryLibrary0::shared".formatted(project.getName()));

                final CMakeResolver cmakeResolver = new CMakeResolver(project, extension.getFindPackages(),
                                extension.getToolchains());
                final CMakeResolvedBuild resolvedBuild = cmakeResolver.process(extension.getLibraries(),
                                extension.getApplications(), extension.getTests());

                assertEquals(0, resolvedBuild.getResolvedPackages().size());
                assertEquals(2, resolvedBuild.getResolvedLibraries().size());
        }

        @Test
        void resolveTestLinkDependenciesTest() {
                final Project project = ProjectBuilder.builder().build();
                final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                                CMakeExtension.class);

                TestCMakeFindPackage.register("FindPackage0", extension);
                TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
                TestCMakeBinaryLibrary.register("BinaryLibrary0", extension, "Toolchain0");
                TestCMakeToolchain.registerWithTestDependencies("Toolchain0", extension,
                                "FindPackage0",
                                "%s::InterfaceLibrary0::interface".formatted(project.getName()),
                                "%s::BinaryLibrary0::shared".formatted(project.getName()));

                final CMakeResolver cmakeResolver = new CMakeResolver(project, extension.getFindPackages(),
                                extension.getToolchains());
                final CMakeResolvedBuild resolvedBuild = cmakeResolver.process(extension.getLibraries(),
                                extension.getApplications(), extension.getTests());

                assertEquals(0, resolvedBuild.getResolvedPackages().size());
                assertEquals(2, resolvedBuild.getResolvedLibraries().size());
        }
}
