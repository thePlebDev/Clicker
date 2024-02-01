package com.example.clicker.network.domain

import kotlinx.coroutines.flow.StateFlow


/**
 * NetworkMonitorRepo is the interface that acts as the API for all the methods needed to interact with the
 * current state of the Network's status
 *
 * @property connectionLost a function called when the connection is lost
 * @property connectionAvailable a function called when there is a connection
 * @property networkAvailable a [StateFlow] object representing the current state of the Network.
 * */
interface NetworkMonitorRepo {

    fun connectionLost():Unit
    fun connectionAvailable():Unit

    val networkAvailable: StateFlow<Boolean>
}