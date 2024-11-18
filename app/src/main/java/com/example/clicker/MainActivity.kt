package com.example.clicker

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen



import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private lateinit var gLView: GLSurfaceView






    override fun onResume() {
        super.onResume()


    }


    @RequiresApi(34)
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        // Registers BroadcastReceiver to track network connection changes.
        System.setProperty("kotlinx.coroutines.debug", if (BuildConfig.DEBUG) "on" else "off")
        installSplashScreen()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 14+

                requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS,Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK),
                    1001 // Request code
                )

        }


        supportActionBar!!.hide()
//        val testing = NativeLoading()
//        testing.init()

        val bitmap = Bitmap.createBitmap(24, 24, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.BLACK)
        val bitmapDrawable = BitmapDrawable(resources, bitmap)
        window.setBackgroundDrawable(bitmapDrawable)
       setContentView(R.layout.activity_main) //this is the normal one
//        gLView = MyGLSurfaceView(this) // Green box
//        setContentView(gLView)
//        val mView =  GL2JNIView(application); // flashing triangle
//        setContentView(mView);

        //reportFullyDrawn() this was for testing



    }



    override fun onDestroy() {
        super.onDestroy()
    }

}
