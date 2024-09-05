package com.example.clicker.presentation.home.views

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.R

import com.example.clicker.network.models.twitchRepo.StreamData


import com.example.clicker.presentation.home.disableClickAndRipple
import com.example.clicker.presentation.sharedViews.LogoutDialog
import com.example.clicker.presentation.stream.models.ClickedStreamInfo
import com.example.clicker.util.NetworkAuthResponse
import com.example.clicker.util.NetworkNewUserResponse
import com.example.clicker.util.NetworkResponse
import com.example.clicker.util.Response



    /**
     * - Implementation of [Builder.HomeModalBottomSheet].
     * - Contains 3 parts:
     * 1) [LoginWithTwitchBottomModalButton][Parts.LoginWithTwitchBottomModalButton]
     * 2) [MainScaffoldComponent][ScaffoldComponents.MainScaffoldComponent]
     * 3) [Parts.DisableForceRegister][Parts.DisableForceRegister]
     *
     * @param bottomModalState [ModalBottomSheetState] object used to determine if the Bottom modal should pop up or not
     * @param modalText a String passed to [LoginWithTwitchBottomModalButton][Parts.LoginWithTwitchBottomModalButton]
     * @param loginWithTwitch a function passed to [LoginWithTwitchBottomModalButton][Parts.LoginWithTwitchBottomModalButton]
     * @param addToLinks a function passed to [Parts.DisableForceRegister][Parts.DisableForceRegister]
     * @param onNavigate a function passed to [MainScaffoldComponent][ScaffoldComponents.MainScaffoldComponent]
     * @param quarterTotalScreenHeight a Int passed to [MainScaffoldComponent][ScaffoldComponents.MainScaffoldComponent]
     * @param streamersListLoading a value passed to [MainScaffoldComponent][ScaffoldComponents.MainScaffoldComponent]
     * @param urlList a list of [com.example.clicker.presentation.home.StreamInfo] passed to [MainScaffoldComponent][ScaffoldComponents.MainScaffoldComponent]
     * @param clientId a String passed to [MainScaffoldComponent][ScaffoldComponents.MainScaffoldComponent]
     * @param userId a String passed to [MainScaffoldComponent][ScaffoldComponents.MainScaffoldComponent]
     * @param pullToRefreshRequest a function passed to [MainScaffoldComponent][ScaffoldComponents.MainScaffoldComponent]
     * @param showFailedNetworkRequestMessage a Boolean passed to [MainScaffoldComponent][ScaffoldComponents.MainScaffoldComponent]
     * @param height a Int representing the height in a aspect ratio that will make the images look nice
     * @param width a Int representing the width in a aspect ratio that will make the images look nice
     * @param logout a function passed to [MainScaffoldComponent][ScaffoldComponents.MainScaffoldComponent]
     * @param userIsAuthenticated a Boolean passed to [MainScaffoldComponent][ScaffoldComponents.MainScaffoldComponent]
     * @param screenDensity a float meant to represent the screen density of the current device
     * */
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun HomeViewImplementation(
        bottomModalState: ModalBottomSheetState,
        loginWithTwitch: () -> Unit,
        onNavigate: (Int) -> Unit,
        updateStreamerName: (String, String, String, String) -> Unit,
        updateClickedStreamInfo:(ClickedStreamInfo)->Unit,
        followedStreamerList: NetworkNewUserResponse<List<StreamData>>,
        clientId: String,
        userId: String,
        width:Int,
        height:Int,

        userIsAuthenticated: Boolean,
        screenDensity:Float,
        homeRefreshing:Boolean,
        homeRefreshFunc:()->Unit,
        networkMessageColor:Color,
        networkMessage: String,
        showNetworkMessage:Boolean,


        logout: () -> Unit,
        logoutDialogIsOpen:Boolean,
        hideLogoutDialog:()->Unit,
        showLogoutDialog: () -> Unit,
        currentUsername:String,
        showNetworkRefreshError:Boolean,
        hapticFeedBackError:() ->Unit,
        lowPowerModeActive:Boolean,
        changeLowPowerMode:(Boolean)->Unit,

        ){

        HomeModalBottomSheetBuilder(
            loginBottomModal = {
                LoginWithTwitchBottomModalButton(
                    loginWithTwitch = { loginWithTwitch() }
                )
            },
            scaffoldHomeView ={
                HomeViewScaffold(
                    onNavigate = {id -> onNavigate(id) },
                    updateStreamerName = { streamerName, clientId,broadcasterId,userId->
                        updateStreamerName(
                            streamerName,
                            clientId,
                            broadcasterId,
                            userId
                        )
                    },
                    updateClickedStreamInfo={clickedStreamInfo ->  updateClickedStreamInfo(clickedStreamInfo)},
                    followedStreamerList = followedStreamerList,

                    clientId = clientId,
                    userId = userId,
                    height = height,
                    width = width,
                    showLogoutDialog = {
                        showLogoutDialog()
                    },
                    userIsLoggedIn =userIsAuthenticated,

                    screenDensity =screenDensity,
                    homeRefreshing =homeRefreshing,
                    homeRefreshFunc = {homeRefreshFunc()},
                    networkMessageColor =networkMessageColor,
                    networkMessage = networkMessage,
                    showNetworkMessage =showNetworkMessage,
                    bottomModalState =bottomModalState,
                    loginWithTwitch ={loginWithTwitch()},
                    showNetworkRefreshError =showNetworkRefreshError,
                    hapticFeedBackError={hapticFeedBackError()},
                    lowPowerModeActive=lowPowerModeActive,
                    changeLowPowerMode={newValue ->changeLowPowerMode(newValue)},

                )

            },

            logoutDialog ={

                    LogoutDialog(
                        logoutDialogIsOpen =logoutDialogIsOpen,
                        closeDialog = {hideLogoutDialog()},
                        logout={
                            //todo: this is where we are going to navigate to the logout UI

                            logout()
                        },
                        currentUsername =currentUsername
                    )


            },
            bottomModalState =bottomModalState,
        )


    }// end of homeViewImplementation



        /**
         * - HomeModalBottomSheetBuilder uses [Slotting](https://chrisbanes.me/posts/slotting-in-with-compose-ui/)
         * to create what a logged in user sees when they open the application
         *
         * @param loginBottomModal a composable function that will be shown on the bottom modal
         * @param scaffoldHomeView a composable function that will be covered by the bottom modal
         * @param bottomModalState the state of the [ModalBottomSheetLayout]
         * */
        @OptIn(ExperimentalMaterialApi::class)
        @Composable
        fun HomeModalBottomSheetBuilder(
            loginBottomModal:@Composable () -> Unit,
            scaffoldHomeView:@Composable () -> Unit,
            logoutDialog:@Composable () -> Unit,
            bottomModalState: ModalBottomSheetState,
        ){

            ModalBottomSheetLayout(
                sheetState = bottomModalState,
                sheetContent = { loginBottomModal() },
                content = {scaffoldHomeView()}
            )
            logoutDialog()

        }


/**

 *
 * - LoginWithTwitchBottomModalButton is the Button and text that is shown to the user when they are not logged in
 *
 * @param modalText a String that will be displayed on the button and will tell the user what the button does
 * @param loginWithTwitch a function that will be called when the Button is clicked. This button should be used
 * to log in with Twitch
 * */
@Composable
fun LoginWithTwitchBottomModalButton(
    loginWithTwitch:()->Unit
){
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
        Button(onClick = { loginWithTwitch() }) {
            Text(text = "Log out of Twitch")
        }
    }
}







