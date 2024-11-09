//
// Created by Tristan on 2024-11-08.
//

#include "camera_manager.h"
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <camera/NdkCameraManager.h>
#include <camera/NdkCameraError.h>
#include <camera/NdkCameraMetadataTags.h>
#include <camera/NdkCameraMetadata.h>
#include <android/native_activity.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <media/NdkImageReader.h>


#include <functional>
#include <thread>
#include <android/log.h>

#define LOG_TAG "streamLogging"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)





//NDKCamera::NDKCamera()
//    :cameraMgr_(nullptr),
//     activeCameraId_(""){
//
//    cameras_.clear();
//    cameraMgr_ = ACameraManager_create();
//
//}
//
//NDKCamera::~NDKCamera() {
//    cameras_.clear();
//    if (cameraMgr_) {
//        //CALL_MGR(unregisterAvailabilityCallback(cameraMgr_, GetManagerListener()));
//        ACameraManager_delete(cameraMgr_);
//        cameraMgr_ = nullptr;
//    }
//}

/**
 * GetSensorOrientation()
 *     Retrieve current sensor orientation regarding to the phone device
 * orientation
 *     SensorOrientation is NOT settable.
 */
//bool NDKCamera::GetSensorOrientation(int32_t* facing, int32_t* angle) {
//    if (!cameraMgr_) {
//        return false;
//    }
//
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
////
////    ACameraMetadata_free(metadataObj);
////    cameraOrientation_ = orientation.data.i32[0];
////
////    if (facing) *facing = cameraFacing_;
////    if (angle) *angle = cameraOrientation_;
//    return true;
//}




