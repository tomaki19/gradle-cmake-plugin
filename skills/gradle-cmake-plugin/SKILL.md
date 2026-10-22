---
name: gradle-cmake-plugin
description: Configure, build and test C/C++ projects that use the Gradle CMake plugin (`io.github.tomaki19.gradle-cmake-plugin`) — writing or changing the `cmake { }` block in build.gradle (toolchains, build configs, libraries, applications, tests, find_package packages, linking, custom exec and archive tasks) and running the generated Gradle tasks such as `build-<lib>-shared-gcc-debug` or `check-all-gcc`. Use this skill whenever a build.gradle contains `cmake {` or applies this plugin, whenever the user wants to add a C/C++ library, executable, test, compiler flag, package dependency, toolchain or cross-compilation setup to such a Gradle build, or asks why a CMake build driven by Gradle fails — even if they only mention CMake, gcc/clang/MSVC or "the native build".
---

# gradle-cmake-plugin

The plugin turns a `cmake { }` block in `build.gradle` into a generated
`CMakeLists.txt` plus one Gradle task per component, toolchain and build
config. Gradle is the source of truth: change `build.gradle`, never the
generated CMake files.

```groovy
plugins {
  id('io.github.tomaki19.gradle-cmake-plugin') version '<version>'
}
```

Requirements: Gradle ≥ 8, Java ≥ 17 for Gradle itself, CMake ≥ 3.21 and a
C/C++ compiler on the machine.

## Before you change anything

1. Read the existing `cmake { }` block and keep its style.
2. Check the plugin version in `plugins { }`. Version 0.9.8 and earlier
   behave differently in a few places; see *Older versions* at the end.
3. List the real task names instead of guessing them:
   `./gradlew tasks --group "cmake build"` (also `"cmake test"`; custom
   tasks appear under `--all`). Prefix `:<project>:` in multi-project builds.
   Names depend on the configuration and are always lowercase.
4. Use the project's wrapper (`./gradlew`, `gradlew.bat` on Windows).

Do not edit the generated `CMakeLists.txt` in the project directory or the
`*-module.cmake` files under `build/cmake/config/`: they are regenerated on
every build and `CMakeLists.txt` is deleted by `clean`.

## Calling the DSL correctly

- **Method calls, not assignments:** `buildConfigs 'Debug', 'Release'`,
  `toolchains 'gcc'`, `buildVariants SHARED, STATIC` — never `= [...]`
  (`Could not set unknown property`).
- **Specs are named arguments** (they may come before or after the names).
  A trailing map literal does not work:

  ```groovy
  options('-Wall', '-Wextra', visibility: PUBLIC)    // works
  link('core', variant: SHARED, visibility: PRIVATE) // works
  options('-Wall')                                   // works, default visibility PUBLIC
  link('core', [variant: SHARED])                    // FAILS: Could not find method link()
  ```
- `visibility` takes `PUBLIC` / `PRIVATE` or the strings `'Public'` /
  `'Private'`. `PUBLIC` propagates flags and links to everything that links
  the component; use `PRIVATE` for things only this target needs.
- Plain properties use `=`: `generator`, `environment`, `environmentFile`,
  `toolchainFile`, `operatingSystem`, `outputName`, `outputVersion`,
  `stripDebug`, `testResultsXmlOutput`, and `srcDirs` in `sources { }` /
  `headers { }`.
- Build config names go to CMake unchanged and are case-sensitive
  (`'Debug'`, `'Release'`, `'RelWithDebInfo'`, `'MinSizeRel'`).

## A working example

