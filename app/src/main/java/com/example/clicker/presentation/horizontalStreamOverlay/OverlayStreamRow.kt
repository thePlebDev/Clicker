package com.example.clicker.presentation.horizontalStreamOverlay

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.fragment.findNavController
import coil.compose.SubcomposeAsyncImage
import com.example.clicker.R
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.presentation.home.HomeViewModel

import com.example.clicker.presentation.stream.StreamViewModel
import com.example.clicker.presentation.stream.models.ClickedStreamInfo
import com.example.clicker.presentation.streamInfo.StreamInfoViewModel
import com.example.clicker.util.NetworkNewUserResponse


@Composable
fun OverlayStreamRow(
    homeViewModel:HomeViewModel,
    streamViewModel:StreamViewModel,
    loadURL:(String)->Unit,
    createNewTwitchEventWebSocket:()->Unit,
    updateClickedStreamInfo:(ClickedStreamInfo)->Unit,
    updateModViewSettings:(String,String,String,String,)->Unit,
    updateStreamerName: (String, String, String, String) -> Unit,
    streamInfoViewModel:StreamInfoViewModel
){
    val height = homeViewModel.state.value.aspectHeight
    val width = homeViewModel.state.value.width
    val oAuthToken = homeViewModel.oAuthToken.collectAsState().value ?:""


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.7f))
    ){

    }
    LazyRowViewTesting(
        followedStreamerList = homeViewModel.state.value.streamersListLoading,
        height = height,
        width = width,
        density = homeViewModel.state.value.screenDensity,
        clientId = homeViewModel.validatedUser.collectAsState().value?.clientId ?:"",
        userId = homeViewModel.validatedUser.collectAsState().value?.userId ?:"",
        loadURL ={ newUrl ->
            updateModViewSettings(
                oAuthToken,
                streamViewModel.state.value.clientId,
                streamViewModel.state.value.broadcasterId,
                streamViewModel.state.value.userId,
            )
            loadURL(newUrl)
            createNewTwitchEventWebSocket()
            streamInfoViewModel.getStreamInfo(
                authorizationToken = oAuthToken,
                clientId = streamViewModel.state.value.clientId,
                broadcasterId = streamViewModel.state.value.broadcasterId,
            )
            

        },
        reconnectWebSocketChat ={channelName -> streamViewModel.restartWebSocketFromLongClickMenu(channelName)},
        updateStreamerName ={
                streamerName,clientId,broadcasterId,userId ->
            updateStreamerName(streamerName,clientId,broadcasterId,userId)
            Log.d("horizontalNavigation","CLICKING")
            streamViewModel.clearAllChatters()
        },
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
        clickedStreamName = homeViewModel.clickedStreamerName.value,
        updateClickedStreamerName = {clickedName ->homeViewModel.updateClickedStreamerName(clickedName)}
    )


    }


@Composable
fun LazyRowViewTesting(
    followedStreamerList: NetworkNewUserResponse<List<StreamData>>,
    height: Int,
    width: Int,
    density: Float,
    clientId: String,
    userId:String,
    loadURL: (String) -> Unit,
    reconnectWebSocketChat:(String)->Unit,
    updateStreamerName: (String, String, String, String) -> Unit,
    getChannelEmotes:(String) ->Unit,
    updateClickedStreamInfo:(ClickedStreamInfo)->Unit,
    clickedStreamName:String,
    updateClickedStreamerName:(String) ->Unit
){

    LazyRow(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)) {


        when (followedStreamerList) {
            is NetworkNewUserResponse.Success -> {
                val listData = followedStreamerList.data
                items(listData){streamItem ->
                    ImageWithViewCount(
                        url = streamItem.thumbNailUrl,
                        height = height,
                        width = width,
                        viewCount = streamItem.viewerCount,
                        density =density,
                        streamTitle = streamItem.title,
                        streamerName = streamItem.userLogin,
                        loadURL={newUrl->
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
                        reconnectWebSocketChat ={channelName ->reconnectWebSocketChat(channelName)},
                        clickedStreamName=clickedStreamName,
                        updateClickedStreamerName = {streamerName -> updateClickedStreamerName(streamerName)}
                    )
                }


            }

            else -> {

            }
        }
    }

}



@Composable
fun ImageWithViewCount(
    url: String,
    height: Int,
    width: Int,
    viewCount:Int,
    density:Float,
    streamTitle:String,
    streamerName:String,
    loadURL: (String) -> Unit,
    reconnectWebSocketChat:(String)->Unit,
    clickedStreamName:String,
    updateClickedStreamerName:(String) ->Unit
){
    Log.d("ImageHeightWidth","url -> $url")
    val newUser = "https://player.twitch.tv/?channel=$streamerName&controls=false&muted=false&parent=modderz"
    val adjustedWidth = width / density
    val clickedModifier = Modifier
        .clip(RoundedCornerShape(5.dp))
        .border(
            width = 4.dp,
            color = MaterialTheme.colorScheme.secondary,
            shape = RoundedCornerShape(5.dp)
        )
    val nonClickedModifier = Modifier.clip(RoundedCornerShape(5.dp))
    val chosenModifier = if(clickedStreamName == streamerName) clickedModifier else nonClickedModifier
    Row(
        modifier = Modifier.clickable {
            loadURL(newUser)
            reconnectWebSocketChat(streamerName)
            updateClickedStreamerName(streamerName)
        }
    ) {

        Column(
            modifier = Modifier.width(adjustedWidth.dp)
        ) {

            Box() {
                val adjustedHeight = height / density

                SubcomposeAsyncImage(
                    modifier = chosenModifier,
                    model = url,
                    loading = {
                        Column(
                            modifier = Modifier
                                .height((adjustedHeight).dp)
                                .width((adjustedWidth).dp)
                                .background(MaterialTheme.colorScheme.primary),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                        }
                    },
                    contentDescription = stringResource(R.string.sub_compose_async_image_description)
                )
                androidx.compose.material.Text(
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
            Text(streamTitle,color = MaterialTheme.colorScheme.onPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(streamerName,color = MaterialTheme.colorScheme.onPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Spacer(modifier =Modifier.width(10.dp))
    }
}