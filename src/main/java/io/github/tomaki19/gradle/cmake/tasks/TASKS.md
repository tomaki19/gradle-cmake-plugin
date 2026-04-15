# Tasks Reference

The gradle-cmake-plugin automatically creates a set of Gradle tasks for managing the CMake build lifecycle. This document describes all available tasks and how to customize them.

## Task Naming Conventions

Most plugin tasks follow a naming pattern based on the component name and toolchain:

- **Build tasks:** `build-<component-name>-<toolchain-name>`
- **Check tasks:** `check-<test-name>-<toolchain-name>`
- **Configure tasks:** `configure-<toolchain-name>`
- **Assemble tasks:** `assemble-cmake-lists`, `assemble-<toolchain-name>-config`

---

## Assemble Tasks

**Implementation:** [`CMakeAssemble.java`](CMakeAssemble.java)

Assemble tasks generate the necessary CMake configuration files for the project.

### assemble-cmake-lists

Generates the main `CMakeLists.txt` file in the project root. This is the primary CMake build file that defines all libraries, applications, and tests for the project.

**Task Type:** `CMakeAssemble`  
**Runs:** Automatically before configure tasks  
**Output:** `CMakeLists.txt` in project root

### assemble-<toolchain>-config

Generates CMake module configuration files that expose all project libraries for use by other CMake projects. These files allow external projects to discover and link against your project's libraries.

**Pattern:** `assemble-<toolchain-name>-config`  
**Example:** `assemble-gcc-config`, `assemble-clang-config`  
**Task Type:** `CMakeAssemble`  
**Output:** `build/cmake/config/<project-name>-<toolchain>-config.cmake`

**Usage in Another Project:**
```cmake
list(APPEND CMAKE_MODULE_PATH "/path/to/project/build/cmake/config")
find_package(MyProject REQUIRED)
target_link_libraries(myapp PRIVATE MyProject::mylib)
```

---

## Configure Tasks

**Implementation:** [`CMakeConfigure.java`](CMakeConfigure.java)

Configure tasks execute the CMake configuration step, generating platform-specific build files (Makefiles, Ninja files, Visual Studio projects, etc.).

### configure-<toolchain>

Runs CMake in the specified toolchain's build directory. This step:
- Generates build system files (Makefiles, Ninja, etc.)
- Processes CMakeLists.txt
- Discovers libraries and dependencies
- Performs compiler capability checks

**Pattern:** `configure-<toolchain-name>`  
**Example:** `configure-gcc`, `configure-clang`, `configure-msvc`  
**Task Type:** `CMakeConfigure`  
**Output:** `build/cmake/build/<toolchain>/` directory with CMake cache and build files

**Dependencies:** Runs after `assemble-cmake-lists`

**Typical Usage:**
```bash
./gradlew configure-gcc
./gradlew configure-clang
```

---

## Build Tasks

**Implementation:** [`CMakeBuild.java`](CMakeBuild.java), [`CMakeBuildLibrary.java`](CMakeBuildLibrary.java), [`CMakeBuildExecutable.java`](CMakeBuildExecutable.java)

Build tasks compile libraries and executables using CMake's build system.

### build-<component-name>-<toolchain>

Compiles a specific library, application, or test for a given toolchain. Each component can be built independently or as part of a larger build.

**Pattern:** `build-<name>-<toolchain>`  
**Examples:**
- `build-mylib-gcc` - Build library "mylib" with gcc
- `build-myapp-clang` - Build application "myapp" with clang
- `build-mytest-gcc` - Build test "mytest" with gcc

**Task Type:** `CMakeBuild*` (specific subclass depends on component type)  
**Output:** 
- Libraries: `build/cmake/install/<toolchain>/lib/`
- Applications: `build/cmake/install/<toolchain>/bin/`

**Artifact Naming:** `<name>-<toolchain>-<link-type>-<build-config>`
- Example: `mylib-gcc-shared-debug`, `mylib-gcc-static-release`

**Dependencies:** Runs after `configure-<toolchain>`

### build-all-<toolchain>

Convenience task that builds all libraries and applications for a specific toolchain. Useful when you want to build everything but target a particular compiler.

**Pattern:** `build-all-<toolchain>`  
**Examples:** `build-all-gcc`, `build-all-clang`  
**Effect:** Runs all `build-*-<toolchain>` tasks in dependency order

**Typical Usage:**
```bash
./gradlew build-all-gcc      # Build everything with gcc
./gradlew build-all-clang    # Build everything with clang
./gradlew build-myapp-gcc    # Build only myapp with gcc
```

