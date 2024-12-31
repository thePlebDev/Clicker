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



/**
 *
 * */
CameraEngine::CameraEngine(android_app* app)
        :app_(app) //to avoid the automatic default constructor creation, member initializer list.
           {

}

CameraEngine::~CameraEngine() {
    //cameraReady_ = false;
    //  DeleteCamera();

}









void CameraEngine::SaveNativeWinRes(int32_t w, int32_t h, int32_t format) {
    savedNativeWinRes_.width = w;
    savedNativeWinRes_.height = h;
    savedNativeWinRes_.format = format;
    LOGI("SaveNativeWinRes: width=%d, height=%d, format=%d", w, h, format);

}

struct android_app* CameraEngine::AndroidApp(void) const { return app_; }

/**
 * Handle Android System APP_CMD_INIT_WINDOW message
 *   Request camera persmission from Java side
 *   Create camera object if camera has been granted
 */
void CameraEngine::OnAppInitWindow(void) {
    //TODO: REIMPLEMENT THIS CONDITIONAL AFTER THE CAMERA IS ACTUALLY WORKING

}

/**
* MAX_BUF_COUNT:
*   Max buffers in this ImageReader.
*/
#define MAX_BUF_COUNT 4





/*
 * SampleEngine global object
 */
static CameraEngine* pEngineObj = nullptr;
CameraEngine* GetAppEngine(void) {
//    ASSERT(pEngineObj, "AppEngine has not initialized");
    return pEngineObj;
}






