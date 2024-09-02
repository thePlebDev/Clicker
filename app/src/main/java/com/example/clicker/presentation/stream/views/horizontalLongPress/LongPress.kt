package com.example.clicker.presentation.stream.views.horizontalLongPress

import android.util.Log
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

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.clicker.R
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.presentation.home.HomeViewModel
import com.example.clicker.presentation.modChannels.views.ModChannelComponents
import com.example.clicker.presentation.sharedViews.NoDrawerScaffold
import com.example.clicker.presentation.sharedViews.PullToRefreshComponent
import com.example.clicker.presentation.sharedViews.ScaffoldBottomBarScope
import com.example.clicker.presentation.sharedViews.ScaffoldTopBarScope
import com.example.clicker.presentation.stream.ClickedStreamInfo
import com.example.clicker.presentation.stream.StreamViewModel
import com.example.clicker.util.NetworkNewUserResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HorizontalLongPressView(
    homeViewModel: HomeViewModel,
    streamViewModel: StreamViewModel,
    loadURL:(String)->Unit,
    createNewTwitchEventWebSocket:()->Unit,
    updateClickedStreamInfo:(ClickedStreamInfo)->Unit,
    updateModViewSettings:(String,String,String,String,)->Unit,
    updateStreamerName: (String, String, String, String) -> Unit,

){
    val clicked = remember { mutableStateOf(true) }
    val text = if (clicked.value) "Live channels" else "Mod channels"

    val userId = homeViewModel.validatedUser.collectAsState().value?.userId ?:""
    val clientId = homeViewModel.validatedUser.collectAsState().value?.clientId ?:""
    val oAuthToken = homeViewModel.oAuthToken.collectAsState().value ?:""


    NoDrawerScaffold(
            topBar = {
                TopBarTextRow(text)
            },
            bottomBar = {},
            content = { contentPadding ->
                    LongPressPullToRefresh(
                        contentPadding =contentPadding,
                        refreshing =homeViewModel.state.value.homeRefreshing,
                        refreshFun = {homeViewModel.pullToRefreshGetLiveStreams()},
                        content ={
                            TestingLazyColumnItem(
                                height = homeViewModel.state.value.aspectHeight,
                                width = homeViewModel.state.value.width,
                                density =homeViewModel.state.value.screenDensity,
                                loadURL ={
                                        newUrl ->
                                    updateModViewSettings(
                                        oAuthToken,
                                        streamViewModel.state.value.clientId,
                                        streamViewModel.state.value.broadcasterId,
                                        streamViewModel.state.value.userId,
                                    )
                                    loadURL(newUrl)
                                    createNewTwitchEventWebSocket()

                                         },
                                reconnectWebSocketChat ={channelName -> streamViewModel.restartWebSocketFromLongClickMenu(channelName)},
                                listData = homeViewModel.state.value.horizontalLongHoldStreamList,
                                getChannelEmotes={
                                        broadcasterId ->
                                    streamViewModel.getChannelEmotes(
                                        oAuthToken,
                                        streamViewModel.state.value.clientId,
                                        broadcasterId,
                                    )
                                    streamViewModel.getBetterTTVChannelEmotes(broadcasterId)

                                },
                                updateClickedStreamInfo ={value -> updateClickedStreamInfo(value)},
                                updateStreamerName ={
                                        streamerName,clientId,broadcasterId,userId ->
                                    updateStreamerName(streamerName,clientId,broadcasterId,userId)
                                    Log.d("horizontalNavigation","CLICKING")
                                    streamViewModel.clearAllChatters()
                                    homeViewModel.updateClickedStreamerName(streamerName)

                                },
                                clientId =clientId,
                                userId = userId,

                                )
                        }
                    )
            },
        )
}


