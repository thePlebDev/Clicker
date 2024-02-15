package com.example.clicker.network.repository.util

import com.example.clicker.network.clients.TwitchAuthenticationClient
import com.example.clicker.network.clients.TwitchClient
import com.example.clicker.network.interceptors.NetworkMonitor
import com.example.clicker.network.interceptors.NetworkMonitorInterceptor
import com.example.clicker.network.interceptors.Retry
import com.example.clicker.network.interceptors.RetryInterceptor
import com.example.clicker.network.interceptors.responseCodeInterceptors.Authentication401Interceptor
import com.example.clicker.network.interceptors.responseCodeInterceptors.AuthenticationInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * TwitchAuthenticationClientBuilder is a object declaration helper class. Using the Builder pattern,
 * this class allows us to easily create [TwitchAuthenticationClient] and add a failing [Interceptor]
 * */
object TwitchAuthenticationClientBuilder{
    private val retroFitClient = Retrofit.Builder()

    fun buildClientWithURL(url:String): TwitchAuthenticationClient {
        return retroFitClient.baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(TwitchAuthenticationClient::class.java)
    }
    fun addFailingNetworkInterceptor():TwitchAuthenticationClientBuilder{
        val failingNetworkClient = object: NetworkMonitor {
            override fun isConnected(): Boolean {
                return false
            }
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(NetworkMonitorInterceptor(failingNetworkClient))
            .build()
        retroFitClient.client(client)
        return this
    }
}

/**
 * TwitchStreamClientBuilder is a object declaration helper class. Using the Builder pattern,
 * this class allows us to easily create [TwitchStreamClientBuilder] and add a failing application level [Interceptor]
 * */
object TwitchStreamClientBuilder{
    private val retroFitClient = Retrofit.Builder()

    fun buildClientWithURL(url:String): TwitchClient {
        return retroFitClient.baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(TwitchClient::class.java)
    }
    fun addFailingNetworkInterceptor():TwitchStreamClientBuilder{
        val failingNetworkClient = object: NetworkMonitor {
            override fun isConnected(): Boolean {
                return false
            }
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(NetworkMonitorInterceptor(failingNetworkClient))
            .build()
        retroFitClient.client(client)
        return this
    }
}

object TwitchClientBuilderUtil{
    private val retroFitClient = Retrofit.Builder()
    private var client = OkHttpClient.Builder()

    fun addMockedUrl(url:String):TwitchClientBuilderUtil{
        retroFitClient.baseUrl(url)
        return this
    }
    fun addNetworkInterceptor(networkIsOnline: Boolean):TwitchClientBuilderUtil{
        val networkMonitor = object: NetworkMonitor {
            override fun isConnected(): Boolean {
                return networkIsOnline
            }
        }
        client.addInterceptor(NetworkMonitorInterceptor(networkMonitor))

        return this
    }

    fun addAuthentication401Interceptor(codeIs401: Boolean):TwitchClientBuilderUtil{
        val auth401Checker = object: AuthenticationInterceptor{
            override fun responseCodeIs401(code: Int): Boolean {
                return codeIs401
            }


        }
        client.addInterceptor(Authentication401Interceptor(auth401Checker))
        return this
    }

    fun build():TwitchAuthenticationClient{

        retroFitClient.client(client.build())
        return retroFitClient
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TwitchAuthenticationClient::class.java)
    }

}

