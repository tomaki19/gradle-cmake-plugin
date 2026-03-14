# The Extension

The `cmake` extension offers the following main configuration blocks:

```groovy
cmake {
  packages {...}
  toolchains {...}
  libraries {...}
  applications {...}
  tests {...}
}
```

## Packages

The `packages` configuration block lets you specify system libraries that can be referenced by your build items as link dependencies. Each package has a mandatory, unique name and the following optional configuration options:

```groovy
packages {
  '<name>' {
    moduleMode = <boolean> // optional, default: false
    targetPrefix = <String> // optional
    components = <List<String>> // optional
    properties = <Map<String,String>> // optional
  }
}
```

## Toolchains

The `toolchains` configuration block lets you specify build toolchains that can be referenced by your build items. Each toolchain has a mandatory, unique name and the following optional configuration options:

```groovy
toolchains {
  '<name>' {
    operatingSystem = //optional: enum [Linux, Windows, MacOS], default: auto detect
    generator = String //optional, default: cmake defaults for operating system
    buildConfigs List<String> //optional, default: ['debug','release']
    environment Map<String,String> //optional default: none
    environmentFile = File //optional, default: none
    toolchainFile = File //optional, default: none
    libraries {
      buildVariants List<CMakeBuildType> //optional, options: Shared, Static, default: Shared
      compiling {
        defines <List<String>> //optional
        options <List<String>> //optional
      }
      linking {
        options(<List<String>>) //optional
        dependencies(<List<String>>)
          .from(<String>) //optional
          .build(<CMakeBuildType>) //optional, options: Shared|Static, default: Shared
          .link(<CMakeLinkType>) //optional, options: Shared|Static|Interface, default: Shared
          .visibility(<CMakeVisibilityType>) //optional, options: Public|Private, default: Public
      }
      stripDebug = boolean //optional, default: false
    }
    applications {
      compiling {
        defines <List<String>> //optional
        options <List<String>> //optional
      }
      linking {
        options(<List<String>>) //optional
        dependencies(<List<String>>)
          .from(<String>) //optional
          .build(<CMakeBuildType>) //optional, options: Shared|Static, default: Shared
          .link(<CMakeLinkType>) //optional, options: Shared|Static|Interface, default: Shared
          .visibility(<CMakeVisibilityType>) //optional, options: Public|Private, default: Public
      }
      stripDebug = //optional: boolean, default: false
    }
    tests {
      compiling {
        defines <List<String>> //optional
        options <List<String>> //optional
      }
      linking {
        options(<List<String>>) //optional
        dependencies(<List<String>>)
          .from(<String>) //optional
          .link(<CMakeLinkType>) //optional, options: Shared|Static|Interface, default: Shared
          .visibility(<CMakeVisibilityType>) //optional, options: Public|Private, default: Public
      }
      stripDebug = //optional: boolean, default: false
      testResultsXmlOutput = //optional: boolean, default: false
    }
  }
}
```

## Libraries

The `libraries` configuration block lets you specify library build items that can be referenced by other build items as link dependencies. Each library has a mandatory, unique name and the following optional configuration options:

```groovy
libraries {
  <String> {
    toolchains <List<String>> //required
    buildVariants List<CMakeBuildType> //optional, options: Shared|Static, default: Shared
    headers {
      srcDir <String> //optional
      srcDirs <List<String>> //optional
    }
    sources {
      srcDir <String> //optional
      srcDirs <List<String>> //optional
    }
    compiling {
      options <List<String>> //optional
      defines <List<String>> //optional
    }
    linking {
      options(<List<String>>) //optional
      dependencies(<List<String>>)
        .from(<String>) //optional
        .forBuildVariant(<CMakeBuildType>) //optional, options: Shared|Static, default: Shared
        .link(<CMakeLinkType>) //optional, options: Shared|Static|Interface, default: Shared
        .visibility(<CMakeVisibilityType>) //optional, options: Public|Private, default: Public
    }
    outputName <String> //optional
    stripDebug = <boolean> //optional, default: false
  }
}
```

## Applications

The `applications` configuration block lets you specify application build items. Each application has a mandatory, unique name and the following optional configuration options:

```groovy
applications {
  <String> {
    toolchains <List<String>> //required
    headers {
      srcDir <String> //optional
      srcDirs <List<String>> //optional
    }
    sources {
      srcDir <String> //optional
      srcDirs <List<String>> //optional
    }
    compiling {
      options <List<String>> //optional
      defines <List<String>> //optional
    }
    linking {
      options(<List<String>>) //optional
      dependencies(<List<String>>)
        .from(<String>) //optional
        .link(<CMakeLinkType>) //optional, options: Shared|Static|Interface, default: Shared
        .visibility(<CMakeVisibilityType>) //optional, options: Public|Private, default: Private
    }
    outputName <String> //optional
    stripDebug = <boolean> //optional, default: false
  }
}
```

## Tests

The `tests` configuration block lets you specify test build items. Each test has a mandatory, unique name and the following optional configuration options:

```groovy
tests {
  <String> {
    toolchains <List<String>> //required
    headers {
      srcDir <String> //optional
      srcDirs <List<String>> //optional
    }
    sources {
      srcDir <String> //optional
      srcDirs <List<String>> //optional
    }
    compiling {
      options <List<String>> //optional
      defines <List<String>> //optional
    }
    linking {
      options(<List<String>>) //optional
      dependencies(<List<String>>)
        .from(<String>) //optional
        .link(<CMakeLinkType>) //optional, options: Shared|Static|Interface, default: Shared
        .visibility(<CMakeVisibilityType>) //optional, options: Public|Private, default: Private
    }
    outputName <String> //optional
    stripDebug = <boolean> //optional, default: false
    testResultsXmlOutput = <boolean> //optional, default: false
  }
}
```
