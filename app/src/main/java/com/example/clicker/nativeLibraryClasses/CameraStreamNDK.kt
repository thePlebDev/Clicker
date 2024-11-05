package com.example.clicker.nativeLibraryClasses

class CameraStreamNDK {

    init{
        System.loadLibrary("camera_stream");
    }


    external fun notifyCameraPermission(granted: Boolean)

    //THIS IS WHAT SHOULD GET CALLED WHEN THE USER WANTS TO CREATE A PICTURE
    external fun TakePhoto()
}