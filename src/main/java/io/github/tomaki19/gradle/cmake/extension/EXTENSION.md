# Extension Configuration Reference

The `cmake` extension is the root configuration block for the gradle-cmake-plugin. It contains the following main sections:

```groovy
cmake {
  packages {...}      // System/external packages (find_package)
  toolchains {...}    // Build toolchains and compiler settings
  libraries {...}     // Shared/static libraries to build
  applications {...}  // Executable applications
  tests {...}         // Test executables
}
```

## Overview

This document describes all available configuration options for the `cmake` extension. Configuration blocks can be deeply nested to provide fine-grained control over compilation and linking settings per toolchain, build type, and component type.

---

## Packages

Define system libraries and external packages that will be discovered using CMake's `find_package()` mechanism.

**Use Case:** Link against system-installed libraries like OpenGL, Boost, Qt, etc.

```groovy
cmake {
  packages {
    '<name>' {
      moduleMode = boolean              // optional, default: false
      targetPrefix = String             // optional
      components = List<String>         // optional
      properties = Map<String, String>  // optional
    }
  }
}
```

### Package Configuration Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `moduleMode` | boolean | false | If true, uses `find_package()` in MODULE mode; if false, uses CONFIG mode |
| `targetPrefix` | String | — | Custom prefix for the target names discovered by find_package |
| `components` | List<String> | — | Specific components to request when finding the package (e.g., `['system', 'filesystem']` for Boost) |
| `properties` | Map<String, String> | — | Additional CMake package properties or hints passed to find_package |

### Example: Multiple Packages

```groovy
cmake {
  packages {
    opengl {
      moduleMode = true
    }
    boost {
      components = ['system', 'filesystem', 'thread']
    }
    qt {
      components = ['Core', 'Gui', 'Widgets']
    }
    custom {
      moduleMode = true
      targetPrefix = 'Custom::'
    }
  }
}
```

---

## Toolchains

Define one or more build toolchains. Each toolchain represents a compiler/generator combination and can have different settings for libraries, applications, and tests.

**Use Case:** Configure multiple compilers (gcc, clang, MSVC) or cross-compilation toolchains.

```groovy
cmake {
  toolchains {
    '<name>' {
      operatingSystem = String                      // optional, default: auto-detect
      generator = String                            // optional, default: CMake default for OS
      buildConfigs = List<String>                   // optional, default: ['debug', 'release']
      environment = Map<String, String>            // optional, default: empty
      environmentFile = File                        // optional
      toolchainFile = File                          // optional
      
      libraries {
        buildVariants = List<CMakeBuildVariant>    // optional, default: [Shared]
        compiling { ... }                           // compilation settings
        linking { ... }                             // linking settings
        stripDebug = boolean                        // optional, default: false
      }
      
      applications {
        compiling { ... }                           // compilation settings
        linking { ... }                             // linking settings
        stripDebug = boolean                        // optional, default: false
      }
      
      tests {
        compiling { ... }                           // compilation settings
        linking { ... }                             // linking settings
        stripDebug = boolean                        // optional, default: false
        testResultsXmlOutput = boolean             // optional, default: false
      }
    }
  }
}
```

### Toolchain Configuration Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `operatingSystem` | enum | auto-detect | Target OS: `Linux`, `Windows`, `MacOS` |
| `generator` | String | platform default | CMake generator (e.g., `Unix Makefiles`, `Ninja`, `Visual Studio 16 2019`) |
| `buildConfigs` | List<String> | `['debug', 'release']` | Build configurations per toolchain |
| `environment` | Map<String, String> | — | Environment variables for CMake execution |
| `environmentFile` | File | — | Script file to source for environment setup |
| `toolchainFile` | File | — | CMake toolchain file for cross-compilation |

### Compilation Settings (compiling block)

All component types (libraries, applications, tests) can have compilation settings:

```groovy
compiling {
  options = List<String>    // Compiler flags: ['-Wall', '-O2', '-std=c++17']
  defines = List<String>    // Preprocessor defines: ['VERSION_1_0', 'DEBUG_MODE']
}
```

### Linking Settings (linking block)

Define link options and dependencies. Syntax varies slightly between sections.

**For Toolchain-level settings (applies to all libraries/apps/tests):**
```groovy
linking {
  options(List<String>)     // Linker flags: ['-Wl,-rpath=/custom/lib']
  dependencies(List<String>)
    .from(String)           // Dependency name
    .build(String)          // Library variant: 'Shared'|'Static' (for libraries only)
    .link(String)           // Link variant: 'Shared'|'Static'|'Interface'
    .visibility(String)     // Visibility: 'Public'|'Private'
}
```

