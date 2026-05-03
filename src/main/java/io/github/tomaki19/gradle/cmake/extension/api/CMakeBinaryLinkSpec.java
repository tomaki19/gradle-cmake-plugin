/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeVisibility;

public abstract class CMakeBinaryLinkSpec {

  public static final String PROJECT = "from";
  public static final String LINK_VARIANT = "variant";
  public static final String VISIBILITY = "visibility";

  private final Set<String> components;
  private final String project;
  private final CMakeLinkVariant linkVariant;
  private final CMakeVisibility visibility;

  protected CMakeBinaryLinkSpec(final Set<String> components, final String project, final CMakeLinkVariant linkVariant,
      final CMakeVisibility visibility) {
    this.components = Collections.unmodifiableSet(components);
    this.project = project;
    this.linkVariant = linkVariant;
    this.visibility = visibility;
  }

  public Set<String> getComponents() {
    return components;
  }

  public String getProject() {
    return project;
  }

  public CMakeLinkVariant getLinkVariant() {
    return linkVariant;
  }

  public CMakeVisibility getVisibility() {
    return visibility;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + getComponents().hashCode();
    result = prime * result + Objects.hashCode(getProject());
    result = prime * result + getLinkVariant().hashCode();
    result = prime * result + getVisibility().hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof CMakeBinaryLinkSpec))
      return false;
    final CMakeBinaryLinkSpec other = (CMakeBinaryLinkSpec) obj;
    if (!getComponents().equals(other.getComponents()))
      return false;
    if (!Objects.equals(getProject(), other.getProject()))
      return false;
    if (!getLinkVariant().equals(other.getLinkVariant()))
      return false;
    if (!getVisibility().equals(other.getVisibility()))
      return false;
    return true;
  }

  static class Init extends CMakeApiSpecInit {

    protected static void validateContentTypes(final Map<String, Object> entries) throws CMakeApiException {
      validateType(entries.get(PROJECT), PROJECT, CharSequence.class);
      validateType(entries.get(LINK_VARIANT), LINK_VARIANT, CharSequence.class);
      validateType(entries.get(VISIBILITY), VISIBILITY, CharSequence.class);
    }
  }

}
