package com.example.clicker.presentation.search.views

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.clicker.R
import com.example.clicker.presentation.enhancedModView.viewModels.ModViewViewModel
import com.example.clicker.presentation.home.HomeViewModel
import com.example.clicker.presentation.search.SearchViewModel
import com.example.clicker.presentation.sharedViews.NoDrawerScaffold
import com.example.clicker.presentation.sharedViews.PullToRefreshComponent
import com.example.clicker.presentation.stream.AutoModViewModel
import com.example.clicker.presentation.stream.StreamViewModel
import com.example.clicker.presentation.stream.views.chat.chatSettings.ChatSettingsViewModel
import com.example.clicker.presentation.streamInfo.StreamInfoViewModel
import kotlinx.coroutines.launch


/**
 * - **SearchView** is the external wrapper for all the compose code that will be shown inside of the [SearchFragment][com.example.clicker.presentation.search.SearchFragment] .
 * It will handle all the UI related interactions when the user is on the homepage. It also act as the main api between
 * ViewModels and the [SearchFragment][com.example.clicker.presentation.search.SearchFragment] compose code
 *
 * @param homeViewModel a [HomeViewModel] object containing access to all the parameters and functions of the  [HomeViewModel]
 * @param streamViewModel a [StreamViewModel] object containing access to all the parameters and functions of the  [StreamViewModel]
 * @param chatSettingsViewModel a [ChatSettingsViewModel] object containing access to all the parameters and functions of the  [ChatSettingsViewModel]
 * @param onNavigate a function, when called with an Integer, will navigate the user to the appropriate fragment
 * @param autoModViewModel a [AutoModViewModel] object containing access to all the parameters and functions of the  [AutoModViewModel]
 * @param updateModViewSettings a function, when called with an 4 String, will update information related to the current user
 * @param createNewTwitchEventWebSocket a function, when called, will create a websocket to connect to the desired Twitch chat
 * @param hapticFeedBackError a function, when called, will create a haptic feedback on the user's device. This function is
 * meant to be called when an error occurs
 * @param modViewViewModel a [ModViewViewModel] object containing access to all the parameters and functions of the  [ModViewViewModel]
 * @param searchViewModel a [SearchViewModel] object containing access to all the parameters and functions of the  [SearchViewModel]
 * @param streamInfoViewModel a [StreamInfoViewModel] object containing access to all the parameters and functions of the  [StreamInfoViewModel]
 *
 * */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchView(
    onNavigate: (Int) -> Unit,
    homeViewModel: HomeViewModel,
    searchViewModel: SearchViewModel,
    hapticFeedBackError:() ->Unit,
    streamViewModel: StreamViewModel,
    streamInfoViewModel: StreamInfoViewModel,
    modViewViewModel: ModViewViewModel,
    chatSettingsViewModel: ChatSettingsViewModel,
    autoModViewModel: AutoModViewModel,
    createNewTwitchEventWebSocket:()->Unit,
    updateModViewSettings:(String,String,String,String,)->Unit,
    contentPadding: PaddingValues,
    webViewAnimation:(String)->Unit,

    ){

    val oAuthToken = homeViewModel.oAuthToken.collectAsState().value ?:""
    val userId = homeViewModel.validatedUser.collectAsState().value?.userId ?:""
    val clientId = homeViewModel.validatedUser.collectAsState().value?.clientId ?:""
    val lowPowerModeActive = streamViewModel.lowPowerModeActive.value

    // This is where the modal should be

    val state = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden,skipHalfExpanded =true)
    val scope = rememberCoroutineScope()
    ModalBottomSheetLayout(
        modifier = Modifier.padding(contentPadding),
        sheetBackgroundColor= MaterialTheme.colorScheme.primary,
        sheetState = state,
        sheetContent = {
            CategoryModal(
                gameTitle=searchViewModel.clickedGameTitle.value,
                gameInfoResponse=searchViewModel.searchGameInfo.value,
                liveGameStreamsResponse = searchViewModel.searchStreamData.value,
                userId=userId,
                clientId=clientId,
                onNavigate = {navItem ->onNavigate(navItem)},
                height = homeViewModel.state.value.aspectHeight,
                width = homeViewModel.state.value.width,
                density = homeViewModel.state.value.screenDensity,
                updateClickedStreamInfo={
                    //todo: THIS IS WHAT I NEED TO UPDATE
                        clickedStreamInfo ->streamViewModel.updateClickedStreamInfo(clickedStreamInfo)
                },
                updateStreamerName = { streamerName, clientId, broadcasterId, userId ->
                    if (!lowPowerModeActive) {
                        homeViewModel.updateClickedStreamerName(streamerName)


                        Log.d("LOWPOWERMODETESTING", "NON-ACTIVE")
                        streamViewModel.updateChannelNameAndClientIdAndUserId(
                            streamerName,
                            clientId,
                            broadcasterId,
                            userId,
                            login = homeViewModel.validatedUser.value?.login ?: "",
                            oAuthToken= homeViewModel.oAuthToken.value ?:""
                        )
                        streamViewModel.getBetterTTVGlobalEmotes()
                        autoModViewModel.updateAutoModCredentials(
                            oAuthToken = oAuthToken,
                            clientId = streamViewModel.state.value.clientId,
                            moderatorId = streamViewModel.state.value.userId,
                            broadcasterId = streamViewModel.state.value.broadcasterId,
                        )
                        updateModViewSettings(
                            oAuthToken,
                            streamViewModel.state.value.clientId,
                            streamViewModel.state.value.broadcasterId,
                            streamViewModel.state.value.userId,
                        )
                        createNewTwitchEventWebSocket()
                        streamViewModel.getChannelEmotes(
                            oAuthToken,
                            streamViewModel.state.value.clientId,
                            streamViewModel.state.value.broadcasterId,
                        )
                        chatSettingsViewModel.getGlobalChatBadges(
                            oAuthToken = oAuthToken,
                            clientId = streamViewModel.state.value.clientId,
                        )
                        chatSettingsViewModel.getGlobalEmote(
                            oAuthToken = oAuthToken,
                            clientId = streamViewModel.state.value.clientId,
                        )
                        streamViewModel.getBetterTTVChannelEmotes(streamViewModel.state.value.broadcasterId)
                        streamViewModel.clearAllChatters()
                        streamInfoViewModel.getStreamInfo(
                            authorizationToken = oAuthToken,
                            clientId = streamViewModel.state.value.clientId,
                            broadcasterId = streamViewModel.state.value.broadcasterId,
                        )
                        //todo: this needs to be added to the
                        modViewViewModel.getUnbanRequests(
                            oAuthToken =homeViewModel.oAuthToken.value ?:"",
                            clientId=clientId,
                            moderatorId=userId,
                            broadcasterId=broadcasterId
                        )
                    }

                },
                getMoreStreams = {searchViewModel.getMoreStreams()},
                streamDataList= searchViewModel.searchStreamDataList.toList(),
                closeModal={scope.launch { state.hide() }},
                selectedLanguage=searchViewModel.selectedLanguage.value,
                changeSelectedLanguage={newValue ->searchViewModel.changeSelectedLanguage(newValue)},
                webViewAnimation={channelName ->webViewAnimation(channelName)}



            )
        }
    ) {

            PullToRefreshComponent(
                padding = contentPadding,
                refreshing = searchViewModel.searchRefreshing.value,
                refreshFunc = {searchViewModel.pullToRefreshTopGames()},
                showNetworkMessage=searchViewModel.searchNetworkStatus.value.showMessage,
                networkStatus = {modifier -> }
            ) {
                //THIS IS WHERE THE MODAL SHOULD GO
                SearchViewComponent(
                    topGamesListResponse = searchViewModel.topGames.value,
                    showNetworkRefreshError = searchViewModel.searchNetworkStatus.value.showMessage,
                    hapticFeedBackError={hapticFeedBackError()},
                    topGamesList=searchViewModel.topGamesList.toList(),
                    categoryDoubleClickedAdd={id->searchViewModel.doubleClickedCategoryAdd(id)},
                    categoryDoubleClickedRemove ={topGame->searchViewModel.doubleClickedCategoryAdd(topGame.id)},
                    pinnedList = searchViewModel.topGamesPinnedList.toList(),
                    pinned = searchViewModel.pinnedFilter.value,
                    fetchMoreTopGames={
                        searchViewModel.fetchMoreTopGames()
                    },
                    openCategoryModal={
                        scope.launch {
                            state.show()
                        }
                    },
                    getGameInfo={id,gameName ->searchViewModel.getGameInfo(id,gameName)},
                    getGameStreams={id ->searchViewModel.getStreams(id)},

                )
            }


    }

}