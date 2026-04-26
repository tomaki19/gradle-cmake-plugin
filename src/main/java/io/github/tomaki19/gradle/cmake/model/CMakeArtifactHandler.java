/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.artifacts.dsl.ArtifactHandler;
import org.gradle.api.file.Directory;

public class CMakeArtifactHandler {

  private final ArtifactHandler artifacts;

  public CMakeArtifactHandler(final ArtifactHandler artifacts) {
    this.artifacts = artifacts;
  }

  public PublishArtifact addDirectoryArtifact(final Configuration configuration,
      final Directory outputDirectory, final Object... builtBy) {
    return artifacts.add(configuration.getName(), outputDirectory, (artifact) -> {
      artifact.builtBy(builtBy);
    });
  }
}
