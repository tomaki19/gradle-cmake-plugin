/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.helper;

import java.util.HashMap;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.model.ObjectFactory;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeApplication;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary;
import io.github.tomaki19.gradle.cmake.extension.api.CMakePackage;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeTest;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;

public class MockCMakeExtension extends CMakeExtension {

  private final NamedDomainObjectContainer<CMakeToolchain> toolchains;
  private final NamedDomainObjectContainer<CMakePackage> packages;
  private final NamedDomainObjectContainer<CMakeLibrary> libraries;
  private final NamedDomainObjectContainer<CMakeApplication> applications;
  private final NamedDomainObjectContainer<CMakeTest> tests;

  public MockCMakeExtension(final ObjectFactory factory) {
    super(new HashMap<>());
    this.toolchains = factory.domainObjectContainer(CMakeToolchain.class);
    this.packages = factory.domainObjectContainer(CMakePackage.class);
    this.libraries = factory.domainObjectContainer(CMakeLibrary.class);
    this.applications = factory.domainObjectContainer(CMakeApplication.class);
    this.tests = factory.domainObjectContainer(CMakeTest.class);
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
