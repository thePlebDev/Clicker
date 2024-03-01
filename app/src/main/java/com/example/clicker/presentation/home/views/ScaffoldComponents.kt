package com.example.clicker.presentation.home.views

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DrawerValue
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.semantics.testTagsAsResourceId
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
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.presentation.home.StreamInfo
import com.example.clicker.presentation.home.views.ScaffoldParts.AccountActionCard
import com.example.clicker.presentation.home.views.ScaffoldParts.EmptyFollowingList
import com.example.clicker.presentation.home.views.ScaffoldParts.GettingStreamsError
import com.example.clicker.presentation.home.views.ScaffoldParts.ImageWithViewCount
import com.example.clicker.presentation.home.views.ScaffoldParts.LiveChannelRowItem
import com.example.clicker.presentation.home.views.ScaffoldParts.StreamTitleWithInfo
import com.example.clicker.presentation.home.views.ScaffoldParts.setTagAndId
import com.example.clicker.presentation.modChannels.views.PullToRefresh
import com.example.clicker.util.NetworkResponse
import com.example.clicker.util.PullRefreshState
import com.example.clicker.presentation.modChannels.views.rememberPullToRefreshState
import com.example.clicker.presentation.sharedViews.IndicatorScopes
import com.example.clicker.presentation.sharedViews.NewUserAlert
import com.example.clicker.presentation.sharedViews.NotificationsScope
import com.example.clicker.presentation.sharedViews.NotifyUserScope
import com.example.clicker.presentation.sharedViews.PullToRefreshComponent
import com.example.clicker.presentation.sharedViews.ScaffoldBottomBarScope
import com.example.clicker.presentation.sharedViews.ScaffoldTopBarScope
import com.example.clicker.presentation.stream.ClickedStreamInfo
import com.example.clicker.util.NetworkNewUserResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



@Stable
class MainScaffoldScope(){

