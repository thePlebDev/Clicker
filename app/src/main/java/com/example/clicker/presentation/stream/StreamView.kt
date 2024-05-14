package com.example.clicker.presentation.stream

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.presentation.home.HomeViewModel
import com.example.clicker.presentation.modView.ModViewViewModel
import com.example.clicker.presentation.modView.followerModeList
import com.example.clicker.presentation.modView.slowModeList

import com.example.clicker.presentation.stream.views.BottomModal.BottomModalBuilder
import com.example.clicker.presentation.stream.views.chat.ChatSettingsColumn
import com.example.clicker.presentation.stream.views.chat.ChatUI
import com.example.clicker.presentation.stream.views.dialogs.ImprovedBanDialog
import com.example.clicker.presentation.stream.views.dialogs.ImprovedTimeoutDialog
import com.example.clicker.presentation.stream.views.overlays.VerticalOverlayView
import com.example.clicker.util.Response
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StreamView(
    streamViewModel: StreamViewModel,
    autoModViewModel: AutoModViewModel,
    modViewViewModel: ModViewViewModel,
    homeViewModel: HomeViewModel,
    hideSoftKeyboard:()->Unit,
    notificationAmount: Int

) {
    val twitchUserChat = streamViewModel.listChats.toList()
//    val drawerState = rememberDrawerState(androidx.compose.material3.DrawerValue.Closed)
//    val chatSettingData = streamViewModel.state.value.chatSettings
//    val modStatus = streamViewModel.state.value.loggedInUserData?.mod
    val filteredChat = streamViewModel.filteredChatList
    val clickedUsernameChats = streamViewModel.clickedUsernameChats
    val scope = rememberCoroutineScope()
//    var showAdvancedChatSettings by remember { mutableStateOf(true) }
//
    val bottomModalState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val outerBottomModalState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

//    var oneClickActionsChecked by remember { mutableStateOf(true) }
//
//    //todo: Move these two to the ViewModel
//
//
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
//    val showTimeOutDialog = remember{ mutableStateOf(false) }
//    val showBanDialog = remember{ mutableStateOf(false) }
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
                autoModViewModel,
                modViewViewModel
            )
        }
        else -> {
            ModalBottomSheetLayout(
                sheetState = outerBottomModalState,
                sheetContent ={
                    ChatSettingsColumn(
                        advancedChatSettings = streamViewModel.advancedChatSettingsState.value,
                        changeAdvancedChatSettings = {newValue -> streamViewModel.updateAdvancedChatSettings(newValue)},
                        changeNoChatMode = {newValue -> streamViewModel.setNoChatMode(newValue)},

                        chatSettingsEnabled = streamViewModel.state.value.loggedInUserData?.mod ?: false,
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
                            isMod = streamViewModel.state.value.loggedInUserData?.mod ?: false,
                            openTimeoutDialog = {

                                streamViewModel.openTimeoutDialog.value = true
                            },

                            openBanDialog = {
                                streamViewModel.openBanDialog.value = true
                                            },
                            shouldMonitorUser = streamViewModel.shouldMonitorUser.value,
                            updateShouldMonitorUser = {

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
                        closeTimeoutDialog = {
                            streamViewModel.openTimeoutDialog.value = false
                                             },
                        changeTimeoutDuration={newDuration -> streamViewModel.changeTimeoutDuration(newDuration) },
                        timeoutReason=streamViewModel.state.value.timeoutReason,
                        changeTimeoutReason = {reason ->streamViewModel.changeTimeoutReason(reason)},
                        showBanDialog =streamViewModel.openBanDialog.value,
                        changeBanReason = {newReason -> streamViewModel.changeBanReason(newReason)},
                        banUser  ={
                            streamViewModel.banUser()
                        },
                        closeBanDialog = {
                            streamViewModel.openBanDialog.value = false
                                         },
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

                        orientationIsVertical =true,

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
                        deleteChatMessage={messageId ->streamViewModel.deleteChatMessage(messageId)},
                        forwardSlashCommands = streamViewModel.forwardSlashCommands,
                        clickedCommandAutoCompleteText={clickedValue -> streamViewModel.clickedCommandAutoCompleteText(clickedValue)},
                        inlineContentMap = streamViewModel.inlineTextContentTest.value,
                        hideSoftKeyboard={hideSoftKeyboard()},
                        emoteBoardGlobalList = streamViewModel.globalEmoteUrlList.value,
                        updateTextWithEmote = {newValue -> streamViewModel.addEmoteToText(newValue)}
                    )


                    VerticalOverlayView(
                        channelName = streamViewModel.clickedStreamInfo.value.channelName,
                        streamTitle = streamViewModel.clickedStreamInfo.value.streamTitle,
                        category = streamViewModel.clickedStreamInfo.value.category,
                        tags = streamViewModel.clickedStreamInfo.value.tags,
                        showStreamDetails = autoModViewModel.verticalOverlayIsVisible.collectAsState().value
                    )

                }
            }

        }
    }






}

@Composable
fun TimeoutBanDialogs(
    showTimeOutDialog:Boolean,
    closeTimeoutDialog:()->Unit,
    timeoutDuration:Int,
    changeTimeoutDuration:(Int)->Unit,
    timeoutReason:String,
    changeTimeoutReason: (String) -> Unit,
    timeoutUser:()->Unit,
    username:String,

    showBanDialog:Boolean,
    closeBanDialog:()->Unit,
    banReason:String,
    changeBanReason:(String)->Unit,
    banUser:()->Unit,
){
    if(showTimeOutDialog){

        ImprovedTimeoutDialog(
            onDismissRequest ={
                closeTimeoutDialog()
            },
            username = username,
            timeOutUser={
                timeoutUser()
            },
            timeoutDuration=timeoutDuration,
            changeTimeoutDuration={newDuration -> changeTimeoutDuration(newDuration) },
            timeoutReason=timeoutReason,
            changeTimeoutReason = {reason ->changeTimeoutReason(reason)},
        )
    }
    if(showBanDialog){
        ImprovedBanDialog(
            onDismissRequest ={
                closeBanDialog()
            },
            changeBanReason = {reason ->
                changeBanReason(reason)
            },
            username = username,
            banUser  ={
                banUser()
            },
            banReason = banReason,
        )
    }
}









