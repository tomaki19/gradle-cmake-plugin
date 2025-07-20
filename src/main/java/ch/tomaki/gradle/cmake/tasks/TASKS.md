# Assemble Tasks

[`ch.tomaki.gradle.cmake.tasks.CMakeAssemble`](./CMakeAssemble.java)

The `assemble-cmake-lists` task creates the `CMakeLists.txt` file for the project.

The `assemble-<toolchain>-config` task creates `build/cmake/build/<project>-<toolchain>-config.cmake` files containing all the exposed libraries of the project. This cmake config file may be referenced by other local cmake projects.

## Configure Tasks

[`ch.tomaki.gradle.cmake.tasks.CMakeConfigure`](./CMakeConfigure.java)

The `configure-<toolchain>` tasks run the cmake configure step for each defined binary (see [extension](<./../extension/EXTENSION.md>) documentation).

The cmake configuration files for each toolchain are created in a `build/cmake/build/<toolchain>/` directory.

## Build Tasks

[`ch.tomaki.gradle.cmake.tasks.CMakeBuild`](./CMakeBuild.java)

The `build-<binary>-<toolchain>` tasks run the cmake build step for each binary in the context of a toolchain (see [extension](<./../extension/EXTENSION.md>) documentation).

The compiled artifacts for each toolchain are deployed in a `build/cmake/install/<toolchain>/` directory.

The default file names of the artifacts are `<name>-<toolchain>-<link_type>-<build_config>`.

### Build All Tasks

For each toolchain a special `build-all-<toolchain>` task is created that runs all the build tasks for that toolchain.

## Check Tasks

[`ch.tomaki.gradle.cmake.tasks.CMakeCheck`](./CMakeCheck.java)

These tasks run the cmake check step for each defined test (see the [extension](<./../extension/EXTENSION.md>) documentation).

### Check All Tasks

For each toolchain a special `check-all-<toolchain>` task is created that runs all the check tasks for that toolchain.

### Customize Check Tasks

You may want to customize the test run tasks, e.g. by adding additional arguments to the internally used ctest command:

```groovy
tasks.withType(ch.tomaki.gradle.cmake.tasks.CMakeCheck) {
    additionalArguments = [
        '--verbose',
        '--output-on-failure',
        '--output-junit',
        "${project.buildDir}/reports/tests/${checkTarget}-results.xml"
    ]
}
```

# Package Tasks

[`ch.tomaki.gradle.cmake.tasks.CMakePackage`](./CMakePackage.java)

These tasks create zip packages containing the built binaries. The default package name is <buildTarget>-<version>.zip

### Customize Package Tasks

You may want to customize the package tasks, e.g. by adding additional files:

```groovy
tasks.withType(ch.tomaki.gradle.cmake.tasks.CMakePackage) {
    doFirst {
        mkdir('build/tmp')
        file('build/tmp/description.txt').write("""
            name=${archiveBaseName}
            version=${project.version}
            """)
    }
    from("${project.buildDir}/tmp").include('description.txt')
}
```

# Custom Exec Tasks

[`ch.tomaki.gradle.cmake.tasks.CMakeCustomExec`](./CMakeCustomExec.java)

These tasks execute a custom execution task in the context of a toolchain (see [extension](<./../extension/EXTENSION.md>) documentation).

The plugin provides a special register method for these tasks:

```groovy
cmake.register(<name>, [<toolchains>]) {
    baseCommandLine = [...]
    getAdditionalArguments = [...]
}
```

This may be useful for additional cmake features like test coverage.

## Test Coverage

As an example, to enable test coverage for a build you can add a custom exec task that runs ctest -T coverage after the tests are run:

```groovy
cmake.register('coverage', ['gcc-x86-64']) {
    dependsOn(tasks.withType(ch.tomaki.gradle.cmake.tasks.CMakeCheck))
    baseCommandLine = [
        'ctest',
        '-T',
        'Coverage',
        '--test-dir',
        "${project.buildDir}/cmake/build/${toolchainName}/",
    ]
}
```

In addition to the custom task you also need to add special compile flags (`--coverage` or `-fprofile-arcs -ftest-coverage`) and linker flags (`--coverage` or `-fprofile-arcs -lgcov`). For more details see
