package ch.tomaki.gradle.cmake.extensions;

import org.gradle.api.provider.Property;

public interface CMakeTest extends CMakeBinary {

  public Property<Boolean> getTestResultsXmlOutput();

}
