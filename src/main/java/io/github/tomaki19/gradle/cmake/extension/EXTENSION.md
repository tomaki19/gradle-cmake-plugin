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
  options('-Wall', '-Wextra', [visibility: 'Public'])
  options('-O2', [visibility: 'Private'])
  defines('VERSION_1_0', 'ENABLE_LOGGING', [visibility: 'Public'])
  defines('INTERNAL_BUILD', [visibility: 'Private'])
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
  options('-static-libgcc', [visibility: 'Private'])
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
          options('-Wall', '-Wextra', '-fPIC', [visibility: 'Public'])
          defines('LINUX', [visibility: 'Public'])
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

      headers {
        srcDirs = ['include']               // defaults to src/<name>/headers
      }

      sources {
        srcDirs = ['src']                   // defaults to src/<name>/sources
      }

      compiling {
        options('-Wall', [visibility: 'Public'])
        defines('INTERNAL', [visibility: 'Private'])
      }

      linking {
        options('-Wl,-rpath,\$ORIGIN')
        link('utils', 'common', [variant: 'Shared', visibility: 'Public'])
        link('remoteLib', [variant: 'Shared', visibility: 'Private', from: 'otherProject'])
        link('sharedOnlyDep', [forBuild: 'Shared', variant: 'Shared', visibility: 'Private'])
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
| `variant` | `'Shared'` \| `'Static'` \| `'Interface'` | `'Shared'` | How to link the dependency |
| `visibility` | `'Public'` \| `'Private'` | `'Public'` | CMake target link visibility |
| `forBuild` | `'Shared'` \| `'Static'` \| `'Module'` | `'Shared'` | Apply this link spec only when building with the given build variant |

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
      options('-std=c++17', [visibility: 'Public'])
      defines('CORE_VERSION_2', [visibility: 'Public'])
    }

    linking {
      link('utils', [variant: 'Shared', visibility: 'Private'])
      link('system', 'filesystem', [variant: 'Shared', visibility: 'Public', from: 'boost'])
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
        options('-O3', [visibility: 'Private'])
        defines('APP_BUILD', [visibility: 'Private'])
      }

      linking {
        options('-Wl,--as-needed')
        link('core', 'utils', [variant: 'Shared', visibility: 'Private'])
        link('program_options', [from: 'boost', variant: 'Shared', visibility: 'Private'])
      }

      outputName = 'my-app'                  // optional
      stripDebug = false                     // optional, default: false
    }
  }
}
```

### Application `linking.link()` Spec Keys

| Key | Values | Default | Description |
|-----|--------|---------|-------------|
| `from` | String | `''` | Project name for inter-project dependencies |
| `variant` | `'Shared'` \| `'Static'` \| `'Interface'` | `'Shared'` | How to link the dependency |
| `visibility` | `'Public'` \| `'Private'` | `'Public'` | CMake target link visibility |

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
        defines('TEST_BUILD', 'ENABLE_ASSERT', [visibility: 'Private'])
      }

      linking {
        link('core', [variant: 'Shared', visibility: 'Private'])
      }

      outputName = 'my-test'                 // optional
      stripDebug = false                     // optional, default: false
      testResultsXmlOutput = false           // optional, default: false
    }
  }
}
```

### Test Configuration Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `testResultsXmlOutput` | boolean | false | Generate JUnit XML output via ctest's `--output-junit` |

---

## Custom Tasks

Register custom exec tasks and archive tasks via `cmake.tasks`. Tasks are applied during configuration, per toolchain, build config, and/or component.

### `cmake.tasks.registerExecTasks(Map spec, Action<CMakeCustomExec> action)`

Registers a custom command that runs as a Gradle task. The spec map controls when the task is created:

```groovy
cmake.tasks.registerExecTasks(
    [name: 'my-task', toolchains: ['gcc'], buildConfigs: ['Debug'], components: ['*library']],
    { task ->
        task.executable = 'mycommand'
        task.args '--option', task.compileCommands
    }
)
```

**Spec map keys:**

| Key | Type | Required | Description |
|-----|------|----------|-------------|
| `name` | String | **yes** | Task name (must be unique per matching combination) |
| `toolchains` | Collection<String> | no | Toolchain names to match; omit to match any toolchain (per-toolchain registration) |
| `buildConfigs` | Collection<String> | no | Build configs to match; omit for per-toolchain (no build-config) registration |
| `components` | Collection<String> | no | Component filter; omit for no-component registration |

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
| `"<name>"` | Exact component name |

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
          options('-Wall', '-Wextra', [visibility: 'Public'])
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
        link('opengl', [variant: 'Shared', visibility: 'Public'])
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
        link('graphics', 'utils', [variant: 'Shared', visibility: 'Private'])
      }
    }
  }

  tests {
    graphics_tests {
      toolchains 'gcc'
      sources { srcDirs = ['tests'] }
      linking {
        link('graphics', [variant: 'Shared', visibility: 'Private'])
      }
      testResultsXmlOutput = true
    }
  }

  tasks.registerExecTasks(
      [name: 'coverage-gcc-debug', toolchains: ['gcc'], buildConfigs: ['Debug'], components: ['*test']],
      { task ->
          task.executable = 'ctest'
          task.args '-T', 'Coverage', '--test-dir', task.workingDir.absolutePath
      }
  )
}
```
