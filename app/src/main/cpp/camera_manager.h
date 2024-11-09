//
// Created by 14035 on 2024-11-08.
//

#ifndef CLICKER_CAMERA_MANAGER_H
#define CLICKER_CAMERA_MANAGER_H

/*
 * A set of macros to call into Camera APIs. The API is grouped with a few
 * objects, with object name as the prefix of function names.
 */
#define CALL_CAMERA(func)                                             \
  {                                                                   \
    camera_status_t status = func;                                    \
  }
#define CALL_MGR(func) CALL_CAMERA(ACameraManager_##func)
#define CALL_METADATA(func) CALL_CAMERA(ACameraMetadata_##func)
#include <camera/NdkCameraDevice.h>
#include <camera/NdkCameraError.h>
#include <camera/NdkCameraManager.h>
#include <camera/NdkCameraMetadataTags.h>

#include <map>
#include <string>
#include <vector>
class CameraId; // Forward declaration, not a complete definition
class NDKCamera {

private:
    ACameraManager* cameraMgr_;
    std::map<std::string, CameraId> cameras_;
    std::string activeCameraId_;
    uint32_t cameraFacing_;
    uint32_t cameraOrientation_;
    ACameraDevice_stateCallbacks* GetDeviceListener();


public:
    NDKCamera();
    ~NDKCamera();
    void EnumerateCamera(void);
//
    bool GetSensorOrientation(int32_t* facing, int32_t* angle);

    void OnDeviceState(ACameraDevice* dev);
    void OnDeviceError(ACameraDevice* dev, int err);

};
// helper classes to hold enumerated camera
class CameraId {
public:
    ACameraDevice* device_;
    std::string id_;
    acamera_metadata_enum_android_lens_facing_t facing_;
    bool available_;  // free to use ( no other apps are using
    bool owner_;      // we are the owner of the camera
    explicit CameraId(const char* id)
            : device_(nullptr),
              facing_(ACAMERA_LENS_FACING_FRONT),
              available_(false),
              owner_(false) {
        id_ = id;
    }

    explicit CameraId(void) { CameraId(""); }
};

#endif //CLICKER_CAMERA_MANAGER_H

