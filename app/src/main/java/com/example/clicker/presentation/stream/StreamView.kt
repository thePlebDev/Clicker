package com.example.clicker.presentation.stream

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color.parseColor
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.R
import com.example.clicker.network.models.ChatSettingsData
import com.example.clicker.network.websockets.models.TwitchUserData
import com.example.clicker.presentation.home.HomeViewModel
import com.example.clicker.presentation.stream.views.BottomModal
import com.example.clicker.presentation.stream.views.BottomModal.BanTimeOutDialogs
import com.example.clicker.presentation.stream.views.ChatSettingsContainer
import com.example.clicker.presentation.stream.views.MainChat
import com.example.clicker.util.Response
import com.example.clicker.util.SwipeableActionsState
import com.example.clicker.util.rememberSwipeableActionsState
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StreamView(
    streamViewModel: StreamViewModel,
    homeViewModel: HomeViewModel
) {
    val twitchUserChat = streamViewModel.listChats.toList()
    val drawerState = rememberDrawerState(androidx.compose.material3.DrawerValue.Closed)
    val chatSettingData = streamViewModel.state.value.chatSettings
    val modStatus = streamViewModel.state.value.loggedInUserData?.mod
    val filteredChat = streamViewModel.filteredChatList
    val clickedUsernameChats = streamViewModel.clickedUsernameChats
    val scope = rememberCoroutineScope()

    val bottomModalState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    var oneClickActionsChecked by remember { mutableStateOf(true) }

    //todo: Move these two to the ViewModel


    var orientation by remember { mutableStateOf(Configuration.ORIENTATION_PORTRAIT) }
    val configuration = LocalConfiguration.current
//
    LaunchedEffect(configuration) {
        // Save any changes to the orientation value on the configuration object
        snapshotFlow { configuration.orientation }
            .collect { orientation = it }
    }

    when (orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            HorizontalChat(streamViewModel)
        }
        else -> {
            // Below is the behemoth I am trying to rework
            // be warned, the code below is not for those weak of heart
            ModalBottomSheetLayout(
                sheetBackgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                sheetState = bottomModalState,
                sheetContent = {
                    // bottom modal below
                    BanTimeOutDialogs(
                        clickedUsernameChats = clickedUsernameChats,
                        clickedUsername = streamViewModel.clickedUIState.value.clickedUsername,
                        bottomModalState = bottomModalState,
                        textFieldValue = streamViewModel.textFieldValue,
                        closeBottomModal = { scope.launch { bottomModalState.hide() } },
                        banned = streamViewModel.clickedUIState.value.clickedUsernameBanned,
                        unbanUser = { streamViewModel.unBanUser() },
                        isMod = streamViewModel.clickedUIState.value.clickedUsernameIsMod,
                        openTimeoutDialog = {streamViewModel.openTimeoutDialog.value = true},
                        closeTimeoutDialog = {streamViewModel.openTimeoutDialog.value = false},
                        timeOutDialogOpen =streamViewModel.openTimeoutDialog.value,
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
                        openBanDialog = {streamViewModel.openBanDialog.value = true},
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

                        ChatSettingsContainer.SettingsSwitches(
                            enableSwitches =streamViewModel.chatSettingsState.value.switchesEnabled,
                            showChatSettingAlert = streamViewModel.chatSettingsState.value.showChatSettingAlert,
                            chatSettingsData =streamViewModel.chatSettingsState.value.data ,
                            updateChatSettings = {newData -> streamViewModel.toggleChatSettings(newData)},
                            closeAlertHeader = {streamViewModel.closeSettingsAlertHeader()}
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
                            showOneClickAction = oneClickActionsChecked,
                            oneClickBanUser={userId -> streamViewModel.oneClickBanUser(userId)},
                            oneClickTimeoutUser={}
                        )
                    }
                )
            } // end of the bottom modal


        }
    }
}

@Composable
fun SideModal(
    drawerState: DrawerState,
    contentCoveredBySideModal:@Composable () -> Unit,
    drawerContent:@Composable () -> Unit,

){
    ModalNavigationDrawer(
        drawerState =drawerState,
        drawerContent ={
            ModalDrawerSheet{
                drawerContent()
            }
        }
    ){
        contentCoveredBySideModal()
    }
}

/**THIS IS THE CHAT SHOWING IN THE UI*/
@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun TextChat(
    twitchUserChat: List<TwitchUserData>,
    sendMessageToWebSocket: (String) -> Unit,
    drawerState: DrawerState,
    modStatus: Boolean?,
    bottomModalState: ModalBottomSheetState,
    filteredChatList: List<String>,
    filterMethod: (String, String) -> Unit,
    clickedAutoCompleteText: (String, String) -> String,
    addChatter: (String, String) -> Unit,
    updateClickedUser: (String, String, Boolean, Boolean) -> Unit,
    textFieldValue: MutableState<TextFieldValue>,
    channelName: String?,
    deleteMessage: (String) -> Unit,
    banResponse: Response<Boolean>,
    undoBanResponse: Boolean,
    undoBan: () -> Unit,
    showStickyHeader: Boolean,
    closeStickyHeader: () -> Unit,
    banResponseMessage: String,
    removeUnBanButton: () -> Unit,
    restartWebSocket: () -> Unit,
    showOneClickAction:Boolean,
    oneClickBanUser: (String) -> Unit,
    oneClickTimeoutUser: (String) -> Unit

) {

    MainChat.AutoScrollChatWithTextBox(
        showStickyHeader =showStickyHeader,
        closeStickyHeader ={closeStickyHeader()},
        twitchUserChat = twitchUserChat,
        bottomModalState =bottomModalState,
        restartWebSocket ={restartWebSocket()},
        banResponseMessage =banResponseMessage,
        updateClickedUser ={ username, userId, banned, isMod ->updateClickedUser(username,userId,banned,isMod)},
        deleteMessage ={ messageId -> deleteMessage(messageId)},
        sendMessageToWebSocket ={ text -> sendMessageToWebSocket(text) },
        modStatus = modStatus,
        filteredChatList = filteredChatList,
        filterMethod = { username, newText -> filterMethod(username, newText) },
        clickedAutoCompleteText = { fullText, clickedText ->
            clickedAutoCompleteText(
                fullText,
                clickedText
            )
        },
        textFieldValue = textFieldValue,
        channelName = channelName,
        drawerState =drawerState
    )

}


