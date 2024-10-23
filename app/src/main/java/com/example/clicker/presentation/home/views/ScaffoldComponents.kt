package com.example.clicker.presentation.home.views

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.clicker.R
import com.example.clicker.network.models.twitchRepo.StreamData

import com.example.clicker.presentation.sharedViews.DrawerScaffold
import com.example.clicker.presentation.sharedViews.ErrorScope
import com.example.clicker.presentation.sharedViews.LazyListLoadingIndicator
import com.example.clicker.presentation.sharedViews.NewUserAlert
import com.example.clicker.presentation.sharedViews.PullToRefreshComponent
import com.example.clicker.presentation.sharedViews.SwitchWithIcon
import com.example.clicker.presentation.stream.models.ClickedStreamInfo
import com.example.clicker.util.NetworkNewUserResponse
import kotlinx.coroutines.launch




    /**
     *
     * - HomeViewScaffold is used to create the scaffold home view.
     * Essentially it is what the user sees when the data loads from the twitch server plus a
     * Scaffold drawer which allows the user to login and logout
     *
     * @param login a function that is used to log in the current user into their twitch account
     * @param userIsLoggedIn a boolean to determine if the user is logged in or not
     * @param urlList it is a nullable list of all the live channels returned by the twitch server
     * @param urlListLoading a [Response][com.example.clicker.util.Response] class to represent which state the request to the twitch server is
     * @param onNavigate a function used to navigate from the home page to the individual stream view
     * @param updateStreamerName a function used to update the current streamer the user has clicked on. This information is used
     * to connect the [TwitchWebSocket][com.example.clicker.network.websockets.TwitchWebSocket] to the Twitch servers
     * @param clientId a string representing the clientId of the streamer that is being viewed
     * @param userId a string representing the userId of the streamer that is being viewed
     * @param height a Int representing the height in a aspect ratio that will make the images look nice
     * @param width a Int representing the width in a aspect ratio that will make the images look nice
     * @param showFailedNetworkRequestMessage a boolean that is used to determine if the user should show a error message or not
     * to determine when the pull to refresh icon should change
     * */
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun HomeViewScaffold(
        showLogoutDialog:()->Unit,
        userIsLoggedIn: Boolean,
        followedStreamerList: NetworkNewUserResponse<List<StreamData>>,
        onNavigate: (Int) -> Unit,
        updateStreamerName: (String, String, String, String) -> Unit,
        updateClickedStreamInfo:(ClickedStreamInfo)->Unit,
        clientId:String,
        userId:String,
        height:Int,
        width:Int,
        screenDensity:Float,
        homeRefreshing:Boolean,
        homeRefreshFunc:()->Unit,
        networkMessageColor:Color,
        networkMessage: String,
        showNetworkMessage:Boolean,
        bottomModalState: ModalBottomSheetState,
        loginWithTwitch:() ->Unit,
        showNetworkRefreshError:Boolean,
        hapticFeedBackError:() ->Unit,
        lowPowerModeActive:Boolean,
        changeLowPowerMode:(Boolean)->Unit,
        getTopGames:()->Unit
        ){
        val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
        val scope = rememberCoroutineScope()


        DrawerScaffold(
            scaffoldState = scaffoldState,
            topBar = {
                IconTextTopBarRow(
                    icon={
                        BasicIcon(
                            color = MaterialTheme.colorScheme.onPrimary,
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Open left side drawer",
                            onClick = {
                                scope.launch { scaffoldState.drawerState.open() }
                            }
                        )
                    },
                    text=stringResource(R.string.live_channels),
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize
                )
            },
            bottomBar = {
                TripleButtonNavigationBottomBarRow(
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    horizontalArrangement=Arrangement.SpaceAround,
                    firstButton = {
                        IconOverTextColumn(
                            iconColor = MaterialTheme.colorScheme.secondary,
                            text = "Home",
                            imageVector = Icons.Default.Home,
                            iconContentDescription = "Stay on home page",
                            onClick = {},
                            fontColor = MaterialTheme.colorScheme.onPrimary,
                        )
                    },
                    secondButton = {
                        PainterResourceIconOverTextColumn(
                            iconColor =MaterialTheme.colorScheme.onPrimary,
                            text = "Mod Channels",
                            painter = painterResource(R.drawable.moderator_white),
                            iconContentDescription = "Navigate to mod channel page",
                            onClick ={onNavigate(R.id.action_homeFragment_to_modChannelsFragment)},
                            fontColor = MaterialTheme.colorScheme.onPrimary,
                        )
                    },
                    thirdButton = {
                        IconOverTextColumn(
                            iconColor = MaterialTheme.colorScheme.onPrimary,
                            text = "Search",
                            imageVector = Icons.Default.Search,
                            iconContentDescription = "Navigate to search bar",
                            onClick = {
                                getTopGames()
                                onNavigate(R.id.action_homeFragment_to_searchFragment)
                                      },
                            fontColor = MaterialTheme.colorScheme.onPrimary,
                        )
                    },
                )
            },
            drawerContent = {
                LoginLogoutScaffoldDrawer(
                    showLogoutDialog = {
                        showLogoutDialog()
                    },
                    loginWithTwitch = {
                        loginWithTwitch()
                    },
                    scaffoldState = scaffoldState,
                    userIsLoggedIn = userIsLoggedIn,
                    lowPowerModeActive=lowPowerModeActive,
                    changeLowPowerMode={newValue ->changeLowPowerMode(newValue)}
                )

            }
        ) { contentPadding ->

            PullToRefreshComponent(
                padding = contentPadding,
                refreshing = homeRefreshing,
                refreshFunc = {homeRefreshFunc()},
                showNetworkMessage=showNetworkMessage,
                networkStatus = {modifier ->
                    NetworkStatusCard(
                        modifier = modifier,
                        color =  networkMessageColor,
                        networkMessage = networkMessage
                    )
                }
            ){
                LiveChannelsLazyColumn(
                    followedStreamerList =followedStreamerList,
                    contentPadding =contentPadding,
                    loadingIndicator = {
                        LazyListLoadingIndicator()
                    },
                    emptyList={
                        EmptyFollowingList()
                    },
                    liveChannelRowItem = {streamItem ->
                        LiveChannelRowItem(
                            updateStreamerName ={
                                    streamerName,clientId,broadcasterId,userId ->
                                updateStreamerName(streamerName,clientId,broadcasterId,userId)

                            },
                            updateClickedStreamInfo={clickedStreamInfo ->  updateClickedStreamInfo(clickedStreamInfo)},
                            streamItem = streamItem,
                            clientId =clientId,
                            userId = userId,
                            height = height,
                            width = width,
                            onNavigate = {id -> onNavigate(id)},
                            density =screenDensity
                        )
                    },
                    gettingStreamError = {message ->
                        this.GettingStreamsError(errorMessage = message)
                    },
                    newUserAlert={responseMessage ->
                        NewUserAlert(
                            iconSize = 35.dp,
                            iconContentDescription = "Show the login modal",
                            iconColor = MaterialTheme.colorScheme.onSecondary,
                            iconImageVector = Icons.Default.AccountCircle,
                            backgroundColor = MaterialTheme.colorScheme.secondary,
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                            textColor = MaterialTheme.colorScheme.onSecondary,
                            message = responseMessage,
                            onClick ={
                                scope.launch {
                                    bottomModalState.show()
                                }
                            }
                        )
                    },
                    bottomModalState =bottomModalState,
                    showNetworkRefreshError =showNetworkRefreshError,
                    hapticFeedBackError={hapticFeedBackError()},
                )

            }


        }
    }






    /**
     * Parts represents the most individual parts of [ScaffoldComponents] and should be thought of as the individual
     * pieces that are used inside of a [Builders] to create a [ScaffoldComponents] implementation
     * */

        @OptIn(ExperimentalComposeUiApi::class)
        fun Modifier.setTagAndId(tag: String): Modifier {
            return this
                .semantics { this.testTagsAsResourceId = true }
                .testTag(tag)
        }


        /**
         *
         * - LiveChannelRowItem is a composable function that will show the individual information for each live stream
         * retrieved from the Twitch server
         *
         * @param updateStreamerName a function used to update the current streamer the user has clicked on. This information is used
         * to connect the [TwitchWebSocket][com.example.clicker.network.websockets.TwitchWebSocket] to the Twitch servers
         * @param streamItem a [StreamInfo][com.example.clicker.presentation.home.StreamInfo] object that is used to represent the all the information
         * of a single live stream
         * @param clientId a String representing the clientId of the user
         * @param userId a String representing the userId of the user
         * @param onNavigate a function used to navigate to the StreamView
         * @param height a Int representing the height of the image. The height is in a 9/16 aspect ration
         * @param width a Int representing the width of the image. The width is in a 9/16 aspect ration
         * */
        @Composable
        fun LiveChannelRowItem(
            updateStreamerName: (String, String, String, String) -> Unit,
            updateClickedStreamInfo:(ClickedStreamInfo)->Unit,
            streamItem: StreamData,
            clientId: String,
            userId:String,
            onNavigate: (Int) -> Unit,
            height: Int,
            width: Int,
            density:Float

        ){
            Log.d("LiveChannelRowItem","height->$height")
            Log.d("LiveChannelRowItem","width->$width")
            Log.d("LiveChannelRowItem","density->$density")
            Log.d("LiveChannelRowItem","StreamData->${streamItem}")

            Row(
                modifier = Modifier.clickable {
                    updateClickedStreamInfo(
                        ClickedStreamInfo(
                            channelName = streamItem.userLogin,
                            streamTitle = streamItem.title,
                            category =  streamItem.gameName,
                            tags = streamItem.tags,
                            adjustedUrl = streamItem.thumbNailUrl
                        )
                    )

                    updateStreamerName(
                        streamItem.userLogin,
                        clientId,
                        streamItem.userId,
                        userId
                    )
                    onNavigate(R.id.action_homeFragment_to_streamFragment)
                }
            ){
                ImageWithViewCount(
                    url = streamItem.thumbNailUrl,
                    height = height,
                    width = width,
                    viewCount = streamItem.viewerCount,
                    density =density
                )
                StreamTitleWithInfo(
                    streamerName = streamItem.userLogin,
                    streamTitle = streamItem.title,
                    gameTitle = streamItem.gameName
                )

            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )
        }





        /**
         *
         * - GettingStreamsError is a composable function that will appear to the user when there was an error
         * retrieving the streams from the Twitch server
         *
         * */
        @Composable
        fun GettingStreamsError(
            errorMessage: String
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .clickable { },
                elevation = 10.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = stringResource(R.string.pull_to_refresh_icon_description),
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.size(35.dp)
                    )
                    Text(errorMessage, fontSize = MaterialTheme.typography.headlineMedium.fontSize,color=MaterialTheme.colorScheme.onSecondary)
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = stringResource(R.string.pull_to_refresh_icon_description),
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.size(35.dp)
                    )
                }
            }
        }
        /**
         *
         * - EmptyFollowingList is a composable function that will appear to the user when there are no live channels
         *
         * */
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
                    Text(stringResource(R.string.no_live_streams), fontSize = MaterialTheme.typography.headlineMedium.fontSize)
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = stringResource(R.string.pull_to_refresh_icon_description),
                        tint = Color.Black,
                        modifier = Modifier.size(35.dp)
                    )
                }
            }
        }





    /**
     *
     * - ScaffoldDrawer is a composable that is shown to the user when the [ScaffoldState] is set to OPEN
     *
     * @param userIsLoggedIn a boolean to determine if the user is logged in or not
     * */
    @Composable
    fun LoginLogoutScaffoldDrawer(
        showLogoutDialog: () -> Unit,
        loginWithTwitch: () -> Unit,
        scaffoldState: ScaffoldState,
        userIsLoggedIn: Boolean,
        lowPowerModeActive:Boolean,
        changeLowPowerMode:(Boolean)->Unit,
    ) {

        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)){
            Column(
                modifier= Modifier
                    .matchParentSize()
                    .padding(horizontal = 15.dp)
            ){

                if (userIsLoggedIn) {
                    AccountActionCard(
                        scaffoldState,
                        onCardClick = { showLogoutDialog() },
                        title = stringResource(R.string.logout_icon_description),
                        iconImageVector = Icons.Default.ExitToApp
                    )
                } else {
                    AccountActionCard(
                        scaffoldState,
                        onCardClick = { loginWithTwitch() },
                        title = stringResource(R.string.login_with_twitch),
                        iconImageVector = Icons.Default.AccountCircle
                    )

                }

                AccountActionCardWithSwitch(
                    checkedValue =lowPowerModeActive,
                    changeCheckedValue={newValue ->changeLowPowerMode(newValue)}
                )
                LowPowerModeAnimatedColumn(lowPowerModeActive)


            }

        }

    }
    @Composable
    fun LowPowerModeAnimatedColumn(checked:Boolean){
        val density = LocalDensity.current
        AnimatedVisibility(
            visible = checked,
            enter = slideInVertically {
                // Slide in from 40 dp from the top.
                with(density) { -40.dp.roundToPx() }
            } + expandVertically(
                // Expand from the top.
                expandFrom = Alignment.Top
            ) + fadeIn(
                // Fade in with the initial alpha of 0.3f.
                initialAlpha = 0.3f
            ),
            exit = slideOutVertically() + shrinkVertically() + fadeOut()
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)){
                Text("Status: ", Modifier.fillMaxWidth(),color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineMedium.fontSize)
                Text("   - Active", Modifier.fillMaxWidth(),color = Color.Green,fontSize = 18.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Text("Benefits: ", Modifier.fillMaxWidth(),color = MaterialTheme.colorScheme.onPrimary,fontSize = MaterialTheme.typography.headlineMedium.fontSize)
                Text("   - 5% to 20% less battery usage", Modifier.fillMaxWidth(),color = MaterialTheme.colorScheme.onPrimary, fontSize = 18.sp)
                Text("   - No background data refresh", Modifier.fillMaxWidth(),color = MaterialTheme.colorScheme.onPrimary, fontSize = 18.sp)
                Text("   - No extra network calls", Modifier.fillMaxWidth(),color = MaterialTheme.colorScheme.onPrimary, fontSize = 18.sp)
                Text("   - No chat", Modifier.fillMaxWidth(),color = MaterialTheme.colorScheme.onPrimary,fontSize = 18.sp)

            }

        }

    }
    @Composable
    fun AccountActionCardWithSwitch(
        checkedValue:Boolean,
        changeCheckedValue:(Boolean)->Unit
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .clickable {
                    changeCheckedValue(!checkedValue)

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
                Text("Low power mode", fontSize = MaterialTheme.typography.headlineMedium.fontSize,color = MaterialTheme.colorScheme.onSecondary)
                SwitchWithIcon(
                    checkedValue =checkedValue,
                    changeCheckedValue={newValue ->changeCheckedValue(newValue)},
                    icon = Icons.Filled.Check,
                )

            }
        }
    }



    /**
     *
     * - AccountActionCard is a clickable card that can be clicked on the trigger the action of [onCardClick]
     *
     * @param scaffoldState the state of the [Scaffold]. Will be used to open and close the drawer of the Scaffold
     * @param onCardClick a function will be run once the Card is clicked
     * @param title a string representing a text that will be shown on the Card and should tell the user what the clickable card does
     * @param iconImageVector a [ImageVector] that will be displayed after the [title]
     * */
    @Composable
    fun AccountActionCard(
        scaffoldState: ScaffoldState,
        onCardClick: () -> Unit,
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
                    onCardClick()
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
                Text(title, fontSize = MaterialTheme.typography.headlineMedium.fontSize,color = MaterialTheme.colorScheme.onSecondary)
                Icon(
                    iconImageVector,
                    stringResource(R.string.logout_icon_description),
                    modifier = Modifier.size(35.dp),
                    tint =  MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }



@Stable
class LiveChannelsLazyColumnScope(){

    /**
     * - Contains 3 extra parts:
     * 1) [EmptyFollowingList]
     * 2) [LiveChannelRowItem]
     * 3) [GettingStreamsError]
     *
     * - LiveChannelRowItem is a composable function that will show the individual information for each live stream
     * retrieved from the Twitch server
     *
     * @param urlList it is a nullable list of all the live channels returned by the twitch server
     * @param urlListLoading a [Response][com.example.clicker.util.Response] class to represent which state the request to the twitch server is
     * @param userId a String representing the userId of the user
     * @param onNavigate a function used to navigate to the StreamView
     * @param height a Int representing the height of the image. The height is in a 9/16 aspect ration
     * @param width a Int representing the width of the image. The width is in a 9/16 aspect ration
     * */
    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
    @Composable
    fun LiveChannelsLazyColumn(
        bottomModalState: ModalBottomSheetState,
        followedStreamerList: NetworkNewUserResponse<List<StreamData>>,
        contentPadding: PaddingValues,
        loadingIndicator:@Composable () -> Unit,
        emptyList:@Composable LiveChannelsLazyColumnScope.() -> Unit,
        liveChannelRowItem:@Composable LiveChannelsLazyColumnScope.(streamItem: StreamData) -> Unit,
        gettingStreamError:@Composable LiveChannelsLazyColumnScope.(message:String) -> Unit,
        newUserAlert:@Composable (message:String) ->Unit,
        showNetworkRefreshError:Boolean,
        hapticFeedBackError:() ->Unit,

        ){
        val fontSize =MaterialTheme.typography.headlineMedium.fontSize
        val scope = rememberCoroutineScope()
        val lazyColumnScope = remember() { LiveChannelsLazyColumnScope() }

        val errorScope = remember(){ ErrorScope(fontSize) }



        Box(modifier = Modifier.fillMaxSize()){
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .setTagAndId("streamersListLoading"),
                contentPadding = contentPadding
            ) {



                when (followedStreamerList) {
                    is NetworkNewUserResponse.Loading -> {
                        scope.launch {
                            bottomModalState.hide()
                        }
                        item {

                                loadingIndicator()

                        }
                    }
                    is NetworkNewUserResponse.Success -> {

                        val listData = followedStreamerList.data
                        if (listData != null) {

                            if (listData.isEmpty()) {
                                item {
                                    with(lazyColumnScope){
                                        emptyList()
                                    }
                                }
                            }
                            items(listData,key = { streamItem -> streamItem.userId }) { streamItem ->


                                with(lazyColumnScope){

                                    liveChannelRowItem(streamItem)
                                }

                            }



                            // end of the lazy column
                        }
                    }
                    is NetworkNewUserResponse.Failure -> {

                        item {

                            val message =followedStreamerList.e.message ?:"Error! please pull down to refresh"
                            with(lazyColumnScope){
                                gettingStreamError(message)
                            }
                        }
                    }
                    is NetworkNewUserResponse.NetworkFailure -> {
                        val message =followedStreamerList.e.message ?:"Error! please pull down to refresh"

                        item{
                            with(lazyColumnScope){
                                gettingStreamError(message)
                            }
                        }

                    }

                    is NetworkNewUserResponse.Auth401Failure ->{
                        item{
                            newUserAlert(message = followedStreamerList.e.message ?:"Error! Re-login with Twitch")
                        }
                        scope.launch {
                            bottomModalState.show()
                        }
                    }
                }

            } // end of the lazylist
            if(showNetworkRefreshError){
                Box(modifier = Modifier.align(Alignment.BottomCenter)){
                    hapticFeedBackError()
                    with(errorScope){
                        this.NetworkErrorMessage()
                    }
                }

            }
        }

    }




    /**
     * - Contains 0 extra parts
     *
     * - EmptyFollowingList is a composable function that will appear to the user when there are no live channels
     *
     * */
    @Composable
    fun EmptyFollowingList() {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .clickable { },
            border = BorderStroke(2.dp,MaterialTheme.colorScheme.secondary),
            elevation = 10.dp,
            backgroundColor = MaterialTheme.colorScheme.primary
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(R.string.pull_to_refresh_icon_description),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(35.dp)
                )
                Text(stringResource(R.string.no_live_streams), fontSize = MaterialTheme.typography.headlineMedium.fontSize, color = MaterialTheme.colorScheme.onPrimary)
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(R.string.pull_to_refresh_icon_description),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(35.dp)
                )
            }
        }
    }


    /**
     * - Contains 0 extra parts
     *
     * - GettingStreamsError is a composable function that will appear to the user when there was an error
     * retrieving the streams from the Twitch server
     *
     * */
    @Composable
    fun GettingStreamsError(
        errorMessage: String
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .clickable { },
            elevation = 10.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = stringResource(R.string.pull_to_refresh_icon_description),
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(35.dp)
                )
                Text(errorMessage, fontSize = MaterialTheme.typography.headlineMedium.fontSize,color=MaterialTheme.colorScheme.onSecondary)
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = stringResource(R.string.pull_to_refresh_icon_description),
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(35.dp)
                )
            }
        }
    }

}/**END OF LAZYCOLUMNSCOPE*/


