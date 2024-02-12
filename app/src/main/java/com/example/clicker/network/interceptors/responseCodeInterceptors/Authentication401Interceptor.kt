package com.example.clicker.network.interceptors.responseCodeInterceptors

import android.util.Log
import com.example.clicker.network.interceptors.NoNetworkException
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class Authentication401Interceptor(
    private val authChecker:AuthenticationInterceptor
): Interceptor {

    @Throws(Authentication401Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        Log.d("Authentication401InterceptorURL","url -->${request.url}")
        // Proceed with the request
        val response: Response = chain.proceed(request)
        val code = response.code
        if(authChecker.responseCodeIs401(code)){
            Log.d("Authentication401Interceptor","401 ERROR")
            throw Authentication401Exception("Problem with OAuth token")
        }
        return response
    }
}


interface AuthenticationInterceptor {
    fun responseCodeIs401(code:Int):Boolean
}

class ResponseChecker(): AuthenticationInterceptor{
    override fun responseCodeIs401(code: Int): Boolean {
        return code == 401
    }

}