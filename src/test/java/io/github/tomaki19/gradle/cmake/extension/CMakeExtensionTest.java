/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomTaskProto;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeApplication;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeExtension;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeToolchain;

class CMakeExtensionTest {

  @Test
  void testExtensionValues() {
    assertEquals("cmake", MockCMakeExtension.NAME);
  }

  @Test
  void testConstructor() {
    final Project project = ProjectBuilder.builder().build();
    final MockCMakeExtension extension = new MockCMakeExtension(project.getObjects());
    assertNotNull(extension);
  }

  @Test
  void testMockCMakeApplication() {
    final Project project = ProjectBuilder.builder().build();
    final MockCMakeApplication app = new MockCMakeApplication("mockApp", project.getObjects());
    assertNotNull(app);
    assertEquals("mockApp", app.getName());
  }

  @Test
  void testRegisterMethods() {
    final Project project = ProjectBuilder.builder().build();
    final MockCMakeExtension extension = new MockCMakeExtension(project.getObjects());
    // No exception should be thrown for each overload
    extension.register("myTask", t -> {
    });
    extension.register("myTask", Collections.singletonList("myToolchain"), t -> {
    });
    extension.register("myTask", Collections.singletonList("myToolchain"),
        Collections.singletonList("debug"), t -> {
        });
  }

  @Test
  void testRegisterRegistersForAllToolchainsAndBuildConfigs() {
    final Project project = ProjectBuilder.builder().build();
    final MockCMakeExtension extension = new MockCMakeExtension(project.getObjects());
    MockCMakeToolchain.registerWithBuildConfigs("tc1", extension, "Debug", "Release");
    MockCMakeToolchain.registerWithBuildConfigs("tc2", extension, "Debug");

    extension.register("myTask", t -> {
    });

    final Map<String, ?> protos = extension.getCustomTaskProtos();
    assertEquals(2, protos.size(), "both toolchains must be registered");
    assertTrue(protos.containsKey("tc1"));
    assertTrue(protos.containsKey("tc2"));
    final Map<CMakeCustomTaskProto, ?> tc1Protos = extension.getCustomTaskProtos().get("tc1");
    assertEquals(2, tc1Protos.size(), "tc1 must have entries for Debug and Release");
    final Map<CMakeCustomTaskProto, ?> tc2Protos = extension.getCustomTaskProtos().get("tc2");
    assertEquals(1, tc2Protos.size(), "tc2 must have entry for Debug only");
  }

  @Test
  void testRegisterWithToolchainFilterOnlyRegistersMatchingToolchain() {
    final Project project = ProjectBuilder.builder().build();
    final MockCMakeExtension extension = new MockCMakeExtension(project.getObjects());
    MockCMakeToolchain.registerWithBuildConfigs("tc1", extension, "Debug", "Release");
    MockCMakeToolchain.registerWithBuildConfigs("tc2", extension, "Debug");

    extension.register("myTask", Collections.singletonList("tc1"), t -> {
    });

    final Map<String, ?> protos = extension.getCustomTaskProtos();
    assertEquals(1, protos.size(), "only tc1 must be registered");
    assertTrue(protos.containsKey("tc1"));
    assertFalse(protos.containsKey("tc2"));
  }

  @Test
  void testRegisterWithNonMatchingToolchainRegistersNothing() {
    final Project project = ProjectBuilder.builder().build();
    final MockCMakeExtension extension = new MockCMakeExtension(project.getObjects());
    MockCMakeToolchain.registerWithBuildConfigs("tc1", extension, "Debug");

    extension.register("myTask", Collections.singletonList("unknown"), t -> {
    });

    assertTrue(extension.getCustomTaskProtos().isEmpty(), "nothing must be registered for unknown toolchain");
  }

  @Test
  void testRegisterWithBuildConfigFilterOnlyRegistersMatchingConfigs() {
    final Project project = ProjectBuilder.builder().build();
    final MockCMakeExtension extension = new MockCMakeExtension(project.getObjects());
    MockCMakeToolchain.registerWithBuildConfigs("tc1", extension, "Debug", "Release", "RelWithDebInfo");

    extension.register("myTask", Collections.singletonList("tc1"),
        Arrays.asList("Debug", "Release"), t -> {
        });

    final Map<CMakeCustomTaskProto, ?> tc1Protos = extension.getCustomTaskProtos().get("tc1");
    assertNotNull(tc1Protos);
    assertEquals(2, tc1Protos.size(), "only Debug and Release configs must be registered");
    final List<String> registeredConfigs = tc1Protos.keySet().stream()
        .map(CMakeCustomTaskProto::getBuildConfig)
        .sorted()
        .toList();
    assertEquals(Arrays.asList("Debug", "Release"), registeredConfigs);
  }

  @Test
  void testRegisterWithNonMatchingBuildConfigRegistersNothing() {
    final Project project = ProjectBuilder.builder().build();
    final MockCMakeExtension extension = new MockCMakeExtension(project.getObjects());
    MockCMakeToolchain.registerWithBuildConfigs("tc1", extension, "Debug");

    extension.register("myTask", Collections.singletonList("tc1"),
        Collections.singletonList("Release"), t -> {
        });

    assertTrue(extension.getCustomTaskProtos().isEmpty(),
        "nothing must be registered when build config does not match");
  }

  @Test
  void testRegisterWithMultipleToolchainsAndBuildConfigFilter() {
    final Project project = ProjectBuilder.builder().build();
    final MockCMakeExtension extension = new MockCMakeExtension(project.getObjects());
    MockCMakeToolchain.registerWithBuildConfigs("tc1", extension, "Debug", "Release");
    MockCMakeToolchain.registerWithBuildConfigs("tc2", extension, "Debug", "Release");

    extension.register("myTask", Arrays.asList("tc1", "tc2"),
        Collections.singletonList("Debug"), t -> {
        });

    final Map<String, ?> protos = extension.getCustomTaskProtos();
    assertEquals(2, protos.size());
    assertEquals(1, extension.getCustomTaskProtos().get("tc1").size(),
        "tc1 must have only Debug");
    assertEquals(1, extension.getCustomTaskProtos().get("tc2").size(),
        "tc2 must have only Debug");
  }

  @Test
  void testRegisterWithEmptyToolchainsRegistersNothing() {
    final Project project = ProjectBuilder.builder().build();
    final MockCMakeExtension extension = new MockCMakeExtension(project.getObjects());
    // No toolchains registered

    extension.register("myTask", t -> {
    });

    assertTrue(extension.getCustomTaskProtos().isEmpty(),
        "nothing must be registered when no toolchains exist");
  }

}
