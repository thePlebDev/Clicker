package com.example.clicker.presentation.stream

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.input.TextFieldValue
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.presentation.home.HomeViewModel
import com.example.clicker.presentation.stream.util.ForwardSlashCommands
import com.example.clicker.presentation.stream.views.AutoScrollChatWithTextBox
import com.example.clicker.presentation.stream.views.BottomModal.BottomModalBuilder
import com.example.clicker.presentation.stream.views.chat.ChatUI
import com.example.clicker.util.Response
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StreamView(
    streamViewModel: StreamViewModel,
    autoModViewModel: AutoModViewModel,
    homeViewModel: HomeViewModel,
    showStreamManager:()->Unit,
    notificationAmount: Int

) {
    val twitchUserChat = streamViewModel.listChats.toList()
//    val drawerState = rememberDrawerState(androidx.compose.material3.DrawerValue.Closed)
//    val chatSettingData = streamViewModel.state.value.chatSettings
//    val modStatus = streamViewModel.state.value.loggedInUserData?.mod
//    val filteredChat = streamViewModel.filteredChatList
    val clickedUsernameChats = streamViewModel.clickedUsernameChats
    val scope = rememberCoroutineScope()
//    var showAdvancedChatSettings by remember { mutableStateOf(true) }
//
    val bottomModalState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
//    val outerBottomModalState = rememberModalBottomSheetState(
//        initialValue = ModalBottomSheetValue.Hidden,
//        skipHalfExpanded = true
//    )
//    var oneClickActionsChecked by remember { mutableStateOf(true) }
//
//    //todo: Move these two to the ViewModel
//
//
//    var orientation by remember { mutableStateOf(Configuration.ORIENTATION_PORTRAIT) }
//    val configuration = LocalConfiguration.current
////
//    LaunchedEffect(configuration) {
//        // Save any changes to the orientation value on the configuration object
//        snapshotFlow { configuration.orientation }
//            .collect { orientation = it }
//    }

//    when (orientation) {
//
//        Configuration.ORIENTATION_LANDSCAPE -> {
//
//            HorizontalChat(
//                streamViewModel,
//                autoModViewModel
//            )
//        }
//        else -> {
//
//            // Below is the behemoth I am trying to rework
//            // be warned, the code below is not for those weak of heart
//            ModalBottomSheetLayout(
//                sheetBackgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
//                sheetGesturesEnabled =false,
//                sheetContent ={},
//                sheetState = outerBottomModalState
//            ) {
//
//
//                ModalBottomSheetLayout(
//                    sheetBackgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
//                    sheetState = bottomModalState,
//                    sheetContent = {
//                        // bottom modal below
//                        BanTimeOutDialogs(
//                            clickedUsernameChats = clickedUsernameChats,
//                            clickedUsername = streamViewModel.clickedUIState.value.clickedUsername,
//                            bottomModalState = bottomModalState,
//                            textFieldValue = streamViewModel.textFieldValue,
//                            closeBottomModal = { scope.launch { bottomModalState.hide() } },
//                            banned = streamViewModel.clickedUIState.value.clickedUsernameBanned,
//                            unbanUser = { streamViewModel.unBanUser() },
//                            isMod = streamViewModel.clickedUIState.value.clickedUsernameIsMod,
//                            openTimeoutDialog = { streamViewModel.openTimeoutDialog.value = true },
//                            closeTimeoutDialog = {
//                                streamViewModel.openTimeoutDialog.value = false
//                            },
//                            timeOutDialogOpen = streamViewModel.openTimeoutDialog.value,
//                            timeoutDuration = streamViewModel.state.value.timeoutDuration,
//                            timeoutReason = streamViewModel.state.value.timeoutReason,
//                            changeTimeoutDuration = { duration ->
//                                streamViewModel.changeTimeoutDuration(
//                                    duration
//                                )
//                            },
//                            changeTimeoutReason = { reason ->
//                                streamViewModel.changeTimeoutReason(
//                                    reason
//                                )
//                            },
//                            closeDialog = {
//                                streamViewModel.openTimeoutDialog.value = false
//                                scope.launch { bottomModalState.hide() }
//
//                            },
//                            timeOutUser = {
//                                streamViewModel.timeoutUser()
//                            },
//                            banDialogOpen = streamViewModel.openBanDialog.value,
//                            openBanDialog = { streamViewModel.openBanDialog.value = true },
//                            closeBanDialog = {
//                                scope.launch {
//                                    streamViewModel.openBanDialog.value = false
//                                }
//                            },
//                            banReason = streamViewModel.state.value.banReason,
//                            changeBanReason = { reason -> streamViewModel.changeBanReason(reason) },
//                            banUser = { streamViewModel.banUser() },
//                            shouldMonitorUser = streamViewModel.shouldMonitorUser.value,
//                            updateShouldMonitorUser = {streamViewModel.updateShouldMonitorUser()}
//                        )
//
//                    }
//                ) {
//
//
//                    SideModal(
//                        drawerState = drawerState,
//                        drawerContent = {
//
//                            if(showAdvancedChatSettings){
//                                ChatSettingsContainer.EnhancedChatSettingsBox(
//                                    enableSwitches = streamViewModel.modChatSettingsState.value.switchesEnabled,
//                                    showChatSettingAlert = streamViewModel.modChatSettingsState.value.showChatSettingAlert,
//                                    chatSettingsData = streamViewModel.modChatSettingsState.value.data,
//                                    updateChatSettings = { newData ->
//                                        streamViewModel.toggleChatSettings(
//                                            newData
//                                        )
//                                    },
//                                    closeAlertHeader = { streamViewModel.closeSettingsAlertHeader() },
//                                    showUndoButton = { showStatus ->
//                                        streamViewModel.showUndoButton(
//                                            showStatus
//                                        )
//                                    },
//                                    showUndoButtonStatus = streamViewModel.modChatSettingsState.value.showUndoButton,
//                                    noChatMode = streamViewModel.advancedChatSettingsState.value.noChatMode,
//                                    setNoChatMode = { state -> streamViewModel.setNoChatMode(state) },
//                                    advancedChatSettings = streamViewModel.advancedChatSettingsState.value,
//                                    updateAdvancedChatSettings = { data ->
//                                        streamViewModel.updateAdvancedChatSettings(
//                                            data
//                                        )
//                                    },
//                                    userIsModerator = modStatus ?: false
//                                )
//                            }else{
//                                StreamManagerUI(
//                                    showModView={
//                                        streamViewModel.setAutoModSettings(false)
//                                        showStreamManager()
//                                    },
//                                )
//
//                            }
//                            //TODO: THIS IS WHERE THE TABBED ROW IS GOING TO GO
//
//
//                        },
//                        contentCoveredBySideModal = {
//                            TestingLazyColumn(twitchUserChat = twitchUserChat)
////                            TextChat(
////                                notificationAmount =notificationAmount,
////                                twitchUserChat = twitchUserChat,
////                                sendMessageToWebSocket = { string ->
////                                    streamViewModel.sendMessage(string)
////                                },
////
////                                modStatus = modStatus,
////                                bottomModalState = bottomModalState,
////                                filteredChatList = filteredChat,
////                                clickedAutoCompleteText = { username ->
////                                    streamViewModel.autoTextChange(username)
////                                },
////
////                                addChatter = { username, message ->
////                                    streamViewModel.addChatter(
////                                        username,
////                                        message
////                                    )
////                                },
//                                updateClickedUser = { username, userId, banned, isMod ->
//
//                                    streamViewModel.updateClickedChat(
//                                        username,
//                                        userId,
//                                        banned,
//                                        isMod
//                                    )
//                                },
////                                textFieldValue = streamViewModel.textFieldValue,
////                                channelName = streamViewModel.channelName.collectAsState().value,
////                                deleteMessage = { messageId ->
////                                    streamViewModel.deleteChatMessage(
////                                        messageId
////                                    )
////                                },
////
////                                banResponse = streamViewModel.state.value.banResponse,
////                                undoBan = { streamViewModel.unBanUser() },
////                                undoBanResponse = streamViewModel.state.value.undoBanResponse,
////                                showStickyHeader = streamViewModel.state.value.showStickyHeader,
////                                closeStickyHeader = { streamViewModel.closeStickyHeader() },
////                                banResponseMessage = streamViewModel.state.value.banResponseMessage,
////                                restartWebSocket = { streamViewModel.restartWebSocket() },
////                                showUndoButton = streamViewModel.modChatSettingsState.value.showUndoButton,
////                                noChatMode = streamViewModel.advancedChatSettingsState.value.noChatMode,
////                                showOuterBottomModalState = {
////                                    scope.launch {
////                                        showAdvancedChatSettings = false
////                                        drawerState.open()
////                                    }
////                                },
////                                newFilterMethod={newTextValue -> streamViewModel.newParsingAgain(newTextValue)},
////                                forwardSlashCommands = streamViewModel.forwardSlashCommands,
////                                clickedCommandAutoCompleteText={ command ->
////                                    streamViewModel.autoTextChangeCommand(
////                                        command
////                                    )
////                                },
////                                toggleTimeoutDialog={streamViewModel.openTimeoutDialog.value = true},
////                                toggleBanDialog={streamViewModel.openBanDialog.value = true},
////                                openSideDrawer={
////                                    scope.launch {
////                                        showAdvancedChatSettings = true
////                                        drawerState.open()
////                                    }
////                                },
////                                orientationIsVertical =true
////                            )
////                            VerticalOverlayView(
////                                channelName = streamViewModel.clickedStreamInfo.value.channelName,
////                                streamTitle = streamViewModel.clickedStreamInfo.value.streamTitle,
////                                category = streamViewModel.clickedStreamInfo.value.category,
////                                tags = streamViewModel.clickedStreamInfo.value.tags,
////                                showStreamDetails = autoModViewModel.verticalOverlayIsVisible.collectAsState().value
////                            )
//                        }
//                    )
//                } // end of the bottom modal
//
//            }
//
    val updateClickedUser:(String,String,Boolean,Boolean)->Unit = remember(streamViewModel) { { username, userId, banned, isMod ->
        streamViewModel.updateClickedChat(
                username,
                userId,
                banned,
                isMod
            )
    } }
    val showBottomModal:()->Unit =remember(bottomModalState) { {
        scope.launch {
            bottomModalState.show()
        }
    } }

    ModalBottomSheetLayout(
                    sheetBackgroundColor = MaterialTheme.colorScheme.primary,
                    sheetState = bottomModalState,
                    sheetContent = {
                        BottomModalBuilder(
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
                                                },

                            openBanDialog = {  },
                            shouldMonitorUser = streamViewModel.shouldMonitorUser.value,
                            updateShouldMonitorUser = {
                               // streamViewModel.updateShouldMonitorUser()
                            }
                        )
                    }
    ){
        //this is where the chatUI goes
        ChatUI(
            twitchUserChat = twitchUserChat,
            showBottomModal={
                showBottomModal()
            },
            updateClickedUser = { username, userId, banned, isMod ->
                updateClickedUser(
                    username,
                    userId,
                    banned,
                    isMod
                )

            },
        )
   }


}


