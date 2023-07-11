package com.example.clicker.presentation

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.network.repository.GitHubRepoImpl
import com.example.clicker.util.Response
import kotlinx.coroutines.launch

class HomeViewModel(
    val gitHubRepo: GitHubRepoImpl = GitHubRepoImpl()
): ViewModel(){


    private var _uiState: MutableState<String?> = mutableStateOf(null)
    val state:State<String?> = _uiState

    fun changeUiState(code:String){
        _uiState.value = code
    }

    fun makeGitHubRequest(clientId:String,clientSecret:String,code:String) = viewModelScope.launch{

        gitHubRepo.getAccessToken(clientId,clientSecret,code).collect{ response ->
            when(response){
                is Response.Loading ->{Log.d("GITHUB","LOADING")}
                is Response.Success ->{
                    Log.d("GITHUB","SUCCESS")
                    Log.d("GITHUB",response.data.toString())
                }
                is Response.Failure ->{Log.d("GITHUB","FAILURE")}
            }
        }
    }





}