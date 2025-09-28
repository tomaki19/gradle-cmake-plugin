/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake;

import java.util.Collection;
import java.util.Optional;

import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.component.SoftwareComponentFactory;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.attributes.Bundling;
import org.gradle.api.attributes.Category;
import org.gradle.api.attributes.LibraryElements;
import org.gradle.api.file.RegularFile;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.tasks.TaskProvider;

import io.github.tomaki19.gradle.cmake.extension.CMakeConfigurations;
import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.CMakeValidator;
import io.github.tomaki19.gradle.cmake.files.CMakeLinkType;
import io.github.tomaki19.gradle.cmake.files.CMakeListsFile;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedExecutable;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeResolver;
import io.github.tomaki19.gradle.cmake.tasks.CMakeAssemble;
import io.github.tomaki19.gradle.cmake.tasks.CMakeBuildExecutable;
import io.github.tomaki19.gradle.cmake.tasks.CMakeBuildLibrary;
import io.github.tomaki19.gradle.cmake.tasks.CMakeCheck;
import io.github.tomaki19.gradle.cmake.tasks.CMakeConfigure;
import io.github.tomaki19.gradle.cmake.tasks.CMakeTaskRegistry;

public class CMakePlugin implements Plugin<Project> {

  private final SoftwareComponentFactory softwareComponentFactory;

