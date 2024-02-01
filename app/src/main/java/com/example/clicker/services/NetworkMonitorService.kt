package com.example.clicker.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.example.clicker.network.domain.TwitchAuthentication
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NetworkMonitorService @Inject constructor(
): Service() {
    @Inject
    lateinit var authentication: TwitchAuthentication



    private lateinit var connectivityManager:ConnectivityManager
    //todo: move below
    val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // network is available for use
        init{
            Log.d("NetworkMonitorService","object created")
        }

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            val handle = network.networkHandle
            val another = network.toString()

            val threadName = Looper.getMainLooper().thread.id
            authentication.testingLogging()

            Log.d("NetworkMonitorService","Available - handle --> $another")


        }

        // Network capabilities have changed for the network
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)

        }

        // lost network connection
        override fun onLost(network: Network) {
            super.onLost(network)
            Log.d("NetworkMonitorService","Lost")
        }
    }


    override fun onCreate() {
        super.onCreate()
        connectivityManager =this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("NetworkMonitorService","startId -->$startId")



        // If we get killed, after returning from here, restart
        return START_NOT_STICKY
    }


    override fun onDestroy() {
       // Log.d("NetworkMonitorService","DESTROY")
        connectivityManager.unregisterNetworkCallback(networkCallback)

    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}
