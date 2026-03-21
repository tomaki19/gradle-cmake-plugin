/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.files;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.gradle.api.file.Directory;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public abstract class CMakeFileContent {

  private static final Configuration FREEMARKER_CONFIG;

  static {
    FREEMARKER_CONFIG = new Configuration(Configuration.VERSION_2_3_34);
    FREEMARKER_CONFIG.setClassForTemplateLoading(CMakeFileContent.class, "/templates/cmake");
    FREEMARKER_CONFIG.setDefaultEncoding(StandardCharsets.UTF_8.name());
    FREEMARKER_CONFIG.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    FREEMARKER_CONFIG.setInterpolationSyntax(Configuration.SQUARE_BRACKET_INTERPOLATION_SYNTAX);
    FREEMARKER_CONFIG.setLogTemplateExceptions(false);
  }

  private final String name;
  private final String projectName;
  private final Directory projectDirectory;
  private final Directory buildDirectory;

  public CMakeFileContent(final String name, final String projectName, final Directory projectDirectory,
      final Directory buildDirectory) {
    this.name = name;
    this.projectName = projectName;
    this.projectDirectory = projectDirectory;
    this.buildDirectory = buildDirectory;
  }

  public String getName() {
    return name;
  }

  protected String getProjectName() {
    return projectName;
  }

  protected Directory getProjectDirectory() {
    return projectDirectory;
  }

  protected Directory getBuildDirectory() {
    return buildDirectory;
  }

  public abstract void writeTo(final FileOutputStream outputStream) throws IOException;

  protected void processTemplate(final String templateName, final Map<String, Object> model,
      final FileOutputStream outputStream) throws IOException {
    try {
      final Template template = FREEMARKER_CONFIG.getTemplate(templateName);
      final Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
      template.process(model, writer);
      writer.flush();
    } catch (final TemplateException e) {
      throw new IOException("Failed to process FreeMarker template: " + templateName, e);
    }
  }

}
