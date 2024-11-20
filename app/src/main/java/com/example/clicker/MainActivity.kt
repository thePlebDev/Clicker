package com.example.clicker

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.activityViewModels
import com.example.clicker.presentation.home.HomeViewModel
import androidx.fragment.app.activityViewModels
import com.example.clicker.presentation.stream.StreamViewModel
import com.example.clicker.services.BackgroundStreamService


import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private lateinit var gLView: GLSurfaceView

    private val homeViewModel: HomeViewModel by viewModels()
    private val streamViewModel: StreamViewModel by viewModels()






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



        createNotificationChannel()

        val textMessage = intent?.action

        // Use the data
        textMessage?.let {
            Log.d("ExampleFragmentINtentGrab", "Received message: $it")
        } ?: run {
            Log.d("ExampleFragmentINtentGrab", "No message received")
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
//    /**
//     * so this does 3 checks:
//     * 1) check if permission granted. if true, normal feature
//     *
//     * 2) shouldShowRequestPermissionRationale() if permission is denied, call this method, if this method
//     * returns true show an educational UI to the user.
//     *
//     * 3) request permission
//     *
//     * */

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {

        }
        Log.d("RequestPermissionCheck","requestCode-->$requestCode")
    }



    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
       val backgroundServiceChecked = homeViewModel.backgroundServiceChecked.value
        val channelName = streamViewModel.channelName.value
        if(channelName !=null && backgroundServiceChecked){
            Log.d("MainActivityOnPause","SEND THE REQUEST")
            val startIntent = Intent(this, BackgroundStreamService::class.java)
            startIntent.action = BackgroundStreamService.Actions.START.toString()
            startIntent.putExtra("channelName", channelName)
            this.startService(startIntent)
        }
        Log.d("MainActivityOnPause","onPause()")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {



            val channel = NotificationChannel(
                "CHANNEL_ID",
                "Background Audio",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notification for media playback service. Allows stream audio to continue to play when application is closed"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun deleteNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val id: String = "CHANNEL_ID"
            notificationManager.deleteNotificationChannel(id)
        }

    }





}
