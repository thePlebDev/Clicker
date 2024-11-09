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
 * EnumerateCamera()
 *     Loop through cameras on the system, pick up
 *     1) back facing one if available
 *     2) otherwise pick the first one reported to us
 */
void NDKCamera::EnumerateCamera() {
    ACameraIdList* cameraIds = nullptr;
    ACameraManager_getCameraIdList(cameraMgr_, &cameraIds); //retrieves and stores cameraIds
//
    for (int i = 0; i < cameraIds->numCameras; ++i) {
        const char* id = cameraIds->cameraIds[i];
        LOGI("CAMERA ID CHECK-----> %8s", id);


        //retrieves and stores metadata
        ACameraMetadata* metadataObj;
        ACameraManager_getCameraCharacteristics(cameraMgr_, id, &metadataObj);


        int32_t count = 0;
        const uint32_t* tags = nullptr;
        //List all the entry tags in input ACameraMetadata and stores in tags.
        ACameraMetadata_getAllTags(metadataObj, &count, &tags);
        for (int tagIdx = 0; tagIdx < count; ++tagIdx) {
            if (ACAMERA_LENS_FACING == tags[tagIdx]) {
                ACameraMetadata_const_entry lensInfo = {
                        0,
                };
                //Get a metadata entry from an input ACameraMetadata.
                ACameraMetadata_getConstEntry(metadataObj, tags[tagIdx], &lensInfo);
                //Storing camera information:
                CameraId cam(id);
                cam.facing_ = static_cast<acamera_metadata_enum_android_lens_facing_t>(
                        lensInfo.data.u8[0]);

                cam.owner_ = false;
                cam.device_ = nullptr;
                cameras_[cam.id_] = cam;
                if (cam.facing_ == ACAMERA_LENS_FACING_BACK) {
                    activeCameraId_ = cam.id_;
                }
                break;
            }
        }
        ACameraMetadata_free(metadataObj);
    }

//    ASSERT(cameras_.size(), "No Camera Available on the device");
    if (activeCameraId_.length() == 0) {
        // if no back facing camera found, pick up the first one to use...
        activeCameraId_ = cameras_.begin()->second.id_;
    }
    ACameraManager_deleteCameraIdList(cameraIds);
}

/**
 * ---------------------------------------- START OF LISTENERS ----------------------------------------
 * */
/*
 * CameraDevice callbacks
 */
void OnDeviceStateChanges(void* ctx, ACameraDevice* dev) {
    reinterpret_cast<NDKCamera*>(ctx)->OnDeviceState(dev);
}

void OnDeviceErrorChanges(void* ctx, ACameraDevice* dev, int err) {
    reinterpret_cast<NDKCamera*>(ctx)->OnDeviceError(dev, err);
}
/**
 * OnCameraStatusChanged()
 *  handles Callback from ACameraManager
 */
void NDKCamera::OnCameraStatusChanged(const char* id, bool available) {
    if (valid_) {
        cameras_[std::string(id)].available_ = available ? true : false;
    }
}

/*
 * Camera Manager Listener object
 */
void OnCameraAvailable(void* ctx, const char* id) {
    reinterpret_cast<NDKCamera*>(ctx)->OnCameraStatusChanged(id, true);
}
void OnCameraUnavailable(void* ctx, const char* id) {
    reinterpret_cast<NDKCamera*>(ctx)->OnCameraStatusChanged(id, false);
}

/**
 * ---------------------------------------- END OF LISTENERS ----------------------------------------
 * */

ACameraDevice_stateCallbacks* NDKCamera::GetDeviceListener() {
    static ACameraDevice_stateCallbacks cameraDeviceListener = {
            .context = this,
            .onDisconnected = ::OnDeviceStateChanges,
            .onError = ::OnDeviceErrorChanges,
    };
    return &cameraDeviceListener;
}

/**
 * Handle Camera DeviceStateChanges msg, notify device is disconnected
 * simply close the camera
 */
void NDKCamera::OnDeviceState(ACameraDevice* dev) {
    std::string id(ACameraDevice_getId(dev));
    LOGI("device %s is disconnected", id.c_str());

    cameras_[id].available_ = false;
    ACameraDevice_close(cameras_[id].device_);
    cameras_.erase(id);
}
/**
 * Handles Camera's deviceErrorChanges message, no action;
 * mainly debugging purpose
 *
 *
 */