    /**
     *
     * - MainScaffoldComponent is used soley inside of [HomeViewImplementation][HomeComponents.HomeViewImplementation] to
     * create the scaffold home view. Essentially it is what the user sees when the data loads from the twitch server plus a
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
    @Composable
    fun MainScaffoldComponent(
        showLogoutDialog:()->Unit,
        login: () -> Unit,
        userIsLoggedIn: Boolean,
        urlList: List<StreamData>?,
        urlListLoading: NetworkNewUserResponse<Boolean>,
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
        showLoginModal:()->Unit,

    ){
        val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
        val scope = rememberCoroutineScope()



        ScaffoldBuilder(
            scaffoldState =scaffoldState,
            drawerContent = {
                ScaffoldScope.LoginLogoutScaffoldDrawer(
                    showLogoutDialog = {
                        showLogoutDialog()
                    },
                    loginWithTwitch = {
                        login()
                    },
                    scaffoldState = scaffoldState,
                    userIsLoggedIn = userIsLoggedIn
                )
            },
            topBar = {
                IconTextTopBar(
                    clickableIcon={
                        ClickableIcon(
                            iconColor = MaterialTheme.colorScheme.onPrimary,
                            imageVector = Icons.Filled.Menu,
                            iconContentDescription = "Open left side drawer",
                            onClick = {
                                scope.launch { scaffoldState.drawerState.open() }
                            }
                        )
                    },
                    text={
                        Text(
                            stringResource(R.string.live_channels),
                            fontSize = 25.sp,
                            modifier = Modifier.padding(start = 20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )

                    }
                )
            },
            bottomBar = {
                DualButtonNavigationBottomBar(
                    bottomRowHeight = 100.dp,
                    firstButton = {
                        IconOverText(
                            iconColor = MaterialTheme.colorScheme.secondary,
                            text = "Home",
                            imageVector = Icons.Default.Home,
                            iconContentDescription = "Stay on home page",
                            onClick = {}
                        )
                    },
                    secondButton = {
                        PainterResourceIconOverText(
                            iconColor =MaterialTheme.colorScheme.onPrimary,
                            text = "Mod Channels",
                            painter = painterResource(R.drawable.moderator_white),
                            iconContentDescription = "Navigate to mod channel page",
                            onClick ={onNavigate(R.id.action_homeFragment_to_modChannelsFragment)}
                        )
                    },
                )
                        },
            pullToRefreshList ={contentPadding ->
                PullToRefreshComponent(
                    padding = contentPadding,
                    refreshing = homeRefreshing,
                    refreshFunc = {homeRefreshFunc()},
                    showNetworkMessage=showNetworkMessage,
                    networkStatus = {modifier ->
                        NetworkStatus(
                            modifier = modifier,
                            color =  networkMessageColor,
                            networkMessage = networkMessage
                        )
                    }
                ){
                    LiveChannelsLazyColumn(
                        urlList =urlList,
                        urlListLoading =urlListLoading,
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
                                fontSize = 20.sp,
                                textColor = MaterialTheme.colorScheme.onSecondary,
                                message = responseMessage,
                                onClick ={showLoginModal()}
                            )
                        }
                    )

                }
            },


        )
    }


    /**
     * - ScaffoldBuilder is used inside of  [MainScaffoldComponent].
     *
     *

     * that is used to enable to nested scrolling of the two layout boxes using the [NestedScrollConnection](https://developer.android.com/reference/kotlin/androidx/compose/ui/input/nestedscroll/NestedScrollConnection)
     * @param scaffoldState a [ScaffoldState] object representing the state of the scaffold
     * @param drawerContent a composable function that represent the drawer content of the scaffold
     * @param topBar a composable function that represent the topBar content of the scaffold
     * @param bottomBar a composable function that represent the bottomBar content of the scaffold
     * This will get covered by the scaffold
     * */
    @Composable
    fun ScaffoldBuilder(
        scaffoldState: ScaffoldState,
        drawerContent:@Composable () -> Unit,
        topBar:@Composable ScaffoldTopBarScope.() -> Unit,
        bottomBar:@Composable ScaffoldBottomBarScope.() -> Unit,
        pullToRefreshList:@Composable (contentPadding: PaddingValues) -> Unit,

        ){
        val scaffoldBarsScope = remember() { ScaffoldBottomBarScope(35.dp) }

        val topBarScaffoldScope = remember(){ScaffoldTopBarScope(35.dp)}

        Scaffold(
            backgroundColor= MaterialTheme.colorScheme.primary,
            scaffoldState = scaffoldState,
            drawerContent = {
                    drawerContent()
            },
            topBar = {
                with(topBarScaffoldScope){
                    topBar()
                }
            },
            bottomBar = {
                with(scaffoldBarsScope){
                    bottomBar()
                }

            },
        ) { contentPadding ->

            pullToRefreshList(contentPadding)
        }
    }
}




    /**
     * Parts represents the most individual parts of [ScaffoldComponents] and should be thought of as the individual
     * pieces that are used inside of a [Builders] to create a [ScaffoldComponents] implementation
     * */
    private object ScaffoldParts{
        @OptIn(ExperimentalComposeUiApi::class)
        fun Modifier.setTagAndId(tag: String): Modifier {
            return this
                .semantics { this.testTagsAsResourceId = true }
                .testTag(tag)
        }


        /**
         * - Contains 2 extra parts:
         * 1) [ImageWithViewCount]
         * 2) [StreamTitleWithInfo]
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
                ScaffoldParts.ImageWithViewCount(
                    url = streamItem.thumbNailUrl,
                    height = height,
                    width = width,
                    viewCount = streamItem.viewerCount,
                    density =density
                )
                ScaffoldParts.StreamTitleWithInfo(
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
         * - Contains 0 extra parts
         *
         * - CustomBottomBar is a custom bottom bar for a [Scaffold] and is meant to act as the home activities Navigation
         * */


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
                    backgroundColor = MaterialTheme.colorScheme.secondary
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
        /**
         * - Contains 0 extra parts
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
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(5.dp)
                )
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
                    Text(errorMessage, fontSize = 20.sp,color=MaterialTheme.colorScheme.onSecondary)
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

        /**
         * - Contains 0 extra parts
         *
         * - AccountActionCard is a clickable card that can be clicked on the trigger the action of [accountAction]
         *
         * @param scaffoldState the state of the [Scaffold]. Will be used to open and close the drawer of the Scaffold
         * @param accountAction a function will be run once the Card is clicked
         * @param title a string representing a text that will be shown on the Card and should tell the user what the clickable card does
         * @param iconImageVector a [ImageVector] that will be displayed after the [title]
         * */
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



    }

