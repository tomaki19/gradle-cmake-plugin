# Tasks Reference

The gradle-cmake-plugin registers Gradle tasks that manage the full CMake build lifecycle. All task names are derived from component names, link variants, toolchain names, and build configs — always **lowercased**.

---

## Task Naming Conventions

| Task type | Pattern |
|-----------|---------|
| Assemble CMakeLists | `assemble-cmake-lists` |
| Assemble module file | `assemble-<name>-<linkvariant>-<toolchain>-<buildconfig>-module` |
| Configure | `configure-<toolchain>-<buildconfig>` |
| Build library | `build-<name>-<linkvariant>-<toolchain>-<buildconfig>` |
| Build application or test | `build-<name>-<toolchain>-<buildconfig>` |
| Build-all (toolchain) | `build-all-<toolchain>` |
| Build-all (toolchain + config) | `build-all-<toolchain>-<buildconfig>` |
| Check (test) | `check-<name>-<toolchain>-<buildconfig>` |
| Check-all (toolchain) | `check-all-<toolchain>` |
| Check-all (toolchain + config) | `check-all-<toolchain>-<buildconfig>` |
| Runtime zip archive | `zip-runtime-<name>-<linkvariant>-<toolchain>-<buildconfig>` (library) |
| Runtime zip archive | `zip-runtime-<name>-<toolchain>-<buildconfig>` (application/test) |
| Runtime tar archive | `tar-runtime-<name>-<linkvariant>-<toolchain>-<buildconfig>` (library) |
| Runtime tar archive | `tar-runtime-<name>-<toolchain>-<buildconfig>` (application/test) |
| Develop zip archive | `zip-develop-<name>-<linkvariant>-<toolchain>-<buildconfig>` |
| Develop tar archive | `tar-develop-<name>-<linkvariant>-<toolchain>-<buildconfig>` |
| Custom exec | `<name>-<toolchain>-<buildconfig>` (when registered per buildConfig) |
| Clean CMakeLists | `clean-cmake-lists` |

**Example names** for a library `core` (shared), toolchain `gcc`, build config `Debug`:

- `build-core-shared-gcc-debug`
- `check-core-shared-gcc-debug` (if core is a test library)
- `assemble-core-shared-gcc-debug-module`
- `configure-gcc-debug`
- `zip-runtime-core-shared-gcc-debug`
- `zip-develop-core-shared-gcc-debug`

---

## Assemble Tasks

**Implementation:** [`CMakeAssemble.java`](CMakeAssemble.java)

### `assemble-cmake-lists`

Generates the `CMakeLists.txt` file in the project root directory. This file defines all libraries, applications, and tests. It is re-generated whenever the Gradle build file changes.

**Depends on:** All `assemble-*-module` tasks
**Output:** `CMakeLists.txt` in project root

### `assemble-<name>-<linkvariant>-<toolchain>-<buildconfig>-module`

Generates a CMake module configuration file that exposes a library for use by other CMake projects. Created for each library variant/toolchain/buildConfig combination.

**Output:** `build/cmake/config/<name>-<linkvariant>-<toolchain>-<buildconfig>-module.cmake`

**Usage in another CMake project:**
```cmake
list(APPEND CMAKE_MODULE_PATH "/path/to/project/build/cmake/config")
find_package(MyProject REQUIRED)
target_link_libraries(myapp PRIVATE MyProject::core-shared)
```

### `clean-cmake-lists`

Deletes the generated `CMakeLists.txt` from the project root. Wired as a dependency of the standard `clean` task.

---

## Configure Tasks

**Implementation:** [`CMakeConfigure.java`](CMakeConfigure.java)

### `configure-<toolchain>-<buildconfig>`

Runs `cmake -S … -B … -G …` to configure the CMake build for one toolchain/buildConfig combination. Generates platform-specific build files (Makefiles, Ninja files, Visual Studio projects, etc.).

