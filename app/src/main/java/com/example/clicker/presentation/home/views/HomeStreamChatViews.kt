package com.example.clicker.presentation.home.views

import android.content.Context
import android.util.Log
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.clicker.presentation.enhancedModView.viewModels.ModViewViewModel
import com.example.clicker.presentation.stream.AutoModViewModel
import com.example.clicker.presentation.stream.StreamView
import com.example.clicker.presentation.stream.StreamViewModel
import com.example.clicker.presentation.stream.TagListStable
import com.example.clicker.presentation.stream.views.chat.ChatUI
import com.example.clicker.presentation.stream.views.chat.chatSettings.ChatSettingsViewModel
import com.example.clicker.presentation.stream.views.overlays.VerticalOverlayView
import com.example.clicker.presentation.streamInfo.StreamInfoViewModel
import kotlinx.coroutines.launch


@Composable
fun HomeStreamChatViews(
    streamViewModel: StreamViewModel,
    autoModViewModel: AutoModViewModel,
    modViewViewModel: ModViewViewModel,
    chatSettingsViewModel: ChatSettingsViewModel,
    hideSoftKeyboard:()->Unit,
    showModView:()->Unit,
    modViewIsVisible:Boolean,
    streamInfoViewModel: StreamInfoViewModel,
    showHomeChat:Boolean
){

    if(showHomeChat){
        StreamView(
            streamViewModel=streamViewModel,
            autoModViewModel=autoModViewModel,
            modViewViewModel=modViewViewModel,
            chatSettingsViewModel=chatSettingsViewModel,
            streamInfoViewModel=streamInfoViewModel,
            hideSoftKeyboard={hideSoftKeyboard()},
            showModView={showModView()},
            modViewIsVisible=modViewIsVisible,
        )
    }
}


@Composable
fun VerticalHomeStreamOverlayView(
    channelName:String,
    streamTitle:String,
    category:String,
    tags: TagListStable,
){

    VerticalHomeStreamOverlayUI(
        channelName,
        streamTitle,
        category,
        tags
            )


}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VerticalHomeStreamOverlayUI(
    channelName:String,
    streamTitle:String,
    category: String,
    tags: TagListStable
){
    Log.d("VerticalTestingOverlayUI","RECOMPING")

    //  val tags = listOf("meat ball", " tagerine","gabagool")

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp)) {
        Text(
            channelName,
            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
            color = Color.White,

            )
        Text(
            streamTitle,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            color = Color.White,
            lineHeight = MaterialTheme.typography.headlineSmall.fontSize,

            )
        Text(
            category,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.padding(vertical = 5.dp)
        )
        LazyRow(
            modifier = Modifier.background(Color.Transparent).padding(bottom = 10.dp)
        ) {
            items(tags.list){tagTitle->
                VerticalTagText(tagTitle)
            }

        }
    }

}

@Composable
fun VerticalTagText(
    tagText:String
){
    Box(
        Modifier
            .background(Color.Transparent)
            .padding(3.dp)
    ){
        Text(
            text =tagText,
            Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.DarkGray)
                .padding(horizontal = 10.dp, vertical = 5.dp)
            ,
            color = Color.White,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize

        )
    }
}





