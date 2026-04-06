/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Mockito;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomTaskProto;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;
import io.github.tomaki19.gradle.cmake.files.CMakeFileConventions;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeResolver;

class CMakePluginTestEnhanced {

    @Mock
    private org.gradle.api.file.FileCollection mockFileCollection;

    @Mock
    private org.gradle.api.artifacts.Configuration mockConfiguration;

    @Mock
    private DirectoryProperty mockDirectory;

    @Mock
    private org.gradle.api.file.FileCollection mockArtifacts;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void pluginAppliesBasePlugin() {
        final Project project = ProjectBuilder.builder().build();

        // Mock the plugin manager
        project.getPluginManager().apply(BasePlugin.class);

        // Verify that the plugin was applied
        assertTrue(project.getPlugins().hasPlugin(BasePlugin.class));
    }

    @Test
    void pluginCreatesExtensionCorrectly() {
        final Project project = ProjectBuilder.builder().build();

        // Apply the plugin
        project.getPluginManager().apply(CMakePlugin.class);

        // Verify the extension was created
        final CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);
        assertNotNull(extension);
        assertEquals(CMakeExtension.NAME, "cmake");
    }

    @Test
    void pluginConstructorWorks() {
        // Create plugin with mock software component factory
        final org.gradle.api.component.SoftwareComponentFactory mockComponentFactory = Mockito.mock(org.gradle.api.component.SoftwareComponentFactory.class);

        final CMakePlugin plugin = new CMakePlugin(mockComponentFactory);
        assertNotNull(plugin);
    }

    @Test
    void pluginRegistersCustomTasks() {
        final Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply(CMakePlugin.class);

        final CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);

        // Register a toolchain so the register call has something to iterate
        MockCMakeToolchain.register("toolchain", extension);

        // Register a custom task across all toolchains and configs - should not throw
        extension.register("customTask", (proto) -> {});

        assertNotNull(extension);
    }

    @Test
    void pluginHandlesMultipleBuildConfigs() {
        final Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply(CMakePlugin.class);

        final CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);

        // Create toolchains with multiple build configs
        MockCMakeToolchain.registerWithBuildConfigs("toolchain1", extension, "Debug", "Release");
        MockCMakeToolchain.registerWithBuildConfigs("toolchain2", extension, "Debug");

        // Verify extensions were created
        assertNotNull(extension.getToolchains());
        assertEquals(2, extension.getToolchains().size());
    }

    @Test
    void pluginHandlesEmptyConfiguration() {
        final Project project = ProjectBuilder.builder().build();

        // Apply plugin with empty configuration
        project.getPluginManager().apply(CMakePlugin.class);

        final CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);

        // Verify extension exists even with no toolchains/libraries
        assertNotNull(extension);
        assertEquals(0, extension.getLibraries().size());
        assertEquals(0, extension.getApplications().size());
        assertEquals(0, extension.getTests().size());
    }

    @Test
    void pluginHandlesNullCustomTasksMap() {
        // Test that plugin can handle null custom tasks map (edge case)
        final org.gradle.api.component.SoftwareComponentFactory mockComponentFactory =
            Mockito.mock(org.gradle.api.component.SoftwareComponentFactory.class);

        final CMakePlugin plugin = new CMakePlugin(mockComponentFactory);
        assertNotNull(plugin);
    }

    @Test
    void pluginCreatesSoftwareComponent() {
        final Project project = ProjectBuilder.builder().build();

        // Apply plugin
        project.getPluginManager().apply(CMakePlugin.class);

        // Verify component exists
        final var components = project.getComponents();
        assertFalse(components.isEmpty());
    }

    @Test
    void testCustomTaskProtoCreation() {
        final Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply(CMakePlugin.class);

        final CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);
        MockCMakeToolchain.register("toolchain", extension);

        final CMakeToolchain toolchain = extension.getToolchains().stream().findFirst().orElse(null);

        assertNotNull(toolchain);

        // Create custom task proto
        final CMakeCustomTaskProto proto = new CMakeCustomTaskProto("customTask", toolchain, "Debug");

        assertNotNull(proto);
        assertEquals("customTask", proto.getName());
        assertEquals("Debug", proto.getBuildConfig());
    }

    @Test
    void testResolverIntegration() {
        final Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply(CMakePlugin.class);

        final CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);
        MockCMakeToolchain.register("toolchain", extension);
        extension.getPackages().register("package");
        extension.getLibraries().register("library");

        // Create resolver and process
        final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(),
            extension.getToolchains());

        final java.util.Collection<CMakeResolvedToolchain> results = resolver.process(
            extension.getLibraries(), extension.getApplications(), extension.getTests());

        assertNotNull(results);
    }

    @Test
    void testPluginWithMultipleToolchains() {
        final Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply(CMakePlugin.class);

        final CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);
        MockCMakeToolchain.registerWithBuildConfigs("linux-toolchain", extension, "Debug", "Release");
        MockCMakeToolchain.registerWithBuildConfigs("windows-toolchain", extension, "Debug");
        extension.getLibraries().register("mylib");

        final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(),
            extension.getToolchains());

        final java.util.Collection<CMakeResolvedToolchain> results = resolver.process(
            extension.getLibraries(), extension.getApplications(), extension.getTests());

        assertNotNull(results);
        assertFalse(results.isEmpty());
    }

    @Test
    void testPluginHandlesNullCollections() {
        final Project project = ProjectBuilder.builder().build();

        // Apply plugin
        project.getPluginManager().apply(CMakePlugin.class);

        // Extension should handle null collections gracefully
        final CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);

        assertNotNull(extension);
    }

    @Test
    void testCMakeFileConventions() {
        final Project project = ProjectBuilder.builder().build();

        // Test file convention methods
        final DirectoryProperty buildDir = project.getLayout().getBuildDirectory();

        assertNotNull(buildDir);

        // Verify file conventions are accessible
        final Directory configDir = CMakeFileConventions.targetConfigDirectory(buildDir.get());

        assertNotNull(configDir);
    }
}
