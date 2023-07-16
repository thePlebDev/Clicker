package com.example.clicker.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TwitchRetrofitInstance {
    private val retrofits by lazy{
        Retrofit.Builder()
            .baseUrl("https://api.twitch.tv/helix/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val api:TwitchClient by lazy {
        retrofits.create(TwitchClient::class.java)
    }

}