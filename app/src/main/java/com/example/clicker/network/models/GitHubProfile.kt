package com.example.clicker.network.models

import com.google.gson.annotations.SerializedName

data class GitHubProfile(
    val login:String
)

//data class AccessToken(
//    @SerializedName("access_token")
//    val accessToken:String,
//    @SerializedName("token_type")
//    val tokenType:String,
//)