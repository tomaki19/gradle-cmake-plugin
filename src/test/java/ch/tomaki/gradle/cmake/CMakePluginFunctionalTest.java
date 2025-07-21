/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
