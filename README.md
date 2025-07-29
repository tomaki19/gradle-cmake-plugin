# gradle-cmake-plugin

A Gradle plugin that uses the CMake build framework for C/C++ builds.

# Setup

```groovy
plugins {
  id('io.github.tomaki19.gradle-cmake-plugin') version '...'
}
```

# Compatibility

The plugin requires the following version:

- gradle >= 8
- Java >= 17
- cmake >= 3.14

# Extension

See the [extension documentation](<src/main/java/io/github/tomaki19/gradle/cmake/extension/EXTENSION.md>).

# Tasks

See the [tasks documentation](<src/main/java/io/github/tomaki19/gradle/cmake/tasks/TASKS.md>).
