package com.example.clicker.network.models

data class AuthenticatedUser(
    val clientId:String,
    val userId:String,
    val userName:String
)
