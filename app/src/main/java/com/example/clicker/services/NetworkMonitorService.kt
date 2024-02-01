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
import com.example.clicker.network.domain.NetworkMonitorRepo
import com.example.clicker.network.domain.TwitchAuthentication
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NetworkMonitorService @Inject constructor(
): Service() {
    @Inject
    lateinit var networkMonitorRepo: NetworkMonitorRepo


    private lateinit var connectivityManager:ConnectivityManager
    //todo: move below
    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    private val networkCallback =NetworkCallBack(
        onNetworkAvailable = {networkMonitorRepo.connectionAvailable()},
        onNetworkLost = {networkMonitorRepo.connectionLost()}
    )




    override fun onCreate() {
        super.onCreate()
        connectivityManager =this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback.checkCurrentNetwork(connectivityManager.activeNetwork)
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

class NetworkCallBack(
    val onNetworkAvailable: () -> Unit,
    val onNetworkLost: () -> Unit,
): ConnectivityManager.NetworkCallback(){

    override fun onAvailable(network: Network) {
        super.onAvailable(network)

        onNetworkAvailable()

    }

    fun checkCurrentNetwork(currentNetwork:Network?){
        if (currentNetwork == null){
            onNetworkLost()
        }
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
        onNetworkLost()
    }
}
