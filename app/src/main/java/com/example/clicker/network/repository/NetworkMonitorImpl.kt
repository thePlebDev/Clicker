package com.example.clicker.network.repository

import com.example.clicker.network.domain.NetworkMonitorRepo
import com.example.clicker.network.domain.TwitchAuthentication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * NetworkMonitorImpl the implementation class of [NetworkMonitorRepo]. This class contains all the methods
 * relating to the state of the network
 * */
class NetworkMonitorImpl() : NetworkMonitorRepo {
    private val _networkAvailable = MutableStateFlow<Boolean>(true)
    override val networkAvailable: StateFlow<Boolean> = _networkAvailable.asStateFlow()


    override fun connectionLost() {
        _networkAvailable.tryEmit(false)
    }

    override fun connectionAvailable() {
        _networkAvailable.tryEmit(true)
    }
}