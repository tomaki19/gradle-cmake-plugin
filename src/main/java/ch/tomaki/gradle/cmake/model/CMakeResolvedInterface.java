/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.HashSet;
import java.util.Set;

import ch.tomaki.gradle.cmake.extension.api.CMakeBinary;

public abstract class CMakeResolvedInterface extends CMakeResolvedName {

    private final Set<String> headers;
    private final Set<String> sources;
    private final Set<String> privateCompileOptions;
    private final Set<String> privateCompileDefinitions;
    private final Set<String> privateLinkOptions = new HashSet<>();
    private final Set<String> privateSystemPackageDependencies = new HashSet<>();
    private final Set<CMakeResolvedProjectPackageDependency> privateProjectPackageDependencies = new HashSet<>();

    CMakeResolvedInterface(final CMakeBinary binary) {
        super(binary.getName());
        this.headers = binary.getHeaders().get();
        this.sources = binary.getSources().get();
        this.privateCompileOptions = binary.getPrivateCompileOptions().get();
        this.privateCompileDefinitions = binary.getPrivateCompileDefinitions().get();
    }

    public Set<String> getHeaders() {
        return headers;
    }

    public Set<String> getSources() {
        return sources;
    }

    public Set<String> getPrivateCompileOptions() {
        return privateCompileOptions;
    }

    public Set<String> getPrivateCompileDefinitions() {
        return privateCompileDefinitions;
    }

    public Set<String> getPrivateLinkOptions() {
        return privateLinkOptions;
    }

    void addPrivateLinkOption(final String option) {
        privateLinkOptions.add(option);
    }

    public Set<String> getPrivateSystemPackageDependencies() {
        return privateSystemPackageDependencies;
    }

    void addPrivateSystemPackageDependency(final String dependency) {
        privateSystemPackageDependencies.add(dependency);
    }

    public Set<CMakeResolvedProjectPackageDependency> getPrivateProjectPackageDependencies() {
        return privateProjectPackageDependencies;
    }

    void addPrivateProjectPackageDependency(final CMakeResolvedProjectPackageDependency dependency) {
        privateProjectPackageDependencies.add(dependency);
    }

}