```groovy
cmake {
  toolchains {
    gcc {
      generator = 'Ninja'                      // default: CMake's default generator
      buildConfigs 'Debug', 'Release'          // default: all four CMake configs
      environment = [CC: 'gcc', CXX: 'g++']
      // environmentFile = file('env.sh')      // sourced before each command
      // toolchainFile = file('arm.cmake')     // cross-compilation
      // operatingSystem = WINDOWS             // LINUX, MAC_OS, WINDOWS; default: host
      libraries { compiling { options('-Wall', '-Wextra') } }
      tests { testResultsXmlOutput = true }    // JUnit XML for every test of this toolchain
    }
  }

  libraries {
    core {
      toolchains 'gcc'                         // default: every toolchain
      buildVariants SHARED, STATIC             // default: SHARED
      sources { srcDirs = ['src/core'] }       // default: src/<name>/sources
      headers { srcDirs = ['include/core'] }   // default: src/<name>/headers
      compiling { defines('CORE_EXPORTS', visibility: PRIVATE) }
    }
    hdr {                                      // no source files => INTERFACE library
      headers { srcDirs = ['include/hdr'] }
    }
  }

  applications {
    app {
      toolchains 'gcc'
      sources { srcDirs = ['src/app'] }
      linking {
        link('core', variant: SHARED, visibility: PRIVATE)
        link('hdr', variant: INTERFACE, visibility: PRIVATE)
      }
    }
  }

  tests {
    core_test {
      toolchains 'gcc'
      sources { srcDirs = ['test'] }           // exit code 0 = pass
      linking { link('core', variant: STATIC, visibility: PRIVATE) }
    }
  }
}
```

Notes:

- A library with **no source files** becomes an `INTERFACE` (header-only)
  library and gets no build task; link it with `variant: INTERFACE`.
- Library build variants are the union of the toolchain-level and the
  component-level `buildVariants`. `link()` can only use a variant that the
  dependency is actually built in.
- Each toolchain multiplies the tasks by its build configs, so keep
  `buildConfigs` to what the user needs.

### `link()` spec keys

| Key | Values | Default |
|---|---|---|
| `variant` | `SHARED`, `STATIC`, `INTERFACE` | `SHARED` |
| `visibility` | `PUBLIC`, `PRIVATE` (or `'Public'`, `'Private'`) | `PUBLIC` |
| `from` | a `packages` entry, or another project's name (`'core'`) or path (`':libs:core'`) | this project |
| `forBuild` (libraries only) | `SHARED`, `STATIC` or a list — link only when the library is built as that variant | all variants |

A name without `from:` is a component of this project and is not validated,
so a typo only fails later inside CMake. A project named in `from:` must
apply this plugin too. Link dependencies go on components; toolchain-level
`linking { }` blocks only take `options(...)`.

### Packages (`find_package`)

```groovy
packages {
  OpenGL { moduleMode = true }                   // MODULE mode; default CONFIG mode
  Boost  { components = ['system', 'filesystem'] }
  ZLIB   { moduleMode = true; properties = [ROOT: '/opt/zlib'] }  // set(ZLIB_ROOT "/opt/zlib") before find_package
  // Foo { targetPrefix = 'foo' }                // when targets are foo::… not Foo::…
}
// in a component:
linking {
  link('GL', from: 'OpenGL', visibility: PRIVATE)                  // OpenGL::GL
  link('system', 'filesystem', from: 'Boost', visibility: PRIVATE)  // Boost::system, Boost::filesystem
  link('ZLIB', from: 'ZLIB', visibility: PRIVATE)                  // ZLIB::ZLIB
}
```

- The package name goes to `find_package(<name> …)` unchanged, so use
  CMake's exact spelling (`OpenGL`, `Boost`, `ZLIB`).
- A link is a package link **only** when `from:` names a `packages` entry.
  The target is `<targetPrefix or package name>::<link name>`; give
  `targetPrefix` without `::`.
- `properties` become `set(<Package>_<KEY> "<value>")` before
  `find_package` (key upper-cased), which is how to pass hints such as
  `ROOT` or `USE_STATIC_LIBS`.
- If `find_package` fails during `configure-*`, the package is missing or
  needs a hint; report that rather than rewriting the build.

### Custom exec tasks

```groovy
['Debug', 'Release'].each { config ->
  tasks.registerExecTasks(
      [prefix: "cppcheck-gcc-${config.toLowerCase()}", toolchains: ['gcc'], buildConfigs: [config]],
      { task ->
          task.executable = 'cppcheck'
          task.args '--enable=all', '--project=' + task.compileCommands
      })
}
// ./gradlew cppcheck-gcc-debug
```

- The Gradle task name is **exactly `prefix`**; nothing is appended. If the
  filters match more than one toolchain/config/component, each match
  registers the same name and the build fails, so register one spec per
  combination with its own `prefix`.
