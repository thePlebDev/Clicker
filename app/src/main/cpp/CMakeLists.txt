# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)



# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add_library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.


#Add a library to the project using the specified source files.
add_library(gl_code SHARED
        gl_code.cpp)

add_library(my_class SHARED
        my_class.cpp)

add_library(square_code SHARED
        square_code.cpp)

# add lib dependencies
target_link_libraries(gl_code
        my_class  # Link my_class with gl_code
        android
        log
        EGL
        GLESv2)

include_directories(my_class.h)
# Link libraries for my_class
target_link_libraries(my_class
        android
        log
        EGL
        GLESv2)

target_link_libraries(square_code
        android
        log
        EGL
        GLESv2)