**Depends on:** `assemble-cmake-lists`
**Output:** `build/cmake/config/<toolchain>/<buildconfig>/` (CMake build directory)

```bash
./gradlew configure-gcc-debug
./gradlew configure-clang-release
```

---

## Build Tasks

**Implementation:** [`CMakeBuildLibrary.java`](CMakeBuildLibrary.java), [`CMakeBuildExecutable.java`](CMakeBuildExecutable.java)

### `build-<name>-<linkvariant>-<toolchain>-<buildconfig>` (library)

Compiles one library variant for one toolchain/buildConfig.

```bash
./gradlew build-core-shared-gcc-debug
./gradlew build-utils-static-clang-release
```

### `build-<name>-<toolchain>-<buildconfig>` (application or test)

Compiles one application or test executable.

```bash
./gradlew build-viewer-gcc-release
./gradlew build-unit-tests-gcc-debug
```

**Depends on:** `configure-<toolchain>-<buildconfig>`
**Output:** `build/cmake/config/<toolchain>/<buildconfig>/<name>-[<linkvariant>-]<toolchain>-<buildconfig>/`

### `build-all-<toolchain>`

Convenience task: builds all libraries and applications for all build configs of one toolchain.

```bash
./gradlew build-all-gcc
```

### `build-all-<toolchain>-<buildconfig>`

Builds all libraries and applications for one toolchain/buildConfig.

```bash
./gradlew build-all-gcc-release
```

---

## Check Tasks

**Implementation:** [`CMakeCheck.java`](CMakeCheck.java)

Runs `ctest` to execute tests. Only created for components defined in the `tests {}` block.

### `check-<name>-<toolchain>-<buildconfig>`

Runs one test executable via ctest.

```bash
./gradlew check-unit-tests-gcc-debug
./gradlew check-integration-tests-clang-release
```

**Depends on:** `build-<name>-<toolchain>-<buildconfig>`

### `check-all-<toolchain>`

Runs all tests for all build configs of one toolchain.

```bash
./gradlew check-all-gcc
```

### `check-all-<toolchain>-<buildconfig>`

Runs all tests for one toolchain/buildConfig.

```bash
./gradlew check-all-gcc-debug
```

### JUnit XML Output

Enable per-test XML output in the extension:

```groovy
cmake {
  tests {
    unit_tests {
      toolchains 'gcc'
      testResultsXmlOutput = true
    }
  }
}
```

ctest writes the JUnit XML to the CMake build directory. Integrate with CI by pointing your test results parser at `build/cmake/config/<toolchain>/<buildconfig>/`.

---

## Archive Tasks

**Implementation:** [`CMakeCustomZip.java`](CMakeCustomZip.java)