- Filters: `toolchains`, `buildConfigs`, `components` (omit to match all).
  Component wildcards: `*`, `*library`, `*interface`, `*shared`, `*static`,
  `*executable`, `*application`, `*test`, or a component name.
- `task.compileCommands` is that build's `compile_commands.json`. The
  command runs after sourcing the toolchain's `environmentFile`.

### Archive tasks

```groovy
tasks.registerRuntimeArchiveTasks(
    [toolchains: ['gcc'], buildConfigs: ['Release'], components: ['*shared']],
    { task -> task.destinationDirectory.set(layout.buildDirectory.dir('dist')) })

tasks.registerDevelopArchiveTasks(
    [type: io.github.tomaki19.gradle.cmake.tasks.CMakeCustomTar,   // default: zip
     toolchains: ['gcc'], buildConfigs: ['Release'], components: ['*static']],
    { task -> task.destinationDirectory.set(layout.buildDirectory.dir('sdk')) })
```

Runtime archives hold binaries and their runtime dependencies; develop
archives hold a library in `lib/` and its headers in `include/`. Their names
are generated per component (below), so wildcards are fine here.

## Tasks

All names are lowercase. `<variant>` is `shared` or `static`.

| Task | What it does |
|---|---|
| `build` / `check` / `clean` | everything / all tests / remove generated files |
| `build-all-<tc>[-<config>]` | all components of one toolchain (and config) |
| `build-<lib>-<variant>-<tc>-<config>` | one library |
| `build-<app or test>-<tc>-<config>` | one executable |
| `check-all-<tc>[-<config>]`, `check-<test>-<tc>-<config>` | run tests via `ctest` |
| `configure-<tc>-<config>` | CMake configure only |
| `assemble-cmake-lists` | regenerate `CMakeLists.txt` |
| `zip-runtime-…`, `tar-runtime-…`, `zip-develop-…`, `tar-develop-…` | archives, only if registered |

Prefer the narrowest task that answers the question: after changing one
library, build `build-<lib>-<variant>-<tc>-debug` and run its tests before a
full `build`, which compiles every toolchain × config × variant.

## Build output

```
<project>/CMakeLists.txt                         # generated, do not edit
<project>/build/cmake/config/
  <tc>/<Config>/                                 # CMake build dir; config keeps its case (gcc/Debug)
    compile_commands.json, CMakeCache.txt
    libraries/…, applications/<name>/, tests/<name>/
    <test>-<tc>-<config>-junit.xml               # with testResultsXmlOutput = true
  <project>-<name>-<variant>-<tc>-<config>-module.cmake
```

Point clangd and similar tools at `<tc>/<Config>/compile_commands.json`, and
CI test reporting at `build/cmake/config/**/*-junit.xml`.

## When a build fails

- **`Could not find method options()/link()`**: a trailing map literal —
  use named arguments.
- **`Could not set unknown property`**: assignment to a method such as
  `buildConfigs` or `toolchains`.
- **Task not found**: list tasks again; names are lowercase, include the
  variant for libraries, and need `:<project>:` from another project.
- **`configure-*` fails**: read the CMake output for the first error —
  missing compiler, generator not installed (`Ninja`), failed
  `find_package`.
- **Compile/link errors**: from the C/C++ code or from `link` choices (a
  `PRIVATE` dependency whose headers appear in a public header, linking a
  variant the dependency is not built in).
- **Duplicate task name** from `registerExecTasks`: see *Custom exec tasks*.
- Stale state after big DSL changes: `./gradlew clean`, then build again.

## Older versions (0.9.8 and earlier)

- The plugin fails in the **root project** as soon as a component links
  another one (`Project with path ':<name>' could not be found`), and in
  nested subprojects (`:a:b`). Apply it in a subproject directly below the
  root.
- `options()`/`defines()` need a string visibility (`'Public'`), `link()`
  needs the constants (`PUBLIC`), and every call needs a spec — use `[:]`
  first when there is none: `options([:], '-Wall')`, `link([:], 'core')`.
- Without `generator`, configure fails (`Could not create named
  generator`); always set one.
- `testResultsXmlOutput` has no effect, and package `properties` are set
  after `find_package` with upper-cased values, so they are no use as hints.
- `forBuild` has no effect: a library's links apply to all its variants.
