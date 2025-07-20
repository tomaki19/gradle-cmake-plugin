/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.extension.api;

import org.gradle.api.provider.SetProperty;

public interface CMakeInterface {

  SetProperty<String> getHeaders();

}
