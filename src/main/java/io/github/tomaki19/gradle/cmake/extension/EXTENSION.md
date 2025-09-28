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
    properties = //optional: Map<String,String>
  }
}
```

## Toolchains

The `toolchains` configuration block lets you specify build toolchains that can be referenced by your build items. Each toolchain has a mandatory, unique name and the following optional configuration options:

```groovy
toolchains {
  '<name>' {
    operatingSystem = //optional: enum [Linux, Windows, MacOS], default: auto detect
    generator = //optional: String, default: cmake defaults for operating system
    buildConfigs = //optional: List<String>, default: ['debug','release']
    environment = //optional: Map<String,String>, default: none
    environmentFile = //optional: File, default: none
    toolchainFile = //optional: File, default: none
    binaries {
      privateLinkDependencies = //optional: List<String>, default: none
      buildStatic = //optional: boolean, default: false
      buildShared = //optional: boolean, default: true
      stripDebug = //optional: boolean, default: false
      packageBuildOutputs = //optional: boolean, default: false
    }
    libraries {
      privateLinkDependencies = //optional: List<String>, default: none
      buildStatic = //optional: boolean, default: false
      buildShared = //optional: boolean, default: true
      stripDebug = //optional: boolean, default: false
      packageBuildOutputs = //optional: boolean, default: false
    }
    applications {
      privateLinkDependencies = //optional: List<String>, default: none
      buildStatic = //optional: boolean, default: false
      buildShared = //optional: boolean, default: true
      stripDebug = //optional: boolean, default: false
      packageBuildOutputs = //optional: boolean, default: false
    }
    tests {
      privateLinkDependencies = //optional: List<String>, default: none
      buildStatic = //optional: boolean, default: false
      buildShared = //optional: boolean, default: true
      stripDebug = //optional: boolean, default: false
      packageBuildOutputs = //optional: boolean, default: false
      testResultsXmlOutput = //optional: boolean, default: false
    }
  }
}
```

## Libraries

The `libraries` configuration block lets you specify library build items that can be referenced by other build items as link dependencies. Each library has a mandatory, unique name and the following optional configuration options:

```groovy
libraries {
  '<name>' {
    outputName = //optional: String, default: <name>
    toolchains = //optional: List<String>, (header only library, if not present)
    headers = //required: List<String>
    sources = //optional: List<String>
    privateCompileOptions = //optional: List<String>
    publicCompileOptions = //optional: List<String>
    privateCompileDefinitions = //optional: List<String>
    publicCompileDefinitions = //optional: List<String>
    privateLinkDependencies = //optional: List<String>
    publicLinkDependencies = //optional: List<String>
    buildStatic = //optional: boolean, default: false
    buildShared = //optional: boolean, default: true
    stripDebug = //optional: boolean, default: false
    packageBuildOutputs = //optional: boolean, default: false
  }
}
```

## Applications

The `applications` configuration block lets you specify application build items. Each application has a mandatory, unique name and the following optional configuration options:

```groovy
applications {
  '<name>' {
    outputName = //optional: String, default: <name>
    toolchains = //required: List<String>
    headers = //optional: List<String>
    sources = //required: List<String>
    privateCompileOptions = //optional: List<String>
    privateCompileDefinitions = //optional: List<String>
    privateLinkDependencies = //optional: List<String>
    buildStatic = //optional: boolean, default: false
    buildShared = //optional: boolean, default: true
    stripDebug = //optional: boolean, default: false
    packageBuildOutputs = //optional: boolean, default: false
  }
}
```

## Tests

The `tests` configuration block lets you specify test build items. Each test has a mandatory, unique name and the following optional configuration options:

```groovy
tests {
  '<name>' {
    outputName = //optional: String, default: <name>
    toolchains = //required: List<String>
    headers = //optional: List<String>
    sources = //required: List<String>
    privateCompileOptions = //optional: List<String>
    privateCompileDefinitions = //optional: List<String>
    privateLinkDependencies = //optional: List<String>
    buildStatic = //optional: boolean, default: false
    buildShared = //optional: boolean, default: true
    stripDebug = //optional: boolean, default: false
    packageBuildOutputs = //optional: boolean, default: false
    testResultsXmlOutput = //optional: boolean, default: false
  }
}
```
