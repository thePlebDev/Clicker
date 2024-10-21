package com.example.clicker.presentation.search

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.network.clients.TopGame
import com.example.clicker.network.domain.TwitchSearch
import com.example.clicker.network.repository.models.EmoteNameUrl
import com.example.clicker.network.repository.models.EmoteNameUrlList
import com.example.clicker.network.websockets.models.MessageToken
import com.example.clicker.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


data class ClickedValidatedUser(
    val oAuthToken:String,
    val clientId:String,
)
data class SearchNetworkStatus(
    val showMessage: Boolean,
    val message:String,
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val twitchSearch: TwitchSearch,
    private val ioDispatcher: CoroutineDispatcher,
): ViewModel(){


    private val _validatedUser = mutableStateOf(ClickedValidatedUser("",""))
    private var _topGames: MutableState<Response<List<TopGame>>> = mutableStateOf(Response.Loading)
    val topGames:State<Response<List<TopGame>>> = _topGames
    private val _searchRefreshing = mutableStateOf(false)
    val searchRefreshing: State<Boolean> = _searchRefreshing

    private val _searchNetworkStatus = mutableStateOf(SearchNetworkStatus(false,""))
    val searchNetworkStatus: State<SearchNetworkStatus> = _searchNetworkStatus






     fun getTopGames(
        clientId: String,
        oAuthToken: String
    )=viewModelScope.launch(ioDispatcher){
        Log.d("getTopGamesTesting","oAuthToken -->$oAuthToken")
         _validatedUser.value = _validatedUser.value.copy(
             oAuthToken = oAuthToken,
             clientId=clientId
         )
        twitchSearch.getTopGames(
            authorizationToken = oAuthToken,
            clientId=clientId

        ).collect{response ->
            when(response){
                is Response.Loading ->{
                }
                is Response.Success ->{
                    //todo: I NEED TO PARSE THE DATA AND REPLACE THE WIDTH AND HEIGHT
                    //  _topGames.value = Response.Success
                    val updatedList = response.data.map {
                        changeTopGameUrlWidthHeight(
                            aspectWidth=138,
                            aspectHeight=190,
                            topGame=it
                        )
                    }

                    _searchRefreshing.value = false
                    _topGames.value = Response.Success(updatedList)
                }
                is Response.Failure ->{
                   when(_topGames.value){
                       is Response.Success->{
                           _searchRefreshing.value = false
                           _searchNetworkStatus.value = _searchNetworkStatus.value.copy(
                               showMessage = true,
                               message = "Error! Please try again"
                           )
                           delay(1000)
                           _searchNetworkStatus.value = _searchNetworkStatus.value.copy(
                               showMessage = false,

                               )
                       }
                       else ->{
                           _searchRefreshing.value = false
                           _topGames.value = Response.Failure(Exception("Error! Please try again"))
                           _searchNetworkStatus.value = _searchNetworkStatus.value.copy(
                               showMessage = true,
                               message = "Error! Please try again"
                           )
                           delay(1000)
                           _searchNetworkStatus.value = _searchNetworkStatus.value.copy(
                               showMessage = false,

                               )

                       }
                   }

                }
            }

        }
    }


    fun pullToRefreshTopGames(){
        viewModelScope.launch(ioDispatcher) {

            _searchRefreshing.value = true

            getTopGames(
                clientId= _validatedUser.value.clientId,
                oAuthToken = _validatedUser.value.oAuthToken
            )

        }
    }



    fun changeTopGameUrlWidthHeight(aspectWidth: Int, aspectHeight: Int,topGame: TopGame): TopGame {

        return topGame.copy(
            box_art_url = topGame.box_art_url.replace("{width}", "$aspectWidth")
                .replace("{height}", "$aspectHeight")
        )
    }



}