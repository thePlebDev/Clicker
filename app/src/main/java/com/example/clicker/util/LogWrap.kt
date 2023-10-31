package com.example.clicker.util

import android.util.Log
/**
 * A wrapper class for the [Log](android.util.Log) class.
 *
 * - This class is used so ProGuard can identify all the logs and remove them for the release version
 *
 *
 */
class LogWrap {
    companion object {
        fun d(tag: String, message: String) {
            Log.d(tag, message)
        }
    }
}