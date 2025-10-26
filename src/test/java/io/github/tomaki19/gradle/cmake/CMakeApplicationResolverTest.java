/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake;

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
import io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomTaskProto;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeApplication;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeBinaryLibrary;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeDependencies;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeInterfaceLibrary;
import io.github.tomaki19.gradle.cmake.helper.TestCMakePackage;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedExecutable;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeResolver;

public class CMakeApplicationResolverTest {

  @Test
  void resolveNoToolchainTest() throws Exception {
    final Project project = ProjectBuilder.builder().build();
    final Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
        CMakeExtension.class, customTasks);

    TestCMakePackage.register("Package0", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
        Arrays.asList("Toolchain0"));
    TestCMakeToolchain.registerWithApplicationDependencies("Toolchain0", extension,
        Arrays.asList(
            TestCMakeDependencies.create("target").from("Package0"),
            TestCMakeDependencies.create("InterfaceLibrary0").getLinkInterface(),
            TestCMakeDependencies.create("BinaryLibrary0").from(project.getName()).getLinkShared()),
        Arrays.asList("-loption"));
    TestCMakeApplication.register("Application0", extension);

    assertEquals(1, extension.getPackages().size());
    assertEquals(1, extension.getToolchains().size());
    assertEquals(2, extension.getLibraries().size());
    assertEquals(1, extension.getApplications().size());
    assertEquals(0, extension.getTests().size());

    final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
    final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
    assertEquals(1, toolchains.length);

