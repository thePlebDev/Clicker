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

/**
 * The main function rendering a frame. In our case, it is yuv to RGBA8888
 * converter
 */
void drawFrame(void) {
//    if (!cameraReady_ || !yuvReader_) return;
//    AImage* image = yuvReader_->GetNextImage();
//    if (!image) {
//        return;
//    }
//
//    ANativeWindow_acquire(app_->window);
//    ANativeWindow_Buffer buf;
//    if (ANativeWindow_lock(app_->window, &buf, nullptr) < 0) {
//        yuvReader_->DeleteImage(image);
//        return;
//    }
//
//    yuvReader_->DisplayImage(&buf, image);
//    ANativeWindow_unlockAndPost(app_->window);
//    ANativeWindow_release(app_->window);
}


//todo: this will run instead of android_main if implemented
//extern "C" void ANativeActivity_onCreate(ANativeActivity* activity, void* savedState, size_t savedStateSize) {
//    // Initialization code for NativeActivity
//    LOGI("NativeActivity created");
//
//    // Set up app state, attach events, etc.
//    // ...
//
//}
static void ProcessAndroidCmd(struct android_app* app, int32_t cmd) {

    switch (cmd) {
        case APP_CMD_INIT_WINDOW:
            LOGI("NativeActivity APP_CMD_INIT_WINDOW");

            break;
        case APP_CMD_TERM_WINDOW:
            LOGI("NativeActivity APP_CMD_TERM_WINDOW");
            break;
        case APP_CMD_CONFIG_CHANGED:
            LOGI("NativeActivity APP_CMD_CONFIG_CHANGED");

            break;
        case APP_CMD_LOST_FOCUS:
            LOGI("NativeActivity APP_CMD_LOST_FOCUS");
            break;
    }
}


extern "C" void android_main(struct android_app* app) {
    LOGI("NativeActivity ANDROID_MAIN");
    app->onAppCmd = [](android_app* app, int32_t cmd) {
        if (cmd == APP_CMD_INIT_WINDOW) {
            __android_log_print(ANDROID_LOG_INFO, "NativeActivity", "Window initialized.");
        }
    };

    while (true) {
        int events;
        android_poll_source* source;

        // Process events
        while (ALooper_pollAll(0, nullptr, &events, (void**)&source) >= 0) {
            if (source != nullptr) {
                source->process(app, source);
            }
            if (app->destroyRequested != 0) {
                return;
            }
        }
        // Your main loop work here
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_cameraNDK_CameraNDKNativeActivity_notifyCameraPermission(JNIEnv *env,
                                                                                  jobject thiz,
                                                                                  jboolean granted) {
//    std::thread permissionHandler();
//    permissionHandler().detach();

    LOGI("PERMISSION GRANTED");
}