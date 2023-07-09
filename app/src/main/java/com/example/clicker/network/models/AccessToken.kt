package com.example.clicker.network.models

import com.google.gson.annotations.SerializedName

data class AccessToken(
    @SerializedName("access_token")
    val accessToken:String,
    @SerializedName("token_type")
    val tokenType:String
)
