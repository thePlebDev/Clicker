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
            BottomModalContent(

                // TODO: this should 100% not be filteredChat. Need to create new variable
                clickedUsernameChats = clickedUsernameChats,
                clickedUsername = streamViewModel.clickedUIState.value.clickedUsername,
                bottomModalState = bottomModalState,
                textFieldValue = streamViewModel.textFieldValue,
                closeBottomModal = { scope.launch { bottomModalState.hide() } },
                banned = streamViewModel.clickedUIState.value.clickedUsernameBanned,
                unbanUser = { streamViewModel.unBanUser() },
                isMod = streamViewModel.clickedUIState.value.clickedUsernameIsMod,
                openTimeoutDialog = {openTimeoutDialog.value = true},
                timeoutDialogContent ={
                    if(openTimeoutDialog.value){
                        BottomModal.TimeoutDialog(
                            onDismissRequest = {
                                openTimeoutDialog.value = false
                            },
                            username = streamViewModel.clickedUIState.value.clickedUsername,
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
                            }
                        )
                    }
                },
                openBanDialog = {openBanDialog.value = true},
                banDialogContent ={
                    if(openBanDialog.value){
                        BottomModal.BanDialog(
                            onDismissRequest = {
                                openBanDialog.value = false
                            },
                            username = streamViewModel.clickedUIState.value.clickedUsername,
                            banDuration = streamViewModel.state.value.banDuration,
                            banReason = streamViewModel.state.value.banReason,
                            changeBanDuration = { duration ->
                                streamViewModel.changeBanDuration(
                                    duration
                                )
                            },
                            changeBanReason = { reason -> streamViewModel.changeBanReason(reason) },
                            banUser = { banUser -> streamViewModel.banUser(banUser) },
                            clickedUserId = streamViewModel.clickedUIState.value.clickedUserId,
                            closeDialog = {
                                openBanDialog.value = false
                                scope.launch { bottomModalState.hide() }
                            },
                            closeBottomModal = {
                                scope.launch {
                                    openBanDialog.value = false
                                    bottomModalState.hide()
                                }
                            }
                        )
                    }

                }

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



@Composable
fun HorizontalTimeoutDialog(
    onDismissRequest: () -> Unit,
    clickedUsername: String,
    timeoutDuration: Int,
    timeoutReason: String,
    changeTimeoutDuration: (Int) -> Unit,
    changeTimeoutReason: (String) -> Unit,
    timeOutUser: () -> Unit
){
    val secondary = androidx.compose.material3.MaterialTheme.colorScheme.secondary
    val primary = androidx.compose.material3.MaterialTheme.colorScheme.primary
    val onPrimary = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
    val onSecondary = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            backgroundColor = primary,
            border = BorderStroke(2.dp,secondary)
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .background(primary)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(stringResource(R.string.timeout_text), fontSize = 22.sp,color = onPrimary)
                    Text(clickedUsername, fontSize = 22.sp,color = onPrimary)
                }
                androidx.compose.material.Divider(
                    color = secondary,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(stringResource(R.string.duration_text),color = onPrimary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    Column {
                        RadioButton(
                            colors =  RadioButtonDefaults.colors( selectedColor=secondary, unselectedColor = onPrimary),
                            selected = timeoutDuration == 60,
                            onClick = {
                                changeTimeoutDuration(60)
                            }
                        )
                        Text(stringResource(R.string.one_minute),color = onPrimary)
                    }
                    Column {
                        RadioButton(
                            colors =  RadioButtonDefaults.colors( selectedColor=secondary, unselectedColor = onPrimary),
                            selected = timeoutDuration == 600,
                            onClick = {
                                changeTimeoutDuration(600)
                            }
                        )
                        Text(stringResource(R.string.ten_minutes),color = onPrimary)
                    }
                    Column {
                        RadioButton(
                            colors =  RadioButtonDefaults.colors( selectedColor=secondary, unselectedColor = onPrimary),
                            selected = timeoutDuration == 1800,
                            onClick = {
                                changeTimeoutDuration(1800)
                            }
                        )
                        Text(stringResource(R.string.thirty_minutes),color = onPrimary)
                    }
                    Column {
                        RadioButton(
                            colors =  RadioButtonDefaults.colors( selectedColor=secondary, unselectedColor = onPrimary),
                            selected = timeoutDuration == 604800,
                            onClick = {
                                changeTimeoutDuration(604800)
                            }
                        )
                        Text(stringResource(R.string.one_week),color = onPrimary)
                    }
                }
                OutlinedTextField(
                    colors= TextFieldDefaults.textFieldColors(
                        textColor = onPrimary, focusedLabelColor = onPrimary,
                        focusedIndicatorColor = onPrimary, unfocusedIndicatorColor = onPrimary, unfocusedLabelColor = onPrimary),
                    value = timeoutReason,
                    onValueChange = {
                        changeTimeoutReason(it)
                                    },
                    label = { Text(stringResource(R.string.reason)) }
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(10.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = secondary)
                    ) {
                        Text(stringResource(R.string.cancel),color = onSecondary)
                    }
                    // todo: Implement the details of the timeout implementation
                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = secondary),
                        onClick = {
                            onDismissRequest()
                            timeOutUser()
                        }, modifier = Modifier.padding(10.dp)) {
                        Text(stringResource(R.string.timeout_confirm),color = onSecondary)
                    }
                }
            }
        }
    }

}