fun someNonScopedFunction() {
    print("Do something")
}






    @Composable
    fun SideModal(
        drawerState: DrawerState,
        contentCoveredBySideModal: @Composable () -> Unit,
        drawerContent: @Composable () -> Unit,

        ) {
        val enabled = drawerState.currentValue == androidx.compose.material3.DrawerValue.Open
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = enabled,
            drawerContent = {
                ModalDrawerSheet {
                    drawerContent()
                }
            }
        ) {
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
        openSideDrawer: () -> Unit,
        modStatus: Boolean?,
        bottomModalState: ModalBottomSheetState,
        filteredChatList: List<String>,
        clickedAutoCompleteText: (String) -> Unit,
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
        restartWebSocket: () -> Unit,
        showUndoButton: Boolean,
        noChatMode: Boolean,
        showOuterBottomModalState: () -> Unit,
        newFilterMethod: (TextFieldValue) -> Unit,
        forwardSlashCommands: List<ForwardSlashCommands>,
        clickedCommandAutoCompleteText: (String) -> Unit,
        toggleTimeoutDialog: () -> Unit,
        toggleBanDialog: () -> Unit,
        orientationIsVertical: Boolean,
        notificationAmount: Int

    ) {

        AutoScrollChatWithTextBox(
            showStickyHeader = showStickyHeader,
            closeStickyHeader = { closeStickyHeader() },
            twitchUserChat = twitchUserChat,
            bottomModalState = bottomModalState,
            restartWebSocket = { restartWebSocket() },
            banResponseMessage = banResponseMessage,
            updateClickedUser = { username, userId, banned, isMod ->
                updateClickedUser(
                    username,
                    userId,
                    banned,
                    isMod
                )
            },
            deleteMessage = { messageId -> deleteMessage(messageId) },
            sendMessageToWebSocket = { text -> sendMessageToWebSocket(text) },
            modStatus = modStatus,
            filteredChatList = filteredChatList,
            clickedAutoCompleteText = { username ->
                clickedAutoCompleteText(
                    username
                )
            },
            textFieldValue = textFieldValue,
            channelName = channelName,
            openSideDrawer = {
                openSideDrawer()
            },
            undoBan = { undoBan() },
            showUndoButton = showUndoButton,
            noChatMode = noChatMode,
            showOuterBottomModalState = { showOuterBottomModalState() },
            newFilterMethod = { newTextValue -> newFilterMethod(newTextValue) },
            forwardSlashCommandsList = forwardSlashCommands,
            clickedCommandAutoCompleteText = { command ->
                clickedCommandAutoCompleteText(
                    command
                )
            },
            toggleTimeoutDialog = { toggleTimeoutDialog() },
            toggleBanDialog = { toggleBanDialog() },
            orientationIsVertical = orientationIsVertical,
            notificationAmount = notificationAmount
        )

    }





