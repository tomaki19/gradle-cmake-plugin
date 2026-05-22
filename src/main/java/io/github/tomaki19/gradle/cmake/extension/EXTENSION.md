# Extension Configuration Reference

The `cmake` extension is the root configuration block for the gradle-cmake-plugin. It contains the following main sections:

```groovy
cmake {
  packages {...}      // System/external packages (find_package)
  toolchains {...}    // Build toolchains and compiler settings
  libraries {...}     // Libraries (shared,static,interface) to build
  applications {...}  // Executable applications to build
  tests {...}         // Test executables to build
  tasks {...}         // Custom exec and archive tasks
}
```

---

## Packages

Define system libraries and external packages discovered via CMake's `find_package()`.

```groovy
cmake {
  packages {
    '<name>' {
      targetPrefix = 'prefix::'         // optional
      components = ['comp1', 'comp2']   // optional
      properties = [key: 'value']       // optional
      moduleMode = false                // optional
    }
  }
}
```

### Package Configuration Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `moduleMode` | boolean | false | Use MODULE mode for `find_package()`; if false uses CONFIG mode |
| `targetPrefix` | String | — | Custom prefix for target names discovered by find_package |
| `components` | Set<String> | — | Specific components to request (e.g., `['system', 'filesystem']` for Boost) |
| `properties` | Map<String,String> | — | Additional CMake hints passed to find_package |

### Example

```groovy
cmake {
  packages {
    opengl { moduleMode = true }
    boost  { components = ['system', 'filesystem', 'thread'] }
    qt     { components = ['Core', 'Gui', 'Widgets'] }
    zlib   { targetPrefix = 'ZLIB::' }
  }
}
```

---

## Toolchains

Define one or more build toolchains. Each toolchain represents a compiler/generator combination.

```groovy
cmake {
  toolchains {
    '<name>' {
      operatingSystem = Linux                    // optional
      generator = 'Unix Makefiles'               // optional
      buildConfigs 'Debug', 'Release', ...       // optional
      environment = [CC: 'gcc', CXX: 'g++']      // optional
      environmentFile = file('env.sh')           // optional
      toolchainFile = file('toolchain.cmake')    // optional

      libraries {
        buildVariants SHARED, STATIC             // optional
        compiling { ... }
        linking { ... }
        stripDebug = false                       // optional
      }

      applications {
        compiling { ... }
        linking { ... }
        stripDebug = false                       // optional
      }

      tests {
        compiling { ... }
        linking { ... }
        stripDebug = false                       // optional, default: false
        testResultsXmlOutput = false             // optional, default: false
      }
    }
  }
}
```

### Toolchain Configuration Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `operatingSystem` | `OperatingSystem` | auto-detect | Target OS: `CMakeToolchain.LINUX`, `CMakeToolchain.MAC_OS`, `CMakeToolchain.WINDOWS` |
| `generator` | String | auto-detect | CMake generator (e.g., `'Ninja'`, `'Visual Studio 17 2022'`) |
| `buildConfigs` | method | `'Debug','Release','RelWithDebInfo','MinSizeRel'` | Build configurations; call as `buildConfigs 'Debug', 'Release'` |
| `environment` | Map<String,String> | — | Environment variables passed to CMake invocations |
| `environmentFile` | File | — | Shell script sourced before each CMake invocation |
| `toolchainFile` | File | — | CMake toolchain file (`-DCMAKE_TOOLCHAIN_FILE`) for cross-compilation |

> **Note:** `buildConfigs` is a **method call**, not a property assignment:
> ```groovy
> buildConfigs('Debug', 'Release')   // correct
> buildConfigs = ['Debug', 'Release'] // WRONG – does not compile
> ```
> Build config names are passed to CMake as-is and must match CMake conventions
> (case-sensitive: `'Debug'`, `'Release'`, `'RelWithDebInfo'`, `'MinSizeRel'`).

### Compilation Settings (`compiling` block)

Available in the `libraries`, `applications`, and `tests` sub-blocks of a toolchain. Also available directly on library/application/test components (see below).

```groovy
compiling {
  options('-Wall', '-Wextra', [visibility: PUBLIC])
  options('-O2', [visibility: PRIVATE])
  defines('VERSION_1_0', 'ENABLE_LOGGING', [visibility: PUBLIC])
  defines('INTERNAL_BUILD', [visibility: PRIVATE])
}
```