void NDKCamera::OnDeviceError(ACameraDevice* dev, int err) {
    std::string id(ACameraDevice_getId(dev));

    LOGI("CameraDevice %s is in error %#x", id.c_str(), err);
   // PrintCameraDeviceError(err);

    CameraId& cam = cameras_[id];

    switch (err) {
        case ERROR_CAMERA_IN_USE:
            cam.available_ = false;
            cam.owner_ = false;
            break;
        case ERROR_CAMERA_SERVICE:
        case ERROR_CAMERA_DEVICE:
        case ERROR_CAMERA_DISABLED:
        case ERROR_MAX_CAMERAS_IN_USE:
            cam.available_ = false;
            cam.owner_ = false;
            break;
        default:
            LOGI("Unknown Camera Device Error: %#x", err);
    }
}
/**
 * Construct a camera manager listener on the fly and return to caller
 *
 * @return ACameraManager_AvailabilityCallback
 */
ACameraManager_AvailabilityCallbacks* NDKCamera::GetManagerListener() {
    static ACameraManager_AvailabilityCallbacks cameraMgrListener = {
            .context = this,
            .onCameraAvailable = ::OnCameraAvailable,
            .onCameraUnavailable = ::OnCameraUnavailable,
    };
    return &cameraMgrListener;
}


NDKCamera::NDKCamera()
        :cameraMgr_(nullptr),
         activeCameraId_(""),
         cameraFacing_(ACAMERA_LENS_FACING_BACK),
         cameraOrientation_(0){



    cameraMgr_ = ACameraManager_create();

    // Pick up a back-facing camera to preview
    EnumerateCamera();

    // Create back facing camera device
   ACameraManager_openCamera(cameraMgr_, activeCameraId_.c_str(), GetDeviceListener(),
                        &cameras_[activeCameraId_].device_);

   //Register camera availability callbacks.
    ACameraManager_registerAvailabilityCallback(cameraMgr_, GetManagerListener());

    // Initialize camera controls(exposure time and sensitivity), pick
    // up value of 2% * range + min as starting value (just a number, no magic)
    //todo: I DO NOT NEED THE CONTROLS RIGHT NOW
    //todo: BUT I WILL MAKE IT UP AGAIN LATER
//    ACameraMetadata* metadataObj;
//    CALL_MGR(getCameraCharacteristics(cameraMgr_, activeCameraId_.c_str(),
//                                      &metadataObj));
//    ACameraMetadata_const_entry val = {
//            0,
//    };
//    camera_status_t status = ACameraMetadata_getConstEntry(
//            metadataObj, ACAMERA_SENSOR_INFO_EXPOSURE_TIME_RANGE, &val);
//    if (status == ACAMERA_OK) {
//        exposureRange_.min_ = val.data.i64[0];
//        if (exposureRange_.min_ < kMinExposureTime) {
//            exposureRange_.min_ = kMinExposureTime;
//        }
//        exposureRange_.max_ = val.data.i64[1];
//        if (exposureRange_.max_ > kMaxExposureTime) {
//            exposureRange_.max_ = kMaxExposureTime;
//        }
//        exposureTime_ = exposureRange_.value(2);
//    } else {
//        LOGW("Unsupported ACAMERA_SENSOR_INFO_EXPOSURE_TIME_RANGE");
//        exposureRange_.min_ = exposureRange_.max_ = 0l;
//        exposureTime_ = 0l;
//    }
//    status = ACameraMetadata_getConstEntry(
//            metadataObj, ACAMERA_SENSOR_INFO_SENSITIVITY_RANGE, &val);
//
//    if (status == ACAMERA_OK) {
//        sensitivityRange_.min_ = val.data.i32[0];
//        sensitivityRange_.max_ = val.data.i32[1];
//
//        sensitivity_ = sensitivityRange_.value(2);
//    } else {
//        LOGW("failed for ACAMERA_SENSOR_INFO_SENSITIVITY_RANGE");
//        sensitivityRange_.min_ = sensitivityRange_.max_ = 0;
//        sensitivity_ = 0;
//    }
//    valid_ = true;
//}

}

NDKCamera::~NDKCamera() {
    //cameras_.clear();
    if (cameraMgr_) {
        //CALL_MGR(unregisterAvailabilityCallback(cameraMgr_, GetManagerListener()));
        ACameraManager_delete(cameraMgr_);
        cameraMgr_ = nullptr;
    }
}
/**
 * GetSensorOrientation()
 *     Retrieve current sensor orientation regarding to the phone device
 * orientation
 *     SensorOrientation is NOT settable.
 */
