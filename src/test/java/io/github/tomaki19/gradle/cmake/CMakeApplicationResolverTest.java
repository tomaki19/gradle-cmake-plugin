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
import io.github.tomaki19.gradle.cmake.helper.TestCMakeApplication;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeBinaryLibrary;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeInterfaceLibrary;
import io.github.tomaki19.gradle.cmake.helper.TestCMakePackage;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedExecutable;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeResolver;

public class CMakeApplicationResolverTest {

    @Test
    void resolveNoToolchainTest() {
        final Project project = ProjectBuilder.builder().build();
        final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                CMakeExtension.class, project.getTasks());

        TestCMakePackage.register("Package0", extension);
        TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
        TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
                new HashSet<>(Arrays.asList("Toolchain0")));
        TestCMakeToolchain.registerWithApplicationDependencies("Toolchain0", extension,
                new HashSet<>(Arrays.asList("Package0", "-loption",
                        "%s::InterfaceLibrary0::interface".formatted(project.getName()),
                        "%s::BinaryLibrary0::shared".formatted(project.getName()))));
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
        assertEquals(1, resolver.getAvailableSystemPackages().size());
        assertEquals(2, toolchains[0].getLibraries().size());
        assertEquals(0, toolchains[0].getApplications().size());
        assertEquals(0, toolchains[0].getTests().size());
    }

    @Test
    void resolveMultipleToolchainTest() {
        final Project project = ProjectBuilder.builder().build();
        final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                CMakeExtension.class, project.getTasks());

        TestCMakePackage.register("Package0", extension);
        TestCMakePackage.register("Package1", extension);
        TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
        TestCMakeInterfaceLibrary.register("InterfaceLibrary1", extension);
        TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
                new HashSet<>(Arrays.asList("Toolchain0")));
        TestCMakeToolchain.registerWithApplicationDependencies("Toolchain0", extension,
                new HashSet<>(Arrays.asList("Package0::target", "-loption",
                        "%s::InterfaceLibrary0::interface".formatted(project.getName()),
                        "%s::BinaryLibrary0::shared".formatted(project.getName()))));
        TestCMakeToolchain.registerWithApplicationDependencies("Toolchain1", extension,
                new HashSet<>(Arrays.asList("Package1::target", "-loption",
                        "%s::InterfaceLibrary1::interface".formatted(project.getName()))));
        TestCMakeApplication.register("Application0", extension,
                new HashSet<>(Arrays.asList("Toolchain0", "Toolchain1")));

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
        assertEquals(2, resolver.getAvailableSystemPackages().size());
        assertEquals(3, toolchains[0].getLibraries().size());
        assertEquals(1, toolchains[0].getApplications().size());
        {
            final CMakeResolvedExecutable[] applications = toolchains[0].getApplications()
                    .toArray(new CMakeResolvedExecutable[toolchains[0].getApplications().size()]);
            assertEquals(1, applications[0].getPrivateSystemPackageDependencies().size());
            assertEquals(2, applications[0].getPrivateProjectPackageDependencies().size());
            assertEquals(1, applications[0].getPrivateLinkOptions().size());
        }
        assertEquals(0, toolchains[0].getTests().size());

        assertEquals("Toolchain1", toolchains[1].getName());
        assertEquals(2, resolver.getAvailableSystemPackages().size());
        assertEquals(2, toolchains[1].getLibraries().size());
        assertEquals(1, toolchains[1].getApplications().size());
        {
            final CMakeResolvedExecutable[] applications = toolchains[1].getApplications()
                    .toArray(new CMakeResolvedExecutable[toolchains[1].getApplications().size()]);
            assertEquals(1, applications[0].getPrivateSystemPackageDependencies().size());
            assertEquals(1, applications[0].getPrivateProjectPackageDependencies().size());
            assertEquals(1, applications[0].getPrivateLinkOptions().size());
        }
        assertEquals(0, toolchains[1].getTests().size());
    }

    @Test
    void resolveNoDependenciesTest() {
        final Project project = ProjectBuilder.builder().build();
        final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                CMakeExtension.class, project.getTasks());

        TestCMakePackage.register("Package0", extension);
        TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
        TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
                new HashSet<>(Arrays.asList("Toolchain0")));
        TestCMakeToolchain.registerWithApplicationDependencies("Toolchain0", extension,
                new HashSet<>());
        TestCMakeApplication.register("Application0", extension,
                new HashSet<>(Arrays.asList("Toolchain0")));

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
        assertEquals(1, resolver.getAvailableSystemPackages().size());
        assertEquals(2, toolchains[0].getLibraries().size());
        assertEquals(1, toolchains[0].getApplications().size());
        {
            final CMakeResolvedExecutable[] applications = toolchains[0].getApplications()
                    .toArray(new CMakeResolvedExecutable[toolchains[0].getApplications().size()]);
            assertEquals(0, applications[0].getPrivateSystemPackageDependencies().size());
            assertEquals(0, applications[0].getPrivateProjectPackageDependencies().size());
            assertEquals(0, applications[0].getPrivateLinkOptions().size());
        }
        assertEquals(0, toolchains[0].getTests().size());
    }

    @Test
    void resolveToolchainDependencyTest() {
        final Project project = ProjectBuilder.builder().build();
        final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                CMakeExtension.class, project.getTasks());

        TestCMakePackage.register("Package0", extension);
        TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
        TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
                new HashSet<>(Arrays.asList("Toolchain0")));
        TestCMakeToolchain.registerWithApplicationDependencies("Toolchain0", extension,
                new HashSet<>(Arrays.asList("Package0::target", "-loption",
                        "%s::InterfaceLibrary0::interface".formatted(project.getName()),
                        "%s::BinaryLibrary0::shared".formatted(project.getName()))));
        TestCMakeApplication.register("Application0", extension,
                new HashSet<>(Arrays.asList("Toolchain0")));

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
        assertEquals(1, resolver.getAvailableSystemPackages().size());
        assertEquals(2, toolchains[0].getLibraries().size());
        assertEquals(1, toolchains[0].getApplications().size());
        {
            final CMakeResolvedExecutable[] applications = toolchains[0].getApplications()
                    .toArray(new CMakeResolvedExecutable[toolchains[0].getApplications().size()]);
            assertEquals(1, applications[0].getPrivateSystemPackageDependencies().size());
            assertEquals(2, applications[0].getPrivateProjectPackageDependencies().size());
            assertEquals(1, applications[0].getPrivateLinkOptions().size());
        }
        assertEquals(0, toolchains[0].getTests().size());
    }

    @Test
    void resolveLinkDependenciesTest() {
        final Project project = ProjectBuilder.builder().build();
        final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                CMakeExtension.class, project.getTasks());

        TestCMakePackage.register("Package0", extension);
        TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
        TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
                new HashSet<>(Arrays.asList("Toolchain0")));
        TestCMakeToolchain.register("Toolchain0", extension);
        TestCMakeApplication.registerWithPrivateDependencies("Application0", extension,
                new HashSet<>(Arrays.asList("Toolchain0")),
                new HashSet<>(Arrays.asList("Package0::target", "-loption",
                        "%s::InterfaceLibrary0::interface".formatted(project.getName()),
                        "%s::BinaryLibrary0::shared".formatted(project.getName()))));

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
        assertEquals(1, resolver.getAvailableSystemPackages().size());
        assertEquals(2, toolchains[0].getLibraries().size());
        assertEquals(1, toolchains[0].getApplications().size());
        {
            final CMakeResolvedExecutable[] applications = toolchains[0].getApplications()
                    .toArray(new CMakeResolvedExecutable[toolchains[0].getApplications().size()]);
            assertEquals(1, applications[0].getPrivateSystemPackageDependencies().size());
            assertEquals(2, applications[0].getPrivateProjectPackageDependencies().size());
            assertEquals(1, applications[0].getPrivateLinkOptions().size());
        }
        assertEquals(0, toolchains[0].getTests().size());
    }
}
