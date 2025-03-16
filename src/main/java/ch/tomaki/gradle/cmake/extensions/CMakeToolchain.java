package ch.tomaki.gradle.cmake.extensions;

import java.io.File;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.internal.os.OperatingSystem;

public interface CMakeToolchain extends CMakeNamedObject {

  public static final OperatingSystem Linux = OperatingSystem.LINUX;
  public static final OperatingSystem MacOs = OperatingSystem.MAC_OS;
  public static final OperatingSystem Windows = OperatingSystem.WINDOWS;

  public Property<String> getCompiler();

  public Property<OperatingSystem> getOperatingSystem();

  public Property<String> getArchitecture();

  public Property<String> getGenerator(); // Ninja Multi Config

  public SetProperty<String> getBuildConfigs(); // Debug, Release, MinSizeRel, RelWithDebInfo

  public MapProperty<String, String> getEnvironment();

  public Property<File> getEnvironmentFile();

  public RegularFileProperty getToolchainFile();

  public SetProperty<String> getPrivateLibraryLinkDependencies();

  public SetProperty<String> getPrivateApplicationLinkDependencies();

  public SetProperty<String> getPrivateTestLinkDependencies();

  public Property<Boolean> getBuildStatic();

  public Property<Boolean> getBuildShared();

  public Property<Boolean> getStripDebug();

  public Property<Boolean> getPackageBuildOutputs();

}
