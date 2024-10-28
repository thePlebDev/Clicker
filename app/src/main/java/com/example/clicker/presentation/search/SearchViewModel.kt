package com.example.clicker.presentation.search

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.network.clients.Game
import com.example.clicker.network.clients.SearchStreamData
import com.example.clicker.network.clients.TopGame
import com.example.clicker.network.domain.StreamType
import com.example.clicker.network.domain.TwitchSearch
import com.example.clicker.network.repository.models.EmoteNameUrl
import com.example.clicker.network.repository.models.EmoteNameUrlList
import com.example.clicker.network.websockets.models.MessageToken
import com.example.clicker.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.Language
import javax.inject.Inject
val languageHashMap: HashMap<String, String> = hashMapOf(
    "Arabic" to "ar",
    "Bulgarian" to "bg",
    "Catalan" to "ca",
    "Chinese" to "zh",
    "Czech" to "cs",
    "Danish" to "da",
    "Dutch" to "nl",
    "English" to "en",
    "Finnish" to "fi",
    "French" to "fr",
    "German" to "de",
    "Greek" to "el",
    "Hindi" to "hi",
    "Hungarian" to "hu",
    "Indonesian" to "id",
    "Italian" to "it",
    "Japanese" to "ja",
    "Korean" to "Ko",
    "Malay" to "ms",
    "Norwegian" to "no",
    "Polish" to "pl",
    "Portuguese" to "pt",
    "Romanian" to "ro",
    "Russian" to "ru",
    "Slovak" to "sk",
    "Spanish" to "es",
    "Swedish" to "sv",
    "Tagalog" to "tl",
    "Thai" to "th",
    "Turkish" to "tr",
    "Ukrainian" to "uk",
    "American Sign Language" to "ase"
)

data class ClickedValidatedUser(
    val oAuthToken:String,
    val clientId:String,
)
data class SearchNetworkStatus(
    val showMessage: Boolean,
    val message:String,
)

