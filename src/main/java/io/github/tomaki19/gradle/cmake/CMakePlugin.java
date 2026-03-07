/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.component.SoftwareComponentFactory;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.tasks.TaskProvider;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomTaskProto;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedExecutable;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeResolver;
import io.github.tomaki19.gradle.cmake.tasks.CMakeAssemble;
import io.github.tomaki19.gradle.cmake.tasks.CMakeBuildExecutable;
import io.github.tomaki19.gradle.cmake.tasks.CMakeBuildLibrary;
import io.github.tomaki19.gradle.cmake.tasks.CMakeCheck;
import io.github.tomaki19.gradle.cmake.tasks.CMakeClean;
import io.github.tomaki19.gradle.cmake.tasks.CMakeConfigure;
import io.github.tomaki19.gradle.cmake.tasks.CMakeCustomExec;
import io.github.tomaki19.gradle.cmake.tasks.CMakeTaskRegistry;

public class CMakePlugin implements Plugin<Project> {

  private final SoftwareComponentFactory softwareComponentFactory;
  private final Map<String, Map<CMakeCustomTaskProto, Action<CMakeCustomExec>>> customTaskProtos = new HashMap<>();

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
      project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class, customTaskProtos);

      final AdhocComponentWithVariants adhocComponent = softwareComponentFactory.adhoc("cmake");
      project.getComponents().add(adhocComponent);

      // final Configuration cmakeCompile = project.getConfigurations()
      // .create(CMakeConfigurations.CMAKE_COMPILE.toString(),
      // CMakeConfigurations.CMAKE_COMPILE.configure());

      // final Configuration cmakeCompileClasspath =
      // project.getConfigurations().create(
      // CMakeConfigurations.CMAKE_COMPILE_CLASSPATH.toString(),
      // CMakeConfigurations.CMAKE_COMPILE_CLASSPATH.configure());
      // cmakeCompileClasspath.extendsFrom(cmakeCompile);
      // cmakeCompileClasspath.getAttributes().attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
      // project.getObjects().named(LibraryElements.class, "cmake-compile"));

      // final Configuration cmakeCompileElements = project.getConfigurations()
      // .create(CMakeConfigurations.CMAKE_COMPILE_ELEMENTS.toString(),
      // CMakeConfigurations.CMAKE_COMPILE_ELEMENTS.configure());
      // cmakeCompileElements.extendsFrom(cmakeCompile);
      // cmakeCompileElements.getAttributes().attribute(Category.CATEGORY_ATTRIBUTE,
      // project.getObjects().named(Category.class, Category.LIBRARY));
      // cmakeCompileElements.getAttributes().attribute(Bundling.BUNDLING_ATTRIBUTE,
      // project.getObjects().named(Bundling.class, Bundling.EXTERNAL));
      // cmakeCompileElements.getAttributes().attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
      // project.getObjects().named(LibraryElements.class, "cmake-compile"));

      // adhocComponent.addVariantsFromConfiguration(cmakeCompileElements, (variant)
      // -> {
      // variant.mapToMavenScope("compile");
      // variant.mapToOptional();
      // });
    } catch (Exception e) {
      throw new GradleException(e.getMessage(), e.getCause());
    }
  }

  private void afterEvaluate(final Project project) {
    try {
      final CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);

      /* Resolve */

      final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      final Collection<CMakeResolvedToolchain> toolchains = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      /* Tasks */

      final CMakeTaskRegistry taskRegistry = new CMakeTaskRegistry();

      final TaskProvider<CMakeClean> cleanListsTask = taskRegistry.cleanListsTask(project.getTasks());
      taskRegistry.cleanTask(project.getTasks()).configure((task) -> task.dependsOn(cleanListsTask));

      final TaskProvider<CMakeAssemble> assembleListsTask = taskRegistry.assembleListsTask(project.getTasks(),
          toolchains, project);
      taskRegistry.assembleTask(project.getTasks()).configure((task) -> task.dependsOn(assembleListsTask));

      for (final CMakeResolvedToolchain toolchain : toolchains) {

        Optional<TaskProvider<?>> buildAllToolchainTask = Optional.empty();
        if (toolchain.hasBinaries()) {
          buildAllToolchainTask = Optional.of(taskRegistry.buildAllToolchainTask(project.getTasks(), toolchain));
          buildAllToolchainTask.ifPresent((taskProvider) -> {
            taskProvider.configure((task) -> {
              task.setGroup(CMakeTaskRegistry.GROUP_BUILD);
            });
            taskRegistry.buildTask(project.getTasks()).configure((task) -> task.dependsOn(taskProvider));
          });
        }

        Optional<TaskProvider<?>> checkAllToolchainTask = Optional.empty();
        if (toolchain.hasBinaries()) {
          checkAllToolchainTask = Optional.of(taskRegistry.checkAllToolchainTask(project.getTasks(), toolchain));
          checkAllToolchainTask.ifPresent((taskProvider) -> {
            taskProvider.configure((task) -> {
              task.setGroup(CMakeTaskRegistry.GROUP_CHECK);
            });
            taskRegistry.checkTask(project.getTasks()).configure((task) -> task.dependsOn(taskProvider));
          });
        }

        for (final String buildConfig : toolchain.getBuildConfigs()) {
          final TaskProvider<CMakeConfigure> configureTask = taskRegistry.configureTask(project.getTasks(), toolchain,
              buildConfig);
          configureTask.configure((task) -> {
            task.dependsOn(assembleListsTask);
          });

          if (customTaskProtos.containsKey(toolchain.getName())) {
            customTaskProtos.get(toolchain.getName()).forEach((taskProto, taskAction) -> {
              if (Objects.equals(buildConfig, taskProto.getBuildConfig())) {
                taskRegistry.customExecTask(project.getTasks(), taskProto).configure(taskAction);
              }
            });
          }

          for (final CMakeResolvedLibrary library : toolchain.getInterfaceLibraries()) {
            final TaskProvider<CMakeAssemble> assembleModulesTask = taskRegistry.assembleModuleTask(project.getTasks(),
                library, toolchain, buildConfig, project);
            taskRegistry.assembleTask(project.getTasks()).configure((task) -> task.dependsOn(assembleModulesTask));
          }
          for (final CMakeResolvedLibrary library : toolchain.getStaticLibraries()) {
            final TaskProvider<CMakeAssemble> assembleModulesTask = taskRegistry.assembleModuleTask(project.getTasks(),
                library, toolchain, buildConfig, project);
            taskRegistry.assembleTask(project.getTasks()).configure((task) -> task.dependsOn(assembleModulesTask));

            assembleListsTask.configure((task) -> {
              CMakeTaskRegistry.configureRemote(task, project, library, toolchain, buildConfig);
            });

            configureTask.configure((task) -> {
              CMakeTaskRegistry.configureRemote(task, project, library, toolchain, buildConfig);
            });

            final TaskProvider<CMakeBuildLibrary> buildTask = taskRegistry.buildTask(project.getTasks(), library,
                toolchain, buildConfig);
            buildTask.configure((task) -> {
              task.dependsOn(configureTask);
              CMakeTaskRegistry.configureRemote(task, library, toolchain, buildConfig);
            });
            buildAllToolchainTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(buildTask));
            });
          }
          for (final CMakeResolvedLibrary library : toolchain.getSharedLibraries()) {
            final TaskProvider<CMakeAssemble> assembleModulesTask = taskRegistry.assembleModuleTask(project.getTasks(),
                library, toolchain, buildConfig, project);
            taskRegistry.assembleTask(project.getTasks()).configure((task) -> task.dependsOn(assembleModulesTask));

            assembleListsTask.configure((task) -> {
              CMakeTaskRegistry.configureRemote(task, project, library, toolchain, buildConfig);
            });

            configureTask.configure((task) -> {
              CMakeTaskRegistry.configureRemote(task, project, library, toolchain, buildConfig);
            });

            final TaskProvider<CMakeBuildLibrary> buildTask = taskRegistry.buildTask(project.getTasks(), library,
                toolchain, buildConfig);
            buildTask.configure((task) -> {
              task.dependsOn(configureTask);
              CMakeTaskRegistry.configureRemote(task, library, toolchain, buildConfig);
            });
            buildAllToolchainTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(buildTask));
            });
            taskRegistry.installTask(project.getTasks(), library, toolchain, buildConfig)
                .configure((task) -> task.dependsOn(buildTask));
          }

          for (final CMakeResolvedExecutable application : toolchain.getApplications()) {
            assembleListsTask.configure((task) -> {
              CMakeTaskRegistry.configureRemote(task, project, application, toolchain, buildConfig);
            });

            configureTask.configure((task) -> {
              CMakeTaskRegistry.configureRemote(task, project, application, toolchain, buildConfig);
            });

            final TaskProvider<CMakeBuildExecutable> buildTask = taskRegistry.buildTask(project.getTasks(), application,
                toolchain, buildConfig);
            buildTask.configure((task) -> {
              task.dependsOn(configureTask);
              CMakeTaskRegistry.configureRemote(task, application, toolchain, buildConfig);
            });
            buildAllToolchainTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(buildTask));
            });
            taskRegistry.installTask(project.getTasks(), application, toolchain, buildConfig)
                .configure((task) -> task.dependsOn(buildTask));
          }

          for (final CMakeResolvedExecutable test : toolchain.getTests()) {
            assembleListsTask.configure((task) -> {
              CMakeTaskRegistry.configureRemote(task, project, test, toolchain, buildConfig);
            });

            configureTask.configure((task) -> {
              CMakeTaskRegistry.configureRemote(task, project, test, toolchain, buildConfig);
            });

            final TaskProvider<CMakeBuildExecutable> buildTask = taskRegistry.buildTask(project.getTasks(), test,
                toolchain, buildConfig);
            buildTask.configure((task) -> {
              CMakeTaskRegistry.configureRemote(task, test, toolchain, buildConfig);
              task.dependsOn(configureTask);
            });
            buildAllToolchainTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(buildTask));
            });

            final TaskProvider<CMakeCheck> checkTask = taskRegistry.checkTask(project.getTasks(), test, toolchain,
                buildConfig);
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
