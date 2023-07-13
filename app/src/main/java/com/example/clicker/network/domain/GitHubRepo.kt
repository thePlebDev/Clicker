package com.example.clicker.network.domain

import com.example.clicker.network.models.AccessToken
import com.example.clicker.network.models.GitHubProfile
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow


interface GitHubRepo {

    suspend fun getAccessToken(clientId:String, clientSecret:String, code:String): Flow<Response<AccessToken>>
    suspend fun getProfileData(url:String, authorizationHeader:String): Flow<Response<GitHubProfile>>
}