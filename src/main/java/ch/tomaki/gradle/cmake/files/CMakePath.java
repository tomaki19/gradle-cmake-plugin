/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.files;

import java.util.Arrays;
import java.util.List;

public class CMakePath {

  private List<String> entries;

  private CMakePath(final String... elements) {
    this.entries = Arrays.asList(elements);
  }

  public CMakePath append(final String... elements) {
    final CMakePath path = new CMakePath(elements);
    for (final String element : elements) {
      path.entries.add(element);
    }
    return path;
  }

  @Override
  public String toString() {
    return String.join("/", entries);
  }

  public static CMakePath get(final String... elements) {
    return new CMakePath(elements);
  }
}
