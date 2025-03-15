package ch.tomaki.gradle.cmake.extensions;

import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;

public interface CMakeBinary extends CMakeNamedObject {

  public SetProperty<String> getBuildToolchains();

  public SetProperty<String> getIncludes();

  public SetProperty<String> getSources();

  public SetProperty<String> getPrivateCompileOptions();

  public SetProperty<String> getPrivateCompileDefinitions();

  public SetProperty<String> getPrivateLinkDependencies();

  public Property<Boolean> getBuildStatic();

  public Property<Boolean> getBuildShared();

  public Property<Boolean> getStripDebug();

  public Property<Boolean> getPackageBuildOutputs();

}
