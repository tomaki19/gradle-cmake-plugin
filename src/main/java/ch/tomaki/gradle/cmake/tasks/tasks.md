# CMakeAssemble Tasks

The `assemble-cmake-config` task creates the <project>-config.cmake file containing all the defined libraries of the project. This cmake file is used by other local projects.

The `assemble-cmake-lists` task creates the CMakeLists.txt file of the project.

# CMakeExec Tasks

These tasks execute a cmake task in the context of a defined toolchain (see [extension](<./../extension/extension.md>) documentation).

## CMakeConfigureExec Tasks

These tasks run the cmake configure step for each defined binary (see [extension](<./../extension/extension.md>) documentation).

## CMakeBuildExec Tasks

These tasks run the cmake build step for each defined binary (see [extension documenation](<./../extension/extension.md>) documentation).

## CMakeTestExec Tasks

These tasks run the ctest step for each defined test (see the [extension documenation](<./../extension/extension.md>) documentation).

### Customize the CMakeTestExec Tasks

The test exec tasks can be customized. For detailed information refer to the source code for ch.tomaki.gradle.cmake.tasks.CMakeTestExec.

You may want to customize the test run tasks, e.g. by adding additional arguments:

```groovy
tasks.withType(ch.tomaki.gradle.cmake.tasks.CMakeTestExec).configureEach {
    additionalArguments = [
        '--verbose',
        '--output-on-failure',
        '--output-junit',
        "${project.buildDir}/reports/tests/${buildTarget}-results.xml"
    ]
}
```

# CMakePackage Tasks

These tasks create zip packages containing the built binaries. The default final name is <buildTarget>-<version>.zip

### Customize the CMakePackage Tasks

You may want to customize the package tasks, e.g. by adding additional files:

```groovy
tasks.withType(ch.tomaki.gradle.cmake.tasks.CMakePackage).configureEach {
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
