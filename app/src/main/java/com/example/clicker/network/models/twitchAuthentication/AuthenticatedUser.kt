package com.example.clicker.network.models.twitchAuthentication

data class AuthenticatedUser(
    val clientId: String,
    val userId: String,
    val userName: String
)
