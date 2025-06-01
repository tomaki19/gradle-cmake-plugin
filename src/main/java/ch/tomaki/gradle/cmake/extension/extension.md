# The Extension

The `cmake` extension offers the following main configuration blocks:

```groovy
cmake {
  findPackages
  toolchains
  libraries
  applications
  tests
}
```

## FindPackages

The `findPackages` configuration block lets you specify exernal libraries that can be referenced by your build. Each component has a mandatory, unique name and thw following optional configuration options:

```groovy
findPackages {
  <name> {
    components = //optional: String
    properties = //optional: Map<String,String>
  }
}
```

## Toolchains

```groovy
toolchains {
  '<name>' {
    operatingSystem = //required: org.gradle.internal.os.OperatingSystem
    compiler = //required: String
    architecture = //required: String
    generator = //required: String
    buildConfigs = //optional: List<String>, default: ['debug','release']
    environment = //optional, Map<String,String>
    environmentFile = //optional, File
    toolchainFile = //optional, File
    binaries {
      privateLinkDependencies = //optional: List<String>
      buildStatic = //optional: boolean, default: false
      buildShared = //optional: boolean, default: true
      stripDebug = //optional: boolean, default: false
      packageBuildOutputs = //optional: boolean, default: false
    }
    libraries {
      privateLinkDependencies = //optional: List<String>
      buildStatic = //optional: boolean, default: false
      buildShared = //optional: boolean, default: true
      stripDebug = //optional: boolean, default: false
      packageBuildOutputs = //optional: boolean, default: false
    }
    applications {
      privateLinkDependencies = //optional: List<String>
      buildStatic = //optional: boolean, default: false
      buildShared = //optional: boolean, default: true
      stripDebug = //optional: boolean, default: false
      packageBuildOutputs = //optional: boolean, default: false
    }
    tests {
      privateLinkDependencies = //optional: List<String>
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

```groovy
libraries {
  '<name>' {
    toolchains = //optional: List<String>, (header only library, if not present)
    includes = //required: List<String>
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

```groovy
applications {
  '<name>' {
    toolchains = //required: List<String>
    includes = //optional: List<String>
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

```groovy
tests {
  '<name>' {
    toolchains = //required: List<String>
    includes = //optional: List<String>
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
