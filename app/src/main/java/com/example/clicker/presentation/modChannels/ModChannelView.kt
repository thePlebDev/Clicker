package com.example.clicker.presentation.modChannels

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.R
import com.example.clicker.presentation.home.HomeViewModel
import com.example.clicker.presentation.home.StreamInfo
import com.example.clicker.presentation.authentication.logout.LogoutViewModel
import com.example.clicker.presentation.modChannels.views.ModChannelComponents
import com.example.clicker.presentation.sharedViews.ButtonScope
import com.example.clicker.presentation.stream.AutoModViewModel
import com.example.clicker.presentation.stream.StreamViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModChannelView(
    homeViewModel: HomeViewModel,
    streamViewModel: StreamViewModel,
    autoModViewModel:AutoModViewModel,
    popBackStackNavigation: () -> Unit,
    onNavigate: (Int) -> Unit,
    updateModViewSettings:(String,String,String,String,)->Unit,
    createNewTwitchEventWebSocket:()->Unit,
    hapticFeedBackError:() ->Unit,
    logoutViewModel: LogoutViewModel,
){
    val bottomModalState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()


    val userId = homeViewModel.validatedUser.collectAsState().value?.userId ?:""
    val clientId = homeViewModel.validatedUser.collectAsState().value?.clientId ?:""
    val showNetworkRefreshError:Boolean = homeViewModel.state.value.showNetworkRefreshError




    ModalBottomSheetLayout(
        sheetState = bottomModalState,
        sheetContent = {
            BottomModalSheetContent(
                loginWithTwitch= {
                    logoutViewModel.setLoggedOutStatus("TRUE")
                    onNavigate(R.id.action_modChannelsFragment_to_logoutFragment)
                }
            )
        }
    ) {

        ModChannelComponents.MainModView(
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
                streamViewModel.updateChannelNameAndClientIdAndUserId(
                    streamerName,
                    clientId,
                    broadcasterId,
                    userId,
                    login = homeViewModel.validatedUser.value?.login ?:""
                )
                autoModViewModel.updateAutoModCredentials(
                    oAuthToken = homeViewModel.state.value.oAuthToken,
                    clientId = streamViewModel.state.value.clientId,
                    moderatorId = streamViewModel.state.value.userId,
                    broadcasterId = streamViewModel.state.value.broadcasterId,
                )
                updateModViewSettings(
                    homeViewModel.state.value.oAuthToken,
                    streamViewModel.state.value.clientId,
                    streamViewModel.state.value.broadcasterId,
                    streamViewModel.state.value.userId,
                )
                createNewTwitchEventWebSocket()
                //
                streamViewModel.getChannelEmotes(
                    homeViewModel.state.value.oAuthToken,
                    streamViewModel.state.value.clientId,
                    streamViewModel.state.value.broadcasterId,
                )
                //todo: I think this is where I need to make the call
                Log.d("CLickingtestingTHingy","AGAIN CLICKED")
                streamViewModel.getGlobalChatBadges(
                    oAuthToken =homeViewModel.state.value.oAuthToken,
                    clientId = streamViewModel.state.value.clientId,
                )
                streamViewModel.getBetterTTVChannelEmotes(streamViewModel.state.value.broadcasterId)
                streamViewModel.clearAllChatters()
                                 },
            updateClickedStreamInfo={clickedStreamInfo ->streamViewModel.updateClickedStreamInfo(clickedStreamInfo)  },
            onNavigate ={
                    destination ->onNavigate(destination)
                        },
            clientId=clientId,
            userId=userId,
            networkMessageColor=Color.Red,
            networkMessage =homeViewModel.state.value.homeNetworkErrorMessage,
            showLoginModal={
                scope.launch {
                    bottomModalState.show()
                }
            },
            showNetworkRefreshError =showNetworkRefreshError,
            hapticFeedBackError={hapticFeedBackError()}


        )
    }

}

@Composable
fun BottomModalSheetContent(
    loginWithTwitch: () -> Unit,
){
    val fontSize =MaterialTheme.typography.headlineSmall.fontSize
    val buttonScope = remember(){ ButtonScope(fontSize) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(MaterialTheme.colorScheme.primary),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Log out to be issued a new Twitch authentication token",
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
            modifier = Modifier
                .padding(bottom = 10.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        with(buttonScope){
            this.Button(
                text ="Log out of Twitch",
                onClick = { loginWithTwitch()},
            )
        }
    }
}