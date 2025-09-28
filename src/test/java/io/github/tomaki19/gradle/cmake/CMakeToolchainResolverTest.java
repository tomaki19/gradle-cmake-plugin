/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeBinaryLibrary;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeInterfaceLibrary;
import io.github.tomaki19.gradle.cmake.helper.TestCMakePackage;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeResolver;

public class CMakeToolchainResolverTest {

        @Test
        void resolveNoLinkDependenciesTest() {
                final Project project = ProjectBuilder.builder().build();
                final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                                CMakeExtension.class);

                TestCMakePackage.register("Package0", extension);
                TestCMakeBinaryLibrary.register("BinaryLibrary0", extension);
                TestCMakeToolchain.register("Toolchain0", extension);

                assertEquals(1, extension.getPackages().size());
                assertEquals(1, extension.getToolchains().size());
                assertEquals(1, extension.getLibraries().size());
                assertEquals(0, extension.getApplications().size());
                assertEquals(0, extension.getTests().size());

                final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(),
                                extension.getToolchains());
                final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
                                extension.getApplications(), extension.getTests());

                final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
                assertEquals(0, toolchains.length);
        }

        @Test
        void resolveInterfaceLinkDependenciesTest() {
                final Project project = ProjectBuilder.builder().build();
                final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                                CMakeExtension.class);

                TestCMakePackage.register("Package0", extension);
                TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
                TestCMakeBinaryLibrary.register("BinaryLibrary0", extension);
                TestCMakeToolchain.register("Toolchain0", extension);

                assertEquals(1, extension.getPackages().size());
                assertEquals(1, extension.getToolchains().size());
                assertEquals(2, extension.getLibraries().size());
                assertEquals(0, extension.getApplications().size());
                assertEquals(0, extension.getTests().size());

                final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(),
                                extension.getToolchains());
                final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
                                extension.getApplications(), extension.getTests());

                final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
                assertEquals(1, toolchains.length);

                assertEquals("Toolchain0", toolchains[0].getName());
                assertEquals(1, resolver.getAvailableSystemPackages().size());
                assertEquals(1, toolchains[0].getLibraries().size());
                assertEquals(0, toolchains[0].getApplications().size());
                assertEquals(0, toolchains[0].getTests().size());
        }

        @Test
        void resolveLibraryLinkDependenciesTest() {
                final Project project = ProjectBuilder.builder().build();
                final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                                CMakeExtension.class);

                TestCMakePackage.register("Package0", extension);
                TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
                TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
                                new HashSet<>(Arrays.asList("Toolchain0")));
                TestCMakeToolchain.registerWithLibraryDependencies("Toolchain0", extension,
                                new HashSet<>(Arrays.asList("Package0::target",
                                                "%s::InterfaceLibrary0::interface".formatted(project.getName()),
                                                "%s::BinaryLibrary0::shared".formatted(project.getName()))));

                assertEquals(1, extension.getPackages().size());
                assertEquals(1, extension.getToolchains().size());
                assertEquals(2, extension.getLibraries().size());
                assertEquals(0, extension.getApplications().size());
                assertEquals(0, extension.getTests().size());

                final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(),
                                extension.getToolchains());
                final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
                                extension.getApplications(), extension.getTests());

                final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
                assertEquals(1, toolchains.length);

                assertEquals("Toolchain0", toolchains[0].getName());
                assertEquals(1, resolver.getAvailableSystemPackages().size());
                assertEquals(2, toolchains[0].getLibraries().size());
                assertEquals(0, toolchains[0].getApplications().size());
                assertEquals(0, toolchains[0].getTests().size());
        }

        @Test
        void resolveApplicationLinkDependenciesTest() {
                final Project project = ProjectBuilder.builder().build();
                final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                                CMakeExtension.class);

                TestCMakePackage.register("Package0", extension);
                TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
                TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
                                new HashSet<>(Arrays.asList("Toolchain0")));
                TestCMakeToolchain.registerWithApplicationDependencies("Toolchain0", extension,
                                new HashSet<>(Arrays.asList("Package0::target",
                                                "%s::InterfaceLibrary0::interface".formatted(project.getName()),
                                                "%s::BinaryLibrary0::shared".formatted(project.getName()))));

                assertEquals(1, extension.getPackages().size());
                assertEquals(1, extension.getToolchains().size());
                assertEquals(2, extension.getLibraries().size());
                assertEquals(0, extension.getApplications().size());
                assertEquals(0, extension.getTests().size());

                final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(),
                                extension.getToolchains());
                final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
                                extension.getApplications(), extension.getTests());

                final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
                assertEquals(1, toolchains.length);

                assertEquals("Toolchain0", toolchains[0].getName());
                assertEquals(1, resolver.getAvailableSystemPackages().size());
                assertEquals(2, toolchains[0].getLibraries().size());
                assertEquals(0, toolchains[0].getApplications().size());
                assertEquals(0, toolchains[0].getTests().size());
        }

        @Test
        void resolveTestLinkDependenciesTest() {
                final Project project = ProjectBuilder.builder().build();
                final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                                CMakeExtension.class);

                TestCMakePackage.register("Package0", extension);
                TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
                TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
                                new HashSet<>(Arrays.asList("Toolchain0")));
                TestCMakeToolchain.registerWithTestDependencies("Toolchain0", extension,
                                new HashSet<>(Arrays.asList("Package0::target",
                                                "%s::InterfaceLibrary0::interface".formatted(project.getName()),
                                                "%s::BinaryLibrary0::shared".formatted(project.getName()))));

                assertEquals(1, extension.getPackages().size());
                assertEquals(1, extension.getToolchains().size());
                assertEquals(2, extension.getLibraries().size());
                assertEquals(0, extension.getApplications().size());
                assertEquals(0, extension.getTests().size());

                final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(),
                                extension.getToolchains());
                final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
                                extension.getApplications(), extension.getTests());

                final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
                assertEquals(1, toolchains.length);

                assertEquals("Toolchain0", toolchains[0].getName());
                assertEquals(1, resolver.getAvailableSystemPackages().size());
                assertEquals(2, toolchains[0].getLibraries().size());
                assertEquals(0, toolchains[0].getApplications().size());
                assertEquals(0, toolchains[0].getTests().size());
        }
}