Archive tasks are **not** created automatically. They are registered via `cmake.tasks.registerRuntimeArchiveTasks(...)` or `cmake.tasks.registerDevelopArchiveTasks(...)` in `build.gradle` (see [Extension Configuration Guide](../extension/EXTENSION.md#custom-tasks)).

### `zip-runtime-<name>-<linkvariant>-<toolchain>-<buildconfig>` (library)
### `zip-runtime-<name>-<toolchain>-<buildconfig>` (application/test)

Packages a component's runtime binaries and their resolved runtime dependencies into a ZIP file. Pre-configured with the build output directory; the action closure can adjust the destination or add files.

```bash
./gradlew zip-runtime-core-shared-gcc-release
```

### `zip-develop-<name>-<linkvariant>-<toolchain>-<buildconfig>`

Packages a library's development artifacts (the static/shared library into `lib/`, its public headers into `include/`) into a ZIP file. Only applicable to library components.

```bash
./gradlew zip-develop-core-static-gcc-release
```

**Depends on:** The corresponding `build-*` task.

---

## Standard Gradle Tasks

The plugin wires into the standard Gradle lifecycle:

| Task | Effect |
|------|--------|
| `assemble` | Depends on `assemble-cmake-lists` |
| `build` | Depends on all `build-all-<toolchain>` tasks |
| `check` | Depends on all `check-all-<toolchain>` tasks |
| `clean` | Depends on `clean-cmake-lists` (deletes `CMakeLists.txt` from project root) |

---

## Custom Exec Tasks

**Implementation:** [`CMakeCustomExec.java`](CMakeCustomExec.java)

Registered via `cmake.tasks.registerExecTasks(Map spec, Action<CMakeCustomExec> action)`. See [Extension Configuration Guide — Custom Tasks](../extension/EXTENSION.md#custom-tasks) for full registration options.

The action receives a `CMakeCustomExec` task with all standard `Exec` task properties plus:

| Property | Type | Description |
|----------|------|-------------|
| `compileCommands` | String | Absolute path to `compile_commands.json` for the current toolchain/buildConfig |

The task is run in a shell (`sh -c` on Unix, `cmd /c` on Windows) and will source the toolchain's `environmentFile` before the command if configured.

### Task name

Custom exec tasks are registered with the `name` key from the spec. When registered **per toolchain** (no `buildConfigs` in spec), the name is used as-is. It is the caller's responsibility to ensure the name is unique across all matching combinations.

### Example: Static analysis with cppcheck

```groovy
// Register for each build config individually to get unique names
cmake.tasks.registerExecTasks(
    [prefix: 'cppcheck', toolchains: ['gcc'], buildConfigs: ['Debug', 'Release']],
    { task ->
        task.executable = 'cppcheck'
        task.args '--enable=all', '--project', task.compileCommands
    }
}
```

```bash
./gradlew cppcheck-gcc-debug
./gradlew cppcheck-gcc-release
```

### Example: Test coverage with gcov/lcov

```groovy
cmake.tasks.registerExecTasks(
    [prefix: 'coverage', toolchains: ['gcc'], buildConfigs: ['Debug']],
    { task ->
        task.executable = 'ctest'
        task.args '-T', 'Coverage', '--test-dir', task.workingDir.absolutePath
    }
)
```

```bash
./gradlew coverage-gcc-debug
```

---

## Task Dependencies Overview

```
assemble-cmake-lists
└── assemble-<name>-<variant>-<toolchain>-<config>-module  (per library)

configure-<toolchain>-<config>
└── depends on: assemble-cmake-lists

build-<name>-[<variant>-]<toolchain>-<config>
└── depends on: configure-<toolchain>-<config>

check-<name>-<toolchain>-<config>
└── depends on: build-<name>-<toolchain>-<config>

zip-runtime-* / zip-develop-*
└── depends on: build-*

build-all-<toolchain>-<config>
└── depends on: all build-*-<toolchain>-<config>

build-all-<toolchain>
└── depends on: build-all-<toolchain>-<config>  (all configs)

check-all-<toolchain>-<config>
└── depends on: all check-*-<toolchain>-<config>

check-all-<toolchain>
└── depends on: check-all-<toolchain>-<config>  (all configs)

build (lifecycle)
└── depends on: build-all-<toolchain>  (all toolchains)

check (lifecycle)
└── depends on: check-all-<toolchain>  (all toolchains)

clean (lifecycle)
└── depends on: clean-cmake-lists
```

---

## Build Output Structure

```
<project root>/
└── CMakeLists.txt                              # Generated by assemble-cmake-lists

build/cmake/
└── config/
    ├── <toolchain>/
    │   └── <buildconfig>/                      # CMake build directory (configure output)
    │       ├── CMakeCache.txt
    │       ├── compile_commands.json
    │       ├── libraries/<variant>/<name>/     # Build output per library
    │       ├── applications/<name>/            # Build output per application
    │       └── tests/<name>/                   # Build output per test
    └── <name>-<variant>-<toolchain>-<buildconfig>-module.cmake  # Module files
```
