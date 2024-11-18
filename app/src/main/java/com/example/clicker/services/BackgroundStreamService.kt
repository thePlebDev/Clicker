package com.example.clicker.services

import android.Manifest
import android.app.ForegroundServiceStartNotAllowedException
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import com.example.clicker.R

class BackgroundStreamService: Service() {
    private val binder = LocalBinder()
    override fun onBind(p0: Intent?): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            Actions.START.toString()->{
                Log.d("BackgroundStreamServiceOnStartCommand", "onStartCommand START")
                createNotificationChannel()
                startForeground()
            }
            Actions.END.toString() ->{
                Log.d("BackgroundStreamServiceOnStartCommand", "onStartCommand END")
                stopSelf()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
    private fun start(){
//        val notification = NotificationCompat.Builder(this,"running_channel")
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setContentTitle("RUnning")
//            .setContentText("This is another one")
//            .build()
//        startForeground(1,notification)
        startForeground()
    }

    enum class Actions{
        START, END
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("BackgroundStreamService", "Service CREATE")


    }
    inner class LocalBinder : Binder() {
        fun getService(): BackgroundStreamService = this@BackgroundStreamService
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("BackgroundStreamService", "Service DESTROY")

    }

    private fun startForeground() {
        // Before starting the service as foreground check that the app has the
        // appropriate runtime permissions. In this case, verify that the user has
        // granted the CAMERA permission.

        Log.d("BackgroundStreamServiceOnStartCommand", "startForeground()")
        val notification = NotificationCompat.Builder(this, "CHANNEL_ID")
            .setContentTitle("Background audio mode is active")
            .setContentText("The stream audio will continue even when app is closed")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        ServiceCompat.startForeground(
            /* service = */ this,
            /* id = */ 100, // Cannot be 0
            /* notification = */ notification,
            /* foregroundServiceType = */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            } else {
                0
            },
        )
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "CHANNEL_ID",
                "Media Playback",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notification for media playback service"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

}