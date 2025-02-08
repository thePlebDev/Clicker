# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)

set(COMMON_SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../../../common)


# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add_library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.


#Add a library to the project using the specified source files.
add_library(gl_code SHARED
        gl_code.cpp)

# build native_app_glue as a static lib
include_directories(${ANDROID_NDK}/sources/android/native_app_glue
        ${COMMON_SOURCE_DIR})


add_library(app_glue STATIC
        ${ANDROID_NDK}/sources/android/native_app_glue/android_native_app_glue.c)

# Export ANativeActivity_onCreate(),
# Refer to: https://github.com/android-ndk/ndk/issues/381.
set(CMAKE_SHARED_LINKER_FLAGS
        "${CMAKE_SHARED_LINKER_FLAGS} -u ANativeActivity_onCreate")

add_library(my_class SHARED
        my_class.cpp)

add_library(square_code SHARED
        square_code.cpp)

add_library(blur_effect SHARED
        blur_effect.cpp)

add_library(cube_code SHARED
        cube_code.cpp)

add_library(video_encoder SHARED
        video_encoder.cpp)

add_library(camera_stream SHARED
        camera_stream.cpp)

add_library(parse_url SHARED
        parsing/parse_url.cpp)

add_library(
        # Specifies the name of the library.
        rtmp_client
        # Sets the library as a shared library.
        SHARED
        # Provides a relative path to your source file(s).
        rtmp_client.cpp

)
# I need to link the target libraries with rtmp.h and then link rtmp.h with rtmp_client

#added to the INCLUDE_DIRECTORIES directory for preproccessing
include_directories(rtmp_client.h)

# Link libraries for rtmp_client
target_link_libraries(rtmp_client
        parse_url
        android
        log
        EGL
        GLESv2)
target_link_libraries(parse_url
        android
        log
        EGL
        GLESv2)









find_library( camera-lib camera2ndk )
find_library( media-lib mediandk )
find_library( android-lib android )
find_library( gl-lib GLESv2 )

#TODO: I NEED TO ADD THE TARGET LINK LIBRARIES
add_library(understand_triangle SHARED
        understand_triangle.cpp)
# Link libraries for understand_triangle
target_link_libraries(understand_triangle
        android
        log
        EGL
        GLESv2)

#This got rid of the UI errors I was having with the types inside of camera_manager.cpp
add_library(camera_manager SHARED
        camera_manager.cpp)
include_directories(camera_manager.h)
target_link_libraries(camera_manager
        android
        log
        EGL
        GLESv2)

target_link_libraries(camera_stream
        ${CMAKE_CURRENT_SOURCE_DIR}/camera_manager.cpp
        android
        log
        EGL
        GLESv2
        m
        app_glue
        ${camera-lib} ${media-lib} ${android-lib} ${gl-lib}
        )

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
target_link_libraries(blur_effect
        android
        log
        EGL
        GLESv2)

target_link_libraries(cube_code
        android
        log
        EGL
        GLESv2)
target_link_libraries(video_encoder
        android
        log
        EGL
        GLESv2)
