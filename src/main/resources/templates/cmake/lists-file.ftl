<#macro packageDeps deps>
<#list deps as dep>
<#assign pkg = dep.resolvedPackage>
    find_package( [=pkg.name] REQUIRED
<#if pkg.moduleMode>
        MODULE
<#else>
        CONFIG
</#if>
<#if pkg.components?has_content>
        COMPONENTS
<#list pkg.components as comp>
            [=comp]
</#list>
</#if>
    )
<#list pkg.properties?keys as propKey>
    set( [=pkg.name]_[=propKey?upper_case] [=pkg.properties[propKey]?upper_case] )
</#list>
</#list>
</#macro>
<#macro projectIncludes includes>
<#list includes as include>
    include( [=include] )
</#list>
</#macro>
<#macro targetIncludeDirs target access headerDirs>
    target_include_directories( [=target] [=access]
<#list headerDirs as dir>
        "$<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/[=dir]>"
</#list>
        "$<INSTALL_INTERFACE:${CMAKE_INSTALL_INCLUDEDIR}>"
    )
</#macro>
<#macro targetSources target sourcePaths>
    target_sources( [=target] PRIVATE
<#list sourcePaths as src>
        "${CMAKE_CURRENT_SOURCE_DIR}/[=src]"
</#list>
    )
</#macro>
<#macro targetCompileOptions target access options>
    target_compile_options( [=target] [=access]
<#list options as opt>
        [=opt]
</#list>
    )
</#macro>
<#macro targetCompileDefinitions target access definitions>
    target_compile_definitions( [=target] [=access]
<#list definitions as def>
        [=def]
</#list>
    )
</#macro>
<#macro targetLinkLibraries target access libs>
    target_link_libraries( [=target] [=access]
<#list libs as lib>
        [=lib]
</#list>
    )
</#macro>
<#macro targetProperties target outputName targetRelPath buildConfigs>
    set_target_properties( [=target] PROPERTIES
        OUTPUT_NAME "[=outputName]"
        ARCHIVE_OUTPUT_DIRECTORY "${CMAKE_CURRENT_SOURCE_DIR}/[=targetRelPath]"
<#list buildConfigs as cfg>
        ARCHIVE_OUTPUT_DIRECTORY_[=cfg?upper_case] "${CMAKE_CURRENT_SOURCE_DIR}/[=targetRelPath]"
</#list>
        LIBRARY_OUTPUT_DIRECTORY "${CMAKE_CURRENT_SOURCE_DIR}/[=targetRelPath]"
<#list buildConfigs as cfg>
        LIBRARY_OUTPUT_DIRECTORY_[=cfg?upper_case] "${CMAKE_CURRENT_SOURCE_DIR}/[=targetRelPath]"
</#list>
        RUNTIME_OUTPUT_DIRECTORY "${CMAKE_CURRENT_SOURCE_DIR}/[=targetRelPath]"
<#list buildConfigs as cfg>
        RUNTIME_OUTPUT_DIRECTORY_[=cfg?upper_case] "${CMAKE_CURRENT_SOURCE_DIR}/[=targetRelPath]"
</#list>
    )
</#macro>
<#macro stripDebugCmd target>
    add_custom_command( TARGET [=target] POST_BUILD
        COMMAND ${CMAKE_COMMAND} -E copy $<TARGET_FILE:[=target]> $<TARGET_FILE:[=target]>.debug
        COMMAND ${CMAKE_STRIP} -g $<TARGET_FILE:[=target]>
    )
