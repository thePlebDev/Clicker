package com.example.clicker.presentation.stream

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.TextFieldValue
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.presentation.home.HomeViewModel
import com.example.clicker.presentation.stream.views.AutoMod
import com.example.clicker.presentation.stream.views.BottomModal.BanTimeOutDialogs
import com.example.clicker.presentation.stream.views.ChatSettingsContainer
import com.example.clicker.presentation.stream.views.MainChat
import com.example.clicker.util.Response
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StreamView(
    streamViewModel: StreamViewModel,
    autoModViewModel: AutoModViewModel,
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
    val outerBottomModalState = rememberModalBottomSheetState(
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

            HorizontalChat(
                streamViewModel,
                autoModViewModel
            )
        }
        else -> {

            // Below is the behemoth I am trying to rework
            // be warned, the code below is not for those weak of heart
            ModalBottomSheetLayout(
                sheetBackgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                sheetGesturesEnabled =false,
                sheetContent ={

                    AutoMod.Settings(
                        sliderPosition = autoModViewModel.autoModUIState.value.sliderValue,
                        changSliderPosition = {currentValue -> autoModViewModel.updateSliderValue(currentValue)},

                        discriminationFilterList=autoModViewModel.autoModUIState.value.filterList,

                        changeSelectedIndex = {newIndex,filterType -> autoModViewModel.updateSelectedIndex(newIndex,filterType)},
                        updateAutoModSettings = {autoModViewModel.updateAutoMod()},

                        sexBasedTermsIndex = autoModViewModel.autoModUIState.value.sexBasedTerms,
                        swearingIndex = autoModViewModel.autoModUIState.value.swearing,
                        aggressionIndex =autoModViewModel.autoModUIState.value.aggression,
                        bullyingIndex = autoModViewModel.autoModUIState.value.bullying,
                        disabilityIndex =autoModViewModel.autoModUIState.value.disability,
                        sexualityIndex =autoModViewModel.autoModUIState.value.sexuality,
                        misogynyIndex =autoModViewModel.autoModUIState.value.misogyny,
                        raceIndex =autoModViewModel.autoModUIState.value.race,
                        filterText=autoModViewModel.autoModUIState.value.filterText,
                        isModerator = autoModViewModel.autoModCredentials.value.isModerator
                    )

                },
                sheetState = outerBottomModalState
            ) {


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
                            openTimeoutDialog = { streamViewModel.openTimeoutDialog.value = true },
                            closeTimeoutDialog = {
                                streamViewModel.openTimeoutDialog.value = false
                            },
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
                        drawerContent = {

                            //TODO: THIS IS WHERE THE TABBED ROW IS GOING TO GO
                            ChatSettingsContainer.EnhancedChatSettingsBox(
                                enableSwitches = streamViewModel.modChatSettingsState.value.switchesEnabled,
                                showChatSettingAlert = streamViewModel.modChatSettingsState.value.showChatSettingAlert,
                                chatSettingsData = streamViewModel.modChatSettingsState.value.data,
                                updateChatSettings = { newData ->
                                    streamViewModel.toggleChatSettings(
                                        newData
                                    )
                                },
                                closeAlertHeader = { streamViewModel.closeSettingsAlertHeader() },
                                showUndoButton = { showStatus ->
                                    streamViewModel.showUndoButton(
                                        showStatus
                                    )
                                },
                                showUndoButtonStatus = streamViewModel.modChatSettingsState.value.showUndoButton,
                                noChatMode = streamViewModel.advancedChatSettingsState.value.noChatMode,
                                setNoChatMode = { state -> streamViewModel.setNoChatMode(state) },
                                advancedChatSettings = streamViewModel.advancedChatSettingsState.value,
                                updateAdvancedChatSettings = { data ->
                                    streamViewModel.updateAdvancedChatSettings(
                                        data
                                    )
                                },
                                userIsModerator = modStatus ?: false
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
                                clickedAutoCompleteText = { username ->
                                    streamViewModel.autoTextChange(username)
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
                                oneClickBanUser = { userId -> streamViewModel.oneClickBanUser(userId) },
                                oneClickTimeoutUser = {},
                                showUndoButton = streamViewModel.modChatSettingsState.value.showUndoButton,
                                noChatMode = streamViewModel.advancedChatSettingsState.value.noChatMode,
                                showOuterBottomModalState = {
                                    scope.launch {
                                        outerBottomModalState.show()
                                    }
                                },
                                newFilterMethod={newTextValue -> streamViewModel.newParsingAgain(newTextValue)},
                                forwardSlashCommands = streamViewModel.forwardSlashCommands
                            )
                        }
                    )
                } // end of the bottom modal

            }

        }
    }
}

@Composable
fun SideModal(
    drawerState: DrawerState,
    contentCoveredBySideModal:@Composable () -> Unit,
    drawerContent:@Composable () -> Unit,

){
    val enabled = drawerState.currentValue == androidx.compose.material3.DrawerValue.Open
    ModalNavigationDrawer(
        drawerState =drawerState,
        gesturesEnabled = enabled,
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
    removeUnBanButton: () -> Unit,
    restartWebSocket: () -> Unit,
    showOneClickAction:Boolean,
    oneClickBanUser: (String) -> Unit,
    oneClickTimeoutUser: (String) -> Unit,
    showUndoButton:Boolean,
    noChatMode:Boolean,
    showOuterBottomModalState:() ->Unit,
    newFilterMethod:(TextFieldValue) ->Unit,
    forwardSlashCommands: List<ForwardSlashCommands>

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
        clickedAutoCompleteText = { username ->
            clickedAutoCompleteText(
                username
            )
        },
        textFieldValue = textFieldValue,
        channelName = channelName,
        drawerState =drawerState,
        undoBan = {undoBan()},
        showUndoButton =showUndoButton,
        noChatMode =noChatMode,
        showOuterBottomModalState ={showOuterBottomModalState()},
        newFilterMethod ={newTextValue -> newFilterMethod(newTextValue)},
        forwardSlashCommands =forwardSlashCommands
    )

}



