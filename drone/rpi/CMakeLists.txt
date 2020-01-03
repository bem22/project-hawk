cmake_minimum_required(VERSION 3.13)
project(server C)

set(CMAKE_C_STANDARD 11)

add_executable(server main.c network_utils/hawk-packets.c network_utils/hawk-packets.h network_utils/hawk-actions.c network_utils/hawk-actions.h network_utils/string-utils.c network_utils/string-utils.h network_utils/connection.h network_utils/connection.c drone_utils/state.c drone_utils/state.h drone_utils/servo.c)

set(CMAKE_USE_PTHREADS_INIT true)
set(CMAKE_USE_PTHREADS_INIT ON)
SET(CMAKE_C_FLAGS "-pthread  -lpigpio -lrt ${CMAKE_C_FLAGS}")

find_package(Threads REQUIRED)
find_package(OpenSSL REQUIRED)
find_package(pigpio REQUIRED)

include_directories(${pigpio_INCLUDE_DIRS})
target_link_libraries(server LINK_PRIVATE ${pigpio_LIBS})
target_link_libraries(server OpenSSL::SSL)
