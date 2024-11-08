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

struct ImageFormat {
    int32_t width;
    int32_t height;

    int32_t format;  // Through out this demo, the format is fixed to
    // YUV_420 format
};
/**
 * basic CameraAppEngine
 */
class CameraEngine {
    struct android_app* app_;
    ImageFormat savedNativeWinRes_;
public:
    explicit CameraEngine(android_app *app);// Declare the constructor

    ~CameraEngine(); // Declare the destructor


    // Interfaces to android application framework
    void DrawFrame(void);
    struct android_app* AndroidApp(void) const;
    void OnAppInitWindow(void);


    // Native Window handlers
    void SaveNativeWinRes(int32_t w, int32_t h, int32_t format);

private:
    int GetDisplayRotation(void);
    int rotation_;
};

CameraEngine::CameraEngine(android_app* app)
        :app_(app) //to avoid the automatic default constructor creation
           {

}

CameraEngine::~CameraEngine() {
    //cameraReady_ = false;
    //  DeleteCamera();

}

void CameraEngine::DrawFrame(void) {

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
    //this can be re-implemented later
//    if (!cameraGranted_) {
//        // Not permitted to use camera yet, ask(again) and defer other events
//        RequestCameraPermission();
//        return;
//    }

    rotation_ = GetDisplayRotation();
    LOGI("GettingDeviceRotation ---> %d",rotation_);

//    CreateCamera();
//    ASSERT(camera_, "CameraCreation Failed");
//
//    EnableUI();
//
//    // NativeActivity end is ready to display, start pulling images
//    cameraReady_ = true;
//    camera_->StartPreview(true);
}

/**
 * Retrieve current rotation from Java side
 *
 * @return current rotation angle
 */
int CameraEngine::GetDisplayRotation() {


    JNIEnv *env;
    ANativeActivity *activity = app_->activity;
    activity->vm->GetEnv((void **)&env, JNI_VERSION_1_6);

    activity->vm->AttachCurrentThread(&env, NULL);

    jobject activityObj = env->NewGlobalRef(activity->clazz);
    jclass clz = env->GetObjectClass(activityObj);
    jint newOrientation = env->CallIntMethod(
            activityObj, env->GetMethodID(clz, "getRotationDegree", "()I"));
    env->DeleteGlobalRef(activityObj);

    activity->vm->DetachCurrentThread();
    return newOrientation;
}


/*
 * SampleEngine global object
 */
static CameraEngine* pEngineObj = nullptr;








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
    CameraEngine engine(state);
    pEngineObj = &engine;


    state->userData = reinterpret_cast<void*>(&engine);
    state->onAppCmd = ProcessAndroidCmd;
//
//    // loop waiting for stuff to do.
    while (!state->destroyRequested) {
        struct android_poll_source* source = nullptr;

        ALooper_pollOnce(0, NULL, nullptr, (void**)&source);
        if (source != NULL) {
            source->process(state, source);
        }
        pEngineObj->DrawFrame();


    }

    LOGI("CameraEngine thread destroy requested!");
//    engine.DeleteCamera();
//    pEngineObj = nullptr;
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