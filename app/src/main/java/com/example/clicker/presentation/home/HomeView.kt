package com.example.clicker.presentation.home

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.clicker.R
import com.example.clicker.presentation.authentication.AuthenticationViewModel
import com.example.clicker.presentation.home.views.HomeComponents.AccountActionCard
import com.example.clicker.presentation.home.views.HomeComponents.DisableForceRegister
import com.example.clicker.presentation.home.views.HomeComponents.EmptyFollowingList
import com.example.clicker.presentation.home.views.HomeComponents.GettingStreamsError
import com.example.clicker.presentation.home.views.HomeComponents.HomeImplementationScaffold
import com.example.clicker.presentation.home.views.HomeComponents.LoginWithTwitchBottomModalButton
import com.example.clicker.presentation.stream.StreamViewModel
import com.example.clicker.util.PullRefreshState
import com.example.clicker.util.Response
import com.example.clicker.util.rememberNestedScrollConnection
import com.example.clicker.util.rememberPullToRefreshState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ValidationView(
    homeViewModel: HomeViewModel,
    streamViewModel: StreamViewModel,
    authenticationViewModel: AuthenticationViewModel,
    loginWithTwitch: () -> Unit,
    onNavigate: (Int) -> Unit,
    addToLinks: () -> Unit,
    quarterTotalScreenHeight:Int,
    loadingPadding: Int,
) {
    val bottomModalState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    val modalText = authenticationViewModel.authenticationUIState.value.modalText
    val showModalState = authenticationViewModel.authenticationUIState.value.showLoginModal
    val domainIsRegistered = homeViewModel.state.value.domainIsRegistered
    if (showModalState) {
        LaunchedEffect(bottomModalState) { // the key define when the block is relaunched
            // Your coroutine code here
            scope.launch {
                bottomModalState.show()
            }
        }
    } else {
        LaunchedEffect(bottomModalState) { // the key define when the block is relaunched
            // Your coroutine code here
            scope.launch {
                bottomModalState.hide()
            }
        }
    }

    val userIsAuthenticated = authenticationViewModel.authenticationUIState.value.authenticated
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))

    ModalBottomSheetLayout(
        sheetState = bottomModalState,
        sheetContent = {
            LoginWithTwitchBottomModalButton(
                modalText =modalText,
                loginWithTwitch ={loginWithTwitch()}
            )
        }
    ) {
        HomeImplementationScaffold(
            logout = {
                authenticationViewModel.beginLogout(
                    clientId = authenticationViewModel.authenticationUIState.value.clientId,
                    oAuthToken = authenticationViewModel.authenticationUIState.value.authenticationCode
                )
            },
            loginWithTwitch = {
                loginWithTwitch()
            },
            scaffoldState = scaffoldState,
            userIsAuthenticated =userIsAuthenticated,
            updateAuthenticatedUser={
                val certifiedUser = authenticationViewModel.validatedUser()
                homeViewModel.updateAuthenticatedUser(certifiedUser)
            }

        ){
            HomeView(
                onNavigate,
                quarterTotalScreenHeight,
                loadingPadding,
                updateStreamerName = { streamerName, clientId,broadcasterId,userId->
                    streamViewModel.updateChannelNameAndClientIdAndUserId(
                        streamerName,
                        clientId,
                        broadcasterId,
                        userId
                    )
                },
                urlListLoading = homeViewModel.state.value.streamersListLoading,
                urlList =homeViewModel.newUrlList.collectAsState().value,
                clientId = homeViewModel.authenticatedUser.value?.clientId ?: "",
                userId = homeViewModel.authenticatedUser.value?.userId ?: "",
                pullToRefreshRequest ={
                        resetUI: suspend () -> Unit ->
                    homeViewModel.pullToRefreshGetLiveStreams(
                        resetUI = resetUI
                    )
                },
                showFailedNetworkRequestMessage = homeViewModel.state.value.failedNetworkRequest,
                height = homeViewModel.state.value.aspectHeight,
                width = homeViewModel.state.value.width,


            )
        }
    }

    if (!domainIsRegistered) {
        DisableForceRegister(
            addToLinks = { addToLinks() }
        )
    }
}



