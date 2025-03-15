package ch.tomaki.gradle.cmake.extensions;

import org.gradle.api.NamedDomainObjectContainer;

public interface CMakeExtension {

  public NamedDomainObjectContainer<CMakeToolchain> getToolchains();

  public NamedDomainObjectContainer<CMakeFindPackage> getFindPackages();

  public NamedDomainObjectContainer<CMakeLibrary> getLibraries();

  public NamedDomainObjectContainer<CMakeBinary> getApplications();

  public NamedDomainObjectContainer<CMakeTest> getTests();

  public static String getName() {
    return "cmake";
  }

  // public List<CMakeToolchain> getResolvedToolchains(final List<String>
  // toolchainNames) {
  // final List<CMakeToolchain> resolvedToolchains = new ArrayList<>();
  // final SortedMap<String, CMakeToolchain> toolchains =
  // getToolchains().getAsMap();
  // for (final String toolchainName : toolchainNames) {
  // if (toolchains.containsKey(toolchainName)) {
  // final CMakeToolchain toolchain = toolchains.get(toolchainName);
  // if (toolchain.getOperatingSystem().isPresent()) {
  // final String toolchainOpertingSystemName =
  // toolchain.getOperatingSystem().get().toLowerCase();
  // if (Objects.equals(toolchainOpertingSystemName,
  // OperatingSystem.current().getFamilyName().toLowerCase())) {
  // resolvedToolchains.add(toolchain);
  // }
  // }
  // }
  // }
  // return resolvedToolchains;
  // }

}