---

## Check Tasks

**Implementation:** [`CMakeCheck.java`](CMakeCheck.java)

Check tasks execute tests using CMake's `ctest` command. Test results can optionally be exported as JUnit XML for CI/CD integration.

### check-<test-name>-<toolchain>

Executes a specific test for a given toolchain. Tests are discovered and managed by CMake.

**Pattern:** `check-<test-name>-<toolchain>`  
**Examples:** `check-unit_tests-gcc`, `check-integration_tests-clang`  
**Task Type:** `CMakeCheck`  
**Output:** Test output to console; optionally JUnit XML if configured

**Dependencies:** Runs after `build-<test-name>-<toolchain>`

### check-all-<toolchain>

Executes all tests for a specific toolchain.

**Pattern:** `check-all-<toolchain>`  
**Examples:** `check-all-gcc`, `check-all-clang`  
**Effect:** Runs all `check-*-<toolchain>` tasks

### Customizing Check Tasks

You can customize test execution by configuring `CMakeCheck` task properties:

```groovy
tasks.withType(io.github.tomaki19.gradle.cmake.tasks.CMakeCheck) {
    // Add custom ctest arguments
    additionalArguments = [
        '--verbose',                    // Verbose output
        '--output-on-failure',          // Show output for failed tests
        '--parallel', '4',              // Run tests in parallel
        '--timeout', '300'              // 5 minute timeout per test
    ]
}
```

### JUnit XML Output

When configured in the extension, tests can export results as JUnit XML:

```groovy
cmake {
  tests {
    unit_tests {
      toolchains = ['gcc']
      testResultsXmlOutput = true  // Enable JUnit XML output
    }
  }
}
```

Results are generated at: `build/cmake/build/<toolchain>/junit_<test-name>.xml`

**Usage in CI/CD:**
```groovy
// Example: Jenkins/GitHub Actions configuration
tasks.withType(io.github.tomaki19.gradle.cmake.tasks.CMakeCheck) {
    additionalArguments = [
        '--output-junit',
        "${project.buildDir}/reports/tests/"
    ]
}
```

**Example: Customizing Tests by Toolchain**

```groovy
tasks.named('check-unit_tests-gcc', io.github.tomaki19.gradle.cmake.tasks.CMakeCheck) {
    additionalArguments = ['--verbose']
}

tasks.named('check-unit_tests-clang', io.github.tomaki19.gradle.cmake.tasks.CMakeCheck) {
    additionalArguments = ['--output-on-failure']
}
```

---

## Standard Gradle Tasks

The plugin integrates with Gradle's standard task lifecycle:

### build

The `build` task compiles all libraries and applications for all toolchains and all build configurations.

**Dependencies:** Depends on all `build-*-*` tasks  
**Typical Usage:**
```bash
./gradlew build     # Full build for all toolchains
```

### check

The `check` task runs all tests for all toolchains and all build configurations.

**Dependencies:** Depends on all `check-*-*` tasks  
**Typical Usage:**
```bash
./gradlew check     # Run all tests
```

### clean

Cleans all build artifacts.

**Removes:**
- `build/cmake/` directory
- All compiled binaries and artifacts

---

## Custom Exec Tasks

**Implementation:** [`CMakeCustomExec.java`](CMakeCustomExec.java)

Custom exec tasks allow you to register arbitrary commands to run in the context of a CMake build. This is useful for code analysis, test coverage, benchmarking, or other toolchain-specific operations.

### Registering Custom Tasks

Use `cmake.register()` to define custom tasks. There are three registration levels:

#### Register for All Toolchains and Build Configs

```groovy
cmake.register('<name>') {
    baseCommand = '<executable>'
    baseArguments = ['<argument>', ...]      // optional
    additionalArguments = ['<argument>', ...] // optional
    // Available variables:
    // - toolchainName: The toolchain name
    // - buildConfig: The build configuration (debug, release, etc.)
    // - compileCommands: Path to compile_commands.json
}
```

Creates tasks: `<name>-<toolchain>-<config>` for each toolchain and build config.

#### Register for Specific Toolchains

```groovy
cmake.register('<name>', ['<toolchain1>', '<toolchain2>']) {
    baseCommand = '<executable>'
    baseArguments = ['<argument>', ...]
}
```

Creates tasks only for the specified toolchains with all build configs.

#### Register for Specific Toolchains and Build Configs

