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

/**
 * StartPreview()
 *   Toggle preview start/stop
 */
void NDKCamera::StartPreview(bool start) {
    if (start) {
        CALL_SESSION(setRepeatingRequest(captureSession_, nullptr, 1,
                                         &requests_[PREVIEW_REQUEST_IDX].request_,
                                         nullptr));
    } else if (!start && captureSessionState_ == CaptureSessionState::ACTIVE) {
        ACameraCaptureSession_stopRepeating(captureSession_);
    } else {
//        ASSERT(false, "Conflict states(%s, %d)", (start ? "true" : "false"),
//               static_cast<int>(captureSessionState_));
    }
}

NDKCamera::NDKCamera()
        :cameraMgr_(nullptr),
         activeCameraId_(""),
         cameraFacing_(ACAMERA_LENS_FACING_BACK),
         captureSessionState_(CaptureSessionState::MAX_STATE),
         cameraOrientation_(0){


    valid_ = false;
    requests_.resize(CAPTURE_REQUEST_COUNT); //resize the vector to 2
    memset(requests_.data(), 0, requests_.size() * sizeof(requests_[0]));
    cameras_.clear();
    cameraMgr_ = ACameraManager_create();


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













    //TODO: I THINK i CAN IGNORE THE REQUESTS
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
bool NDKCamera::GetSensorOrientation(int32_t* facing, int32_t* angle) { //working on this right now
    if (!cameraMgr_) {
        return false;
    }

    ACameraMetadata* metadataObj;
    ACameraMetadata_const_entry face, orientation;

    //Query the capabilities of a camera device.
    CALL_MGR(getCameraCharacteristics(cameraMgr_, activeCameraId_.c_str(),
                                      &metadataObj));
    CALL_METADATA(
            getConstEntry(metadataObj, ACAMERA_SENSOR_ORIENTATION, &orientation)
            );

    cameraFacing_ = static_cast<int32_t>(face.data.u8[0]);
//
//
//
    LOGI("Current SENSOR_ORIENTATION-----> %8d", orientation.data.i32[0]);
    LOGI("Current SENSOR_ORIENTATION cameraFacing_-----> %8d", cameraFacing_);
//

    ACameraMetadata_free(metadataObj); // to free the memory of the output characteristics.
    cameraOrientation_ = orientation.data.i32[0];
////
    if (facing) *facing = cameraFacing_;
    if (angle) *angle = cameraOrientation_;
    return true;
}


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
    //TODO: REIMPLEMENT THIS CONDITIONAL AFTER THE CAMERA IS ACTUALLY WORKING
//    if (!cameraGranted_) {
//        // Not permitted to use camera yet, ask(again) and defer other events
//        RequestCameraPermission();
//        return;
//    }

    rotation_ = GetDisplayRotation(); //TODO: MAKING SURE THAT THIS WORKS
    LOGI("Present Rotation Angle: %d", rotation_);

    CreateCamera(); // working on this section


    //This seems to deal with the sesitivity Ui that I do not need right now
   // EnableUI();

    // NativeActivity end is ready to display, start pulling images
    cameraReady_ = true;
    camera_->StartPreview(true);
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
 * A helper class to assist image size comparison, by comparing the absolute
 * size
 * regardless of the portrait or landscape mode.
 */
class DisplayDimension {
    public:
    DisplayDimension(int32_t w, int32_t h) : w_(w), h_(h), portrait_(false) {
        if (h > w) {
            // make it landscape
            w_ = h;
            h_ = w;
            portrait_ = true;
        }
    }
    void Flip(void) { portrait_ = !portrait_; }
    bool IsSameRatio(DisplayDimension& other) {
        return (w_ * other.h_ == h_ * other.w_);
    }
    bool operator>(DisplayDimension& other) {
        return (w_ >= other.w_ & h_ >= other.h_);
    }
    bool operator==(DisplayDimension& other) {
        return (w_ == other.w_ && h_ == other.h_ && portrait_ == other.portrait_);
    }
    int32_t org_width(void) { return (portrait_ ? h_ : w_); }
    int32_t org_height(void) { return (portrait_ ? w_ : h_); }
    bool IsPortrait(void) { return portrait_; }

private:
    int32_t w_, h_;
    bool portrait_;
};



/**
 * Find a compatible camera modes:
 *    1) the same aspect ration as the native display window, which should be a
 *       rotated version of the physical device
 *    2) the smallest resolution in the camera mode list
 * This is to minimize the later color space conversion workload.
 */
bool NDKCamera::MatchCaptureSizeRequest(ANativeWindow* display,
                                        ImageFormat* resView,
                                        ImageFormat* resCap) {
    DisplayDimension disp(ANativeWindow_getWidth(display),
                          ANativeWindow_getHeight(display));
    if (cameraOrientation_ == 90 || cameraOrientation_ == 270) {
        disp.Flip();
    }

    ACameraMetadata* metadata;
    CALL_MGR(
            getCameraCharacteristics(cameraMgr_, activeCameraId_.c_str(), &metadata));
    ACameraMetadata_const_entry entry;
    CALL_METADATA(getConstEntry(
            metadata, ACAMERA_SCALER_AVAILABLE_STREAM_CONFIGURATIONS, &entry));
    // format of the data: format, width, height, input?, type int32
    bool foundIt = false;
    DisplayDimension foundRes(4000, 4000);
    DisplayDimension maxJPG(0, 0);

    for (int i = 0; i < entry.count; i += 4) {
        int32_t input = entry.data.i32[i + 3];
        int32_t format = entry.data.i32[i + 0];
        if (input) continue;

        if (format == AIMAGE_FORMAT_YUV_420_888 || format == AIMAGE_FORMAT_JPEG) {
            DisplayDimension res(entry.data.i32[i + 1], entry.data.i32[i + 2]);
            if (!disp.IsSameRatio(res)) continue;
            if (format == AIMAGE_FORMAT_YUV_420_888 && foundRes > res) {
                foundIt = true;
                foundRes = res;
            } else if (format == AIMAGE_FORMAT_JPEG && res > maxJPG) {
                maxJPG = res;
            }
        }
    }

    if (foundIt) {
        resView->width = foundRes.org_width();
        resView->height = foundRes.org_height();
        resCap->width = maxJPG.org_width();
        resCap->height = maxJPG.org_height();
    } else {
        LOGI("Did not find any compatible camera resolution, taking 640x480");
        if (disp.IsPortrait()) {
            resView->width = 480;
            resView->height = 640;
        } else {
            resView->width = 640;
            resView->height = 480;
        }
        *resCap = *resView;
    }
    resView->format = AIMAGE_FORMAT_YUV_420_888;
    resCap->format = AIMAGE_FORMAT_JPEG;
    return foundIt;
}


/**
 * -----------------------------IMAGE READER---------------------------
 *
 * */
/**
* MAX_BUF_COUNT:
*   Max buffers in this ImageReader.
*/
#define MAX_BUF_COUNT 4

/*
 * For JPEG capture, captured files are saved under
 *     DirName
 * File names are incrementally appended an index number as
 *     capture0.jpg, capture1.jpg, capture2.jpg
 */
static const char *kDirName = "/sdcard/DCIM/Camera/";
static const char *kFileName = "capture";

/**
 * Write out jpeg files to kDirName directory
 * @param image point capture jpg image
 */
void ImageReader::WriteFile(AImage *image) {
    int planeCount;
    media_status_t status = AImage_getNumberOfPlanes(image, &planeCount);

    uint8_t *data = nullptr;
    int len = 0;
    AImage_getPlaneData(image, 0, &data, &len);

    DIR *dir = opendir(kDirName);
    if (dir) {
        closedir(dir);
    } else {
        std::string cmd = "mkdir -p ";
        cmd += kDirName;
        system(cmd.c_str());
    }

    struct timespec ts {
            0, 0
    };
    clock_gettime(CLOCK_REALTIME, &ts);
    struct tm localTime;
    localtime_r(&ts.tv_sec, &localTime);

    std::string fileName = kDirName;
    std::string dash("-");
    fileName += kFileName + std::to_string(localTime.tm_mon) +
                std::to_string(localTime.tm_mday) + dash +
                std::to_string(localTime.tm_hour) +
                std::to_string(localTime.tm_min) +
                std::to_string(localTime.tm_sec) + ".jpg";
    FILE *file = fopen(fileName.c_str(), "wb");
    if (file && data && len) {
        fwrite(data, 1, len, file);
        fclose(file);

        if (callback_) {
            callback_(callbackCtx_, fileName.c_str());
        }
    } else {
        if (file) fclose(file);
    }
    AImage_delete(image);
}


void ImageReader::ImageCallback(AImageReader *reader) {
    int32_t format;
    media_status_t status = AImageReader_getFormat(reader, &format);

    if (format == AIMAGE_FORMAT_JPEG) {
        AImage *image = nullptr;
        media_status_t status = AImageReader_acquireNextImage(reader, &image);


        // Create a thread and write out the jpeg files
        std::thread writeFileHandler(&ImageReader::WriteFile, this, image);
        writeFileHandler.detach();
    }
}
/**
 * ImageReader listener: called by AImageReader for every frame captured
 * We pass the event to ImageReader class, so it could do some housekeeping
 * about
 * the loaded queue. For example, we could keep a counter to track how many
 * buffers are full and idle in the queue. If camera almost has no buffer to
 * capture
 * we could release ( skip ) some frames by AImageReader_getNextImage() and
 * AImageReader_delete().
 */
void OnImageCallback(void *ctx, AImageReader *reader) {
    reinterpret_cast<ImageReader *>(ctx)->ImageCallback(reader);
}
/**
 * Constructor
 */
ImageReader::ImageReader(ImageFormat *res, enum AIMAGE_FORMATS format)
        : presentRotation_(0), reader_(nullptr) {
    callback_ = nullptr;
    callbackCtx_ = nullptr;

    media_status_t status = AImageReader_new(res->width, res->height, format,
                                             MAX_BUF_COUNT, &reader_);
    bool a = reader_ && status == AMEDIA_OK;


    if(a){
        LOGI("Failed to create AImageReader");
    }else{
        LOGI("Failed to CREATED!!!!!!!");
    }


    AImageReader_ImageListener listener{
            .context = this,
            .onImageAvailable = OnImageCallback,
    };
    AImageReader_setImageListener(reader_, &listener);
}
/**
 * Handles capture session state changes.
 *   Update into internal session state.
 */
void NDKCamera::OnSessionState(ACameraCaptureSession* ses,
                               CaptureSessionState state) {
    if (!ses || ses != captureSession_) {
      //  LOGW("CaptureSession is %s", (ses ? "NOT our session" : "NULL"));
        return;
    }

//    ASSERT(state < CaptureSessionState::MAX_STATE, "Wrong state %d",
//           static_cast<int>(state));

    captureSessionState_ = state;
}
// CaptureSession state callbacks
void OnSessionClosed(void* ctx, ACameraCaptureSession* ses) {
    //LF
    reinterpret_cast<NDKCamera*>(ctx)->OnSessionState(
            ses, CaptureSessionState::CLOSED);
}
void OnSessionReady(void* ctx, ACameraCaptureSession* ses) {
    //LOGW("session %p ready", ses);
    reinterpret_cast<NDKCamera*>(ctx)->OnSessionState(ses,
                                                      CaptureSessionState::READY);
}
void OnSessionActive(void* ctx, ACameraCaptureSession* ses) {
    //LOGW("session %p active", ses);
    reinterpret_cast<NDKCamera*>(ctx)->OnSessionState(
            ses, CaptureSessionState::ACTIVE);
}
ACameraCaptureSession_stateCallbacks* NDKCamera::GetSessionListener() {
    static ACameraCaptureSession_stateCallbacks sessionListener = {
            .context = this,
            .onClosed = ::OnSessionClosed,
            .onReady = ::OnSessionReady,
            .onActive = ::OnSessionActive,
    };
    return &sessionListener;
}


void ImageReader::SetPresentRotation(int32_t angle) {
    presentRotation_ = angle;
}
void ImageReader::RegisterCallback(
        void *ctx, std::function<void(void *ctx, const char *fileName)> func) {
    callbackCtx_ = ctx;
    callback_ = func;
}

void CameraEngine::OnPhotoTaken(const char *fileName) {
    JNIEnv *jni;
    app_->activity->vm->AttachCurrentThread(&jni, NULL);

    // Default class retrieval
    jclass clazz = jni->GetObjectClass(app_->activity->clazz);
    jmethodID methodID =
            jni->GetMethodID(clazz, "OnPhotoTaken", "(Ljava/lang/String;)V");
    jstring javaName = jni->NewStringUTF(fileName);

    jni->CallVoidMethod(app_->activity->clazz, methodID, javaName);
    app_->activity->vm->DetachCurrentThread();
}
//TODO: FIX THE CRASHING ERROR
//TODO: READ ABOUT HOW TO READ C++ CRASH LOGS
//todo: So the error is SOMEWHERE IN HERE
void NDKCamera::CreateSession(ANativeWindow* previewWindow,
                              ANativeWindow* jpgWindow, int32_t imageRotation) {
    // Create output from this app's ANativeWindow, and add into output container
    //todo: IT LOOKS LIKE THIS ASSIGNMENT IS CAUSING THE CRASH
    requests_[PREVIEW_REQUEST_IDX].outputNativeWindow_ = previewWindow;
    requests_[PREVIEW_REQUEST_IDX].template_ = TEMPLATE_PREVIEW;
    requests_[JPG_CAPTURE_REQUEST_IDX].outputNativeWindow_ = jpgWindow;
    requests_[JPG_CAPTURE_REQUEST_IDX].template_ = TEMPLATE_STILL_CAPTURE;
//
    CALL_CONTAINER(create(&outputContainer_));
    for (auto& req : requests_) {
        ANativeWindow_acquire(req.outputNativeWindow_);
        CALL_OUTPUT(create(req.outputNativeWindow_, &req.sessionOutput_));
        CALL_CONTAINER(add(outputContainer_, req.sessionOutput_));
        CALL_TARGET(create(req.outputNativeWindow_, &req.target_));
        CALL_DEV(createCaptureRequest(cameras_[activeCameraId_].device_,
                                      req.template_, &req.request_));
        CALL_REQUEST(addTarget(req.request_, req.target_));
    }
//
//    // Create a capture session for the given preview request
    captureSessionState_ = CaptureSessionState::READY;
    CALL_DEV(createCaptureSession(cameras_[activeCameraId_].device_,
                                  outputContainer_, GetSessionListener(),
                                  &captureSession_));
//
    ACaptureRequest_setEntry_i32(requests_[JPG_CAPTURE_REQUEST_IDX].request_,
                                 ACAMERA_JPEG_ORIENTATION, 1, &imageRotation);
//
//    /*
//     * Only preview request is in manual mode, JPG is always in Auto mode
//     * JPG capture mode could also be switch into manual mode and control
//     * the capture parameters, this sample leaves JPG capture to be auto mode
//     * (auto control has better effect than author's manual control)
//     */
    uint8_t aeModeOff = ACAMERA_CONTROL_AE_MODE_OFF;
    CALL_REQUEST(setEntry_u8(requests_[PREVIEW_REQUEST_IDX].request_,
                             ACAMERA_CONTROL_AE_MODE, 1, &aeModeOff));
    //todo:DON'T THINK i NEED THESE RIGHT NOW
//    CALL_REQUEST(setEntry_i32(requests_[PREVIEW_REQUEST_IDX].request_,
//                              ACAMERA_SENSOR_SENSITIVITY, 1, &sensitivity_));
//    CALL_REQUEST(setEntry_i64(requests_[PREVIEW_REQUEST_IDX].request_,
//                              ACAMERA_SENSOR_EXPOSURE_TIME, 1, &exposureTime_));
}


ANativeWindow *ImageReader::GetNativeWindow(void) {
    if (!reader_) return nullptr;
    ANativeWindow *nativeWindow;
    media_status_t status = AImageReader_getWindow(reader_, &nativeWindow);
    //ASSERT(status == AMEDIA_OK, "Could not get ANativeWindow");
    if (status == AMEDIA_OK) {
        LOGI("Successfully obtained ANativeWindow");
    } else {
        LOGE("Could not get ANativeWindow: status = %d", status);
    }

    return nativeWindow;
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

    camera_ = new NDKCamera();
//
    int32_t facing = 0, angle = 0, imageRotation = 0;

    if (camera_->GetSensorOrientation(&facing, &angle)) {
        //todo: THIS IS WHAT I START NEXT
        if (facing == ACAMERA_LENS_FACING_FRONT) {
            imageRotation = (angle + rotation_) % 360;
            imageRotation = (360 - imageRotation) % 360;
        } else {
            imageRotation = (angle - rotation_ + 360) % 360;
        }
    }
    LOGI("Phone Rotation: %d, Present Rotation Angle: %d", rotation_,
         imageRotation);
    ImageFormat view{0, 0, 0}, capture{0, 0, 0};
    camera_->MatchCaptureSizeRequest(app_->window, &view, &capture);
//
//    ASSERT(view.width && view.height, "Could not find supportable resolution");
//
//    // Request the necessary nativeWindow to OS
    bool portraitNativeWindow =
            (savedNativeWinRes_.width < savedNativeWinRes_.height);
    ANativeWindow_setBuffersGeometry(
            app_->window, portraitNativeWindow ? view.height : view.width,
            portraitNativeWindow ? view.width : view.height, WINDOW_FORMAT_RGBA_8888);
//
    yuvReader_ = new ImageReader(&view, AIMAGE_FORMAT_YUV_420_888);
    //todo: this is what I am currently working on
    yuvReader_->SetPresentRotation(imageRotation);
    jpgReader_ = new ImageReader(&capture, AIMAGE_FORMAT_JPEG);
    jpgReader_->SetPresentRotation(imageRotation);
    jpgReader_->RegisterCallback(
            this, [this](void* ctx, const char* str) -> void {
                reinterpret_cast<CameraEngine*>(ctx)->OnPhotoTaken(str);
            });

    ANativeWindow* testingWindow = yuvReader_->GetNativeWindow();

    if(testingWindow == nullptr){
        LOGI("Testing the pointer ---> null pointer");
    }else{
        LOGI("Testing the pointer ---> not null");
    }


//
//    // now we could create session
    camera_->CreateSession(yuvReader_->GetNativeWindow(),
                           jpgReader_->GetNativeWindow(), imageRotation);

}


/*
 * SampleEngine global object
 */
static CameraEngine* pEngineObj = nullptr;
CameraEngine* GetAppEngine(void) {
//    ASSERT(pEngineObj, "AppEngine has not initialized");
    return pEngineObj;
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

void CameraEngine::DeleteCamera(void) {
    cameraReady_ = false;
    if (camera_) {
        delete camera_;
        camera_ = nullptr;
    }
//    if (yuvReader_) {
//        delete yuvReader_;
//        yuvReader_ = nullptr;
//    }
//    if (jpgReader_) {
//        delete jpgReader_;
//        jpgReader_ = nullptr;
//    }
}

/**
 * Called when the NativeActivity is created
 * */
extern "C" void android_main(struct android_app* state) {
    LOGI("NativeActivity android_main()");
    CameraEngine engine(state);
    pEngineObj = &engine; //pEngineObj is defined as the global object


    state->userData = reinterpret_cast<void*>(&engine);
    state->onAppCmd = ProcessAndroidCmd;
////
////    // loop waiting for stuff to do.
//WITHOUT THE WHILE LOOP BELOW THE APP WILL FREEZE AND CRASH
    while (!state->destroyRequested) {
        struct android_poll_source* source = nullptr;

        // both ALooper_pollOnce and source->process(state, source) need to be called or it will crash the app
        ALooper_pollOnce(0, NULL, nullptr, (void**)&source);
        if (source != NULL) {
            source->process(state, source);
        }
        pEngineObj->DrawFrame();


    }

    LOGI("CameraEngine thread destroy requested!");
    engine.DeleteCamera();
    pEngineObj = nullptr;
}
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