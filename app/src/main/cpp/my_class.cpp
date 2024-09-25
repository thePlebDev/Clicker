//
// Created by 14035 on 2024-09-24.
//

// my_class.cpp
#include "my_class.h" // header in local directory
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <android/log.h>
#include <jni.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>

#define LOG_TAG "libgl2jni"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)


using namespace N;
using namespace std;

void my_class::do_something()
{
    const int size = 1024;
    LOGI("-----------THIS IS DONE THROUGH THE TESTING OF THE HEADER FILES -----------> %d,",  size);
}