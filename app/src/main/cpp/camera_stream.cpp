//
// Created by Tristan on 2024-11-04.
//
#include <camera/NdkCameraManager.h>
#include <camera/NdkCameraError.h>
#include <camera/NdkCameraMetadataTags.h>
#include <camera/NdkCameraMetadata.h>

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


extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_nativeLibraryClasses_CameraStreamNDK_notifyCameraPermission(JNIEnv *env,
                                                                                     jobject thiz,
                                                                                     jboolean granted) {
    LOGI ("PERMISSION IS ------> %hhu",granted);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_nativeLibraryClasses_CameraStreamNDK_TakePhoto(JNIEnv *env, jobject thiz) {
    //this is what should run when they try to take the actual photo
}