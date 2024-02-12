package com.example.clicker.presentation

import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthenticationEventBus:AuthenticationEvent {

    private val _authenticationStatus = MutableStateFlow<Response<Boolean>?>(null)
    override val authenticationStatus = _authenticationStatus.asStateFlow()


    override fun setLoggedInt(loggedInStatus:Response<Boolean>){
        _authenticationStatus.tryEmit(loggedInStatus)
    }


}

 interface AuthenticationEvent{
     val authenticationStatus: StateFlow<Response<Boolean>?> // abstract

     fun setLoggedInt(loggedInStatus:Response<Boolean>)
 }