@file:OptIn(ExperimentalFoundationApi::class)

package com.example.clicker.presentation.home.views

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.HapticFeedbackConstants
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
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
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
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.fragment.findNavController
import coil.compose.SubcomposeAsyncImage
import com.example.clicker.BuildConfig
import com.example.clicker.R
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.presentation.authentication.logout.LogoutViewModel
import com.example.clicker.presentation.enhancedModView.viewModels.ModViewViewModel
import com.example.clicker.presentation.home.HomeViewModel
import com.example.clicker.presentation.minigames.dinoRun.ComposeDinoRunViews
import com.example.clicker.presentation.minigames.views.PingPongViewGLSurfaceViewComposable
import com.example.clicker.presentation.moderatedChannelsHome.views.ModChannelView
import com.example.clicker.presentation.search.SearchViewModel
import com.example.clicker.presentation.search.views.SearchView

import com.example.clicker.presentation.sharedViews.DrawerScaffold
import com.example.clicker.presentation.sharedViews.ErrorScope
import com.example.clicker.presentation.sharedViews.LazyListLoadingIndicator
import com.example.clicker.presentation.sharedViews.NewUserAlert
import com.example.clicker.presentation.sharedViews.NoDrawerScaffold
import com.example.clicker.presentation.sharedViews.PullToRefreshComponent
import com.example.clicker.presentation.sharedViews.SwitchWithIcon
import com.example.clicker.presentation.stream.AutoModViewModel
import com.example.clicker.presentation.stream.StreamViewModel
import com.example.clicker.presentation.stream.models.ClickedStreamInfo
import com.example.clicker.presentation.stream.views.chat.chatSettings.ChatSettingsViewModel
import com.example.clicker.presentation.streamInfo.StreamInfoViewModel
import com.example.clicker.util.NetworkNewUserResponse
import kotlinx.coroutines.launch




    /**
     *
     * - **HomeViewScaffold** is used to create the scaffold home view.
     * Essentially it is what the user sees when the data loads from the twitch server plus a
     * Scaffold drawer which allows the user to login and logout
     *
     * @param showLogoutDialog  a function, when called, will show the logout dialog from the user.
     * @param userIsLoggedIn a Boolean  used to determine if the user is logged in or not
     * @param followedStreamerList a [NetworkNewUserResponse] object representing the list of the user's followed streams
     * @param updateClickedStreamInfo a function when called with a [ClickedStreamInfo] object,
     * will populate the view model with the needed [ClickedStreamInfo] information
     * @param onNavigate a function used to navigate from the home page to the individual stream view
     * @param updateStreamerName a function used to update the current streamer the user has clicked on. This information is used
     * to connect the [TwitchWebSocket][com.example.clicker.network.websockets.TwitchWebSocket] to the Twitch servers
     * @param clientId a string representing the clientId of the streamer that is being viewed
     * @param userId a string representing the userId of the streamer that is being viewed
     * @param height a Int representing the height in a aspect ratio that will make the images look nice
     * @param width a Int representing the width in a aspect ratio that will make the images look nice
     * @param screenDensity a float meant to represent the screen density of the current device
     * @param homeRefreshing a Boolean used to determine if the user has pulled the refreshing code or not
     * @param homeRefreshFunc a function, when called, will refresh the user's home page
     * @param networkMessageColor a Color object that will determine the UI for [networkMessage]
     * @param networkMessage a String used to represent a message shown to the user when there was a problem with the network
     * @param showNetworkMessage a Boolean used to determine if [networkMessage] should be shown or not
     * @param bottomModalState [ModalBottomSheetState] object used to determine if the Bottom modal should pop up or not
     * @param loginWithTwitch a function, when called, will log the user out
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
        getTopGames:()->Unit,
        getPinnedList:()->Unit,
        permissionCheck:()->Unit,
        startService:()->Unit,
        endService:()->Unit,
        checkIfServiceRunning:()->Boolean,
        backgroundServiceChecked:Boolean,
        changeBackgroundServiceChecked:(Boolean)->Unit,
        grantedNotifications:Boolean,
        openAppSettings:() ->Unit,
        navigateToStream:()->Unit,
        loadUrl:(String)->Unit,
        movePager:(Int)->Unit,

        autoModViewModel: AutoModViewModel,
        homeViewModel: HomeViewModel,
        streamViewModel: StreamViewModel,
        modViewViewModel: ModViewViewModel,
        searchViewModel: SearchViewModel,
        logoutViewModel: LogoutViewModel,
        webViewAnimation:(String)->Unit,
        chatSettingsViewModel: ChatSettingsViewModel,
        streamInfoViewModel: StreamInfoViewModel,
        ){
        val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
        val scope = rememberCoroutineScope()
        val pagerState = rememberPagerState(
            initialPage = 0,
            pageCount = { 5 }
        )

        val titleText = when(pagerState.currentPage){
            0 ->"Live Channels"
            1->"Mod Channels"
            2->"Categories"
            3->"Mini Game (Beta)"
            4->"Settings"
            else->""
        }
        val context = LocalContext.current


        NoDrawerScaffold(
            topBar = {
                IconTextTopBarRow(
                    icon={},
                    text=titleText,
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize
                )
            },
            bottomBar = {


                this.FiveButtonNavigationBottomBarRow(
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    horizontalArrangement = Arrangement.SpaceAround,
                    firstButton = {
                        IconOverTextColumn(
                            iconColor = if(pagerState.currentPage ==0)MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onPrimary,
                            text = "Home",
                            imageVector = Icons.Default.Home,
                            iconContentDescription = "Stay on home page",
                            onClick = {
                                      if(pagerState.currentPage !=0){
                                          scope.launch {
                                              pagerState.animateScrollToPage(0)
                                          }
                                      }
                            },
                            fontColor = MaterialTheme.colorScheme.onPrimary,
                        )
                    },
                    secondButton = {
                        PainterResourceIconOverTextColumn(
                            iconColor = if(pagerState.currentPage ==1)MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onPrimary,
                            text = "Mod",
                            painter = painterResource(R.drawable.moderator_white),
                            iconContentDescription = "Navigate to mod channel page",
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(1)
                                }
                              //  onNavigate(R.id.action_homeFragment_to_modChannelsFragment)
                                      },
                            fontColor = MaterialTheme.colorScheme.onPrimary,
                        )
                    },
                    thirdButton = {
                        this.PainterResourceIconOverTextColumn(
                            iconColor = if(pagerState.currentPage ==2)MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onPrimary,
                            painter = painterResource(id = R.drawable.baseline_category_24),
                            iconContentDescription = "Navigate to search bar",
                            fontColor = MaterialTheme.colorScheme.onPrimary,
                            text = "Categories",
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(2)
                                }
                                getTopGames()
                                getPinnedList()
                              //  onNavigate(R.id.action_homeFragment_to_searchFragment)
                            },
                        )
                    },
                    fourthButton={
                        this.PainterResourceIconOverTextColumn(
                            iconColor = if(pagerState.currentPage ==3)MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onPrimary,
                            painter = painterResource(id = R.drawable.videogame_asset),
                            iconContentDescription = "Navigate to Mini game page",
                            fontColor = MaterialTheme.colorScheme.onPrimary,
                            text = "Mini game",
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(3)
                                }

                            },
                        )

                    },
                    fiveButton = {

                        this.PainterResourceIconOverTextColumn(
                            iconColor = if(pagerState.currentPage ==4)MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onPrimary,
                            painter = painterResource(id = R.drawable.baseline_settings_24),
                            iconContentDescription = "Navigate to Settings page",
                            fontColor = MaterialTheme.colorScheme.onPrimary,
                            text = "Settings",
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(4)
                                }

                            },
                        )
                    }

                )


            },

        ) { contentPadding ->
            //todo: THIS IS WHERE THE HORIZONTAL PAGER NEEDS OT GO

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                Log.d("TESTINGoNUPTHINGERS", "HorizontalPager-->onpress")
                            },
                            onTap = {

                                Log.d("TESTINGoNUPTHINGERS", "HorizontalPager-->TAP")
                            }
                        )
                    }
            ) { page ->

                // Our page content
                when (page) {
                     0 -> {
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
                                     EmptyFollowingListCard()
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
                                         density =screenDensity,
                                         loadUrl={url->
                                             navigateToStream()
                                             loadUrl(url)

                                         }
                                     )
                                 },
                                 gettingStreamError = {message ->
                                     GettingStreamsErrorCard(errorMessage = message)
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


                     } // END OF THE HOME VIEW
                     1 -> {


                         ModChannelView(
                             popBackStackNavigation = {   },
                             homeViewModel = homeViewModel,
                             streamViewModel =streamViewModel,
                             onNavigate = { dest ->

                             },
                             autoModViewModel = autoModViewModel,
                             updateModViewSettings = { oAuthToken,clientId,broadcasterId,moderatorId ->
                                 modViewViewModel.updateAutoModTokens(
                                     oAuthToken =oAuthToken,
                                     clientId =clientId,
                                     broadcasterId=broadcasterId,
                                     moderatorId =moderatorId
                                 )
                             },
                             createNewTwitchEventWebSocket ={modViewViewModel.createNewTwitchEventWebSocket()},
                             hapticFeedBackError={

                             },
                             logoutViewModel =logoutViewModel,
                             modViewViewModel=modViewViewModel,
                             searchViewModel=searchViewModel,
                             movePager={pagerValue ->
                                 scope.launch {
                                     pagerState.animateScrollToPage(pagerValue)
                                 }
                             },
                             contentPadding = contentPadding,
                             webViewAnimation={channelName ->webViewAnimation(channelName)}
                         )






                     }// END OF THE MOD CHANNELS VIEW
                     2 -> {
                         //todo:put catories here
                         SearchView(
                             onNavigate = { dest ->  },
                             homeViewModel=homeViewModel,
                             searchViewModel=searchViewModel,
                             hapticFeedBackError={ },
                             streamViewModel = streamViewModel,
                             autoModViewModel =autoModViewModel,
                             updateModViewSettings = { oAuthToken,clientId,broadcasterId,moderatorId ->
                                 modViewViewModel.updateAutoModTokens(
                                     oAuthToken =oAuthToken,
                                     clientId =clientId,
                                     broadcasterId=broadcasterId,
                                     moderatorId =moderatorId
                                 )
                             },
                             createNewTwitchEventWebSocket ={modViewViewModel.createNewTwitchEventWebSocket()},
                             chatSettingsViewModel=chatSettingsViewModel,
                             streamInfoViewModel=streamInfoViewModel,
                             modViewViewModel=modViewViewModel,
                             contentPadding = contentPadding,
                             webViewAnimation={channelName ->webViewAnimation(channelName)}


                             )

                     }// END OF THE CATEGORIES
                    3->{
                        ComposeDinoRunViews(
                            context = context,
                            modifier = Modifier
                                .padding(contentPadding)
                                .fillMaxSize()
                        )

                    }
                    4->{


//                        PingPongViewGLSurfaceViewComposable(
//                            context = context,
//                            modifier = Modifier
//                                .padding(contentPadding)
//                                .fillMaxSize()
//                        )
                        LoginLogoutScaffoldDrawerBox(
                            showLogoutDialog = {
                                showLogoutDialog()
                            },
                            loginWithTwitch = {
                                loginWithTwitch()
                            },
                            scaffoldState = scaffoldState,
                            userIsLoggedIn = userIsLoggedIn,
                            lowPowerModeActive=lowPowerModeActive,
                            changeLowPowerMode={newValue ->changeLowPowerMode(newValue)},
                            startService={startService()},
                            endService={endService()},
                            checkIfServiceRunning={checkIfServiceRunning()},
                            backgroundServiceChecked=backgroundServiceChecked,
                            changeBackgroundServiceChecked={newValue ->changeBackgroundServiceChecked(newValue)},
                            grantedNotification= grantedNotifications,
                            openAppSettings={openAppSettings()},
                            contentPadding = contentPadding

                        )
                    }
            }
        }

            // I actually think this is where I want the horizontal pager




        }
    }



/**

 *
 *  **LoginWithTwitchBottomModalButtonColumn** is a composable function representing a Button and text that is shown to the user when they are not logged in
 *
 * @param loginWithTwitch a function, when called, will log the user out
 * */