Both `options()` and `defines()` take one or more flag/define names as the first arguments followed by a spec map:

| Spec Key | Values | Default | Description |
|----------|--------|---------|-------------|
| `visibility` | `'Public'` \| `'Private'` | `'Public'` | CMake target property visibility |

Omit the visibility key to use the default (`Public`):
```groovy
options('-Wall', '-Wextra')
defines('DEBUG_MODE')
```

### Linking Settings (`linking` block)

#### Link options (linker flags)

```groovy
linking {
  options('-Wl,-rpath,/usr/local/lib')
  options('-static-libgcc', [visibility: PRIVATE])
}
```

Same map spec as `compiling` — only `visibility` key is supported.

#### Link dependencies

For the **toolchain-level** `libraries.linking` and `applications.linking` blocks the `link()` method is not available. Link dependencies are defined at the component level (see Libraries / Applications / Tests sections below).

### Example: Multiple Toolchains

```groovy
cmake {
  toolchains {
    gcc {
      generator = 'Unix Makefiles'
      buildConfigs 'Debug', 'Release'
      environment = [CC: 'gcc', CXX: 'g++']

      libraries {
        buildVariants SHARED, STATIC
        compiling {
          options('-Wall', '-Wextra', '-fPIC', [visibility: PUBLIC])
          defines('LINUX', [visibility: PUBLIC])
        }
        linking {
          options('-Wl,-rpath,\$ORIGIN')
        }
      }
    }

    clang {
      generator = 'Ninja'
      buildConfigs 'Debug', 'Release'
      environment = [CC: 'clang', CXX: 'clang++']

      applications {
        compiling {
          options('-Wall', '-O3')
        }
      }
    }
  }
}
```

---

## Libraries

Define library targets. Libraries can be shared, static, or interface (header-only).

```groovy
cmake {
  libraries {
    '<name>' {
      toolchains 'gcc', 'clang'             // required – method call, not assignment
      buildVariants SHARED, STATIC           // optional, default: [SHARED]
      outputName = 'custom_output_name'      // optional
      outputVersion = '1.2.3'               // optional

      headers {
        srcDirs = ['include']               // defaults to src/<name>/headers
      }

      sources {
        srcDirs = ['src']                   // defaults to src/<name>/sources
      }

      compiling {
        options('-Wall', [visibility: PUBLIC])
        defines('INTERNAL', [visibility: PRIVATE])
      }

      linking {
        options('-Wl,-rpath,\$ORIGIN')
        link('utils', 'common', [variant: SHARED, visibility: PUBLIC])
        link('remoteLib', [variant: SHARED, visibility: PRIVATE, from: 'otherProject'])
        link('sharedOnlyDep', [forBuild: 'Shared', variant: SHARED, visibility: PRIVATE])
      }

      stripDebug = false                    // optional, default: false
    }
  }
}
```

### Library Configuration Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `toolchains(...)` | method | all defined toolchains | Toolchains to build this library for: `toolchains 'gcc', 'clang'` |
| `buildVariants(...)` | method | `SHARED` | Library types: `SHARED`, `STATIC`, `MODULE` (from `CMakeBuildVariant`) |
| `outputName` | String | component name | Override the CMake target output name |
| `outputVersion` | String | project version | Override the CMake target `VERSION` property |
| `headers.srcDirs` | List<String> | `src/<name>/headers` | Header directories |
| `sources.srcDirs` | List<String> | `src/<name>/sources` | Source directories |
| `stripDebug` | boolean | false | Strip debug symbols |

> `toolchains` and `buildVariants` are **method calls**:
> ```groovy
> toolchains 'gcc', 'clang'        // correct
> toolchains = ['gcc', 'clang']    // WRONG
> buildVariants SHARED, STATIC     // correct (enum constants)
> buildVariants = ['Shared']       // WRONG
> ```

### Library `linking.link()` Spec Keys

| Key | Values | Default | Description |
|-----|--------|---------|-------------|
| `from` | String | `''` | Project name for inter-project dependencies |
| `variant` | `SHARED` \| `STATIC` \| `INTERFACE` | `SHARED` | How to link the dependency (from `CMakeLinkVariant`) |
| `visibility` | `PUBLIC` \| `PRIVATE` | `PUBLIC` | CMake target link visibility (from `CMakeVisibility`) |
| `forBuild` | `SHARED` \| `STATIC` | `SHARED` | Apply this link spec only when building with the given build variant (from `CMakeBuildVariant`) |

