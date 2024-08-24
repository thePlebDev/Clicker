package com.example.clicker.presentation.stream

import android.content.res.Configuration
import android.util.Log
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.example.clicker.network.repository.EmoteNameUrl
import com.example.clicker.presentation.home.HomeViewModel
import com.example.clicker.presentation.modView.ListTitleValue
import com.example.clicker.presentation.modView.ModViewViewModel
import com.example.clicker.presentation.modView.slowModeListImmutable
import com.example.clicker.presentation.modView.followerModeListImmutable

import com.example.clicker.presentation.stream.views.TestingNewBottomModal
import com.example.clicker.presentation.stream.views.chat.BadgeSlider
import com.example.clicker.presentation.stream.views.chat.chatSettings.ChatSettingsColumn
import com.example.clicker.presentation.stream.views.chat.ChatUI
import com.example.clicker.presentation.stream.views.chat.chatSettings.ChatSettingsViewModel
import com.example.clicker.presentation.stream.views.dialogs.ImprovedBanDialog
import com.example.clicker.presentation.stream.views.dialogs.ImprovedTimeoutDialog
import com.example.clicker.presentation.stream.views.dialogs.WarningDialog
import com.example.clicker.presentation.stream.views.overlays.VerticalOverlayView
import kotlinx.coroutines.launch




