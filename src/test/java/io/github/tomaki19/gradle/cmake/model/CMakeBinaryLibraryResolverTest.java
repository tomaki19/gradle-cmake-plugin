/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeBuildItems;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibraryDependencies;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeBinaryLibrary;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeInterfaceLibrary;
import io.github.tomaki19.gradle.cmake.helper.TestCMakePackage;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeToolchain;
import io.github.tomaki19.gradle.cmake.tasks.CMakeCustomTaskProto;

public class CMakeBinaryLibraryResolverTest {

    @Test
    void resolveNoToolchainTest() throws Exception {
        final Project project = ProjectBuilder.builder().build();
        final Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
        final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                CMakeExtension.class, customTasks, new HashMap<>(), new HashMap<>());

        TestCMakePackage.register("Package0", extension);
        TestCMakeBinaryLibrary.register("BinaryLibrary0", extension, CMakeBuildVariant.SHARED);
        TestCMakeToolchain.registerWithLibraryDependencies("Toolchain0", extension,
                Arrays.asList(new CMakeBuildItems(CMakeVisibility.PUBLIC, "-loption")),
                Arrays.asList(
                        new CMakeLibraryDependencies("target").from("Package0")
                                .forBuildVariant(CMakeBuildVariant.STATIC),
                        new CMakeLibraryDependencies("BinaryLibrary0").from(project.getName())
                                .variant(CMakeLinkVariant.SHARED)
                                .forBuildVariant(CMakeBuildVariant.STATIC)));
        TestCMakeBinaryLibrary.register("BinaryLibrary1", extension, CMakeBuildVariant.SHARED);

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
    }

    @Test
    void resolveMultipleToolchainTest() throws Exception {
        final Project project = ProjectBuilder.builder().build();
        final Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
        final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                CMakeExtension.class, customTasks, new HashMap<>(), new HashMap<>());

        TestCMakePackage.register("Package0", extension);
        TestCMakePackage.register("Package1", extension);
        TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
        TestCMakeInterfaceLibrary.register("InterfaceLibrary1", extension);
        TestCMakeBinaryLibrary.register("StaticLibrary0", extension, Arrays.asList("Toolchain0"),
                CMakeBuildVariant.STATIC);
        TestCMakeBinaryLibrary.register("StaticLibrary1", extension, Arrays.asList("Toolchain1"),
                CMakeBuildVariant.STATIC);
        TestCMakeBinaryLibrary.register("SharedLibrary0", extension, Arrays.asList("Toolchain0"),
                CMakeBuildVariant.SHARED);
        TestCMakeBinaryLibrary.register("SharedLibrary1", extension, Arrays.asList("Toolchain1"),
                CMakeBuildVariant.SHARED);
        TestCMakeToolchain.registerWithLibraryDependencies("Toolchain0", extension,
                Arrays.asList(new CMakeBuildItems(CMakeVisibility.PUBLIC, "-loption")),
                Arrays.asList(
                        new CMakeLibraryDependencies("target").from("Package0"),
                        new CMakeLibraryDependencies("InterfaceLibrary0")
                                .variant(CMakeLinkVariant.INTERFACE),
                        new CMakeLibraryDependencies("StaticLibrary0").from(project.getName())
                                .variant(CMakeLinkVariant.STATIC),
                        new CMakeLibraryDependencies("SharedLibrary0").from(project.getName())
                                .variant(CMakeLinkVariant.SHARED)));
        TestCMakeToolchain.registerWithLibraryDependencies("Toolchain1", extension,
                Arrays.asList(new CMakeBuildItems(CMakeVisibility.PRIVATE, "-loption")),
                Arrays.asList(
                        new CMakeLibraryDependencies("target").from("Package1")
                                .visibility(CMakeVisibility.PRIVATE),
                        new CMakeLibraryDependencies("InterfaceLibrary1")
                                .from(project.getName())
                                .variant(CMakeLinkVariant.INTERFACE)
                                .visibility(CMakeVisibility.PRIVATE),
                        new CMakeLibraryDependencies("StaticLibrary1").from(project.getName())
                                .variant(CMakeLinkVariant.STATIC)
                                .visibility(CMakeVisibility.PRIVATE),
                        new CMakeLibraryDependencies("SharedLibrary1").from(project.getName())
                                .variant(CMakeLinkVariant.SHARED)
                                .visibility(CMakeVisibility.PRIVATE)));
        TestCMakeBinaryLibrary.register("Library0", extension,
                Arrays.asList("Toolchain0", "Toolchain1"), CMakeBuildVariant.SHARED);

        assertEquals(2, extension.getPackages().size());
        assertEquals(2, extension.getToolchains().size());
        assertEquals(7, extension.getLibraries().size());
        assertEquals(0, extension.getApplications().size());
        assertEquals(0, extension.getTests().size());

        final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(),
                extension.getToolchains());
        final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
                extension.getApplications(), extension.getTests());

        final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
        assertEquals(2, toolchains.length);

        assertEquals("Toolchain0", toolchains[0].getName());
        assertEquals(2, toolchains[0].getInterfaceLibraries().size());
        assertEquals(1, toolchains[0].getStaticLibraries().size());
        assertEquals(2, toolchains[0].getSharedLibraries().size());
        assertEquals(0, toolchains[0].getApplications().size());
        assertEquals(0, toolchains[0].getTests().size());
        {
            final CMakeResolvedLibrary[] interfaceLibrary = toolchains[0].getInterfaceLibraries()
                    .toArray(new CMakeResolvedLibrary[toolchains[0].getInterfaceLibraries()
                            .size()]);
            assertEquals("InterfaceLibrary0", interfaceLibrary[0].getName());
            assertEquals(1, interfaceLibrary[0].getPublicPackageDependencies().size());
            assertEquals(2, interfaceLibrary[0].getPublicProjectDependencies().size());
            assertEquals(1, interfaceLibrary[0].getPublicLinkOptions().size());
            assertEquals("InterfaceLibrary1", interfaceLibrary[1].getName());
            assertEquals(1, interfaceLibrary[1].getPublicPackageDependencies().size());
            assertEquals(3, interfaceLibrary[1].getPublicProjectDependencies().size());
            assertEquals(1, interfaceLibrary[1].getPublicLinkOptions().size());
            final CMakeResolvedLibrary[] sharedLibrary = toolchains[0].getSharedLibraries()
                    .toArray(new CMakeResolvedLibrary[toolchains[0].getSharedLibraries().size()]);
            assertEquals("Library0", sharedLibrary[0].getName());
            assertEquals(1, sharedLibrary[0].getPublicPackageDependencies().size());
            assertEquals(3, sharedLibrary[0].getPublicProjectDependencies().size());
            assertEquals(1, sharedLibrary[0].getPublicLinkOptions().size());
            assertEquals("SharedLibrary0", sharedLibrary[1].getName());
            assertEquals(1, sharedLibrary[1].getPublicPackageDependencies().size());
            assertEquals(2, sharedLibrary[1].getPublicProjectDependencies().size());
            assertEquals(1, sharedLibrary[1].getPublicLinkOptions().size());
            final CMakeResolvedLibrary[] staticLibrary = toolchains[0].getStaticLibraries()
                    .toArray(new CMakeResolvedLibrary[toolchains[0].getStaticLibraries().size()]);
            assertEquals("StaticLibrary0", staticLibrary[0].getName());
            assertEquals(1, staticLibrary[0].getPublicPackageDependencies().size());
            assertEquals(2, staticLibrary[0].getPublicProjectDependencies().size());
            assertEquals(1, staticLibrary[0].getPublicLinkOptions().size());
        }

        assertEquals("Toolchain1", toolchains[1].getName());
        assertEquals(2, toolchains[1].getInterfaceLibraries().size());
        assertEquals(1, toolchains[1].getStaticLibraries().size());
        assertEquals(2, toolchains[1].getSharedLibraries().size());
        assertEquals(0, toolchains[1].getApplications().size());
        assertEquals(0, toolchains[1].getTests().size());
        {
            final CMakeResolvedLibrary[] interfaceLibrary = toolchains[1].getInterfaceLibraries()
                    .toArray(new CMakeResolvedLibrary[toolchains[1].getInterfaceLibraries()
                            .size()]);
            assertEquals("InterfaceLibrary0", interfaceLibrary[0].getName());
            assertEquals(0, interfaceLibrary[0].getPublicPackageDependencies().size());
            assertEquals(0, interfaceLibrary[0].getPublicProjectDependencies().size());
            assertEquals(0, interfaceLibrary[0].getPublicLinkOptions().size());
            assertEquals("InterfaceLibrary1", interfaceLibrary[1].getName());
            assertEquals(0, interfaceLibrary[1].getPublicPackageDependencies().size());
            assertEquals(0, interfaceLibrary[1].getPublicProjectDependencies().size());
            assertEquals(0, interfaceLibrary[1].getPublicLinkOptions().size());
            final CMakeResolvedLibrary[] sharedLibrary = toolchains[1].getSharedLibraries()
                    .toArray(new CMakeResolvedLibrary[toolchains[1].getSharedLibraries().size()]);
            assertEquals("Library0", sharedLibrary[0].getName());
            assertEquals(0, sharedLibrary[0].getPublicPackageDependencies().size());
            assertEquals(0, sharedLibrary[0].getPublicProjectDependencies().size());
            assertEquals(0, sharedLibrary[0].getPublicLinkOptions().size());
            assertEquals("SharedLibrary1", sharedLibrary[1].getName());
            assertEquals(0, sharedLibrary[1].getPublicPackageDependencies().size());
            assertEquals(0, sharedLibrary[1].getPublicProjectDependencies().size());
            assertEquals(0, sharedLibrary[1].getPublicLinkOptions().size());
            final CMakeResolvedLibrary[] staticLibrary = toolchains[1].getStaticLibraries()
                    .toArray(new CMakeResolvedLibrary[toolchains[1].getStaticLibraries().size()]);
            assertEquals("StaticLibrary1", staticLibrary[0].getName());
            assertEquals(0, staticLibrary[0].getPublicPackageDependencies().size());
            assertEquals(0, staticLibrary[0].getPublicProjectDependencies().size());
            assertEquals(0, staticLibrary[0].getPublicLinkOptions().size());
        }
    }

    @Test
    void resolveNoDependenciesTest() throws Exception {
        final Project project = ProjectBuilder.builder().build();
        final Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
        final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                CMakeExtension.class, customTasks, new HashMap<>(), new HashMap<>());

        TestCMakePackage.register("Package0", extension);
        TestCMakeBinaryLibrary.register("BinaryLibrary0", extension, Arrays.asList("Toolchain0"),
                CMakeBuildVariant.SHARED);
        TestCMakeToolchain.register("Toolchain0", extension);
        TestCMakeBinaryLibrary.register("BinaryLibrary1", extension, Arrays.asList("Toolchain0"),
                CMakeBuildVariant.SHARED);

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
        assertEquals(0, toolchains[0].getInterfaceLibraries().size());
        assertEquals(0, toolchains[0].getStaticLibraries().size());
        assertEquals(2, toolchains[0].getSharedLibraries().size());
        assertEquals(0, toolchains[0].getApplications().size());
        assertEquals(0, toolchains[0].getTests().size());
    }

    @Test
    void resolveInterfaceDependenciesTest() throws Exception {
        final Project project = ProjectBuilder.builder().build();
        final Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
        final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                CMakeExtension.class, customTasks, new HashMap<>(), new HashMap<>());

        TestCMakePackage.register("Package0", extension);
        TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
        TestCMakeBinaryLibrary.register("BinaryLibrary0", extension, Arrays.asList("Toolchain0"),
                CMakeBuildVariant.SHARED);
        TestCMakeToolchain.register("Toolchain0", extension);
        TestCMakeBinaryLibrary.register("BinaryLibrary1", extension, Arrays.asList("Toolchain0"),
                CMakeBuildVariant.SHARED);

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
        assertEquals(1, toolchains[0].getInterfaceLibraries().size());
        assertEquals(0, toolchains[0].getStaticLibraries().size());
        assertEquals(2, toolchains[0].getSharedLibraries().size());
        assertEquals(0, toolchains[0].getApplications().size());
        assertEquals(0, toolchains[0].getTests().size());
    }

    @Test
    void resolveToolchainDependencyTest() throws Exception {
        final Project project = ProjectBuilder.builder().build();
        final Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
        final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                CMakeExtension.class, customTasks, new HashMap<>(), new HashMap<>());

        TestCMakePackage.register("Package0", extension);
        TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
        TestCMakeBinaryLibrary.register("BinaryLibrary0", extension, Arrays.asList("Toolchain0"),
                CMakeBuildVariant.SHARED);
        TestCMakeToolchain.registerWithLibraryDependencies("Toolchain0", extension,
                Arrays.asList(new CMakeBuildItems(CMakeVisibility.PUBLIC, "-loption")),
                Arrays.asList(
                        new CMakeLibraryDependencies("target").from("Package0")
                                .forBuildVariant(CMakeBuildVariant.SHARED),
                        new CMakeLibraryDependencies("InterfaceLibrary0")
                                .variant(CMakeLinkVariant.INTERFACE)
                                .forBuildVariant(CMakeBuildVariant.SHARED),
                        new CMakeLibraryDependencies("BinaryLibrary0")
                                .variant(CMakeLinkVariant.SHARED)
                                .forBuildVariant(CMakeBuildVariant.SHARED)));
        TestCMakeBinaryLibrary.register("BinaryLibrary1", extension, Arrays.asList("Toolchain0"),
                CMakeBuildVariant.SHARED);

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
        assertEquals(1, toolchains[0].getInterfaceLibraries().size());
        assertEquals(0, toolchains[0].getStaticLibraries().size());
        assertEquals(2, toolchains[0].getSharedLibraries().size());
        assertEquals(0, toolchains[0].getApplications().size());
        assertEquals(0, toolchains[0].getTests().size());
    }

    @Test
    void resolvePrivateLinkDependencyTest() throws Exception {
        final Project project = ProjectBuilder.builder().build();
        final Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
        final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                CMakeExtension.class, customTasks, new HashMap<>(), new HashMap<>());

        TestCMakePackage.register("Package0", extension);
        TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
        TestCMakeBinaryLibrary.register("BinaryLibrary0", extension, Arrays.asList("Toolchain0"),
                CMakeBuildVariant.SHARED);
        TestCMakeToolchain.register("Toolchain0", extension);
        TestCMakeBinaryLibrary.registerWithDependencies("BinaryLibrary1", extension,
                Arrays.asList("Toolchain0"), CMakeBuildVariant.SHARED,
                Arrays.asList(new CMakeBuildItems(CMakeVisibility.PUBLIC, "-loption")),
                Arrays.asList(
                        new CMakeLibraryDependencies("target").from("Package0"),
                        new CMakeLibraryDependencies("InterfaceLibrary0")
                                .variant(CMakeLinkVariant.INTERFACE),
                        new CMakeLibraryDependencies("BinaryLibrary0")
                                .variant(CMakeLinkVariant.SHARED)));

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
        assertEquals(1, toolchains[0].getInterfaceLibraries().size());
        assertEquals(0, toolchains[0].getStaticLibraries().size());
        assertEquals(2, toolchains[0].getSharedLibraries().size());
        assertEquals(0, toolchains[0].getApplications().size());
        assertEquals(0, toolchains[0].getTests().size());
    }

    @Test
    void resolvePublicLinkDependencyTest() throws Exception {
        final Project project = ProjectBuilder.builder().build();
        final Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
        final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
                CMakeExtension.class, customTasks, new HashMap<>(), new HashMap<>());

        TestCMakePackage.register("Package0", extension);
        TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
        TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
                Arrays.asList("Toolchain0"), CMakeBuildVariant.SHARED);
        TestCMakeToolchain.register("Toolchain0", extension);
        TestCMakeBinaryLibrary.registerWithDependencies("BinaryLibrary1", extension,
                Arrays.asList("Toolchain0"), CMakeBuildVariant.SHARED,
                Arrays.asList(new CMakeBuildItems(CMakeVisibility.PUBLIC, "-loption")),
                Arrays.asList(
                        new CMakeLibraryDependencies("target").from("Package0"),
                        new CMakeLibraryDependencies("InterfaceLibrary0")
                                .from(project.getName())
                                .variant(CMakeLinkVariant.INTERFACE),
                        new CMakeLibraryDependencies("BinaryLibrary0")
                                .variant(CMakeLinkVariant.SHARED)));

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
        assertEquals(1, toolchains[0].getInterfaceLibraries().size());
        assertEquals(0, toolchains[0].getStaticLibraries().size());
        assertEquals(2, toolchains[0].getSharedLibraries().size());
        assertEquals(0, toolchains[0].getApplications().size());
        assertEquals(0, toolchains[0].getTests().size());
    }

}
