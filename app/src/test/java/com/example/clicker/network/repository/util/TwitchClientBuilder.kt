package com.example.clicker.network.repository.util

import com.example.clicker.network.clients.TwitchAuthenticationClient
import com.example.clicker.network.clients.TwitchClient
import com.example.clicker.network.interceptors.NetworkMonitor
import com.example.clicker.network.interceptors.NetworkMonitorInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
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

