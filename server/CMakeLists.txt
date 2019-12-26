cmake_minimum_required(VERSION 3.15)
project(server C)

set(CMAKE_C_STANDARD 11)


add_executable(server main.c)

find_package(OpenSSL REQUIRED)
target_link_libraries(server OpenSSL::SSL)
