package com.example.clicker.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofits by lazy{
        Retrofit.Builder()
            .baseUrl("https://github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val api:GitHubClient by lazy {
        retrofits.create(GitHubClient::class.java)
    }

}