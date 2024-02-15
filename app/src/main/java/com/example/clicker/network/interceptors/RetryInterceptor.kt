package com.example.clicker.network.interceptors

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


class RetryInterceptor(private val retry:Retry =RetryWithThreeRequests()) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val isSuccess = retry.requestWithThreeRetries(chain, request)
        if (!isSuccess) {
            throw IOException("Error. Please try again")
        }
        return chain.proceed(request)
    }


}

interface Retry{
    fun requestWithThreeRetries(chain: Interceptor.Chain, request: Request): Boolean
}

class RetryWithThreeRequests:Retry{
    override fun requestWithThreeRetries(chain: Interceptor.Chain, request: Request): Boolean {
        val retryLimit =3
        var tryCount = 0
        var response: Response
        while (tryCount < retryLimit) {
            Log.d("tryRequestWithRetries","trycount --> $tryCount")
            response = chain.proceed(request)
            if (response.isSuccessful) {
                return true
            }
            response.close()
            tryCount++
        }
        return false
    }

}