/**

 *
 *  **LoginWithTwitchBottomModalButtonColumn** is a composable function representing a Button and text that is shown to the user when they are not logged in
 *
 * @param loginWithTwitch a function, when called, will log the user out
 * */
@Composable
fun LoginWithTwitchBottomModalButtonColumn(
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


        /**
         *
         * - **LiveChannelRowItem** is a composable function that will show the individual information for each live stream
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
         * @param density a float meant to represent the screen density of the current device
         * @param updateClickedStreamInfo a function when called with a [ClickedStreamInfo] object,
         * will populate the view model with the needed [ClickedStreamInfo] information
         * */
        /**
         *
         * - **LiveChannelRowItem** is a composable function that will show the individual information for each live stream
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
         * @param density a float meant to represent the screen density of the current device
         * @param updateClickedStreamInfo a function when called with a [ClickedStreamInfo] object,
         * will populate the view model with the needed [ClickedStreamInfo] information
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
            density:Float,
            loadUrl:(String)->Unit

        ){
            Log.d("LiveChannelRowItem","height->$height")
            Log.d("LiveChannelRowItem","width->$width")
            Log.d("LiveChannelRowItem","density->$density")
            Log.d("LiveChannelRowItem","StreamData->${streamItem}")

            Row(
                modifier = Modifier.clickable {
                    Log.d("channelNameCheck","-->${streamItem.userLogin}")
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
                    //todo: UN COMMENT OUT


                    loadUrl(streamItem.userLogin)
                }
            ){
                ImageWithViewCountBox(
                    url = streamItem.thumbNailUrl,
                    height = height,
                    width = width,
                    viewCount = streamItem.viewerCount,
                    density =density
                )
                StreamTitleWithInfoColumn(
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
     * **LoginLogoutScaffoldDrawerBox** is a composable that is shown to the user when the [ScaffoldState] is set to OPEN
     *
     * @param userIsLoggedIn a boolean to determine if the user is logged in or not
     * @param loginWithTwitch a function, when called, will log the user out
     * @param scaffoldState the state of the [Scaffold]. Will be used to open and close the drawer of the Scaffold
     * @param userIsLoggedIn a boolean to determine if the user is logged in or not
     * @param lowPowerModeActive a Boolean used to determine if the user is in --low power mode-- or not
     * @param changeLowPowerMode a function, when called with a Boolean, will determine the state of [lowPowerModeActive]
     * */
    /**
     *
     * **LoginLogoutScaffoldDrawerBox** is a composable that is shown to the user when the [ScaffoldState] is set to OPEN
     *
     * @param userIsLoggedIn a boolean to determine if the user is logged in or not
     * @param loginWithTwitch a function, when called, will log the user out
     * @param scaffoldState the state of the [Scaffold]. Will be used to open and close the drawer of the Scaffold
     * @param userIsLoggedIn a boolean to determine if the user is logged in or not
     * @param lowPowerModeActive a Boolean used to determine if the user is in --low power mode-- or not
     * @param changeLowPowerMode a function, when called with a Boolean, will determine the state of [lowPowerModeActive]
     * */
    @Composable
    fun LoginLogoutScaffoldDrawerBox(
        showLogoutDialog: () -> Unit,
        loginWithTwitch: () -> Unit,
        scaffoldState: ScaffoldState,
        userIsLoggedIn: Boolean,
        lowPowerModeActive:Boolean,
        changeLowPowerMode:(Boolean)->Unit,
        startService:()->Unit,
        endService:()->Unit,
        checkIfServiceRunning:()->Boolean,
        backgroundServiceChecked:Boolean,
        changeBackgroundServiceChecked:(Boolean)->Unit,
        grantedNotification:Boolean,
        openAppSettings:() ->Unit,
        contentPadding: PaddingValues,
    ) {
        val context = LocalContext.current

        Box(modifier = Modifier
            .padding(contentPadding)
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

                AccountActionSwitchCard(
                    checkedValue =lowPowerModeActive,
                    changeCheckedValue={newValue ->changeLowPowerMode(newValue)}
                )
                LowPowerModeAnimatedColumnAnimatedVisibility(lowPowerModeActive)

                CreatingBackgroundServiceSwitch(
                    startService={startService()},
                    endService={endService()},
                    checkIfServiceRunning={checkIfServiceRunning()},
                    backgroundServiceChecked=backgroundServiceChecked,
                    changeBackgroundServiceChecked={newValue ->changeBackgroundServiceChecked(newValue)}
                )
                if(backgroundServiceChecked){
                    WhichNotification(
                        grantedNotification,
                        openAppSettings={openAppSettings()}
                    )
                }
                PrivacyPolicyActionCard(
                    onCardClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/thePlebDev/Modderz-privacy-policy/blob/main/README.md"))
                        startActivity(context,intent,null)
                    },
                    title="Privacy policy"
                )


            }//end of the column

        }

    }

