package com.example.clicker.presentation.home.views

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
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
import com.example.clicker.presentation.stream.ClickedStreamInfo
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
        urlListLoading: NetworkResponse<Boolean>,
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
        showNetworkMessage:Boolean

    ){
        val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))



        ScaffoldBuilder(
            scaffoldState =scaffoldState,
            drawerContent = {
                LoginLogoutScaffoldDrawer(
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
                CustomTopBar(
                    scaffoldState = scaffoldState
                )
            },
            bottomBar = {
                CustomBottomBar(
                    onNavigate= {id -> onNavigate(id)
                    }
                )},
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
                        }
                    )

                }
            },
            scaffoldIconSize = 35.dp,
            scaffoldBottomBarHeight = 100.dp

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
        drawerContent:@Composable ScaffoldScope.() -> Unit,
        topBar:@Composable ScaffoldBarsScope.() -> Unit,
        bottomBar:@Composable ScaffoldBarsScope.() -> Unit,
        pullToRefreshList:@Composable (contentPadding: PaddingValues) -> Unit,
        scaffoldIconSize:Dp,
        scaffoldBottomBarHeight:Dp
    ){
        val scaffoldBarsScope = remember() { ScaffoldBarsScope(scaffoldIconSize,scaffoldBottomBarHeight) }
        val scaffoldScope = remember() { ScaffoldScope() }

        Scaffold(
            backgroundColor= MaterialTheme.colorScheme.primary,
            scaffoldState = scaffoldState,
            drawerContent = {
                with(scaffoldScope){
                    drawerContent()
                }

            },
            topBar = {
                with(scaffoldBarsScope){
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



@Composable
fun PullToRefreshComponent(
    padding: PaddingValues,
    refreshing:Boolean,
    refreshFunc:()->Unit,
    showNetworkMessage:Boolean,
    networkStatus:@Composable NotificationsScope.(modifier:Modifier) -> Unit,
    content:@Composable LiveChannelsLazyColumnScope.() -> Unit,
){

    val lazyColumnScope = remember() { LiveChannelsLazyColumnScope() }
    val networkStatusScope = remember() { NotificationsScope() }


    PullToRefresh(
        state = rememberPullToRefreshState(isRefreshing = refreshing),
        onRefresh = { refreshFunc()},
        indicatorPadding = padding
    ) {
        Box(modifier= Modifier
            .fillMaxSize()
            .padding(padding)){

            with(lazyColumnScope){
                content()
            }
            if(!showNetworkMessage){
                with(networkStatusScope){
                    networkStatus(Modifier.align(Alignment.BottomCenter))
                }
            }

        }

    }
}


@Stable
class NotificationsScope{

    @Composable
    fun NetworkStatus(
        modifier:Modifier,
        color:Color,
        networkMessage:String
    ){
        Card(
            modifier = modifier
                .clickable{ },
            elevation = 10.dp,
            backgroundColor =color.copy(alpha = 0.8f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    "home icon",
                    tint= MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(30.dp)
                )
                Text(networkMessage,color = MaterialTheme.colorScheme.onPrimary)
            }
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
class ScaffoldScope(){
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
        urlListLoading: NetworkResponse<Boolean>,
        contentPadding: PaddingValues,
        loadingIndicator:@Composable LiveChannelsLazyColumnScope.() -> Unit,
        emptyList:@Composable LiveChannelsLazyColumnScope.() -> Unit,
        liveChannelRowItem:@Composable LiveChannelsLazyColumnScope.(streamItem: StreamData) -> Unit,
        gettingStreamError:@Composable LiveChannelsLazyColumnScope.(message:String) -> Unit,

        ){
        val lazyColumnScope = remember() { LiveChannelsLazyColumnScope() }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .setTagAndId("streamersListLoading"),
            contentPadding = contentPadding
        ) {



            when (urlListLoading) {
                is NetworkResponse.Loading -> {
                    item {
                        with(lazyColumnScope){
                            loadingIndicator()
                        }
                    }
                }
                is NetworkResponse.Success -> {
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
                is NetworkResponse.Failure -> {

                    item {

                        val message =urlListLoading.e.message ?:"Error! please pull down to refresh"
                        with(lazyColumnScope){
                            gettingStreamError(message)
                        }
                    }
                }
                is NetworkResponse.NetworkFailure -> {

                    item{
                        with(lazyColumnScope){
                            gettingStreamError("Error! Pull down to refresh")
                        }
                    }

                }
            }

        }
    }

    @Composable
    fun LazyListLoadingIndicator(){
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



@Stable
class ScaffoldBarsScope(
    private val iconSize: Dp,
    private val bottomRowHeight:Dp,
    ){

    @Composable
    fun CustomBottomBar(
        onNavigate: (Int) -> Unit,
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .height(bottomRowHeight),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home Icon",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(iconSize)
                )
                Text("Home",color = MaterialTheme.colorScheme.onPrimary)
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onNavigate(R.id.action_homeFragment_to_modChannelsFragment) }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.moderator_secondary_color),
                    "Moderation Icon",
                    tint= MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(iconSize)
                )
                Text("Mod Channels",color = MaterialTheme.colorScheme.onPrimary)
            }


        }
    }

    /**
     * - Contains 0 other parts
     *
     * - A part used to represent the topBar of a [Scaffold]
     *
     * @param scaffoldState the state of the [Scaffold]. Will be used to open and close the drawer of the Scaffold
     * */

    @Composable
    fun CustomTopBar(
        scaffoldState: ScaffoldState
    ) {
        val scope = rememberCoroutineScope()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Menu,
                    stringResource(R.string.menu_icon_description),
                    modifier = Modifier
                        .size(iconSize)
                        .clickable { scope.launch { scaffoldState.drawerState.open() } },
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    stringResource(R.string.live_channels),
                    fontSize = 25.sp,
                    modifier = Modifier.padding(start = 20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }


}