</#macro>
cmake_minimum_required( VERSION 3.21 )
project( [=projectName] LANGUAGES C CXX )
include( GNUInstallDirs )
set( CMAKE_EXPORT_COMPILE_COMMANDS ON CACHE INTERNAL "" )
set( CMAKE_WINDOWS_EXPORT_ALL_SYMBOLS ON CACHE INTERNAL "" )
include(CMakePrintHelpers)
cmake_print_variables(CMAKE_TOOLCHAIN_FILE)
cmake_print_variables(CMAKE_TOOLCHAIN_NAME)
cmake_print_variables(CMAKE_MODULE_PATH)
<#list toolchains as tc>
<#if tc.hasLibraries>
if( ${CMAKE_TOOLCHAIN_NAME} STREQUAL "[=tc.name]" )
<#list tc.interfaceLibraries as lib>
<@packageDeps lib.packageDependencies/>
<@projectIncludes lib.projectIncludes/>
    add_library( [=lib.target] INTERFACE )
    add_library( [=lib.projectAliasTarget] ALIAS [=lib.target])
<@targetIncludeDirs lib.target "INTERFACE" lib.headerDirs/>
<#if lib.hasInterfaceCompileOptions>
<@targetCompileOptions lib.target "INTERFACE" lib.interfaceCompileOptions/>
</#if>
<#if lib.hasInterfaceCompileDefinitions>
<@targetCompileDefinitions lib.target "INTERFACE" lib.interfaceCompileDefinitions/>
</#if>
<#if lib.hasInterfaceLinking>
<@targetLinkLibraries lib.target "INTERFACE" lib.interfaceLinkLibraries/>
</#if>
</#list>
<#list tc.staticLibraries as lib>
<@packageDeps lib.packageDependencies/>
<@projectIncludes lib.projectIncludes/>
    add_library( [=lib.target] STATIC )
    add_library( [=lib.projectAliasTarget] ALIAS [=lib.target])
<@targetIncludeDirs lib.target "PUBLIC" lib.headerDirs/>
<@targetSources lib.target lib.sourcePaths/>
<#if lib.hasPrivateCompileOptions>
<@targetCompileOptions lib.target "PRIVATE" lib.privateCompileOptions/>
</#if>
<#if lib.hasPublicCompileOptions>
<@targetCompileOptions lib.target "PUBLIC" lib.publicCompileOptions/>
</#if>
<#if lib.hasPrivateCompileDefinitions>
<@targetCompileDefinitions lib.target "PRIVATE" lib.privateCompileDefinitions/>
</#if>
<#if lib.hasPublicCompileDefinitions>
<@targetCompileDefinitions lib.target "PUBLIC" lib.publicCompileDefinitions/>
</#if>
<#if lib.hasPrivateLinking>
<@targetLinkLibraries lib.target "PRIVATE" lib.privateLinkLibraries/>
</#if>
<#if lib.hasPublicLinking>
<@targetLinkLibraries lib.target "PUBLIC" lib.publicLinkLibraries/>
</#if>
<@targetProperties lib.target lib.outputName lib.targetRelPath lib.buildConfigs/>
<#if lib.stripDebug>
<@stripDebugCmd lib.target/>
</#if>
</#list>
<#list tc.sharedLibraries as lib>
<@packageDeps lib.packageDependencies/>
<@projectIncludes lib.projectIncludes/>
    add_library( [=lib.target] SHARED )
    add_library( [=lib.projectAliasTarget] ALIAS [=lib.target])