### Example

```groovy
libraries {
  utils {
    toolchains 'gcc'
    sources { srcDirs = ['src/utils'] }
    headers { srcDirs = ['include/utils'] }
  }

  core {
    toolchains 'gcc', 'clang'
    buildVariants SHARED, STATIC

    sources { srcDirs = ['src/core'] }
    headers { srcDirs = ['include/core'] }

    compiling {
      options('-std=c++17', [visibility: PUBLIC])
      defines('CORE_VERSION_2', [visibility: PUBLIC])
    }

    linking {
      link('utils', [variant: SHARED, visibility: PRIVATE])
      link('system', 'filesystem', [from: 'boost', variant: SHARED, visibility: PUBLIC])
    }
  }
}
```

---

## Applications

Define executable application targets.

```groovy
cmake {
  applications {
    '<name>' {
      toolchains 'gcc'                       // required

      headers {
        srcDirs = ['include']               // defaults to src/<name>/headers
      }
      sources {
        srcDirs = ['src']                   // defaults to src/<name>/sources
      }

      compiling {
        options('-O3', [visibility: PRIVATE])
        defines('APP_BUILD', [visibility: PRIVATE])
      }

      linking {
        options('-Wl,--as-needed')
        link('core', 'utils', [variant: SHARED, visibility: PRIVATE])
        link('program_options', [from: 'boost', variant: SHARED, visibility: PRIVATE])
      }

      outputName = 'my-app'                  // optional
      outputVersion = '2.0.0'               // optional, default: project version
      stripDebug = false                     // optional, default: false
    }
  }
}
```

### Application `linking.link()` Spec Keys

| Key | Values | Default | Description |
|-----|--------|---------|-------------|
| `from` | String | `''` | Project name for inter-project dependencies |
| `variant` | `SHARED` \| `STATIC` \| `INTERFACE` | `SHARED` | How to link the dependency (from `CMakeLinkVariant`) |
| `visibility` | `PUBLIC` \| `PRIVATE` | `PUBLIC` | CMake target link visibility (from `CMakeVisibility`) |

---

## Tests

Define test executable targets.

```groovy
cmake {
  tests {
    '<name>' {
      toolchains 'gcc'                       // required

      headers {
        srcDirs = ['include']               // defaults to src/<name>/headers
      }
      sources {
        srcDirs = ['src']                   // defaults to src/<name>/sources
      }

      compiling {
        defines('TEST_BUILD', 'ENABLE_ASSERT', [visibility: PRIVATE])
      }

      linking {
        link('core', [variant: SHARED, visibility: PRIVATE])
      }

      outputName = 'my-test'                 // optional
      outputVersion = '1.0.0'               // optional, default: project version
      stripDebug = false                     // optional, default: false
      testResultsXmlOutput = false           // optional, default: false
    }
  }
}
```

### Test `linking.link()` Spec Keys

| Key | Values | Default | Description |
|-----|--------|---------|-------------|
| `from` | String | `''` | Project name for inter-project dependencies |
| `variant` | `SHARED` \| `STATIC` \| `INTERFACE` | `SHARED` | How to link the dependency (from `CMakeLinkVariant`) |
| `visibility` | `PUBLIC` \| `PRIVATE` | `PUBLIC` | CMake target link visibility (from `CMakeVisibility`) |

### Test Configuration Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `outputName` | String | component name | Override the CMake target output name |
| `outputVersion` | String | project version | Override the CMake target `VERSION` property |
| `testResultsXmlOutput` | boolean | false | Generate JUnit XML output via ctest's `--output-junit` |

---

## Custom Tasks

Register custom exec tasks and archive tasks via `cmake.tasks`. Tasks are applied during configuration, per toolchain, build config, and/or component.

### `cmake.tasks.registerExecTasks(Map spec, Action<CMakeCustomExec> action)`

Registers a custom command that runs as a Gradle task. The spec map controls when the task is created:

```groovy
cmake.tasks.registerExecTasks(
    [prefix: 'my-task-prefix', toolchains: ['gcc'], buildConfigs: ['Debug'], components: ['*library']],
    { task ->
        task.executable = 'mycommand'
        task.args '--option', task.compileCommands
    }
)
```

**Spec map keys:**
| Parameter | Type | Required | Description |
|-----|------|----------|-------------|
| `spec` | Map<String,Object> | **yes** | Specifications for task generation |
| `action` | Closure | **yes** | Task action for generated tasks |

