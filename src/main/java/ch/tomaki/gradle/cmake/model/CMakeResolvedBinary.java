package ch.tomaki.gradle.cmake.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.gradle.api.Project;

import ch.tomaki.gradle.cmake.extensions.CMakeBinary;
import ch.tomaki.gradle.cmake.extensions.CMakeFindPackage;
import ch.tomaki.gradle.cmake.files.CMakeListsConventions;

public abstract class CMakeResolvedBinary {

  private final String name;
  private final String buildConfig;
  private final CMakeResolvedToolchain toolchain;
  private final Set<String> includes;
  private final Set<String> sources;
  private final Set<String> privateCompileOptions;
  private final Set<String> privateCompileDefinitions;
  private final Set<String> privateLinkOptions;
  private final Set<CMakeResolvedFindPackageDependency> privateFindPackageDependencies;
  private final Set<CMakeResolvedProjectModuleDependency> privateProjectModuleDependencies;
  private final boolean buildStatic;
  private final boolean buildShared;
  private final boolean stripDebug;
  private final boolean packageBuildOutputs;

  public CMakeResolvedBinary(final CMakeBinary binary, final Map<String, CMakeFindPackage> findPackages,
      final CMakeResolvedToolchain toolchain, final String buildConfig, final Project project)
      throws IllegalArgumentException {
    this.name = binary.getName();
    this.buildConfig = buildConfig;
    this.toolchain = toolchain;
    this.includes = new HashSet<>(binary.getIncludes().get());
    this.sources = new HashSet<>(binary.getSources().get());
    this.privateCompileOptions = new HashSet<>(binary.getPrivateCompileOptions().get());
    this.privateCompileDefinitions = new HashSet<>(binary.getPrivateCompileDefinitions().get());
    this.privateLinkOptions = resolveLinkOptions(binary.getPrivateLinkDependencies().get());
    this.privateFindPackageDependencies = resolveFindPackageDependencies(binary.getPrivateLinkDependencies().get(),
        findPackages, toolchain, project);
    this.privateProjectModuleDependencies = resolveProjectModuleDependencies(binary.getPrivateLinkDependencies().get(),
        buildConfig, toolchain, project);
    this.buildStatic = binary.getBuildStatic().getOrElse(Boolean.FALSE) || toolchain.isBuildStatic();
    this.buildShared = binary.getBuildShared().getOrElse(Boolean.FALSE) || toolchain.isBuildShared();
    this.stripDebug = binary.getStripDebug().getOrElse(Boolean.FALSE) || toolchain.isStripDebug();
    this.packageBuildOutputs = binary.getPackageBuildOutputs().getOrElse(Boolean.FALSE)
        || toolchain.isPackageBuildOutputs();
  }

  public String getName() {
    return name;
  }

  public String getBuildConfig() {
    return buildConfig;
  }

  public CMakeResolvedToolchain getToolchain() {
    return toolchain;
  }

  public Set<String> getIncludes() {
    return includes;
  }

  public Set<String> getSources() {
    return sources;
  }

  public Set<String> getPrivateCompileOptions() {
    return privateCompileOptions;
  }

  public Set<String> getPrivateCompileDefinitions() {
    return privateCompileDefinitions;
  }

  public void addLibraryDependencies(final Set<String> dependencies, final Map<String, CMakeFindPackage> findPackages,
      final Project project) throws IllegalArgumentException {
    privateLinkOptions.addAll(resolveLinkOptions(dependencies));
    privateFindPackageDependencies.addAll(resolveFindPackageDependencies(dependencies,
        findPackages, toolchain, project));
    privateProjectModuleDependencies.addAll(resolveProjectModuleDependencies(dependencies,
        buildConfig, toolchain, project));

  }

  public Set<String> getPrivateLinkOptions() {
    return privateLinkOptions;
  }

  public Set<CMakeResolvedFindPackageDependency> getPrivateFindPackageDependencies() {
    return privateFindPackageDependencies;
  }

