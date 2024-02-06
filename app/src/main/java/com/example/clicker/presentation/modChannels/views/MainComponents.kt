package com.example.clicker.presentation.modChannels.views

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
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

import com.example.clicker.presentation.modChannels.views.ModChannelComponents.Parts.EmptyList
import com.example.clicker.util.Response
import kotlinx.coroutines.delay

import kotlinx.coroutines.launch

/**
 * - Contains 1 implementation:
 * 1) [MainModView]
 *
 * - ModChannelComponents represents all the UI composables that create the UI experience for the ModChannelView
 *
 *
 * */

object ModChannelComponents{
    /**THIS IS THE MAIN IMPLEMENTATION LEVEL*/

    /**
     * - Implementation of [Builders.ScaffoldBuilder].
     * - Contains 3 parts:
     * 1) [CustomTopBar][Parts.CustomTopBar]
     * 2) [CustomBottomBar][Parts.CustomBottomBar]
     * 3) [ModChannelsResponse][Parts.ModChannelsResponse]
     *
     * @param onNavigate a function used to navigate from the home page to the individual stream view
     * @param height a Int representing the height in a aspect ratio that will make the images look nice
     * @param width a Int representing the width in a aspect ratio that will make the images look nice
     * @param density a float meant to represent the screen density of the current device
     * */
    @Composable
    fun MainModView(
        onNavigate: () -> Unit,
        height: Int,
        width: Int,
        density:Float,
        offlineModChannelList:List<String>,
        liveModChannelList:List<StreamInfo>,
        modChannelResponseState: Response<Boolean>,
        refreshing:Boolean,
        refreshFunc:()->Unit,
    ){
        Builders.ScaffoldBuilder(
            topBar = {
                Parts.CustomTopBar(
                    onNavigate ={ onNavigate()}
                )
            },
            bottomBar={
                Parts.CustomBottomBar(
                    onNavigate ={ onNavigate()}
                )
            },
            pullToRefreshList = {contentPadding ->
                TestingPullToRefresh(
                    padding =contentPadding,
                    refreshing = refreshing,
                    refreshFunc = {refreshFunc()}
                ){
                    Parts.ModChannelsResponse(
                        contentPadding,
                        height,
                        width,
                        density,
                        offlineModChannelList = offlineModChannelList,
                        liveModChannelList=liveModChannelList,
                        modChannelResponseState =modChannelResponseState

                    )
                }
            },
        )
    }


    /**BUILDERS BELOW THIS*/
    /**
     * Builder represents the most generic parts of [ModChannelComponents] and should be thought of as layout guides used
     * by the implementations above
     * */
   private object Builders{

        /**
         * - ScaffoldBuilder is used inside of  [ModChannelComponents].
         *
         *
         * @param topBar a composable meant to act as the UI for this Scaffolds top bar
         * @param bottomBar a composable meant to act as the UI for this Scaffolds bottom bar
         * This will get covered by the scaffold
         * @param modChannelList a composable that will act as the list to display all the items shown to the user
         * */
        @Composable
        fun ScaffoldBuilder(
            topBar:@Composable () -> Unit,
            bottomBar:@Composable () -> Unit,
            pullToRefreshList:@Composable (contentPadding:PaddingValues) -> Unit,
        ){

            Scaffold(
                backgroundColor= MaterialTheme.colorScheme.primary,
                topBar = {
                    topBar()
                },
                bottomBar = {
                    bottomBar()
                },
            ) { contentPadding ->

                pullToRefreshList(contentPadding)

            }
        }
    } /***END OF THE BUILDERS****/


    @Composable
    fun TestingPullToRefresh(
        padding: PaddingValues,
        refreshing:Boolean,
        refreshFunc:()->Unit,
        content:@Composable () -> Unit,
    ){


        PullToRefresh(
            state = rememberPullToRefreshState(isRefreshing = refreshing),
            onRefresh = { refreshFunc() },
            indicatorPadding = padding
        ) {
            content()
        }
    }



    /**
     * Parts represents the most individual parts of [ModChannelComponents] and should be thought of as the individual
     * pieces that are used inside of a [Builders] to create a [ModChannelComponents] implementation
     * */
    private object Parts{

