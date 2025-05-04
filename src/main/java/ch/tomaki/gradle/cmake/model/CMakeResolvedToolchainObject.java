/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import ch.tomaki.gradle.cmake.extension.CMakeObject;
import ch.tomaki.gradle.cmake.extension.CMakeToolchain;

abstract class CMakeResolvedToolchainObject extends CMakeResolvedInterfaceObject {

  private final CMakeResolvedToolchain toolchain;

  CMakeResolvedToolchainObject(final CMakeObject object, final CMakeToolchain toolchain) {
    super(object);
    this.toolchain = new CMakeResolvedToolchain(toolchain);
  }

  public CMakeResolvedToolchain getToolchain() {
    return toolchain;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((toolchain == null) ? 0 : toolchain.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    CMakeResolvedToolchainObject other = (CMakeResolvedToolchainObject) obj;
    if (toolchain == null) {
      if (other.toolchain != null)
        return false;
    } else if (!toolchain.equals(other.toolchain))
      return false;
    return true;
  }

}
