//
// Created by 14035 on 2024-11-08.
//

#ifndef CLICKER_CAMERA_ENGINE_H
#define CLICKER_CAMERA_ENGINE_H

#include <android/native_window.h>
#include <android_native_app_glue.h>


#include <functional>
#include <thread>


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

struct ImageFormat {
    int32_t width;
    int32_t height;

    int32_t format;  // Through out this demo, the format is fixed to
    // YUV_420 format
};

class CameraId; // Forward declaration, not a complete definition
class NDKCamera {

private:
    ACameraManager* cameraMgr_;
    std::map<std::string, CameraId> cameras_;
    std::string activeCameraId_;
    uint32_t cameraFacing_;
    uint32_t cameraOrientation_;
    ACameraDevice_stateCallbacks* GetDeviceListener();
    ACameraManager_AvailabilityCallbacks* GetManagerListener();
    volatile bool valid_;


public:
    NDKCamera();
    ~NDKCamera();
    void EnumerateCamera(void);
//
    bool GetSensorOrientation(int32_t* facing, int32_t* angle);

    void OnDeviceState(ACameraDevice* dev);
    void OnDeviceError(ACameraDevice* dev, int err);
    void OnCameraStatusChanged(const char* id, bool available);
    bool MatchCaptureSizeRequest(ANativeWindow* display, ImageFormat* view,
                                 ImageFormat* capture);

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


/**
 * --------------------------------------END OF THE MANAGER-------------------------------------------
 * */



class ImageReader {
public:
    /**
     * Ctor and Dtor()
     */
    explicit ImageReader(ImageFormat *res, enum AIMAGE_FORMATS format);

    ~ImageReader();

    /**
  * AImageReader callback handler. Called by AImageReader when a frame is
  * captured
  * (Internal function, not to be called by clients)
  */
    void ImageCallback(AImageReader* reader);

private:
    int32_t presentRotation_;
    AImageReader* reader_;
    std::function<void(void* ctx, const char* fileName)> callback_;
    void* callbackCtx_;


    void WriteFile(AImage* image);
};










/**
 * --------------------------------------END OF THE ImageReader -------------------------------------------
 * */

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

    // Manage NDKCamera Object
    void CreateCamera(void);

private:
    int GetDisplayRotation(void);
    int rotation_;
    NDKCamera* camera_;
    ImageReader* yuvReader_;
};

#endif //CLICKER_CAMERA_ENGINE_H

