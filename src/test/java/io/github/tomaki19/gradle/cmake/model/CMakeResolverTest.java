/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.internal.os.OperatingSystem;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeApplication;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeBinaryLinkSpec;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary;
import io.github.tomaki19.gradle.cmake.extension.api.CMakePackage;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeTest;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeApplication;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeLibrary;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeTest;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeToolchain;
import io.github.tomaki19.gradle.cmake.helper.TestCMakePackage;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeToolchain;

class CMakeResolverTest {

    @Test
    void testResolverCreation() {
        final Project project = mock(Project.class);
        final Set<CMakePackage> packages = Set.of();
        final Set<CMakeToolchain> toolchains = Set.of();

        final CMakeResolver resolver = new CMakeResolver(project, packages, toolchains);
        assertNotNull(resolver);
    }

    @Test
    void testProcessWithEmptyCollections() {
        final Project project = mock(Project.class);
        final Set<CMakePackage> packages = Set.of();
        final Set<CMakeToolchain> toolchains = Set.of();

        final CMakeResolver resolver = new CMakeResolver(project, packages, toolchains);

        final Set<CMakeLibrary> libraries = Set.of();
        final Set<CMakeApplication> applications = Set.of();
        final Set<CMakeTest> tests = Set.of();

        final Collection<?> results = resolver.process(libraries, applications, tests);
        assertNotNull(results);
        assertEquals(0, results.size());
    }

    @Test
    void testProcessWithNullToolchains() {
        final Project project = mock(Project.class);
        final Set<CMakePackage> packages = Set.of();
        final Set<CMakeToolchain> toolchains = null;

        assertThrows(NullPointerException.class, () -> {
            new CMakeResolver(project, packages, toolchains);
        });
    }

    @Test
    void testProcessWithNullPackages() {
        final Project project = mock(Project.class);
        final Set<CMakePackage> packages = null;
        final Set<CMakeToolchain> toolchains = Set.of();

        assertThrows(NullPointerException.class, () -> {
            new CMakeResolver(project, packages, toolchains);
        });
    }

    @Test
    void testProcessWithNullProject() {
        final Project project = null;
        final Set<CMakePackage> packages = Set.of();
        final Set<CMakeToolchain> toolchains = Set.of();

        assertThrows(NullPointerException.class, () -> {
            new CMakeResolver(project, packages, toolchains);
        });
    }

    @Test
    void testConstructorWithNullProject() {
        Project project = null;
        Set<CMakePackage> packages = Collections.emptySet();
        Set<CMakeToolchain> toolchains = Collections.emptySet();

        assertThrows(NullPointerException.class, () -> {
            new CMakeResolver(project, packages, toolchains);
        });
    }

    @Test
    void testConstructorWithNullPackages() {
        Project project = ProjectBuilder.builder().build();
        Set<CMakePackage> packages = null;
        Set<CMakeToolchain> toolchains = Collections.emptySet();

        assertThrows(NullPointerException.class, () -> {
            new CMakeResolver(project, packages, toolchains);
        });
    }

    @Test
    void testConstructorWithNullToolchains() {
        Project project = ProjectBuilder.builder().build();
        Set<CMakePackage> packages = Collections.emptySet();
        Set<CMakeToolchain> toolchains = null;

        assertThrows(NullPointerException.class, () -> {
            new CMakeResolver(project, packages, toolchains);
        });
    }

    @Test
    void testConstructorValid() {
        Project project = ProjectBuilder.builder().build();
        Set<CMakePackage> packages = Collections.emptySet();
        Set<CMakeToolchain> toolchains = Collections.emptySet();

        CMakeResolver resolver = new CMakeResolver(project, packages, toolchains);
        assertNotNull(resolver);
    }

