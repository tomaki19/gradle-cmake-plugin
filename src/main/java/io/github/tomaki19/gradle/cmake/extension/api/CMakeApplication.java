/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import javax.inject.Inject;

import org.gradle.api.model.ObjectFactory;

public abstract class CMakeApplication extends CMakeBinary implements CMakeApplications {

  @Inject
  public CMakeApplication(ObjectFactory objectFactory) {
    super(objectFactory);
  }

}
