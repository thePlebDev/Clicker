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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.clicker.R


import com.example.clicker.presentation.home.StreamInfo
import com.example.clicker.presentation.home.disableClickAndRipple
import com.example.clicker.presentation.home.views.ScaffoldComponents.MainScaffoldComponent
import com.example.clicker.util.PullRefreshState
import com.example.clicker.util.PullToRefreshNestedScrollConnection
import com.example.clicker.util.Response
import com.example.clicker.util.rememberNestedScrollConnection
import com.example.clicker.util.rememberPullToRefreshState
import kotlinx.coroutines.launch
/**
 * - Contains 1 implementation:
 * 1) [HomeViewImplementation]
 *
 * - HomeComponents represents a combination of [ScaffoldComponents] implementation and [HomeComponents] to
 * create the entire HomeView
 * */
object HomeComponents {

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
     * @param updateAuthenticatedUser a function passed to [MainScaffoldComponent][ScaffoldComponents.MainScaffoldComponent]
     * */
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun HomeViewImplementation(
        bottomModalState: ModalBottomSheetState,
        modalText: String,
        loginWithTwitch: () -> Unit,
        domainIsRegistered: Boolean,
        addToLinks: () -> Unit,
        onNavigate: (Int) -> Unit,
        quarterTotalScreenHeight: Int,
        updateStreamerName: (String, String, String, String) -> Unit,
        streamersListLoading: Response<Boolean>,
        urlList: List<StreamInfo>?,
        clientId: String,
        userId: String,
        pullToRefreshRequest: (suspend () -> Unit) -> Unit,
        showFailedNetworkRequestMessage: Boolean,
        width:Int,
        height:Int,
        logout: () -> Unit,
        userIsAuthenticated: Boolean,
        updateAuthenticatedUser: () -> Unit

    ){
        Builder.HomeModalBottomSheetBuilder(
            loginBottomModal = {
                Parts.LoginWithTwitchBottomModalButton(
                    modalText = modalText,
                    loginWithTwitch = { loginWithTwitch() }
                )
            },
            scaffoldHomeView ={
                ScaffoldComponents.MainScaffoldComponent(
                    onNavigate = {id -> onNavigate(id) },
                    updateStreamerName = { streamerName, clientId,broadcasterId,userId->
                        updateStreamerName(
                            streamerName,
                            clientId,
                            broadcasterId,
                            userId
                        )
                    },
                    urlListLoading = streamersListLoading,
                    urlList =urlList,
                    clientId = clientId,
                    userId = userId,
                    pullToRefreshRequest ={
                            resetUI: suspend () -> Unit ->
                        pullToRefreshRequest(resetUI)
                    },
                    quarterTotalScreenHeight =quarterTotalScreenHeight,
                    showFailedNetworkRequestMessage = showFailedNetworkRequestMessage,
                    height = height,
                    width = width,
                    logout = {
                        logout()
                    },
                    login = {
                        loginWithTwitch()
                    },
                    userIsLoggedIn =userIsAuthenticated,
                    updateAuthenticatedUser={
                        updateAuthenticatedUser()
                    },

                )

            },
            forceRegisterLinks ={
                Parts.DisableForceRegister(
                    addToLinks = { addToLinks() }
                )
            },
            bottomModalState =bottomModalState,
            domainIsRegistered =domainIsRegistered
        )
    }


    /**
     * Below are the layout builders of the HomeView. Meaning they will act as the layout guidelines for specific parts
     * of the `HomeView`
     * */
    private object Builder{

        /**
         * - HomeModalBottomSheetBuilder is used inside of  [HomeViewImplementation].
         *
         *
         * @param loginBottomModal a composable function that will be shown on the bottom modal
         * @param scaffoldHomeView a composable function that will be covered by the bottom modal
         * @param forceRegisterLinks a composable function that will be shown when their is no deep links registered
         * @param bottomModalState the state of the [ModalBottomSheetLayout]
         * @param domainIsRegistered a boolean determining if [forceRegisterLinks] should be shown or not
         * */
        @OptIn(ExperimentalMaterialApi::class)
        @Composable
        fun HomeModalBottomSheetBuilder(
            loginBottomModal:@Composable () -> Unit,
            scaffoldHomeView:@Composable () -> Unit,
            forceRegisterLinks:@Composable () -> Unit,
            bottomModalState: ModalBottomSheetState,
            domainIsRegistered: Boolean
        ){
            ModalBottomSheetLayout(
                sheetState = bottomModalState,
                sheetContent = {
                    loginBottomModal()
                }
            ) {

                scaffoldHomeView()
            }
            if (!domainIsRegistered) {
                forceRegisterLinks()
            }


        }


    }

    /**
     * The components inside of [Parts] represent all the composables that make up the the `HomeView` experience
     * */
     object Parts{


        /**
         * - Contains 0 extra parts
         *
         * - AnimatedErrorMessage is an animated Error message that will only be shown to the user where an error from fetching
         * the network occurs
         *
         * @param modifier a modifier used to determine where this composable should be displayed
         * @param showFailedNetworkRequestMessage a Boolean used to determine if the error message should show or not.
         * @param errorMessage a String displaying the actual error message
         * */
        @Composable
        fun AnimatedErrorMessage(
            modifier: Modifier,
            showFailedNetworkRequestMessage: Boolean,
            errorMessage:String
        ){
            AnimatedVisibility(
                visible = showFailedNetworkRequestMessage,
                modifier = modifier
                    .padding(5.dp)

            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp),
                    elevation = 10.dp,
                    backgroundColor = Color.LightGray
                ) {
                    Text(
                        errorMessage,
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        color = Color.Red,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }



        /**
         * - Contains 0 extra parts
         *
         * - LoginWithTwitchBottomModalButton is the Button and text that is shown to the user when they are not logged in
         *
         * @param modalText a String that will be displayed on the button and will tell the user what the button does
         * @param loginWithTwitch a function that will be called when the Button is clicked. This button should be used
         * to log in with Twitch
         * */
        @Composable
        fun LoginWithTwitchBottomModalButton(
            modalText:String,
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
                    modalText,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 30.sp,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Button(onClick = { loginWithTwitch() }) {
                    Text(text = stringResource(R.string.login_with_twitch))
                }
            }
        }

        /**
         * - Contains 0 extra parts
         *
         * - DisableForceRegister is shown to the user if they are on Android 12 or up and have not validated the deep link
         *
         * @param addToLinks a function used to send the user to validate the deep link
         * */
        @Composable
        fun DisableForceRegister(
            addToLinks: () -> Unit
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Spacer(
                    modifier = Modifier
                        .disableClickAndRipple()
                        .background(
                            color = Color.Gray.copy(alpha = .7f)
                        )
                        .matchParentSize()
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                        .align(Alignment.Center)
                        .clickable { },
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    elevation = 10.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            stringResource(R.string.you_must_add),
                            fontSize = 25.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            stringResource(R.string.package_name),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            stringResource(R.string.enable_login_with_Android_12),
                            fontSize = 25.sp,
                            color = MaterialTheme.colorScheme.onPrimary,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { addToLinks() },
                            modifier = Modifier.padding(top = 20.dp, bottom = 20.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text(text =stringResource(R.string.add_to_links), fontSize = 25.sp, color = MaterialTheme.colorScheme.onSecondary)
                        }
                    }
                }
            } // end of the box
        }





    }//end of parts


    //parts







}