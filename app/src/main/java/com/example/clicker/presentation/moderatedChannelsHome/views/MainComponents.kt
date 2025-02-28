package com.example.clicker.presentation.moderatedChannelsHome.views

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.clicker.R
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.presentation.sharedViews.ButtonScope

import com.example.clicker.presentation.sharedViews.ErrorScope
import com.example.clicker.presentation.sharedViews.LazyListLoadingIndicator

import com.example.clicker.presentation.sharedViews.NoDrawerScaffold
import com.example.clicker.presentation.sharedViews.PullToRefreshComponent
import com.example.clicker.presentation.stream.models.ClickedStreamInfo

import com.example.clicker.util.NetworkNewUserResponse
import com.example.clicker.util.Response


/**
     *
     * @param onNavigate a function used to navigate from the home page to the individual stream view
     * @param height a Int representing the height in a aspect ratio that will make the images look nice
     * @param width a Int representing the width in a aspect ratio that will make the images look nice
     * @param density a float meant to represent the screen density of the current device
     * */
    @Composable
    fun MainModView(
        popBackStackNavigation: () -> Unit,
        height: Int,
        width: Int,
        density:Float,
        offlineModChannelList:List<String>,
        liveModChannelList:List<StreamData>,
        modChannelResponseState: NetworkNewUserResponse<Boolean>,
        refreshing:Boolean,
        refreshFunc:()->Unit,
        showNetworkMessage:Boolean,
        updateStreamerName: (String, String,String,String) -> Unit,
        updateClickedStreamInfo:(ClickedStreamInfo)->Unit,
        onNavigate: (Int) -> Unit,
        clientId: String,
        userId: String,
        networkMessageColor:Color,
        networkMessage: String,
        showLoginModal:()->Unit,
        showNetworkRefreshError:Boolean,
        hapticFeedBackError:() ->Unit,
        getTopGames:()->Unit,
        movePager: (Int) -> Unit,
    ){
        NoDrawerScaffold(
            topBar = {
                IconTextTopBarRow(
                    icon ={
                        BasicIcon(
                            color = MaterialTheme.colorScheme.onPrimary,
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Navigate back to home page",
                            onClick = {movePager(0)}
                        )
                    },
                    text =""

                    )
            }
            , bottomBar={
                TripleButtonNavigationBottomBarRow(
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    horizontalArrangement=Arrangement.SpaceAround,
                    firstButton = {
                        IconOverTextColumn(
                            iconColor = MaterialTheme.colorScheme.onPrimary,
                            text = "Home",
                            imageVector = Icons.Default.Home,
                            iconContentDescription = "Navigate back to home page",
                            onClick = {
                                movePager(0)
                                      },
                            fontColor = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    secondButton = {
                        PainterResourceIconOverTextColumn(
                            iconColor =MaterialTheme.colorScheme.secondary,
                            text = "Mod Channels",
                            painter = painterResource(R.drawable.moderator_white),
                            iconContentDescription = "Click to stay on mod page",
                            onClick ={},
                            fontColor = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    thirdButton = {

                        this.PainterResourceIconOverTextColumn(
                            iconColor = MaterialTheme.colorScheme.onPrimary,
                            painter = painterResource(id = R.drawable.baseline_category_24),
                            iconContentDescription = "Navigate to search bar",
                            fontColor = MaterialTheme.colorScheme.onPrimary,
                            text = "Categories",
                            onClick = {
                                getTopGames()
                                movePager(2)
                            },
                        )
                    },
                )
            }
        ) {contentPadding ->
            PullToRefreshComponent(
                padding = contentPadding,
                refreshing = refreshing,
                refreshFunc = { refreshFunc() },
                showNetworkMessage = showNetworkMessage,
                networkStatus = { modifier ->
                    NetworkStatusCard(
                        modifier = modifier,
                        color = networkMessageColor,
                        networkMessage = networkMessage
                    )
                }
            ){
                ModChannelsResponse(
                    height,
                    width,
                    density,
                    offlineModChannelList = offlineModChannelList,
                    liveModChannelList=liveModChannelList,
                    modChannelResponseState =modChannelResponseState,
                    updateStreamerName ={
                            streamerName,clientId,broadcasterId,userId ->
                        updateStreamerName(streamerName,clientId,broadcasterId,userId)

                    },
                    updateClickedStreamInfo={clickedStreamInfo ->updateClickedStreamInfo(clickedStreamInfo)},
                    onNavigate={destination -> onNavigate(destination)},
                    userId = userId,
                    clientId = clientId,
                    loadingIndicator = {
                        LazyListLoadingIndicator()
                    },
                    showLoginModal ={showLoginModal()},
                    showNetworkRefreshError =showNetworkRefreshError,
                    hapticFeedBackError={hapticFeedBackError()}

                )
            }

        }
    }




        /**
         *
         * - ModChannelsResponse is a composable function used to show the current state of the data to
         * to the user. Either LOADING,SUCCESS OR FAILURE. All dependent on the network call to get channels where the
         * user is a moderator
         * @param density a Float representing the screen density of the current device
         * @param height a Int representing the height of the image. The height is in a 9/16 aspect ration
         * @param width a Int representing the width of the image. The width is in a 9/16 aspect ration
         * @param offlineModChannelList subject to change
         * @param liveModChannelList subject to change
         * @param modChannelResponseState a [Response] object used to determine if there is any data to be shown to the user
         * or not
         * */
        @OptIn(ExperimentalFoundationApi::class)
        @Composable
        fun ModChannelsResponse(
            height: Int,
            width: Int,
            density:Float,
            offlineModChannelList:List<String>,
            liveModChannelList:List<StreamData>,
            modChannelResponseState: NetworkNewUserResponse<Boolean>,
            updateStreamerName: (String, String,String,String) -> Unit,
            updateClickedStreamInfo:(ClickedStreamInfo)->Unit,
            onNavigate: (Int) -> Unit,
            clientId:String,
            userId:String,
            showLoginModal:()->Unit,
            loadingIndicator:@Composable () -> Unit,
            showNetworkRefreshError:Boolean,
            hapticFeedBackError:() ->Unit,

            ){
            val fontSize =MaterialTheme.typography.headlineMedium.fontSize
            val errorScope = remember(){ ErrorScope(fontSize) }

            Box(modifier = Modifier.fillMaxSize()){
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ){
                    when(modChannelResponseState){
                        is NetworkNewUserResponse.Loading ->{
                            item{

                                    loadingIndicator()


                            }

                        }
                        is NetworkNewUserResponse.Success ->{
                            stickyHeader {
                                ModHeader("Live")
                            }
                            if(liveModChannelList.isEmpty()){
                                item{
                                    EmptyList(
                                        message ="No live moderated channels found"
                                    )
                                }
                            }
                            items(liveModChannelList){streamInfo ->


                                LiveModChannelItem(
                                    height =height,
                                    width=width,
                                    density= density,
                                    streamerName = streamInfo.userLogin,
                                    broadcasterId = streamInfo.userId,
                                    streamTitle=streamInfo.title,
                                    gameTitle =streamInfo.gameName,
                                    viewCount = streamInfo.viewerCount,
                                    thumbnailURL = streamInfo.thumbNailUrl,
                                    updateStreamerName ={
                                            streamerName,clientId,broadcasterId,userId ->
                                        updateStreamerName(streamerName,clientId,broadcasterId,userId)

                                    },
                                    onNavigate ={destination -> onNavigate(destination)},
                                    clientId =clientId,
                                    userId=userId,
                                    streamItem = streamInfo,
                                    updateClickedStreamInfo={clickedStreamInfo ->updateClickedStreamInfo(clickedStreamInfo)},
                                )
                            }

                            stickyHeader {
                                ModHeader("Offline")
                            }
                            if(offlineModChannelList.isEmpty()){
                                item{
                                    EmptyList(
                                        message ="No offline moderated channels found"
                                    )
                                }
                            }


                            items(offlineModChannelList){channelName ->
                                OfflineModChannelItem(
                                    height,
                                    width,
                                    density,
                                    channelName = channelName
                                )
                            }

                        }
                        is NetworkNewUserResponse.Failure ->{
                            item{
                                val message = modChannelResponseState.e.message ?:"Error! Pull to refresh"
                                ErrorPullToRefresh(message)
                            }

                        }
                        is NetworkNewUserResponse.NetworkFailure->{
                            item{
                                val message = modChannelResponseState.e.message ?:"Error! Pull to refresh"
                                ErrorPullToRefresh(message)
                            }

                        }
                        is NetworkNewUserResponse.Auth401Failure->{
                            item{

                                ErrorPullToRefresh("Error! Re-login with Twitch")
                            }
                            showLoginModal()

                        }

                    }
                }
                if(showNetworkRefreshError){
                    Box(modifier = Modifier.align(Alignment.BottomCenter)){
                        hapticFeedBackError()
                        with(errorScope){
                            this.NetworkErrorMessage()
                        }
                    }

                }

            }// end of the box


        }


        /**
         *
         * - OfflineModChannelItem is a composable function meant to show the individual mod channels that are offline
         *
         * @param height a Int representing the height in a aspect ratio that will make the images look nice
         * @param width a Int representing the width in a aspect ratio that will make the images look nice
         * @param density a float meant to represent the screen density of the current device
         * @param channelName a String meant to represent the name of the channel shown to the user
         * */
        @Composable
        fun OfflineModChannelItem(
            height: Int,
            width: Int,
            density:Float,
            channelName:String

        ){
            Row(
                modifier = Modifier
                    .padding(10.dp)
                    .clickable {}
            ){
                OfflineModChannelImage(height,width, density)
                StreamerName(channelName)
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )
        }

        /**
         *
         * - LiveModChannelItem is a composable function meant to show the individual mod channels that are live
         *
         * @param height a Int representing the height in a aspect ratio that will make the images look nice
         * @param width a Int representing the width in a aspect ratio that will make the images look nice
         * @param density a float meant to represent the screen density of the current device
         * @param streamerName a String meant to represent the name of the channel shown to the user
         * @param streamTitle a String meant to represent the name of the title of the stream
         * @param gameTitle a String meant to represent the name of the title of the game
         *
         * @param thumbnailURL a String used to load the screen shot of what the streamer is playing
         * @param viewCount a Int meant to represent how many users are viewing the stream
         * */
        @Composable
        fun LiveModChannelItem(
            broadcasterId:String,
            height: Int,
            width: Int,
            density:Float,
            streamerName:String,
            streamTitle:String,
            gameTitle:String,
            thumbnailURL:String,
            viewCount: Int,
            updateStreamerName: (String, String,String,String) -> Unit,
            updateClickedStreamInfo:(ClickedStreamInfo)->Unit,
            streamItem: StreamData,
            onNavigate: (Int) -> Unit,
            clientId:String,
            userId:String,
        ){
            Row(
                modifier = Modifier
                    .padding(10.dp)
                    .clickable {
                        Log.d("updateStreamerName", "streamTitle --> $streamTitle")
                        //todo: navigate to stream and update all the necessary information
                        updateStreamerName(
                            streamerName,
                            clientId,
                            broadcasterId,
                            userId
                        )
                        updateClickedStreamInfo(
                            ClickedStreamInfo(
                                channelName = streamItem.userLogin,
                                streamTitle = streamItem.title,
                                category = streamItem.gameName,
                                tags = streamItem.tags,
                                adjustedUrl = streamItem.thumbNailUrl
                            )
                        )
                        onNavigate(R.id.action_modChannelsFragment_to_streamFragment)
                    }
            ){
                OnlineModChannelImage(
                    height = height,
                    width = width,
                    density = density,
                    thumbnailURL= thumbnailURL,
                    viewCount = viewCount
                )
                StreamInfo(streamerName,streamTitle,gameTitle)
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )
        }


        /**
         * - StreamInfo is a composable function meant to show the individual information about a stream
         *
         * @param streamTitle a String meant to represent the name of the title of the stream
         * @param gameTitle a String meant to represent the name of the title of the game
         * @param streamerName a String meant to represent the name of the streamer
         * */
        @Composable
        fun StreamInfo(
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
         * - ErrorPullToRefresh is a composable function that will appear to the user when a generic error
         * has happened and they have to pull to make the request again
         *
         * @param errorMessage means to represent the message that is shown to the user when an error occurs
         *
         * */
        @Composable
        fun ErrorPullToRefresh(
            errorMessage:String
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
                    .clickable { },
                elevation = 10.dp,
                backgroundColor = MaterialTheme.colorScheme.secondary
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,

                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = stringResource(R.string.pull_to_refresh_icon_description),
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.size(35.dp)
                    )
                    Text(
                        errorMessage,
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                        color =MaterialTheme.colorScheme.onSecondary
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = stringResource(R.string.pull_to_refresh_icon_description),
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.size(35.dp)
                    )
                }
            }
        }





        /**
         *
         * - StreamerName is a composable function meant to show the individual streamer's name
         *
         * @param channelName a String meant to represent the name of the streamer
         * */
        @Composable
        fun StreamerName(channelName:String){
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(
                    channelName,
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        /**
         * - ModHeader is a composable function meant to act as a sticky header in a [LazyColumn]
         *
         * @param headerTitle a String meant to represent the name of the streamer
         * */
        @Composable
        fun ModHeader(
            headerTitle:String
        ){
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(5.dp))
                    .background(color = MaterialTheme.colorScheme.primary)

            ){
                Text(
                    headerTitle,
                    color =MaterialTheme.colorScheme.onPrimary,
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                    modifier = Modifier.padding(horizontal = 5.dp)
                )
            }

        }

        /**
         * - EmptyList is a composable function meant to shown to the user when there is a empty [LazyColumn]
         *
         * @param message a String meant to represent a message shown directly to the user
         * */
        @Composable
        fun EmptyList(
            message:String
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
                    .clickable { },
                elevation = 10.dp,
                backgroundColor = MaterialTheme.colorScheme.primary,
                border = BorderStroke(2.dp,MaterialTheme.colorScheme.secondary)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    Text(message, fontSize = MaterialTheme.typography.headlineMedium.fontSize,color = MaterialTheme.colorScheme.onPrimary)

                }
            }
        }


        /**
         * - OfflineModChannelImage is a composable function meant to mimic a streamer's stream thumbnail
         *
         * @param height a Int representing the height in a aspect ratio that will make the images look nice
         * @param width a Int representing the width in a aspect ratio that will make the images look nice
         * @param density a float meant to represent the screen density of the current device
         * */
        @Composable
        fun OfflineModChannelImage(
            height: Int,
            width: Int,
            density:Float,

        ){
            val adjustedHeight = height/density
            val adjustedWidth = width/density
            Column() {
                Box(
                    modifier = Modifier
                        .height(adjustedHeight.dp)
                        .width(adjustedWidth.dp)
                        .clip(RectangleShape)
                        .background(Color.DarkGray)
                ){
                    Text("Offline",modifier = Modifier.align(Alignment.Center), fontSize = MaterialTheme.typography.headlineMedium.fontSize,color = Color.White)
                }
                Spacer(modifier=Modifier.height(10.dp))
            }
        }


