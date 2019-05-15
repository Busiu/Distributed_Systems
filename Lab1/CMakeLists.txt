cmake_minimum_required(VERSION 3.10)
project(Distributed_Systems)

set(CMAKE_CXX_STANDARD 17)

add_executable(Distributed_Systems src/main.cpp
        src/Application.cpp
        src/Client.cpp
        src/MessageQueue.cpp)

target_link_libraries(Distributed_Systems -lws2_32)