<@targetIncludeDirs lib.target "PUBLIC" lib.headerDirs/>
<@targetSources lib.target lib.sourcePaths/>
<#if lib.hasPrivateCompileOptions>
<@targetCompileOptions lib.target "PRIVATE" lib.privateCompileOptions/>
</#if>
<#if lib.hasPublicCompileOptions>
<@targetCompileOptions lib.target "PUBLIC" lib.publicCompileOptions/>
</#if>
<#if lib.hasPrivateCompileDefinitions>
<@targetCompileDefinitions lib.target "PRIVATE" lib.privateCompileDefinitions/>
</#if>
<#if lib.hasPublicCompileDefinitions>
<@targetCompileDefinitions lib.target "PUBLIC" lib.publicCompileDefinitions/>
</#if>
<#if lib.hasPrivateLinking>
<@targetLinkLibraries lib.target "PRIVATE" lib.privateLinkLibraries/>
</#if>
<#if lib.hasPublicLinking>
<@targetLinkLibraries lib.target "PUBLIC" lib.publicLinkLibraries/>
</#if>
<@targetProperties lib.target lib.outputName lib.targetRelPath lib.buildConfigs/>
<#if lib.stripDebug>
<@stripDebugCmd lib.target/>
</#if>
</#list>
endif()
</#if>
<#if tc.hasApplications>
if( ${CMAKE_TOOLCHAIN_NAME} STREQUAL "[=tc.name]" )
<#list tc.applications as exec>
<@packageDeps exec.packageDependencies/>
<@projectIncludes exec.projectIncludes/>
    add_executable( [=exec.target] )
<@targetIncludeDirs exec.target "PRIVATE" exec.headerDirs/>
<@targetSources exec.target exec.sourcePaths/>
<#if exec.hasPrivateCompileOptions>
<@targetCompileOptions exec.target "PRIVATE" exec.privateCompileOptions/>
</#if>
<#if exec.hasPublicCompileOptions>
<@targetCompileOptions exec.target "PUBLIC" exec.publicCompileOptions/>
</#if>
<#if exec.hasPrivateCompileDefinitions>
<@targetCompileDefinitions exec.target "PRIVATE" exec.privateCompileDefinitions/>
</#if>
<#if exec.hasPublicCompileDefinitions>
<@targetCompileDefinitions exec.target "PUBLIC" exec.publicCompileDefinitions/>
</#if>
<#if exec.hasPrivateLinking>
<@targetLinkLibraries exec.target "PRIVATE" exec.privateLinkLibraries/>
</#if>
<#if exec.hasPublicLinking>
<@targetLinkLibraries exec.target "PUBLIC" exec.publicLinkLibraries/>
</#if>
<@targetProperties exec.target exec.outputName exec.targetRelPath exec.buildConfigs/>
<#if exec.stripDebug>
<@stripDebugCmd exec.target/>
</#if>
</#list>
endif()
</#if>
<#if tc.hasTests>
enable_testing()
include( CTest )
if( ${CMAKE_TOOLCHAIN_NAME} STREQUAL "[=tc.name]" )
<#list tc.tests as exec>
<@packageDeps exec.packageDependencies/>
<@projectIncludes exec.projectIncludes/>
    add_executable( [=exec.target] )
<@targetIncludeDirs exec.target "PRIVATE" exec.headerDirs/>
<@targetSources exec.target exec.sourcePaths/>
<#if exec.hasPrivateCompileOptions>
<@targetCompileOptions exec.target "PRIVATE" exec.privateCompileOptions/>
</#if>
<#if exec.hasPublicCompileOptions>
<@targetCompileOptions exec.target "PUBLIC" exec.publicCompileOptions/>
</#if>
<#if exec.hasPrivateCompileDefinitions>
<@targetCompileDefinitions exec.target "PRIVATE" exec.privateCompileDefinitions/>
</#if>
<#if exec.hasPublicCompileDefinitions>
<@targetCompileDefinitions exec.target "PUBLIC" exec.publicCompileDefinitions/>
</#if>
<#if exec.hasPrivateLinking>
<@targetLinkLibraries exec.target "PRIVATE" exec.privateLinkLibraries/>
</#if>
<#if exec.hasPublicLinking>
<@targetLinkLibraries exec.target "PUBLIC" exec.publicLinkLibraries/>
</#if>
<@targetProperties exec.target exec.outputName exec.targetRelPath exec.buildConfigs/>
<#if exec.stripDebug>
<@stripDebugCmd exec.target/>
</#if>
    add_test(
        NAME [=exec.target]
        COMMAND $<TARGET_FILE:[=exec.target]>
    )
</#list>
endif()
</#if>
</#list>