```groovy
cmake.register('<name>', ['<toolchain1>'], ['debug']) {
    baseCommand = '<executable>'
    baseArguments = ['<argument>', ...]
}
```

Creates tasks only for the specified combinations.

### Example: Test Coverage Analysis

```groovy
cmake {
  // Configure toolchain with coverage flags
  toolchains {
    gcc {
      libraries {
        compiling {
          options = ['--coverage']
        }
        linking {
          options = ['--coverage']
        }
      }
      tests {
        compiling {
          options = ['--coverage']
        }
        linking {
          options = ['--coverage', '-lgcov']
        }
      }
    }
  }
  
  // Register coverage task that runs after tests
  register('coverage', ['gcc'], ['debug']) {
    dependsOn(tasks.withType(io.github.tomaki19.gradle.cmake.tasks.CMakeCheck))
    
    baseCommand = 'ctest'
    baseArguments = [
        '-T', 'Coverage',
        '--test-dir', "${project.buildDir}/cmake/build/gcc/"
    ]
  }
}
```

**Usage:**
```bash
./gradlew coverage-gcc-debug
```

### Example: Code Analysis with cppcheck

```groovy
cmake {
  register('cppcheck', ['gcc', 'clang']) {
    baseCommand = 'cppcheck'
    baseArguments = [
        '--enable=all',
        '--inconclusive',
        '--suppress=missingIncludeSystem'
    ]
    additionalArguments = ['src/']
  }
}
```

**Usage:**
```bash
./gradlew cppcheck-gcc-debug
./gradlew cppcheck-clang-release
```

### Example: Compile Database Export

```groovy
cmake {
  register('export-compdb', ['gcc']) {
    baseCommand = 'cp'
    baseArguments = [
        "${project.buildDir}/cmake/build/gcc/compile_commands.json",
        "${project.buildDir}/compile_commands.json"
    ]
  }
}
```

### Variables Available in Custom Tasks

Inside custom task blocks, the following variables are available:

| Variable | Type | Description |
|----------|------|-------------|
| `toolchainName` | String | Name of the current toolchain (e.g., "gcc", "clang") |
| `buildConfig` | String | Current build configuration (e.g., "debug", "release") |
| `compileCommands` | String | Path to `compile_commands.json` file |

**Example Usage:**

```groovy
cmake {
  register('analyze', ['gcc']) {
    baseCommand = 'clang-tidy'
    additionalArguments = [
        '-p', "${project.buildDir}/cmake/build/${toolchainName}/",
        'src/**/*.cpp'
    ]
  }
}
```

---

## Task Dependencies

Understanding task dependencies helps optimize your build:

```
assemble-cmake-lists
├── assemble-<toolchain>-config
└── configure-<toolchain>
    └── build-<component>-<toolchain>
        └── check-<test>-<toolchain>

build (standard task)
├── build-all-<toolchain1>
└── build-all-<toolchain2>
    ├── build-lib1-<toolchain>
    ├── build-lib2-<toolchain>
    ├── build-app1-<toolchain>
    └── ...

check (standard task)
├── check-all-<toolchain1>
└── check-all-<toolchain2>
    ├── check-test1-<toolchain>
    ├── check-test2-<toolchain>
    └── ...
```

---

## Common Build Scenarios

### Build Everything

```bash
./gradlew build
```

Builds all libraries and applications for all toolchains and configurations.

### Build Specific Toolchain

```bash
./gradlew build-all-gcc
./gradlew check-all-gcc
```

Build and test everything using gcc only.

### Build and Test Single Component

```bash
./gradlew build-myapp-gcc check-mytest-gcc
```

Build an application and run its tests.

### Run Tests with Verbose Output

```groovy
tasks.withType(io.github.tomaki19.gradle.cmake.tasks.CMakeCheck) {
    additionalArguments = ['--verbose', '--output-on-failure']
}
```

Then:
```bash
./gradlew check
```

### Parallel Test Execution

```groovy
tasks.withType(io.github.tomaki19.gradle.cmake.tasks.CMakeCheck) {
    additionalArguments = ['--parallel', '4']
}
```

### Export Test Results

```groovy
cmake {
  tests {
    unit_tests {
      testResultsXmlOutput = true
    }
  }
}

tasks.withType(io.github.tomaki19.gradle.cmake.tasks.CMakeCheck) {
    additionalArguments = [
        '--output-junit',
        "${project.buildDir}/reports/tests/"
    ]
}
```

Then parse `build/reports/tests/*.xml` in your CI/CD pipeline.
