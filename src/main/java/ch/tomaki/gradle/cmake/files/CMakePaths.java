
/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.files;

public class CMakePaths {

  private static String BUILD_PATH = "build";
  private static String TEMP_PATH = "tmp";
  private static String CMAKE_PATH = "cmake";
  private static String BINARIES_PATH = "binaries";
  private static String DEPENDENCIES_PATH = "dependencies";
  private static String INSTALL_PATH = "install";

  private final CMakePath buildRootPath;
  private final CMakePath buildDependenciesPath;
  private final CMakePath outputPath;
  private final CMakePath installPath;

  public CMakePaths() {
    this.buildRootPath = CMakePath.get(BUILD_PATH, TEMP_PATH, CMAKE_PATH, BINARIES_PATH);
    this.buildDependenciesPath =
        CMakePath.get(BUILD_PATH, TEMP_PATH, CMAKE_PATH, DEPENDENCIES_PATH);
    this.outputPath = CMakePath.get(BUILD_PATH, CMAKE_PATH);
    this.installPath = CMakePath.get(BUILD_PATH, INSTALL_PATH);
  }

  public String getBuildRootPath() {
    return buildRootPath.toString();
  }

  public String getBuildDependenciesPath() {
    return buildDependenciesPath.toString();
  }

  public String getBuildDependenciesPath(final String... extraDirs) {
    return buildDependenciesPath.append(extraDirs).toString();
  }

  public String getInstallPath() {
    return installPath.toString();
  }

  public String getOutputPath() {
    return outputPath.toString();
  }
}
