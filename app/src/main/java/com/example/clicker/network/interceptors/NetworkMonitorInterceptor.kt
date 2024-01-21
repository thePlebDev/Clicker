package com.example.clicker.network.interceptors

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class NetworkMonitorInterceptor @Inject constructor(
    private val liveNetworkMonitor:NetworkMonitor
): Interceptor {


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        if(liveNetworkMonitor.isConnected()){
            return chain.proceed(request)
        }else{
            throw IOException("Network Problem")
        }

    }
}