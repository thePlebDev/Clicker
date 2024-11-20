package com.example.clicker.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.clicker.services.BackgroundStreamService

class ShutDownBroadcastReceiver : BroadcastReceiver() {


    override fun onReceive(p0: Context?, p1: Intent?) {

        Log.d("ShutDownBroadcastReceiver","SHUT IT DOWN!!!!!!!!!!")
        p0?.let{context ->
            val startIntent = Intent(context, BackgroundStreamService::class.java)
            startIntent.action = BackgroundStreamService.Actions.END.toString()
            context.startService(startIntent)
        }

    }

}





