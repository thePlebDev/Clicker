package com.example.clicker.presentation.stream

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
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
import androidx.compose.ui.text.TextRange
import com.example.clicker.network.repository.models.EmoteNameUrl
import com.example.clicker.presentation.modView.ModViewViewModel
import com.example.clicker.presentation.modView.followerModeListImmutable
import com.example.clicker.presentation.modView.slowModeListImmutable


import com.example.clicker.presentation.stream.views.BottomModal
import com.example.clicker.presentation.stream.views.TestingNewBottomModal

import com.example.clicker.presentation.stream.views.chat.chatSettings.ChatSettingsColumn
import com.example.clicker.presentation.stream.views.chat.ChatUI
import com.example.clicker.presentation.stream.views.chat.chatSettings.ChatSettingsViewModel
import com.example.clicker.presentation.stream.views.dialogs.WarningDialog

import kotlinx.coroutines.launch



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HorizontalChat(
    streamViewModel: StreamViewModel,
    autoModViewModel:AutoModViewModel,
    chatSettingsViewModel: ChatSettingsViewModel,
    modViewViewModel:ModViewViewModel,
    hideSoftKeyboard:()->Unit,
){
    val twitchUserChat = streamViewModel.listChats.toList()
    val lazyColumnListState = rememberLazyListState()
    var autoscroll by remember { mutableStateOf(true) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val clickedUsername = streamViewModel.clickedUIState.value.clickedUsername

    val textFieldValue = streamViewModel.textFieldValue
    var showAdvancedChatSettings by remember { mutableStateOf(true) }

    val scope =rememberCoroutineScope()

    val bottomModalState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    var bottomModalStateImmutable by remember { mutableStateOf(BottomModalStateImmutable(bottomModalState)) }


    val outerBottomModalState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )




    val chatSettingData = streamViewModel.state.value.chatSettings
    val modStatus = streamViewModel.state.value.loggedInUserData?.mod
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
    val updateMostFrequentEmoteList:(EmoteNameUrl)->Unit =remember(streamViewModel) { {

        streamViewModel.updateTemporaryMostFrequentList(it)
    } }
    val hideClickedUserBottomModal:()->Unit =remember(bottomModalState) { {
        scope.launch {
            bottomModalState.hide()
        }
    } }

    val changeActualTextFieldValue:(String, TextRange) -> Unit = remember(streamViewModel) { { text, textRange ->
        streamViewModel.changeActualTextFieldValue(text, textRange)
    } }

    val sendMessageToWebSocket:(String) -> Unit = remember(streamViewModel) { { message ->
        streamViewModel.sendMessage(message)
        streamViewModel.updateMostFrequentEmoteList()
    } }

    /*******ALL FUNCTIONS RELATED TO CHAT SETTINGS SIZE***********/
    val changeBadgeSize:(Float) -> Unit = remember(chatSettingsViewModel) { {newValue ->
        chatSettingsViewModel.changeBadgeSize(newValue)
    } }
    val changeEmoteSize:(Float) -> Unit = remember(chatSettingsViewModel) { {newValue ->
        chatSettingsViewModel.changeEmoteSize(newValue)
    } }
    val changeUsernameSize:(Float) -> Unit = remember(chatSettingsViewModel) { {newValue ->
        chatSettingsViewModel.changeUsernameSize(newValue)
    } }
    val changeMessageSize:(Float) -> Unit = remember(chatSettingsViewModel) { {newValue ->
        chatSettingsViewModel.changeMessageSize(newValue)
    } }
    val changeLineHeight:(Float) -> Unit = remember(chatSettingsViewModel) { {newValue ->
        chatSettingsViewModel.changeLineHeight(newValue)
    } }
    val changeCustomUsernameColor:(Boolean) -> Unit = remember(chatSettingsViewModel) { {newValue ->
        chatSettingsViewModel.changeCustomUsernameColor(newValue)
    } }

    val unBanUser:() -> Unit = remember(streamViewModel) { {
        streamViewModel.unBanUser()
    } }
    val setOpenTimeoutDialogTrue:() -> Unit = remember(streamViewModel) { {
        streamViewModel.setOpenTimeoutDialogTrue()
    } }
    val setOpenBanDialogTrue:() -> Unit = remember(streamViewModel) { {
        streamViewModel.setOpenBanDialogTrue()
    } }

    val changeOpenWarningDialog:() -> Unit = remember(streamViewModel) { {
        streamViewModel.changeOpenWarningDialog(true)
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

                followerModeListImmutable = followerModeListImmutable,
                slowModeListImmutable= slowModeListImmutable,

                selectedFollowersModeItem=modViewViewModel.uiState.value.selectedFollowerMode,
                changeSelectedFollowersModeItem ={newValue ->
                    modViewViewModel.changeSelectedFollowersModeItem(newValue)},

                selectedSlowModeItem=modViewViewModel.uiState.value.selectedSlowMode,
                changeSelectedSlowModeItem ={newValue ->modViewViewModel.changeSelectedSlowModeItem(newValue)},
                emoteOnly = modViewViewModel.uiState.value.emoteOnly,
                setEmoteOnly = {newValue ->modViewViewModel.updateEmoteOnly(newValue)},
                subscriberOnly =modViewViewModel.uiState.value.subscriberOnly,
                setSubscriberOnly={newValue -> modViewViewModel.updateSubscriberOnly(newValue)},

                badgeSize = chatSettingsViewModel.badgeSize.value,
                changeBadgeSize = {newValue-> changeBadgeSize(newValue)},
                emoteSize = chatSettingsViewModel.emoteSize.value,
                changeEmoteSize={newValue -> changeEmoteSize(newValue)},
                usernameSize = chatSettingsViewModel.usernameSize.value,
                changeUsernameSize ={newValue ->changeUsernameSize(newValue)},
                messageSize = chatSettingsViewModel.messageSize.value,
                changeMessageSize={newValue ->changeMessageSize(newValue)},
                lineHeight = chatSettingsViewModel.lineHeight.value,
                changeLineHeight = {newValue -> changeLineHeight(newValue)},
                customUsernameColor = chatSettingsViewModel.customUsernameColor.value,
                changeCustomUsernameColor = {newValue -> changeCustomUsernameColor(newValue)}
            )
        }
    ) {

        ModalBottomSheetLayout(
            sheetBackgroundColor = MaterialTheme.colorScheme.primary,
            sheetState = bottomModalState,
            sheetContent = {
                TestingNewBottomModal(
                    clickedUsername = streamViewModel.clickedUIState.value.clickedUsername,
                    textFieldValue = streamViewModel.textFieldValue,
                    closeBottomModal = {
                        hideClickedUserBottomModal()
                    },
                    banned = streamViewModel.clickedUIState.value.clickedUsernameBanned,
                    unbanUser = {
                        unBanUser()
                    },
                    isMod = streamViewModel.state.value.loggedInUserData?.mod ?: false,
                    openTimeoutDialog = {
                        setOpenTimeoutDialogTrue()
                    },
                    openBanDialog = {
                        setOpenBanDialogTrue()
                    },
                    clickedUsernameChatsDateSentImmutable = streamViewModel.clickedUsernameChatsDateSentImmutable.value,
                    openWarnDialog={
                        changeOpenWarningDialog()
                    },
                    badgeInlineContentMap=chatSettingsViewModel.globalChatBadgesMap.value,
                    clickedUserBadgeList =streamViewModel.clickedUserBadgesImmutable.value,
                    globalTwitchEmoteContentMap = chatSettingsViewModel.globalEmoteMap.value,
                    channelTwitchEmoteContentMap= chatSettingsViewModel.inlineContentMapChannelEmoteList.value,
                    globalBetterTTVEmoteContentMap =chatSettingsViewModel.betterTTVGlobalInlineContentMapChannelEmoteList.value,
                    channelBetterTTVEmoteContentMap =chatSettingsViewModel.betterTTVChannelInlineContentMapChannelEmoteList.value,
                    sharedBetterTTVEmoteContentMap =chatSettingsViewModel.betterTTVSharedInlineContentMapChannelEmoteList.value,

                    )
            }
        ){

            if(streamViewModel.openWarningDialog.value){
                WarningDialog(
                    onDismissRequest={
                        streamViewModel.changeOpenWarningDialog(false)
                                     },
                    warnUser={
                        streamViewModel.warnUser()
                        hideClickedUserBottomModal()
                             },
                    clickedUsername=streamViewModel.clickedUIState.value.clickedUsername,
                    waringText=streamViewModel.warningText.value,
                    changeWaringText={newValue->streamViewModel.changeWarningText(newValue)},

                    )
            }
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

                newFilterMethod={newTextValue -> streamViewModel.newParsingAgain(newTextValue)},

                orientationIsVertical =false,

                isMod = streamViewModel.state.value.loggedInUserData?.mod ?: false,
                clickedAutoCompleteText = { username ->
                    streamViewModel.autoTextChange(username)
                },
                showModal = {
                    showChatSettingsBottomModal()
                },
                notificationAmount =0,
                textFieldValue = streamViewModel.textFieldValue,
                sendMessageToWebSocket = { message ->
                    sendMessageToWebSocket(message)

                },
                noChat = streamViewModel.advancedChatSettingsState.value.noChatMode,
                deleteChatMessage = {messageId ->streamViewModel.deleteChatMessage(messageId)},
                clickedCommandAutoCompleteText={clickedValue -> streamViewModel.clickedCommandAutoCompleteText(clickedValue)},
                hideSoftKeyboard ={
                    hideSoftKeyboard()
                },
                emoteBoardGlobalList = streamViewModel.globalEmoteUrlList.value,
                updateTextWithEmote = {newValue -> streamViewModel.addEmoteToText(newValue)},
                emoteBoardChannelList =streamViewModel.channelEmoteUrlList.value,
                deleteEmote={streamViewModel.deleteEmote()},
                showModView = {},
                emoteBoardMostFrequentList= streamViewModel.mostFrequentEmoteListTesting.value,
                updateTempararyMostFrequentEmoteList={value ->updateMostFrequentEmoteList(value)},
                globalBetterTTVEmotes=streamViewModel.globalBetterTTVEmotes.value,
                channelBetterTTVResponse = streamViewModel.channelBetterTTVEmote.value,
                sharedBetterTTVResponse= streamViewModel.sharedChannelBetterTTVEmote.value,
                userIsSub = streamViewModel.state.value.loggedInUserData?.sub ?: false,
                forwardSlashes = streamViewModel.forwardSlashCommandImmutable.value,
                filteredChatListImmutable = streamViewModel.filteredChatListImmutable.value,
                actualTextFieldValue = streamViewModel.textFieldValue.value,
                changeActualTextFieldValue={text,textRange->
                    changeActualTextFieldValue(text, textRange)
                },
                badgeListMap= streamViewModel.badgeListMap.value,
                usernameSize = chatSettingsViewModel.usernameSize.value,
                messageSize = chatSettingsViewModel.messageSize.value,
                lineHeight=chatSettingsViewModel.lineHeight.value,
                useCustomUsernameColors = chatSettingsViewModel.customUsernameColor.value,
                globalTwitchEmoteContentMap = chatSettingsViewModel.globalEmoteMap.value,
                channelTwitchEmoteContentMap= chatSettingsViewModel.inlineContentMapChannelEmoteList.value,
                globalBetterTTVEmoteContentMap =chatSettingsViewModel.betterTTVGlobalInlineContentMapChannelEmoteList.value,
                channelBetterTTVEmoteContentMap =chatSettingsViewModel.betterTTVChannelInlineContentMapChannelEmoteList.value,
                sharedBetterTTVEmoteContentMap =chatSettingsViewModel.betterTTVSharedInlineContentMapChannelEmoteList.value,
                lowPowerMode= streamViewModel.lowPowerModeActive.value,
                channelName = streamViewModel.channelName.value ?:""

            )


        }
    }




}