**Spec map keys:**

| Key | Type | Required | Description |
|-----|------|----------|-------------|
| `prefix` | String | **yes** | Unique task name prefix |
| `toolchains` | Collection<String> | no | Toolchain filter, omit to match any toolchain |
| `buildConfigs` | Collection<String> | no | Build configs filter, omit to match any build-config |
| `components` | Collection<String> | no | Component filter, omit to match any component |

**Component filter wildcards:**

| Value | Matches |
|-------|---------|
| `"*"` | All components |
| `"*library"` | All libraries (shared, static, interface) |
| `"*interface"` | Interface libraries only |
| `"*shared"` | Shared libraries only |
| `"*static"` | Static libraries only |
| `"*executable"` | All executables (applications and tests) |
| `"*application"` | Applications only |
| `"*test"` | Tests only |

**In the action closure, the `CMakeCustomExec` task exposes:**
- All standard `Exec` task properties (`executable`, `args`, `workingDir`, …)
- `task.compileCommands` — absolute path to the `compile_commands.json` for the current toolchain/buildConfig

> **Task name uniqueness:** The `name` from the spec is used directly as the Gradle task name. If the spec matches multiple toolchains, buildConfigs, or components in a single build, each match attempts to register the same name, causing a conflict. Use specific `toolchains` + `buildConfigs` + `components` filters so each spec resolves to exactly one task registration.

### `cmake.tasks.registerRuntimeArchiveTasks(Map spec, Action<AbstractArchiveTask> action)`

Registers a ZIP archive task (`zip-runtime-*`) that packages a component's runtime artifacts (binaries and their dependencies). Applies to libraries and executables.

```groovy
cmake.tasks.registerRuntimeArchiveTasks(
    [toolchains: ['gcc'], buildConfigs: ['Release'], components: ['*shared']],
    { task -> task.destinationDirectory.set(layout.buildDirectory.dir('dist')) }
)
```

The archive is pre-configured with the built binaries; the action can add further files or override the destination.

### `cmake.tasks.registerDevelopArchiveTasks(Map spec, Action<AbstractArchiveTask> action)`

Registers a ZIP archive task (`zip-develop-*`) that packages a library's development artifacts (static library + headers). Applies to libraries only.

```groovy
cmake.tasks.registerDevelopArchiveTasks(
    [toolchains: ['gcc'], buildConfigs: ['Release'], components: ['*static']],
    { task -> task.destinationDirectory.set(layout.buildDirectory.dir('sdk')) }
)
```

The archive is pre-configured with the static library (into `lib/`) and headers (into `include/`).

---

## Complete Example

```groovy
cmake {
  packages {
    opengl { moduleMode = true }
    boost  { components = ['system', 'filesystem'] }
  }

  toolchains {
    gcc {
      generator = 'Unix Makefiles'
      buildConfigs 'Debug', 'Release'

      libraries {
        buildVariants SHARED, STATIC
        compiling {
          options('-Wall', '-Wextra', [visibility: PUBLIC])
        }
      }

      tests {
        testResultsXmlOutput = true
      }
    }
  }

  libraries {
    graphics {
      toolchains 'gcc'
      buildVariants SHARED
      sources { srcDirs = ['src/graphics'] }
      headers { srcDirs = ['include/graphics'] }
      linking {
        link('opengl', [variant: SHARED, visibility: PUBLIC])
      }
    }

    utils {
      toolchains 'gcc'
      sources { srcDirs = ['src/utils'] }
      headers { srcDirs = ['include/utils'] }
    }
  }

  applications {
    viewer {
      toolchains 'gcc'
      sources { srcDirs = ['src/viewer'] }
      linking {
        link('graphics', 'utils', [variant: SHARED, visibility: PRIVATE])
      }
    }
  }

  tests {
    graphics_tests {
      toolchains 'gcc'
      sources { srcDirs = ['tests'] }
      linking {
        link('graphics', [variant: SHARED, visibility: PRIVATE])
      }
      testResultsXmlOutput = true
    }
  }

  tasks.registerExecTasks(
      [prefix: 'coverage', toolchains: ['gcc'], buildConfigs: ['Debug'], components: ['*test']],
      { task ->
          task.executable = 'ctest'
          task.args '-T', 'Coverage', '--test-dir', task.workingDir.absolutePath
      }
  )
}
```
