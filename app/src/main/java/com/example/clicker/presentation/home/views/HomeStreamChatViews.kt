package com.example.clicker.presentation.home.views

import android.content.Context
import android.util.Log
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.clicker.presentation.enhancedModView.viewModels.ModViewViewModel
import com.example.clicker.presentation.stream.AutoModViewModel
import com.example.clicker.presentation.stream.StreamView
import com.example.clicker.presentation.stream.StreamViewModel
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
fun StreamScreen(
    webView: WebView,
    webViewIsLoading:Boolean,
    clickedUser:String,
    listOfLiveUserNames:List<String>
) {
    val configuration = LocalConfiguration.current
    var webViewWidth by remember { mutableStateOf(webView.width) }
    var webViewHeight by remember { mutableStateOf(webView.height) }

    // Update dimensions when the orientation changes
    LaunchedEffect(configuration) {
        webViewWidth = webView.width
        webViewHeight = webView.height
    }
    if(webViewIsLoading){
        StreamLoading(
            width = webViewWidth,
            height = webViewHeight,
            clickedUser=clickedUser,
            listOfLiveUserNames=listOfLiveUserNames
        )
    }



}


@Composable
fun StreamLoading(
    width:Int,
    height:Int,
    clickedUser:String,
    listOfLiveUserNames:List<String>

){
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Log.d("CLickedUserChecks", "user -->$clickedUser")
    Log.d("onConfigurationChangedTesting", "width-->$width height -->$height")

    preloadImages(
        context=context,
        clickedUsers = listOfLiveUserNames,
        width=width,
        height=height
    )



    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {
        AsyncImage(
            model = "https://static-cdn.jtvnw.net/previews-ttv/live_user_$clickedUser-${width}x$height.jpg",
            contentDescription = "Translated description of what the image contains",
            modifier = Modifier.align(Alignment.TopCenter)
        )
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun preloadImages(
    context: Context,
    clickedUsers:List<String>,
    width:Int,
    height:Int,
) {
    val imageLoader = ImageLoader(context)
    val imageUrls = clickedUsers.map {
            clickedUser ->
        "https://static-cdn.jtvnw.net/previews-ttv/live_user_$clickedUser-${width}x$height.jpg"

    }

    imageUrls.forEach { url ->
        val request = ImageRequest.Builder(context)
            .data(url)
            .memoryCacheKey(url) // Optional: Helps with in-memory caching
            .diskCacheKey(url)   // Optional: Helps with disk caching
            .build()
        imageLoader.enqueue(request) // Preload each image
    }
}





