# gradle-cmake-plugin

A Gradle plugin that seamlessly integrates CMake into your Gradle build workflow for C/C++ projects. This plugin bridges Gradle and CMake, enabling you to leverage Gradle's dependency management, conventions, and task system while maintaining full control over native compilation with CMake.

## Features

- **Multi-Project Support** - Configure and build multiple projects in your workspace
- **Dependency Management** - Link against system packages (via CMake's `find_package`) and local project libraries
- **CMake Integration** - Automatic CMake configuration, compilation, and testing through Gradle tasks
- **Multi-Toolchain/Cross-Compilation Support** - Configure and build against multiple compilers and CMake generators
- **Multi-Configuration Builds** - Debug, Release, RelWithDebInfo, MinSizeRel, and custom build configurations per toolchain
- **Library & Application Management** - Define shared, static and interface libraries and executables with fine-grained compilation and linking control
- **Test Automation** - Built-in test execution via `ctest` with optional JUnit XML output for CI/CD integration
- **Custom Tasks** - Register custom CMake-related commands (coverage, linting, analysis) and archive tasks (runtime ZIP, develop SDK ZIP)
- **Artifact Organization** - Automatic Gradle configurations and artifacts for runtime and development distributions

## Installation

Add the plugin to your `build.gradle`:

```groovy
plugins {
  id('io.github.tomaki19.gradle-cmake-plugin') version '<version>'
}
```

## Requirements

- **Gradle** >= 8
- **Java** >= 17 (JVM, not the C/C++ compiler)
- **CMake** >= 3.21
- **C/C++ compiler** (gcc, clang, MSVC, etc.)

## Quick Start

```groovy
plugins {
  id('io.github.tomaki19.gradle-cmake-plugin') version '<version>'
}

cmake {
  toolchains {
    gcc {
      buildConfigs 'Debug', 'Release'     // method call, not assignment
    }
  }

  libraries {
    mylib {
      toolchains 'gcc'                    // method call, not assignment
      sources { srcDirs = ['src/lib'] }
      headers { srcDirs = ['include'] }
    }
  }

  applications {
    myapp {
      toolchains 'gcc'
      sources { srcDirs = ['src/app'] }
      linking {
        link([variant: 'Shared', visibility: 'Private'], 'mylib')
      }
    }
  }

  tests {
    mytest {
      toolchains 'gcc'
      sources { srcDirs = ['src/test'] }
      linking {
        link([variant: 'Shared', visibility: 'Private'], 'mylib')
      }
      testResultsXmlOutput = true
    }
  }
}
```

### Common Build Commands

See the [Tasks Reference](src/main/java/io/github/tomaki19/gradle/cmake/tasks/TASKS.md) for the full list of tasks.

```bash
# Build all components for all toolchains and build configs
./gradlew build

# Build all components for a specific toolchain (all build configs)
./gradlew build-all-gcc

# Build all components for a specific toolchain and build config
./gradlew build-all-gcc-debug

# Build a specific library (includes link variant and build config)
./gradlew build-mylib-shared-gcc-debug

# Build a specific application
./gradlew build-myapp-gcc-release

# Run all tests
./gradlew check

# Run tests for a specific toolchain
./gradlew check-all-gcc

# Run a specific test
./gradlew check-mytest-gcc-debug
```

## Documentation

- **[Extension Configuration Guide](src/main/java/io/github/tomaki19/gradle/cmake/extension/EXTENSION.md)** — All configuration options for toolchains, libraries, applications, tests, and custom tasks
- **[Tasks Reference](src/main/java/io/github/tomaki19/gradle/cmake/tasks/TASKS.md)** — Complete reference of all generated tasks, naming conventions, and dependencies

## Examples

### Multi-Toolchain Build

See [Toolchains](src/main/java/io/github/tomaki19/gradle/cmake/extension/EXTENSION.md#toolchains) for all options.

```groovy
cmake {
  toolchains {
    gcc {
      generator = 'Unix Makefiles'
      buildConfigs 'Debug', 'Release'
      environment = [CC: 'gcc', CXX: 'g++']
    }
    clang {
      generator = 'Ninja'
      buildConfigs 'Debug', 'Release'
      environment = [CC: 'clang', CXX: 'clang++']
    }
  }

  libraries {
    core {
      toolchains 'gcc', 'clang'
      buildVariants SHARED, STATIC
      sources { srcDirs = ['src'] }
      headers { srcDirs = ['include'] }
      compiling {
        options([visibility: 'Public'], '-Wall', '-Wextra')
        defines([visibility: 'Public'], 'CORE_VERSION_2')
      }
    }
  }
}
```

### Linking Against System Libraries

See [Packages](src/main/java/io/github/tomaki19/gradle/cmake/extension/EXTENSION.md#packages) for all options.

```groovy
cmake {
  packages {
    opengl { moduleMode = true }
    boost  { components = ['system', 'filesystem'] }
  }

  applications {
    renderer {
      toolchains 'gcc'
      sources { srcDirs = ['src'] }
      linking {
        link([variant: 'Shared', visibility: 'Private'], 'opengl')
        link([variant: 'Shared', visibility: 'Private'], 'boost')
      }
    }
  }
}
```

### Custom Exec Task (Code Analysis)

See [Custom Tasks](src/main/java/io/github/tomaki19/gradle/cmake/extension/EXTENSION.md#custom-tasks) for full registration options.

```groovy
cmake {
  // Register a cppcheck analysis task for each build config
  ['Debug', 'Release'].each { config ->
    tasks.registerExecTasks(
        [name: "cppcheck-gcc-${config.toLowerCase()}", toolchains: ['gcc'], buildConfigs: [config]],
        { task ->
            task.executable = 'cppcheck'
            task.args '--enable=all', '--project', task.compileCommands
        }
    )
  }
}

// Run: ./gradlew cppcheck-gcc-debug
```

### Runtime and Develop Archives

```groovy
cmake {
  // ZIP runtime binaries for distribution
  tasks.registerRuntimeArchiveTasks(
      [toolchains: ['gcc'], buildConfigs: ['Release'], components: ['*shared']],
      { task -> task.destinationDirectory.set(layout.buildDirectory.dir('dist')) }
  )

  // ZIP development SDK (library + headers)
  tasks.registerDevelopArchiveTasks(
      [toolchains: ['gcc'], buildConfigs: ['Release'], components: ['*static']],
      { task -> task.destinationDirectory.set(layout.buildDirectory.dir('sdk')) }
  )
}

// Run: ./gradlew zip-runtime-core-shared-gcc-release
//      ./gradlew zip-develop-core-static-gcc-release
```

## Build Output Structure

```
<project root>/
└── CMakeLists.txt                  # Generated; deleted by clean

build/cmake/
└── config/
    ├── <toolchain>/
    │   └── <buildconfig>/          # CMake build directory
    │       ├── CMakeCache.txt
    │       ├── compile_commands.json
    │       └── <target>/           # Per-component build output
    └── <name>-<variant>-<toolchain>-<buildconfig>-module.cmake
```

## Contributing

Contributions are welcome. Please submit pull requests or open issues for bug reports and feature requests.

## License

This project is licensed under the MIT License. See the LICENSE file for details.