@Composable
fun WhichNotification(
    grantedNotification:Boolean,
    openAppSettings:()->Unit
){
    if(grantedNotification){
        FullGrantedNotification()
    }else{
        DeniedNotification(
            openAppSettings={openAppSettings()}
        )
    }
}
@Composable
fun FullGrantedNotification(){
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(15.dp)){
        Text("Status: ", Modifier.fillMaxWidth(),color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineMedium.fontSize)
        Text("   - Active", Modifier.fillMaxWidth(),color = Color.Green,fontSize = 18.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Text("Benefits: ", Modifier.fillMaxWidth(),color = MaterialTheme.colorScheme.onPrimary,fontSize = MaterialTheme.typography.headlineMedium.fontSize)
        Text("   - Stream will now continue to play when the application is closed", Modifier.fillMaxWidth(),color = MaterialTheme.colorScheme.onPrimary, fontSize = 18.sp)
        Text("   - Notification will be shown", Modifier.fillMaxWidth(),color = MaterialTheme.colorScheme.onPrimary, fontSize = 18.sp)
    }
}
@Composable
fun DeniedNotification(
    openAppSettings:()->Unit
){
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(15.dp)){
        Text("Status: ", Modifier.fillMaxWidth(),color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineMedium.fontSize)
        Text("   - Denied", Modifier.fillMaxWidth(),color = Color.Red,fontSize = 18.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Text("Benefits: ", Modifier.fillMaxWidth(),color = MaterialTheme.colorScheme.onPrimary,fontSize = MaterialTheme.typography.headlineMedium.fontSize)
        Text("   - You must allow notifications before background audio can play", Modifier.fillMaxWidth(),color = MaterialTheme.colorScheme.onPrimary, fontSize = 18.sp)
        Text("   - Check notification permissions in settings", Modifier.fillMaxWidth(),color = MaterialTheme.colorScheme.onPrimary, fontSize = 18.sp)

        Button(
            onClick = { openAppSettings() },
            colors =  ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary),
            modifier = Modifier.padding(5.dp),
            shape = RoundedCornerShape(4.dp)
        )
        {
            Text(text = "Open Settings", color = MaterialTheme.colorScheme.onSecondary, fontSize = MaterialTheme.typography.bodyMedium.fontSize)
        }
    }
}


