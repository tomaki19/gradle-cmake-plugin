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

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomPackageTaskProto;
import io.github.tomaki19.gradle.cmake.extension.api.CMakePackageType;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeExtension;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeToolchain;

class CMakeExtensionCustomPackageTest {

  @Test
  void testRegisterPackageRuntimeRegistersForAllToolchainsAndBuildConfigs() {
    final Project project = ProjectBuilder.builder().build();
    final MockCMakeExtension extension = new MockCMakeExtension(project.getObjects());
    MockCMakeToolchain.registerWithBuildConfigs("tc1", extension, "Debug", "Release");
    MockCMakeToolchain.registerWithBuildConfigs("tc2", extension, "Debug");

    extension.registerPackageRuntime("myPkg", t -> {
    });

    final Map<String, ?> protos = extension.getCustomPackageTaskProtos();
    assertEquals(2, protos.size());
    assertTrue(protos.containsKey("tc1"));
    assertTrue(protos.containsKey("tc2"));
    assertEquals(2, extension.getCustomPackageTaskProtos().get("tc1").size());
    assertEquals(1, extension.getCustomPackageTaskProtos().get("tc2").size());
  }

  @Test
  void testRegisterPackageRuntimeHasRuntimeType() {
    final Project project = ProjectBuilder.builder().build();
    final MockCMakeExtension extension = new MockCMakeExtension(project.getObjects());
    MockCMakeToolchain.registerWithBuildConfigs("tc1", extension, "Debug");

    extension.registerPackageRuntime("myPkg", t -> {
    });

    final CMakeCustomPackageTaskProto proto =
        extension.getCustomPackageTaskProtos().get("tc1").keySet().iterator().next();
    assertEquals(CMakePackageType.RUNTIME, proto.getPackageType());
    assertEquals("myPkg", proto.getName());
    assertFalse(proto.isComponentScoped());
  }

  @Test
  void testRegisterPackageDevelopmentHasDevelopmentType() {
    final Project project = ProjectBuilder.builder().build();
    final MockCMakeExtension extension = new MockCMakeExtension(project.getObjects());
    MockCMakeToolchain.registerWithBuildConfigs("tc1", extension, "Debug");

    extension.registerPackageDevelopment("myPkg", t -> {
    });

    final CMakeCustomPackageTaskProto proto =
        extension.getCustomPackageTaskProtos().get("tc1").keySet().iterator().next();
    assertEquals(CMakePackageType.DEVELOPMENT, proto.getPackageType());
    assertFalse(proto.isComponentScoped());
  }

  @Test
  void testRegisterPackageRuntimeWithToolchainFilter() {
    final Project project = ProjectBuilder.builder().build();
    final MockCMakeExtension extension = new MockCMakeExtension(project.getObjects());
    MockCMakeToolchain.registerWithBuildConfigs("tc1", extension, "Debug");
    MockCMakeToolchain.registerWithBuildConfigs("tc2", extension, "Debug");

    extension.registerPackageRuntime("myPkg", Collections.singletonList("tc1"), t -> {
    });

    final Map<String, ?> protos = extension.getCustomPackageTaskProtos();
    assertEquals(1, protos.size());
    assertTrue(protos.containsKey("tc1"));
    assertFalse(protos.containsKey("tc2"));
  }

  @Test
  void testRegisterPackageRuntimeWithToolchainAndBuildConfigFilter() {
    final Project project = ProjectBuilder.builder().build();
    final MockCMakeExtension extension = new MockCMakeExtension(project.getObjects());
    MockCMakeToolchain.registerWithBuildConfigs("tc1", extension, "Debug", "Release", "RelWithDebInfo");

    extension.registerPackageRuntime("myPkg", Collections.singletonList("tc1"),
        Arrays.asList("Debug", "Release"), t -> {
        });

    final Map<CMakeCustomPackageTaskProto, ?> tc1Protos =
        extension.getCustomPackageTaskProtos().get("tc1");
    assertNotNull(tc1Protos);
    assertEquals(2, tc1Protos.size());
    final List<String> configs = tc1Protos.keySet().stream()
        .map(CMakeCustomPackageTaskProto::getBuildConfig)
        .sorted()
        .toList();
    assertEquals(Arrays.asList("Debug", "Release"), configs);
  }

  @Test
  void testRegisterPackageDevelopmentWithToolchainFilter() {
    final Project project = ProjectBuilder.builder().build();
    final MockCMakeExtension extension = new MockCMakeExtension(project.getObjects());
    MockCMakeToolchain.registerWithBuildConfigs("tc1", extension, "Debug");
    MockCMakeToolchain.registerWithBuildConfigs("tc2", extension, "Debug");

    extension.registerPackageDevelopment("myPkg", Collections.singletonList("tc1"), t -> {
    });

    assertEquals(1, extension.getCustomPackageTaskProtos().size());
    assertTrue(extension.getCustomPackageTaskProtos().containsKey("tc1"));
  }

