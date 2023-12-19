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

object HomeComponents {

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
        Builder.HomeModalBottomSheet(
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

        @OptIn(ExperimentalMaterialApi::class)
        @Composable
        fun HomeModalBottomSheet(
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