    assertEquals("Toolchain0", toolchains[0].getName());
    assertEquals(1, toolchains[0].getPackages().size());
    assertEquals(2, toolchains[0].getLibraries().size());
    assertEquals(1, toolchains[0].getApplications().size());
    assertEquals(0, toolchains[0].getTests().size());
  }

  @Test
  void resolveMultipleToolchainTest() throws Exception {
    final Project project = ProjectBuilder.builder().build();
    final Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
        CMakeExtension.class, customTasks);

    TestCMakePackage.register("Package0", extension);
    TestCMakePackage.register("Package1", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary1", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
        Arrays.asList("Toolchain0"));
    TestCMakeToolchain.registerWithApplicationDependencies("Toolchain0", extension,
        Arrays.asList(
            TestCMakeDependencies.create("target").from("Package0"),
            TestCMakeDependencies.create("InterfaceLibrary0").getLinkInterface(),
            TestCMakeDependencies.create("BinaryLibrary0").from(project.getName()).getLinkShared()),
        Arrays.asList("-loption"));
    TestCMakeToolchain.registerWithApplicationDependencies("Toolchain1", extension,
        Arrays.asList(
            TestCMakeDependencies.create("target").from("Package1"),
            TestCMakeDependencies.create("InterfaceLibrary1").from(project.getName()).getLinkInterface()),
        Arrays.asList("-loption"));
    TestCMakeApplication.register("Application0", extension,
        Arrays.asList("Toolchain0", "Toolchain1"));

    assertEquals(2, extension.getPackages().size());
    assertEquals(2, extension.getToolchains().size());
    assertEquals(3, extension.getLibraries().size());
    assertEquals(1, extension.getApplications().size());
    assertEquals(0, extension.getTests().size());

    final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
    final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
    assertEquals(2, toolchains.length);

    assertEquals("Toolchain0", toolchains[0].getName());
    assertEquals(1, toolchains[0].getPackages().size());
    assertEquals(3, toolchains[0].getLibraries().size());
    assertEquals(1, toolchains[0].getApplications().size());
    {
      final CMakeResolvedExecutable[] applications = toolchains[0].getApplications()
          .toArray(new CMakeResolvedExecutable[toolchains[0].getApplications().size()]);
      assertEquals(1, applications[0].getPrivateSystemPackageDependencies().size());
      assertEquals(2, applications[0].getPrivateProjectPackageDependencies().size());
      assertEquals(1, applications[0].getPrivateLinkOptions().size());
    }
    assertEquals(0, toolchains[0].getTests().size());

    assertEquals("Toolchain1", toolchains[1].getName());
    assertEquals(1, toolchains[1].getPackages().size());
    assertEquals(2, toolchains[1].getLibraries().size());
    assertEquals(1, toolchains[1].getApplications().size());
    {
      final CMakeResolvedExecutable[] applications = toolchains[1].getApplications()
          .toArray(new CMakeResolvedExecutable[toolchains[1].getApplications().size()]);
      assertEquals(1, applications[0].getPrivateSystemPackageDependencies().size());
      assertEquals(1, applications[0].getPrivateProjectPackageDependencies().size());
      assertEquals(1, applications[0].getPrivateLinkOptions().size());
    }
    assertEquals(0, toolchains[1].getTests().size());
  }

  @Test
  void resolveNoDependenciesTest() throws Exception {
    final Project project = ProjectBuilder.builder().build();
    final Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
        CMakeExtension.class, customTasks);

    TestCMakePackage.register("Package0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
        Arrays.asList("Toolchain0"));
    TestCMakeToolchain.registerWithApplicationDependencies("Toolchain0", extension,
        Arrays.asList(), Arrays.asList());
    TestCMakeToolchain.registerWithTestDependencies("Toolchain1", extension,
        Arrays.asList(TestCMakeDependencies.create("target").from("Package0")),
        Arrays.asList("-loption"));
    TestCMakeApplication.register("Application0", extension,
        Arrays.asList("Toolchain0"));

    assertEquals(1, extension.getPackages().size());
    assertEquals(2, extension.getToolchains().size());
    assertEquals(1, extension.getLibraries().size());
    assertEquals(1, extension.getApplications().size());
    assertEquals(0, extension.getTests().size());

    final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
    final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
    assertEquals(1, toolchains.length);

    assertEquals("Toolchain0", toolchains[0].getName());
    assertEquals(0, toolchains[0].getPackages().size());
    assertEquals(1, toolchains[0].getLibraries().size());
    assertEquals(1, toolchains[0].getApplications().size());
    {
      final CMakeResolvedExecutable[] applications = toolchains[0].getApplications()
          .toArray(new CMakeResolvedExecutable[toolchains[0].getApplications().size()]);
      assertEquals(0, applications[0].getPrivateSystemPackageDependencies().size());
      assertEquals(0, applications[0].getPrivateProjectPackageDependencies().size());
      assertEquals(0, applications[0].getPrivateLinkOptions().size());
    }
    assertEquals(0, toolchains[0].getTests().size());
  }

  @Test
  void resolveToolchainDependencyTest() throws Exception {
    final Project project = ProjectBuilder.builder().build();
    final Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
        CMakeExtension.class, customTasks);

    TestCMakePackage.register("Package0", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
        Arrays.asList("Toolchain0"));
    TestCMakeToolchain.registerWithApplicationDependencies("Toolchain0", extension,
        Arrays.asList(
            TestCMakeDependencies.create("target").from("Package0"),
            TestCMakeDependencies.create("InterfaceLibrary0").getLinkInterface(),
            TestCMakeDependencies.create("BinaryLibrary0").from(project.getName()).getLinkShared()),
        Arrays.asList("-loption"));
    TestCMakeApplication.register("Application0", extension,
        Arrays.asList("Toolchain0"));

    assertEquals(1, extension.getPackages().size());
    assertEquals(1, extension.getToolchains().size());
    assertEquals(2, extension.getLibraries().size());
    assertEquals(1, extension.getApplications().size());
    assertEquals(0, extension.getTests().size());

    final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
    final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
    assertEquals(1, toolchains.length);

    assertEquals("Toolchain0", toolchains[0].getName());
    assertEquals(1, toolchains[0].getPackages().size());
    assertEquals(2, toolchains[0].getLibraries().size());
    assertEquals(1, toolchains[0].getApplications().size());
    {
      final CMakeResolvedExecutable[] applications = toolchains[0].getApplications()
          .toArray(new CMakeResolvedExecutable[toolchains[0].getApplications().size()]);
      assertEquals(1, applications[0].getPrivateSystemPackageDependencies().size());
      assertEquals(2, applications[0].getPrivateProjectPackageDependencies().size());
      assertEquals(1, applications[0].getPrivateLinkOptions().size());
    }
    assertEquals(0, toolchains[0].getTests().size());
  }

  @Test
  void resolveLinkDependenciesTest() throws Exception {
    final Project project = ProjectBuilder.builder().build();
    final Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
        CMakeExtension.class, customTasks);

    TestCMakePackage.register("Package0", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
        Arrays.asList("Toolchain0"));
    TestCMakeToolchain.register("Toolchain0", extension);
    TestCMakeApplication.registerWithPrivateDependencies("Application0", extension,
        Arrays.asList("Toolchain0"),
        Arrays.asList(
            TestCMakeDependencies.create("target").from("Package0"),
            TestCMakeDependencies.create("InterfaceLibrary0").getLinkInterface(),
            TestCMakeDependencies.create("BinaryLibrary0").from(project.getName()).getLinkShared()),
        Arrays.asList("-loption"));

    assertEquals(1, extension.getPackages().size());
    assertEquals(1, extension.getToolchains().size());
    assertEquals(2, extension.getLibraries().size());
    assertEquals(1, extension.getApplications().size());
    assertEquals(0, extension.getTests().size());

    final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
    final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
    assertEquals(1, toolchains.length);

    assertEquals("Toolchain0", toolchains[0].getName());
    assertEquals(1, toolchains[0].getPackages().size());
    assertEquals(2, toolchains[0].getLibraries().size());
    assertEquals(1, toolchains[0].getApplications().size());
    {
      final CMakeResolvedExecutable[] applications = toolchains[0].getApplications()
          .toArray(new CMakeResolvedExecutable[toolchains[0].getApplications().size()]);
      assertEquals(1, applications[0].getPrivateSystemPackageDependencies().size());
      assertEquals(2, applications[0].getPrivateProjectPackageDependencies().size());
      assertEquals(1, applications[0].getPrivateLinkOptions().size());
    }
    assertEquals(0, toolchains[0].getTests().size());
  }
}
