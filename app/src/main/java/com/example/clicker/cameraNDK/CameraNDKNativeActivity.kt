package com.example.clicker.cameraNDK


import android.Manifest
import android.app.NativeActivity
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService


class CameraNDKNativeActivity : NativeActivity() {
    private val PERMISSION_REQUEST_CODE_CAMERA = 1





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //this.setContentView(R.layout.camera_ndk_native_activity)

        val mainLayout = RelativeLayout(this)

        this.setContentView(mainLayout)
       checkCameraPermission()
    }

    private fun checkCameraPermission() {
        Log.d("PermissionCheckingNative","CHECKING")
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Request camera permission
            Log.d("PermissionCheckingNative","not GRANTED")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE_CAMERA)
        } else {
            // Permission already granted

            Log.d("PermissionCheckingNative","ALREADY GRANTED")
           // notifyCameraPermission(true)
        }
    }

    // get current rotation method
    fun getRotationDegree(): Int {
        val defaultDisplay = getSystemService<DisplayManager>()?.getDisplay(Display.DEFAULT_DISPLAY)?.rotation?:0
        return 90 * defaultDisplay
    }


    external fun notifyCameraPermission(granted: Boolean)

    init{
        System.loadLibrary("camera_stream");
    }


}
