package com.example.clicker.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.clicker.services.BackgroundStreamService
import com.example.clicker.services.ServiceActions

/**
 * - **ShutDownBroadcastReceiver** is a [BroadcastReceiver] object meant to capture the action from the
 * notification and send a [END][com.example.clicker.services.BackgroundStreamService.Actions.END] action to
 * the [BackgroundStreamService] object
 *
 * */
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





