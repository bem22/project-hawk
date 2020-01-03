cmake_minimum_required(VERSION 3.15)
project(server C)

set(CMAKE_C_STANDARD 11)

add_executable(server main.c network_utils/hawk-packets.c network_utils/hawk-packets.h network_utils/hawk-actions.c network_utils/hawk-actions.h network_utils/string-utils.c network_utils/string-utils.h network_utils/connection.h network_utils/connection.c drone_utils/state.c drone_utils/state.h)

set(CMAKE_USE_PTHREADS_INIT true)
set(CMAKE_USE_PTHREADS_INIT ON)
SET(CMAKE_C_FLAGS "-pthread ${CMAKE_C_FLAGS}")

find_package(Threads REQUIRED)
find_package(OpenSSL REQUIRED)
target_link_libraries(server OpenSSL::SSL)
