package com.example.clicker.network.interceptors

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

/**
 * NetworkMonitorInterceptor is a [application-interceptor](https://square.github.io/okhttp/features/interceptors/#application-interceptors)
 * meant to first check the status of the Network before sending the request
 *
 * @param liveNetworkMonitor a [NetworkMonitor] implementation that handles all of the actual network logic checking
 * */
class NetworkMonitorInterceptor @Inject constructor(
    private val liveNetworkMonitor:NetworkMonitor
): Interceptor {


    @Throws(NoNetworkException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val request: Request = chain.request()

        Log.d("NetworkMonitorInterceptor","NetworkMonitorInterceptor")
        Log.d("checkingInterceptors","NetworkMonitorInterceptor")


        if(liveNetworkMonitor.isConnected()){
            return chain.proceed(request)
        }else{

            Log.d("NetworkMonitorInterceptor","Network Error")
            throw NoNetworkException("Network Error")
        }


    }
}