  @javax.inject.Inject
  CMakePlugin(SoftwareComponentFactory softwareComponentFactory) {
    this.softwareComponentFactory = softwareComponentFactory;
  }

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(BasePlugin.class);
    project.allprojects(this::allProjects);
    project.afterEvaluate(this::afterEvaluate);
  }

  private void allProjects(final Project project) {
    try {
      project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class, project.getTasks());

      final AdhocComponentWithVariants adhocComponent = softwareComponentFactory.adhoc("cmake");
      project.getComponents().add(adhocComponent);

      final Configuration cmakeCompile = project.getConfigurations()
          .create(CMakeConfigurations.CMAKE_COMPILE.toString(),
              CMakeConfigurations.CMAKE_COMPILE.configure());

      final Configuration cmakeCompileClasspath = project.getConfigurations().create(
          CMakeConfigurations.CMAKE_COMPILE_CLASSPATH.toString(),
          CMakeConfigurations.CMAKE_COMPILE_CLASSPATH.configure());
      cmakeCompileClasspath.extendsFrom(cmakeCompile);
      cmakeCompileClasspath.getAttributes().attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
          project.getObjects().named(LibraryElements.class, "cmake-compile"));

      final Configuration cmakeCompileElements = project.getConfigurations()
          .create(CMakeConfigurations.CMAKE_COMPILE_ELEMENTS.toString(),
              CMakeConfigurations.CMAKE_COMPILE_ELEMENTS.configure());
      cmakeCompileElements.extendsFrom(cmakeCompile);
      cmakeCompileElements.getAttributes().attribute(Category.CATEGORY_ATTRIBUTE,
          project.getObjects().named(Category.class, Category.LIBRARY));
      cmakeCompileElements.getAttributes().attribute(Bundling.BUNDLING_ATTRIBUTE,
          project.getObjects().named(Bundling.class, Bundling.EXTERNAL));
      cmakeCompileElements.getAttributes().attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
          project.getObjects().named(LibraryElements.class, "cmake-compile"));

      adhocComponent.addVariantsFromConfiguration(cmakeCompileElements, (variant) -> {
        variant.mapToMavenScope("compile");
        variant.mapToOptional();
      });
    } catch (Exception e) {
      throw new GradleException(e.getMessage(), e.getCause());
    }
  }

  private void afterEvaluate(final Project project) {
    try {
      final CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);

      /* Validation */

      CMakeValidator.validateToolchains(extension.getToolchains());
      CMakeValidator.validateLibraries(extension.getLibraries());
      CMakeValidator.validateApplications(extension.getApplications());
      CMakeValidator.validateTests(extension.getTests());

      /* Resolve */

      final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(),
          extension.getToolchains());
      final Collection<CMakeResolvedToolchain> toolchains = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      /* Tasks */

      final CMakeTaskRegistry taskRegistry = new CMakeTaskRegistry(project.getTasks());

      final TaskProvider<CMakeAssemble> assembleListsTask = taskRegistry.assembleListsTask(
          resolver.getAvailableSystemPackages(), toolchains, project);
      taskRegistry.assembleTask().configure((task) -> task.dependsOn(assembleListsTask));

      final TaskProvider<Task> cleanTask = taskRegistry.cleanTask();
      final RegularFile cmakeListsFile = project.getLayout().getProjectDirectory().file(CMakeListsFile.NAME);
      cleanTask.configure((task) -> task.doLast((action) -> {
        cmakeListsFile.getAsFile().delete();
      }));

      for (final CMakeResolvedToolchain toolchain : toolchains) {
        final TaskProvider<CMakeAssemble> assembleConfigTask = taskRegistry.assembleConfigTask(toolchain, project);
        taskRegistry.assembleTask().configure((task) -> task.dependsOn(assembleConfigTask));

        Optional<TaskProvider<?>> buildAllToolchainTask = Optional.empty();
        if (toolchain.hasBinaries()) {
          buildAllToolchainTask = Optional.of(taskRegistry.buildAllToolchainTask(toolchain));
          buildAllToolchainTask.ifPresent((taskProvider) -> {
            taskProvider.configure((task) -> {
              task.setGroup(CMakeTaskRegistry.GROUP_BUILD);
            });
            taskRegistry.buildTask().configure((task) -> task.dependsOn(taskProvider));
          });
        }

        Optional<TaskProvider<?>> checkAllToolchainTask = Optional.empty();
        if (toolchain.hasBinaries()) {
          checkAllToolchainTask = Optional.of(taskRegistry.checkAllToolchainTask(toolchain));
          checkAllToolchainTask.ifPresent((taskProvider) -> {
            taskProvider.configure((task) -> {
              task.setGroup(CMakeTaskRegistry.GROUP_CHECK);
            });
            taskRegistry.checkTask().configure((task) -> task.dependsOn(taskProvider));
          });
        }

        for (final String buildConfig : toolchain.getBuildConfigs()) {
          final TaskProvider<CMakeConfigure> configureTask = taskRegistry.configureTask(toolchain, buildConfig);
          configureTask.configure((task) -> {
            CMakeTaskRegistry.configureRemote(task, toolchain, buildConfig, project);
            task.dependsOn(assembleListsTask);
          });

          for (final CMakeResolvedLibrary library : toolchain.getLibraries()) {
            if (!library.getSources().isEmpty()) {
              if (library.isBuildStatic()) {
                final TaskProvider<CMakeBuildLibrary> buildTask = taskRegistry.buildTask(library, toolchain,
                    CMakeLinkType.STATIC, buildConfig);
                buildTask.configure((task) -> {
                  CMakeTaskRegistry.configureRemote(task, library, toolchain, buildConfig);
                  task.dependsOn(configureTask);
                });
                buildAllToolchainTask.ifPresent((taskProvider) -> {
                  taskProvider.configure((task) -> task.dependsOn(buildTask));
                });

                if (library.isPackageBuildOutputs()) {
                  taskRegistry.packageTask(library, toolchain, CMakeLinkType.STATIC, buildConfig)
                      .configure((task) -> task.dependsOn(buildTask));
                }
              }
              if (library.isBuildShared()) {
                final TaskProvider<CMakeBuildLibrary> buildTask = taskRegistry.buildTask(library, toolchain,
                    CMakeLinkType.SHARED, buildConfig);
                buildTask.configure((task) -> {
                  CMakeTaskRegistry.configureRemote(task, library, toolchain, buildConfig);
                  task.dependsOn(configureTask);
                });
                buildAllToolchainTask.ifPresent((taskProvider) -> {
                  taskProvider.configure((task) -> task.dependsOn(buildTask));
                });

                if (library.isPackageBuildOutputs()) {
                  taskRegistry.packageTask(library, toolchain, CMakeLinkType.SHARED, buildConfig)
                      .configure((task) -> task.dependsOn(buildTask));
                }
              }

            }
          }

          for (final CMakeResolvedExecutable application : toolchain.getApplications()) {
            final TaskProvider<CMakeBuildExecutable> buildTask = taskRegistry.buildTask(application, toolchain,
                buildConfig);
            buildTask.configure((task) -> {
              CMakeTaskRegistry.configureRemote(task, application, toolchain, buildConfig);
              task.dependsOn(configureTask);
            });
            buildAllToolchainTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(buildTask));
            });

            if (application.isPackageBuildOutputs()) {
              taskRegistry.packageTask(application, toolchain, buildConfig)
                  .configure((task) -> task.dependsOn(buildTask));
            }
          }

          for (final CMakeResolvedExecutable test : toolchain.getTests()) {
            final TaskProvider<CMakeBuildExecutable> buildTask = taskRegistry.buildTask(test, toolchain,
                buildConfig);
            buildTask.configure((task) -> {
              CMakeTaskRegistry.configureRemote(task, test, toolchain, buildConfig);
              task.dependsOn(configureTask);
            });
            buildAllToolchainTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(buildTask));
            });

            final TaskProvider<CMakeCheck> checkTask = taskRegistry.checkTask(test, toolchain, buildConfig);
            checkTask.configure((task) -> task.dependsOn(buildTask));
            checkAllToolchainTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(checkTask));
            });
          }
        }
      }
    } catch (Exception e) {
      throw new GradleException(e.getMessage(), e.getCause());
    }
  }
}
