/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.files;

public enum CMakeLinkType {
  STATIC, SHARED, INTERFACE;

  @Override
  public String toString() {
    return name().toLowerCase();
  }

}
