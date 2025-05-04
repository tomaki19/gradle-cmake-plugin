/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import ch.tomaki.gradle.cmake.extension.CMakeObject;

public final class CMakeResolvedInterface extends CMakeResolvedInterfaceObject {

  CMakeResolvedInterface(final CMakeObject object) throws IllegalArgumentException {
    super(object);
  }

}