//todo: this is going to be my top level implementation
@SuppressLint("SuspiciousIndentation")
@Composable
fun HomeView(
    onNavigate: (Int) -> Unit,
    quarterTotalScreenHeight:Int,
    loadingPadding: Int,
    updateStreamerName: (String, String, String, String) -> Unit,
    urlListLoading: Response<Boolean>,
    urlList: List<StreamInfo>?,
    clientId: String,
    userId:String,
    pullToRefreshRequest: (suspend () -> Unit) -> Unit,
    showFailedNetworkRequestMessage: Boolean,
    height: Int,
    width:Int,

) {
    // todo: home pager page goes here
    UrlImages(
        urlList = urlList,
        onNavigate = { onNavigate(R.id.action_homeFragment_to_streamFragment) },
        updateStreamerName = {
                streamerName, clientIds, broadcasterId, userIds ->

            updateStreamerName(streamerName, clientIds, broadcasterId, userIds)
        },
        clientId = clientId,
        userId = userId,
        networkRequest = {
                resetUI: suspend () -> Unit ->
            pullToRefreshRequest(resetUI)

        },
        showFailedNetworkRequestMessage = showFailedNetworkRequestMessage,
        height = height,
        width = width,
        urlListLoading = urlListLoading,
        quarterTotalScreenHeight = quarterTotalScreenHeight,
        loadingPadding = loadingPadding
    )
} // END OF THE HOME VIEW

@OptIn(ExperimentalFoundationApi::class)
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





/**
 * This is the composable that shows the loading images and the actual images that are shown to the user to click on
 * */
