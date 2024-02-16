package com.example.clicker.network.repository.util

import com.example.clicker.network.clients.TwitchAuthenticationClient
import com.example.clicker.network.clients.TwitchClient
import com.example.clicker.network.clients.TwitchHomeClient
import com.example.clicker.network.interceptors.NetworkMonitor
import com.example.clicker.network.interceptors.NetworkMonitorInterceptor
import com.example.clicker.network.interceptors.Retry
import com.example.clicker.network.interceptors.RetryInterceptor
import com.example.clicker.network.interceptors.responseCodeInterceptors.Authentication401Interceptor
import com.example.clicker.network.interceptors.responseCodeInterceptors.AuthenticationInterceptor
import com.example.clicker.network.repository.util.TwitchClientBuilderUtil.client
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**createJsonBodyFrom is a generic function that is used to anything it is given into a JSON string
 *
 * @param body represents that is going to be turned into a JSON string
 * */
fun <T> createJsonBodyFrom(body:T): String {
    val gson = Gson()
    return gson.toJson(body)
}



/**
 * TwitchClientBuilderUtil is a object declaration helper class. Using the Builder pattern,
 * this class allows us to easily create [TwitchClientBuilderUtil] and add a failing application level [Interceptor]
 * */

object TwitchClientBuilderUtil{
    /**The fake http client make for tests involving [TwitchAuthenticationClient]*/
    private val retroFitClient = Retrofit.Builder()

    /**The a client added to [retroFitClient] and is used to add interceptors*/
    private var client = OkHttpClient.Builder()

    /**
     * sets the `fake` url for our [retroFitClient]
     *
     * @param url sets the fake url for our client. Must use mockWebServer: mockWebServer.url("/").toString()
     * */
    fun addMockedUrl(url:String):TwitchClientBuilderUtil{
        retroFitClient.baseUrl(url)
        return this
    }
    /**
     * addNetworkInterceptor adds a [NetworkMonitorInterceptor] to the [client] and represents an controllable network interceptor
     *
     * @param networkIsOnline a Boolean used to represent if the network is live or not
     * */
    fun addNetworkInterceptor(networkIsOnline: Boolean):TwitchClientBuilderUtil{
        val networkMonitor = object: NetworkMonitor {
            override fun isConnected(): Boolean {
                return networkIsOnline
            }
        }
        client.addInterceptor(NetworkMonitorInterceptor(networkMonitor))

        return this
    }

    /**
     * addAuthentication401Interceptor adds a [Authentication401Interceptor] to the [client] and represents an controllable
     * interceptor that checks for the response code 401
     *
     * @param responseCodeIs401 a Boolean used to fake if the response code is 401 or not
     * */
    fun addAuthentication401Interceptor(responseCodeIs401: Boolean):TwitchClientBuilderUtil{
        val auth401Checker = object: AuthenticationInterceptor{
            override fun responseCodeIs401(code: Int): Boolean {
                return responseCodeIs401
            }


        }
        client.addInterceptor(Authentication401Interceptor(auth401Checker))
        return this
    }

    /**
     * build() is called to build the [client] and the [retroFitClient] and deliver a fully functioning [TwitchAuthenticationClient]
     *
     *
     * @return a [TwitchAuthenticationClient] which is used to mock calls to the a actual web server
     * */
    fun build():TwitchAuthenticationClient{

        retroFitClient.client(client.build())
        return retroFitClient
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TwitchAuthenticationClient::class.java)
    }

}

object TwitchHomeClientBuilder {

    /**The fake http client make for tests involving [TwitchAuthenticationClient]*/
    private val retroFitClient = Retrofit.Builder()

    /**The a client added to [retroFitClient] and is used to add interceptors*/
    private var client = OkHttpClient.Builder()

    /**
     * sets the `fake` url for our [retroFitClient]
     *
     * @param url sets the fake url for our client. Must use mockWebServer: mockWebServer.url("/").toString()
     * */
    fun addMockedUrl(url:String):TwitchHomeClientBuilder{
        retroFitClient.baseUrl(url)
        return this
    }

    /**
     * addNetworkInterceptor adds a [NetworkMonitorInterceptor] to the [client] and represents an controllable network interceptor
     *
     * @param networkIsOnline a Boolean used to represent if the network is live or not
     * */
    fun addNetworkInterceptor(networkIsOnline: Boolean):TwitchHomeClientBuilder{
        val networkMonitor = object: NetworkMonitor {
            override fun isConnected(): Boolean {
                return networkIsOnline
            }
        }
        client.addInterceptor(NetworkMonitorInterceptor(networkMonitor))

        return this
    }

    /**
     * addAuthentication401Interceptor adds a [Authentication401Interceptor] to the [client] and represents an controllable
     * interceptor that checks for the response code 401
     *
     * @param responseCodeIs401 a Boolean used to fake if the response code is 401 or not
     * */
    fun addAuthentication401Interceptor(responseCodeIs401: Boolean):TwitchHomeClientBuilder{
        val auth401Checker = object: AuthenticationInterceptor{
            override fun responseCodeIs401(code: Int): Boolean {
                return responseCodeIs401
            }


        }
        client.addInterceptor(Authentication401Interceptor(auth401Checker))
        return this
    }

    fun build():TwitchHomeClient{

        retroFitClient.client(client.build())
        return retroFitClient
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TwitchHomeClient::class.java)
    }
}

