package com.example.clicker.presentation.home.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.clicker.presentation.enhancedModView.viewModels.ModViewViewModel
import com.example.clicker.presentation.stream.AutoModViewModel
import com.example.clicker.presentation.stream.StreamView
import com.example.clicker.presentation.stream.StreamViewModel
import com.example.clicker.presentation.stream.views.chat.ChatUI
import com.example.clicker.presentation.stream.views.chat.chatSettings.ChatSettingsViewModel
import com.example.clicker.presentation.stream.views.overlays.VerticalOverlayView
import com.example.clicker.presentation.streamInfo.StreamInfoViewModel


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





