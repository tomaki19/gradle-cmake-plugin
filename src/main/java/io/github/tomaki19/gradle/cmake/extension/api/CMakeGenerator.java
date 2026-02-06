/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

public final class CMakeGenerator {

    public static final CMakeGenerator BORLAND_MAKEFILES = new CMakeGenerator("Borland Makefiles");
    public static final CMakeGenerator MSYS_MAKEFILES = new CMakeGenerator("MSYS Makefiles");
    public static final CMakeGenerator MINGW_MAKEFILE = new CMakeGenerator("MinGW Makefiles");
    public static final CMakeGenerator NMAKE_MAKEFILES = new CMakeGenerator("NMake Makefiles");
    public static final CMakeGenerator NMAKE_MAKEFILES_JOM = new CMakeGenerator("NMake Makefiles JOM");
    public static final CMakeGenerator UNIX_MAKEFILES = new CMakeGenerator("Unix Makefiles");
    public static final CMakeGenerator WATCOM_WMAKE = new CMakeGenerator("Watcom WMake");
    public static final CMakeGenerator NINJA = new CMakeGenerator("Ninja");
    public static final CMakeGenerator NINJA_MULTI_CONFIG = new CMakeGenerator("Ninja Multi-Config");
    public static final CMakeGenerator FASTBUILD = new CMakeGenerator("FASTBuild");
    public static final CMakeGenerator VISUAL_STUDIO_6 = new CMakeGenerator("Visual Studio 6");
    public static final CMakeGenerator VISUAL_STUDIO_7 = new CMakeGenerator("Visual Studio 7");
    public static final CMakeGenerator VISUAL_STUDIO_7_NET_2003 = new CMakeGenerator("Visual Studio 7 .NET 2003");
    public static final CMakeGenerator VISUAL_STUDIO_8_2005 = new CMakeGenerator("Visual Studio 8 2005");
    public static final CMakeGenerator VISUAL_STUDIO_9_2008 = new CMakeGenerator("Visual Studio 9 2008");
    public static final CMakeGenerator VISUAL_STUDIO_10_2010 = new CMakeGenerator("Visual Studio 10 2010");
    public static final CMakeGenerator VISUAL_STUDIO_11_2012 = new CMakeGenerator("Visual Studio 11 2012");
    public static final CMakeGenerator VISUAL_STUDIO_12_2013 = new CMakeGenerator("Visual Studio 12 2013");
    public static final CMakeGenerator VISUAL_STUDIO_14_2015 = new CMakeGenerator("Visual Studio 14 2015");
    public static final CMakeGenerator VISUAL_STUDIO_15_2017 = new CMakeGenerator("Visual Studio 15 2017");
    public static final CMakeGenerator VISUAL_STUDIO_16_2019 = new CMakeGenerator("Visual Studio 16 2019");
    public static final CMakeGenerator VISUAL_STUDIO_17_2022 = new CMakeGenerator("Visual Studio 17 2022");
    public static final CMakeGenerator VISUAL_STUDIO_18_2026 = new CMakeGenerator("Visual Studio 18 2026");
    public static final CMakeGenerator GREEN_HILLS_MULTI = new CMakeGenerator("Green Hills MULTI");
    public static final CMakeGenerator XCODE = new CMakeGenerator("Xcode");
    public static final CMakeGenerator CODEBLOCKS = new CMakeGenerator("CodeBlocks");
    public static final CMakeGenerator CODELITE = new CMakeGenerator("CodeLite");
    public static final CMakeGenerator ECLIPSE_CDT4 = new CMakeGenerator("Eclipse CDT4");
    public static final CMakeGenerator KATE = new CMakeGenerator("Kate");
    public static final CMakeGenerator SUBLIME_TEXT_2 = new CMakeGenerator("Sublime Text 2");

    private final String name;

    private CMakeGenerator(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static CMakeGenerator custom(final String name) {
        return new CMakeGenerator(name);
    }

}
