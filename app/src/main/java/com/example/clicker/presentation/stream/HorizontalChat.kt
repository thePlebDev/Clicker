package com.example.clicker.presentation.stream

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
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

import com.example.clicker.presentation.stream.views.BottomModal
import com.example.clicker.presentation.stream.views.ChatSettingsContainer
import kotlinx.coroutines.launch



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HorizontalChat(
    streamViewModel: StreamViewModel
){
    val twitchUserChat = streamViewModel.listChats.toList()
    val lazyColumnListState = rememberLazyListState()
    var autoscroll by remember { mutableStateOf(true) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val clickedUsername = streamViewModel.clickedUIState.value.clickedUsername
    val recentChatMessagesByClickedUsername = streamViewModel.clickedUsernameChats
    val textFieldValue = streamViewModel.textFieldValue

    val bottomModalState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val clickedUsernameChats = streamViewModel.clickedUsernameChats

    val chatSettingData = streamViewModel.state.value.chatSettings
    val modStatus = streamViewModel.state.value.loggedInUserData?.mod
    val filteredChat = streamViewModel.filteredChatList


    val scope = rememberCoroutineScope()

    //todo: Also need to refactor the dialogs


    ModalBottomSheetLayout(
        sheetBackgroundColor = MaterialTheme.colorScheme.primary,
        sheetState = bottomModalState,
        sheetContent = {
            BottomModal.BanTimeOutDialogs(
                clickedUsernameChats = clickedUsernameChats,
                clickedUsername = streamViewModel.clickedUIState.value.clickedUsername,
                bottomModalState = bottomModalState,
                textFieldValue = streamViewModel.textFieldValue,
                closeBottomModal = { scope.launch { bottomModalState.hide() } },
                banned = streamViewModel.clickedUIState.value.clickedUsernameBanned,
                unbanUser = { streamViewModel.unBanUser() },
                isMod = streamViewModel.clickedUIState.value.clickedUsernameIsMod,
                openTimeoutDialog = { streamViewModel.openTimeoutDialog.value = true },
                closeTimeoutDialog = { streamViewModel.openTimeoutDialog.value = false },
                timeOutDialogOpen = streamViewModel.openTimeoutDialog.value,
                timeoutDuration = streamViewModel.state.value.timeoutDuration,
                timeoutReason = streamViewModel.state.value.timeoutReason,
                changeTimeoutDuration = { duration ->
                    streamViewModel.changeTimeoutDuration(
                        duration
                    )
                },
                changeTimeoutReason = { reason ->
                    streamViewModel.changeTimeoutReason(
                        reason
                    )
                },
                closeDialog = {
                    streamViewModel.openTimeoutDialog.value = false
                    scope.launch { bottomModalState.hide() }

                },
                timeOutUser = {
                    streamViewModel.timeoutUser()
                },
                banDialogOpen = streamViewModel.openBanDialog.value,
                openBanDialog = { streamViewModel.openBanDialog.value = true },
                closeBanDialog = {
                    scope.launch {
                        streamViewModel.openBanDialog.value = false

                    }
                },
                banReason = streamViewModel.state.value.banReason,
                changeBanReason = { reason -> streamViewModel.changeBanReason(reason) },
                banUser = { banUser -> streamViewModel.banUser(banUser) },
                clickedUserId = streamViewModel.clickedUIState.value.clickedUserId
            )
        }
    ) {
        SideModal(
            drawerState = drawerState,
            drawerContent={
                ChatSettingsContainer.EnhancedChatSettingsBox(
                    enableSwitches =streamViewModel.chatSettingsState.value.switchesEnabled,
                    showChatSettingAlert = streamViewModel.chatSettingsState.value.showChatSettingAlert,
                    chatSettingsData =streamViewModel.chatSettingsState.value.data ,
                    updateChatSettings = {newData -> streamViewModel.toggleChatSettings(newData)},
                    closeAlertHeader = {streamViewModel.closeSettingsAlertHeader()},
                    showUndoButton = {showStatus ->streamViewModel.showUndoButton(showStatus)},
                    showUndoButtonStatus = streamViewModel.chatSettingsState.value.showUndoButton,
                    noChatMode = streamViewModel.chatSettingsState.value.noChatMode,
                    setNoChatMode = {state ->streamViewModel.setNoChatMode(state)}
                )
            },
            contentCoveredBySideModal = {
                TextChat(
                    twitchUserChat = twitchUserChat,
                    sendMessageToWebSocket = { string ->
                        streamViewModel.sendMessage(string)
                    },
                    drawerState = drawerState,
                    modStatus = modStatus,
                    bottomModalState = bottomModalState,
                    filteredChatList = filteredChat,
                    filterMethod = { username, newText ->
                        streamViewModel.filterChatters(
                            username,
                            newText
                        )
                    },
                    clickedAutoCompleteText = { fullText, clickedText ->
                        streamViewModel.autoTextChange(
                            fullText,
                            clickedText
                        )
                    },
                    addChatter = { username, message ->
                        streamViewModel.addChatter(
                            username,
                            message
                        )
                    },
                    updateClickedUser = { username, userId, banned, isMod ->
                        streamViewModel.updateClickedChat(
                            username,
                            userId,
                            banned,
                            isMod
                        )
                    },
                    textFieldValue = streamViewModel.textFieldValue,
                    channelName = streamViewModel.channelName.collectAsState().value,
                    deleteMessage = { messageId ->
                        streamViewModel.deleteChatMessage(
                            messageId
                        )
                    },

                    banResponse = streamViewModel.state.value.banResponse,
                    undoBan = { streamViewModel.unBanUser() },
                    undoBanResponse = streamViewModel.state.value.undoBanResponse,
                    showStickyHeader = streamViewModel.state.value.showStickyHeader,
                    closeStickyHeader = { streamViewModel.closeStickyHeader() },
                    banResponseMessage = streamViewModel.state.value.banResponseMessage,
                    removeUnBanButton = { streamViewModel.removeUnBanButton() },
                    restartWebSocket = { streamViewModel.restartWebSocket() },
                    showOneClickAction = false,
                    oneClickBanUser={userId -> streamViewModel.oneClickBanUser(userId)},
                    oneClickTimeoutUser={
                            userDetails ->
                    },
                    showUndoButton = streamViewModel.chatSettingsState.value.showUndoButton,
                    noChatMode =  streamViewModel.chatSettingsState.value.noChatMode
                )
            }
        )
    }


}

