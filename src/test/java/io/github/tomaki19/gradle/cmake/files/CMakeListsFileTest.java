/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;

import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeApplication;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibraryLinkSpec;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibraryLinkSpec;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeApplication;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeBinaryLibrary;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeInterfaceLibrary;
import io.github.tomaki19.gradle.cmake.helper.TestCMakePackage;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeTest;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeBuildVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeResolver;

class CMakeListsFileTest {

  @Test
  void testName() {
    assertEquals("CMakeLists.txt", CMakeListsFile.NAME);
  }

  @Test
  void testConstructor() throws IOException {
    File tempDir = createTempDir();
    try {
      org.gradle.api.Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      CMakeListsFile file = new CMakeListsFile(java.util.Collections.emptyList(), project);
      assertNotNull(file);
    } finally {
      deleteRecursively(tempDir);
    }
  }

  @Test
  void testWriteTo() throws IOException {
    File tempDir = createTempDir();
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      CMakeListsFile file = new CMakeListsFile(java.util.Collections.emptyList(), project);

      File outputFile = new File(tempDir, "CMakeLists.txt");
      try (FileOutputStream fos = new FileOutputStream(outputFile)) {
        file.writeTo(fos);
      }

      assertNotNull(outputFile);
      assertTrue(outputFile.exists());
    } finally {
      deleteRecursively(tempDir);
    }
  }

  @Test
  void testWriteToWithInterfaceLibrary() throws IOException, URISyntaxException {
    File tempDir = createTempDir();
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
          CMakeExtension.class, project.getTasks());
      TestCMakeToolchain.register("Toolchain0", extension);
      TestCMakeInterfaceLibrary.register("InterfaceLib0", extension);

      CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      CMakeListsFile file = new CMakeListsFile(results, project);
      File outputFile = new File(tempDir, "CMakeLists.txt");
      try (FileOutputStream fos = new FileOutputStream(outputFile)) {
        file.writeTo(fos);
      }
      assertTrue(outputFile.exists());
      assertTrue(outputFile.length() > 0);
    } finally {
      deleteRecursively(tempDir);
    }
  }

  @Test
  void testWriteToWithStaticLibrary() throws IOException, URISyntaxException {
    File tempDir = createTempDir();
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
          CMakeExtension.class, project.getTasks());

      TestCMakeToolchain.register("Toolchain0", extension);
      TestCMakeBinaryLibrary.register("StaticLib0", extension, CMakeBuildVariant.STATIC);

      CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      CMakeListsFile file = new CMakeListsFile(results, project);
      File outputFile = new File(tempDir, "CMakeLists.txt");
      try (FileOutputStream fos = new FileOutputStream(outputFile)) {
        file.writeTo(fos);
      }
      assertTrue(outputFile.exists());
      assertTrue(outputFile.length() > 0);
    } finally {
      deleteRecursively(tempDir);
    }
  }

  @Test
  void testWriteToWithSharedLibrary() throws IOException, URISyntaxException {
    File tempDir = createTempDir();
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
          CMakeExtension.class, project.getTasks());
      TestCMakeToolchain.register("Toolchain0", extension);
      TestCMakeBinaryLibrary.register("SharedLib0", extension, CMakeBuildVariant.SHARED);

      CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      CMakeListsFile file = new CMakeListsFile(results, project);
      File outputFile = new File(tempDir, "CMakeLists.txt");
      try (FileOutputStream fos = new FileOutputStream(outputFile)) {
        file.writeTo(fos);
      }
      assertTrue(outputFile.exists());
      assertTrue(outputFile.length() > 0);
    } finally {
      deleteRecursively(tempDir);
    }
  }

  @Test
  void testWriteToWithApplication() throws IOException, URISyntaxException {
    File tempDir = createTempDir();
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
          CMakeExtension.class, project.getTasks());

      TestCMakeToolchain.register("Toolchain0", extension);
      TestCMakeApplication.register("App0", extension);

      CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      CMakeListsFile file = new CMakeListsFile(results, project);
      File outputFile = new File(tempDir, "CMakeLists.txt");
      try (FileOutputStream fos = new FileOutputStream(outputFile)) {
        file.writeTo(fos);
      }
      assertTrue(outputFile.exists());
      assertTrue(outputFile.length() > 0);
    } finally {
      deleteRecursively(tempDir);
    }
  }

  @Test
  void testWriteToWithTest() throws IOException, URISyntaxException {
    File tempDir = createTempDir();
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
          CMakeExtension.class, project.getTasks());

      TestCMakeToolchain.register("Toolchain0", extension);
      TestCMakeTest.register("Test0", extension);

      CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      CMakeListsFile file = new CMakeListsFile(results, project);
      File outputFile = new File(tempDir, "CMakeLists.txt");
      try (FileOutputStream fos = new FileOutputStream(outputFile)) {
        file.writeTo(fos);
      }
      assertTrue(outputFile.exists());
      assertTrue(outputFile.length() > 0);
    } finally {
      deleteRecursively(tempDir);
    }
  }

  /**
   * Covers populateLinkModel: hasPrivateLinking C=true (privateLinkOptions
   * non-empty),
   * hasPublicLinking E=true (publicPackageDeps non-empty).
   * Also covers buildLinkLibraries: options loop entered, packageDeps loop
   * entered.
   */
  @Test
  void testWriteToWithBinaryLibraryWithPrivateLinkOptionAndPublicPackageDep()
      throws IOException, URISyntaxException {
    File tempDir = createTempDir();
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
          CMakeExtension.class, project.getTasks());

      TestCMakeToolchain.register("Toolchain0", extension);
      TestCMakePackage.register("Package0", extension);

      NamedDomainObjectProvider<CMakeLibrary> libProvider = TestCMakeBinaryLibrary.register("StaticLib0", extension,
          CMakeBuildVariant.STATIC);
      libProvider.configure((lib) -> {
        lib.getLinking().options(Map.of(CMakeLibraryLinkSpec.VISIBILITY, "PRIVATE"), "-lm");
        lib.getLinking().link(Map.of(CMakeLibraryLinkSpec.PROJECT, "Package0"), "target");
      });

      CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      CMakeListsFile file = new CMakeListsFile(results, project);
      File outputFile = new File(tempDir, "CMakeLists.txt");
      try (FileOutputStream fos = new FileOutputStream(outputFile)) {
        file.writeTo(fos);
      }
      assertTrue(outputFile.exists());
      assertTrue(outputFile.length() > 0);
    } finally {
      deleteRecursively(tempDir);
    }
  }

  /**
   * Covers populateLinkModel: hasPrivateLinking B=true (privatePackageDeps
   * non-empty),
   * hasPublicLinking F=true (publicLinkOptions non-empty).
   */
  @Test
  void testWriteToWithBinaryLibraryWithPrivatePackageDepAndPublicLinkOption()
      throws IOException, URISyntaxException {
    File tempDir = createTempDir();
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
          CMakeExtension.class, project.getTasks());

      TestCMakeToolchain.register("Toolchain0", extension);
      TestCMakePackage.register("Package0", extension);

      NamedDomainObjectProvider<CMakeLibrary> libProvider = TestCMakeBinaryLibrary.register("StaticLib1", extension,
          CMakeBuildVariant.STATIC);
      libProvider.configure((lib) -> {
        lib.getLinking().link(Map.of(CMakeLibraryLinkSpec.PROJECT, "Package0",
            CMakeLibraryLinkSpec.VISIBILITY, "PRIVATE"), "target");
        lib.getLinking().options(Map.of(CMakeLibraryLinkSpec.VISIBILITY, "PUBLIC"), "-lstdc++");
      });

      CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      CMakeListsFile file = new CMakeListsFile(results, project);
      File outputFile = new File(tempDir, "CMakeLists.txt");
      try (FileOutputStream fos = new FileOutputStream(outputFile)) {
        file.writeTo(fos);
      }
      assertTrue(outputFile.exists());
      assertTrue(outputFile.length() > 0);
    } finally {
      deleteRecursively(tempDir);
    }
  }

  /**
   * Covers populateCompileModel: hasPublicCompileOptions=true,
   * hasPublicCompileDefinitions=true.
   * Covers populateLinkModel: hasPrivateLinking A=true (privateProjectDeps
   * non-empty),
   * hasPublicLinking D=true (publicProjectDeps non-empty).
   * Covers buildFilteredProjectIncludes: loop entered, if-false branch (same
   * project filtered).
   * Covers buildLinkLibraries: projectDeps loop entered.
   */
  @Test
  void testWriteToWithBinaryLibraryWithProjectDepsAndCompileOptions()
      throws IOException, URISyntaxException {
    File tempDir = createTempDir();
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
          CMakeExtension.class, project.getTasks());

      TestCMakeToolchain.register("Toolchain0", extension);

      NamedDomainObjectProvider<CMakeLibrary> libProvider = TestCMakeBinaryLibrary.register("StaticLib2", extension,
          CMakeBuildVariant.STATIC);
      libProvider.configure((lib) -> {
        lib.getCompiling().options(Map.of(), "-Wall");
        lib.getCompiling().defines(Map.of(), "NDEBUG");
        lib.getLinking().link(Map.of(CMakeLibraryLinkSpec.LINK_VARIANT, "INTERFACE",
            CMakeLibraryLinkSpec.VISIBILITY, "PRIVATE"), "Dep1");
        lib.getLinking().link(Map.of(CMakeLibraryLinkSpec.LINK_VARIANT, "INTERFACE"), "Dep2");
      });

      CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      CMakeListsFile file = new CMakeListsFile(results, project);
      File outputFile = new File(tempDir, "CMakeLists.txt");
      try (FileOutputStream fos = new FileOutputStream(outputFile)) {
        file.writeTo(fos);
      }
      assertTrue(outputFile.exists());
      assertTrue(outputFile.length() > 0);
    } finally {
      deleteRecursively(tempDir);
    }
  }

  /**
   * Covers populateCompileModel: hasPrivateCompileOptions=true,
   * hasPrivateCompileDefinitions=true.
   * CMakeExecutableCompiling defaults to PRIVATE visibility.
   */
  @Test
  void testWriteToWithApplicationWithPrivateCompileOptions() throws IOException, URISyntaxException {
    File tempDir = createTempDir();
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
          CMakeExtension.class, project.getTasks());

      TestCMakeToolchain.register("Toolchain0", extension);

      NamedDomainObjectProvider<CMakeApplication> appProvider = TestCMakeApplication.register("App0", extension);
      appProvider.configure((app) -> {
        app.getCompiling().options(Map.of(CMakeLibraryLinkSpec.VISIBILITY, "PRIVATE"), "-g");
        app.getCompiling().defines(Map.of(CMakeLibraryLinkSpec.VISIBILITY, "PRIVATE"), "DEBUG");
      });

      CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      CMakeListsFile file = new CMakeListsFile(results, project);
      File outputFile = new File(tempDir, "CMakeLists.txt");
      try (FileOutputStream fos = new FileOutputStream(outputFile)) {
        file.writeTo(fos);
      }
      assertTrue(outputFile.exists());
      assertTrue(outputFile.length() > 0);
    } finally {
      deleteRecursively(tempDir);
    }
  }

  /**
   * Covers buildInterfaceLibraryModel: hasInterfaceCompileOptions=true,
   * hasInterfaceCompileDefinitions=true, hasInterfaceLinking A=true
   * (publicProjectDeps).
   */
  @Test
  void testWriteToWithInterfaceLibraryWithCompileOptionsAndPublicProjectDep()
      throws IOException, URISyntaxException {
    File tempDir = createTempDir();
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
          CMakeExtension.class, project.getTasks());

      TestCMakeToolchain.register("Toolchain0", extension);

      NamedDomainObjectProvider<CMakeLibrary> libProvider = TestCMakeInterfaceLibrary.register("InterfaceLib0",
          extension);
      libProvider.configure((lib) -> {
        lib.getCompiling().options(Map.of(), "-Wall");
        lib.getCompiling().defines(Map.of(), "NDEBUG");
        lib.getLinking().link(Map.of(CMakeLibraryLinkSpec.LINK_VARIANT, "INTERFACE"), "AnotherLib");
      });

      CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      CMakeListsFile file = new CMakeListsFile(results, project);
      File outputFile = new File(tempDir, "CMakeLists.txt");
      try (FileOutputStream fos = new FileOutputStream(outputFile)) {
        file.writeTo(fos);
      }
      assertTrue(outputFile.exists());
      assertTrue(outputFile.length() > 0);
    } finally {
      deleteRecursively(tempDir);
    }
  }

  /**
   * Covers buildInterfaceLibraryModel: hasInterfaceLinking B=true
   * (publicPackageDeps non-empty).
   * When publicProjectDeps is empty and publicPackageDeps is non-empty, B=true
   * branch is reached.
   */
  @Test
  void testWriteToWithInterfaceLibraryWithPublicPackageDep() throws IOException, URISyntaxException {
    File tempDir = createTempDir();
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
          CMakeExtension.class, project.getTasks());

      TestCMakeToolchain.register("Toolchain0", extension);
      TestCMakePackage.register("Package0", extension);

      NamedDomainObjectProvider<CMakeLibrary> libProvider = TestCMakeInterfaceLibrary.register("InterfaceLib0",
          extension);
      libProvider.configure((lib) -> {
        lib.getLinking().link(Map.of(CMakeLibraryLinkSpec.PROJECT, "Package0"), "target");
      });

      CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      CMakeListsFile file = new CMakeListsFile(results, project);
      File outputFile = new File(tempDir, "CMakeLists.txt");
      try (FileOutputStream fos = new FileOutputStream(outputFile)) {
        file.writeTo(fos);
      }
      assertTrue(outputFile.exists());
      assertTrue(outputFile.length() > 0);
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

}
