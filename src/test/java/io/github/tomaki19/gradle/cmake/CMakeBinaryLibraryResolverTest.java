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

public class CMakeBinaryLibraryResolverTest {

    @Test
    void resolveNoToolchainTest() {
        final Project project = ProjectBuilder.builder().build();
        final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                CMakeExtension.class, project.getTasks());

        TestCMakePackage.register("Package0", extension);
        TestCMakeBinaryLibrary.register("BinaryLibrary0", extension);
        TestCMakeToolchain.registerWithLibraryDependencies("Toolchain0", extension,
                new HashSet<>(Arrays.asList("Package0::target", "-loption",
                        "%s::BinaryLibrary0::shared".formatted(project.getName()))));
        TestCMakeBinaryLibrary.register("BinaryLibrary1", extension);

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
        assertEquals(0, toolchains.length);
    }

    @Test
    void resolveNoDependenciesTest() {
        final Project project = ProjectBuilder.builder().build();
        final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                CMakeExtension.class, project.getTasks());

        TestCMakePackage.register("Package0", extension);
        TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
                new HashSet<>(Arrays.asList("Toolchain0")));
        TestCMakeToolchain.register("Toolchain0", extension);
        TestCMakeBinaryLibrary.register("BinaryLibrary1", extension,
                new HashSet<>(Arrays.asList("Toolchain0")));

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
        assertEquals(1, toolchains[0].getSystemPackages().size());
        assertEquals(2, toolchains[0].getLibraries().size());
        assertEquals(0, toolchains[0].getApplications().size());
        assertEquals(0, toolchains[0].getTests().size());
    }

    @Test
    void resolveInterfaceDependenciesTest() {
        final Project project = ProjectBuilder.builder().build();
        final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                CMakeExtension.class, project.getTasks());

        TestCMakePackage.register("Package0", extension);
        TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
        TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
                new HashSet<>(Arrays.asList("Toolchain0")));
        TestCMakeToolchain.register("Toolchain0", extension);
        TestCMakeBinaryLibrary.register("BinaryLibrary1", extension,
                new HashSet<>(Arrays.asList("Toolchain0")));

        assertEquals(1, extension.getPackages().size());
        assertEquals(1, extension.getToolchains().size());
        assertEquals(3, extension.getLibraries().size());
        assertEquals(0, extension.getApplications().size());
        assertEquals(0, extension.getTests().size());

        final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(),
                extension.getToolchains());
        final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
                extension.getApplications(), extension.getTests());

        final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
        assertEquals(1, toolchains.length);

        assertEquals("Toolchain0", toolchains[0].getName());
        assertEquals(1, toolchains[0].getSystemPackages().size());
        assertEquals(3, toolchains[0].getLibraries().size());
        assertEquals(0, toolchains[0].getApplications().size());
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
        TestCMakeToolchain.registerWithLibraryDependencies("Toolchain0", extension,
                new HashSet<>(Arrays.asList("Package0::target", "-loption",
                        "%s::InterfaceLibrary0::interface".formatted(project.getName()),
                        "%s::BinaryLibrary0::shared".formatted(project.getName()))));
        TestCMakeBinaryLibrary.register("BinaryLibrary1", extension,
                new HashSet<>(Arrays.asList("Toolchain0")));

        assertEquals(1, extension.getPackages().size());
        assertEquals(1, extension.getToolchains().size());
        assertEquals(3, extension.getLibraries().size());
        assertEquals(0, extension.getApplications().size());
        assertEquals(0, extension.getTests().size());

        final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(),
                extension.getToolchains());
        final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
                extension.getApplications(), extension.getTests());

        final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
        assertEquals(1, toolchains.length);

        assertEquals("Toolchain0", toolchains[0].getName());
        assertEquals(1, toolchains[0].getSystemPackages().size());
        assertEquals(3, toolchains[0].getLibraries().size());
        assertEquals(0, toolchains[0].getApplications().size());
        assertEquals(0, toolchains[0].getTests().size());
    }

    @Test
    void resolvePrivateLinkDependencyTest() {
        final Project project = ProjectBuilder.builder().build();
        final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                CMakeExtension.class, project.getTasks());

        TestCMakePackage.register("Package0", extension);
        TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
        TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
                new HashSet<>(Arrays.asList("Toolchain0")));
        TestCMakeToolchain.register("Toolchain0", extension);
        TestCMakeBinaryLibrary.registerWithPrivateDependencies("BinaryLibrary1", extension,
                new HashSet<>(Arrays.asList("Toolchain0")),
                new HashSet<>(Arrays.asList("Package0::target", "-loption",
                        "%s::InterfaceLibrary0::interface".formatted(project.getName()),
                        "%s::BinaryLibrary0::shared".formatted(project.getName()))));

        assertEquals(1, extension.getPackages().size());
        assertEquals(1, extension.getToolchains().size());
        assertEquals(3, extension.getLibraries().size());
        assertEquals(0, extension.getApplications().size());
        assertEquals(0, extension.getTests().size());

        final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(),
                extension.getToolchains());
        final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
                extension.getApplications(), extension.getTests());

        final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
        assertEquals(1, toolchains.length);

        assertEquals("Toolchain0", toolchains[0].getName());
        assertEquals(1, toolchains[0].getSystemPackages().size());
        assertEquals(3, toolchains[0].getLibraries().size());
        assertEquals(0, toolchains[0].getApplications().size());
        assertEquals(0, toolchains[0].getTests().size());
    }

    @Test
    void resolvePublicLinkDependencyTest() {
        final Project project = ProjectBuilder.builder().build();
        final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                CMakeExtension.class, project.getTasks());

        TestCMakePackage.register("Package0", extension);
        TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
        TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
                new HashSet<>(Arrays.asList("Toolchain0")));
        TestCMakeToolchain.register("Toolchain0", extension);
        TestCMakeBinaryLibrary.registerWithPublicDependencies("BinaryLibrary1", extension,
                new HashSet<>(Arrays.asList("Toolchain0")),
                new HashSet<>(Arrays.asList("Package0::target", "-loption",
                        "%s::InterfaceLibrary0::interface".formatted(project.getName()),
                        "%s::BinaryLibrary0::shared".formatted(project.getName()))));

        assertEquals(1, extension.getPackages().size());
        assertEquals(1, extension.getToolchains().size());
        assertEquals(3, extension.getLibraries().size());
        assertEquals(0, extension.getApplications().size());
        assertEquals(0, extension.getTests().size());

        final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(),
                extension.getToolchains());
        final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
                extension.getApplications(), extension.getTests());

        final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
        assertEquals(1, toolchains.length);

        assertEquals("Toolchain0", toolchains[0].getName());
        assertEquals(1, toolchains[0].getSystemPackages().size());
        assertEquals(3, toolchains[0].getLibraries().size());
        assertEquals(0, toolchains[0].getApplications().size());
        assertEquals(0, toolchains[0].getTests().size());
    }

}
