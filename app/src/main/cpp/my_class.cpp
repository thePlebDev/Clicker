//
// Created by 14035 on 2024-09-24.
//

// my_class.cpp
#include "my_class.h"
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <android/log.h>

#define LOG_TAG "libgl2jni"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
//using namespace N;
//
//namespace N {
//    void my_class::do_something() {
//        const int size = 1024;
//        LOGI("-----------THIS IS DONE THROUGH THE TESTING OF THE HEADER FILES -----------> %d,", size);
//    }
//}
void do_something() {
    const int size = 1024;
    LOGI("-----------THIS IS DONE THROUGH THE TESTING OF THE HEADER FILES -----------> %d,", size);
}