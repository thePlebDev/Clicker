package com.example.clicker.presentation.stream

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.clicker.presentation.modView.ModViewViewModel
import com.example.clicker.presentation.modView.followerModeList
import com.example.clicker.presentation.modView.slowModeList


import com.example.clicker.presentation.stream.views.BottomModal

import com.example.clicker.presentation.stream.views.chat.ChatSettingsColumn
import com.example.clicker.presentation.stream.views.chat.ChatUI
import com.example.clicker.presentation.stream.views.overlays.VerticalOverlayView
import com.example.clicker.presentation.stream.views.streamManager.StreamManagerUI
import kotlinx.coroutines.launch



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HorizontalChat(
    streamViewModel: StreamViewModel,
    autoModViewModel:AutoModViewModel,
    modViewViewModel:ModViewViewModel
){
    val twitchUserChat = streamViewModel.listChats.toList()
    val lazyColumnListState = rememberLazyListState()
    var autoscroll by remember { mutableStateOf(true) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val clickedUsername = streamViewModel.clickedUIState.value.clickedUsername
    val recentChatMessagesByClickedUsername = streamViewModel.clickedUsernameChats
    val textFieldValue = streamViewModel.textFieldValue
    var showAdvancedChatSettings by remember { mutableStateOf(true) }

    val scope =rememberCoroutineScope()

    val bottomModalState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val outerBottomModalState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    val clickedUsernameChats = streamViewModel.clickedUsernameChats

    val chatSettingData = streamViewModel.state.value.chatSettings
    val modStatus = streamViewModel.state.value.loggedInUserData?.mod
    val notificationAmount=modViewViewModel.uiState.value.autoModQuePedingMessages
    val filteredChat = streamViewModel.filteredChatList
    val showClickedUserBottomModal:()->Unit =remember(bottomModalState) { {
        scope.launch {
            bottomModalState.show()
        }
    } }
    val showChatSettingsBottomModal:()->Unit =remember(bottomModalState) { {
        scope.launch {
            outerBottomModalState.show()
        }
    } }

    val doubleClickChat:(String)->Unit =remember(streamViewModel) { {
        streamViewModel.sendDoubleTapEmote(it)
    } }
    val updateClickedUser:(String,String,Boolean,Boolean)->Unit = remember(streamViewModel) { { username, userId, banned, isMod ->
        streamViewModel.updateClickedChat(
            username,
            userId,
            banned,
            isMod
        )
    } }


    //todo: Also need to refactor the dialogs

    ModalBottomSheetLayout(
        sheetState = outerBottomModalState,
        sheetContent ={
            ChatSettingsColumn(
                advancedChatSettings = streamViewModel.advancedChatSettingsState.value,
                changeAdvancedChatSettings = {newValue -> streamViewModel.updateAdvancedChatSettings(newValue)},
                changeNoChatMode = {newValue -> streamViewModel.setNoChatMode(newValue)},
                chatSettingsEnabled = modViewViewModel.uiState.value.enabledChatSettings,
                followerModeList= followerModeList,
                selectedFollowersModeItem=modViewViewModel.uiState.value.selectedFollowerMode,
                changeSelectedFollowersModeItem ={newValue -> modViewViewModel.changeSelectedFollowersModeItem(newValue)},
                slowModeList= slowModeList,
                selectedSlowModeItem=modViewViewModel.uiState.value.selectedSlowMode,
                changeSelectedSlowModeItem ={newValue ->modViewViewModel.changeSelectedSlowModeItem(newValue)},
                emoteOnly = modViewViewModel.uiState.value.emoteOnly,
                setEmoteOnly = {newValue ->modViewViewModel.updateEmoteOnly(newValue)},
                subscriberOnly =modViewViewModel.uiState.value.subscriberOnly,
                setSubscriberOnly={newValue -> modViewViewModel.updateSubscriberOnly(newValue)},
            )
        }
    ) {



        ModalBottomSheetLayout(
            sheetBackgroundColor = MaterialTheme.colorScheme.primary,
            sheetState = bottomModalState,
            sheetContent = {
                BottomModal.BottomModalBuilder(
                    clickedUsernameChats = clickedUsernameChats,
                    clickedUsername = streamViewModel.clickedUIState.value.clickedUsername,
                    bottomModalState = bottomModalState,
                    textFieldValue = streamViewModel.textFieldValue,
                    closeBottomModal = {

                    },
                    banned = streamViewModel.clickedUIState.value.clickedUsernameBanned,
                    unbanUser = {
                        //  streamViewModel.unBanUser()
                    },
                    isMod = true,
                    openTimeoutDialog = {
                        //  streamViewModel.openTimeoutDialog.value = true
                        streamViewModel.openTimeoutDialog.value = true
                    },

                    openBanDialog = {

                        streamViewModel.openBanDialog.value = true
                                    },
                    shouldMonitorUser = streamViewModel.shouldMonitorUser.value,
                    updateShouldMonitorUser = {
                        // streamViewModel.updateShouldMonitorUser()
                    },

                    )
            }
        ){
            //this is where the chatUI goes


            //todo:this is the dialogs
            TimeoutBanDialogs(
                showTimeOutDialog =streamViewModel.openTimeoutDialog.value,
                username = streamViewModel.clickedUIState.value.clickedUsername,
                timeoutUser={
                    streamViewModel.timeoutUser()
                },
                timeoutDuration=streamViewModel.state.value.timeoutDuration,
                closeTimeoutDialog = {streamViewModel.openTimeoutDialog.value = false},
                changeTimeoutDuration={newDuration -> streamViewModel.changeTimeoutDuration(newDuration) },
                timeoutReason=streamViewModel.state.value.timeoutReason,
                changeTimeoutReason = {reason ->streamViewModel.changeTimeoutReason(reason)},
                showBanDialog =streamViewModel.openBanDialog.value,
                changeBanReason = {newReason -> streamViewModel.changeBanReason(newReason)},
                banUser  ={
                    streamViewModel.banUser()
                },
                closeBanDialog = {streamViewModel.openBanDialog.value = false},
                banReason = streamViewModel.state.value.banReason,
            )

            ChatUI(
                twitchUserChat = twitchUserChat,
                showBottomModal={
                    showClickedUserBottomModal()
                },
                updateClickedUser = { username, userId, banned, isMod ->
                    updateClickedUser(
                        username,
                        userId,
                        banned,
                        isMod
                    )
                },
                showTimeoutDialog={
                    streamViewModel.openTimeoutDialog.value = true
                },
                showBanDialog = {streamViewModel.openBanDialog.value = true},
                doubleClickMessage={ username->
                    doubleClickChat(username)
                },

                showOuterBottomModalState = {
                    scope.launch {

                    }
                },
                newFilterMethod={newTextValue -> streamViewModel.newParsingAgain(newTextValue)},

                orientationIsVertical =false,

                isMod = streamViewModel.state.value.loggedInUserData?.mod ?: false,
                filteredChatList = filteredChat,
                clickedAutoCompleteText = { username ->
                    streamViewModel.autoTextChange(username)
                },
                showModal = {
                    showChatSettingsBottomModal()
                },
                notificationAmount =0,
                textFieldValue = streamViewModel.textFieldValue,
                sendMessageToWebSocket = { string ->
                    streamViewModel.sendMessage(string)
                },
                noChat = streamViewModel.advancedChatSettingsState.value.noChatMode,
                deleteChatMessage = {messageId ->streamViewModel.deleteChatMessage(messageId)},
                forwardSlashCommands = streamViewModel.forwardSlashCommands,
                clickedCommandAutoCompleteText={clickedValue -> streamViewModel.clickedCommandAutoCompleteText(clickedValue)},
                inlineContentMap = streamViewModel.inlineTextContentTest.value
            )


        }
    }




}

