package com.example.clicker.presentation.stream.views.streamManager

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp
import com.example.clicker.network.clients.BlockedTerm

import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.network.websockets.AutoModQueueMessage
import com.example.clicker.presentation.modView.ListTitleValue
import com.example.clicker.presentation.modView.ModViewDragStateViewModel
import com.example.clicker.presentation.modView.ModViewViewModel
import com.example.clicker.presentation.stream.ClickedUIState
import com.example.clicker.presentation.stream.FilterType
import com.example.clicker.presentation.stream.views.AutoMod
import com.example.clicker.presentation.modView.views.ModViewDragSection
import com.example.clicker.util.Response
import kotlinx.coroutines.launch


@Composable
fun ManageStreamInformation(
    closeStreamInfo:()->Unit,
    modViewDragStateViewModel: ModViewDragStateViewModel,
    chatMessages: List<TwitchUserData>,
    clickedUserData: ClickedUIState,
    clickedUserChatMessages:List<String>,
    updateClickedUser: (String, String, Boolean, Boolean) -> Unit,
    timeoutDuration:Int,
    changeTimeoutDuration:(Int)->Unit,
    timeoutReason: String,
    changeTimeoutReason: (String) -> Unit,
    banDuration:Int,
    changeBanDuration:(Int)->Unit,
    banReason:String,
    changeBanReason: (String) -> Unit,

    loggedInUserIsMod:Boolean,
    clickedUserIsMod:Boolean,
    timeoutUser:()->Unit,
    showTimeoutErrorMessage:Boolean,
    setTimeoutShowErrorMessage:(Boolean)->Unit,
    showBanErrorMessage:Boolean,
    setBanShowErrorMessage:(Boolean)->Unit,
    banUser:()->Unit,
    modActionList: List<TwitchUserData>,
    autoModMessageList:List<AutoModQueueMessage>,
    manageAutoModMessage:(String,String,String)-> Unit,
    connectionError: Response<Boolean>,
    reconnect:()->Unit,
    blockedTerms:List<BlockedTerm>,
    deleteBlockedTerm:(String) ->Unit,
    emoteOnly:Boolean,
    setEmoteOnly:(Boolean) ->Unit,
    subscriberOnly:Boolean,
    setSubscriberOnly:(Boolean) ->Unit,

    chatSettingsEnabled:Boolean,
    followersOnlyList: List<ListTitleValue>,
    selectedFollowersModeItem: ListTitleValue,
    changeSelectedFollowersModeItem: (ListTitleValue) -> Unit,
    slowModeList: List<ListTitleValue>,
    selectedSlowModeItem: ListTitleValue,
    changeSelectedSlowModeItem: (ListTitleValue) -> Unit,
    deleteMessage:(String)->Unit,

    ){

        ModView.ModViewScaffold(
            closeStreamInfo={closeStreamInfo()},
            modViewDragStateViewModel =modViewDragStateViewModel,
            chatMessages =chatMessages,
            clickedUserData=clickedUserData,
            clickedUserChats = clickedUserChatMessages,
            updateClickedUser = {  username, userId,isBanned,isMod ->
                updateClickedUser(
                    username,
                    userId,
                    isBanned,
                    isMod
                )
            },
            timeoutDuration=timeoutDuration,
            changeTimeoutDuration={newValue ->changeTimeoutDuration(newValue)},
            timeoutReason = timeoutReason,
            changeTimeoutReason = {newValue->changeTimeoutReason(newValue)},
            banDuration = banDuration,
            changeBanDuration={newValue ->changeBanDuration(newValue)},
            banReason= banReason,
            changeBanReason = {newValue ->changeBanReason(newValue)},
            loggedInUserIsMod =loggedInUserIsMod,
            clickedUserIsMod=clickedUserIsMod,
            timeoutUser = {timeoutUser()},
            showTimeoutErrorMessage= showTimeoutErrorMessage,
            setTimeoutShowErrorMessage ={newValue ->setTimeoutShowErrorMessage(newValue)},
            showBanErrorMessage= showBanErrorMessage,
            setBanShowErrorMessage ={newValue ->setBanShowErrorMessage(newValue)},
            banUser={banUser()},
            modActionList = modActionList,
            autoModMessageList =autoModMessageList,
            manageAutoModMessage={messageId,userId, action ->manageAutoModMessage(messageId,userId,action)},
            connectionError =connectionError,
            reconnect ={reconnect()},
            blockedTerms=blockedTerms,
            deleteBlockedTerm ={blockedTermId ->deleteBlockedTerm(blockedTermId)},
            emoteOnly =emoteOnly,
            setEmoteOnly={newValue -> setEmoteOnly(newValue)},
            subscriberOnly =subscriberOnly,
            setSubscriberOnly={newValue -> setSubscriberOnly(newValue)},

            chatSettingsEnabled=chatSettingsEnabled,
            switchEnabled = chatSettingsEnabled,

            followersOnlyList=followersOnlyList,
            selectedFollowersModeItem=selectedFollowersModeItem,
            changeSelectedFollowersModeItem ={newValue -> changeSelectedFollowersModeItem(newValue)},
            slowModeList=slowModeList,
            selectedSlowModeItem=selectedSlowModeItem,
            changeSelectedSlowModeItem ={newValue ->changeSelectedSlowModeItem(newValue)},
            deleteMessage ={messageId ->deleteMessage(messageId)}


        )
    }










