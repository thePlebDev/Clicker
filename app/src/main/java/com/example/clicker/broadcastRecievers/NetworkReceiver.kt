package com.example.clicker.broadcastRecievers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log


class NetworkReceiver : BroadcastReceiver() {
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // network is available for use
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.d("NetworkReceiver","Available")
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
            Log.d("NetworkReceiver","Lost")
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
         val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder()
            .build()

        connectivityManager.requestNetwork(networkRequest, TestingThing())
    }
}
class TestingThing: ConnectivityManager.NetworkCallback(){

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        Log.d("TestingThingNetworkReceiver","Available")
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
        Log.d("TestingThingNetworkReceiver","Lost")
    }

}