/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.helper;

import java.util.HashMap;
import java.util.Map;

import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.model.ObjectFactory;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeApplication;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomPackageTaskProto;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomTaskProto;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary;
import io.github.tomaki19.gradle.cmake.extension.api.CMakePackage;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeTest;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;
import io.github.tomaki19.gradle.cmake.tasks.CMakeCustomExec;
import io.github.tomaki19.gradle.cmake.tasks.CMakePackageDevelopment;
import io.github.tomaki19.gradle.cmake.tasks.CMakePackageRuntime;

public class MockCMakeExtension extends CMakeExtension {

  private final NamedDomainObjectContainer<CMakeToolchain> toolchains;
  private final NamedDomainObjectContainer<CMakePackage> packages;
  private final NamedDomainObjectContainer<CMakeLibrary> libraries;
  private final NamedDomainObjectContainer<CMakeApplication> applications;
  private final NamedDomainObjectContainer<CMakeTest> tests;
  private final Map<String, Map<CMakeCustomTaskProto, Action<CMakeCustomExec>>> customTaskProtosRef;
  private final Map<String, Map<CMakeCustomPackageTaskProto, Action<CMakePackageRuntime>>> customPackageRuntimeTaskProtosRef;
  private final Map<String, Map<CMakeCustomPackageTaskProto, Action<CMakePackageDevelopment>>> customPackageDevelopmentTaskProtosRef;

  public MockCMakeExtension(final ObjectFactory factory) {
    this(factory, new HashMap<>(), new HashMap<>(), new HashMap<>());
  }

  private MockCMakeExtension(final ObjectFactory factory,
      final Map<String, Map<CMakeCustomTaskProto, Action<CMakeCustomExec>>> execMap,
      final Map<String, Map<CMakeCustomPackageTaskProto, Action<CMakePackageRuntime>>> runtimeMap,
      final Map<String, Map<CMakeCustomPackageTaskProto, Action<CMakePackageDevelopment>>> developmentMap) {
    super(execMap, runtimeMap, developmentMap);
    this.customTaskProtosRef = execMap;
    this.customPackageRuntimeTaskProtosRef = runtimeMap;
    this.customPackageDevelopmentTaskProtosRef = developmentMap;
    this.toolchains = factory.domainObjectContainer(CMakeToolchain.class);
    this.packages = factory.domainObjectContainer(CMakePackage.class);
    this.libraries = factory.domainObjectContainer(CMakeLibrary.class);
    this.applications = factory.domainObjectContainer(CMakeApplication.class);
    this.tests = factory.domainObjectContainer(CMakeTest.class);
  }

  public Map<String, Map<CMakeCustomTaskProto, Action<CMakeCustomExec>>> getCustomTaskProtos() {
    return customTaskProtosRef;
  }

  public Map<String, Map<CMakeCustomPackageTaskProto, Action<CMakePackageRuntime>>> getCustomPackageRuntimeTaskProtos() {
    return customPackageRuntimeTaskProtosRef;
  }

  public Map<String, Map<CMakeCustomPackageTaskProto, Action<CMakePackageDevelopment>>> getCustomPackageDevelopmentTaskProtos() {
    return customPackageDevelopmentTaskProtosRef;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public Map<String, Map<CMakeCustomPackageTaskProto, ?>> getCustomPackageTaskProtos() {
    final Map<String, Map<CMakeCustomPackageTaskProto, ?>> merged = new HashMap<>();
    customPackageRuntimeTaskProtosRef.forEach((key, value) ->
        merged.computeIfAbsent(key, k -> new HashMap<>()).putAll((Map) value));
    customPackageDevelopmentTaskProtosRef.forEach((key, value) ->
        merged.computeIfAbsent(key, k -> new HashMap<>()).putAll((Map) value));
    return merged;
  }

  @Override
  public NamedDomainObjectContainer<CMakeToolchain> getToolchains() {
    return toolchains;
  }

  @Override
  public NamedDomainObjectContainer<CMakePackage> getPackages() {
    return packages;
  }

  @Override
  public NamedDomainObjectContainer<CMakeLibrary> getLibraries() {
    return libraries;
  }

  @Override
  public NamedDomainObjectContainer<CMakeApplication> getApplications() {
    return applications;
  }

  @Override
  public NamedDomainObjectContainer<CMakeTest> getTests() {
    return tests;
  }

}
