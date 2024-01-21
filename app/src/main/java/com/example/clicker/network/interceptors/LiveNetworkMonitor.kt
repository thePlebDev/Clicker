package com.example.clicker.network.interceptors

import android.content.Context
import android.net.ConnectivityManager
import javax.inject.Inject

class LiveNetworkMonitor @Inject constructor(
    private val context: Context
):NetworkMonitor {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun isConnected(): Boolean {
        val network =connectivityManager.activeNetwork
        return network != null
    }
}