        /**
         * - Contains 1 extra parts:
         * 1) [ModChannelList]
         *
         * - ModChannelsResponse is a composable function used to show the current state of the data to
         * to the user. Either LOADING,SUCCESS OR FAILURE. All dependent on the network call to get channels where the
         * user is a moderator
         *
         * @param contentPadding it is a nullable list of all the live channels returned by the twitch server
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
            contentPadding: PaddingValues,
            height: Int,
            width: Int,
            density:Float,
            offlineModChannelList:List<String>,
            liveModChannelList:List<StreamInfo>,
            modChannelResponseState: Response<Boolean>

        ){
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = contentPadding){
                when(modChannelResponseState){
                    is Response.Loading ->{
                        item{
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ){
                                CircularProgressIndicator()

                            }
                        }

                    }
                    is Response.Success ->{
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
                                height,
                                width,
                                density,
                                channelName = streamInfo.streamerName,
                                streamTitle=streamInfo.streamTitle,
                                gameTitle =streamInfo.gameTitle,
                                viewCount = streamInfo.views,
                                url = streamInfo.url
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
                    is Response.Failure ->{
                        item{
                            val message = modChannelResponseState.e.message ?:"Error! Pull to refresh"
                            ErrorPullToRefresh(message)
                        }

                    }

                }
            }

        }

        /**
         * - Contains 3 extra parts:
         * 1) [ModHeader]
         * 2) [EmptyList]
         * 3) [LiveModChannelItem]
         *
         * - LiveChannelRowItem is a composable function that will show the individual information for each live stream
         * retrieved from the Twitch server
         *
         * @param contentPadding it is a nullable list of all the live channels returned by the twitch server
         * @param density a Float representing the screen density of the current device
         * @param height a Int representing the height of the image. The height is in a 9/16 aspect ration
         * @param width a Int representing the width of the image. The width is in a 9/16 aspect ration
         * @param offlineModChannelList subject to change
         * @param liveModChannelList subject to change
         * */
        @OptIn(ExperimentalFoundationApi::class)
        @Composable
        fun ModChannelList(
            contentPadding: PaddingValues,
            height: Int,
            width: Int,
            density:Float,
            offlineModChannelList:List<String>,
            liveModChannelList:List<StreamInfo>,
        ){
            LazyColumn(modifier = Modifier.fillMaxSize().padding(contentPadding)) {
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
                        height,
                        width,
                        density,
                        channelName = streamInfo.streamerName,
                        streamTitle=streamInfo.streamTitle,
                        gameTitle =streamInfo.gameTitle,
                        viewCount = streamInfo.views,
                        url = streamInfo.url
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
        }
        /**
         * - Contains 2 extra parts:
         * 1) [OfflineModChannelImage]
         * 2) [StreamerName]
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
            //updateStreamerName: (String, String, String, String) -> Unit,
         //    streamItem: StreamInfo,
//    clientId: String,
//    userId:String,
//    onNavigate: (Int) -> Unit,
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
         * - Contains 2 extra parts:
         * 1) [OfflineModChannelImage]
         * 2) [StreamInfo]
         *
         * - LiveModChannelItem is a composable function meant to show the individual mod channels that are live
         *
         * @param height a Int representing the height in a aspect ratio that will make the images look nice
         * @param width a Int representing the width in a aspect ratio that will make the images look nice
         * @param density a float meant to represent the screen density of the current device
         * @param channelName a String meant to represent the name of the channel shown to the user
         * @param streamTitle a String meant to represent the name of the title of the stream
         * @param gameTitle a String meant to represent the name of the title of the game
         *
         * @param url a String used to load the screen shot of what the streamer is playing
         * @param viewCount a Int meant to represent how many users are viewing the stream
         * */
        @Composable
        fun LiveModChannelItem(
            height: Int,
            width: Int,
            density:Float,
            channelName:String,
            streamTitle:String,
            gameTitle:String,
            url:String,
            viewCount: Int
        ){
            Row(
                modifier = Modifier
                    .padding(10.dp)
                    .clickable {}
            ){
                OnlineModChannelImage(
                    height = height,
                    width = width,
                    density = density,
                    url= url,
                    viewCount = viewCount
                )
                StreamInfo(channelName,streamTitle,gameTitle)
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )
        }


        /**
         * - Contains 0 extra parts:

         *
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
         * - ErrorPullToRefresh is a composable function that will appear to the user when a generic error
         * has happened and they have to pull to make the request again
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
                        fontSize = 20.sp,
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
         * - Contains 0 extra parts:
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
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        /**
         * - Contains 0 extra parts:
         *
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
                    fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 5.dp)
                )
            }


        }

        /**
         * - Contains 0 extra parts:
         *
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

                    Text(message, fontSize = 20.sp,color = MaterialTheme.colorScheme.onPrimary)

                }
            }
        }

        /**
         * - Contains 0 extra parts:
         *
         * - CustomTopBar is a composable function meant to act as the top bar inside of a [Scaffold]
         *
         * @param onNavigate a function used to navigate between fragments
         * */
        @Composable
        fun CustomTopBar(
            onNavigate: () -> Unit
        ) {
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
                        Icons.Filled.ArrowBack,
                        "Navigate back to home Fragment",
                        modifier = Modifier
                            .size(35.dp)
                            .clickable {
                               // onNavigate(R.id.action_modChannelsFragment_to_homeFragment)
                                onNavigate()
                            },
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                    Text(
                        "Channels you mod for",
                        fontSize = 25.sp,
                        modifier = Modifier.padding(start = 20.dp),
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }

        /**
         * - Contains 0 extra parts:
         *
         * - CustomBottomBar is a composable function meant to act as the bottom bar inside of a [Scaffold]
         *
         * @param onNavigate a function used to navigate between fragments
         * */
        @Composable
        fun CustomBottomBar(
            onNavigate: () -> Unit,
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(MaterialTheme.colorScheme.primary),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier=Modifier.clickable {
                        //onNavigate(R.id.action_modChannelsFragment_to_homeFragment)
                        onNavigate()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home Icon",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(35.dp)
                    )
                    androidx.compose.material.Text("Home", color = MaterialTheme.colorScheme.onPrimary)
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.moderator_secondary_color),
                        "Moderation Icon",
                        tint= MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(35.dp)
                    )
                    androidx.compose.material.Text(
                        "Mod Channels",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }


            }
        }

        /**
         * - Contains 0 extra parts:
         *
         * - OfflineModChannelImage is a composable function meant to mimic a streamer's stream image
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
                    Text("Offline",modifier = Modifier.align(Alignment.Center), fontSize = 20.sp,color = Color.White)
                }
                Spacer(modifier=Modifier.height(10.dp))
            }
        }
        @Composable
        fun OnlineModChannelImage(
            height: Int,
            width: Int,
            density:Float,
            viewCount:Int,
            url:String
        ){
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

    }/***END OF THE PARTS****/

}



















