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

int GetDeviceRotation(ANativeActivity* activity) {
    JNIEnv* env;
    activity->vm->AttachCurrentThread(&env, NULL);

    jobject activityObj = env->NewGlobalRef(activity->clazz);
    jclass clz = env->GetObjectClass(activityObj);

    // Assuming you have a method getRotationDegree() in your Java code that returns the rotation as an int
    jmethodID methodID = env->GetMethodID(clz, "getRotationDegree", "()I");
    jint rotation = env->CallIntMethod(activityObj, methodID);

    env->DeleteGlobalRef(activityObj);
    activity->vm->DetachCurrentThread();

    return rotation;
}
ACameraDevice *cameraDevice = nullptr;
static void onDisconnected(void* context, ACameraDevice* device)
{
    // ...
}

static void onError(void* context, ACameraDevice* device, int error)
{
    // ...
}
/**********START OF THE NEW***********/
//AImageReader* reader_;



/**
 * Handle Android System APP_CMD_INIT_WINDOW message
 *   Request camera persmission from Java side
 *   Create camera object if camera has been granted
 */
int rotation_;
//int GetDisplayRotation() {
//    ASSERT(app_, "Application is not initialized");
//
//    JNIEnv *env;
//    ANativeActivity *activity = app_->activity;
//    activity->vm->GetEnv((void **)&env, JNI_VERSION_1_6);
//
//    activity->vm->AttachCurrentThread(&env, NULL);
//
//    jobject activityObj = env->NewGlobalRef(activity->clazz);
//    jclass clz = env->GetObjectClass(activityObj);
//    jint newOrientation = env->CallIntMethod(
//            activityObj, env->GetMethodID(clz, "getRotationDegree", "()I"));
//    env->DeleteGlobalRef(activityObj);
//
//    activity->vm->DetachCurrentThread();
//    return newOrientation;
//}
void OnAppInitWindow(void) {
//    if (!cameraGranted_) { //this should be used to handle when the permissions are not granted. However, we will deal with this later. only happy path right now
//        // Not permitted to use camera yet, ask(again) and defer other events
//        RequestCameraPermission();
//        return;
//    }
//todo: need to get the display rotation()
  //  rotation_ = GetDisplayRotation();


}





/**********END OF THE NEW***********/

extern "C" void android_main(struct android_app* state) {
    LOGI ("THE FUNCTION IS ------> %s", "CALLED");
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_nativeLibraryClasses_CameraStreamNDK_notifyCameraPermission(JNIEnv *env,jobject thiz,jboolean granted) {
//    LOGI ("PERMISSION IS ------> %hhu", granted);
//    LOGI("PERMISSION IS ------> %hhu", granted);

    ACameraManager *camManager = ACameraManager_create();
    ACameraIdList *cameraIds = nullptr;

    // Get the list of camera IDs
    ACameraManager_getCameraIdList(camManager, &cameraIds);

    if (cameraIds->numCameras == 0) {
        LOGI("No cameras available");
        ACameraManager_delete(camManager);
        return;
    }

    for (int i = 0; i < cameraIds->numCameras; ++i) {
        const char *id = cameraIds->cameraIds[i];

        // Get camera characteristics
        ACameraMetadata *metadataObj;
        ACameraManager_getCameraCharacteristics(camManager, id, &metadataObj);

        // Work with metadata here
        // ...

        ACameraMetadata_free(metadataObj);

        // Define the camera device callbacks
        static ACameraDevice_StateCallbacks cameraDeviceCallbacks = {
                .context = nullptr,
                .onDisconnected = onDisconnected,
                .onError = onError,
        };

        // Open the camera using the camera ID
        ACameraManager_openCamera(camManager, id, &cameraDeviceCallbacks, &cameraDevice);
        if (cameraDevice != nullptr) {
            LOGI("Successfully opened camera: %s", id);
            break;
        } else {
            LOGI("Failed to open camera: %s", id);
        }
    }
    // Clean up


    ACameraManager_delete(camManager);
    ACameraManager_deleteCameraIdList(cameraIds);

}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_nativeLibraryClasses_CameraStreamNDK_TakePhoto(JNIEnv *env, jobject thiz) {
    //this is what should run when they try to take the actual photo
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_clicker_cameraNDK_CameraNDKNativeActivity_notifyCameraPermission(JNIEnv *env,
                                                                                  jobject thiz,
                                                                                  jboolean granted) {
    // TODO: implement notifyCameraPermission()
}