@Composable
fun HorizontalBanDialog(
    onDismissRequest: () -> Unit,
    clickedUsername: String,
    banDuration: Int,
    banReason: String,
    changeBanDuration: (Int) -> Unit,
    changeBanReason: (String) -> Unit,
    banUser: (BanUser) -> Unit,
    clickedUserId:String
){
    val secondary = androidx.compose.material3.MaterialTheme.colorScheme.secondary
    val primary = androidx.compose.material3.MaterialTheme.colorScheme.primary
    val onPrimary = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
    val onSecondary = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
    Dialog(
        onDismissRequest = { onDismissRequest() },
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            backgroundColor = primary,
            border = BorderStroke(2.dp,secondary)
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .background(primary)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(stringResource(R.string.ban), fontSize = 22.sp,color = onPrimary)
                    Text(clickedUsername, fontSize = 22.sp,color = onPrimary)
                }
                androidx.compose.material.Divider(
                    color = MaterialTheme.colorScheme.secondary,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(stringResource(R.string.duration_text),color = onPrimary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Column() {
                        RadioButton(
                            colors =  RadioButtonDefaults.colors( selectedColor=secondary, unselectedColor = onPrimary),
                            selected = banDuration == 0,
                            onClick = {
                                changeBanDuration(0)
                            }
                        )
                        Text(stringResource(R.string.permanently),color = onPrimary)
                    }
                }
                OutlinedTextField(
                    colors= TextFieldDefaults.textFieldColors(
                        textColor = onPrimary, focusedLabelColor = onPrimary,
                        focusedIndicatorColor = onPrimary, unfocusedIndicatorColor = onPrimary, unfocusedLabelColor = onPrimary),
                    value = banReason,
                    onValueChange = {
                        changeBanReason(it)
                                    },
                    label = { Text(stringResource(R.string.reason),color = onPrimary) }
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = secondary),
                        onClick = { onDismissRequest() }, modifier = Modifier.padding(10.dp)
                    ) {
                        Text(stringResource(R.string.cancel),color =onSecondary)
                    }

                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = secondary),
                        onClick = {
                                  /**This is the actual modding action takes place*/
                            onDismissRequest()

                            banUser(
                                BanUser(
                                    data = BanUserData(
                                        user_id = clickedUserId,
                                        reason = banReason
                                    )
                                )
                            )
                        },
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text(stringResource(R.string.ban),color =onSecondary)
                    }
                }
            }
        }
    }
}