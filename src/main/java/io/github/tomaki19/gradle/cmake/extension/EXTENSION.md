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

### Calling Conventions

- List-valued settings are **method calls**: `buildConfigs 'Debug', 'Release'`,
  `toolchains 'gcc'`, `buildVariants SHARED, STATIC` (not `= [...]`).
- `options()`, `defines()` and `link()` take their spec as **named arguments**, which may appear before or after
  the names: `link('core', variant: SHARED, visibility: PRIVATE)`. Without a spec, call them with the names only:
  `options('-Wall')`, `link('core')`. A trailing map literal (`link('core', [variant: SHARED])`) is not supported.
- `visibility` accepts the constants `PUBLIC` / `PRIVATE` or the strings `'Public'` / `'Private'` (any case).

---

## Packages

Define system libraries and external packages discovered via CMake's `find_package()`.

```groovy
cmake {
  packages {
    '<name>' {                          // exact find_package() name, e.g. 'OpenGL', 'Boost'
      targetPrefix = 'prefix'           // optional, without '::'
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
| `targetPrefix` | String | package name | Namespace of the imported targets, without `::` (e.g. `'foo'` for `foo::bar`) |
| `components` | Set<String> | — | Specific components to request (e.g., `['system', 'filesystem']` for Boost) |
| `properties` | Map<String,String> | — | Hints set before `find_package()`: `ROOT: '/opt/zlib'` becomes `set( ZLIB_ROOT "/opt/zlib" )` (key upper-cased, value unchanged) |

The package name is passed to `find_package()` unchanged, so use CMake's exact spelling.

Link a package's targets from a component with `from:` set to the package name. The linked target is
`<targetPrefix or package name>::<link name>`:

```groovy
linking {
  link('GL', from: 'OpenGL', visibility: PRIVATE)                  // OpenGL::GL
  link('system', 'filesystem', from: 'Boost', visibility: PRIVATE) // Boost::system, Boost::filesystem
}
```

### Example

```groovy
cmake {
  packages {
    OpenGL { moduleMode = true }
    Boost  { components = ['system', 'filesystem', 'thread'] }
    Qt6    { components = ['Core', 'Gui', 'Widgets'] }
    ZLIB   { moduleMode = true; properties = [ROOT: '/opt/zlib'] }
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
| `generator` | String | CMake's default | CMake generator (e.g., `'Ninja'`, `'Visual Studio 17 2022'`); omitted from the `cmake` call when not set |
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
  options('-Wall', '-Wextra', visibility: PUBLIC)
  options('-O2', visibility: PRIVATE)
  defines('VERSION_1_0', 'ENABLE_LOGGING', visibility: PUBLIC)
  defines('INTERNAL_BUILD', visibility: PRIVATE)
}
```

Both `options()` and `defines()` take one or more flag/define names and an optional named `visibility` argument:

| Spec Key | Values | Default | Description |
|----------|--------|---------|-------------|
| `visibility` | `PUBLIC` \| `PRIVATE` (or `'Public'` \| `'Private'`) | `PUBLIC` | CMake target property visibility |

Omit the visibility to use the default (`PUBLIC`):
```groovy
options('-Wall', '-Wextra')
defines('DEBUG_MODE')
```

### Linking Settings (`linking` block)

#### Link options (linker flags)

```groovy
linking {
  options('-Wl,-rpath,/usr/local/lib')
  options('-static-libgcc', visibility: PRIVATE)
}
```

Same spec as `compiling` — only the `visibility` key is supported.

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
          options('-Wall', '-Wextra', '-fPIC', visibility: PUBLIC)
          defines('LINUX', visibility: PUBLIC)
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
      toolchains 'gcc', 'clang'             // optional, default: all toolchains – method call, not assignment
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
        options('-Wall', visibility: PUBLIC)
        defines('INTERNAL', visibility: PRIVATE)
      }

      linking {
        options('-Wl,-rpath,\$ORIGIN')
        link('utils', 'common', variant: SHARED, visibility: PUBLIC)
        link('remoteLib', variant: SHARED, visibility: PRIVATE, from: 'otherProject')
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
| `buildVariants(...)` | method | `SHARED` | Library types: `SHARED`, `STATIC` (from `CMakeBuildVariant`); combined with the toolchain's `libraries.buildVariants`. A library without sources is always an `INTERFACE` library |
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
| `from` | String | this project | A `packages` entry, or another project: its name (`'core'`) or path (`':libs:core'`) |
| `variant` | `SHARED` \| `STATIC` \| `INTERFACE` | `SHARED` | How to link the dependency (from `CMakeLinkVariant`) |
| `visibility` | `PUBLIC` \| `PRIVATE` | `PUBLIC` | CMake target link visibility (from `CMakeVisibility`) |
| `forBuild` | `CMakeBuildVariant` | — | Accepted, but currently has no effect |

A name without `from:` refers to a component of this project. Linking another project requires that project to
apply this plugin as well.

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
      options('-std=c++17', visibility: PUBLIC)
      defines('CORE_VERSION_2', visibility: PUBLIC)
    }

    linking {
      link('utils', variant: SHARED, visibility: PRIVATE)
      link('system', 'filesystem', from: 'Boost', visibility: PUBLIC)
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
      toolchains 'gcc'                       // optional, default: all toolchains

      headers {
        srcDirs = ['include']               // defaults to src/<name>/headers
      }
      sources {
        srcDirs = ['src']                   // defaults to src/<name>/sources
      }

      compiling {
        options('-O3', visibility: PRIVATE)
        defines('APP_BUILD', visibility: PRIVATE)
      }

      linking {
        options('-Wl,--as-needed')
        link('core', 'utils', variant: SHARED, visibility: PRIVATE)
        link('program_options', from: 'Boost', visibility: PRIVATE)
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
      toolchains 'gcc'                       // optional, default: all toolchains

      headers {
        srcDirs = ['include']               // defaults to src/<name>/headers
      }
      sources {
        srcDirs = ['src']                   // defaults to src/<name>/sources
      }

      compiling {
        defines('TEST_BUILD', 'ENABLE_ASSERT', visibility: PRIVATE)
      }

      linking {
        link('core', variant: SHARED, visibility: PRIVATE)
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
| `testResultsXmlOutput` | boolean | toolchain `tests` setting, else false | Write a JUnit XML report via ctest's `--output-junit` to `build/cmake/config/<toolchain>/<BuildConfig>/<name>-<toolchain>-<buildconfig>-junit.xml` |

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

**Parameters:**

| Parameter | Type | Required | Description |
|-----|------|----------|-------------|
| `spec` | Map<String,Object> | **yes** | Specifications for task generation |
| `action` | Closure | **yes** | Task action for generated tasks |

**Spec map keys:**

| Key | Type | Required | Description |
|-----|------|----------|-------------|
| `prefix` | String | **yes** | The Gradle task name, used as-is |
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

> **Task name uniqueness:** The `prefix` from the spec is used directly as the Gradle task name; nothing is appended. If the spec matches multiple toolchains, buildConfigs, or components in a single build, each match attempts to register the same name, causing a conflict. Use specific `toolchains` + `buildConfigs` + `components` filters so each spec resolves to exactly one task registration, for example one spec per build config:
>
> ```groovy
> ['Debug', 'Release'].each { config ->
>   cmake.tasks.registerExecTasks(
>       [prefix: "cppcheck-gcc-${config.toLowerCase()}", toolchains: ['gcc'], buildConfigs: [config]],
>       { task -> task.executable = 'cppcheck'; task.args '--project=' + task.compileCommands })
> }
> ```

### `cmake.tasks.registerRuntimeArchiveTasks(Map spec, Action<AbstractArchiveTask> action)`

Registers an archive task (`zip-runtime-*`, or `tar-runtime-*` with `type: io.github.tomaki19.gradle.cmake.tasks.CMakeCustomTar` in the spec) that packages a component's runtime artifacts (binaries and their dependencies). Applies to libraries and executables. The spec takes the same `toolchains`, `buildConfigs` and `components` filters as above; task names are generated per component, so wildcards are fine.

```groovy
cmake.tasks.registerRuntimeArchiveTasks(
    [toolchains: ['gcc'], buildConfigs: ['Release'], components: ['*shared']],
    { task -> task.destinationDirectory.set(layout.buildDirectory.dir('dist')) }
)
```

The archive is pre-configured with the built binaries; the action can add further files or override the destination.

### `cmake.tasks.registerDevelopArchiveTasks(Map spec, Action<AbstractArchiveTask> action)`

Registers an archive task (`zip-develop-*`, or `tar-develop-*` with the `type` spec key) that packages a library's development artifacts (library + headers). Applies to libraries only.

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
    OpenGL { moduleMode = true }
    Boost  { components = ['system', 'filesystem'] }
  }

  toolchains {
    gcc {
      generator = 'Unix Makefiles'
      buildConfigs 'Debug', 'Release'

      libraries {
        buildVariants SHARED, STATIC
        compiling {
          options('-Wall', '-Wextra', visibility: PUBLIC)
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
        link('GL', from: 'OpenGL', visibility: PUBLIC)
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
        link('graphics', 'utils', variant: SHARED, visibility: PRIVATE)
      }
    }
  }

  tests {
    graphics_tests {
      toolchains 'gcc'
      sources { srcDirs = ['tests'] }
      linking {
        link('graphics', variant: SHARED, visibility: PRIVATE)
      }
      testResultsXmlOutput = true
    }
  }

  tasks.registerExecTasks(
      [prefix: 'coverage-gcc-debug', toolchains: ['gcc'], buildConfigs: ['Debug']],
      { task ->
          task.executable = 'ctest'
          task.args '-T', 'Coverage', '--test-dir', task.workingDir.absolutePath
      }
  )
}
```