bool NDKCamera::GetSensorOrientation(int32_t* facing, int32_t* angle) {
    if (!cameraMgr_) {
        return false;
    }

//    ACameraMetadata* metadataObj;
//    ACameraMetadata_const_entry face, orientation;
//    CALL_MGR(getCameraCharacteristics(cameraMgr_, activeCameraId_.c_str(),
//                                      &metadataObj));
//
//    cameraFacing_ = static_cast<int32_t>(face.data.u8[0]);
//
//
//
//    LOGI("Current SENSOR_ORIENTATION-----> %8d", orientation.data.i32[0]);
//
//
//    ACameraMetadata_free(metadataObj);
//    cameraOrientation_ = orientation.data.i32[0];
////
//    if (facing) *facing = cameraFacing_;
//    if (angle) *angle = cameraOrientation_;
    return true;
}


/**
 *
 * end of CAMERA_MANAGER
 * */

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

    CreateCamera();
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


    JNIEnv *env; // access to the JNI functions
    ANativeActivity *activity = app_->activity; //access to the ANativeActivity
    activity->vm->GetEnv((void **)&env, JNI_VERSION_1_6); //get hold of the JVM environment context and set env

    //attaches the current thread to the JVM, setting up env as the JNI environment
    // only threads attached to the JVM can make JNI calls
    activity->vm->AttachCurrentThread(&env, NULL);

    jobject activityObj = env->NewGlobalRef(activity->clazz); //getting a reference to the Activity
    jclass clz = env->GetObjectClass(activityObj); //retrieves the actual class
    //calls the actual method and stores it newOrientation
    jint newOrientation = env->CallIntMethod(
            activityObj, env->GetMethodID(clz, "getRotationDegree", "()I"));

    //clean up to avoid memory leaks
    env->DeleteGlobalRef(activityObj);
    activity->vm->DetachCurrentThread();

    return newOrientation;
}


/**
 * Create a camera object for onboard BACK_FACING camera
 */
void CameraEngine::CreateCamera(void) {
    ACameraMetadata* metadataObj;
    // Camera needed to be requested at the run-time from Java SDK
    // if Not granted, do nothing.
    //this can be implemented later
//    if (!cameraGranted_ || !app_->window) {
//        LOGW("Camera Sample requires Full Camera access");
//        return;
//    }

    int32_t displayRotation = GetDisplayRotation();
    rotation_ = displayRotation;

    camera_ = new NDKCamera(); //so we are working on this section right now

//
    int32_t facing = 0, angle = 0, imageRotation = 0;

    if (camera_->GetSensorOrientation(&facing, &angle)) {
//        if (facing == ACAMERA_LENS_FACING_FRONT) {
//            imageRotation = (angle + rotation_) % 360;
//            imageRotation = (360 - imageRotation) % 360;
//        } else {
//            imageRotation = (angle - rotation_ + 360) % 360;
//        }
    }
//    LOGI("Phone Rotation: %d, Present Rotation Angle: %d", rotation_,
//         imageRotation);
//    ImageFormat view{0, 0, 0}, capture{0, 0, 0};
//    camera_->MatchCaptureSizeRequest(app_->window, &view, &capture);
//
//    ASSERT(view.width && view.height, "Could not find supportable resolution");
//
//    // Request the necessary nativeWindow to OS
//    bool portraitNativeWindow =
//            (savedNativeWinRes_.width < savedNativeWinRes_.height);
//    ANativeWindow_setBuffersGeometry(
//            app_->window, portraitNativeWindow ? view.height : view.width,
//            portraitNativeWindow ? view.width : view.height, WINDOW_FORMAT_RGBA_8888);
//
//    yuvReader_ = new ImageReader(&view, AIMAGE_FORMAT_YUV_420_888);
//    yuvReader_->SetPresentRotation(imageRotation);
//    jpgReader_ = new ImageReader(&capture, AIMAGE_FORMAT_JPEG);
//    jpgReader_->SetPresentRotation(imageRotation);
//    jpgReader_->RegisterCallback(
//            this, [this](void* ctx, const char* str) -> void {
//                reinterpret_cast<CameraEngine*>(ctx)->OnPhotoTaken(str);
//            });
//
//    // now we could create session
//    camera_->CreateSession(yuvReader_->GetNativeWindow(),
//                           jpgReader_->GetNativeWindow(), imageRotation);
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