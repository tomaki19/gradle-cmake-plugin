/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.dsl.ArtifactHandler;
import org.gradle.api.artifacts.type.ArtifactTypeDefinition;
import org.gradle.api.attributes.Category;
import org.gradle.api.file.Directory;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;

import io.github.tomaki19.gradle.cmake.model.CMakeResolvedExecutable;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomTaskProto;
import io.github.tomaki19.gradle.cmake.files.CMakeModuleFile;
import io.github.tomaki19.gradle.cmake.files.CMakeFileConventions;
import io.github.tomaki19.gradle.cmake.files.CMakeListsFile;
import io.github.tomaki19.gradle.cmake.model.CMakeArtifactAttributes;
import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedBinary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedProjectDependency;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public final class CMakeTaskRegistry {

  public static final String GROUP_BUILD = "cmake build";
  public static final String GROUP_CHECK = "cmake test";
  public static final String GROUP_INSTALL = "cmake install";
  public static final String GROUP_PACKAGE = "cmake package";

  // public static final String CONFIGURATION_OUTPUT_DIRECTORIES =
  // "outputDirectories";
  // public static final String CONFIGURATION_ARTIFACT_DIRECTORIES =
  // "artifactDirectories";

  public TaskProvider<Task> assembleTask(final TaskContainer tasks) {
    return tasks.named("assemble");
  }

  public TaskProvider<Task> buildTask(final TaskContainer tasks) {
    return tasks.named("build");
  }

  public TaskProvider<Task> checkTask(final TaskContainer tasks) {
    return tasks.named("check");
  }

  public TaskProvider<Task> cleanTask(final TaskContainer tasks) {
    return tasks.named("clean");
  }

  public TaskProvider<CMakeClean> cleanListsTask(final TaskContainer tasks) {
    final String taskName = CMakeTasksConventions.cleanListsTaskName();
    return tasks.register(taskName, CMakeClean.class);
  }

  public TaskProvider<CMakeAssemble> assembleListsTask(final TaskContainer tasks,
      final Collection<CMakeResolvedToolchain> toolchains, final Project project) throws FileNotFoundException {
    final String taskName = CMakeTasksConventions.assembleListsTaskName();
    return tasks.register(taskName, CMakeAssemble.class, new CMakeListsFile(toolchains, project),
        project.getLayout().getProjectDirectory());
  }

  public TaskProvider<CMakeAssemble> assembleModuleTask(final TaskContainer tasks, final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String buildConfig, final Project project)
      throws FileNotFoundException {
    final String taskName = CMakeTasksConventions.assembleModuleTaskName(library.getName(), library.getLinkType(),
        toolchain.getName(), buildConfig);
    final CMakeModuleFile moduleFile = new CMakeModuleFile(library, toolchain, buildConfig, project);
    return tasks.register(taskName, CMakeAssemble.class, moduleFile,
        project.getLayout().getBuildDirectory().get().dir(CMakeFileConventions.CMAKE_CONFIG_PATH));
  }

  public TaskProvider<CMakeCustomExec> customExecTask(final TaskContainer tasks, final CMakeCustomTaskProto taskProto) {
    final String taskName = CMakeTasksConventions.customExecTaskName(taskProto.getName(), taskProto.getToolchain(),
        taskProto.getBuildConfig());
    return tasks.register(taskName, CMakeCustomExec.class, taskProto.getToolchain(),
        taskProto.getBuildConfig(), taskProto.getEnvironmentFile());
  }

  public TaskProvider<CMakeConfigure> configureTask(final TaskContainer tasks, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    final String taskName = CMakeTasksConventions.configureTaskName(toolchain.getName(), buildConfig);
    return tasks.register(taskName, CMakeConfigure.class, toolchain, buildConfig);
  }

  public TaskProvider<Task> buildAllToolchainTask(final TaskContainer tasks, final CMakeResolvedToolchain toolchain) {
    final String taskName = CMakeTasksConventions.buildAllToolchainTaskName(toolchain.getName());
    return tasks.register(taskName);
  }

  public TaskProvider<Task> buildAllBuildConfigTask(final TaskContainer tasks, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    final String taskName = CMakeTasksConventions.buildAllBuildConfigTaskName(toolchain.getName(), buildConfig);
    return tasks.register(taskName);
  }

  public TaskProvider<CMakeBuildLibrary> buildTask(final TaskContainer tasks, final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String taskName = CMakeTasksConventions.buildTaskName(library.getName(), library.getLinkType(),
        toolchain.getName(), buildConfig);
    return tasks.register(taskName, CMakeBuildLibrary.class, library, toolchain, buildConfig);
  }

  public TaskProvider<CMakeBuildExecutable> buildTask(final TaskContainer tasks, final CMakeResolvedBinary<?> binary,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String taskName = CMakeTasksConventions.buildTaskName(binary.getName(), toolchain.getName(),
        buildConfig);
    return tasks.register(taskName, CMakeBuildExecutable.class, binary, toolchain, buildConfig);
  }

  public TaskProvider<Task> checkAllToolchainTask(final TaskContainer tasks, final CMakeResolvedToolchain toolchain) {
    final String taskName = CMakeTasksConventions.checkAllToolchainTaskName(toolchain.getName());
    return tasks.register(taskName);
  }

  public TaskProvider<Task> checkAllBuildConfigTask(final TaskContainer tasks, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    final String taskName = CMakeTasksConventions.checkAllBuildConfigTaskName(toolchain.getName(), buildConfig);
    return tasks.register(taskName);
  }

  public TaskProvider<CMakeCheck> checkTask(final TaskContainer tasks, final CMakeResolvedExecutable test,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String taskName = CMakeTasksConventions.checkTaskName(test.getName(), toolchain.getName(),
        buildConfig);
    return tasks.register(taskName, CMakeCheck.class, test, toolchain, buildConfig);
  }

  public TaskProvider<Task> installAllToolchainTask(final TaskContainer tasks, final CMakeResolvedToolchain toolchain) {
    final String taskName = CMakeTasksConventions.installAllToolchainTaskName(toolchain.getName());
    return tasks.register(taskName);
  }

  public TaskProvider<Task> installAllBuildConfigTask(final TaskContainer tasks, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    final String taskName = CMakeTasksConventions.installAllBuildConfigTaskName(toolchain.getName(), buildConfig);
    return tasks.register(taskName);
  }

  public TaskProvider<CMakeInstall> installTask(final TaskContainer tasks, final CMakeResolvedExecutable executable,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String taskName = CMakeTasksConventions.installTaskName(executable.getName(), toolchain.getName(),
        buildConfig);
    return tasks.register(taskName, CMakeInstall.class, CMakeFileConventions.buildTarget(executable.getName(),
        toolchain.getName(), buildConfig), toolchain, buildConfig);
  }

  public TaskProvider<CMakeInstall> installTask(final TaskContainer tasks, final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String taskName = CMakeTasksConventions.installTaskName(library.getName(), library.getLinkType(),
        toolchain.getName(), buildConfig);
    return tasks.register(taskName, CMakeInstall.class, CMakeFileConventions.buildTarget(library.getName(),
        library.getLinkType(), toolchain.getName(), buildConfig), toolchain, buildConfig);
  }

  public static void configureRemote(final CMakeAssemble task, final Project project,
      final CMakeResolvedBinary<?> binary, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final Collection<CMakeResolvedProjectDependency> dependencies = new ArrayList<>();
    dependencies.addAll(binary.getPrivateProjectDependencies());
    dependencies.addAll(binary.getPublicProjectDependencies());
    configureRemote(task, project, toolchain, buildConfig, dependencies);
  }

  private static void configureRemote(final CMakeAssemble task, final Project project,
      final CMakeResolvedToolchain toolchain, final String buildConfig,
      final Collection<CMakeResolvedProjectDependency> dependencies) {
    dependencies.stream()
        .filter(dependency -> !Objects.equals(project.getPath(), dependency.getProjectPath()))
        .forEach(dependency -> task.dependsOn(CMakeTasksConventions.assembleModuleTaskName(dependency.getProjectPath(),
            dependency.getName(), dependency.getLinkType(), toolchain.getName(), buildConfig)));
  }

  public static void configureRemote(final CMakeConfigure task, final Project project,
      final CMakeResolvedBinary<?> binary, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    configureRemote(task, project, toolchain, buildConfig, binary.getAllProjectDependencies());
  }

  private static void configureRemote(final CMakeConfigure task, final Project project,
      final CMakeResolvedToolchain toolchain, final String buildConfig,
      final Collection<CMakeResolvedProjectDependency> dependencies) {
    dependencies.stream()
        .filter(dependency -> !Objects.equals(project.getPath(), dependency.getProjectPath()))
        .forEach(dependency -> task.dependsOn(CMakeTasksConventions.configureTaskName(dependency.getProjectPath(),
            toolchain.getName(), buildConfig)));
  }

  public static void configureRemote(final CMakeBuild task, final CMakeResolvedToolchain toolchain,
      final String buildConfig, final Collection<CMakeResolvedProjectDependency> dependencies) {
    dependencies.stream()
        .filter((dependency) -> !Objects.equals(dependency.getLinkType(), CMakeLinkVariant.INTERFACE))
        .forEach((dependency) -> {
          task.dependsOn("%s:%s".formatted(dependency.getProjectPath(), CMakeTasksConventions
              .buildTaskName(dependency.getName(), dependency.getLinkType(), toolchain.getName(), buildConfig)));
        });
  }

  public static Configuration createDirectoryDependencyConfiguration(final ConfigurationContainer configurations,
      final ObjectFactory objects, final String target, final CMakeResolvedProjectDependency dependency,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String name = "%s-dependency".formatted(target);
    System.out.println("in  : " + name);
    final Optional<Configuration> oldConfiguration = Optional.ofNullable(configurations.findByName(name));
    if (oldConfiguration.isPresent()) {
      return oldConfiguration.get();
    }
    return configurations.create(name, (newConfiguration) -> {
      newConfiguration.setCanBeDeclared(true);
      newConfiguration.setCanBeResolved(true);
      newConfiguration.setCanBeConsumed(false);
      newConfiguration.getAttributes().attribute(CMakeArtifactAttributes.LIBRARY_ATTRIBUTE,
          dependency.getName().toLowerCase());
      newConfiguration.getAttributes().attribute(CMakeArtifactAttributes.LINK_TYPE_ATTRIBUTE,
          dependency.getLinkType().toLowerCase());
      newConfiguration.getAttributes().attribute(CMakeArtifactAttributes.TOOLCHAIN_ATTRIBUTE,
          toolchain.getName().toLowerCase());
      newConfiguration.getAttributes().attribute(CMakeArtifactAttributes.BUILD_CONFIG_ATTRIBUTE,
          buildConfig.toLowerCase());
      newConfiguration.getAttributes().attribute(Category.CATEGORY_ATTRIBUTE,
          objects.named(Category.class, CMakeArtifactAttributes.CATEGORY));
    });
  }

  public static Configuration createOutputDirectoryConfiguration(final ConfigurationContainer configurations,
      final ObjectFactory objects, final String target, final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String name = "%s-directory".formatted(target);
    System.out.println("out : " + name);
    return configurations.create(name, (newConfiguration) -> {
      newConfiguration.setCanBeDeclared(true);
      newConfiguration.setCanBeResolved(false);
      newConfiguration.setCanBeConsumed(true);
      newConfiguration.getAttributes().attribute(CMakeArtifactAttributes.LIBRARY_ATTRIBUTE,
          library.getName().toLowerCase());
      newConfiguration.getAttributes().attribute(CMakeArtifactAttributes.LINK_TYPE_ATTRIBUTE,
          library.getLinkType().toLowerCase());
      newConfiguration.getAttributes().attribute(CMakeArtifactAttributes.TOOLCHAIN_ATTRIBUTE,
          toolchain.getName().toLowerCase());
      newConfiguration.getAttributes().attribute(CMakeArtifactAttributes.BUILD_CONFIG_ATTRIBUTE,
          buildConfig.toLowerCase());
      newConfiguration.getAttributes().attribute(Category.CATEGORY_ATTRIBUTE,
          objects.named(Category.class, CMakeArtifactAttributes.CATEGORY));
    });
  }

  public static void addOutputDirectoryArtifact(final ArtifactHandler artifacts,
      final Configuration configuration, final Directory outputDirectory,
      final TaskProvider<? extends CMakeBuild> buildTask) {
    artifacts.add(configuration.getName(), outputDirectory, (artifact) -> {
      artifact.builtBy(buildTask);
      artifact.setType(ArtifactTypeDefinition.DIRECTORY_TYPE);
    });
  }

  // public TaskProvider<Zip> packageTask(final TaskContainer tasks, final Project
  // project,
  // final CMakeResolvedLibrary library, final CMakeResolvedToolchain toolchain,
  // final String buildConfig) {
  // final String taskName =
  // CMakeTasksConventions.packageTaskName(library.getName(),
  // library.getLinkType(),
  // toolchain.getName(), buildConfig);
  // final TaskProvider<Zip> taskProvider = tasks.register(taskName, Zip.class);
  // taskProvider.configure(task -> {
  // task.setGroup(GROUP_PACKAGE);
  // task.setDescription("Packages dependency artifacts for %s (%s, %s)"
  // .formatted(library.getName(), toolchain.getName(), buildConfig));

  // final String configName =
  // CMakeTasksConventions.artifactDirsConfigurationName(
  // toolchain.getName(), buildConfig);
  // task.from(project.getConfigurations().getByName(configName).getIncoming().getFiles());

  // task.getArchiveFileName().set("%s-%s-%s-%s.zip".formatted(
  // library.getName().toLowerCase(), library.getLinkType().toLowerCase(),
  // toolchain.getName().toLowerCase(), buildConfig.toLowerCase()));
  // final String target = CMakeFileConventions.buildTarget(library.getName(),
  // library.getLinkType(),
  // toolchain.getName(), buildConfig);
  // task.getDestinationDirectory()
  // .set(CMakeFileConventions.targetBinaryDirectory(project.getLayout().getBuildDirectory().get(),
  // target, toolchain, buildConfig));
  // });
  // return taskProvider;
  // }

  // public TaskProvider<Zip> packageTask(final TaskContainer tasks, final Project
  // project,
  // final CMakeResolvedExecutable executable, final CMakeResolvedToolchain
  // toolchain, final String buildConfig) {
  // final String taskName =
  // CMakeTasksConventions.packageTaskName(executable.getName(),
  // toolchain.getName(), buildConfig);
  // final TaskProvider<Zip> taskProvider = tasks.register(taskName, Zip.class);
  // taskProvider.configure(task -> {
  // task.setGroup(GROUP_PACKAGE);
  // task.setDescription("Packages dependency artifacts for %s (%s, %s)"
  // .formatted(executable.getName(), toolchain.getName(), buildConfig));

  // final String configName =
  // CMakeTasksConventions.artifactDirsConfigurationName(
  // toolchain.getName(), buildConfig);
  // task.from(project.getConfigurations().getByName(configName).getIncoming().getFiles());

  // task.getArchiveFileName().set("%s-%s-%s.zip".formatted(
  // executable.getName().toLowerCase(),
  // toolchain.getName().toLowerCase(), buildConfig.toLowerCase()));
  // final String target = CMakeFileConventions.buildTarget(executable.getName(),
  // toolchain.getName(), buildConfig);
  // task.getDestinationDirectory()
  // .set(CMakeFileConventions.targetBinaryDirectory(project.getLayout().getBuildDirectory().get(),
  // target, toolchain, buildConfig));
  // });
  // return taskProvider;
  // }

  // public static void dependencyArtifactDirectories(final Project project, final
  // CMakeResolvedBinary<?> binary) {
  // project.getConfigurations().named(CONFIGURATION_ARTIFACT_DIRECTORIES).configure((configuration)
  // -> {
  // configuration.getIncoming().beforeResolve((dependencies)-> {
  // binary.getAllProjectDependencies().forEach((remoteProject)->{
  // final ProjectDependency projectDependency = project.getDependencies().pro
  // dependencies.getDependencies().add(null)
  // });
  // });
  // });
  // }
}
