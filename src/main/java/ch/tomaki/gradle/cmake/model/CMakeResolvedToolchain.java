package ch.tomaki.gradle.cmake.model;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.gradle.api.file.RegularFile;
import org.gradle.internal.os.OperatingSystem;

import ch.tomaki.gradle.cmake.extensions.CMakeToolchain;

public final class CMakeResolvedToolchain {

  private final String name;
  private final OperatingSystem operatingSystem;
  private final String architecture;
  private final String compiler;
  private final String generator;
  private final Set<String> buildConfigs;
  private final Map<String, String> environment;
  private final Optional<File> environmentFile;
  private final Optional<RegularFile> toolchainFile;
  private final Set<String> privateLibraryLinkDependencies;
  private final Set<String> privateApplicationLinkDependencies;
  private final Set<String> privateTestLinkDependencies;
  private final boolean buildStatic;
  private final boolean buildShared;
  private final boolean stripDebug;
  private final boolean packageBuildOutputs;

  public CMakeResolvedToolchain(final CMakeToolchain toolchain) {
    this.name = toolchain.getName();
    this.operatingSystem = toolchain.getOperatingSystem().getOrNull();
    this.architecture = toolchain.getArchitecture().getOrElse("").toLowerCase();
    this.compiler = toolchain.getCompiler().getOrElse("").toLowerCase();
    this.generator = toolchain.getGenerator().getOrElse("");
    this.buildConfigs = toolchain.getBuildConfigs().get();
    this.environment = toolchain.getEnvironment().get();
    this.environmentFile = Optional.ofNullable(toolchain.getEnvironmentFile().getOrNull());
    this.toolchainFile = Optional.ofNullable(toolchain.getToolchainFile().getOrNull());
    this.privateLibraryLinkDependencies = toolchain.getPrivateLibraryLinkDependencies().get();
    this.privateApplicationLinkDependencies = toolchain.getPrivateApplicationLinkDependencies().get();
    this.privateTestLinkDependencies = toolchain.getPrivateTestLinkDependencies().get();
    this.buildStatic = toolchain.getBuildStatic().getOrElse(Boolean.FALSE);
    this.buildShared = toolchain.getBuildShared().getOrElse(Boolean.TRUE);
    this.stripDebug = toolchain.getStripDebug().getOrElse(Boolean.FALSE);
    this.packageBuildOutputs = toolchain.getPackageBuildOutputs().getOrElse(Boolean.FALSE);
  }

  public String getName() {
    return name;
  }

  public String getCompiler() {
    return compiler;
  }

  public OperatingSystem getOperatingSystem() {
    return operatingSystem;
  }

  public String getArchitecture() {
    return architecture;
  }

  public String getGenerator() {
    return generator;
  }

  public Set<String> getBuildConfigs() {
    return buildConfigs;
  }

  public Map<String, String> getEnvironment() {
    return environment;
  }

  public Optional<File> getEnvironmentFile() {
    return environmentFile;
  }

  public Optional<RegularFile> getToolchainFile() {
    return toolchainFile;
  }

  public Set<String> getPrivateLibraryLinkDependencies() {
    return privateLibraryLinkDependencies;
  }

  public Set<String> getPrivateApplicationLinkDependencies() {
    return privateApplicationLinkDependencies;
  }

  public Set<String> getPrivateTestLinkDependencies() {
    return privateTestLinkDependencies;
  }

  public boolean isBuildStatic() {
    return buildStatic;
  }

  public boolean isBuildShared() {
    return buildShared;
  }

  public boolean isStripDebug() {
    return stripDebug;
  }

  public boolean isPackageBuildOutputs() {
    return packageBuildOutputs;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CMakeResolvedToolchain other = (CMakeResolvedToolchain) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

}
