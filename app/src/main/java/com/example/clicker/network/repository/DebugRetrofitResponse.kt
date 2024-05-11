package com.example.clicker.network.repository

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class DebugRetrofitResponse: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val rawJson = response.body!!.string()
        Log.d("DebugRetrofitResponse","response -->${response.body}")
        Log.d("DebugRetrofitResponse","rawJson -->${rawJson}")
        return response
    }
}