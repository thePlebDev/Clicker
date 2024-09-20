package com.example.clicker

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.clicker.farmingGame.MyGLSurfaceView
import com.example.clicker.nativeLibraryClasses.NativeLoading

import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private lateinit var gLView: GLSurfaceView






    override fun onResume() {
        super.onResume()


    }


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        // Registers BroadcastReceiver to track network connection changes.
        System.setProperty("kotlinx.coroutines.debug", if (BuildConfig.DEBUG) "on" else "off")
        installSplashScreen()

        supportActionBar!!.hide()
        val testing = NativeLoading()
        testing.init()

        val bitmap = Bitmap.createBitmap(24, 24, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.BLACK)
        val bitmapDrawable = BitmapDrawable(resources, bitmap)
        window.setBackgroundDrawable(bitmapDrawable)
        //setContentView(R.layout.activity_main) todo: this is the main feature function
        gLView = MyGLSurfaceView(this)
        setContentView(gLView)

        //reportFullyDrawn()



    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
