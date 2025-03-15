# gradle-cmake-plugin
A gradle plugin for C/C++ builds that utilizes cmake.

# Intention
Organizing software projects is hard, especially in large teams. To allow for a certain degree of standardization for build scripting, this gradle plugin combines the powerful and feature rich cmake framework with the flexibile gradle framework to produce standardized modern cmake configurations. This enforces some build scripting rules for the project, while keeping a certain degree of freedom for developers to build their projects.

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
