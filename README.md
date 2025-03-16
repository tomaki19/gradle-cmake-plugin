# gradle-cmake-plugin
A Gradle plugin that uses the CMake build framework for C/C++ builds.

# Intention
Organizing multi-project C/C++ builds is hard, especially in multi language environments. The Gradle build framework does a great job in integrating multiple languages, while still enforcing some basic build guidelines. This plugin combines the powerful and feature rich CMake build framework with the flexibile Gradle framework to produce modern CMake multi-project configurations. Some build scripting rules are enforced onto the projects, while keeping a certain degree of freedom for developers to build their own setups.

# Features

## Toolchains
```groovy
camke {
  toolchains {
    "<name>" {
      compiler
      opperatingSystem
      architecture
      generator
      buildConfigs
      environment
      toolchainFile
      linkStatic
      linkShared
    }
    ...
  }
  ...
}
```

## Dependency
```groovy
camke {
  dependencies {
    "<name>" {
      url
      branch
    }
    ...
  }
  ...
}
```

## FindPackage
```groovy
camke {
  findPackages {
    "<name>" {
      buildToolchains
      components
      properties
    }
    ...
  }
  ...
}
```

## Libraries
```groovy
camke {
  libraries {
    "<name>" {
      buildToolchains
      sources
      headers
      publicCompilerFlags
      privateCompilerFlags
      publicSharedLinkDependencies
      publicStaticLinkDependencies
      privateSharedLinkDependencies
      privateStaticLinkDependencies
    }
    ...
  }
  ...
}
```

## Applications
```groovy
camke {
  applications {
    "<name>" {
      buildToolchains
      sources
      headers
      privateCompilerFlags
      privateSharedLinkDependencies
      privateStaticLinkDependencies
    }
    ...
  }
  ...
}
```

## Tests
```groovy
camke {
  tests {
    "<name>" {
      buildToolchains
      sources
      headers
      privateCompilerFlags
      privateSharedLinkDependencies
      privateStaticLinkDependencies
    }
    ...
  }
  ...
}
```
