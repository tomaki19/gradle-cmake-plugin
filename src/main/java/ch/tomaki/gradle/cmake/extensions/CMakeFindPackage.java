package ch.tomaki.gradle.cmake.extensions;

import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.SetProperty;

public interface CMakeFindPackage extends CMakeNamedObject {

  public SetProperty<String> getComponents();

  public MapProperty<String, String> getProperties();

}