static void ProcessAndroidCmd(struct android_app* app, int32_t cmd) {
    CameraEngine* engine = reinterpret_cast<CameraEngine*>(app->userData);

    switch (cmd) {
        case APP_CMD_INIT_WINDOW: //Called when the NativeActivity is created
            LOGI("NativeActivity APP_CMD_INIT_WINDOW");


            if (engine->AndroidApp()->window != NULL) {
                engine->SaveNativeWinRes(ANativeWindow_getWidth(app->window),
                                         ANativeWindow_getHeight(app->window),
                                         ANativeWindow_getFormat(app->window));
                engine->OnAppInitWindow();

            }
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

/**
 * Called when the NativeActivity is created
 * */
extern "C" void android_main(struct android_app* state) {
    LOGI("NativeActivity android_main()");
    CameraEngine engine(state); //uses the explicit constructor to initialize the state and creates a variable called engine
   pEngineObj = &engine; //pEngineObj is defined as the global object
//
//
    state->userData = reinterpret_cast<void*>(&engine);
    state->onAppCmd = ProcessAndroidCmd;
//////
//////    // loop waiting for stuff to do.
////WITHOUT THE WHILE LOOP BELOW THE APP WILL FREEZE AND CRASH
    while (!state->destroyRequested) {
        struct android_poll_source* source = nullptr;

        // both ALooper_pollOnce and source->process(state, source) need to be called or it will crash the app
        ALooper_pollOnce(0, NULL, nullptr, (void**)&source);
        if (source != NULL) {
            source->process(state, source);
        }

        //todo: this section is what is causing the crash error
       // pEngineObj->DrawFrame();


    }

    LOGI("CameraEngine thread destroy requested!");
//    engine.DeleteCamera();
//    pEngineObj = nullptr;
}
/*****************----------------FUNCTIONS THAT MUST BE CALLED-------------********************/
/**
 * Process user camera and disk writing permission
 * Resume application initialization after user granted camera and disk usage
 * If user denied permission, do nothing: no camera
 *
 * @param granted user's authorization for camera and disk usage.
 * @return none
 */
void CameraEngine::OnCameraPermission(jboolean granted) {
    cameraGranted_ = (granted != JNI_FALSE);
    LOGI("camera status %d",cameraGranted_);

    OnAppInitWindow();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_cameraNDK_CameraNDKNativeActivity_notifyCameraPermission(JNIEnv *env,jobject thiz,jboolean granted) {
    std::thread permissionHandler(&CameraEngine::OnCameraPermission,
                                  GetAppEngine(), granted);
    permissionHandler.detach();

    LOGI("PERMISSION GRANTED");
}







void LogMediaStatus(media_status_t status) {
    switch (status) {
        case AMEDIA_OK:
            LOGI("Media operation succeeded.");
            break;
        case AMEDIACODEC_ERROR_INSUFFICIENT_RESOURCE:
            LOGE("AMEDIACODEC_ERROR_INSUFFICIENT_RESOURCE: Unable to allocate required resources.");
            break;
        case AMEDIACODEC_ERROR_RECLAIMED:
            LOGE("AMEDIACODEC_ERROR_RECLAIMED: Codec resource was reclaimed. Release codec.");
            break;
        case AMEDIA_DRM_DEVICE_REVOKED:
            LOGE("AMEDIA_DRM_DEVICE_REVOKED: DRM device revoked.");
            break;
        case AMEDIA_DRM_ERROR_BASE:
            LOGE("AMEDIA_DRM_ERROR_BASE: General DRM error.");
            break;
        case AMEDIA_DRM_LICENSE_EXPIRED:
            LOGE("AMEDIA_DRM_LICENSE_EXPIRED: DRM license has expired.");
            break;
        case AMEDIA_DRM_NEED_KEY:
            LOGE("AMEDIA_DRM_NEED_KEY: DRM key is required for operation.");
            break;
        case AMEDIA_DRM_NOT_PROVISIONED:
            LOGE("AMEDIA_DRM_NOT_PROVISIONED: DRM not provisioned.");
            break;
        case AMEDIA_DRM_RESOURCE_BUSY:
            LOGE("AMEDIA_DRM_RESOURCE_BUSY: DRM resource is busy.");
            break;
        case AMEDIA_DRM_SESSION_NOT_OPENED:
            LOGE("AMEDIA_DRM_SESSION_NOT_OPENED: DRM session not opened.");
            break;
        case AMEDIA_DRM_SHORT_BUFFER:
            LOGE("AMEDIA_DRM_SHORT_BUFFER: DRM buffer too short.");
            break;
        case AMEDIA_DRM_TAMPER_DETECTED:
            LOGE("AMEDIA_DRM_TAMPER_DETECTED: DRM tampering detected.");
            break;
        case AMEDIA_DRM_VERIFY_FAILED:
            LOGE("AMEDIA_DRM_VERIFY_FAILED: DRM verification failed.");
            break;
        case AMEDIA_ERROR_BASE:
            LOGE("AMEDIA_ERROR_BASE: General media error.");
            break;
        case AMEDIA_ERROR_END_OF_STREAM:
            LOGI("AMEDIA_ERROR_END_OF_STREAM: End of media stream.");
            break;
        case AMEDIA_ERROR_INVALID_OBJECT:
            LOGE("AMEDIA_ERROR_INVALID_OBJECT: Invalid or closed object used.");
            break;
        case AMEDIA_ERROR_INVALID_OPERATION:
            LOGE("AMEDIA_ERROR_INVALID_OPERATION: Invalid operation for current media state.");
            break;
        case AMEDIA_ERROR_INVALID_PARAMETER:
            LOGE("AMEDIA_ERROR_INVALID_PARAMETER: Invalid parameter used.");
            break;
        case AMEDIA_ERROR_IO:
            LOGE("AMEDIA_ERROR_IO: IO error during media operation.");
            break;
        case AMEDIA_ERROR_MALFORMED:
            LOGE("AMEDIA_ERROR_MALFORMED: Malformed or corrupt media data.");
            break;
        case AMEDIA_ERROR_UNSUPPORTED:
            LOGE("AMEDIA_ERROR_UNSUPPORTED: Unsupported media format or operation.");
            break;
        case AMEDIA_ERROR_WOULD_BLOCK:
            LOGE("AMEDIA_ERROR_WOULD_BLOCK: Operation would block, but blocking is disabled.");
            break;
        case AMEDIA_IMGREADER_CANNOT_LOCK_IMAGE:
            LOGE("AMEDIA_IMGREADER_CANNOT_LOCK_IMAGE: Image buffer lock failed.");
            break;
        case AMEDIA_IMGREADER_CANNOT_UNLOCK_IMAGE:
            LOGE("AMEDIA_IMGREADER_CANNOT_UNLOCK_IMAGE: Failed to unlock image buffer.");
            break;
        case AMEDIA_IMGREADER_ERROR_BASE:
            LOGE("AMEDIA_IMGREADER_ERROR_BASE: General ImageReader error.");
            break;
        case AMEDIA_IMGREADER_IMAGE_NOT_LOCKED:
            LOGE("AMEDIA_IMGREADER_IMAGE_NOT_LOCKED: Image not locked for required operation.");
            break;
        case AMEDIA_IMGREADER_MAX_IMAGES_ACQUIRED: //TODO: THIS IS THE ONE THAT IS ALSO CRASHING
            LOGE("AMEDIA_IMGREADER_MAX_IMAGES_ACQUIRED: Maximum images acquired, release one first.");
            break;
        case AMEDIA_IMGREADER_NO_BUFFER_AVAILABLE: //TODO: THIS IS THE ONE THAT IS ALSO CRASHING
            LOGE("AMEDIA_IMGREADER_NO_BUFFER_AVAILABLE: No available image buffers.");
            break;
        default:
            LOGE("Unknown media status: %d", status);
            break;
    }
}