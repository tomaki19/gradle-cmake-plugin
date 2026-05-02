/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibraryLinkSpec;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeApplication;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeBinaryLibrary;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeInterfaceLibrary;
import io.github.tomaki19.gradle.cmake.helper.TestCMakePackage;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeToolchain;

public class CMakeApplicationResolverTest {

  @Test
        void resolveNoToolchainTest() throws Exception {
                final Project project = ProjectBuilder.builder().build();
                final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                                CMakeExtension.class, project.getTasks());

                TestCMakePackage.register("Package0", extension);
                TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
                TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
                                Arrays.asList("Toolchain0"), CMakeBuildVariant.SHARED);
                TestCMakeToolchain.registerWithApplicationDependencies("Toolchain0", extension,
                                Arrays.asList(Map.of("names", List.of("-loption"), CMakeLibraryLinkSpec.VISIBILITY, "PRIVATE")),
                                Arrays.asList(
                                                Map.of("components", List.of("target"),
                                                                CMakeLibraryLinkSpec.PROJECT, "Package0",
                                                                CMakeLibraryLinkSpec.VISIBILITY, "PRIVATE"),
                                                Map.of("components", List.of("InterfaceLibrary0"),
                                                                CMakeLibraryLinkSpec.LINK_VARIANT, "INTERFACE",
                                                                CMakeLibraryLinkSpec.VISIBILITY, "PRIVATE"),
                                                Map.of("components", List.of("BinaryLibrary0"),
                                                                CMakeLibraryLinkSpec.PROJECT, project.getName(),
                                                                CMakeLibraryLinkSpec.LINK_VARIANT, "SHARED",
                                                                CMakeLibraryLinkSpec.VISIBILITY, "PRIVATE")));
                TestCMakeApplication.register("Application0", extension);

                assertEquals(1, extension.getPackages().size());
                assertEquals(1, extension.getToolchains().size());
                assertEquals(2, extension.getLibraries().size());
                assertEquals(1, extension.getApplications().size());
                assertEquals(0, extension.getTests().size());

                final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(),
                                extension.getToolchains());
                final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
                                extension.getApplications(), extension.getTests());

                final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
                assertEquals(1, toolchains.length);

                assertEquals("Toolchain0", toolchains[0].getName());
                assertEquals(1, toolchains[0].getInterfaceLibraries().size());
                assertEquals(0, toolchains[0].getStaticLibraries().size());
                assertEquals(1, toolchains[0].getSharedLibraries().size());
                assertEquals(1, toolchains[0].getApplications().size());
                assertEquals(0, toolchains[0].getTests().size());
        }

  @Test
        void resolveMultipleToolchainTest() throws Exception {
                final Project project = ProjectBuilder.builder().build();
                final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                                CMakeExtension.class, project.getTasks());

                TestCMakePackage.register("Package0", extension);
                TestCMakePackage.register("Package1", extension);
                TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
                TestCMakeInterfaceLibrary.register("InterfaceLibrary1", extension);
                TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
                                Arrays.asList("Toolchain0"), CMakeBuildVariant.SHARED);
                TestCMakeToolchain.registerWithApplicationDependencies("Toolchain0", extension,
                                Arrays.asList(Map.of("names", List.of("-loption"), CMakeLibraryLinkSpec.VISIBILITY, "PUBLIC")),
                                Arrays.asList(
                                                Map.of("components", List.of("target"),
                                                                CMakeLibraryLinkSpec.PROJECT, "Package0",
                                                                CMakeLibraryLinkSpec.VISIBILITY, "PUBLIC"),
                                                Map.of("components", List.of("InterfaceLibrary0"),
                                                                CMakeLibraryLinkSpec.LINK_VARIANT, "INTERFACE",
                                                                CMakeLibraryLinkSpec.VISIBILITY, "PUBLIC"),
                                                Map.of("components", List.of("BinaryLibrary0"),
                                                                CMakeLibraryLinkSpec.PROJECT, project.getName(),
                                                                CMakeLibraryLinkSpec.LINK_VARIANT, "SHARED",
                                                                CMakeLibraryLinkSpec.VISIBILITY, "PUBLIC")));
                TestCMakeToolchain.registerWithApplicationDependencies("Toolchain1", extension,
                                Arrays.asList(Map.of("names", List.of("-loption"), CMakeLibraryLinkSpec.VISIBILITY, "PRIVATE")),
                                Arrays.asList(
                                                Map.of("components", List.of("target"),
                                                                CMakeLibraryLinkSpec.PROJECT, "Package1",
                                                                CMakeLibraryLinkSpec.VISIBILITY, "PRIVATE"),
                                                Map.of("components", List.of("InterfaceLibrary1"),
                                                                CMakeLibraryLinkSpec.PROJECT, project.getName(),
                                                                CMakeLibraryLinkSpec.LINK_VARIANT, "INTERFACE",
                                                                CMakeLibraryLinkSpec.VISIBILITY, "PRIVATE")));
                TestCMakeApplication.register("Application0", extension,
                                Arrays.asList("Toolchain0", "Toolchain1"));

                assertEquals(2, extension.getPackages().size());
                assertEquals(2, extension.getToolchains().size());
                assertEquals(3, extension.getLibraries().size());
                assertEquals(1, extension.getApplications().size());
                assertEquals(0, extension.getTests().size());

                final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(),
                                extension.getToolchains());
                final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
                                extension.getApplications(), extension.getTests());

                final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
                assertEquals(2, toolchains.length);

                assertEquals("Toolchain0", toolchains[0].getName());
                assertEquals(2, toolchains[0].getInterfaceLibraries().size());
                assertEquals(0, toolchains[0].getStaticLibraries().size());
                assertEquals(1, toolchains[0].getSharedLibraries().size());
                assertEquals(1, toolchains[0].getApplications().size());
                {
                        final CMakeResolvedApplication[] applications = toolchains[0].getApplications()
                                        .toArray(new CMakeResolvedApplication[toolchains[0].getApplications().size()]);
                        assertEquals(1, applications[0].getPublicPackageDependencies().size());
                        assertEquals(2, applications[0].getPublicProjectDependencies().size());
                        assertEquals(1, applications[0].getPublicLinkOptions().size());
                }
                assertEquals(0, toolchains[0].getTests().size());

                assertEquals("Toolchain1", toolchains[1].getName());
                assertEquals(2, toolchains[1].getInterfaceLibraries().size());
                assertEquals(0, toolchains[1].getStaticLibraries().size());
                assertEquals(0, toolchains[1].getSharedLibraries().size());
                assertEquals(1, toolchains[1].getApplications().size());
                {
                        final CMakeResolvedApplication[] applications = toolchains[1].getApplications()
                                        .toArray(new CMakeResolvedApplication[toolchains[1].getApplications().size()]);
                        assertEquals(1, applications[0].getPrivatePackageDependencies().size());
                        assertEquals(1, applications[0].getPrivateProjectDependencies().size());
                        assertEquals(1, applications[0].getPrivateLinkOptions().size());
                }
                assertEquals(0, toolchains[1].getTests().size());
        }

  @Test
        void resolveNoDependenciesTest() throws Exception {
                final Project project = ProjectBuilder.builder().build();
                final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                                CMakeExtension.class, project.getTasks());

                TestCMakePackage.register("Package0", extension);
                TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
                                Arrays.asList("Toolchain0"), CMakeBuildVariant.SHARED);
                TestCMakeToolchain.registerWithApplicationDependencies("Toolchain0", extension,
                                Arrays.asList(), Arrays.asList());
                TestCMakeToolchain.registerWithTestDependencies("Toolchain1", extension,
                                Arrays.asList(Map.of("names", List.of("-loption"), CMakeLibraryLinkSpec.VISIBILITY, "PRIVATE")),
                                Arrays.asList(Map.of("components", List.of("target"), CMakeLibraryLinkSpec.PROJECT,
                                                "Package0",
                                                CMakeLibraryLinkSpec.VISIBILITY, "PRIVATE")));
                TestCMakeApplication.register("Application0", extension,
                                Arrays.asList("Toolchain0"));

                assertEquals(1, extension.getPackages().size());
                assertEquals(2, extension.getToolchains().size());
                assertEquals(1, extension.getLibraries().size());
                assertEquals(1, extension.getApplications().size());
                assertEquals(0, extension.getTests().size());

                final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(),
                                extension.getToolchains());
                final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
                                extension.getApplications(), extension.getTests());

                final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
                assertEquals(1, toolchains.length);

                assertEquals("Toolchain0", toolchains[0].getName());
                assertEquals(0, toolchains[0].getInterfaceLibraries().size());
                assertEquals(0, toolchains[0].getStaticLibraries().size());
                assertEquals(1, toolchains[0].getSharedLibraries().size());
                assertEquals(1, toolchains[0].getApplications().size());
                {
                        final CMakeResolvedApplication[] applications = toolchains[0].getApplications()
                                        .toArray(new CMakeResolvedApplication[toolchains[0].getApplications().size()]);
                        assertEquals(0, applications[0].getPrivatePackageDependencies().size());
                        assertEquals(0, applications[0].getPrivateProjectDependencies().size());
                        assertEquals(0, applications[0].getPrivateLinkOptions().size());
                }
                assertEquals(0, toolchains[0].getTests().size());
        }

  @Test
        void resolveToolchainDependencyTest() throws Exception {
                final Project project = ProjectBuilder.builder().build();
                final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                                CMakeExtension.class, project.getTasks());

                TestCMakePackage.register("Package0", extension);
                TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
                TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
                                Arrays.asList("Toolchain0"), CMakeBuildVariant.SHARED);
                TestCMakeToolchain.registerWithApplicationDependencies("Toolchain0", extension,
                                Arrays.asList(Map.of("names", List.of("-loption"), CMakeLibraryLinkSpec.VISIBILITY, "PRIVATE")),
                                Arrays.asList(
                                                Map.of("components", List.of("target"),
                                                                CMakeLibraryLinkSpec.PROJECT, "Package0",
                                                                CMakeLibraryLinkSpec.VISIBILITY, "PRIVATE"),
                                                Map.of("components", List.of("InterfaceLibrary0"),
                                                                CMakeLibraryLinkSpec.LINK_VARIANT, "INTERFACE",
                                                                CMakeLibraryLinkSpec.VISIBILITY, "PRIVATE"),
                                                Map.of("components", List.of("BinaryLibrary0"),
                                                                CMakeLibraryLinkSpec.PROJECT, project.getName(),
                                                                CMakeLibraryLinkSpec.LINK_VARIANT, "SHARED",
                                                                CMakeLibraryLinkSpec.VISIBILITY, "PRIVATE")));
                TestCMakeApplication.register("Application0", extension,
                                Arrays.asList("Toolchain0"));

                assertEquals(1, extension.getPackages().size());
                assertEquals(1, extension.getToolchains().size());
                assertEquals(2, extension.getLibraries().size());
                assertEquals(1, extension.getApplications().size());
                assertEquals(0, extension.getTests().size());

                final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(),
                                extension.getToolchains());
                final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
                                extension.getApplications(), extension.getTests());

                final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
                assertEquals(1, toolchains.length);

                assertEquals("Toolchain0", toolchains[0].getName());
                assertEquals(1, toolchains[0].getInterfaceLibraries().size());
                assertEquals(0, toolchains[0].getStaticLibraries().size());
                assertEquals(1, toolchains[0].getSharedLibraries().size());
                assertEquals(1, toolchains[0].getApplications().size());
                {
                        final CMakeResolvedApplication[] applications = toolchains[0].getApplications()
                                        .toArray(new CMakeResolvedApplication[toolchains[0].getApplications().size()]);
                        assertEquals(1, applications[0].getPrivatePackageDependencies().size());
                        assertEquals(2, applications[0].getPrivateProjectDependencies().size());
                        assertEquals(1, applications[0].getPrivateLinkOptions().size());
                }
                assertEquals(0, toolchains[0].getTests().size());
        }

  @Test
        void resolveLinkDependenciesTest() throws Exception {
                final Project project = ProjectBuilder.builder().build();
                final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                                CMakeExtension.class, project.getTasks());

                TestCMakePackage.register("Package0", extension);
                TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
                TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
                                Arrays.asList("Toolchain0"), CMakeBuildVariant.SHARED);
                TestCMakeToolchain.register("Toolchain0", extension);
                TestCMakeApplication.registerWithDependencies("Application0", extension,
                                Arrays.asList("Toolchain0"),
                                Arrays.asList(Map.of("names", List.of("-loption"), CMakeLibraryLinkSpec.VISIBILITY, "PRIVATE")),
                                Arrays.asList(
                                                Map.of("components", List.of("target"),
                                                                CMakeLibraryLinkSpec.PROJECT, "Package0",
                                                                CMakeLibraryLinkSpec.VISIBILITY, "PRIVATE"),
                                                Map.of("components", List.of("InterfaceLibrary0"),
                                                                CMakeLibraryLinkSpec.LINK_VARIANT, "INTERFACE",
                                                                CMakeLibraryLinkSpec.VISIBILITY, "PRIVATE"),
                                                Map.of("components", List.of("BinaryLibrary0"),
                                                                CMakeLibraryLinkSpec.PROJECT, project.getName(),
                                                                CMakeLibraryLinkSpec.LINK_VARIANT, "SHARED",
                                                                CMakeLibraryLinkSpec.VISIBILITY, "PRIVATE")));

                assertEquals(1, extension.getPackages().size());
                assertEquals(1, extension.getToolchains().size());
                assertEquals(2, extension.getLibraries().size());
                assertEquals(1, extension.getApplications().size());
                assertEquals(0, extension.getTests().size());

                final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(),
                                extension.getToolchains());
                final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
                                extension.getApplications(), extension.getTests());

                final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
                assertEquals(1, toolchains.length);

                assertEquals("Toolchain0", toolchains[0].getName());
                assertEquals(1, toolchains[0].getInterfaceLibraries().size());
                assertEquals(0, toolchains[0].getStaticLibraries().size());
                assertEquals(1, toolchains[0].getSharedLibraries().size());
                assertEquals(1, toolchains[0].getApplications().size());
                {
                        final CMakeResolvedApplication[] applications = toolchains[0].getApplications()
                                        .toArray(new CMakeResolvedApplication[toolchains[0].getApplications().size()]);
                        assertEquals(1, applications[0].getPrivatePackageDependencies().size());
                        assertEquals(2, applications[0].getPrivateProjectDependencies().size());
                        assertEquals(1, applications[0].getPrivateLinkOptions().size());
                }
                assertEquals(0, toolchains[0].getTests().size());
        }
}
