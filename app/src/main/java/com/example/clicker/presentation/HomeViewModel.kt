package com.example.clicker.presentation

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.network.GitHubClient
import com.example.clicker.network.repository.GItHubRepo
import com.example.clicker.util.findActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeViewModel(
    val gitHubRepo: GItHubRepo = GItHubRepo()
): ViewModel(){


    private var _uiState: MutableState<String?> = mutableStateOf(null)
    val state:State<String?> = _uiState

    fun changeUiState(code:String){
        _uiState.value = code
    }

    fun makeGitHubRequest(clientId:String,clientSecret:String,code:String) = viewModelScope.launch{
        val data = gitHubRepo.getAuthCode(
            clientId=clientId,
            clientSecret = clientSecret,
            code = code
        )
        if(data.isSuccessful){
            if(data.body() !=null){
                Log.d("GITHUB",data.body().toString())
            }else{
                Log.d("GITHUB","data.body() is null")
            }

        }else{
            Log.d("GITHUB", "NOT SUCCESSFUL $data")
        }
    }





}