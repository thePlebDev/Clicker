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
    private var _topGames: MutableState<Response<Boolean>> = mutableStateOf(Response.Loading)
    val topGames:State<Response<Boolean>> = _topGames
    private val _searchRefreshing = mutableStateOf(false)
    val searchRefreshing: State<Boolean> = _searchRefreshing

    private val _searchNetworkStatus = mutableStateOf(SearchNetworkStatus(false,""))
    val searchNetworkStatus: State<SearchNetworkStatus> = _searchNetworkStatus

     var topGamesList = mutableStateListOf<TopGame>()
     var topGamesPinnedList = mutableStateListOf<TopGame>()





    private var _pinnedFilter: MutableState<Boolean> = mutableStateOf(false)
    val pinnedFilter:State<Boolean> = _pinnedFilter

    private var _paginationId: MutableState<String> = mutableStateOf("")

    init{
        monitorMostRecentPaginationRequestId()
    }




    fun updatePinnedFilter(){
        _pinnedFilter.value =!_pinnedFilter.value
    }

    private fun monitorMostRecentPaginationRequestId()=viewModelScope.launch(ioDispatcher){
        twitchSearch.mostRecentPaginationRequestId.collect{nullablePaginationId->
            nullablePaginationId?.also { paginationId ->
                Log.d("PaginationIdMonito","id ->${paginationId}")
                _paginationId.value = paginationId
            }
        }

    }


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
            clientId=clientId,
            after=""
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

                    topGamesList.clear()        // Clear the existing items in topGamesList
                    topGamesList.addAll(updatedList)
                    _searchRefreshing.value = false
                    _topGames.value = Response.Success(true)
                }
                is Response.Failure ->{

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

    fun fetchMoreTopGames()=viewModelScope.launch(ioDispatcher){

        twitchSearch.getTopGames(
            authorizationToken = _validatedUser.value.oAuthToken,
            clientId=_validatedUser.value.clientId,
            after=_paginationId.value
        ).collect{response ->

            Log.d("fetchMoreTopGames","response ->$response")
            when(response){
                is Response.Success->{
                    val updatedList = response.data.map {
                        changeTopGameUrlWidthHeight(
                            aspectWidth=138,
                            aspectHeight=190,
                            topGame=it
                        )
                    }
                    topGamesList.addAll(updatedList)
                }
                else ->{}
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
    fun doubleClickedCategoryAdd(id:String){
        val foundGame = topGamesList.find { it.id==id }

        foundGame?.also { topGame ->
            val updatedTopGame = topGame.copy(clicked = !topGame.clicked)
            val foundGameIndex = topGamesList.indexOf(topGame)
            if(topGame.clicked){
                // remove from pinned
                if(foundGameIndex >=0){
                    doubleClickedCategoryRemove(foundGame)
                    topGamesList[foundGameIndex]= updatedTopGame
                }
            }else{
                //add to the pinnedList
                // avoids the edge case of being -1
                if(foundGameIndex >=0){
                    filterPinnedClickedListAdd(updatedTopGame)
                    topGamesList[foundGameIndex]= updatedTopGame
                }


            }
        }



    }
    //this gets called on the pinnedList
    fun doubleClickedCategoryRemove(topGame:TopGame){
        Log.d("FilterPinnedItemTesting","filterPinnedClickedListRemove")
        val foundGame = topGamesList.find { it.id==topGame.id }
        val foundGameIndex = topGamesList.indexOf(topGame)
        filterPinnedClickedListRemove(topGame)//this needs to be called to remove it from pinnedList
        //I still need to find and update the topGamesList to cause a recomposigion
        if(foundGameIndex >=0){
           foundGame?.also {
               val newTopGame = it.copy(clicked = false)
               topGamesList[foundGameIndex]= newTopGame
           }

        }


    }

    private fun filterPinnedClickedListRemove(topGame: TopGame){
        Log.d("FilterPinnedItemTesting","filterPinnedClickedListRemove")
        Log.d("FilterPinnedItem","${topGame.id}")
        topGamesPinnedList.removeIf { it.id == topGame.id }
        Log.d("FilterPinnedItem","topGamesPinnedList-->${topGamesPinnedList.toList()}")
    }
    private fun filterPinnedClickedListAdd(topGame: TopGame){
        Log.d("FilterPinnedItem","ADD")
        topGamesPinnedList.add(topGame)
        //Log.d("FilterPinnedItem","topGamesPinnedList ->${topGamesPinnedList.toList()}")
    }




    fun changeTopGameUrlWidthHeight(aspectWidth: Int, aspectHeight: Int,topGame: TopGame): TopGame {

        return topGame.copy(
            box_art_url = topGame.box_art_url.replace("{width}", "$aspectWidth")
                .replace("{height}", "$aspectHeight")
        )
    }



}