/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import ch.tomaki.gradle.cmake.extension.CMakeExtension;
import ch.tomaki.gradle.cmake.helper.TestCMakeBinaryLibrary;
import ch.tomaki.gradle.cmake.helper.TestCMakeInterfaceLibrary;
import ch.tomaki.gradle.cmake.helper.TestCMakePackage;
import ch.tomaki.gradle.cmake.helper.TestCMakeTest;
import ch.tomaki.gradle.cmake.helper.TestCMakeToolchain;
import ch.tomaki.gradle.cmake.model.CMakeResolvedExecutable;
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

                final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(),
                                extension.getToolchains());
                final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
                                extension.getApplications(), extension.getTests());

                final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
                assertEquals(1, toolchains.length);

                assertEquals("Toolchain0", toolchains[0].getName());
                assertEquals(0, toolchains[0].getSystemPackages().size());
                assertEquals(2, toolchains[0].getLibraries().size());
                assertEquals(0, toolchains[0].getApplications().size());
                assertEquals(0, toolchains[0].getTests().size());
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
                                new HashSet<>(Arrays.asList("Toolchain1", "Toolchain0")));

                assertEquals(2, extension.getPackages().size());
                assertEquals(2, extension.getToolchains().size());
                assertEquals(3, extension.getLibraries().size());
                assertEquals(0, extension.getApplications().size());
                assertEquals(1, extension.getTests().size());

                final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(),
                                extension.getToolchains());
                final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
                                extension.getApplications(), extension.getTests());

                final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
                assertEquals(2, toolchains.length);

                assertEquals("Toolchain0", toolchains[0].getName());
                assertEquals(1, toolchains[0].getSystemPackages().size());
                assertEquals(3, toolchains[0].getLibraries().size());
                assertEquals(0, toolchains[0].getApplications().size());
                assertEquals(1, toolchains[0].getTests().size());
                {
                        final CMakeResolvedExecutable[] tests = toolchains[0].getTests()
                                        .toArray(new CMakeResolvedExecutable[toolchains[0].getTests().size()]);
                        assertEquals(1, tests[0].getPrivateSystemPackageDependencies().size());
                        assertEquals(2, tests[0].getPrivateProjectPackageDependencies().size());
                        assertEquals(1, tests[0].getPrivateLinkOptions().size());
                }

                assertEquals("Toolchain1", toolchains[1].getName());
                assertEquals(1, toolchains[1].getSystemPackages().size());
                assertEquals(2, toolchains[1].getLibraries().size());
                assertEquals(0, toolchains[1].getApplications().size());
                assertEquals(1, toolchains[1].getTests().size());
                {
                        final CMakeResolvedExecutable[] tests = toolchains[1].getTests()
                                        .toArray(new CMakeResolvedExecutable[toolchains[1].getTests().size()]);
                        assertEquals(1, tests[0].getPrivateSystemPackageDependencies().size());
                        assertEquals(1, tests[0].getPrivateProjectPackageDependencies().size());
                        assertEquals(1, tests[0].getPrivateLinkOptions().size());
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

                final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(),
                                extension.getToolchains());
                final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
                                extension.getApplications(), extension.getTests());

                final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
                assertEquals(1, toolchains.length);

                assertEquals("Toolchain0", toolchains[0].getName());
                assertEquals(0, toolchains[0].getSystemPackages().size());
                assertEquals(2, toolchains[0].getLibraries().size());
                assertEquals(0, toolchains[0].getApplications().size());
                assertEquals(1, toolchains[0].getTests().size());
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

                final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(),
                                extension.getToolchains());
                final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
                                extension.getApplications(), extension.getTests());

                final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
                assertEquals(1, toolchains.length);

                assertEquals("Toolchain0", toolchains[0].getName());
                assertEquals(1, toolchains[0].getSystemPackages().size());
                assertEquals(2, toolchains[0].getLibraries().size());
                assertEquals(0, toolchains[0].getApplications().size());
                assertEquals(1, toolchains[0].getTests().size());
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

                final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(),
                                extension.getToolchains());
                final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
                                extension.getApplications(), extension.getTests());

                final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
                assertEquals(1, toolchains.length);

                assertEquals("Toolchain0", toolchains[0].getName());
                assertEquals(1, toolchains[0].getSystemPackages().size());
                assertEquals(2, toolchains[0].getLibraries().size());
                assertEquals(0, toolchains[0].getApplications().size());
                assertEquals(1, toolchains[0].getTests().size());
        }
}
