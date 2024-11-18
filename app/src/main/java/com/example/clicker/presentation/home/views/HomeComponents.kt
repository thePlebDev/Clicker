package com.example.clicker.presentation.home.views

import android.annotation.SuppressLint
import android.util.Log
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
     * - **HomeViewImplementation** is the main compose component for the [HomeFragment][com.example.clicker.presentation.home.HomeFragment].
     * - All the UI that is shown to the user on the home page is implemented in this composable
     *
     * @param bottomModalState [ModalBottomSheetState] object used to determine if the Bottom modal should pop up or not
     * @param loginWithTwitch a function, when called, will log the user out
     * @param updateStreamerName a function when called, will update the information about the stream clicked on by the user
     * and execute all of the necessary network calls to the Twitch backend
     * @param onNavigate a function when called, will navigate the user to their chosen destination within the application
     * @param updateClickedStreamInfo a function when called with a [ClickedStreamInfo] object,
     * will populate the view model with the needed [ClickedStreamInfo] information
     * @param followedStreamerList a [NetworkNewUserResponse] object representing the list of the user's followed streams
     * @param clientId a String representing the unique id that identifies this application to the Twitch servers
     * @param userId a String that uniquely identifies the user to the Twitch application servers
     * @param height a Int representing the height in a aspect ratio that will make the images look nice
     * @param width a Int representing the width in a aspect ratio that will make the images look nice
     *
     * @param userIsAuthenticated a Boolean used to determine if the user is logged in or not
     * @param screenDensity a Float meant to represent the screen density of the current device
     * @param homeRefreshing a Boolean used to determine if the user has pulled the refreshing code or not
     * @param homeRefreshFunc a function, when called, will refresh the user's home page
     *
     * @param networkMessageColor a Color object that will determine the UI for [networkMessage]
     * @param networkMessage a String used to represent a message shown to the user when there was a problem with the network
     * @param showNetworkMessage a Boolean used to determine if [networkMessage] should be shown or not
     *
     * @param logout a function when called, will log the user out of their current session
     * @param logoutDialogIsOpen a Boolean used to determine if the logout dialog should be shown to the user or not
     * @param hideLogoutDialog a function, when called, will hide the logout dialog from the user.
     * @param showLogoutDialog a function, when called, will show the logout dialog from the user
     * @param currentUsername a String used to show the username of the user currently logged in
     * @param showNetworkRefreshError a Boolean used to determine if the user should see a network related error or not
     * @param hapticFeedBackError a function, when called, will trigger haptic feedback inside of the device
     * @param lowPowerModeActive a Boolean used to determine if the user is in --low power mode-- or not
     * @param changeLowPowerMode a function, when called with a Boolean, will determine the state of [lowPowerModeActive]
     * @param getTopGames a function, when called, will make a request to the Twitch servers requesting the top games on Twitch
     * @param getPinnedList a function, when called, will query the native sql lite data base to check for any pinned games
     * @param permissionCheck a function, when called, will check to determine if the user needs certain permission or not
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
        getTopGames:()->Unit,
        getPinnedList:()->Unit,
        permissionCheck:()->Unit,
        startService:()->Unit,
        endService:()->Unit,

        ){

        HomeModalBottomSheetBuilder(
            loginBottomModal = {
                LoginWithTwitchBottomModalButtonColumn(
                    loginWithTwitch = { loginWithTwitch() }
                )
            },
            scaffoldHomeView ={
                HomeViewScaffold(
                    onNavigate = {id -> onNavigate(id) },
                    updateStreamerName = { streamerName, clientId,broadcasterId,userId->
                        Log.d("CheckingUpdatedINfor","streamerName -> $streamerName")
                        updateStreamerName(
                            streamerName,
                            clientId,
                            broadcasterId,
                            userId
                        )
                    },
                    updateClickedStreamInfo={
                            clickedStreamInfo ->
                        Log.d("CheckingUpdatedINfor","clickedStreamInfo -> ${clickedStreamInfo.channelName}")
                        updateClickedStreamInfo(clickedStreamInfo)
                                            },
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
                    getTopGames={getTopGames()},
                    getPinnedList={getPinnedList()},
                    permissionCheck={permissionCheck()},
                    startService={startService()},
                    endService={endService()}

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
         * - **HomeModalBottomSheetBuilder** uses [Slotting](https://chrisbanes.me/posts/slotting-in-with-compose-ui/)
         * to create what a logged in user sees when they open the application
         *
         * @param loginBottomModal a composable function that will be shown on the bottom modal
         * @param scaffoldHomeView a composable function that will be covered by the bottom modal
         * @param logoutDialog a composable function that will shown the user a dialog prompting them to logout from the application
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









