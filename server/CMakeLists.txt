cmake_minimum_required(VERSION 3.15)
project(server C)

set(CMAKE_C_STANDARD 11)

add_executable(server server.c server_utils/hawk-packets.c server_utils/hawk-packets.h server_utils/hawk-actions.c server_utils/hawk-actions.h server_utils/string-utils.c server_utils/string-utils.h)

set(CMAKE_USE_PTHREADS_INIT true)
set(CMAKE_USE_PTHREADS_INIT ON)
SET(CMAKE_C_FLAGS -pthread)

find_package(Threads REQUIRED)
find_package(OpenSSL REQUIRED)
target_link_libraries(server OpenSSL::SSL)