@Immutable
data class BottomModalStateImmutable @OptIn(ExperimentalMaterialApi::class) constructor(
    val bottomModalState: ModalBottomSheetState
)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StreamView(
    streamViewModel: StreamViewModel,
    autoModViewModel: AutoModViewModel,
    modViewViewModel: ModViewViewModel,
    chatSettingsViewModel: ChatSettingsViewModel,
    homeViewModel: HomeViewModel,
    hideSoftKeyboard:()->Unit,
    showModView:()->Unit,
    modViewIsVisible:Boolean,

) {
    val twitchUserChat = streamViewModel.listChats.toList()
//    val drawerState = rememberDrawerState(androidx.compose.material3.DrawerValue.Closed)
//    val chatSettingData = streamViewModel.state.value.chatSettings
//    val modStatus = streamViewModel.state.value.loggedInUserData?.mod
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
    var bottomModalStateImmutable by remember { mutableStateOf(BottomModalStateImmutable(bottomModalState)) }

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
    val hideClickedUserBottomModal:()->Unit =remember(bottomModalState) { {
        scope.launch {
            bottomModalState.hide()
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

    val updateMostFrequentEmoteList:(EmoteNameUrl)->Unit =remember(streamViewModel) { {
        streamViewModel.updateTemporaryMostFrequentList(it)
    } }

    val updateTextWithEmote:(String)->Unit =remember(streamViewModel) { {
        streamViewModel.addEmoteToText(it)
    } }

    val updateClickedUser:(String,String,Boolean,Boolean)->Unit = remember(streamViewModel) { { username, userId, banned, isMod ->
        streamViewModel.updateClickedChat(
            username,
            userId,
            banned,
            isMod
        )
    } }

    val newFilterMethod:(TextFieldValue) -> Unit = remember(streamViewModel) { { newTextValue ->
        streamViewModel.newParsingAgain(newTextValue)
    } }


    val clearModViewNotifications:()->Unit = remember(modViewViewModel) { {
        modViewViewModel.clearModViewNotifications()
    } }


    val changeActualTextFieldValue:(String,TextRange) -> Unit = remember(streamViewModel) { { text,textRange ->
        streamViewModel.changeActualTextFieldValue(text, textRange)
    } }

    val sendMessageToWebSocket:(String) -> Unit = remember(streamViewModel) { { message ->
        streamViewModel.sendMessage(message)
        streamViewModel.updateMostFrequentEmoteList()

    } }

    val updateAdvancedChatSettings:(AdvancedChatSettings) -> Unit = remember(streamViewModel) { { newValue ->
        streamViewModel.updateAdvancedChatSettings(newValue)
    } }

    val setNoChatMode:(Boolean) -> Unit = remember(streamViewModel) { { newValue ->
        streamViewModel.setNoChatMode(newValue)
    } }

    val changeSelectedFollowersModeItem:(ListTitleValue) -> Unit = remember(modViewViewModel) { { newValue ->
        modViewViewModel.changeSelectedFollowersModeItem(newValue)
    } }

    val changeSelectedSlowModeItem:(ListTitleValue) -> Unit = remember(modViewViewModel) { { newValue ->
        modViewViewModel.changeSelectedSlowModeItem(newValue)
    } }

    val updateEmoteOnly:(Boolean) -> Unit = remember(modViewViewModel) { { newValue ->
        modViewViewModel.updateEmoteOnly(newValue)
    } }
    val updateSubscriberOnly:(Boolean) -> Unit = remember(modViewViewModel) { { newValue ->
        modViewViewModel.updateSubscriberOnly(newValue)
    } }


    val timeoutUser:() -> Unit = remember(streamViewModel) { {
        streamViewModel.timeoutUser()
    } }

    val setOpenTimeoutDialogFalse:() -> Unit = remember(streamViewModel) { {
        streamViewModel.setOpenTimeoutDialogFalse()
    } }

    val changeTimeoutDuration:(Int) -> Unit = remember(streamViewModel) { {duration ->
        streamViewModel.changeTimeoutDuration(duration)
    } }

    val changeTimeoutReason:(String) -> Unit = remember(streamViewModel) { {reason ->
        streamViewModel.changeTimeoutReason(reason)
    } }
    val changeBanReason:(String) -> Unit = remember(streamViewModel) { {reason ->
        streamViewModel.changeBanReason(reason)
    } }

    val banUser:() -> Unit = remember(streamViewModel) { {
        streamViewModel.banUser()
    } }
    val setOpenBanDialogFalse:() -> Unit = remember(streamViewModel) { {
        streamViewModel.setOpenBanDialogFalse()
    } }

    val setOpenBanDialogTrue:() -> Unit = remember(streamViewModel) { {
        streamViewModel.setOpenBanDialogTrue()
    } }
    val setOpenTimeoutDialogTrue:() -> Unit = remember(streamViewModel) { {
        streamViewModel.setOpenTimeoutDialogTrue()
    } }

    val changeOpenWarningDialog:() -> Unit = remember(streamViewModel) { {
        streamViewModel.changeOpenWarningDialog(true)
    } }

    val clickedCommandAutoCompleteText:(String) -> Unit = remember(streamViewModel) { {clickedValue->
        streamViewModel.clickedCommandAutoCompleteText(clickedValue)
    } }
    val deleteEmote:() -> Unit = remember(streamViewModel) { {
        streamViewModel.deleteEmote()
    } }
    val unBanUser:() -> Unit = remember(streamViewModel) { {
        streamViewModel.unBanUser()
    } }
    val getUnbanRequests:() -> Unit = remember(modViewViewModel) { {
        modViewViewModel.getUnbanRequests()
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









    val showWarnDialog = remember{ mutableStateOf(false) }
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
                streamViewModel=streamViewModel,
                autoModViewModel=autoModViewModel,
                modViewViewModel=modViewViewModel,
                hideSoftKeyboard={
                    hideSoftKeyboard()
                },
                chatSettingsViewModel=chatSettingsViewModel,
            )
        }
        else -> {
            ModalBottomSheetLayout(
                sheetState = outerBottomModalState,
                sheetContent ={
                    ChatSettingsColumn(
                        advancedChatSettings = streamViewModel.advancedChatSettingsState.value,
                        changeAdvancedChatSettings = {newValue -> updateAdvancedChatSettings(newValue) },
                        changeNoChatMode = {newValue -> setNoChatMode(newValue) },
                        chatSettingsEnabled = streamViewModel.state.value.loggedInUserData?.mod ?: false,
                        followerModeListImmutable = followerModeListImmutable,
                        slowModeListImmutable=slowModeListImmutable,
                        selectedFollowersModeItem=modViewViewModel.uiState.value.selectedFollowerMode,
                        changeSelectedFollowersModeItem ={newValue -> changeSelectedFollowersModeItem(newValue) },
                        selectedSlowModeItem=modViewViewModel.uiState.value.selectedSlowMode,
                        changeSelectedSlowModeItem ={newValue -> changeSelectedSlowModeItem(newValue) },
                        emoteOnly = modViewViewModel.uiState.value.emoteOnly,
                        setEmoteOnly = {newValue -> updateEmoteOnly(newValue) },
                        subscriberOnly =modViewViewModel.uiState.value.subscriberOnly,
                        setSubscriberOnly={newValue -> updateSubscriberOnly(newValue) },

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
                    sheetState = bottomModalStateImmutable.bottomModalState,
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
                            inlineContentMap=streamViewModel.inlineTextContentTest.value,
                            clickedUserBadgeList =streamViewModel.clickedUserBadgesImmutable.value

                            )
                    }
                ){

                    //this is where the chatUI goes
                    if(streamViewModel.openWarningDialog.value){
                        WarningDialog(
                            onDismissRequest={streamViewModel.changeOpenWarningDialog(false)},
                            warnUser={
                                streamViewModel.warnUser()
                                hideClickedUserBottomModal()
                                     },
                            clickedUsername=streamViewModel.clickedUIState.value.clickedUsername,
                            waringText=streamViewModel.warningText.value,
                            changeWaringText={newValue->streamViewModel.changeWarningText(newValue)},

                        )
                    }


                    //todo:this is the dialogs
                    TimeoutBanDialogs(
                        showTimeOutDialog =streamViewModel.openTimeoutDialog.value,
                        username = streamViewModel.clickedUIState.value.clickedUsername,
                        timeoutUser={ timeoutUser() },
                        timeoutDuration=streamViewModel.state.value.timeoutDuration,
                        closeTimeoutDialog = { setOpenTimeoutDialogFalse() },
                        changeTimeoutDuration={newDuration -> changeTimeoutDuration(newDuration) },
                        timeoutReason=streamViewModel.state.value.timeoutReason,
                        changeTimeoutReason = {reason -> changeTimeoutReason(reason) },
                        showBanDialog =streamViewModel.openBanDialog.value,
                        changeBanReason = {newReason -> changeBanReason(newReason) },
                        banUser  ={ banUser() },
                        closeBanDialog = { setOpenBanDialogFalse() },
                        banReason = streamViewModel.state.value.banReason,
                    )
                    if(!modViewIsVisible){
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

                            newFilterMethod={newTextValue ->
                                newFilterMethod(newTextValue)
                                            },

                            orientationIsVertical =true,

                            isMod = streamViewModel.state.value.loggedInUserData?.mod ?: false,
                            clickedAutoCompleteText = { username ->
                                streamViewModel.autoTextChange(username)
                            },
                            showModal = {
                                showChatSettingsBottomModal()
                            },
                            notificationAmount =modViewViewModel.uiState.value.modViewTotalNotifications,
                            textFieldValue = streamViewModel.textFieldValue,
                            sendMessageToWebSocket = { message ->
                                sendMessageToWebSocket(message)
                            },
                            noChat = streamViewModel.advancedChatSettingsState.value.noChatMode,
                            deleteChatMessage={messageId ->streamViewModel.deleteChatMessage(messageId)},
                            clickedCommandAutoCompleteText={
                                    clickedValue ->
                                clickedCommandAutoCompleteText(clickedValue)

                                                           },
                            inlineContentMap = streamViewModel.inlineTextContentTest.value,
                            hideSoftKeyboard={hideSoftKeyboard()},
                            emoteBoardGlobalList = streamViewModel.globalEmoteUrlList.value,
                            //todo: this is what I need to change
                            updateTextWithEmote = {newValue -> updateTextWithEmote(newValue)},
                            emoteBoardChannelList =streamViewModel.channelEmoteUrlList.value,
                            emoteBoardMostFrequentList= streamViewModel.mostFrequentEmoteListTesting.value,
                            deleteEmote={
                                deleteEmote() // this needs to be changed
                                        },
                            showModView={
                                showModView()
                                clearModViewNotifications()
                           //    getUnbanRequests()
                            },
                            updateTempararyMostFrequentEmoteList = {value ->updateMostFrequentEmoteList(value)},
                            globalBetterTTVEmotes=streamViewModel.globalBetterTTVEmotes.value,
                            channelBetterTTVResponse = streamViewModel.channelBetterTTVEmote.value,
                            sharedBetterTTVResponse= streamViewModel.sharedChannelBetterTTVEmote.value,
                            userIsSub = streamViewModel.state.value.loggedInUserData?.sub ?: false,
                            forwardSlashes = streamViewModel.forwardSlashCommandImmutable.value,
                            filteredChatListImmutable = streamViewModel.filteredChatListImmutable.value,
                            actualTextFieldValue = streamViewModel.textFieldValue.value,
                            changeActualTextFieldValue={text,textRange->
                                changeActualTextFieldValue(text,textRange)
                            },
                            badgeListMap= chatSettingsViewModel.globalChatBadgesMap.value,





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
    Log.d("TimeoutBanDialogsRecomp","RECOMP")
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









