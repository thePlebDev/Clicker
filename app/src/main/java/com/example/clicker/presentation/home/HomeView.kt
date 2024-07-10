package com.example.clicker.presentation.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
//import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.clicker.R
import com.example.clicker.presentation.home.views.HomeViewImplementation
import com.example.clicker.presentation.authentication.logout.LogoutViewModel
import com.example.clicker.presentation.modChannels.modVersionThree.BoxNumber
import com.example.clicker.presentation.modChannels.modVersionThree.ModVersionThree
import com.example.clicker.presentation.modChannels.modVersionThree.ModVersionThreeViewModel
import com.example.clicker.presentation.modView.ModViewDragStateViewModel
import com.example.clicker.presentation.modView.views.DraggableModViewBox
import com.example.clicker.presentation.sharedViews.ModViewScaffoldWithDrawer


import com.example.clicker.presentation.stream.AutoModViewModel
import com.example.clicker.presentation.stream.ClickedUIState
import com.example.clicker.presentation.stream.StreamViewModel
import com.example.clicker.presentation.stream.views.streamManager.ModViewScaffold
import com.example.clicker.util.WebSocketResponse

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ValidationView(
    homeViewModel: HomeViewModel,
    streamViewModel: StreamViewModel,
    logoutViewModel: LogoutViewModel,
    onNavigate: (Int) -> Unit,
    autoModViewModel: AutoModViewModel,
    updateModViewSettings:(String,String,String,String,)->Unit,
    createNewTwitchEventWebSocket:()->Unit,
    hapticFeedBackError:() ->Unit,
    modViewDragStateViewModel: ModViewDragStateViewModel,
    modVersionThreeViewModel: ModVersionThreeViewModel
) {
    val bottomModalState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)



    val userIsAuthenticated = homeViewModel.validatedUser.collectAsState().value?.clientId != null
    val userId = homeViewModel.validatedUser.collectAsState().value?.userId
    val clientId = homeViewModel.validatedUser.collectAsState().value?.clientId
    val oAuthToken = homeViewModel.state.value.oAuthToken


