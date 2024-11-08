//
// Created by 14035 on 2024-11-08.
//

#ifndef CLICKER_CAMERA_ENGINE_H
#define CLICKER_CAMERA_ENGINE_H

#include <android/native_window.h>
#include <android_native_app_glue.h>

#include <functional>
#include <thread>


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

    // Manage NDKCamera Object
    void CreateCamera(void);

private:
    int GetDisplayRotation(void);
    int rotation_;
    NDKCamera* camera_;
};

#endif //CLICKER_CAMERA_ENGINE_H


