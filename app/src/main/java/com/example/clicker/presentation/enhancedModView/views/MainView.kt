package com.example.clicker.presentation.enhancedModView.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import com.example.clicker.R
import com.example.clicker.network.domain.UnbanStatusFilter
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.network.repository.models.EmoteNameUrl
import com.example.clicker.presentation.enhancedModView.ListTitleValue
import com.example.clicker.presentation.enhancedModView.viewModels.ModVersionThreeViewModel
import com.example.clicker.presentation.home.disableClickAndRipple

import com.example.clicker.presentation.enhancedModView.viewModels.ModViewViewModel
import com.example.clicker.presentation.enhancedModView.viewModels.followerModeListImmutable
import com.example.clicker.presentation.enhancedModView.viewModels.slowModeListImmutable
import com.example.clicker.presentation.stream.StreamViewModel
import com.example.clicker.presentation.stream.views.TestingNewBottomModal
import com.example.clicker.presentation.stream.views.chat.FullChatModView
import com.example.clicker.presentation.stream.views.chat.chatSettings.ChatSettingsColumn
import com.example.clicker.presentation.stream.views.chat.chatSettings.ChatSettingsViewModel
import com.example.clicker.util.Response
import kotlinx.coroutines.launch


/**
 * - **EnhancedModViewMainView**
 * */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EnhancedModViewMainView(
    closeModView:()->Unit,
    twitchUserChat: List<TwitchUserData>, // unstable this is the only one I am going to fix
    streamViewModel: StreamViewModel,// unstable
    modViewViewModel: ModViewViewModel,// unstable
    hideSoftKeyboard:()->Unit,
    modVersionThreeViewModel: ModVersionThreeViewModel,// unstable
    chatSettingsViewModel: ChatSettingsViewModel,
){

    val scope = rememberCoroutineScope()

    val clickedChatterModalState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )


    val unbanRequestModalState= rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val showUnbanRequestBottomModal:()->Unit = remember(unbanRequestModalState) { {
        scope.launch {
            unbanRequestModalState.show()
        }
    } }

    val chatSettingsModalState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    val updateClickedUser:(String,String,Boolean,Boolean)->Unit = remember(streamViewModel) { { username, userId, banned, isMod ->
        streamViewModel.updateClickedChat(
            username,
            userId,
            banned,
            isMod
        )
    } }
    val doubleClickChat:(String)->Unit = remember(streamViewModel) { {
        streamViewModel.sendDoubleTapEmote(it)
    } }

    /******BOX-ONE FUNCTIONS*******/
    val setBoxOneDoubleTap:(newValue:Boolean)->Unit = remember(modVersionThreeViewModel) { { newValue ->
        modVersionThreeViewModel.setBoxOneDoubleTap(newValue)
    } }
    val setBoxOneDragging:(newValue:Boolean)->Unit = remember(modVersionThreeViewModel) { { newValue ->
        modVersionThreeViewModel.setBoxOneZIndex(newValue)
    } }

    /******BOX-Two FUNCTIONS*******/
    val setBoxTwoDoubleTap:(newValue:Boolean)->Unit = remember(modVersionThreeViewModel) { { newValue ->
        modVersionThreeViewModel.setBoxTwoDoubleTap(newValue)
    } }

    val setBoxTwoDragging:(newValue:Boolean)->Unit = remember(modVersionThreeViewModel) { { newValue ->
        modVersionThreeViewModel.setBoxTwoZIndex(newValue)
    } }

    /******BOX-Three FUNCTIONS*******/

    val setBoxThreeDoubleTap:(newValue:Boolean)->Unit = remember(modVersionThreeViewModel) { { newValue ->
        modVersionThreeViewModel.setBoxThreeDoubleTap(newValue)
    } }

    val setBoxThreeDragging:(newValue:Boolean)->Unit = remember(modVersionThreeViewModel) { { newValue ->
        modVersionThreeViewModel.setBoxThreeZIndex(newValue)
    } }

    /******Generic FUNCTIONS*******/


    val setDoubleClickAndDragFalse:()->Unit = remember(modVersionThreeViewModel) { {

        modVersionThreeViewModel.updateDoubleClickAndDrag(false)
    } }

    val manageAutoModMessage:(messageId:String,action:String)->Unit = remember(modVersionThreeViewModel) { {messageId,action->
        modViewViewModel.manageAutoModMessage(messageId,action)

    } }

    val changeActualTextFieldValue:(String, TextRange) -> Unit = remember(streamViewModel) { { text, textRange ->
        streamViewModel.changeActualTextFieldValue(text, textRange)
    } }

    val sendMessageToWebSocket:(String) -> Unit = remember(streamViewModel) { { message ->
        streamViewModel.sendMessage(message)
        streamViewModel.updateMostFrequentEmoteList()
    } }
    val setIndex:(Int)->Unit = remember(modVersionThreeViewModel) { { newValue ->

        modVersionThreeViewModel.setIndex(newValue)
    } }

    val changeAutoModQueueChecked:(Boolean)->Unit = remember(modVersionThreeViewModel) { {newValue->
        modViewViewModel.changeAutoModQueueChecked(newValue)

    } }
    val changeModActionsChecked:(Boolean)->Unit = remember(modVersionThreeViewModel) { {newValue->
        modViewViewModel.changeModActionsChecked(newValue)

    } }



    val updateMostFrequentEmoteList:(EmoteNameUrl)->Unit = remember(streamViewModel) { {
        streamViewModel.updateTemporaryMostFrequentList(it)
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


    val resolveUnbanRequest:(String, UnbanStatusFilter)->Unit = remember(modViewViewModel) { { id, status->
        modViewViewModel.resolveUnbanRequest(id,status)

    } }
    val updateOptionalResolutionText:(String)->Unit = remember(modViewViewModel) { {newText->
        modViewViewModel.updateOptionalResolutionText(newText)

    } }

    val changeSelectedFollowersModeItem:(ListTitleValue)->Unit = remember(modViewViewModel) { { newValue->
        modViewViewModel.changeSelectedFollowersModeItem(newValue)

    } }
    val changeSelectedSlowModeItem:(ListTitleValue)->Unit = remember(modViewViewModel) { { newValue->
        modViewViewModel.changeSelectedSlowModeItem(newValue)

    } }
    val updateSubscriberOnly:(Boolean)->Unit = remember(modViewViewModel) { { newValue->
        modViewViewModel.updateSubscriberOnly(newValue)

    } }
    val sortUnbanRequestList:(String)->Unit = remember(modViewViewModel) { { status->
        modViewViewModel.sortUnbanRequestList(status)

    } }
    val retryGetUnbanRequest:()->Unit = remember(modViewViewModel) { {
        modViewViewModel.retryGetUnbanRequest()

    } }
    val changeUnbanRequestChecked:(Boolean)->Unit = remember(modViewViewModel) { { newValue->
        modViewViewModel.changeUnbanRequestChecked(newValue)

    } }

    ModalBottomSheetLayout(
        sheetState=unbanRequestModalState,
        sheetContent = {
            Box {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(10.dp)){
                    NewContentBanner(
                        clickedUsername = modViewViewModel.clickedUnbanRequestUser.value.userName,
                        clickedMessage = modViewViewModel.clickedUnbanRequestUser.value.message,
                        clickedUserInfo=modViewViewModel.clickedUnbanRequestInfo.value,
                        clickedStatus = modViewViewModel.clickedUnbanRequestUser.value.status,
                    )
                    ClickedUserMessages(
                        globalTwitchEmoteContentMap = chatSettingsViewModel.globalEmoteMap.value,
                        channelTwitchEmoteContentMap = chatSettingsViewModel.inlineContentMapChannelEmoteList.value,
                        globalBetterTTVEmoteContentMap = chatSettingsViewModel.betterTTVGlobalInlineContentMapChannelEmoteList.value,
                        channelBetterTTVEmoteContentMap = chatSettingsViewModel.betterTTVChannelInlineContentMapChannelEmoteList.value,
                        sharedBetterTTVEmoteContentMap = chatSettingsViewModel.betterTTVSharedInlineContentMapChannelEmoteList.value,
                        clickedUsernameChatsWithDateSentImmutable = streamViewModel.clickedUsernameChatsDateSentImmutable.value,
                        resolveUnbanRequest={id,status->
                            resolveUnbanRequest(id,status)
                        },
                        clickedRequestId = modViewViewModel.clickedUnbanRequestUser.value.requestId,
                        clickedStatus = modViewViewModel.clickedUnbanRequestUser.value.status,
                        resolutionText=modViewViewModel.optionalResolutionText.value,
                        updateResolutionText={newText->
                            updateOptionalResolutionText(newText)
                        }
                    )

                }
                val response = modViewViewModel.resolveUnbanRequest.value
                when(response){
                    is Response.Loading ->{
                        Spacer(
                            modifier = Modifier
                                .fillMaxSize()
                                .disableClickAndRipple()
                                .background(
                                    color = Color.Black.copy(alpha = .7f)
                                )
                        )
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is Response.Success ->{
                        if(!response.data){
                            Log.d("unbanrequestModalCheck","it should hide")

                            Spacer(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .disableClickAndRipple()
                                    .background(
                                        color = Color.Black.copy(alpha = .7f)
                                    )
                            )
                            Row(modifier = Modifier.align(Alignment.Center)){
                                Icon(painter = painterResource(id = R.drawable.baseline_check_24), contentDescription = "successful request",tint = Color.Green)
                                Text("Success",color = MaterialTheme.colorScheme.onPrimary)
                                Icon(painter = painterResource(id = R.drawable.baseline_check_24), contentDescription = "successful request",tint = Color.Green)
                            }
                        }

                    }
                    is Response.Failure ->{
                        Spacer(
                            modifier = Modifier
                                .fillMaxSize()
                                .disableClickAndRipple()
                                .background(
                                    color = Color.Black.copy(alpha = .7f)
                                )
                        )
                        Row(modifier = Modifier.align(Alignment.Center)){
                            Icon(painter = painterResource(id = R.drawable.baseline_close_24), contentDescription = "Failed request",tint = Color.Red)
                            Text("Failed",color = MaterialTheme.colorScheme.onPrimary)
                            Icon(painter = painterResource(id = R.drawable.baseline_close_24), contentDescription = "Failed request",tint = Color.Red)
                        }
                    }

                }

            }

        }
    ) {

        ModalBottomSheetLayout(
            sheetState = chatSettingsModalState,
            sheetContent = {
                ChatSettingsColumn(
                    advancedChatSettings = streamViewModel.advancedChatSettingsState.value,
                    changeAdvancedChatSettings = { newValue ->
                        streamViewModel.updateAdvancedChatSettings(
                            newValue
                        )
                    },
                    changeNoChatMode = { newValue -> streamViewModel.setNoChatMode(newValue) },

                    chatSettingsEnabled = streamViewModel.state.value.loggedInUserData?.mod
                        ?: false,

                    followerModeListImmutable = followerModeListImmutable,
                    slowModeListImmutable = slowModeListImmutable,

                    selectedFollowersModeItem = modViewViewModel.uiState.value.selectedFollowerMode,
                    changeSelectedFollowersModeItem = { newValue ->
                        changeSelectedFollowersModeItem(
                            newValue
                        )
                    },

                    selectedSlowModeItem = modViewViewModel.uiState.value.selectedSlowMode,
                    changeSelectedSlowModeItem = { newValue ->

                        changeSelectedSlowModeItem(
                            newValue
                        )
                    },
                    emoteOnly = modViewViewModel.uiState.value.emoteOnly,
                    setEmoteOnly = { newValue -> modViewViewModel.updateEmoteOnly(newValue) },
                    subscriberOnly = modViewViewModel.uiState.value.subscriberOnly,
                    setSubscriberOnly = { newValue ->
                        updateSubscriberOnly(
                            newValue
                        )
                    },

                    badgeSize = chatSettingsViewModel.badgeSize.value,
                    changeBadgeSize = { newValue -> changeBadgeSize(newValue) },
                    emoteSize = chatSettingsViewModel.emoteSize.value,
                    changeEmoteSize = { newValue -> changeEmoteSize(newValue) },
                    usernameSize = chatSettingsViewModel.usernameSize.value,
                    changeUsernameSize = { newValue -> changeUsernameSize(newValue) },
                    messageSize = chatSettingsViewModel.messageSize.value,
                    changeMessageSize = { newValue -> changeMessageSize(newValue) },
                    lineHeight = chatSettingsViewModel.lineHeight.value,
                    changeLineHeight = { newValue -> changeLineHeight(newValue) },
                    customUsernameColor = chatSettingsViewModel.customUsernameColor.value,
                    changeCustomUsernameColor = { newValue -> changeCustomUsernameColor(newValue) },

                    )
            }
        ) {

            ModalBottomSheetLayout(
                sheetBackgroundColor = MaterialTheme.colorScheme.primary,
                sheetState = clickedChatterModalState,
                sheetContent = {

                    TestingNewBottomModal(
                        clickedUsername = streamViewModel.clickedUIState.value.clickedUsername,
                        textFieldValue = streamViewModel.textFieldValue,
                        closeBottomModal = {},
                        banned = streamViewModel.clickedUIState.value.clickedUsernameBanned,
                        unbanUser = {
                            streamViewModel.unBanUser()
                        },
                        isMod = streamViewModel.state.value.loggedInUserData?.mod ?: false,
                        openTimeoutDialog = { streamViewModel.openTimeoutDialog.value = true },
                        openBanDialog = { streamViewModel.openBanDialog.value = true },
                        openWarnDialog = { streamViewModel.changeOpenWarningDialog(true) },
                        clickedUsernameChatsDateSentImmutable = streamViewModel.clickedUsernameChatsDateSentImmutable.value,
                        badgeInlineContentMap = chatSettingsViewModel.globalChatBadgesMap.value,
                        clickedUserBadgeList = streamViewModel.clickedUserBadgesImmutable.value,
                        globalTwitchEmoteContentMap = chatSettingsViewModel.globalEmoteMap.value,
                        channelTwitchEmoteContentMap = chatSettingsViewModel.inlineContentMapChannelEmoteList.value,
                        globalBetterTTVEmoteContentMap = chatSettingsViewModel.betterTTVGlobalInlineContentMapChannelEmoteList.value,
                        channelBetterTTVEmoteContentMap = chatSettingsViewModel.betterTTVChannelInlineContentMapChannelEmoteList.value,
                        sharedBetterTTVEmoteContentMap = chatSettingsViewModel.betterTTVSharedInlineContentMapChannelEmoteList.value,
                    )
                }
            ) {
                ModVersionThree(
                    boxOneOffsetY = modVersionThreeViewModel.boxOneOffsetY,
                    setBoxOneOffset = { newValue ->
                        modVersionThreeViewModel.setBoxOneOffset(
                            newValue
                        )
                    },
                    boxOneDragState = modVersionThreeViewModel.boxOneDragState,
                    boxOneSection = modVersionThreeViewModel.boxOneSection,
                    boxOneIndex = modVersionThreeViewModel.boxOneIndex,
                    boxOneDragging = modVersionThreeViewModel.boxesZIndex.value.boxOneIndex,
                    setBoxOneDragging = { newValue ->
                        Log.d("LoggingTheDragging", "ONE")
                        setBoxOneDragging(newValue)
                    },
                    setBoxOneIndex = { newValue ->
                        modVersionThreeViewModel.syncBoxOneIndex(
                            newValue
                        )
                    },
                    deleteBoxOne = modVersionThreeViewModel.deleteBoxOne,
                    boxOneHeight = modVersionThreeViewModel.boxOneHeight,
                    boxOneDoubleTap = modVersionThreeViewModel.doubleTap.value.boxOneDoubleTap,
                    setBoxOneDoubleTap = { newValue ->
                        setBoxOneDoubleTap(newValue)
                    },

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/

                    /*************** BOX TWO PARAMETERS***************************************************************/
                    boxTwoOffsetY = modVersionThreeViewModel.boxTwoOffsetY,
                    setBoxTwoOffset = { newValue ->
                        modVersionThreeViewModel.setBoxTwoOffset(
                            newValue
                        )
                    },
                    boxTwoDragState = modVersionThreeViewModel.boxTwoDragState,
                    boxTwoSection = modVersionThreeViewModel.boxTwoSection,
                    boxTwoIndex = modVersionThreeViewModel.boxTwoIndex,
                    boxTwoDragging = modVersionThreeViewModel.boxesZIndex.value.boxTwoIndex,
                    setBoxTwoDragging = { newValue -> setBoxTwoDragging(newValue) },
                    setBoxTwoIndex = { newValue ->
                        Log.d("LoggingTheDragging", "TWO")
                        modVersionThreeViewModel.syncBoxTwoIndex(newValue)
                    },
                    deleteBoxTwo = modVersionThreeViewModel.deleteBoxTwo,
                    boxTwoHeight = modVersionThreeViewModel.boxTwoHeight,
                    boxTwoDoubleTap = modVersionThreeViewModel.doubleTap.value.boxTwoDoubleTap,
                    setBoxTwoDoubleTap = { newValue ->
                        setBoxTwoDoubleTap(newValue)
                    },

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/

                    /*************** BOX THREE PARAMETERS*****************************************************************/
                    boxThreeOffsetY = modVersionThreeViewModel.boxThreeOffsetY,
                    setBoxThreeOffset = { newValue ->
                        modVersionThreeViewModel.setBoxThreeOffset(
                            newValue
                        )
                    },
                    boxThreeDragState = modVersionThreeViewModel.boxThreeDragState,
                    boxThreeSection = modVersionThreeViewModel.boxThreeSection,
                    boxThreeIndex = modVersionThreeViewModel.boxThreeIndex,
                    boxThreeDragging = modVersionThreeViewModel.boxesZIndex.value.boxThreeIndex,
                    setBoxThreeDragging = { newValue ->
                        setBoxThreeDragging(newValue)
                    },
                    setBoxThreeIndex = { newValue ->
                        Log.d("LoggingTheDragging", "THREE")
                        modVersionThreeViewModel.syncBoxThreeIndex(newValue)
                    },
                    deleteBoxThree = modVersionThreeViewModel.deleteBoxThree,
                    boxThreeHeight = modVersionThreeViewModel.boxThreeHeight,
                    boxThreeDoubleTap = modVersionThreeViewModel.doubleTap.value.boxThreeDoubleTap,
                    setBoxThreeDoubleTap = { newValue ->
                        setBoxThreeDoubleTap(newValue)

                    },


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/


                    /*************** GENERICS PARAMETERS*****************************************************************/
                    updateIndex = { newValue ->
                        setIndex(newValue) //todo: wrap this in a remember function
                    },
                    showError = modVersionThreeViewModel.showPlacementError.value,
                    sectionTwoHeight = modVersionThreeViewModel.section2height,
                    sectionThreeHeight = modVersionThreeViewModel.section3Height,
                    closeModView = { closeModView() },
                    fullChatMode = modVersionThreeViewModel.fullChat.value,
                    deleteOffset = modVersionThreeViewModel.deleteOffset,

                    smallChat = { setDragging ->
                        SmallChat(
                            twitchUserChat = twitchUserChat,
                            showBottomModal = {
                                scope.launch { clickedChatterModalState.show() }
                            },
                            updateClickedUser = { username, userId, banned, isMod ->
                                updateClickedUser( //todo: I need to add this
                                    username,
                                    userId,
                                    banned,
                                    isMod
                                )
                            },
                            showTimeoutDialog = {
                                streamViewModel.openTimeoutDialog.value = true
                            },
                            showBanDialog = { streamViewModel.openBanDialog.value = true },
                            doubleClickMessage = { username ->
                                doubleClickChat(username)
                            },
                            deleteChatMessage = { messageId ->
                                streamViewModel.deleteChatMessage(
                                    messageId
                                )
                            },
                            //todo:change back to --> true for testing
                            isMod = streamViewModel.state.value.loggedInUserData?.mod ?: false,
                            setDragging = { value ->
                                Log.d("DOUBLECLICKDRAGGING", "click from the outside")
                                setDragging()
                            },
                            doubleClickAndDrag = modVersionThreeViewModel.doubleClickAndDrag.value,
                            setDoubleClickAndDragFalse = {
                                setDoubleClickAndDragFalse()
                            },
                            badgeListMap = streamViewModel.badgeListMap.value,
                            usernameSize = chatSettingsViewModel.usernameSize.value,
                            messageSize = chatSettingsViewModel.messageSize.value,
                            lineHeight = chatSettingsViewModel.lineHeight.value,
                            useCustomUsernameColors = chatSettingsViewModel.customUsernameColor.value,
                            globalTwitchEmoteContentMap = chatSettingsViewModel.globalEmoteMap.value,
                            channelTwitchEmoteContentMap = chatSettingsViewModel.inlineContentMapChannelEmoteList.value,
                            globalBetterTTVEmoteContentMap = chatSettingsViewModel.betterTTVGlobalInlineContentMapChannelEmoteList.value,
                            channelBetterTTVEmoteContentMap = chatSettingsViewModel.betterTTVChannelInlineContentMapChannelEmoteList.value,
                            sharedBetterTTVEmoteContentMap = chatSettingsViewModel.betterTTVSharedInlineContentMapChannelEmoteList.value,
                        )
                    },
                    fullChat = { setDragging ->
                        FullChatModView(
                            twitchUserChat = twitchUserChat,
                            showBottomModal = {
                                scope.launch { clickedChatterModalState.show() }
                            },
                            updateClickedUser = { username, userId, banned, isMod ->
                                updateClickedUser(
                                    username,
                                    userId,
                                    banned,
                                    isMod
                                )
                            },
                            showTimeoutDialog = {
                                streamViewModel.openTimeoutDialog.value = true
                            },
                            showBanDialog = { streamViewModel.openBanDialog.value = true },
                            doubleClickMessage = { username ->
                                doubleClickChat(username)

                            },

                            showOuterBottomModalState = {
                                Log.d("BottomModalClicked", "showOuterBottomModalState Clicked")
                                //seems fine that it is empy
//                    scope.launch {
//
//                    }
                            },
                            newFilterMethod = { newTextValue ->
                                streamViewModel.newParsingAgain(
                                    newTextValue
                                )
                            },

                            orientationIsVertical = true,

                            //todo:change back to --> streamViewModel.state.value.loggedInUserData?.mod ?: false
                            isMod = true,
                            clickedAutoCompleteText = { username ->
                                streamViewModel.autoTextChange(username)
                            },
                            showModal = {
                                //todo: This is what is clicked when I want to launch the bottom modal
                                scope.launch {
                                    chatSettingsModalState.show()
                                    // clickedChatterModalState.show()
                                }
                            },
                            notificationAmount = 0,
                            textFieldValue = streamViewModel.textFieldValue,
                            sendMessageToWebSocket = { message ->
                                sendMessageToWebSocket(message)
                            },
                            noChat = streamViewModel.advancedChatSettingsState.value.noChatMode,
                            deleteChatMessage = { messageId ->
                                streamViewModel.deleteChatMessage(
                                    messageId
                                )
                            },
                            clickedCommandAutoCompleteText = { clickedValue ->
                                streamViewModel.clickedCommandAutoCompleteText(
                                    clickedValue
                                )
                            },
                            hideSoftKeyboard = {
                                hideSoftKeyboard()

                            },
                            emoteBoardGlobalList = streamViewModel.globalEmoteUrlList.value,
                            updateTextWithEmote = { newValue ->
                                streamViewModel.addEmoteToText(
                                    newValue
                                )
                            },
                            emoteBoardChannelList = streamViewModel.channelEmoteUrlList.value,
                            deleteEmote = { streamViewModel.deleteEmote() },
                            showModView = {
                                closeModView()
                            },
                            fullMode = modVersionThreeViewModel.fullChat.value,
                            setDragging = {
                                Log.d("doubleClickingThings", "CLICKED")
                                setDragging()
                            },
                            emoteBoardMostFrequentList = streamViewModel.mostFrequentEmoteListTesting.value,
                            updateMostFrequentEmoteList = { value ->
                                updateMostFrequentEmoteList(value)
                            },
                            globalBetterTTVEmotes = streamViewModel.globalBetterTTVEmotes.value,
                            channelBetterTTVResponse = streamViewModel.channelBetterTTVEmote.value,
                            sharedBetterTTVResponse = streamViewModel.sharedChannelBetterTTVEmote.value,
                            userIsSub = streamViewModel.state.value.loggedInUserData?.sub ?: false,
                            forwardSlashes = streamViewModel.forwardSlashCommandImmutable.value,
                            filteredChatListImmutable = streamViewModel.filteredChatListImmutable.value,
                            actualTextFieldValue = streamViewModel.textFieldValue.value,
                            changeActualTextFieldValue = { text, textRange ->
                                changeActualTextFieldValue(text, textRange)
                            },
                            badgeListMap = streamViewModel.badgeListMap.value,
                            usernameSize = chatSettingsViewModel.usernameSize.value,
                            messageSize = chatSettingsViewModel.messageSize.value,
                            lineHeight = chatSettingsViewModel.lineHeight.value,
                            useCustomUsernameColors = chatSettingsViewModel.customUsernameColor.value,
                            globalTwitchEmoteContentMap = chatSettingsViewModel.globalEmoteMap.value,
                            channelTwitchEmoteContentMap = chatSettingsViewModel.inlineContentMapChannelEmoteList.value,
                            globalBetterTTVEmoteContentMap = chatSettingsViewModel.betterTTVGlobalInlineContentMapChannelEmoteList.value,
                            channelBetterTTVEmoteContentMap = chatSettingsViewModel.betterTTVChannelInlineContentMapChannelEmoteList.value,
                            sharedBetterTTVEmoteContentMap = chatSettingsViewModel.betterTTVSharedInlineContentMapChannelEmoteList.value,
                            lowPowerMode = streamViewModel.lowPowerModeActive.value,
                            channelName = streamViewModel.channelName.value ?: "",
                            showChannelInformationModal = {  }

                        )

                    },
                    modActionsList = modViewViewModel.modActionsList,
                    modActionStatus = modViewViewModel.modViewStatus.value.modActions,
                    autoModMessageList = modViewViewModel.autoModMessageList,
                    autoModStatus = modViewViewModel.modViewStatus.value.autoModMessageStatus,
                    manageAutoModMessage = { messageId, action ->
                        manageAutoModMessage(messageId, action)
                    },
                    changeAutoModQueueChecked = { value ->
                        changeAutoModQueueChecked(value)    //todo: wrap this in a remember function
                    },
                    changeModActionsChecked = { value ->
                        changeModActionsChecked(value) //todo: wrap this in a remember function
                    },
                    autoModQueueChecked = modViewViewModel.uiState.value.autoModMessagesNotifications,
                    modActionsChecked = modViewViewModel.uiState.value.modActionNotifications,
                    doubleClickAndDrag = modVersionThreeViewModel.doubleClickAndDrag.value,
                    setDoubleClickAndDragFalse = {
                        setDoubleClickAndDragFalse()
                    },
                    autoModMessageListImmutableCollection = modViewViewModel.autoModMessageListImmutable.value,
                    modActionListImmutableCollection = modViewViewModel.modActionListImmutable.value,
                    unbanRequestResponse = modViewViewModel.unbanRequestResponse.value,
                    showUnbanRequestModal={showUnbanRequestBottomModal()},
                    updateClickedUnbanRequest={ username,text,userId,requestId,status ->
                        updateClickedUser(username,userId,false,false)
                        modViewViewModel.updateClickedUnbanRequestUser(username,text,userId,requestId,status)
                    },
                    immutableUnbanRequestList = modViewViewModel.getUnbanRequestList.value,
                    sortUnbanRequest={status ->
                        sortUnbanRequestList(status)
                    },
                    retryUnbanRequests={
                        retryGetUnbanRequest()
                    },
                    unbanRequestChecked = modViewViewModel.uiState.value.unbanRequestNotifications,
                    changeUnbanRequestChecked = {value ->
                        //todo: WRAP THIS----------------------------------------
                        changeUnbanRequestChecked(value)
                    }



                )
            }
        }

    }

}