  public Set<CMakeResolvedProjectModuleDependency> getPrivateProjectModuleDependencies() {
    return privateProjectModuleDependencies;
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

  protected Set<String> resolveLinkOptions(final Set<String> dependencies) throws IllegalArgumentException {
    final Set<String> resolvedLinkOptions = new HashSet<>();
    for (final String dependency : dependencies) {
      if (dependency.startsWith("-")) {
        final String[] dependencyTokens = dependency.split("::");
        if (dependencyTokens.length <= 2) {
          resolvedLinkOptions.add(dependency);
        } else {
          throw new IllegalArgumentException("Invalid link option declaration: '%s'!".formatted(dependency));
        }
      }
    }
    return resolvedLinkOptions;
  }

  protected Set<CMakeResolvedFindPackageDependency> resolveFindPackageDependencies(final Set<String> dependencies,
      final Map<String, CMakeFindPackage> findPackages, final CMakeResolvedToolchain toolchain, final Project project)
      throws IllegalArgumentException {
    final Set<CMakeResolvedFindPackageDependency> resolvedFindPackageDependencies = new HashSet<>();
    for (final String dependency : dependencies) {
      if (!dependency.startsWith("-")) {
        final String[] dependencyTokens = dependency.split("::");
        if (dependencyTokens.length <= 2) {
          if (findPackages.containsKey(dependencyTokens[0])) {
            final CMakeFindPackage findPackage = findPackages.get(dependencyTokens[0]);
            final CMakeResolvedFindPackageDependency resolvedFindPackage = new CMakeResolvedFindPackageDependency(
                findPackage.getName(), dependency, toolchain);
            resolvedFindPackageDependencies.add(resolvedFindPackage);
          } else {
            throw new IllegalArgumentException("Missing find package declaration for '%s'!".formatted(dependency));
          }
        }
      }
    }
    return resolvedFindPackageDependencies;
  }

  protected Set<CMakeResolvedProjectModuleDependency> resolveProjectModuleDependencies(final Set<String> dependencies,
      final String buildConfig, final CMakeResolvedToolchain toolchain, final Project project)
      throws IllegalArgumentException {
    final Set<CMakeResolvedProjectModuleDependency> resolvedProjectModuleDependencies = new HashSet<>();
    for (final String dependency : dependencies) {
      if (!dependency.startsWith("-")) {
        final String[] dependencyTokens = dependency.split("::");
        if (dependencyTokens.length == 3) {
          final Project dependencyProject = project.findProject(":%s".formatted(dependencyTokens[0]));
          if (Objects.nonNull(dependencyProject)) {
            final String buildTarget = getBuildTarget(dependencyTokens[2], buildConfig, dependencyTokens[1], toolchain,
                dependencyProject);
            final CMakeResolvedProjectModuleDependency resolvedProjectModule = new CMakeResolvedProjectModuleDependency(
                buildTarget, isBuildable(dependencyTokens[2]), toolchain, dependencyProject);
            resolvedProjectModuleDependencies.add(resolvedProjectModule);
          } else {
            throw new IllegalArgumentException("Missing local project: '%s'!".formatted(dependencyTokens[0]));
          }
        } else if (dependencyTokens.length > 3) {
          throw new IllegalArgumentException("Invalid project dependency declaration: '%s'!".formatted(dependency));
        }
      }
    }
    return resolvedProjectModuleDependencies;
  }

  private static String getBuildTarget(final String buildType, final String buildConfig, final String libraryName,
      final CMakeResolvedToolchain toolchain, final Project project) {
    switch (CMakeResolvedBuildTypes.valueOf(buildType.toUpperCase())) {
      case STATIC: {
        return CMakeListsConventions.staticLibraryTarget(libraryName, toolchain, buildConfig);
      }
      case SHARED: {
        return CMakeListsConventions.sharedLibraryTarget(libraryName, toolchain, buildConfig);
      }
      default: {
        return CMakeListsConventions.interfaceLibraryTarget(libraryName, toolchain, buildConfig);
      }
    }
  }

  private static boolean isBuildable(final String libraryType) {
    return !Objects.equals(CMakeResolvedBuildTypes.INTERFACE.name().toLowerCase(), libraryType);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((buildConfig == null) ? 0 : buildConfig.hashCode());
    result = prime * result + ((toolchain == null) ? 0 : toolchain.hashCode());
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
    CMakeResolvedBinary other = (CMakeResolvedBinary) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (buildConfig == null) {
      if (other.buildConfig != null)
        return false;
    } else if (!buildConfig.equals(other.buildConfig))
      return false;
    if (toolchain == null) {
      if (other.toolchain != null)
        return false;
    } else if (!toolchain.equals(other.toolchain))
      return false;
    return true;
  }

}
