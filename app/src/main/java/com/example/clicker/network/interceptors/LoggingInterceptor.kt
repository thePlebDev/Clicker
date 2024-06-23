package com.example.clicker.network.interceptors

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class LoggingInterceptor: Interceptor {


    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()


        val response = chain.proceed(request)
        Log.d("LoggingInterceptorBetterTTV","response ->${response.body}")
        val responseBody = response.body
        val contentLength = responseBody?.contentLength() ?: 0L

        if (contentLength != 0L) {
            val source = responseBody?.source()
            source?.request(Long.MAX_VALUE) // Buffer the entire body.
            val buffer = source?.buffer
            val responseBodyString = buffer?.clone()?.readString(Charsets.UTF_8)


            //logging the indiv bytes below will crash
//            Log.d("LoggingInterceptorBetterTTV", "Response Body Bytes:")
//           buffer?.readByteArray()?.also {byteArray->
//                for (byte in byteArray) {
//                    Log.d("LoggingInterceptorBetterTTV", "byte -->${byte.toInt()}")
//                }
//            }

        }


        return response
    }
}