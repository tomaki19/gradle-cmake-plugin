/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomTaskProto;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibraryDependencies;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeBinaryLibrary;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeInterfaceLibrary;
import io.github.tomaki19.gradle.cmake.helper.TestCMakePackage;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeBuildVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeResolver;

class CMakeModuleFileTest {

  @Test
  void testConstructor() throws IOException, URISyntaxException {
    File tempDir = createTempDir();
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
      CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
          CMakeExtension.class, customTasks);

      TestCMakeToolchain.register("Toolchain0", extension);
      TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);

      CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      java.util.Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
      assertNotNull(toolchains);
      assertEquals(1, toolchains.length);
      assertEquals(1, toolchains[0].getInterfaceLibraries().size());
      assertEquals(4, toolchains[0].getBuildConfigs().size());

      for (final CMakeResolvedLibrary library : toolchains[0].getInterfaceLibraries()) {
        for (final String buildConfig : toolchains[0].getBuildConfigs()) {
          final CMakeModuleFile file = new CMakeModuleFile(library, toolchains[0], buildConfig, project);
          assertNotNull(file);
        }
      }
    } finally {
      deleteRecursively(tempDir);
    }
  }

  @Test
  void testWriteTo() throws IOException, URISyntaxException {
    File tempDir = createTempDir();
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
      CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
          CMakeExtension.class, customTasks);

      TestCMakeToolchain.register("Toolchain0", extension);
      TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);

      CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      java.util.Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
      assertNotNull(toolchains);
      assertEquals(1, toolchains.length);

      for (final CMakeResolvedLibrary library : toolchains[0].getInterfaceLibraries()) {
        for (final String buildConfig : toolchains[0].getBuildConfigs()) {
          final CMakeModuleFile file = new CMakeModuleFile(library, toolchains[0], buildConfig, project);
          assertNotNull(file);

          File outputFile = new File(tempDir, "config-%s-%s.cmake".formatted(toolchains[0].getName(), buildConfig));
          try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            file.writeTo(fos);
          }

          assertNotNull(outputFile);
          assertTrue(outputFile.exists());
        }
      }
    } finally {
      deleteRecursively(tempDir);
    }
  }

  @Test
  void testWriteToWithPackage() throws IOException, URISyntaxException {
    File tempDir = createTempDir();
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
      CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
          CMakeExtension.class, customTasks);

      TestCMakeToolchain.register("Toolchain0", extension);
      TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);

      CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      java.util.Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
      assertNotNull(toolchains);

      for (final CMakeResolvedLibrary library : toolchains[0].getInterfaceLibraries()) {
        for (final String buildConfig : toolchains[0].getBuildConfigs()) {
          final CMakeModuleFile file = new CMakeModuleFile(library, toolchains[0], buildConfig, project);
          assertNotNull(file);

          File outputFile = new File(tempDir, "config-%s-%s.cmake".formatted(toolchains[0].getName(), buildConfig));
          try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            file.writeTo(fos);
          }

          assertNotNull(outputFile);
          assertTrue(outputFile.exists());
        }
      }
    } finally {
      deleteRecursively(tempDir);
    }
  }

  /**
   * Covers buildModel(): library.getLinkVariant() == CMakeLinkVariant.SHARED branch (true),
   * operatingSystem.isLinux() branch (true on Linux), operatingSystem.isWindows() branch (false on Linux).
   */
  @Test
  void testWriteToWithSharedLibrary() throws IOException, URISyntaxException {
    File tempDir = createTempDir();
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
      CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
          CMakeExtension.class, customTasks);

      TestCMakeToolchain.register("Toolchain0", extension);
      TestCMakeBinaryLibrary.register("SharedLib0", extension, CMakeBuildVariant.SHARED);

      CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      java.util.Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
      assertNotNull(toolchains);
      assertEquals(1, toolchains.length);
      assertEquals(1, toolchains[0].getSharedLibraries().size());

      for (final CMakeResolvedLibrary library : toolchains[0].getSharedLibraries()) {
        for (final String buildConfig : toolchains[0].getBuildConfigs()) {
          final CMakeModuleFile file = new CMakeModuleFile(library, toolchains[0], buildConfig, project);
          assertNotNull(file);

          File outputFile = new File(tempDir,
              "shared-%s-%s.cmake".formatted(toolchains[0].getName(), buildConfig));
          try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            file.writeTo(fos);
          }
          assertTrue(outputFile.exists());
          assertTrue(outputFile.length() > 0);
        }
      }
    } finally {
      deleteRecursively(tempDir);
    }
  }

  /**
   * Covers buildModel(): library.getLinkVariant() == CMakeLinkVariant.STATIC branch (true).
   */
  @Test
  void testWriteToWithStaticLibrary() throws IOException, URISyntaxException {
    File tempDir = createTempDir();
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
      CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
          CMakeExtension.class, customTasks);

      TestCMakeToolchain.register("Toolchain0", extension);
      TestCMakeBinaryLibrary.register("StaticLib0", extension, CMakeBuildVariant.STATIC);

      CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      java.util.Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
      assertNotNull(toolchains);
      assertEquals(1, toolchains.length);
      assertEquals(1, toolchains[0].getStaticLibraries().size());

      for (final CMakeResolvedLibrary library : toolchains[0].getStaticLibraries()) {
        for (final String buildConfig : toolchains[0].getBuildConfigs()) {
          final CMakeModuleFile file = new CMakeModuleFile(library, toolchains[0], buildConfig, project);
          assertNotNull(file);

          File outputFile = new File(tempDir,
              "static-%s-%s.cmake".formatted(toolchains[0].getName(), buildConfig));
          try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            file.writeTo(fos);
          }
          assertTrue(outputFile.exists());
          assertTrue(outputFile.length() > 0);
        }
      }
    } finally {
      deleteRecursively(tempDir);
    }
  }

  /**
   * Covers buildModel(): for loops for allProjectDependencies, publicProjectDepTargets,
   * publicPackageLinkLibraries when non-empty.
   */
  @Test
  void testWriteToWithInterfaceLibraryWithProjectAndPackageDeps() throws IOException, URISyntaxException {
    File tempDir = createTempDir();
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
      CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
          CMakeExtension.class, customTasks);

      TestCMakeToolchain.register("Toolchain0", extension);
      TestCMakePackage.register("Package0", extension);

      NamedDomainObjectProvider<CMakeLibrary> libProvider =
          TestCMakeInterfaceLibrary.register("InterfaceLib0", extension);
      libProvider.configure((lib) -> {
        // PUBLIC project dep (same project) -> covers allProjectDependencies and publicProjectDepTargets loops
        // PUBLIC package dep -> covers publicPackageLinkLibraries loop
        lib.getLinking().link(Arrays.asList(
            new CMakeLibraryDependencies("AnotherLib").variant(CMakeLinkVariant.INTERFACE),
            new CMakeLibraryDependencies("target").from("Package0")));
      });

      CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      java.util.Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
      assertNotNull(toolchains);
      assertEquals(1, toolchains.length);
      assertEquals(1, toolchains[0].getInterfaceLibraries().size());

      for (final CMakeResolvedLibrary library : toolchains[0].getInterfaceLibraries()) {
        for (final String buildConfig : toolchains[0].getBuildConfigs()) {
          final CMakeModuleFile file = new CMakeModuleFile(library, toolchains[0], buildConfig, project);
          assertNotNull(file);

          File outputFile = new File(tempDir,
              "deps-%s-%s.cmake".formatted(toolchains[0].getName(), buildConfig));
          try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            file.writeTo(fos);
          }
          assertTrue(outputFile.exists());
          assertTrue(outputFile.length() > 0);
        }
      }
    } finally {
      deleteRecursively(tempDir);
    }
  }

  private File createTempDir() throws IOException {
    File tempDir = new File(System.getProperty("java.io.tmpdir"),
        "cmake-test-" + System.currentTimeMillis());
    if (!tempDir.mkdirs()) {
      throw new IOException("Failed to create directory: " + tempDir.getAbsolutePath());
    }
    return tempDir;
  }

  private void deleteRecursively(File file) {
    if (file.isDirectory()) {
      File[] children = file.listFiles();
      if (children != null) {
        for (File child : children) {
          deleteRecursively(child);
        }
      }
    }
    assertTrue(file.delete() || !file.exists());
  }

  private void assertTrue(boolean condition) {
    if (!condition) {
      throw new AssertionError("Expected true but was false");
    }
  }
}
