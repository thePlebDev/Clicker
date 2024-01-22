package com.example.clicker.utility

import com.example.clicker.network.interceptors.NetworkMonitor
import com.example.clicker.network.interceptors.NoNetworkException
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject


class NetworkMonitorInterceptorTest @Inject constructor(
    private val liveNetworkMonitor: NetworkMonitor
): Interceptor {

    var throwExcpetion= false

    @Throws(NoNetworkException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        if(liveNetworkMonitor.isConnected()){
            return chain.proceed(request)
        }else{

            throw NoNetworkException("Network Error")
        }

    }
}