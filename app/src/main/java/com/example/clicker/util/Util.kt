package com.example.clicker.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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



fun <T, R : Any> Flow<T>.mapWithRetry(
    action: suspend (T) -> R,
    predicate: suspend (R, attempt: Int) -> Boolean
) = map { data ->
    var attempt = 0L
    var shallRetry: Boolean
    var lastValue: R? = null
    do {
        val tr = action(data)
        shallRetry = predicate(tr, (++attempt).toInt())
        if (!shallRetry) lastValue = tr
    } while (shallRetry)
    return@map lastValue
}

