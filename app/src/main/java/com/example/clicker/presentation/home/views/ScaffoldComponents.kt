package com.example.clicker.presentation.home.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.DrawerValue
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.R
import com.example.clicker.presentation.home.StreamInfo
import com.example.clicker.util.PullRefreshState
import com.example.clicker.util.PullToRefreshNestedScrollConnection
import com.example.clicker.util.Response
import com.example.clicker.util.rememberNestedScrollConnection
import com.example.clicker.util.rememberPullToRefreshState
import kotlinx.coroutines.launch

object ScaffoldComponents {

    @Composable
    fun MainScaffoldComponent(
        logout:()->Unit,
        login: () -> Unit,
        userIsLoggedIn: Boolean,
        updateAuthenticatedUser:()->Unit,
        loadingPadding:Int,
        pullToRefreshRequest: (suspend () -> Unit) -> Unit,
        urlList: List<StreamInfo>?,
        urlListLoading: Response<Boolean>,
        onNavigate: (Int) -> Unit,
        updateStreamerName: (String, String, String, String) -> Unit,
        clientId:String,
        userId:String,
        height:Int,
        width:Int,
        showFailedNetworkRequestMessage: Boolean,
        quarterTotalScreenHeight:Int

        ){
        val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
        var pullColor by remember { mutableStateOf(Color.Red) }
        var request by remember { mutableStateOf(false) }
        var pullingState = rememberPullToRefreshState()
        val scope = rememberCoroutineScope()
        val nestedScrollConnection = rememberNestedScrollConnection(
            state = pullingState,
            scope = scope,
            animationMidPoint = (quarterTotalScreenHeight).toFloat(),
            quarterScreenHeight = quarterTotalScreenHeight.toFloat(),
            changeColor = { color -> pullColor = color },

            changeRequest = { boolean -> request = boolean },
            changeIsRefreshing = { boolean -> pullingState.isRefreshing = boolean }
        )

        if (userIsLoggedIn) {
            updateAuthenticatedUser()
        }
        Builder.ScaffoldBuilder(
            scaffoldState =scaffoldState,
            nestedScrollConnection =nestedScrollConnection,
            pullingState =pullingState,
            drawerContent = {
                Parts.ScaffoldDrawer(
                    logout = {
                        logout()
                    },
                    loginWithTwitch = {
                        login()
                    },
                    scaffoldState = scaffoldState,
                    userIsLoggedIn = userIsLoggedIn
                )
            },
            topBar = {
                Parts.CustomTopBar(
                    scaffoldState = scaffoldState
                )
            },
            pullDownTwoRefresh ={modifier ->
                HomeComponents.Parts.PullDownToRequest(
                    request = request,
                    changeRequest={state -> request = state},
                    modifier = modifier,
                    loadingPadding =loadingPadding,
                    pullColor =pullColor,
                    changeColor = {color -> pullColor = color},
                    pullingState = pullingState,
                    networkRequest={request ->
                        pullToRefreshRequest(request)
                    }
                )
            },
            liveChannelsLazyColumn ={
                HomeComponents.Parts.LiveChannelsLazyColumn(
                    urlList =urlList,
                    urlListLoading =urlListLoading,
                    onNavigate = {id -> onNavigate(id)},
                    updateStreamerName = {
                            streamerName, clientIds, broadcasterId, userIds ->
                        updateStreamerName(streamerName, clientIds, broadcasterId, userIds)
                    },
                    clientId =clientId,
                    userId = userId,
                    height = height,
                    width = width

                )
            },
            animatedErrorMessage ={modifier ->
                HomeComponents.Parts.AnimatedErrorMessage(
                    modifier = modifier,
                    showFailedNetworkRequestMessage =showFailedNetworkRequestMessage,
                    errorMessage =stringResource(R.string.failed_request)
                )
            }

        )
    }

    private object Builder{
        @Composable
        fun ScaffoldBuilder(
            pullingState: PullRefreshState,
            nestedScrollConnection: PullToRefreshNestedScrollConnection,
            scaffoldState: ScaffoldState,
            drawerContent:@Composable () -> Unit,
            topBar:@Composable () -> Unit,
            pullDownTwoRefresh:@Composable (modifier: Modifier) -> Unit,
            liveChannelsLazyColumn:@Composable () -> Unit,
            animatedErrorMessage:@Composable (modifier: Modifier) -> Unit,
        ){

            Scaffold(
                backgroundColor= MaterialTheme.colorScheme.primary,
                scaffoldState = scaffoldState,
                drawerContent = {
                    drawerContent()
                },
                topBar = {
                    topBar()
                }
            ) { contentPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                        .nestedScroll(nestedScrollConnection)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    //todo: move this userIsAuthenticated conditional

                    pullDownTwoRefresh(modifier = Modifier.align(Alignment.TopCenter))


                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset { IntOffset(0, pullingState.contentOffset.toInt()) }
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(start = 5.dp, end = 5.dp)

                    ) {
                        liveChannelsLazyColumn()
                        animatedErrorMessage(
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )

                    }

                }
            }
        }
    }
    private object Parts{
        @Composable
        fun ScaffoldDrawer(
            logout: () -> Unit,
            loginWithTwitch: () -> Unit,
            scaffoldState: ScaffoldState,
            userIsLoggedIn: Boolean
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)){
                if (userIsLoggedIn) {

                    HomeComponents.Parts.AccountActionCard(
                        scaffoldState,
                        accountAction = { logout() },
                        title = stringResource(R.string.logout_icon_description),
                        iconImageVector = Icons.Default.ExitToApp
                    )
                } else {
                    HomeComponents.Parts.AccountActionCard(
                        scaffoldState,
                        accountAction = { loginWithTwitch() },
                        title = stringResource(R.string.login_with_twitch),
                        iconImageVector = Icons.Default.AccountCircle
                    )
                }
            }

        }

        @Composable
        fun CustomTopBar(
            scaffoldState: ScaffoldState

        ) {
            val scope = rememberCoroutineScope()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Menu,
                        stringResource(R.string.menu_icon_description),
                        modifier = Modifier
                            .size(35.dp)
                            .clickable { scope.launch { scaffoldState.drawerState.open() } },
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                    Text(
                        stringResource(R.string.live_channels),
                        fontSize = 25.sp,
                        modifier = Modifier.padding(start = 20.dp),
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }
    }
}