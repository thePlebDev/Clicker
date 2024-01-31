package com.example.clicker.network.interceptors

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import javax.inject.Inject

/**
 * LiveNetworkMonitor is a class meant to check the application's network connectivity
 *
 * @param context a [Context] object. This is what is used to get the system's connectivity manager
 * */
class LiveNetworkMonitor @Inject constructor(
    private val context: Context
):NetworkMonitor {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    //todo: move below
    val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // network is available for use
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.d("networkRequestManager","Available")
        }

        // Network capabilities have changed for the network
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            val unmetered = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
        }

        // lost network connection
        override fun onLost(network: Network) {
            super.onLost(network)
            Log.d("networkRequestManager","Lost")
        }
    }
    init{
        stuff()
    }

    fun stuff(){
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }

    //todo: move above

    override fun isConnected(): Boolean {
        val network =connectivityManager.activeNetwork
        return network != null
    }
}