@Stable
object ScaffoldScope{
    /**
     * - Contains 1 extra part [AccountActionCard]
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
        userIsLoggedIn: Boolean
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)){
            if (userIsLoggedIn) {
                AccountActionCard(
                    scaffoldState,
                    accountAction = { showLogoutDialog() },
                    title = stringResource(R.string.logout_icon_description),
                    iconImageVector = Icons.Default.ExitToApp
                )
            } else {
                AccountActionCard(
                    scaffoldState,
                    accountAction = { loginWithTwitch() },
                    title = stringResource(R.string.login_with_twitch),
                    iconImageVector = Icons.Default.AccountCircle
                )

            }
        }

    }

    /**
     * - Contains 0 extra parts
     *
     * - AccountActionCard is a clickable card that can be clicked on the trigger the action of [accountAction]
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
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun LiveChannelsLazyColumn(
        urlList: List<StreamData>?,
        urlListLoading: NetworkNewUserResponse<Boolean>,
        contentPadding: PaddingValues,
        loadingIndicator:@Composable IndicatorScopes.() -> Unit,
        emptyList:@Composable LiveChannelsLazyColumnScope.() -> Unit,
        liveChannelRowItem:@Composable LiveChannelsLazyColumnScope.(streamItem: StreamData) -> Unit,
        gettingStreamError:@Composable LiveChannelsLazyColumnScope.(message:String) -> Unit,
        newUserAlert:@Composable (message:String) ->Unit,

        ){
        val lazyColumnScope = remember() { LiveChannelsLazyColumnScope() }
        val indicatorScopes = remember() { IndicatorScopes() }
        val newUserScope = remember() { NotifyUserScope(fontSize = 20.sp, iconSize = 35.dp) }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .setTagAndId("streamersListLoading"),
            contentPadding = contentPadding
        ) {



            when (urlListLoading) {
                is NetworkNewUserResponse.Loading -> {
                    item {
                        with(indicatorScopes){
                            loadingIndicator()
                        }
                    }
                }
                is NetworkNewUserResponse.Success -> {
                    if (urlList != null) {

                        if (urlList.isEmpty()) {
                            item {
                                with(lazyColumnScope){
                                    emptyList()
                                }
                            }
                        }

                        items(urlList,key = { streamItem -> streamItem.userId }) { streamItem ->

                            with(lazyColumnScope){
                                liveChannelRowItem(streamItem)
                            }

                        }
                        // end of the lazy column
                    }
                }
                is NetworkNewUserResponse.Failure -> {

                    item {

                        val message =urlListLoading.e.message ?:"Error! please pull down to refresh"
                        with(lazyColumnScope){
                            gettingStreamError(message)
                        }
                    }
                }
                is NetworkNewUserResponse.NetworkFailure -> {

                    item{
                        with(lazyColumnScope){
                            gettingStreamError("Error! Pull down to refresh")
                        }
                    }

                }
                is NetworkNewUserResponse.NewUser ->{
                    item{
//                        with(lazyColumnScope){
//                            gettingStreamError(urlListLoading.message)
//                        }

                            newUserAlert(message = urlListLoading.message)

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
                Text(stringResource(R.string.no_live_streams), fontSize = 20.sp, color = MaterialTheme.colorScheme.onPrimary)
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
     * - Contains 2 extra parts:
     * 1) [ImageWithViewCount]
     * 2) [StreamTitleWithInfo]
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
            ScaffoldParts.ImageWithViewCount(
                url = streamItem.thumbNailUrl,
                height = height,
                width = width,
                viewCount = streamItem.viewerCount,
                density =density
            )
            ScaffoldParts.StreamTitleWithInfo(
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
                Text(errorMessage, fontSize = 20.sp,color=MaterialTheme.colorScheme.onSecondary)
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






