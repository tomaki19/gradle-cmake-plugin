package ch.tomaki.gradle.cmake.model;

import java.util.Map;

import org.gradle.api.Project;

import ch.tomaki.gradle.cmake.extensions.CMakeFindPackage;
import ch.tomaki.gradle.cmake.extensions.CMakeTest;

public final class CMakeResolvedTest extends CMakeResolvedBinary {

  public CMakeResolvedTest(final CMakeTest test, final Map<String, CMakeFindPackage> findPackages,
      final CMakeResolvedToolchain toolchain, final String buildConfig, final Project project) {
    super(test, findPackages, toolchain, buildConfig, project);
  }

}
