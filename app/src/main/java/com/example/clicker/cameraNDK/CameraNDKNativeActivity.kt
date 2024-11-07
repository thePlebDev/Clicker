package com.example.clicker.cameraNDK


import android.Manifest
import android.app.NativeActivity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Request camera permission
            Log.d("PermissionCheckingNative","not GRANTED")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE_CAMERA)
        } else {
            // Permission already granted

            notifyCameraPermission(true)
        }
    }


    external fun notifyCameraPermission(granted: Boolean)

    init{
        System.loadLibrary("camera_stream");
    }


}
