package com.example.clicker.network.models

import com.google.gson.annotations.SerializedName

data class ValidatedUser(
    @SerializedName("client_id")
    val clientId:String,
    val login:String,
    val scopes:List<String>,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("expires_in")
    val expiresIn:Int

)