    @Test
    void testProcessEmpty() {
        Project project = ProjectBuilder.builder().build();
        Set<CMakePackage> packages = Collections.emptySet();
        Set<CMakeToolchain> toolchains = Collections.emptySet();
        Set<CMakeLibrary> libraries = Collections.emptySet();
        Set<CMakeApplication> applications = Collections.emptySet();
        Set<CMakeTest> tests = Collections.emptySet();

        CMakeResolver resolver = new CMakeResolver(project, packages, toolchains);
        assertNotNull(resolver);

        // This should not throw any exception
        var result = resolver.process(libraries, applications, tests);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testProcessWithToolchain() {
        Project project = ProjectBuilder.builder().build();
        Set<CMakePackage> packages = Collections.emptySet();

        // Create a mock toolchain
        CMakeToolchain toolchain = new MockCMakeToolchain("toolchain", project.getObjects());

        Set<CMakeToolchain> toolchains = Set.of(toolchain);
        Set<CMakeLibrary> libraries = Collections.emptySet();
        Set<CMakeApplication> applications = Collections.emptySet();
        Set<CMakeTest> tests = Collections.emptySet();

        CMakeResolver resolver = new CMakeResolver(project, packages, toolchains);
        assertNotNull(resolver);

        // This should not throw any exception
        var result = resolver.process(libraries, applications, tests);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testProcessWithMultipleToolchains() {
        Project project = ProjectBuilder.builder().build();
        Set<CMakePackage> packages = Collections.emptySet();

        // Create mock toolchains
        CMakeToolchain toolchain1 = new MockCMakeToolchain("toolchain1", project.getObjects());
        CMakeToolchain toolchain2 = new MockCMakeToolchain("toolchain2", project.getObjects());

        Set<CMakeToolchain> toolchains = Set.of(toolchain1, toolchain2);
        Set<CMakeLibrary> libraries = Collections.emptySet();
        Set<CMakeApplication> applications = Collections.emptySet();
        Set<CMakeTest> tests = Collections.emptySet();

        CMakeResolver resolver = new CMakeResolver(project, packages, toolchains);
        assertNotNull(resolver);

        var result = resolver.process(libraries, applications, tests);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testProcessWithLibraryWithSources() {
        Project project = ProjectBuilder.builder().build();
        Set<CMakePackage> packages = Collections.emptySet();
        Set<CMakeToolchain> toolchains = Collections.emptySet();

        CMakeResolver resolver = new CMakeResolver(project, packages, toolchains);

        // Create a library with sources
        CMakeLibrary library = new MockCMakeLibrary("test-library", project.getObjects());

        Set<CMakeLibrary> libraries = Set.of(library);
        Set<CMakeApplication> applications = Collections.emptySet();
        Set<CMakeTest> tests = Collections.emptySet();

        // Should not throw any exception
        var result = resolver.process(libraries, applications, tests);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testProcessWithApplicationWithSources() {
        Project project = ProjectBuilder.builder().build();
        Set<CMakePackage> packages = Collections.emptySet();
        Set<CMakeToolchain> toolchains = Collections.emptySet();

        CMakeResolver resolver = new CMakeResolver(project, packages, toolchains);

        // Create an application with sources
        CMakeApplication application = new MockCMakeApplication("test-application", project.getObjects());

        Set<CMakeLibrary> libraries = Collections.emptySet();
        Set<CMakeApplication> applications = Set.of(application);
        Set<CMakeTest> tests = Collections.emptySet();

        // Should not throw any exception
        var result = resolver.process(libraries, applications, tests);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testProcessWithTestWithSources() {
        Project project = ProjectBuilder.builder().build();
        Set<CMakePackage> packages = Collections.emptySet();
        Set<CMakeToolchain> toolchains = Collections.emptySet();

        CMakeResolver resolver = new CMakeResolver(project, packages, toolchains);

        // Create a test with sources
        CMakeTest test = new MockCMakeTest("test-test", project.getObjects());

        Set<CMakeLibrary> libraries = Collections.emptySet();
        Set<CMakeApplication> applications = Collections.emptySet();
        Set<CMakeTest> tests = Set.of(test);

        // Should not throw any exception
        var result = resolver.process(libraries, applications, tests);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testProcessWithToolchainsThatMatchBinary() {
        Project project = ProjectBuilder.builder().build();
        Set<CMakePackage> packages = Collections.emptySet();

        // Create a mock toolchain
        CMakeToolchain toolchain = new MockCMakeToolchain("test-toolchain", project.getObjects());

        Set<CMakeToolchain> toolchains = Set.of(toolchain);

        // Create a library with matching toolchain
        CMakeLibrary library = new MockCMakeLibrary("test-library", project.getObjects());
        library.toolchains("test-toolchain");
        library.getHeaders().srcDirs("src/test.h");

        Set<CMakeLibrary> libraries = Set.of(library);
        Set<CMakeApplication> applications = Collections.emptySet();
        Set<CMakeTest> tests = Collections.emptySet();

        CMakeResolver resolver = new CMakeResolver(project, packages, toolchains);

        var result = resolver.process(libraries, applications, tests);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testProcessWithEmptyToolchains() {
        Project project = ProjectBuilder.builder().build();
        Set<CMakePackage> packages = Collections.emptySet();
        Set<CMakeToolchain> toolchains = Collections.emptySet();

        CMakeResolver resolver = new CMakeResolver(project, packages, toolchains);

        // Create a library with no toolchains specified
        CMakeLibrary library = new MockCMakeLibrary("test-library", project.getObjects());

        Set<CMakeLibrary> libraries = Set.of(library);
        Set<CMakeApplication> applications = Collections.emptySet();
        Set<CMakeTest> tests = Collections.emptySet();

        var result = resolver.process(libraries, applications, tests);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testProcessWithToolchain_differentOS_excluded() {
        Project project = ProjectBuilder.builder().build();
        CMakeToolchain toolchain = new MockCMakeToolchain("tc", project.getObjects());
        OperatingSystem otherOS = OperatingSystem.current().isWindows()
            ? OperatingSystem.forName("linux")
            : OperatingSystem.forName("windows");
        toolchain.getOperatingSystem().set(otherOS);

        CMakeResolver resolver = new CMakeResolver(project, Collections.emptySet(), Set.of(toolchain));
        CMakeLibrary lib = new MockCMakeLibrary("myLib", project.getObjects());
        var result = resolver.process(Set.of(lib), Collections.emptySet(), Collections.emptySet());
        assertEquals(0, result.size());
    }

    @Test
    void testProcess_privateCompilingDefines() {
        Project project = ProjectBuilder.builder().build();
        CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class,
            project.getTasks());
        TestCMakeToolchain.register("tc", extension);
        NamedDomainObjectProvider<CMakeLibrary> libProvider = extension.getLibraries().register("myLib");
        libProvider.configure(lib -> {
            lib.getCompiling().defines(Map.of("visibility", "PRIVATE"), "MY_DEFINE");
            lib.getHeaders().srcDir(".");
        });

        CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
        var result = resolver.process(extension.getLibraries(), extension.getApplications(), extension.getTests());
        assertEquals(1, result.size());
        assertTrue(result.iterator().next().getInterfaceLibraries().iterator().next()
            .getPrivateCompileDefinitions().contains("MY_DEFINE"));
    }

    @Test
    void testProcess_privateCompilingOptions() {
        Project project = ProjectBuilder.builder().build();
        CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class,
            project.getTasks());
        TestCMakeToolchain.register("tc", extension);
        NamedDomainObjectProvider<CMakeLibrary> libProvider = extension.getLibraries().register("myLib");
        libProvider.configure(lib -> {
            lib.getCompiling().options(Map.of("visibility", "PRIVATE"), "-fPIC");
            lib.getHeaders().srcDir(".");
        });

        CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
        var result = resolver.process(extension.getLibraries(), extension.getApplications(), extension.getTests());
        assertEquals(1, result.size());
        assertTrue(result.iterator().next().getInterfaceLibraries().iterator().next()
            .getPrivateCompileOptions().contains("-fPIC"));
    }

    @Test
    void testProcess_privateLinkingOption() {
        Project project = ProjectBuilder.builder().build();
        CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class,
            project.getTasks());
        TestCMakeToolchain.register("tc", extension);
        NamedDomainObjectProvider<CMakeLibrary> libProvider = extension.getLibraries().register("myLib");
        libProvider.configure(lib -> {
            lib.getLinking().options(Map.of("visibility", "PRIVATE"), "-lm");
            lib.getHeaders().srcDir(".");
        });

        CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
        var result = resolver.process(extension.getLibraries(), extension.getApplications(), extension.getTests());
        assertEquals(1, result.size());
        assertTrue(result.iterator().next().getInterfaceLibraries().iterator().next()
            .getPrivateLinkOptions().contains("-lm"));
    }

    @Test
    void testProcess_privatePackageDependency() throws Exception {
        Project project = ProjectBuilder.builder().build();
        CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class,
            project.getTasks());
        TestCMakeToolchain.register("tc", extension);
        TestCMakePackage.register("myPkg", extension);
        NamedDomainObjectProvider<CMakeLibrary> libProvider = extension.getLibraries().register("myLib");
        libProvider.configure(lib -> {
            lib.getLinking().link(Map.of(CMakeBinaryLinkSpec.PROJECT, "myPkg", "visibility", "PRIVATE"), "target");
            lib.getHeaders().srcDir(".");
        });

        CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
        var result = resolver.process(extension.getLibraries(), extension.getApplications(), extension.getTests());
        assertEquals(1, result.size());
        assertFalse(result.iterator().next().getInterfaceLibraries().iterator().next()
            .getPrivatePackageDependencies().isEmpty());
    }

    @Test
    void testProcess_invalidProjectReference_throws() {
        Project project = ProjectBuilder.builder().build();
        CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class,
            project.getTasks());
        TestCMakeToolchain.register("tc", extension);
        NamedDomainObjectProvider<CMakeLibrary> libProvider = extension.getLibraries().register("myLib");
        libProvider.configure(lib -> {
            lib.getLinking().link(Map.of(CMakeBinaryLinkSpec.PROJECT, "nonExistentProject"), "dep");
            lib.getHeaders().srcDir(".");
        });

        CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
        assertThrows(IllegalArgumentException.class,
            () -> resolver.process(extension.getLibraries(), extension.getApplications(), extension.getTests()));
    }

    @Test
    void testProcess_selfReferenceIgnored() {
        Project project = ProjectBuilder.builder().build();
        CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class,
            project.getTasks());
        TestCMakeToolchain.register("tc", extension);
        NamedDomainObjectProvider<CMakeLibrary> libProvider = extension.getLibraries().register("myLib");
        libProvider.configure(lib -> {
            lib.getLinking().link(Map.of(), "myLib");
            lib.getHeaders().srcDir(".");
        });

        CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
        var result = resolver.process(extension.getLibraries(), extension.getApplications(), extension.getTests());
        assertEquals(1, result.size());
        assertTrue(result.iterator().next().getInterfaceLibraries().iterator().next()
            .getPublicProjectDependencies().isEmpty());
    }

    @Test
    void testProcess_currentProjectByName() {
        Project project = ProjectBuilder.builder().withName("myProject").build();
        CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class,
            project.getTasks());
        TestCMakeToolchain.register("tc", extension);
        NamedDomainObjectProvider<CMakeLibrary> libProvider = extension.getLibraries().register("myLib");
        libProvider.configure(lib -> {
            lib.getLinking().link(Map.of(CMakeBinaryLinkSpec.PROJECT, "myProject"), "otherLib");
            lib.getHeaders().srcDir(".");
        });

        CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
        var result = resolver.process(extension.getLibraries(), extension.getApplications(), extension.getTests());
        assertEquals(1, result.size());
        assertFalse(result.iterator().next().getInterfaceLibraries().iterator().next()
            .getPublicProjectDependencies().isEmpty());
    }
}
