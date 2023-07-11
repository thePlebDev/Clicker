package com.example.clicker.network.repository

import com.example.clicker.network.domain.GitHubRepo
import com.example.clicker.network.GitHubClient
import com.example.clicker.network.RetrofitInstance
import com.example.clicker.network.models.AccessToken
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GitHubRepoImpl(
    private val gitHubClient: GitHubClient = RetrofitInstance.api
): GitHubRepo {

    override suspend fun getAccessToken(
        clientId: String,
        clientSecret: String,
        code: String
    ): Flow<Response<AccessToken>> = flow{
        emit(Response.Loading)
        val items = gitHubClient.getAccessToken(clientId,clientSecret,code)
        if(items.isSuccessful){
            emit(Response.Success(items.body()!!))
        }else{
            emit(Response.Failure(Exception("Access Token Error")))
        }
    }.catch { cause:Throwable ->
        emit(Response.Failure(Exception("Error! Please try again")))
    }
}