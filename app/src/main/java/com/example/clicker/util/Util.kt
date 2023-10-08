package com.example.clicker.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log

internal fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Permissions should be called in the context of an Activity")
}

fun logCoroutineInfo(tag:String,msg:String){
    Log.d(tag,"Running on: [${Thread.currentThread().name}] | $msg")
}