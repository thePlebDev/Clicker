package com.example.clicker.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
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
                Log.d("BackgroundStreamService", "onStartCommand START")
                start()
            }
            Actions.END.toString() ->{
                Log.d("BackgroundStreamService", "onStartCommand END")
                stopSelf()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
    private fun start(){
        val notification = NotificationCompat.Builder(this,"running_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("RUnning")
            .build()
        startForeground(1,notification)
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

}