/**
 *
 * - StreamTitleWithInfo is a Column that shows information about the streamer and the game they are playing
 *
 * @param streamerName a String representing the name of the live streamer
 * @param streamTitle a String representing the title of the streamer's stream
 * @param gameTitle a String representing the title of the game they are playing
 * */
@Composable
fun StreamTitleWithInfo(
    streamerName:String,
    streamTitle:String,
    gameTitle:String,

){
    Column(modifier = Modifier.padding(start = 10.dp)) {
        Text(
            streamerName,
            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            streamTitle,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            modifier = Modifier.alpha(0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            gameTitle,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            modifier = Modifier.alpha(0.7f),
            color = MaterialTheme.colorScheme.onPrimary
        )

    }
}
/**
 *
 * - ImageWithViewCount is a Box that uses the SubcomposeAsyncImage to load and show the image we get from the Twitch server.
 * It will show the thumbnail for the stream and their current viewer count
 *
 * @param url a String representing the thumbnail image
 * @param height a Int representing the height of the image. The height is in a 9/16 aspect ration
 * @param width a Int representing the width of the image. The width is in a 9/16 aspect ration
 * @param viewCount a Int representing the number of current viewers the streamer has
 * */
@Composable
fun ImageWithViewCount(
    url: String,
    height: Int,
    width: Int,
    viewCount:Int,
    density:Float
){
    Log.d("ImageHeightWidth","url -> $url")
    Box() {
        val adjustedHeight = height/density
        val adjustedWidth = width/density
        SubcomposeAsyncImage(
            model = url,
            loading = {
                Column(modifier = Modifier
                    .height((adjustedHeight).dp)
                    .width((adjustedWidth).dp)
                    .background(MaterialTheme.colorScheme.primary),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    CircularProgressIndicator()
                }
            },
            contentDescription = stringResource(R.string.sub_compose_async_image_description)
        )
        Text(
            "${viewCount}",
            style = TextStyle(
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                fontWeight = FontWeight.ExtraBold
            ),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(5.dp)
        )
    }
}







