/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake;

import java.util.Collection;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.tasks.TaskProvider;

import ch.tomaki.gradle.cmake.extension.CMakeExtension;
import ch.tomaki.gradle.cmake.extension.CMakeValidator;
import ch.tomaki.gradle.cmake.files.CMakeLinkType;
import ch.tomaki.gradle.cmake.model.CMakeResolvedExecutable;
import ch.tomaki.gradle.cmake.model.CMakeResolvedLibrary;
import ch.tomaki.gradle.cmake.model.CMakeResolvedToolchain;
import ch.tomaki.gradle.cmake.model.CMakeResolver;
import ch.tomaki.gradle.cmake.tasks.CMakeAssemble;
import ch.tomaki.gradle.cmake.tasks.CMakeBuildExecutable;
import ch.tomaki.gradle.cmake.tasks.CMakeBuildLibrary;
import ch.tomaki.gradle.cmake.tasks.CMakeCheck;
import ch.tomaki.gradle.cmake.tasks.CMakeConfigure;
import ch.tomaki.gradle.cmake.tasks.CMakeTaskRegistry;

public class CMakePlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(BasePlugin.class);
    project.allprojects(this::allProjects);
    project.afterEvaluate(this::afterEvaluate);
  }

  private void allProjects(final Project project) {
    try {
      project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class, project.getTasks());
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

      final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      final Collection<CMakeResolvedToolchain> toolchains = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      /* Tasks */

      final CMakeTaskRegistry taskRegistry = new CMakeTaskRegistry(project.getTasks());

      final TaskProvider<CMakeAssemble> assembleListsTask = taskRegistry.assembleListsTask(toolchains, project);
      taskRegistry.assembleTask().configure((task) -> task.dependsOn(assembleListsTask));

      for (final CMakeResolvedToolchain toolchain : toolchains) {
        if (toolchain.hasBinaries()) {
          final TaskProvider<CMakeAssemble> assembleConfigTask = taskRegistry.assembleConfigTask(toolchain, project);
          taskRegistry.assembleTask().configure((task) -> task.dependsOn(assembleConfigTask));

          final TaskProvider<CMakeConfigure> configureTask = taskRegistry.configureTask(toolchain);
          configureTask.configure((task) -> {
            CMakeTaskRegistry.configureRemote(task, toolchain, project);
            task.dependsOn(assembleListsTask);
          });

          final TaskProvider<?> buildAllToolchainTask = taskRegistry.buildAllToolchainTask(toolchain);
          buildAllToolchainTask.configure((task) -> {
            task.setGroup(CMakeTaskRegistry.GROUP_BUILD);
            task.setEnabled(!toolchain.getLibraries().isEmpty() || !toolchain.getApplications().isEmpty()
                || !toolchain.getTests().isEmpty());
          });
          taskRegistry.buildTask().configure((task) -> task.dependsOn(buildAllToolchainTask));

          for (final CMakeResolvedLibrary library : toolchain.getLibraries()) {
            for (final String buildConfig : toolchain.getBuildConfigs()) {
              if (!library.getSources().isEmpty()) {
                if (library.isBuildStatic()) {
                  final TaskProvider<CMakeBuildLibrary> buildTask = taskRegistry.buildTask(library, toolchain,
                      CMakeLinkType.STATIC, buildConfig);
                  buildTask.configure((task) -> {
                    CMakeTaskRegistry.configureRemote(task, library, toolchain, buildConfig);
                    task.dependsOn(configureTask);
                  });
                  buildAllToolchainTask.configure((task) -> task.dependsOn(buildTask));

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
                  buildAllToolchainTask.configure((task) -> task.dependsOn(buildTask));

                  if (library.isPackageBuildOutputs()) {
                    taskRegistry.packageTask(library, toolchain, CMakeLinkType.SHARED, buildConfig)
                        .configure((task) -> task.dependsOn(buildTask));
                  }
                }
              }
            }
          }

          for (final CMakeResolvedExecutable application : toolchain.getApplications()) {
            for (final String buildConfig : toolchain.getBuildConfigs()) {
              final TaskProvider<CMakeBuildExecutable> buildTask = taskRegistry.buildTask(application, toolchain,
                  buildConfig);
              buildTask.configure((task) -> {
                CMakeTaskRegistry.configureRemote(task, application, toolchain, buildConfig);
                task.dependsOn(configureTask);
              });
              buildAllToolchainTask.configure((task) -> task.dependsOn(buildTask));

              if (application.isPackageBuildOutputs()) {
                taskRegistry.packageTask(application, toolchain, buildConfig)
                    .configure((task) -> task.dependsOn(buildTask));
              }
            }
          }

          if (toolchain.hasTests()) {

            final TaskProvider<?> checkAllToolchainTask = taskRegistry.checkAllToolchainTask(toolchain);
            checkAllToolchainTask.configure((task) -> {
              task.setGroup(CMakeTaskRegistry.GROUP_CHECK);
              task.setEnabled(!toolchain.getTests().isEmpty());
            });
            taskRegistry.checkTask().configure((task) -> task.dependsOn(checkAllToolchainTask));

            for (final CMakeResolvedExecutable test : toolchain.getTests()) {
              for (final String buildConfig : toolchain.getBuildConfigs()) {
                final TaskProvider<CMakeBuildExecutable> buildTask = taskRegistry.buildTask(test, toolchain,
                    buildConfig);
                buildTask.configure((task) -> {
                  CMakeTaskRegistry.configureRemote(task, test, toolchain, buildConfig);
                  task.dependsOn(configureTask);
                });
                buildAllToolchainTask.configure((task) -> task.dependsOn(buildTask));

                final TaskProvider<CMakeCheck> checkTask = taskRegistry.checkTask(test, toolchain, buildConfig);
                checkTask.configure((task) -> task.dependsOn(buildTask));
                checkAllToolchainTask.configure((task) -> task.dependsOn(checkTask));
              }
            }
          }
        }
      }
    } catch (Exception e) {
      throw new GradleException(e.getMessage(), e.getCause());
    }
  }
}