  @Test
  void testRegisterPackageDevelopmentWithToolchainAndBuildConfigFilter() {
    final Project project = ProjectBuilder.builder().build();
    final MockCMakeExtension extension = new MockCMakeExtension(project.getObjects());
    MockCMakeToolchain.registerWithBuildConfigs("tc1", extension, "Debug", "Release");

    extension.registerPackageDevelopment("myPkg", Collections.singletonList("tc1"),
        Collections.singletonList("Release"), t -> {
        });

    final Map<CMakeCustomPackageTaskProto, ?> tc1Protos =
        extension.getCustomPackageTaskProtos().get("tc1");
    assertNotNull(tc1Protos);
    assertEquals(1, tc1Protos.size());
    assertEquals("Release", tc1Protos.keySet().iterator().next().getBuildConfig());
  }

  @Test
  void testRegisterPackageRuntimeForComponentsIsComponentScoped() {
    final Project project = ProjectBuilder.builder().build();
    final MockCMakeExtension extension = new MockCMakeExtension(project.getObjects());
    MockCMakeToolchain.registerWithBuildConfigs("tc1", extension, "Debug");

    extension.registerPackageRuntimeForComponents("myPkg",
        Arrays.asList("libA", "appB"), t -> {
        });

    final CMakeCustomPackageTaskProto proto =
        extension.getCustomPackageTaskProtos().get("tc1").keySet().iterator().next();
    assertTrue(proto.isComponentScoped());
    assertTrue(proto.matchesComponent("libA"));
    assertTrue(proto.matchesComponent("appB"));
    assertFalse(proto.matchesComponent("libC"));
    assertEquals(CMakePackageType.RUNTIME, proto.getPackageType());
  }

  @Test
  void testRegisterPackageDevelopmentForComponentsIsComponentScoped() {
    final Project project = ProjectBuilder.builder().build();
    final MockCMakeExtension extension = new MockCMakeExtension(project.getObjects());
    MockCMakeToolchain.registerWithBuildConfigs("tc1", extension, "Debug");

    extension.registerPackageDevelopmentForComponents("myPkg",
        Collections.singletonList("libA"), t -> {
        });

    final CMakeCustomPackageTaskProto proto =
        extension.getCustomPackageTaskProtos().get("tc1").keySet().iterator().next();
    assertTrue(proto.isComponentScoped());
    assertTrue(proto.matchesComponent("libA"));
    assertFalse(proto.matchesComponent("libB"));
    assertEquals(CMakePackageType.DEVELOPMENT, proto.getPackageType());
  }

  @Test
  void testNonComponentScopedProtoMatchesAllComponents() {
    final Project project = ProjectBuilder.builder().build();
    final MockCMakeExtension extension = new MockCMakeExtension(project.getObjects());
    MockCMakeToolchain.registerWithBuildConfigs("tc1", extension, "Debug");

    extension.registerPackageRuntime("myPkg", t -> {
    });

    final CMakeCustomPackageTaskProto proto =
        extension.getCustomPackageTaskProtos().get("tc1").keySet().iterator().next();
    assertFalse(proto.isComponentScoped());
    assertTrue(proto.matchesComponent("anything"));
    assertTrue(proto.matchesComponent("anyOtherThing"));
  }

  @Test
  void testRegisterPackageWithNoToolchainsRegistersNothing() {
    final Project project = ProjectBuilder.builder().build();
    final MockCMakeExtension extension = new MockCMakeExtension(project.getObjects());

    extension.registerPackageRuntime("myPkg", t -> {
    });
    extension.registerPackageDevelopment("myPkg", t -> {
    });

    assertTrue(extension.getCustomPackageTaskProtos().isEmpty());
  }

  @Test
  void testRegisterPackageDoesNotAffectExecTaskProtos() {
    final Project project = ProjectBuilder.builder().build();
    final MockCMakeExtension extension = new MockCMakeExtension(project.getObjects());
    MockCMakeToolchain.registerWithBuildConfigs("tc1", extension, "Debug");

    extension.registerPackageRuntime("myPkg", t -> {
    });

    assertTrue(extension.getCustomTaskProtos().isEmpty(),
        "exec task protos must not be affected by package registration");
  }

  @Test
  void testRegisterExecDoesNotAffectPackageTaskProtos() {
    final Project project = ProjectBuilder.builder().build();
    final MockCMakeExtension extension = new MockCMakeExtension(project.getObjects());
    MockCMakeToolchain.registerWithBuildConfigs("tc1", extension, "Debug");

    extension.register("myTask", t -> {
    });

    assertTrue(extension.getCustomPackageTaskProtos().isEmpty(),
        "package task protos must not be affected by exec registration");
  }

}
