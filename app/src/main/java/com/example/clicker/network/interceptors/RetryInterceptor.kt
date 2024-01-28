package com.example.clicker.network.interceptors

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


class RetryInterceptor(private val maxRetries: Int) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

            val request = chain.request()
            // try the request
            var response = chain.proceed(request)

            var tryCount = 0;
            while (!response.isSuccessful && tryCount < maxRetries) {
                Log.d("StartingInterception","$tryCount")
                tryCount++

                // retry the request
                response.close()
                response = chain.proceed(request);
            }
            if(tryCount == maxRetries){

                throw(IOException("Error. Please try again"))

            }
            return response


    }

}