data class AspectHeightAndWidth(
    val width:Int,
    val height:Int
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

    private val _searchGameInfo = mutableStateOf<Response<Game?>>(Response.Loading)
    val searchGameInfo: State<Response<Game?>> = _searchGameInfo

    private val _clickedGameTitle = mutableStateOf<String>("")
    val clickedGameTitle: State<String> = _clickedGameTitle


    private val _searchStreamData: MutableState<Response<Boolean>> = mutableStateOf(
        Response.Loading
    )
    val searchStreamData: State<Response<Boolean>> = _searchStreamData
    var searchStreamDataList = mutableStateListOf<SearchStreamData>()


    private val _aspectHeightAndWdith = mutableStateOf(AspectHeightAndWidth(0,0))

    private val _modalGameId = mutableStateOf("")



    private var _pinnedFilter: MutableState<Boolean> = mutableStateOf(false)
    val pinnedFilter:State<Boolean> = _pinnedFilter

    private var _paginationId: MutableState<String> = mutableStateOf("")

    private var _modalPaginationId: MutableState<String> = mutableStateOf("")

    private var _selectedLanguage: MutableState<String?> = mutableStateOf(null)
    val selectedLanguage:State<String?> = _selectedLanguage

    private val _selectedLanguageStateFlow: MutableStateFlow<String?> = MutableStateFlow(null)


    init{
        monitorMostRecentPaginationRequestId()
    }
    init{
        monitorModalMostRecentPaginationRequestId()
    }
    init {
        monitorSelectedLanguage()
    }

    fun changeSelectedLanguage(language: String){

        _selectedLanguage.value = language //this is update the UI
        _selectedLanguageStateFlow.tryEmit(languageHashMap[language]) //this is the map value
        getStreams(_modalGameId.value)
    }
    private fun monitorSelectedLanguage()=viewModelScope.launch(ioDispatcher){
        _selectedLanguageStateFlow.collect{nullableSelectedLanguage->
            nullableSelectedLanguage?.also { selectedLanguage ->

                getStreams(_modalGameId.value)
            }
        }

    }

    fun updateAspectHeightWidthSearchView(width: Int, height:Int){
        _aspectHeightAndWdith.value = _aspectHeightAndWdith.value.copy(
            width = width,
            height = height
        )

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

    private fun monitorModalMostRecentPaginationRequestId()=viewModelScope.launch(ioDispatcher){
        twitchSearch.mostRecentStreamModalPaginationRequestId.collect{nullablePaginationId->
            nullablePaginationId?.also { paginationId ->
                Log.d("PaginationIdMonito","id ->${paginationId}")

                _modalPaginationId.value = paginationId
            }
        }

    }

    fun getGameInfo(
        gameId:String,
        gameTitle:String
    )=viewModelScope.launch(ioDispatcher){
        _clickedGameTitle.value = gameTitle
        _searchGameInfo.value = Response.Loading

        twitchSearch.getGameInfo(
            authorizationToken = _validatedUser.value.oAuthToken,
            clientId=_validatedUser.value.clientId,
            id=gameId
        ).collect{response ->
            when(response){
                is Response.Loading ->{

                }
                is Response.Success ->{
                   val data = response.data
                    if(data !=null){
                        val newGame =changeGameUrlWidthHeight(
                            aspectWidth=138,
                            aspectHeight=190,
                            game=data
                        )
                        _searchGameInfo.value = Response.Success(newGame)
                    }else{
                        _searchGameInfo.value = Response.Success(null)
                    }

                }
                is Response.Failure ->{
                    _searchGameInfo.value = Response.Failure(Exception("Failed request"))

                }
            }

        }
    }

    fun getStreams(gameId: String)= viewModelScope.launch(ioDispatcher){
        _modalGameId.value = gameId
        _searchStreamData.value = Response.Loading
        searchStreamDataList.clear()
        twitchSearch.getStreams(
            authorization = _validatedUser.value.oAuthToken,
            clientId = _validatedUser.value.clientId,
            gameId=gameId,
            type = StreamType.LIVE,
            language=_selectedLanguageStateFlow.value?:"en",
            after=""
        ).collect{response ->
            when(response){
                is Response.Loading->{


                }
                is Response.Success->{
                    val updatedList = response.data.map {
                        changeSearchStreamDataUrlWidthHeight(
                            aspectWidth=_aspectHeightAndWdith.value.width,
                            aspectHeight=_aspectHeightAndWdith.value.height,
                            searchStreamData=it
                        )
                    }
                    searchStreamDataList.addAll(updatedList)
                    _searchStreamData.value = Response.Success(true)
                }
                is Response.Failure->{
                    _searchStreamData.value = Response.Failure(Exception("Failed"))
                }
            }

        }
    }
    fun getMoreStreams()=viewModelScope.launch(ioDispatcher){
        twitchSearch.getStreams(
            authorization = _validatedUser.value.oAuthToken,
            clientId = _validatedUser.value.clientId,
            gameId=_modalGameId.value,
            type = StreamType.LIVE,
            language="",
            after=_modalPaginationId.value
        ).collect{response ->
            when(response){
                is Response.Loading->{


                }
                is Response.Success->{
                    val updatedList = response.data.map {
                        changeSearchStreamDataUrlWidthHeight(
                            aspectWidth=_aspectHeightAndWdith.value.width,
                            aspectHeight=_aspectHeightAndWdith.value.height,
                            searchStreamData=it
                        )
                    }
                    searchStreamDataList.addAll(updatedList)

                }
                is Response.Failure->{

                }
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




    private fun changeTopGameUrlWidthHeight(aspectWidth: Int, aspectHeight: Int, topGame: TopGame): TopGame {

        return topGame.copy(
            box_art_url = topGame.box_art_url.replace("{width}", "$aspectWidth")
                .replace("{height}", "$aspectHeight")
        )
    }
    private fun changeSearchStreamDataUrlWidthHeight(aspectWidth: Int, aspectHeight: Int, searchStreamData: SearchStreamData): SearchStreamData {
     Log.d("TestingTheCrash","body->${searchStreamData.id}")
        Log.d("TestingTheCrash","body->${searchStreamData.user_id}")
        Log.d("TestingTheCrash","body->${searchStreamData.user_login}")
        Log.d("TestingTheCrash","body->${searchStreamData.user_name}")
        Log.d("TestingTheCrash","body->${searchStreamData.game_id}")
        Log.d("TestingTheCrash","body->${searchStreamData.game_name}")
        Log.d("TestingTheCrash","body->${searchStreamData.title}")
        Log.d("TestingTheCrash","body->${searchStreamData.tags}")
        Log.d("TestingTheCrash","body->${searchStreamData.tag_ids}")

        Log.d("TestingTheCrash","body->${searchStreamData.language}")
        Log.d("TestingTheCrash","body->${searchStreamData.type}")
        Log.d("TestingTheCrash","body->${searchStreamData.is_mature}")
        Log.d("TestingTheCrash","body->${searchStreamData.started_at}")
        Log.d("TestingTheCrash","body->${searchStreamData.viewer_count}")
        Log.d("TestingTheCrash","body->${searchStreamData.thumbnail_url}")



        return searchStreamData.copy(
            thumbnail_url = searchStreamData.thumbnail_url.replace("{width}", "$aspectWidth")
                .replace("{height}", "$aspectHeight"),
            type="live"
        )
    }

    private fun changeGameUrlWidthHeight(aspectWidth: Int, aspectHeight: Int, game: Game): Game {

        return game.copy(
            box_art_url = game.box_art_url.replace("{width}", "$aspectWidth")
                .replace("{height}", "$aspectHeight")
        )
    }



}