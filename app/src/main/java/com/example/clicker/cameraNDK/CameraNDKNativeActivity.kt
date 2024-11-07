package com.example.clicker.cameraNDK


import android.app.NativeActivity
import android.os.Bundle
import android.view.ViewGroup.MarginLayoutParams
import android.widget.RelativeLayout
import com.example.clicker.R


class CameraNDKNativeActivity : NativeActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       // this.setContentView(R.layout.camera_ndk_native_activity)

        val mainLayout = RelativeLayout(this)

        this.setContentView(mainLayout)



    }

    external fun notifyCameraPermission(granted: Boolean)

    init{
        System.loadLibrary("camera_stream");
    }






}
