package com.example.clicker.presentation.moderatedChannelsHome.views

import android.util.Log
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import com.example.clicker.R
import com.example.clicker.presentation.authentication.logout.LogoutViewModel
import com.example.clicker.presentation.enhancedModView.viewModels.ModViewViewModel
import com.example.clicker.presentation.home.HomeFragment
import com.example.clicker.presentation.home.HomeViewModel
import com.example.clicker.presentation.search.SearchViewModel
import com.example.clicker.presentation.stream.AutoModViewModel
import com.example.clicker.presentation.stream.StreamViewModel
import com.example.clicker.presentation.stream.views.chat.chatSettings.ChatSettingsViewModel
import com.example.clicker.presentation.streamInfo.StreamInfoViewModel
import com.example.clicker.util.Response
import kotlinx.coroutines.launch

/**
 * - **ModChannelView** is the external wrapper for all the compose code that will be shown inside of the [ModChannelsFragment][com.example.clicker.presentation.moderatedChannelsHome.ModChannelsFragment] .
 * It will handle all the UI related interactions when the user is on the homepage. It also act as the main api between
 * ViewModels and the [ModChannelsFragment][com.example.clicker.presentation.moderatedChannelsHome.ModChannelsFragment] compose code
 *
 * @param homeViewModel a [HomeViewModel] object containing access to all the parameters and functions of the  [HomeViewModel]
 * @param streamViewModel a [StreamViewModel] object containing access to all the parameters and functions of the  [StreamViewModel]
 * @param onNavigate a function, when called with an Integer, will navigate the user to the appropriate fragment
 * @param autoModViewModel a [AutoModViewModel] object containing access to all the parameters and functions of the  [AutoModViewModel]
 * @param updateModViewSettings a function, when called with an 4 String, will update information related to the current user
 * @param createNewTwitchEventWebSocket a function, when called, will create a websocket to connect to the desired Twitch chat
 * @param hapticFeedBackError a function, when called, will create a haptic feedback on the user's device. This function is
 * meant to be called when an error occurs
 * @param modViewViewModel a [ModViewViewModel] object containing access to all the parameters and functions of the  [ModViewViewModel]
 * @param searchViewModel a [SearchViewModel] object containing access to all the parameters and functions of the  [SearchViewModel]
 *
 * */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModChannelView(
    homeViewModel: HomeViewModel,
    streamViewModel: StreamViewModel,
    autoModViewModel: AutoModViewModel,
    popBackStackNavigation: () -> Unit,
    onNavigate: (Int) -> Unit,
    updateModViewSettings:(String,String,String,String,)->Unit,
    createNewTwitchEventWebSocket:()->Unit,
    hapticFeedBackError:() ->Unit,
    logoutViewModel: LogoutViewModel,
    modViewViewModel: ModViewViewModel,
    searchViewModel: SearchViewModel
){
    val bottomModalState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()


    val userId = homeViewModel.validatedUser.collectAsState().value?.userId ?:""
    val clientId = homeViewModel.validatedUser.collectAsState().value?.clientId ?:""
    val showNetworkRefreshError:Boolean = homeViewModel.state.value.showNetworkRefreshError
    val oAuthToken = homeViewModel.oAuthToken.collectAsState().value ?:""




    ModalBottomSheetLayout(
        sheetState = bottomModalState,
        sheetContent = {
            ModChannelsBottomModalSheetContent(
                loginWithTwitch= {
                    logoutViewModel.setLoggedOutStatus("TRUE")
                    onNavigate(R.id.action_modChannelsFragment_to_logoutFragment)
                }
            )
        }
    ) {

        MainModView(
            popBackStackNavigation = { popBackStackNavigation() },
            height = homeViewModel.state.value.aspectHeight,
            width = homeViewModel.state.value.width,
            density = homeViewModel.state.value.screenDensity,
            offlineModChannelList = homeViewModel.modChannelUIState.value.offlineModChannelList,
            liveModChannelList = homeViewModel.modChannelUIState.value.liveModChannelList,
            modChannelResponseState = homeViewModel.modChannelUIState.value.modChannelResponseState,
            refreshing = homeViewModel.modChannelUIState.value.modRefreshing,
            refreshFunc = {homeViewModel.pullToRefreshModChannels()},
            showNetworkMessage = homeViewModel.state.value.networkConnectionState,
            updateStreamerName = { streamerName, clientId,broadcasterId,userId->
                Log.d("mODvIEWnAVIGATION","tRANSFER")
                streamViewModel.updateChannelNameAndClientIdAndUserId(
                    streamerName,
                    clientId,
                    broadcasterId,
                    userId,
                    login = homeViewModel.validatedUser.value?.login ?:"",
                    oAuthToken= homeViewModel.oAuthToken.value ?:""
                )
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
                //
                streamViewModel.getChannelEmotes(
                    oAuthToken,
                    streamViewModel.state.value.clientId,
                    streamViewModel.state.value.broadcasterId,
                )
                //todo: I think this is where I need to make the call
                Log.d("CLickingtestingTHingy","AGAIN CLICKED")
                streamViewModel.getGlobalChatBadges(
                    oAuthToken =oAuthToken,
                    clientId = streamViewModel.state.value.clientId,
                )
                streamViewModel.getBetterTTVChannelEmotes(streamViewModel.state.value.broadcasterId)
                streamViewModel.clearAllChatters()

                modViewViewModel.getUnbanRequests(
                    oAuthToken =homeViewModel.oAuthToken.value ?:"",
                    clientId=clientId,
                    moderatorId=userId,
                    broadcasterId=broadcasterId
                )
            },

            updateClickedStreamInfo={clickedStreamInfo ->streamViewModel.updateClickedStreamInfo(clickedStreamInfo)  },
            onNavigate ={
                    destination ->onNavigate(destination)
                streamViewModel.setImmersiveMode(false)
            },
            clientId=clientId,
            userId=userId,
            networkMessageColor= Color.Red,
            networkMessage =homeViewModel.state.value.homeNetworkErrorMessage,
            showLoginModal={
                scope.launch {
                    bottomModalState.show()
                }
            },
            showNetworkRefreshError =showNetworkRefreshError,
            hapticFeedBackError={hapticFeedBackError()},
            getTopGames={
                when(searchViewModel.topGames.value){

                    is Response.Success ->{}
                    else->{
                        searchViewModel.getTopGames(
                            oAuthToken = oAuthToken,
                            clientId = clientId?:""
                        )
                    }
                }
            }


        )
    }

}