### Example: Multiple Toolchains with Compilation Settings

```groovy
cmake {
  toolchains {
    gcc {
      operatingSystem = 'Linux'
      generator = 'Unix Makefiles'
      buildConfigs = ['debug', 'release', 'profile']
      environment = ['CC': 'gcc', 'CXX': 'g++']
      
      libraries {
        buildVariants = ['shared', 'static']
        compiling {
          options = ['-Wall', '-Wextra', '-fPIC']
          defines = ['LINUX', 'USE_THREADS']
        }
        linking {
          options(['-Wl,-rpath,/usr/lib'])
        }
      }
    }
    
    clang {
      operatingSystem = 'Linux'
      generator = 'Ninja'
      environment = ['CC': 'clang', 'CXX': 'clang++']
      
      applications {
        compiling {
          options = ['-Wall', '-Wextra', '-O3']
        }
      }
    }
  }
}
```

---

## Libraries

Define library targets to be compiled. Libraries can be shared or static and can be linked by applications and tests.

**Use Case:** Create reusable library components.

```groovy
cmake {
  libraries {
    '<name>' {
      toolchains = List<String>                     // required
      buildVariants = List<CMakeBuildVariant>      // optional, default: [Shared]
      
      headers {
        srcDir = String                             // optional
        srcDirs = List<String>                     // optional
      }
      
      sources {
        srcDir = String                             // optional
        srcDirs = List<String>                     // optional
      }
      
      compiling {
        options = List<String>                     // optional
        defines = List<String>                     // optional
      }
      
      linking {
        options(List<String>)                       // optional
        link(List<String>)
          .from(String)                             // optional
          .forBuildVariant(String)                  // optional: 'Shared'|'Static'
          .variant(String)                          // optional: 'Shared'|'Static'|'Interface'
          .visibility(String)                       // optional: 'Public'|'Private'
      }
      
      outputName = String                           // optional
      stripDebug = boolean                          // optional, default: false
    }
  }
}
```

### Library Configuration Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `toolchains` | List<String> | — | **Required.** Which toolchains to build this library for |
| `buildVariants` | List<String> | `['Shared']` | Library types to build: `Shared`, `Static`, or both |
| `headers.srcDir` | String | — | Single directory containing headers |
| `headers.srcDirs` | List<String> | — | Multiple directories for headers |
| `sources.srcDir` | String | — | Single directory containing sources |
| `sources.srcDirs` | List<String> | — | Multiple directories for sources |
| `compiling.options` | List<String> | — | Compiler flags specific to this library |
| `compiling.defines` | List<String> | — | Preprocessor defines for this library |
| `linking.options` | List<String> | — | Linker flags |
| `outputName` | String | library name | Override the output binary name |
| `stripDebug` | boolean | false | Strip debug symbols from release builds |

### Library Linking Details

Libraries can depend on:
- **System packages** (via `packages` section)
- **Other libraries** in the project
- Specify variant information for each dependency

```groovy
libraries {
  utils {
    toolchains = ['gcc']
    sources { srcDirs = ['src'] }
    headers { srcDirs = ['include'] }
  }
  
  core {
    toolchains = ['gcc']
    buildVariants = ['shared', 'static']
    sources { srcDirs = ['src'] }
    headers { srcDirs = ['include'] }
    
    linking {
      link(['utils'])
        .forBuildVariant('Shared')
        .variant('Shared')
        .visibility('Private')
    }
  }
}
```

---

## Applications

Define executable application targets. Applications cannot be linked by other components but can depend on libraries and packages.

**Use Case:** Create command-line tools, GUI applications, or server executables.

```groovy
cmake {
  applications {
    '<name>' {
      toolchains = List<String>                     // required
      
      headers {
        srcDir = String                             // optional
        srcDirs = List<String>                     // optional
      }
      
      sources {
        srcDir = String                             // optional
        srcDirs = List<String>                     // optional
      }
      
      compiling {
        options = List<String>                     // optional
        defines = List<String>                     // optional
      }
      
      linking {
        options(List<String>)                       // optional
        link(List<String>)
          .from(String)                             // optional
          .variant(String)                          // optional: 'Shared'|'Static'|'Interface'
          .visibility(String)                       // optional, default: 'Private'
      }
      
      outputName = String                           // optional
      stripDebug = boolean                          // optional, default: false
    }
  }
}
```

### Application Configuration Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `toolchains` | List<String> | — | **Required.** Which toolchains to build this application for |
| `headers.srcDir` | String | — | Single directory containing headers |
| `headers.srcDirs` | List<String> | — | Multiple directories for headers |
| `sources.srcDir` | String | — | Single directory containing sources |
| `sources.srcDirs` | List<String> | — | Multiple directories for sources |
| `compiling.options` | List<String> | — | Compiler flags for this application |
| `compiling.defines` | List<String> | — | Preprocessor defines |
| `linking.options` | List<String> | — | Linker flags |
| `outputName` | String | app name | Override the output executable name |
| `stripDebug` | boolean | false | Strip debug symbols from release builds |

