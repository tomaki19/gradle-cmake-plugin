set( CMAKE_SYSTEM_NAME Linux )
set( CMAKE_SYSTEM_PROCESSOR x86_64 )

set( LANGAUGES C CXX )

set( C_STANDARD 17 )
set( C_STANDARD_REQUIRED ON )
set( CMAKE_C_EXTENSIONS ON )

set( CXX_STANDARD 17 )
set( CXX_STANDARD_REQUIRED ON )
set( CMAKE_CXX_EXTENSIONS ON )

add_compile_options( -Wall -Wextra -ffunction-sections -fdata-sections -fstack-protector-strong
    $<$<CONFIG:release>:-O3 -Werror>
    $<$<CONFIG:debug>:-O0 -g>
)

add_link_options( -Wl,--gc-sections
)
