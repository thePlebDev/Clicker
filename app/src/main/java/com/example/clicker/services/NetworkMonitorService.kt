package com.example.clicker.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class NetworkMonitorService: Service() {





    override fun onCreate() {

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("NetworkMonitorService","STARTING")


        // If we get killed, after returning from here, restart
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    override fun onDestroy() {
        Log.d("NetworkMonitorService","DESTROY")

    }

}
