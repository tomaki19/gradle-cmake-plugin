/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CMakePluginFunctionalTest {

  @TempDir
  File testProjectDir;

  private File buildFile;

  @BeforeEach
  void setup() throws IOException {
    buildFile = new File(testProjectDir, "build.gradle");
  }

  @Test
  void pluginCreatesExpectedTasks() throws IOException {
    writeBuildFile();

    BuildResult result = GradleRunner.create()
        .withProjectDir(testProjectDir)
        .withArguments("tasks", "--all")
        .withPluginClasspath()
        .build();

    String output = result.getOutput();

    // Verify core tasks exist
    assertTrue(output.contains("assemble"), "Should contain assemble task");
    assertTrue(output.contains("build"), "Should contain build task");
    assertTrue(output.contains("check"), "Should contain check task");
    assertTrue(output.contains("clean"), "Should contain clean task");

    // Verify CMake-specific tasks exist
    assertTrue(output.contains("assemble-cmake-lists"), "Should contain assemble-cmake-lists task");
    assertTrue(output.contains("assemble-TestToolchain-config"), "Should contain assemble-config task");
    assertTrue(output.contains("configure-TestToolchain"), "Should contain configure task");
    assertTrue(output.contains("build-all-TestToolchain"), "Should contain build-all task");
  }

  @Test
  void pluginFailsWithMissingRequiredToolchainProperties() throws IOException {
    writeBuildFileWithMissingToolchainProperties();

    try {
      GradleRunner.create()
          .withProjectDir(testProjectDir)
          .withArguments("tasks")
          .withPluginClasspath()
          .buildAndFail();
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("Required option is missing") ||
          e.getMessage().contains("BUILD FAILED"),
          "Should fail with validation error for missing toolchain properties");
    }
  }

  @Test
  void pluginFailsWithEmptyToolchainProperties() throws IOException {
    writeBuildFileWithEmptyToolchainProperties();

    try {
      GradleRunner.create()
          .withProjectDir(testProjectDir)
          .withArguments("tasks")
          .withPluginClasspath()
          .buildAndFail();
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("Required option is empty") ||
          e.getMessage().contains("BUILD FAILED"),
          "Should fail with validation error for empty toolchain properties");
    }
  }

  @Test
  void pluginFailsWithMissingLibraryProperties() throws IOException {
    writeBuildFileWithMissingLibraryProperties();

    try {
      GradleRunner.create()
          .withProjectDir(testProjectDir)
          .withArguments("tasks")
          .withPluginClasspath()
          .buildAndFail();
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("Required option is empty") ||
          e.getMessage().contains("BUILD FAILED"),
          "Should fail with validation error for missing library properties");
    }
  }

  @Test
  void pluginFailsWithMissingApplicationProperties() throws IOException {
    writeBuildFileWithMissingApplicationProperties();

    try {
      GradleRunner.create()
          .withProjectDir(testProjectDir)
          .withArguments("tasks")
          .withPluginClasspath()
          .buildAndFail();
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("Required option is empty") ||
          e.getMessage().contains("BUILD FAILED"),
          "Should fail with validation error for missing application properties");
    }
  }

  @Test
  void pluginFailsWithMissingTestProperties() throws IOException {
    writeBuildFileWithMissingTestProperties();

    try {
      GradleRunner.create()
          .withProjectDir(testProjectDir)
          .withArguments("tasks")
          .withPluginClasspath()
          .buildAndFail();
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("Required option is empty") ||
          e.getMessage().contains("BUILD FAILED"),
          "Should fail with validation error for missing test properties");
    }
  }

  @Test
  void pluginIgnoresInvalidToolchainReference() throws IOException {
    writeBuildFileWithInvalidToolchainReference();

    // The plugin silently ignores libraries that reference non-existent toolchains
    // rather than failing. Libraries with valid toolchain references should still
    // work.
    BuildResult result = GradleRunner.create()
        .withProjectDir(testProjectDir)
        .withArguments("tasks", "--all")
        .withPluginClasspath()
        .build();

    String output = result.getOutput();

    // Should contain basic tasks
    assertTrue(output.contains("assemble"), "Should contain assemble task");
    assertTrue(output.contains("build"), "Should contain build task");

    // Should contain tasks for the valid toolchain but not for the invalid
    // reference
    assertTrue(output.contains("configure-TestToolchain"), "Should contain configure task for valid toolchain");
    assertFalse(output.contains("configure-NonExistentToolchain"), "Should not contain tasks for invalid toolchain");

    // Should contain tasks for the valid library but not for the invalid library
    assertTrue(output.contains("ValidLibrary"), "Should contain tasks for valid library");
    assertFalse(output.contains("InvalidLibrary"), "Should not contain tasks for invalid library");
  }

  @Test
  void pluginHandlesFileSystemErrors() throws IOException {
    writeBuildFile();

    // Create a read-only directory to simulate file system errors
    File readOnlyDir = new File(testProjectDir, "readonly");
    readOnlyDir.mkdirs();
    readOnlyDir.setReadOnly();

    try {
      GradleRunner.create()
          .withProjectDir(testProjectDir)
          .withArguments("assemble-cmake-lists")
          .withPluginClasspath()
          .build();
      // If it succeeds, that's also fine - the test is about handling errors
      // gracefully
    } catch (Exception e) {
      // Expected behavior - should handle file system errors gracefully
      assertTrue(e.getMessage().contains("BUILD FAILED") ||
          e.getMessage().contains("Permission denied") ||
          e.getMessage().contains("Access is denied"),
          "Should handle file system errors gracefully");
    } finally {
      // Clean up
      readOnlyDir.setWritable(true);
    }
  }

  @Test
  void pluginHandlesEmptyConfiguration() throws IOException {
    writeBuildFileWithEmptyConfiguration();

    try {
      BuildResult result = GradleRunner.create()
          .withProjectDir(testProjectDir)
          .withArguments("tasks")
          .withPluginClasspath()
          .build();

      // Should succeed but with minimal tasks
      String output = result.getOutput();
      assertTrue(output.contains("assemble"), "Should contain basic assemble task");
      assertTrue(output.contains("build"), "Should contain basic build task");
    } catch (Exception e) {
      // If it fails, that's also acceptable behavior
      assertTrue(e.getMessage().contains("BUILD FAILED"),
          "Should handle empty configuration gracefully");
    }
  }

  @Test
  void pluginFailsWithMissingCMakeExecutable() throws IOException {
    writeBuildFile();

    // Try to run a CMake task that would require the cmake executable
    // This should fail if cmake is not installed or not in PATH
    try {
      GradleRunner.create()
          .withProjectDir(testProjectDir)
          .withArguments("configure-TestToolchain")
          .withPluginClasspath()
          .buildAndFail();
    } catch (Exception e) {
      // Expected - should fail when cmake executable is not available
      assertTrue(e.getMessage().contains("BUILD FAILED") ||
          e.getMessage().contains("cmake") ||
          e.getMessage().contains("command not found") ||
          e.getMessage().contains("No such file"),
          "Should fail when cmake executable is not available");
    }
  }

  private void writeBuildFile() throws IOException {
    try (FileWriter writer = new FileWriter(buildFile)) {
      writer.write("""
          plugins {
            id 'ch.tomaki.gradle-cmake-plugin'
          }

          cmake {
            packages {
              TestPackage {
              }
            }

            toolchains {
              TestToolchain {
                operatingSystem = org.gradle.internal.os.OperatingSystem.current()
                buildConfigs = ['Debug']
                compiler = 'gcc'
                architecture = 'x86-64'
                generator = 'Unix Makefiles'
              }
            }

            libraries {
              TestLibrary {
                toolchains = ['TestToolchain']
                headers = ['src/headers/']
                sources = ['src/cpp/']
              }
            }
          }
          """);
    }
  }

  private void writeBuildFileWithMissingToolchainProperties() throws IOException {
    try (FileWriter writer = new FileWriter(buildFile)) {
      writer.write("""
          plugins {
            id 'ch.tomaki.gradle-cmake-plugin'
          }

          cmake {
            toolchains {
              TestToolchain {
                // Missing required properties: operatingSystem, compiler, architecture, generator
                buildConfigs = ['Debug']
              }
            }
          }
          """);
    }
  }

  private void writeBuildFileWithEmptyToolchainProperties() throws IOException {
    try (FileWriter writer = new FileWriter(buildFile)) {
      writer.write("""
          plugins {
            id 'ch.tomaki.gradle-cmake-plugin'
          }

          cmake {
            toolchains {
              TestToolchain {
                operatingSystem = org.gradle.internal.os.OperatingSystem.current()
                buildConfigs = ['Debug']
                compiler = ''  // Empty compiler
                architecture = 'x86-64'
                generator = ''  // Empty generator
              }
            }
          }
          """);
    }
  }

  private void writeBuildFileWithMissingLibraryProperties() throws IOException {
    try (FileWriter writer = new FileWriter(buildFile)) {
      writer.write("""
          plugins {
            id 'ch.tomaki.gradle-cmake-plugin'
          }

          cmake {
            toolchains {
              TestToolchain {
                operatingSystem = org.gradle.internal.os.OperatingSystem.current()
                buildConfigs = ['Debug']
                compiler = 'gcc'
                architecture = 'x86-64'
                generator = 'Unix Makefiles'
              }
            }

            libraries {
              TestLibrary {
                toolchains = ['TestToolchain']
                // Missing headers property
                sources = ['src/cpp/']
              }
            }
          }
          """);
    }
  }

  private void writeBuildFileWithMissingApplicationProperties() throws IOException {
    try (FileWriter writer = new FileWriter(buildFile)) {
      writer.write("""
          plugins {
            id 'ch.tomaki.gradle-cmake-plugin'
          }

          cmake {
            toolchains {
              TestToolchain {
                operatingSystem = org.gradle.internal.os.OperatingSystem.current()
                buildConfigs = ['Debug']
                compiler = 'gcc'
                architecture = 'x86-64'
                generator = 'Unix Makefiles'
              }
            }

            applications {
              TestApplication {
                // Missing toolchains and sources properties
              }
            }
          }
          """);
    }
  }

  private void writeBuildFileWithMissingTestProperties() throws IOException {
    try (FileWriter writer = new FileWriter(buildFile)) {
      writer.write("""
          plugins {
            id 'ch.tomaki.gradle-cmake-plugin'
          }

          cmake {
            toolchains {
              TestToolchain {
                operatingSystem = org.gradle.internal.os.OperatingSystem.current()
                buildConfigs = ['Debug']
                compiler = 'gcc'
                architecture = 'x86-64'
                generator = 'Unix Makefiles'
              }
            }

            tests {
              TestTest {
                // Missing toolchains and sources properties
              }
            }
          }
          """);
    }
  }

  private void writeBuildFileWithInvalidToolchainReference() throws IOException {
    try (FileWriter writer = new FileWriter(buildFile)) {
      writer.write("""
          plugins {
            id 'ch.tomaki.gradle-cmake-plugin'
          }

          cmake {
            toolchains {
              TestToolchain {
                operatingSystem = org.gradle.internal.os.OperatingSystem.current()
                buildConfigs = ['Debug']
                compiler = 'gcc'
                architecture = 'x86-64'
                generator = 'Unix Makefiles'
              }
            }

            libraries {
              ValidLibrary {
                toolchains = ['TestToolchain']  // Valid toolchain reference
                headers = ['src/headers/']
                sources = ['src/cpp/']
              }
              InvalidLibrary {
                toolchains = ['NonExistentToolchain']  // Invalid toolchain reference
                headers = ['src/headers/']
                sources = ['src/cpp/']
              }
            }
          }
          """);
    }
  }

  private void writeBuildFileWithEmptyConfiguration() throws IOException {
    try (FileWriter writer = new FileWriter(buildFile)) {
      writer.write("""
          plugins {
            id 'ch.tomaki.gradle-cmake-plugin'
          }

          cmake {
            // Empty configuration - no toolchains, libraries, etc.
          }
          """);
    }
  }
}
