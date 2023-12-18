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
        loadingPadding: Int,
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
                    loadingPadding =loadingPadding,
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
        fun LiveChannelsLazyColumn(
            urlList: List<StreamInfo>?,
            urlListLoading: Response<Boolean>,
            onNavigate: (Int) -> Unit,
            updateStreamerName: (String, String, String, String) -> Unit,
            clientId: String,
            userId: String,
            height: Int,
            width: Int,


            ){
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
                                    color = MaterialTheme.colorScheme.secondary
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
                    color = MaterialTheme.colorScheme.secondary
                )
                networkRequest {
                    pullingState.dispatchToResting()
                    pullingState.isRefreshing = false
                    changeRequest(false)
                    changeColor(Color.Red)

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
        fun GettingStreamsError() {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
                    .clickable { },
                elevation = 10.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = stringResource(R.string.pull_to_refresh_icon_description),
                        tint = Color.Black,
                        modifier = Modifier.size(35.dp)
                    )
                    Text(stringResource(R.string.error_pull_to_refresh), fontSize = 20.sp)
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = stringResource(R.string.pull_to_refresh_icon_description),
                        tint = Color.Black,
                        modifier = Modifier.size(35.dp)
                    )
                }
            }
        }
        @Composable
        fun EmptyFollowingList() {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
                    .clickable { },
                elevation = 10.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = stringResource(R.string.pull_to_refresh_icon_description),
                        tint = Color.Black,
                        modifier = Modifier.size(35.dp)
                    )
                    Text(stringResource(R.string.no_live_streams), fontSize = 20.sp)
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = stringResource(R.string.pull_to_refresh_icon_description),
                        tint = Color.Black,
                        modifier = Modifier.size(35.dp)
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
        fun AccountActionCard(
            scaffoldState: ScaffoldState,
            accountAction: () -> Unit,
            title:String,
            iconImageVector: ImageVector
        ) {
            val scope = rememberCoroutineScope()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
                    .clickable {
                        scope.launch {
                            scaffoldState.drawerState.close()
                        }
                        accountAction()
                    },
                elevation = 10.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondary),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(title, fontSize = 20.sp,color = MaterialTheme.colorScheme.onSecondary)
                    Icon(
                        iconImageVector,
                        stringResource(R.string.logout_icon_description),
                        modifier = Modifier.size(35.dp),
                        tint =  MaterialTheme.colorScheme.onSecondary
                    )
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




    }//end of parts


    //parts







}