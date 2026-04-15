# gradle-cmake-plugin

A Gradle plugin that seamlessly integrates CMake into your Gradle build workflow for C/C++ projects. This plugin bridges Gradle and CMake, enabling you to leverage Gradle's dependency management, conventions, and task system while maintaining full control over native compilation with CMake.

## Features

- **Multi-Toolchain Support** - Configure and build against multiple compilers and CMake generators
- **CMake Integration** - Automatic CMake configuration, compilation, and testing through Gradle tasks
- **Library & Application Management** - Define shared/static libraries and executables with fine-grained compilation and linking control
- **Test Automation** - Built-in test execution with JUnit XML output for CI/CD integration
- **Dependency Management** - Link against system packages (via CMake's `find_package`) and local project libraries
- **Multi-Configuration Builds** - Debug, Release, and custom build configurations per toolchain
- **Custom Tasks** - Register custom CMake-related commands (code coverage, linting, analysis)
- **Artifact Organization** - Automatic artifact management and installation to structured directories

## Installation

### Using the Plugin Portal (Recommended)

Add the plugin to your `build.gradle`:

```groovy
plugins {
  id('io.github.tomaki19.gradle-cmake-plugin') version '<version>'
}
```

### Legacy Build Script

```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'io.github.tomaki19:gradle-cmake-plugin:<version>'
    }
}

apply plugin: 'io.github.tomaki19.gradle-cmake-plugin'
```

## Requirements

The plugin requires the following versions:

- **Gradle** >= 8.0
- **Java** >= 17 (JVM language, not C/C++ compilation)
- **CMake** >= 3.21
- **C/C++ Compiler** (gcc, clang, MSVC, etc.) - Your platform's default or a configured toolchain

## Quick Start

### Basic Configuration

Create a simple C++ library and application (see the [Extension Configuration Guide](src/main/java/io/github/tomaki19/gradle/cmake/extension/EXTENSION.md) for all available options):

```groovy
plugins {
  id('io.github.tomaki19.gradle-cmake-plugin') version '<version>'
}

cmake {
  // Define the toolchain to use
  toolchains {
    'gcc' {
      // Uses native gcc and Unix Makefiles by default
      buildConfigs = ['debug', 'release']
    }
  }

  // Define a library
  libraries {
    mylib {
      toolchains = ['gcc']
      sources {
        srcDirs = ['src/lib']
      }
      headers {
        srcDirs = ['include']
      }
    }
  }

  // Define an executable
  applications {
    myapp {
      toolchains = ['gcc']
      sources {
        srcDirs = ['src/app']
      }
      linking {
        link(['mylib'])
      }
    }
  }

  // Define tests
  tests {
    mytest {
      toolchains = ['gcc']
      sources {
        srcDirs = ['src/test']
      }
      linking {
        link(['mylib'])
      }
    }
  }
}
```

### Common Build Commands

See the [Tasks Reference](src/main/java/io/github/tomaki19/gradle/cmake/tasks/TASKS.md) for the full list of tasks and customization options.

```bash
# Build all components for all toolchains
./gradlew build

# Build all components for a specific toolchain
./gradlew build-all-gcc

# Build a specific component
./gradlew build-myapp-gcc

# Run tests
./gradlew check

# Run tests for a specific toolchain
./gradlew check-all-gcc
```

## Documentation

- **[Extension Configuration Guide](src/main/java/io/github/tomaki19/gradle/cmake/extension/EXTENSION.md)** - Detailed documentation of all configuration options for toolchains, libraries, applications, and tests
- **[Tasks Reference](src/main/java/io/github/tomaki19/gradle/cmake/tasks/TASKS.md)** - Complete reference of all available Gradle tasks and how to customize them

## Examples

### Multi-Toolchain Build

See [Toolchains](src/main/java/io/github/tomaki19/gradle/cmake/extension/EXTENSION.md#toolchains) in the Extension Configuration Guide for all toolchain options.

```groovy
cmake {
  toolchains {
    gcc {
      generator = 'Unix Makefiles'
      buildConfigs = ['debug', 'release']
    }
    clang {
      generator = 'Unix Makefiles'
      operatingSystem = 'Linux'
    }
  }

  libraries {
    core {
      toolchains = ['gcc', 'clang']
      buildVariants = ['shared', 'static']
      compiling {
        defines = ['VERSION_1_0', 'ENABLE_LOGGING']
        options = ['-Wall', '-Wextra']
      }
    }
  }
}
```

### Linking Against System Libraries

See [Packages](src/main/java/io/github/tomaki19/gradle/cmake/extension/EXTENSION.md#packages) in the Extension Configuration Guide for all package options.

```groovy
cmake {
  packages {
    opengl {
      moduleMode = true
    }
    boost {
      components = ['system', 'filesystem']
    }
  }

  applications {
    renderer {
      toolchains = ['gcc']
      linking {
        dependencies(['opengl', 'boost'])
      }
    }
  }
}
```

### Custom Execution Tasks

See [Custom Exec Tasks](src/main/java/io/github/tomaki19/gradle/cmake/tasks/TASKS.md#custom-exec-tasks) in the Tasks Reference for registration options and more examples.

```groovy
cmake {
  // Register a coverage analysis task
  register('coverage', ['gcc']) {
    baseCommand = 'ctest'
    baseArguments = [
      '-T', 'Coverage',
      '--test-dir', "${project.buildDir}/cmake/build/gcc/"
    ]
  }
}

// Task runs after tests: ./gradlew coverage-gcc
```

## Build Output Structure

```
build/cmake/
├── CMakeLists.txt              # Generated main build file
├── build/
│   └── <toolchain>/            # CMake build directories
│       ├── CMakeCache.txt
│       └── compile_commands.json
├── install/
│   └── <toolchain>/            # Compiled artifacts
│       ├── lib/                # Libraries
│       └── bin/                # Executables
└── config/
    └── <name>-<toolchain>-config.cmake  # CMake module files
```

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bug reports and feature requests.

## License

This project is licensed under the MIT License. See the LICENSE file for details.
