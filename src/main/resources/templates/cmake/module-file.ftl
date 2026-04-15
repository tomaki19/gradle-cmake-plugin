if( NOT TARGET [=target] )
<#list packageDependencies as dep>
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
<#list projectIncludes as include>
include( [=include] )
</#list>
    add_library( [=target] [=linkType] IMPORTED )
<#if linkType == "SHARED">
    set_target_properties( [=target] PROPERTIES
        IMPORTED_CONFIGURATIONS [=buildConfigUpper]
        IMPORTED_LOCATION_[=buildConfigUpper] "${CMAKE_CURRENT_LIST_DIR}/[=targetRelPath]/[=sharedLibName]"
<#if isLinux>
        IMPORTED_SONAME_[=buildConfigUpper] "[=soname]"
</#if>
<#if isWindows>
        IMPORTED_IMPLIB_[=buildConfigUpper] "${CMAKE_CURRENT_LIST_DIR}/[=targetRelPath]/[=implibName]"
</#if>
    )
<#elseif linkType == "STATIC">
    set_target_properties( [=target] PROPERTIES
        IMPORTED_CONFIGURATIONS [=buildConfigUpper]
        IMPORTED_LOCATION_[=buildConfigUpper] "${CMAKE_CURRENT_LIST_DIR}/[=targetRelPath]/[=staticLibName]"
    )
</#if>
<#list headerRelPaths as headerPath>
    set_property( TARGET [=target] APPEND PROPERTY
        INTERFACE_INCLUDE_DIRECTORIES "${CMAKE_CURRENT_LIST_DIR}/[=headerPath]"
    )
</#list>
<#list publicCompileOptions as option>
    set_property( TARGET [=target] APPEND PROPERTY
        INTERFACE_COMPILE_OPTIONS [=option]
    )
</#list>
<#list publicCompileDefinitions as definition>
    set_property( TARGET [=target] APPEND PROPERTY
        INTERFACE_COMPILE_DEFINITIONS [=definition]
    )
</#list>
<#list publicProjectDepTargets as depTarget>
    set_property( TARGET [=target] APPEND PROPERTY
        INTERFACE_LINK_LIBRARIES [=depTarget]
    )
</#list>
<#list publicPackageLinkLibraries as linkLib>
    set_property( TARGET [=target] APPEND PROPERTY
        INTERFACE_LINK_LIBRARIES [=linkLib]
    )
</#list>
<#list publicLinkOptions as option>
    set_property( TARGET [=target] APPEND PROPERTY
        INTERFACE_LINK_LIBRARIES [=option]
    )
</#list>
endif()
