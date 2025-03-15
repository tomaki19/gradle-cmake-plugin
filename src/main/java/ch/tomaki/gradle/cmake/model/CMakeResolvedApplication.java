package ch.tomaki.gradle.cmake.model;

import java.util.Map;

import org.gradle.api.Project;

import ch.tomaki.gradle.cmake.extensions.CMakeBinary;
import ch.tomaki.gradle.cmake.extensions.CMakeFindPackage;

public final class CMakeResolvedApplication extends CMakeResolvedBinary {

  public CMakeResolvedApplication(final CMakeBinary binary, final Map<String, CMakeFindPackage> findPackages,
      final CMakeResolvedToolchain toolchain, final String buildConfig, final Project project) {
    super(binary, findPackages, toolchain, buildConfig, project);
  }

}
