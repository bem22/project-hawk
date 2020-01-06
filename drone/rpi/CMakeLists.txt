cmake_minimum_required(VERSION 3.13)
project(server C)

set(CMAKE_C_STANDARD 11)

add_executable(server main.c network_utils/hawk-packets.c network_utils/hawk-packets.h network_utils/hawk-actions.c network_utils/hawk-actions.h network_utils/string-utils.c network_utils/string-utils.h network_utils/connection.h network_utils/connection.c drone_utils/state.c drone_utils/state.h drone_utils/encoder.h drone_utils/encoder.h drone_utils/encoder.c drone_utils/ppmer.c drone_utils/ppmer.h)

set(CMAKE_USE_PTHREADS_INIT true)
set(CMAKE_USE_PTHREADS_INIT ON)
SET(CMAKE_C_FLAGS "-pthread  -lpigpio -lrt ${CMAKE_C_FLAGS}")

find_package(Threads REQUIRED)

find_path(pigpio_INCLUDE_DIR
        NAMES pigpio.h pigpiod_if.h pigpiod_if2.h
        HINTS /usr/local/include)
find_library(pigpio_LIBRARY
        NAMES libpigpio.so
        HINTS /usr/local/lib)
find_library(pigpiod_if_LIBRARY
        NAMES libpigpiod_if.so
        HINTS /usr/local/lib)
find_library(pigpiod_if2_LIBRARY
        NAMES libpigpiod_if2.so
        HINTS /usr/local/lib)

set(pigpio_INCLUDE_DIRS ${pigpio_INCLUDE_DIR})
set(pigpio_INCLUDES     ${pigpio_INCLUDE_DIR})

include(FindPackageHandleStandardArgs)
find_package_handle_standard_args(pigpio
        DEFAULT_MSG
        pigpio_INCLUDE_DIR pigpio_LIBRARY pigpiod_if_LIBRARY pigpiod_if2_LIBRARY)


# find_package(OpenSSL REQUIRED)
# find_package(pigpio REQUIRED)
# include_directories(${pigpio_INCLUDE_DIRS})
# target_link_libraries(server LINK_PRIVATE ${pigpio_LIBS})
# target_link_libraries(server OpenSSL::SSL)