### Example: Multiple Applications

```groovy
cmake {
  applications {
    cli {
      toolchains = ['gcc', 'clang']
      sources { srcDirs = ['src/cli'] }
      linking {
        link(['core', 'utils'])
          .variant('Shared')
      }
    }
    
    server {
      toolchains = ['gcc']
      sources { srcDirs = ['src/server'] }
      compiling {
        defines = ['ENABLE_ASYNC', 'USE_BOOST_ASIO']
      }
      linking {
        link(['core', 'boost'])
      }
    }
  }
}
```

---

## Tests

Define test executable targets. Tests are built and executed via the `check` Gradle task and support JUnit XML output.

**Use Case:** Create unit tests, integration tests, or performance benchmarks.

```groovy
cmake {
  tests {
    '<name>' {
      toolchains = List<String>                     // required
      
      headers {
        srcDir = String                             // optional
        srcDirs = List<String>                     // optional
      }
      
      sources {
        srcDir = String                             // optional
        srcDirs = List<String>                     // optional
      }
      
      compiling {
        options = List<String>                     // optional
        defines = List<String>                     // optional
      }
      
      linking {
        options(List<String>)                       // optional
        link(List<String>)
          .from(String)                             // optional
          .variant(String)                          // optional: 'Shared'|'Static'|'Interface'
          .visibility(String)                       // optional, default: 'Private'
      }
      
      outputName = String                           // optional
      stripDebug = boolean                          // optional, default: false
      testResultsXmlOutput = boolean               // optional, default: false
    }
  }
}
```

### Test Configuration Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `toolchains` | List<String> | — | **Required.** Which toolchains to build this test for |
| `headers.srcDir` | String | — | Single directory containing headers |
| `headers.srcDirs` | List<String> | — | Multiple directories for headers |
| `sources.srcDir` | String | — | Single directory containing sources |
| `sources.srcDirs` | List<String> | — | Multiple directories for sources |
| `compiling.options` | List<String> | — | Compiler flags for tests |
| `compiling.defines` | List<String> | — | Preprocessor defines (useful for `TEST_BUILD`) |
| `linking.options` | List<String> | — | Linker flags |
| `outputName` | String | test name | Override the test executable name |
| `stripDebug` | boolean | false | Strip debug symbols |
| `testResultsXmlOutput` | boolean | false | Generate JUnit XML report of test results |

### Example: Test Configuration

```groovy
cmake {
  tests {
    unit_tests {
      toolchains = ['gcc']
      sources { srcDirs = ['tests/unit'] }
      compiling {
        defines = ['TEST_BUILD', 'ENABLE_ASSERT']
      }
      linking {
        link(['core'])
      }
      testResultsXmlOutput = true
      stripDebug = false  // Keep debug info for test debugging
    }
  }
}
```

---

## Complete Example

```groovy
cmake {
  // System packages
  packages {
    opengl { moduleMode = true }
    boost { components = ['system', 'filesystem'] }
  }

  // Toolchains
  toolchains {
    gcc {
      operatingSystem = 'Linux'
      generator = 'Unix Makefiles'
      buildConfigs = ['debug', 'release']
      
      libraries {
        buildVariants = ['shared', 'static']
        compiling {
          options = ['-Wall', '-Wextra']
        }
      }
      
      applications {
        compiling {
          defines = ['RELEASE_VERSION']
        }
      }
      
      tests {
        testResultsXmlOutput = true
      }
    }
  }

  // Libraries
  libraries {
    graphics {
      toolchains = ['gcc']
      buildVariants = ['shared']
      sources { srcDirs = ['src/graphics'] }
      headers { srcDirs = ['include/graphics'] }
      linking {
        link(['opengl'])
          .variant('Shared')
      }
    }
    
    utils {
      toolchains = ['gcc']
      sources { srcDirs = ['src/utils'] }
      headers { srcDirs = ['include/utils'] }
    }
  }

  // Applications
  applications {
    viewer {
      toolchains = ['gcc']
      sources { srcDirs = ['src/viewer'] }
      linking {
        link(['graphics', 'utils'])
      }
    }
  }

  // Tests
  tests {
    graphics_tests {
      toolchains = ['gcc']
      sources { srcDirs = ['tests'] }
      linking {
        link(['graphics'])
      }
      testResultsXmlOutput = true
    }
  }
}
```
