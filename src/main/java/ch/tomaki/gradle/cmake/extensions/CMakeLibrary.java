package ch.tomaki.gradle.cmake.extensions;

import org.gradle.api.provider.SetProperty;

public interface CMakeLibrary extends CMakeBinary {

  public SetProperty<String> getPublicCompileOptions();

  public SetProperty<String> getPublicCompileDefinitions();

  public SetProperty<String> getPublicLinkDependencies();

}