/**
 *
 * **LowPowerModeAnimatedColumnAnimatedVisibility** is a composable that is shown to the user when the [ScaffoldState] is set to OPEN
 *
 * @param checked a Boolean  used to determine if there should be an animation or not
 * */
    /**
 *
 * **LowPowerModeAnimatedColumnAnimatedVisibility** is a composable that is shown to the user when the [ScaffoldState] is set to OPEN
 *
 * @param checked a Boolean  used to determine if there should be an animation or not
 * */
@Composable
    fun LowPowerModeAnimatedColumnAnimatedVisibility(checked:Boolean){
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
fun CreatingBackgroundServiceSwitch(
    startService:()->Unit,
    endService:()->Unit,
    checkIfServiceRunning:()->Boolean,
    backgroundServiceChecked:Boolean,
    changeBackgroundServiceChecked:(Boolean)->Unit
){



    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .clickable {
                Log.d(
                    "BackgroundStreamServiceOnStartCommand",
                    "running --->${checkIfServiceRunning()}"
                )

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
            Text("Background audio", fontSize = MaterialTheme.typography.headlineMedium.fontSize,color = MaterialTheme.colorScheme.onSecondary)
            SwitchWithIcon(
                checkedValue =backgroundServiceChecked,
                changeCheckedValue={newValue ->
                    changeBackgroundServiceChecked(newValue)
                },
                icon = Icons.Filled.Check,
            )

        }
    }

}