/**
 * - OnlineModChannelImage is a composable function meant to demonstrate that a user is live and show a provided thumbnail
 *
 * @param height a Int representing the height in a aspect ratio that will make the images look nice
 * @param width a Int representing the width in a aspect ratio that will make the images look nice
 * @param density a float meant to represent the screen density of the current device
 * @param viewCount meant to represent the number of viewers a stream currently has
 * @param thumbnailURL mean to represent the URL of a thumbnail that is shown to users
 * */
        @Composable
        fun OnlineModChannelImage(
            height: Int,
            width: Int,
            density:Float,
            viewCount:Int,
            thumbnailURL:String
        ){
            Box() {
                val adjustedHeight = height/density
                val adjustedWidth = width/density
                SubcomposeAsyncImage(
                    model = thumbnailURL,
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
 * ModChannelsBottomModalSheetContent() is composable meant to be shown in a [ModalBottomSheetLayout] sheet content
 *
 * @param loginWithTwitch a function used to promp the user to login with Twitch
 * */
@Composable
fun ModChannelsBottomModalSheetContent(
    loginWithTwitch: () -> Unit,
){
    val fontSize =MaterialTheme.typography.headlineSmall.fontSize
    val buttonScope = remember(){ ButtonScope(fontSize) }
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
        with(buttonScope){
            this.Button(
                text ="Log out of Twitch",
                onClick = { loginWithTwitch()},
            )
        }
    }
}



