@Composable
fun LongPressPullToRefresh(
    contentPadding: PaddingValues,
    refreshing:Boolean,
    refreshFun:()->Unit,
    content:@Composable () -> Unit,
){

    PullToRefreshComponent(
        padding = contentPadding,
        refreshing =refreshing,
        refreshFunc = {
            refreshFun()
        },
        content = {
                  content()
        },
        networkStatus = {},
        showNetworkMessage = false


    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TestingLazyColumnItem(
    height: Int,
    width: Int,
    density:Float,
    loadURL: (String) -> Unit,
    reconnectWebSocketChat:(String)->Unit,
    getChannelEmotes:(String) ->Unit,
    listData: NetworkNewUserResponse<List<StreamData>>,
    updateClickedStreamInfo:(ClickedStreamInfo)->Unit,
    clientId: String,
    userId:String,
    updateStreamerName: (String, String, String, String) -> Unit,

    ){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        when(listData){
            is NetworkNewUserResponse.Loading ->{
                item{
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                        CircularProgressIndicator()
                    }

                }
            }
            is NetworkNewUserResponse.Success ->{

                val data = listData.data

                items(data,key = { streamItem -> streamItem.userId }) { streamItem ->
                    RowItem(
                        streamerName = streamItem.userLogin,
                        streamTitle = streamItem.title,
                        gameTitle = streamItem.gameName,
                        url = streamItem.thumbNailUrl,
                        height = height,
                        width = width,
                        viewCount = streamItem.viewerCount,
                        density =density,
                        loadURL ={
                                newUrl ->
                            updateStreamerName(
                                streamItem.userLogin,
                                clientId,
                                streamItem.userId,
                                userId
                            )
                            loadURL(newUrl)
                            getChannelEmotes(streamItem.userId)
                            updateClickedStreamInfo(
                                ClickedStreamInfo(
                                    channelName = streamItem.userLogin,
                                    streamTitle = streamItem.title,
                                    category =  streamItem.gameName,
                                    tags = streamItem.tags,
                                    adjustedUrl = streamItem.thumbNailUrl
                                )
                            )
                                 },
                        reconnectWebSocketChat ={channelName ->reconnectWebSocketChat(channelName)}
                    )
                }

            }
            is NetworkNewUserResponse.Failure ->{
                val message =listData.e.message ?:"Error! please pull down to refresh"
                item {
                    GettingStreamsError(message)
                }

            }
            is NetworkNewUserResponse.Auth401Failure ->{
                val message =listData.e.message ?:"Authentication failed"
                item {
                    GettingStreamsError(message)
                }

            }
            is NetworkNewUserResponse.NetworkFailure ->{
                val message =listData.e.message ?:"Error try again"
                item {
                    GettingStreamsError(message)
                }

            }

        }




    }
}

@Composable
fun RowItem(
    streamerName:String,
    streamTitle:String,
    gameTitle:String,
    url: String,
    height: Int,
    width: Int,
    viewCount:Int,
    density:Float,
    loadURL: (String) -> Unit,
    reconnectWebSocketChat:(String)->Unit,
){
    val newUser = "https://player.twitch.tv/?channel=$streamerName&controls=false&muted=false&parent=modderz"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable {
                loadURL(newUser)
                reconnectWebSocketChat(streamerName)
            },
        verticalAlignment = Alignment.CenterVertically
    ){
        ImageWithViewCount(
            url =url,
            height = height,
            width = width,
            viewCount = viewCount,
            density = density
        )
        StreamTitleWithInfo(
            streamerName = streamerName,
            gameTitle = gameTitle,
            streamTitle = streamTitle

        )


    }
}

@Composable
fun ImageWithViewCount(
    url: String,
    height: Int,
    width: Int,
    viewCount:Int,
    density:Float
){
    val adjustedHeight = (height/2)/density
    val adjustedWidth = (width/2)/density
    Log.d("ImageHeightWidth","url -> $url")
    Box(
    ) {

        SubcomposeAsyncImage(
            model = url,
            loading = {
                Column(modifier = Modifier
                    .height(adjustedHeight.dp)
                    .width(adjustedWidth.dp)
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

@Composable
fun StreamTitleWithInfo(
    streamerName:String,
    streamTitle:String,
    gameTitle:String
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
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.alpha(0.7f),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

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
                modifier = Modifier.size(30.dp)
            )
            Text(errorMessage, fontSize = MaterialTheme.typography.headlineSmall.fontSize,color=MaterialTheme.colorScheme.onSecondary)
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = stringResource(R.string.pull_to_refresh_icon_description),
                tint = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}
/******************************* MOD CHANNEL RELATED UI *******************************************************/