/**
 *
 * **AccountActionSwitchCard** is a composable that is shown to the user when the [ScaffoldState] is set to OPEN
 *
 * @param checkedValue a Boolean used to determine if the internal switch should change its state
 * @param changeCheckedValue a function, when called, will change the value of the [checkedValue]
 * */
    /**
 *
 * **AccountActionSwitchCard** is a composable that is shown to the user when the [ScaffoldState] is set to OPEN
 *
 * @param checkedValue a Boolean used to determine if the internal switch should change its state
 * @param changeCheckedValue a function, when called, will change the value of the [checkedValue]
 * */
@Composable
    fun AccountActionSwitchCard(
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
     * - **AccountActionCard** is a clickable card that can be clicked on the trigger the action of [onCardClick]
     *
     * @param scaffoldState the state of the [Scaffold]. Will be used to open and close the drawer of the Scaffold
     * @param onCardClick a function will be run once the Card is clicked
     * @param title a string representing a text that will be shown on the Card and should tell the user what the clickable card does
     * @param iconImageVector a [ImageVector] that will be displayed after the [title]
     * */
    /**
     *
     * - **AccountActionCard** is a clickable card that can be clicked on the trigger the action of [onCardClick]
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

@Composable
fun PrivacyPolicyActionCard(
    onCardClick: () -> Unit,
    title:String,
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .clickable {

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
                painter = painterResource(id =R.drawable.baseline_privacy_policy),
                contentDescription ="Privacy policy",
                modifier = Modifier.size(35.dp),
                tint =  MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}





    /**
     *
     * - **LiveChannelsLazyColumn** is a composable function that will show the individual information for each live stream
     * retrieved from the Twitch server
     *
     * @param bottomModalState [ModalBottomSheetState] object used to determine if the Bottom modal should pop up or not
     * @param followedStreamerList a [NetworkNewUserResponse] object representing the list of the user's followed streams
     * @param contentPadding a [PaddingValues] object meant to be used to determine the necessary space needed to properly display
     * the composable to the user
     * @param loadingIndicator a composable function that will be show to the user when [followedStreamerList] enters a loading state
     * @param emptyList a composable function that will be shown to the user when [followedStreamerList] is a success state but has no data
     * @param liveChannelRowItem a composable function that will show the individual information for each live stream retrieved from the Twitch server
     * @param gettingStreamError a composable function that will be shown to the user to the user when [followedStreamerList] enters a Failed state
     * @param newUserAlert a composable function that will be show to the user if [followedStreamerList]  enters a new user state
     * @param showNetworkRefreshError a composable function that will be show to the user if [followedStreamerList] enters a network error state
     * @param hapticFeedBackError a composable function that will be run when there is an error of anykind from [followedStreamerList]
     * */
    /**
     *
     * - **LiveChannelsLazyColumn** is a composable function that will show the individual information for each live stream
     * retrieved from the Twitch server
     *
     * @param bottomModalState [ModalBottomSheetState] object used to determine if the Bottom modal should pop up or not
     * @param followedStreamerList a [NetworkNewUserResponse] object representing the list of the user's followed streams
     * @param contentPadding a [PaddingValues] object meant to be used to determine the necessary space needed to properly display
     * the composable to the user
     * @param loadingIndicator a composable function that will be show to the user when [followedStreamerList] enters a loading state
     * @param emptyList a composable function that will be shown to the user when [followedStreamerList] is a success state but has no data
     * @param liveChannelRowItem a composable function that will show the individual information for each live stream retrieved from the Twitch server
     * @param gettingStreamError a composable function that will be shown to the user to the user when [followedStreamerList] enters a Failed state
     * @param newUserAlert a composable function that will be show to the user if [followedStreamerList]  enters a new user state
     * @param showNetworkRefreshError a composable function that will be show to the user if [followedStreamerList] enters a network error state
     * @param hapticFeedBackError a composable function that will be run when there is an error of anykind from [followedStreamerList]
     * */
    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
    @Composable
    fun LiveChannelsLazyColumn(
        bottomModalState: ModalBottomSheetState,
        followedStreamerList: NetworkNewUserResponse<List<StreamData>>,
        contentPadding: PaddingValues,
        showNetworkRefreshError:Boolean,
        hapticFeedBackError:() ->Unit,
        loadingIndicator:@Composable () -> Unit,
        emptyList:@Composable () -> Unit,
        liveChannelRowItem:@Composable (streamItem: StreamData) -> Unit,
        gettingStreamError:@Composable (message:String) -> Unit,
        newUserAlert:@Composable (message:String) ->Unit,


        ){
        val fontSize =MaterialTheme.typography.headlineMedium.fontSize
        val scope = rememberCoroutineScope()

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

                                        emptyList()

                                }
                            }
                            items(listData,key = { streamItem -> streamItem.userId }) { streamItem ->

                                    liveChannelRowItem(streamItem)
                            }



                            // end of the lazy column
                        }
                    }
                    is NetworkNewUserResponse.Failure -> {

                        item {
                            val message =followedStreamerList.e.message ?:"Error! please pull down to refresh"
                            gettingStreamError(message)

                        }
                    }
                    is NetworkNewUserResponse.NetworkFailure -> {
                        val message =followedStreamerList.e.message ?:"Error! please pull down to refresh"

                        item{

                                gettingStreamError(message)

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
     *
     * - **EmptyFollowingListCard** is a composable function that will appear to the user when there are no live channels
     *
     * */
    /**
     *
     * - **EmptyFollowingListCard** is a composable function that will appear to the user when there are no live channels
     *
     * */
    @Composable
    fun EmptyFollowingListCard() {
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
     *
     * - **GettingStreamsErrorCard** is a composable function that will appear to the user when there was an error
     * retrieving the streams from the Twitch server
     *
     * @param errorMessage a String representing what the user sees when this composable is shown
     *
     * */
    /**
     *
     * - **GettingStreamsErrorCard** is a composable function that will appear to the user when there was an error
     * retrieving the streams from the Twitch server
     *
     * @param errorMessage a String representing what the user sees when this composable is shown
     *
     * */
    @Composable
    fun GettingStreamsErrorCard(
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
 * - **StreamTitleWithInfoColumn** is a Column that shows information about the streamer and the game they are playing
 *
 * @param streamerName a String representing the name of the live streamer
 * @param streamTitle a String representing the title of the streamer's stream
 * @param gameTitle a String representing the title of the game they are playing
 * */
/**
 *
 * - **StreamTitleWithInfoColumn** is a Column that shows information about the streamer and the game they are playing
 *
 * @param streamerName a String representing the name of the live streamer
 * @param streamTitle a String representing the title of the streamer's stream
 * @param gameTitle a String representing the title of the game they are playing
 * */
@Composable
fun StreamTitleWithInfoColumn(
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
 * - **ImageWithViewCountBox** is a Box that uses the SubcomposeAsyncImage to load and show the image we get from the Twitch server.
 * It will show the thumbnail for the stream and their current viewer count
 *
 * @param url a String representing the thumbnail image
 * @param height a Int representing the height of the image. The height is in a 9/16 aspect ration
 * @param width a Int representing the width of the image. The width is in a 9/16 aspect ration
 * @param viewCount a Int representing the number of current viewers the streamer has
 * */
/**
 *
 * - **ImageWithViewCountBox** is a Box that uses the SubcomposeAsyncImage to load and show the image we get from the Twitch server.
 * It will show the thumbnail for the stream and their current viewer count
 *
 * @param url a String representing the thumbnail image
 * @param height a Int representing the height of the image. The height is in a 9/16 aspect ration
 * @param width a Int representing the width of the image. The width is in a 9/16 aspect ration
 * @param viewCount a Int representing the number of current viewers the streamer has
 * */
@Composable
fun ImageWithViewCountBox(
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



/**
 *  **setTagAndId** is a custom [semantics][androidx.compose.ui.semantics.semantics] modifier used to
 *  help with accessibility and testing
 * */
/**
 *  **setTagAndId** is a custom [semantics][androidx.compose.ui.semantics.semantics] modifier used to
 *  help with accessibility and testing
 * */
@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.setTagAndId(tag: String): Modifier {
    return this
        .semantics { this.testTagsAsResourceId = true }
        .testTag(tag)
}






