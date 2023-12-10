package com.example.clicker.presentation.stream

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.substring
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.clicker.R
import com.example.clicker.network.BanUser
import com.example.clicker.network.BanUserData
import com.example.clicker.network.websockets.MessageType
import com.example.clicker.network.websockets.models.TwitchUserData
import com.example.clicker.presentation.stream.views.BottomModal
import com.example.clicker.presentation.stream.views.ChatBadges
import com.example.clicker.presentation.stream.views.SystemChats
import com.example.clicker.presentation.stream.views.isScrolledToEnd
import com.example.clicker.util.Response
import com.example.clicker.util.SwipeableActionsState
import com.example.clicker.util.rememberSwipeableActionsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt


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
    val openTimeoutDialog = remember { mutableStateOf(false) }
    val openBanDialog = remember { mutableStateOf(false) }
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
                openTimeoutDialog = { openTimeoutDialog.value = true },
                closeTimeoutDialog = { openTimeoutDialog.value = false },
                timeOutDialogOpen = openTimeoutDialog.value,
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
                    openTimeoutDialog.value = false
                    scope.launch { bottomModalState.hide() }

                },
                timeOutUser = {
                    streamViewModel.timeoutUser()
                },
                banDialogOpen = openBanDialog.value,
                openBanDialog = { openBanDialog.value = true },
                closeBanDialog = {
                    scope.launch {
                        openBanDialog.value = false

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
                DrawerContent(
                    chatSettingData,
                    showChatSettingAlert = streamViewModel.state.value.showChatSettingAlert,
                    slowModeToggle = { chatSettingsData ->
                        streamViewModel.slowModeChatSettings(
                            chatSettingsData
                        )
                    },
                    followerModeToggle = { chatSettingsData ->
                        streamViewModel.followerModeToggle(
                            chatSettingsData
                        )
                    },
                    subscriberModeToggle = { chatSettingsData ->
                        streamViewModel.subscriberModeToggle(
                            chatSettingsData
                        )
                    },
                    emoteModeToggle = { chatSettingsData ->
                        streamViewModel.emoteModeToggle(
                            chatSettingsData
                        )
                    },
                    enableSlowModeSwitch = streamViewModel.state.value.enableSlowMode,
                    enableFollowerModeSwitch = streamViewModel.state.value.enableFollowerMode,
                    enableSubscriberSwitch = streamViewModel.state.value.enableSubscriberMode,
                    enableEmoteModeSwitch = streamViewModel.state.value.enableEmoteMode,
                    chatSettingsFailedMessage = streamViewModel.state.value.chatSettingsFailedMessage,
                    fetchChatSettings = { streamViewModel.retryGettingChatSetting() },
                    closeChatSettingAlter = { streamViewModel.closeChatSettingAlert() },
                    oneClickActionsChecked=false,
                    changeOneClickActionsStatus={}

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
                    oneClickTimeoutUser={userDetails -> streamViewModel.oneClickTimeoutUser(userDetails)}
                )
            }
        )
    }


}

