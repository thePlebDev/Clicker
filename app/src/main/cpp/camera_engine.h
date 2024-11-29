//
// Created by 14035 on 2024-11-08.
//

#ifndef CLICKER_CAMERA_ENGINE_H
#define CLICKER_CAMERA_ENGINE_H

#include <android/native_window.h>
#include <android_native_app_glue.h>


#include <functional>
#include <thread>


#define CALL_CONTAINER(func) CALL_CAMERA(ACaptureSessionOutputContainer_##func)
#define CALL_OUTPUT(func) CALL_CAMERA(ACaptureSessionOutput_##func)
#define CALL_TARGET(func) CALL_CAMERA(ACameraOutputTarget_##func)
#define CALL_DEV(func) CALL_CAMERA(ACameraDevice_##func)
#define CALL_REQUEST(func) CALL_CAMERA(ACaptureRequest_##func)
#define CALL_DEV(func) CALL_CAMERA(ACameraDevice_##func)
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

enum class CaptureSessionState : int32_t {
    READY = 0,  // session is ready
    ACTIVE,     // session is busy
    CLOSED,     // session is closed(by itself or a new session evicts)
    MAX_STATE
};
//these values automatically increment since we set PREVIEW_REQUEST_IDX = 0
enum PREVIEW_INDICES {
    PREVIEW_REQUEST_IDX = 0,
    JPG_CAPTURE_REQUEST_IDX,
    CAPTURE_REQUEST_COUNT,
};

struct CaptureRequestInfo {
    ANativeWindow* outputNativeWindow_;
    ACaptureSessionOutput* sessionOutput_;
    ACameraOutputTarget* target_;
    ACaptureRequest* request_;
    ACameraDevice_request_template template_;
    int sessionSequenceId_;
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
    std::vector<CaptureRequestInfo> requests_;
    ACaptureSessionOutputContainer* outputContainer_;
    CaptureSessionState captureSessionState_;
    ACameraCaptureSession* captureSession_;
    ACameraCaptureSession_stateCallbacks* GetSessionListener();


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

    void CreateSession(ANativeWindow* previewWindow, ANativeWindow* jpgWindow,
                       int32_t imageRotation);
    void OnSessionState(ACameraCaptureSession* ses, CaptureSessionState state);

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

    /**
   * Report cached ANativeWindow, which was used to create camera's capture
   * session output.
   */
    ANativeWindow* GetNativeWindow(void);

    /**
   * Configure the rotation angle necessary to apply to
   * Camera image when presenting: all rotations should be accumulated:
   *    CameraSensorOrientation + Android Device Native Orientation +
   *    Human Rotation (rotated degree related to Phone native orientation
   */
    void SetPresentRotation(int32_t angle);

    /**
   * regsiter a callback function for client to be notified that jpeg already
   * written out.
   * @param ctx is client context when callback is invoked
   * @param callback is the actual callback function
   */
    void RegisterCallback(void* ctx,
                          std::function<void(void* ctx, const char* fileName)>);



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
    explicit CameraEngine(android_app *app);// Declare the constructor,make it explicit to avoid the implicit conversion

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
    ImageReader* jpgReader_;
    void OnPhotoTaken(const char* fileName);

};

#endif //CLICKER_CAMERA_ENGINE_H


