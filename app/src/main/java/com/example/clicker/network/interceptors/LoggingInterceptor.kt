package com.example.clicker.network.interceptors

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.lang.String


class LoggingInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val t1 = System.nanoTime()
        Log.d(
            "LoggingInterceptor",
            "SENDING REQUEST... url -->${request.url} connection ->${chain.connection()} headers -->${request.headers}"
        )


        val response: Response = chain.proceed(request)
        val t2 = System.nanoTime()

        val time = (t2 - t1) / 1e6
        Log.d(
            "LoggingInterceptor",
            "RECEIVED RESPONSE... url -->${response.request.url} time ->${time} headers -->${response.headers}"
        )

        return response
    }
}