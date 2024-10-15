package com.example.clicker.presentation.home

import android.content.Context
import android.graphics.PixelFormat
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Text
//import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.clicker.R
import com.example.clicker.farmingGame.GL2JNIView
import com.example.clicker.farmingGame.NewTestingGLSurfaceView
import com.example.clicker.presentation.home.views.HomeViewImplementation
import com.example.clicker.presentation.authentication.logout.LogoutViewModel

import com.example.clicker.presentation.modChannels.modVersionThree.ModVersionThreeViewModel
import com.example.clicker.presentation.modView.ModViewViewModel


import com.example.clicker.presentation.stream.AutoModViewModel
import com.example.clicker.presentation.stream.GLSurfaceViewComposable
import com.example.clicker.presentation.stream.StreamViewModel
import com.example.clicker.presentation.stream.views.chat.chatSettings.ChatSettingsViewModel
import com.example.clicker.presentation.streamInfo.StreamInfoViewModel


@Composable
fun TestingGLSurfaceViewComposable(
    context: Context,
    modifier:Modifier
) {
    AndroidView(
        factory = {
            GL2JNIView(context)
        },
        modifier = modifier
    )
}

@Composable
fun TestingGLSurfaceViewUnderstandingTriangle(
    context: Context,
    modifier:Modifier
) {
    AndroidView(
        factory = {
            NewTestingGLSurfaceView(context)
        },
        modifier = modifier
    )
}
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ValidationView(
    homeViewModel: HomeViewModel,
    streamViewModel: StreamViewModel,
    streamInfoViewModel: StreamInfoViewModel,
    chatSettingsViewModel:ChatSettingsViewModel,
    logoutViewModel: LogoutViewModel,
    onNavigate: (Int) -> Unit,
    autoModViewModel: AutoModViewModel,
    updateModViewSettings:(String,String,String,String,)->Unit,
    createNewTwitchEventWebSocket:()->Unit,
    hapticFeedBackError:() ->Unit,

    modVersionThreeViewModel: ModVersionThreeViewModel,
    modViewViewModel: ModViewViewModel
) {
    val bottomModalState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)



    val userIsAuthenticated = homeViewModel.validatedUser.collectAsState().value?.clientId != null
    val userId = homeViewModel.validatedUser.collectAsState().value?.userId
    val clientId = homeViewModel.validatedUser.collectAsState().value?.clientId
    val oAuthToken = homeViewModel.oAuthToken.collectAsState().value ?:""
    val lowPowerModeActive = streamViewModel.lowPowerModeActive.value
    val context = LocalContext.current

//    Column(Modifier.background(Color.DarkGray)) {
//        Text(
//            "TEST TEXT",
//            color = Color.Blue,
//            fontSize = 40.sp,
//            modifier = Modifier.fillMaxWidth().background(Color.Red)
//        )
//        Box(modifier = Modifier.fillMaxWidth()) {
//            // OpenGL surface behind the text
//
//            // Text on top of OpenGL surface
//            Text(
//                "THIS IS A TESTING TO SEE IF THE TRIANGLE COVERS UP THIS TEXT AND TO SEE WHAT IT LOOKS LIKE",
//                color = Color.Blue,
//                fontSize = 40.sp
//            )
////            TestingGLSurfaceViewComposable(context,Modifier.matchParentSize())
//        }
//        Text(
//            "TEST TEXT",
//            color = Color.Blue,
//            fontSize = 40.sp,
//            modifier = Modifier.fillMaxWidth().background(Color.Red)
//        )
//
//    }
   // TestingGLSurfaceViewComposable(context,Modifier.fillMaxSize())
    //TestingGLSurfaceViewUnderstandingTriangle(context,Modifier.fillMaxSize())


    HomeViewImplementation(
        bottomModalState =bottomModalState,
        loginWithTwitch ={
            logoutViewModel.setLoggedOutStatus("TRUE")
            onNavigate(R.id.action_homeFragment_to_logoutFragment)
                         },
        onNavigate = {id -> onNavigate(id) },
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
        updateClickedStreamInfo={
            //todo: THIS IS WHAT I NEED TO UPDATE
                clickedStreamInfo ->streamViewModel.updateClickedStreamInfo(clickedStreamInfo)
                                },
        followedStreamerList = homeViewModel.state.value.streamersListLoading,
        clientId = clientId ?: "",
        userId = userId ?: "",
        height = homeViewModel.state.value.aspectHeight,
        width = homeViewModel.state.value.width,
        logout = {

            logoutViewModel.setNavigateHome(false)
//            logoutViewModel.setLoggedOutStatus("TRUE")
            logoutViewModel.logout(
                clientId = clientId?:"",
                oAuthToken = oAuthToken
            )
            homeViewModel.hideLogoutDialog()
            onNavigate(R.id.action_homeFragment_to_logoutFragment)

        },
        userIsAuthenticated =userIsAuthenticated,
        screenDensity = homeViewModel.state.value.screenDensity,
        homeRefreshing =homeViewModel.state.value.homeRefreshing,
        homeRefreshFunc = {homeViewModel.pullToRefreshHome()},
        networkMessageColor=Color.Red,
        networkMessage =homeViewModel.state.value.homeNetworkErrorMessage,
        showNetworkMessage = homeViewModel.state.value.networkConnectionState,
        logoutDialogIsOpen =homeViewModel.state.value.logoutDialogIsOpen,
        hideLogoutDialog ={homeViewModel.hideLogoutDialog()},
        showLogoutDialog ={homeViewModel.showLogoutDialog()},
        currentUsername = homeViewModel.validatedUser.collectAsState().value?.login ?: "Username not found",
        showNetworkRefreshError = homeViewModel.state.value.showNetworkRefreshError,
        hapticFeedBackError={hapticFeedBackError()},
        lowPowerModeActive=lowPowerModeActive,
        changeLowPowerMode={newValue ->streamViewModel.changeLowPowerModeActive(newValue)},

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









