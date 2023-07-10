package com.example.clicker.network.repository

import android.util.Log
import com.example.clicker.network.RetrofitInstance
import com.example.clicker.network.models.AccessToken
import retrofit2.Call
import retrofit2.Response

class GItHubRepo {

    suspend fun getAuthCode(
        clientId:String,
        clientSecret:String,
        code:String
    ):Response<AccessToken>{

        return RetrofitInstance.api.getAccessToken(clientId, clientSecret, code)
    }
}