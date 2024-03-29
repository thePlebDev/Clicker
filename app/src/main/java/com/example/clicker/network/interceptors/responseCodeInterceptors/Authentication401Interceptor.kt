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

        // Proceed with the request
        val response: Response = chain.proceed(request)
        val code = response.code
        val codeIs401 = authChecker.responseCodeIs401(code)

        if(codeIs401){

            throw Authentication401Exception("Problem with OAuth token")
        }


        //else run the multiple requests
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