@Composable
fun UrlImages(
    urlList: List<StreamInfo>?,
    onNavigate: (Int) -> Unit,
    updateStreamerName: (String, String, String, String) -> Unit,
    clientId: String,
    userId: String,
    networkRequest: (suspend () -> Unit) -> Unit,
    showFailedNetworkRequestMessage: Boolean,
    height: Int,
    width: Int,
    quarterTotalScreenHeight:Int,
    loadingPadding: Int,
    urlListLoading: Response<Boolean>
) {
    val scope = rememberCoroutineScope()
    val initialColor = colorResource(R.color.red)

    var pullColor by remember { mutableStateOf(initialColor) }
    val configuration = LocalConfiguration.current
    val another = 5.dp

    //val quarterTotalScreenHeight = configuration.screenHeightDp / 8 //todo: calculation should be done outside of compose

    var request by remember { mutableStateOf(false) }
    var pullingState = rememberPullToRefreshState()

    val nestedScrollConnection = rememberNestedScrollConnection(
        state = pullingState,
        scope = scope,
        animationMidPoint = (quarterTotalScreenHeight).toFloat(),
        quarterScreenHeight = quarterTotalScreenHeight.toFloat(),
        changeColor = { color -> pullColor = color },

        changeRequest = { boolean -> request = boolean },
        changeIsRefreshing = { boolean -> pullingState.isRefreshing = boolean }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
            .background(MaterialTheme.colorScheme.primary)

    ) {
        PullDownToRequest(
            request = request,
            changeRequest={state -> request = state},
            modifier = Modifier.align(Alignment.TopCenter),
            loadingPadding =loadingPadding,
            pullColor =pullColor,
            changeColor = {color -> pullColor = color},
            pullingState = pullingState,
            networkRequest={request ->
                networkRequest(request)

            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(0, pullingState.contentOffset.toInt()) }
                .background(MaterialTheme.colorScheme.primary)
                .padding(start = 5.dp, end = 5.dp)

        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                when (urlListLoading) {
                    is Response.Loading -> {
                        item {
                            //todo:This is its own item
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(40.dp),
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                    is Response.Success -> {
                        if (urlList != null) {

                            if (urlList.isEmpty()) {
                                item {
                                    EmptyFollowingList()
                                }
                            }

                            items(urlList,key = { streamItem -> streamItem.broadcasterId }) { streamItem ->
                                LiveChannelRowItem(
                                    updateStreamerName ={
                                            streamerName,clientId,broadcasterId,userId ->
                                        updateStreamerName(streamerName,clientId,broadcasterId,userId)

                                    },
                                    streamItem = streamItem,
                                    clientId =clientId,
                                    userId = userId,
                                    height = height,
                                    width = width,
                                    onNavigate = {id -> onNavigate(id)}
                                )
//
                            }
                            // end of the lazy column
                        }
                    }
                    is Response.Failure -> {
                        item {
                            GettingStreamsError()
                        }
                    }
                }
            }

            // apparently this is the code I am using to make the message disappear
            AnimatedErrorMessage(
                modifier = Modifier.align(Alignment.BottomCenter),
                showFailedNetworkRequestMessage =showFailedNetworkRequestMessage,
                errorMessage =stringResource(R.string.failed_request)
            )
        }
    }
}

@Composable
fun PullDownToRequest(
    request:Boolean,
    changeRequest:(Boolean)->Unit,
    modifier: Modifier,
    loadingPadding: Int,
    pullColor: Color,
    changeColor:(Color)->Unit,
    pullingState: PullRefreshState,
    networkRequest: (suspend () -> Unit) -> Unit,
){
    if (request) {
        // then we can also make the request here
        //todo: make this into its own loading request
        CircularProgressIndicator(
            modifier = modifier
                .padding(top = (loadingPadding).dp), //todo: calculation should be done outside of compose
            color = androidx.compose.material3.MaterialTheme.colorScheme.secondary
        )
        networkRequest {
            pullingState.dispatchToResting()
            pullingState.isRefreshing = false
            changeRequest(false)
            changeColor(Color.White)

        }
    } else {
        Icon(
            Icons.Filled.KeyboardArrowDown,
            stringResource(R.string.keyboard_arrow_down_description),
            modifier = modifier
                .size(80.dp)
                .offset { IntOffset(0, pullingState.contentOffset.toInt() - 140) },//todo: calculation should be done outside of compose
            tint = pullColor

        )
    }
}
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
fun LiveChannelRowItem(
    updateStreamerName: (String, String, String, String) -> Unit,
    streamItem: StreamInfo,
    clientId: String,
    userId:String,
    onNavigate: (Int) -> Unit,
    height: Int,
    width: Int


){
    Row(
        modifier = Modifier.clickable {
            updateStreamerName(
                streamItem.streamerName,
                clientId,
                streamItem.broadcasterId,
                userId
            )
            onNavigate(R.id.action_homeFragment_to_streamFragment)
        }
    ){
        ImageWithViewCount(
            url =streamItem.url,
            height = height,
            width= width,
            viewCount =streamItem.views
        )
        StreamTitleWithInfo(
            streamerName =streamItem.streamerName,
            streamTitle =streamItem.streamTitle,
            gameTitle = streamItem.gameTitle
        )

    }
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
    )
}

@Composable
fun StreamTitleWithInfo(
    streamerName:String,
    streamTitle:String,
    gameTitle:String
){
    Column(modifier = Modifier.padding(start = 10.dp)) {
        Text(
            streamerName,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            streamTitle,
            fontSize = 15.sp,
            modifier = Modifier.alpha(0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            gameTitle,
            fontSize = 15.sp,
            modifier = Modifier.alpha(0.7f),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}
@Composable
fun ImageWithViewCount(
    url: String,
    height: Int,
    width: Int,
    viewCount:Int,
){
    Box() {

        SubcomposeAsyncImage(
            model = url,
            loading = {
                Card(
                    modifier = Modifier
                        .height((height / 2.8).dp)
                        .width((width / 2.8).dp),
                    backgroundColor = MaterialTheme.colorScheme.primary
                ) {
                }
            },
            contentDescription = stringResource(R.string.sub_compose_async_image_description)
        )
        Text(
            "${viewCount}",
            style = TextStyle(
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold
            ),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(5.dp)
        )
    }
}






fun Modifier.disableClickAndRipple(): Modifier = composed {
    clickable(
        enabled = false,
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = { }
    )
}
