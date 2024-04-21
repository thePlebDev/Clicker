package com.example.clicker.presentation.stream.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class NetworkMonitoring @Inject constructor(
    private val context: Context
) {
    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
    private val _networkStatus = MutableStateFlow<Boolean?>(null)
     val networkStatus = _networkStatus.asStateFlow()


    init{
        connectivityManager.registerDefaultNetworkCallback(
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network : Network) {
                    Log.d("NetworkMonitoring","Available")
                    _networkStatus.tryEmit(true)
                    // indicates that the device is connected to a new network that satisfies the capabilities
                    // and transport type requirements specified in the NetworkRequest
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    Log.d("NetworkMonitoring","Lost")
                    _networkStatus.tryEmit(false)
                }

            }
        )
    }


}