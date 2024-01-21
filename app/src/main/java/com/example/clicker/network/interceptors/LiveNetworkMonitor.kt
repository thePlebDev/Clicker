package com.example.clicker.network.interceptors

import android.content.Context
import android.net.ConnectivityManager
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

    override fun isConnected(): Boolean {
        val network =connectivityManager.activeNetwork
        return network != null
    }
}