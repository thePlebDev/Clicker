//
// Created by Tristan on 2024-11-04.
//
#include <camera/NdkCameraManager.h>
#include <camera/NdkCameraError.h>
#include <camera/NdkCameraMetadataTags.h>
#include <camera/NdkCameraMetadata.h>
#include <android/native_activity.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <media/NdkImageReader.h>
#include <android_native_app_glue.h>
#include <functional>
#include <thread>

#include "camera_manager.h"
#include "camera_engine.h"
#include <dirent.h>





#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <android/log.h>
#include <jni.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>

#define LOG_TAG "streamLogging"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define ASSERT(cond, fmt, ...) //takes a condition to be evaluated, a string format and any extra variables






/**
 * Called when the NativeActivity is created
 * */
extern "C" void android_main(struct android_app* state) {

}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_cameraNDK_CameraNDKNativeActivity_notifyCameraPermission(JNIEnv *env,jobject thiz,jboolean granted) {


    LOGI("PERMISSION GRANTED");
}




