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
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.component.SoftwareComponentFactory;
import org.gradle.api.file.Directory;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.tasks.TaskProvider;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomTaskProto;
import io.github.tomaki19.gradle.cmake.files.CMakeFileConventions;
import io.github.tomaki19.gradle.cmake.model.CMakeArtifactAttributes;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedExecutable;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedProjectDependency;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeResolver;
import io.github.tomaki19.gradle.cmake.tasks.CMakeAssemble;
import io.github.tomaki19.gradle.cmake.tasks.CMakeBuildExecutable;
import io.github.tomaki19.gradle.cmake.tasks.CMakeBuildLibrary;
import io.github.tomaki19.gradle.cmake.tasks.CMakeCheck;
import io.github.tomaki19.gradle.cmake.tasks.CMakeClean;
import io.github.tomaki19.gradle.cmake.tasks.CMakeConfigure;
import io.github.tomaki19.gradle.cmake.tasks.CMakeCustomExec;
import io.github.tomaki19.gradle.cmake.tasks.CMakePackageZip;
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

      project.getDependencies().getAttributesSchema().attribute(CMakeArtifactAttributes.LINK_VARIANT_ATTRIBUTE);
      project.getDependencies().getAttributesSchema().attribute(CMakeArtifactAttributes.TOOLCHAIN_ATTRIBUTE);
      project.getDependencies().getAttributesSchema().attribute(CMakeArtifactAttributes.BUILD_CONFIG_ATTRIBUTE);

    } catch (Exception e) {
      throw new GradleException(e.getMessage(), e.getCause());
    }
  }

  private void afterEvaluate(final Project project) {
    try {
      final CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);

      /* ============ Resolve ============ */

      final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      final Collection<CMakeResolvedToolchain> toolchains = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      /* ============ Tasks ============== */

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
            taskProvider.configure((task) -> task.setGroup(CMakeTaskRegistry.GROUP_BUILD));
            taskRegistry.buildTask(project.getTasks()).configure((task) -> task.dependsOn(taskProvider));
          });
        }

        Optional<TaskProvider<?>> checkAllToolchainTask = Optional.empty();
        if (toolchain.hasTests()) {
          checkAllToolchainTask = Optional.of(taskRegistry.checkAllToolchainTask(project.getTasks(), toolchain));
          checkAllToolchainTask.ifPresent((taskProvider) -> {
            taskProvider.configure((task) -> task.setGroup(CMakeTaskRegistry.GROUP_CHECK));
            taskRegistry.checkTask(project.getTasks()).configure((task) -> task.dependsOn(taskProvider));
          });
        }

        for (final String buildConfig : toolchain.getBuildConfigs()) {
          Optional<TaskProvider<?>> buildAllBuildConfigTask = Optional.empty();
          if (toolchain.hasBinaries()) {
            buildAllBuildConfigTask = Optional.of(taskRegistry.buildAllBuildConfigTask(project.getTasks(), toolchain,
                buildConfig));
            buildAllBuildConfigTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.setGroup(CMakeTaskRegistry.GROUP_BUILD));
              taskRegistry.buildTask(project.getTasks()).configure((task) -> task.dependsOn(taskProvider));
            });
          }

          Optional<TaskProvider<?>> checkAllBuildConfigTask = Optional.empty();
          if (toolchain.hasTests()) {
            checkAllBuildConfigTask = Optional.of(taskRegistry.checkAllBuildConfigTask(project.getTasks(), toolchain,
                buildConfig));
            checkAllBuildConfigTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.setGroup(CMakeTaskRegistry.GROUP_CHECK));
              taskRegistry.checkTask(project.getTasks()).configure((task) -> task.dependsOn(taskProvider));
            });
          }

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

            final Configuration outputDirectoryConfiguration = CMakeTaskRegistry
                .createDirectoryConfiguration(project, library, toolchain, buildConfig);
            for (final CMakeResolvedProjectDependency dependency : library.getAllProjectDependencies()) {
              if (dependency.isRemote()) {
                final Configuration directoryDependencyConfiguration = CMakeTaskRegistry
                    .createDependencyConfiguration(project, dependency, toolchain, buildConfig);
                final ProjectDependency projectDependency = project.getDependencyFactory()
                    .create(project.findProject(":%s".formatted(dependency.getProjectName())));
                directoryDependencyConfiguration.getDependencies().add(projectDependency);
                outputDirectoryConfiguration.extendsFrom(directoryDependencyConfiguration);
              }
            }
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
              CMakeTaskRegistry.configureRemote(task, toolchain, buildConfig, library.getAllProjectDependencies());
            });
            buildAllToolchainTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(buildTask));
            });
            buildAllBuildConfigTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(buildTask));
            });

            final TaskProvider<CMakePackageZip> packageTask = taskRegistry.packageTask(project.getTasks(),
                project.getLayout().getBuildDirectory().get(), library, toolchain, buildConfig);
            packageTask.configure((task) -> task.dependsOn(buildTask));
            final Configuration outputDirectoryConfiguration = CMakeTaskRegistry
                .createDirectoryConfiguration(project, library, toolchain, buildConfig);
            final Directory outputDirectory = CMakeFileConventions.targetBinaryDirectory(
                project.getLayout().getBuildDirectory().get(), library, toolchain, buildConfig);
            CMakeTaskRegistry.addOutputDirectoryArtifact(project.getArtifacts(), outputDirectoryConfiguration,
                outputDirectory, buildTask);
            for (final CMakeResolvedProjectDependency dependency : library.getAllProjectDependencies()) {
              if (dependency.isRemote()) {
                final Configuration directoryDependencyConfiguration = CMakeTaskRegistry
                    .createDependencyConfiguration(project, dependency, toolchain, buildConfig);
                final ProjectDependency projectDependency = project.getDependencyFactory()
                    .create(project.findProject(":%s".formatted(dependency.getProjectName())));
                directoryDependencyConfiguration.getDependencies().add(projectDependency);
                packageTask.configure((task) -> {
                  task.from(directoryDependencyConfiguration);
                });
                outputDirectoryConfiguration.extendsFrom(directoryDependencyConfiguration);
              }
            }
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
              CMakeTaskRegistry.configureRemote(task, toolchain, buildConfig, library.getAllProjectDependencies());
            });
            buildAllToolchainTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(buildTask));
            });
            buildAllBuildConfigTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(buildTask));
            });

            final TaskProvider<CMakePackageZip> packageTask = taskRegistry.packageTask(project.getTasks(),
                project.getLayout().getBuildDirectory().get(), library, toolchain, buildConfig);
            packageTask.configure((task) -> task.dependsOn(buildTask));
            final Configuration outputDirectoryConfiguration = CMakeTaskRegistry
                .createDirectoryConfiguration(project, library, toolchain, buildConfig);
            final Directory outputDirectory = CMakeFileConventions.targetBinaryDirectory(
                project.getLayout().getBuildDirectory().get(), library, toolchain, buildConfig);
            CMakeTaskRegistry.addOutputDirectoryArtifact(project.getArtifacts(), outputDirectoryConfiguration,
                outputDirectory, buildTask);
            for (final CMakeResolvedProjectDependency dependency : library.getAllProjectDependencies()) {
              if (dependency.isRemote()) {
                final Configuration directoryDependencyConfiguration = CMakeTaskRegistry
                    .createDependencyConfiguration(project, dependency, toolchain, buildConfig);
                final ProjectDependency projectDependency = project.getDependencyFactory()
                    .create(project.findProject(":%s".formatted(dependency.getProjectName())));
                directoryDependencyConfiguration.getDependencies().add(projectDependency);
                packageTask.configure((task) -> {
                  task.from(directoryDependencyConfiguration);
                });
                outputDirectoryConfiguration.extendsFrom(directoryDependencyConfiguration);
              }
            }
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
              CMakeTaskRegistry.configureRemote(task, toolchain, buildConfig, application.getAllProjectDependencies());
            });
            buildAllToolchainTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(buildTask));
            });
            buildAllBuildConfigTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(buildTask));
            });

            final TaskProvider<CMakePackageZip> packageTask = taskRegistry.packageTask(project.getTasks(),
                project.getLayout().getBuildDirectory().get(), application, toolchain, buildConfig);
            packageTask.configure((task) -> task.dependsOn(buildTask));
            for (final CMakeResolvedProjectDependency dependency : application.getAllProjectDependencies()) {
              if (dependency.isRemote()) {
                final Configuration directoryDependencyConfiguration = CMakeTaskRegistry
                    .createDependencyConfiguration(project, dependency, toolchain, buildConfig);
                final ProjectDependency projectDependency = project.getDependencyFactory()
                    .create(project.findProject(":%s".formatted(dependency.getProjectName())));
                directoryDependencyConfiguration.getDependencies().add(projectDependency);
                packageTask.configure((task) -> {
                  task.from(directoryDependencyConfiguration);
                });
              }
            }

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
              CMakeTaskRegistry.configureRemote(task, toolchain, buildConfig, test.getAllProjectDependencies());
              task.dependsOn(configureTask);
            });
            buildAllToolchainTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(buildTask));
            });
            buildAllBuildConfigTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(buildTask));
            });

            final TaskProvider<CMakeCheck> checkTask = taskRegistry.checkTask(project.getTasks(), test, toolchain,
                buildConfig);
            checkTask.configure((task) -> task.dependsOn(buildTask));
            checkAllToolchainTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(checkTask));
            });
            checkAllBuildConfigTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(checkTask));
            });

            final TaskProvider<CMakePackageZip> packageTask = taskRegistry.packageTask(project.getTasks(),
                project.getLayout().getBuildDirectory().get(), test, toolchain, buildConfig);
            packageTask.configure((task) -> task.dependsOn(buildTask));
            for (final CMakeResolvedProjectDependency dependency : test.getAllProjectDependencies()) {
              if (dependency.isRemote()) {
                final Configuration directoryDependencyConfiguration = CMakeTaskRegistry
                    .createDependencyConfiguration(project, dependency, toolchain, buildConfig);
                final ProjectDependency projectDependency = project.getDependencyFactory()
                    .create(project.findProject(":%s".formatted(dependency.getProjectName())));
                directoryDependencyConfiguration.getDependencies().add(projectDependency);
                packageTask.configure((task) -> {
                  task.from(directoryDependencyConfiguration);
                });

              }
            }
          }
        }
      }

      System.out.println("==== Incoming beforeResolve ====");
      project.getConfigurations().forEach((configuration) -> {
        if (configuration.isCanBeResolved()) {
          System.out.println(configuration.getName());
          configuration.getResolvedConfiguration().getResolvedArtifacts().forEach((artifact) -> {
            System.out.println(" -> " + artifact.getName() + " @ " + artifact.getFile());
          });
        }
      });
      System.out.println("================================");

    } catch (Exception e) {
      throw new GradleException(e.getMessage(), e.getCause());
    }
  }
}
