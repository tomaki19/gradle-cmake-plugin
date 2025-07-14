/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import ch.tomaki.gradle.cmake.extension.CMakeExtension;
import ch.tomaki.gradle.cmake.helper.TestCMakeBinaryLibrary;
import ch.tomaki.gradle.cmake.helper.TestCMakeInterfaceLibrary;
import ch.tomaki.gradle.cmake.helper.TestCMakePackage;
import ch.tomaki.gradle.cmake.helper.TestCMakeTest;
import ch.tomaki.gradle.cmake.helper.TestCMakeToolchain;
import ch.tomaki.gradle.cmake.model.CMakeResolvedToolchain;
import ch.tomaki.gradle.cmake.model.CMakeResolver;

public class CMakeTestResolverTest {

        @Test
        void resolveNoToolchainTest() {
                final Project project = ProjectBuilder.builder().build();
                final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                                CMakeExtension.class);

                TestCMakePackage.register("Package0", extension);
                TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
                TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
                                new HashSet<>(Arrays.asList("Toolchain0")));
                TestCMakeToolchain.registerWithApplicationDependencies("Toolchain0", extension,
                                new HashSet<>(Arrays.asList("Package0", "-loption",
                                                "%s::InterfaceLibrary0::interface".formatted(project.getName()),
                                                "%s::BinaryLibrary0::shared".formatted(project.getName()))));
                TestCMakeTest.register("Test0", extension);

                assertEquals(1, extension.getPackages().size());
                assertEquals(1, extension.getToolchains().size());
                assertEquals(2, extension.getLibraries().size());
                assertEquals(0, extension.getApplications().size());
                assertEquals(1, extension.getTests().size());

                final CMakeResolver resolver = new CMakeResolver(extension.getToolchains(),
                                extension.getPackages(),
                                project);
                final Map<String, CMakeResolvedToolchain> toolchains = resolver.process(extension.getLibraries(),
                                extension.getApplications(), extension.getTests());

                assertEquals(1, toolchains.size());
                {
                        final CMakeResolvedToolchain toolchain = toolchains.get("Toolchain0");
                        assertEquals(0, toolchain.getSystemPackages().size());
                        assertEquals(2, toolchain.getLibraries().size());
                        assertEquals(0, toolchain.getApplications().size());
                        assertEquals(0, toolchain.getTests().size());
                        assertTrue(toolchain.isUsed());
                }
        }

        @Test
        void resolveMultipleToolchainTest() {
                final Project project = ProjectBuilder.builder().build();
                final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                                CMakeExtension.class);

                TestCMakePackage.register("Package0", extension);
                TestCMakePackage.register("Package1", extension);
                TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
                TestCMakeInterfaceLibrary.register("InterfaceLibrary1", extension);
                TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
                                new HashSet<>(Arrays.asList("Toolchain0")));
                TestCMakeToolchain.registerWithBinaryDependencies("Toolchain0", extension,
                                new HashSet<>(Arrays.asList("Package0", "-loption",
                                                "%s::InterfaceLibrary0::interface".formatted(project.getName()),
                                                "%s::BinaryLibrary0::shared".formatted(project.getName()))));
                TestCMakeToolchain.registerWithTestDependencies("Toolchain1", extension,
                                new HashSet<>(Arrays.asList("Package1", "-loption",
                                                "%s::InterfaceLibrary1::interface".formatted(project.getName()))));
                TestCMakeTest.register("Test0", extension,
                                new HashSet<>(Arrays.asList("Toolchain0", "Toolchain1")));

                assertEquals(2, extension.getPackages().size());
                assertEquals(2, extension.getToolchains().size());
                assertEquals(3, extension.getLibraries().size());
                assertEquals(0, extension.getApplications().size());
                assertEquals(1, extension.getTests().size());

                final CMakeResolver resolver = new CMakeResolver(extension.getToolchains(),
                                extension.getPackages(),
                                project);
                final Map<String, CMakeResolvedToolchain> toolchains = resolver.process(extension.getLibraries(),
                                extension.getApplications(), extension.getTests());

                assertEquals(2, toolchains.size());
                {
                        final CMakeResolvedToolchain toolchain = toolchains.get("Toolchain0");
                        assertEquals(1, toolchain.getSystemPackages().size());
                        assertEquals(3, toolchain.getLibraries().size());
                        assertEquals(0, toolchain.getApplications().size());
                        assertEquals(1, toolchain.getTests().size());
                        toolchain.getTests().forEach((application) -> {
                                assertEquals(1, application.getPrivateSystemPackageDependencies().size());
                                assertEquals(2, application.getPrivateProjectPackageDependencies().size());
                                assertEquals(1, application.getPrivateLinkOptions().size());
                        });
                        assertTrue(toolchain.isUsed());
                }
                {
                        final CMakeResolvedToolchain toolchain = toolchains.get("Toolchain1");
                        assertEquals(1, toolchain.getSystemPackages().size());
                        assertEquals(2, toolchain.getLibraries().size());
                        assertEquals(0, toolchain.getApplications().size());
                        assertEquals(1, toolchain.getTests().size());
                        toolchain.getTests().forEach((application) -> {
                                assertEquals(1, application.getPrivateSystemPackageDependencies().size());
                                assertEquals(1, application.getPrivateProjectPackageDependencies().size());
                                assertEquals(1, application.getPrivateLinkOptions().size());
                        });
                        assertTrue(toolchain.isUsed());
                }
        }

        @Test
        void resolveNoDependenciesTest() {
                final Project project = ProjectBuilder.builder().build();
                final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                                CMakeExtension.class);

                TestCMakePackage.register("Package0", extension);
                TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
                TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
                                new HashSet<>(Arrays.asList("Toolchain0")));
                TestCMakeToolchain.registerWithTestDependencies("Toolchain0", extension,
                                new HashSet<>());
                TestCMakeTest.register("Test0", extension,
                                new HashSet<>(Arrays.asList("Toolchain0")));

                assertEquals(1, extension.getPackages().size());
                assertEquals(1, extension.getToolchains().size());
                assertEquals(2, extension.getLibraries().size());
                assertEquals(0, extension.getApplications().size());
                assertEquals(1, extension.getTests().size());

                final CMakeResolver resolver = new CMakeResolver(extension.getToolchains(),
                                extension.getPackages(),
                                project);
                final Map<String, CMakeResolvedToolchain> toolchains = resolver.process(extension.getLibraries(),
                                extension.getApplications(), extension.getTests());

                assertEquals(1, toolchains.size());
                {
                        final CMakeResolvedToolchain toolchain = toolchains.get("Toolchain0");
                        assertEquals(0, toolchain.getSystemPackages().size());
                        assertEquals(2, toolchain.getLibraries().size());
                        assertEquals(0, toolchain.getApplications().size());
                        assertEquals(1, toolchain.getTests().size());
                        assertTrue(toolchain.isUsed());
                }
        }

        @Test
        void resolveToolchainDependencyTest() {
                final Project project = ProjectBuilder.builder().build();
                final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                                CMakeExtension.class);

                TestCMakePackage.register("Package0", extension);
                TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
                TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
                                new HashSet<>(Arrays.asList("Toolchain0")));
                TestCMakeToolchain.registerWithTestDependencies("Toolchain0", extension,
                                new HashSet<>(Arrays.asList("Package0", "-loption",
                                                "%s::InterfaceLibrary0::interface".formatted(project.getName()),
                                                "%s::BinaryLibrary0::shared".formatted(project.getName()))));
                TestCMakeTest.register("Test0", extension,
                                new HashSet<>(Arrays.asList("Toolchain0")));

                assertEquals(1, extension.getPackages().size());
                assertEquals(1, extension.getToolchains().size());
                assertEquals(2, extension.getLibraries().size());
                assertEquals(0, extension.getApplications().size());
                assertEquals(1, extension.getTests().size());

                final CMakeResolver resolver = new CMakeResolver(extension.getToolchains(),
                                extension.getPackages(),
                                project);
                final Map<String, CMakeResolvedToolchain> toolchains = resolver.process(extension.getLibraries(),
                                extension.getApplications(), extension.getTests());

                assertEquals(1, toolchains.size());
                {
                        final CMakeResolvedToolchain toolchain = toolchains.get("Toolchain0");
                        assertEquals(1, toolchain.getSystemPackages().size());
                        assertEquals(2, toolchain.getLibraries().size());
                        assertEquals(0, toolchain.getApplications().size());
                        assertEquals(1, toolchain.getTests().size());
                        assertTrue(toolchain.isUsed());
                }
        }

        @Test
        void resolveLinkDependenciesTest() {
                final Project project = ProjectBuilder.builder().build();
                final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                                CMakeExtension.class);

                TestCMakePackage.register("Package0", extension);
                TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
                TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
                                new HashSet<>(Arrays.asList("Toolchain0")));
                TestCMakeToolchain.register("Toolchain0", extension);
                TestCMakeTest.registerWithPrivateDependencies("Test0", extension,
                                new HashSet<>(Arrays.asList("Toolchain0")),
                                new HashSet<>(Arrays.asList("Package0", "-loption",
                                                "%s::InterfaceLibrary0::interface".formatted(project.getName()),
                                                "%s::BinaryLibrary0::shared".formatted(project.getName()))));

                assertEquals(1, extension.getPackages().size());
                assertEquals(1, extension.getToolchains().size());
                assertEquals(2, extension.getLibraries().size());
                assertEquals(0, extension.getApplications().size());
                assertEquals(1, extension.getTests().size());

                final CMakeResolver resolver = new CMakeResolver(extension.getToolchains(),
                                extension.getPackages(),
                                project);
                final Map<String, CMakeResolvedToolchain> toolchains = resolver.process(extension.getLibraries(),
                                extension.getApplications(), extension.getTests());

                assertEquals(1, toolchains.size());
                {
                        final CMakeResolvedToolchain toolchain = toolchains.get("Toolchain0");
                        assertEquals(1, toolchain.getSystemPackages().size());
                        assertEquals(2, toolchain.getLibraries().size());
                        assertEquals(0, toolchain.getApplications().size());
                        assertEquals(1, toolchain.getTests().size());
                        assertTrue(toolchain.isUsed());
                }
        }
}
