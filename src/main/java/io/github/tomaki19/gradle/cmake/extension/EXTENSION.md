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
    libraries {
      privateLinkDependencies = //optional: List<String>, default: none
      buildStatic = //optional: boolean, default: false
      buildShared = //optional: boolean, default: true
      stripDebug = //optional: boolean, default: false
    }
    applications {
      privateLinkDependencies = //optional: List<String>, default: none
      buildStatic = //optional: boolean, default: false
      buildShared = //optional: boolean, default: true
      stripDebug = //optional: boolean, default: false
    }
    tests {
      privateLinkDependencies = //optional: List<String>, default: none
      buildStatic = //optional: boolean, default: false
      buildShared = //optional: boolean, default: true
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
    headers {
      srcDir <String> //optional
      srcDirs <List<String>> //optional
    }
    sources {
      srcDir <String> //optional
      srcDirs <List<String>> //optional
    }
    privateCompile {
      define <String> //optional
      defines <List<String>> //optional
      option <String> //optional
      options <List<String>> //optional
    }
    publicCompile {
      define <String> //optional
      defines <List<String>> //optional
      option <String> //optional
      options <List<String>> //optional
    }
    privateLinking {
      option <String> //optional
      options <List<String>> //optional
      dependency <String> from <String> linkStatic|linkShared|linkInterface forStaticBuild|forSharedBuild//optional
      dependencies <List<String>> from <String> linkStatic|linkShared|linkInterface forStaticBuild|forSharedBuild//optional
    }
    publicLinking {
      option <String> //optional
      options <List<String>> //optional
      dependency <String> from <String> linkStatic|linkShared|linkInterface forStaticBuild|forSharedBuild //optional
      dependencies <List<String>> from <String> linkStatic|linkShared|linkInterface forStaticBuild|forSharedBuild//optional
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
    privateCompile {
      define <String> //optional
      defines <List<String>> //optional
      option <String> //optional
      options <List<String>> //optional
    }
    privateLinking {
      option <String> //optional
      options <List<String>> //optional
      dependency <String> from <String> linkStatic|linkShared|linkInterface forStaticBuild|forSharedBuild//optional
      dependencies <List<String>> from <String> linkStatic|linkShared|linkInterface forStaticBuild|forSharedBuild//optional
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
    privateCompile {
      define <String> //optional
      defines <List<String>> //optional
      option <String> //optional
      options <List<String>> //optional
    }
    privateLinking{
      option <String> //optional
      options <List<String>> //optional
      dependency <String> from <String> linkStatic|linkShared|linkInterface forStaticBuild|forSharedBuild//optional
      dependencies <List<String>> from <String> linkStatic|linkShared|linkInterface forStaticBuild|forSharedBuild//optional
    }
    outputName <String> //optional
    stripDebug = <boolean> //optional, default: false
    testResultsXmlOutput = <boolean> //optional, default: false
  }
}
```