//
//
//    HomeViewImplementation(
//        bottomModalState =bottomModalState,
//        loginWithTwitch ={
//            logoutViewModel.setLoggedOutStatus("TRUE")
//            onNavigate(R.id.action_homeFragment_to_logoutFragment)
//                         },
//        onNavigate = {id -> onNavigate(id) },
//        updateStreamerName = { streamerName, clientId,broadcasterId,userId->
//            streamViewModel.getBetterTTVGlobalEmotes()
//            streamViewModel.updateChannelNameAndClientIdAndUserId(
//                streamerName,
//                clientId,
//                broadcasterId,
//                userId,
//                login =homeViewModel.validatedUser.value?.login ?:""
//            )
//            autoModViewModel.updateAutoModCredentials(
//                oAuthToken = homeViewModel.state.value.oAuthToken,
//                clientId = streamViewModel.state.value.clientId,
//                moderatorId = streamViewModel.state.value.userId,
//                broadcasterId = streamViewModel.state.value.broadcasterId,
//            )
//            updateModViewSettings(
//                homeViewModel.state.value.oAuthToken,
//                streamViewModel.state.value.clientId,
//                streamViewModel.state.value.broadcasterId,
//                streamViewModel.state.value.userId,
//            )
//            createNewTwitchEventWebSocket()
//            streamViewModel.getChannelEmotes(
//                homeViewModel.state.value.oAuthToken,
//                streamViewModel.state.value.clientId,
//                streamViewModel.state.value.broadcasterId,
//            )
//            streamViewModel.getGlobalChatBadges(
//                oAuthToken =homeViewModel.state.value.oAuthToken,
//                clientId = streamViewModel.state.value.clientId,
//            )
//            streamViewModel.getBetterTTVChannelEmotes(streamViewModel.state.value.broadcasterId)
//
//        },
//        updateClickedStreamInfo={
//            //todo: THIS IS WHAT I NEED TO UPDATE
//                clickedStreamInfo ->streamViewModel.updateClickedStreamInfo(clickedStreamInfo)
//                                },
//        followedStreamerList = homeViewModel.state.value.streamersListLoading,
//        clientId = clientId ?: "",
//        userId = userId ?: "",
//        height = homeViewModel.state.value.aspectHeight,
//        width = homeViewModel.state.value.width,
//        logout = {
//
//            logoutViewModel.setNavigateHome(false)
////            logoutViewModel.setLoggedOutStatus("TRUE")
//            logoutViewModel.logout(
//                clientId = clientId?:"",
//                oAuthToken = oAuthToken
//            )
//            homeViewModel.hideLogoutDialog()
//            onNavigate(R.id.action_homeFragment_to_logoutFragment)
//
//        },
//        userIsAuthenticated =userIsAuthenticated,
//        screenDensity = homeViewModel.state.value.screenDensity,
//        homeRefreshing =homeViewModel.state.value.homeRefreshing,
//        homeRefreshFunc = {homeViewModel.pullToRefreshGetLiveStreams()},
//        networkMessageColor=Color.Red,
//        networkMessage =homeViewModel.state.value.homeNetworkErrorMessage,
//        showNetworkMessage = homeViewModel.state.value.networkConnectionState,
//        logoutDialogIsOpen =homeViewModel.state.value.logoutDialogIsOpen,
//        hideLogoutDialog ={homeViewModel.hideLogoutDialog()},
//        showLogoutDialog ={homeViewModel.showLogoutDialog()},
//        currentUsername = homeViewModel.validatedUser.collectAsState().value?.login ?: "Username not found",
//        showNetworkRefreshError = homeViewModel.state.value.showNetworkRefreshError,
//        hapticFeedBackError={hapticFeedBackError()}
//
//    )
    val stateList = modVersionThreeViewModel.publicStateList.collectAsState()
    ModVersionThree(
        boxOneOffsetY = modVersionThreeViewModel.boxOneOffsetY,
        setBoxOneOffset = {newValue ->modVersionThreeViewModel.setBoxOneOffset(newValue)},
        boxOneDragState = modVersionThreeViewModel.boxOneDragState,
        boxOneSection = modVersionThreeViewModel.boxOneSection,
        boxOneIndex=modVersionThreeViewModel.boxOneIndex,
        boxOneDragging = modVersionThreeViewModel.boxesDragging.value.boxOneDragging,
        setBoxOneDragging = {
                newValue ->
            Log.d("LoggingTheDragging","ONE")
            modVersionThreeViewModel.setBoxOneDragging(newValue)
                            },
        setBoxOneIndex ={newValue -> modVersionThreeViewModel.syncBoxOneIndex(newValue)},
        deleteBoxOne= modVersionThreeViewModel.deleteBoxOne,
        boxOneHeight = modVersionThreeViewModel.boxOneHeight,

        /*************** BOX TWO PARAMETERS***************************************************************/
        boxTwoOffsetY=modVersionThreeViewModel.boxTwoOffsetY,
        setBoxTwoOffset= {newValue ->modVersionThreeViewModel.setBoxTwoOffset(newValue)},
        boxTwoDragState= modVersionThreeViewModel.boxTwoDragState,
        boxTwoSection= modVersionThreeViewModel.boxTwoSection,
        boxTwoIndex= modVersionThreeViewModel.boxTwoIndex,
        boxTwoDragging = modVersionThreeViewModel.boxesDragging.value.boxTwoDragging,
        setBoxTwoDragging = {newValue -> modVersionThreeViewModel.setBoxTwoDragging(newValue)},
        setBoxTwoIndex ={newValue ->
            Log.d("LoggingTheDragging","TWO")
            modVersionThreeViewModel.syncBoxTwoIndex(newValue)
                        },
        deleteBoxTwo= modVersionThreeViewModel.deleteBoxTwo,
        boxTwoHeight = modVersionThreeViewModel.boxTwoHeight,

        /*************** BOX THREE PARAMETERS*****************************************************************/
        boxThreeOffsetY=modVersionThreeViewModel.boxThreeOffsetY,
        setBoxThreeOffset= {newValue ->modVersionThreeViewModel.setBoxThreeOffset(newValue)},
        boxThreeDragState= modVersionThreeViewModel.boxThreeDragState,
        boxThreeSection= modVersionThreeViewModel.boxThreeSection,
        boxThreeIndex= modVersionThreeViewModel.boxThreeIndex,
        boxThreeDragging = modVersionThreeViewModel.boxesDragging.value.boxThreeDragging,
        setBoxThreeDragging = {newValue -> modVersionThreeViewModel.setBoxThreeDragging(newValue)},
        setBoxThreeIndex ={newValue ->
            Log.d("LoggingTheDragging","THREE")
            modVersionThreeViewModel.syncBoxThreeIndex(newValue)
                          },
        deleteBoxThree= modVersionThreeViewModel.deleteBoxThree,
        boxThreeHeight = modVersionThreeViewModel.boxThreeHeight,

        /*************** GENERICS PARAMETERS*****************************************************************/
        updateIndex={newValue -> modVersionThreeViewModel.setIndex(newValue)},
        showError =modVersionThreeViewModel.showPlacementError.value,
        sectionTwoHeight = modVersionThreeViewModel.section2height,
        sectionThreeHeight=modVersionThreeViewModel.section3Height

    )







}/******END OF THE VALIDATION VIEW********/




fun Modifier.disableClickAndRipple(): Modifier = composed {
    clickable(
        enabled = false,
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = { }
    )
}









