package com.example.clicker.presentation.modChannels.modVersionThree

import android.content.res.Resources
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.clicker.R
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.network.repository.EmoteListMap
import com.example.clicker.network.repository.util.AutoModQueueMessage
import com.example.clicker.presentation.modView.ModActionData
import com.example.clicker.presentation.modView.ModViewDragStateViewModel
import com.example.clicker.presentation.modView.ModViewViewModel
import com.example.clicker.presentation.stream.StreamViewModel
import com.example.clicker.presentation.stream.views.BottomModal
import com.example.clicker.presentation.stream.views.chat.ChatSettingsColumn
import com.example.clicker.presentation.stream.views.chat.DualIconsButton
import com.example.clicker.presentation.stream.views.chat.FullChatModView
import com.example.clicker.presentation.stream.views.chat.ImprovedChatUI
import com.example.clicker.presentation.stream.views.chat.isScrolledToEnd
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import com.example.clicker.presentation.modView.followerModeList
import com.example.clicker.presentation.modView.slowModeList
import com.example.clicker.presentation.modView.views.AutoModBoxHorizontalDragBox
import com.example.clicker.presentation.modView.views.ConnectionErrorResponse
import com.example.clicker.presentation.modView.views.ErrorMessage403
import com.example.clicker.presentation.modView.views.FailedClickToTryAgainBox
import com.example.clicker.presentation.modView.views.LoadingIndicator
import com.example.clicker.presentation.modView.views.ModActionNotificationMessage
import com.example.clicker.presentation.modView.views.ScrollToBottomModView
import com.example.clicker.util.Response
import com.example.clicker.util.WebSocketResponse
import androidx.window.layout.WindowMetrics

enum class Sections {
    ONE, TWO, THREE
}

//todo: this need to go inside of the Fragment, where the old modView is
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModViewComponentVersionThree(
    closeModView:()->Unit,
    modViewDragStateViewModel: ModViewDragStateViewModel,
    inlineContentMap: EmoteListMap,
    twitchUserChat: List<TwitchUserData>,
    streamViewModel: StreamViewModel,
    modViewViewModel: ModViewViewModel,
    hideSoftKeyboard:()->Unit,
    modVersionThreeViewModel:ModVersionThreeViewModel
){

    val clickedChatterModalState = androidx.compose.material.rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val clickedUsernameChatsWithDateSent = streamViewModel.clickedUsernameChatsWithDateSent.toList()
    val chatSettingsModalState = androidx.compose.material.rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val scope = rememberCoroutineScope()
    val updateClickedUser:(String,String,Boolean,Boolean)->Unit = remember(streamViewModel) { { username, userId, banned, isMod ->
        streamViewModel.updateClickedChat(
            username,
            userId,
            banned,
            isMod
        )
    } }
    val doubleClickChat:(String)->Unit =remember(streamViewModel) { {
        streamViewModel.sendDoubleTapEmote(it)
    } }

    ModalBottomSheetLayout(
        sheetState = chatSettingsModalState,
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
        sheetState = clickedChatterModalState,
        sheetContent = {
            BottomModal.BottomModalBuilder(
                clickedUsernameChats = streamViewModel.clickedUsernameChats.toList(),
                clickedUsername = streamViewModel.clickedUIState.value.clickedUsername,
                bottomModalState = clickedChatterModalState,
                textFieldValue = streamViewModel.textFieldValue,
                closeBottomModal = {},
                banned = streamViewModel.clickedUIState.value.clickedUsernameBanned,
                unbanUser = {
                    //  streamViewModel.unBanUser()
                },
                //todo: turn this back into --> streamViewModel.state.value.loggedInUserData?.mod ?: false
                isMod = streamViewModel.state.value.loggedInUserData?.mod ?: false,
                openTimeoutDialog = { streamViewModel.openTimeoutDialog.value = true },
                openBanDialog = { streamViewModel.openBanDialog.value = true },
                shouldMonitorUser = streamViewModel.shouldMonitorUser.value,
                updateShouldMonitorUser = {},
                clickedUsernameChatsWithDate = clickedUsernameChatsWithDateSent,
                openWarnDialog={streamViewModel.changeOpenWarningDialog(true)}
            )
        }
    ) {
        ModVersionThree(
            boxOneOffsetY = modVersionThreeViewModel.boxOneOffsetY,
            setBoxOneOffset = { newValue -> modVersionThreeViewModel.setBoxOneOffset(newValue) },
            boxOneDragState = modVersionThreeViewModel.boxOneDragState,
            boxOneSection = modVersionThreeViewModel.boxOneSection,
            boxOneIndex = modVersionThreeViewModel.boxOneIndex,
            boxOneDragging = modVersionThreeViewModel.boxesZIndex.value.boxOneIndex,
            setBoxOneDragging = { newValue ->
                Log.d("LoggingTheDragging", "ONE")
                modVersionThreeViewModel.setBoxOneZIndex(newValue)
            },
            setBoxOneIndex = { newValue -> modVersionThreeViewModel.syncBoxOneIndex(newValue) },
            deleteBoxOne = modVersionThreeViewModel.deleteBoxOne,
            boxOneHeight = modVersionThreeViewModel.boxOneHeight,
            boxOneDoubleTap = modVersionThreeViewModel.doubleTap.value.boxOneDoubleTap,
            setBoxOneDoubleTap = { newValue -> modVersionThreeViewModel.setBoxOneDoubleTap(newValue) },

            /*************** BOX TWO PARAMETERS***************************************************************/
            boxTwoOffsetY = modVersionThreeViewModel.boxTwoOffsetY,
            setBoxTwoOffset = { newValue -> modVersionThreeViewModel.setBoxTwoOffset(newValue) },
            boxTwoDragState = modVersionThreeViewModel.boxTwoDragState,
            boxTwoSection = modVersionThreeViewModel.boxTwoSection,
            boxTwoIndex = modVersionThreeViewModel.boxTwoIndex,
            boxTwoDragging = modVersionThreeViewModel.boxesZIndex.value.boxTwoIndex,
            setBoxTwoDragging = { newValue -> modVersionThreeViewModel.setBoxTwoZIndex(newValue) },
            setBoxTwoIndex = { newValue ->
                Log.d("LoggingTheDragging", "TWO")
                modVersionThreeViewModel.syncBoxTwoIndex(newValue)
            },
            deleteBoxTwo = modVersionThreeViewModel.deleteBoxTwo,
            boxTwoHeight = modVersionThreeViewModel.boxTwoHeight,
            boxTwoDoubleTap = modVersionThreeViewModel.doubleTap.value.boxTwoDoubleTap,
            setBoxTwoDoubleTap = { newValue -> modVersionThreeViewModel.setBoxTwoDoubleTap(newValue) },

            /*************** BOX THREE PARAMETERS*****************************************************************/
            boxThreeOffsetY = modVersionThreeViewModel.boxThreeOffsetY,
            setBoxThreeOffset = { newValue -> modVersionThreeViewModel.setBoxThreeOffset(newValue) },
            boxThreeDragState = modVersionThreeViewModel.boxThreeDragState,
            boxThreeSection = modVersionThreeViewModel.boxThreeSection,
            boxThreeIndex = modVersionThreeViewModel.boxThreeIndex,
            boxThreeDragging = modVersionThreeViewModel.boxesZIndex.value.boxThreeIndex,
            setBoxThreeDragging = { newValue ->
                modVersionThreeViewModel.setBoxThreeZIndex(
                    newValue
                )
            },
            setBoxThreeIndex = { newValue ->
                Log.d("LoggingTheDragging", "THREE")
                modVersionThreeViewModel.syncBoxThreeIndex(newValue)
            },
            deleteBoxThree = modVersionThreeViewModel.deleteBoxThree,
            boxThreeHeight = modVersionThreeViewModel.boxThreeHeight,
            boxThreeDoubleTap = modVersionThreeViewModel.doubleTap.value.boxThreeDoubleTap,
            setBoxThreeDoubleTap = { newValue ->
                modVersionThreeViewModel.setBoxThreeDoubleTap(
                    newValue
                )
            },


            /*************** GENERICS PARAMETERS*****************************************************************/
            updateIndex = { newValue -> modVersionThreeViewModel.setIndex(newValue) },
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
                    deleteChatMessage = { messageId -> streamViewModel.deleteChatMessage(messageId) },
                    //todo:change back to --> true for testing
                    isMod = streamViewModel.state.value.loggedInUserData?.mod ?: false,
                    inlineContentMap = inlineContentMap,
                    setDragging = { value ->
                        Log.d("DOUBLECLICKDRAGGING", "click from the outside")
                        setDragging()
                    },
                    doubleClickAndDrag= modVersionThreeViewModel.doubleClickAndDrag.value,
                    setDoubleClickAndDragFalse={modVersionThreeViewModel.updateDoubleClickAndDrag(false)}
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
                    newFilterMethod = { newTextValue -> streamViewModel.newParsingAgain(newTextValue) },

                    orientationIsVertical = true,

                    //todo:change back to --> streamViewModel.state.value.loggedInUserData?.mod ?: false
                    isMod = true,
                    filteredChatList = streamViewModel.filteredChatList,
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
                    sendMessageToWebSocket = { string ->
                        streamViewModel.sendMessage(string)
                    },
                    noChat = streamViewModel.advancedChatSettingsState.value.noChatMode,
                    deleteChatMessage = { messageId -> streamViewModel.deleteChatMessage(messageId) },
                    forwardSlashCommands = streamViewModel.forwardSlashCommands,
                    clickedCommandAutoCompleteText = { clickedValue ->
                        streamViewModel.clickedCommandAutoCompleteText(
                            clickedValue
                        )
                    },
                    inlineContentMap = streamViewModel.inlineTextContentTest.value,
                    hideSoftKeyboard = {
                        hideSoftKeyboard()

                    },
                    emoteBoardGlobalList = streamViewModel.globalEmoteUrlList.value,
                    updateTextWithEmote = { newValue -> streamViewModel.addEmoteToText(newValue) },
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
                        // updateMostFrequentEmoteList(value)
                    },
                    globalBetterTTVEmotes = streamViewModel.globalBetterTTVEmotes.value,
                    channelBetterTTVResponse = streamViewModel.channelBetterTTVEmote.value,
                    sharedBetterTTVResponse = streamViewModel.sharedChannelBetterTTVEmote.value,
                    userIsSub = streamViewModel.state.value.loggedInUserData?.sub ?: false
                )

            },
            modActionsList = modViewViewModel.modActionsList,
            modActionStatus = modViewViewModel.modViewStatus.value.modActions,
            autoModMessageList = modViewViewModel.autoModMessageList,
            autoModStatus = modViewViewModel.modViewStatus.value.autoModMessageStatus,
            manageAutoModMessage ={
                    messageId,action -> modViewViewModel.manageAutoModMessage(messageId,action)
            },
            changeAutoModQueueChecked ={value ->modViewViewModel.changeAutoModQueueChecked(value)},
            changeModActionsChecked ={value ->modViewViewModel.changeModActionsChecked(value)},
            autoModQueueChecked = modViewViewModel.uiState.value.autoModMessagesNotifications,
            modActionsChecked=modViewViewModel.uiState.value.modActionNotifications,
            doubleClickAndDrag= modVersionThreeViewModel.doubleClickAndDrag.value,
            setDoubleClickAndDragFalse={modVersionThreeViewModel.updateDoubleClickAndDrag(false)}

        )
    }
    }


}

@Composable
fun ModVersionThree(
    boxOneOffsetY: Float,
    setBoxOneOffset:(Float) ->Unit,
    boxOneDragState: DraggableState,
    boxOneSection: Sections,
    boxOneIndex:Int,
    boxOneDragging: Boolean,
    setBoxOneDragging: (Boolean) -> Unit,
    setBoxOneIndex:(Int)->Unit,
    deleteBoxOne:Boolean,
    boxOneHeight:Dp,
    boxOneDoubleTap:Boolean,
    setBoxOneDoubleTap: (Boolean) -> Unit,

/*************** BOX TWO PARAMETERS***********************************/
    boxTwoOffsetY: Float,
    setBoxTwoOffset:(Float) ->Unit,
    boxTwoDragState: DraggableState,
    boxTwoSection: Sections,
    boxTwoIndex:Int,
    boxTwoDragging: Boolean,
    setBoxTwoDragging: (Boolean) -> Unit,
    setBoxTwoIndex:(Int)->Unit,
    deleteBoxTwo:Boolean,
    boxTwoHeight:Dp,
    boxTwoDoubleTap:Boolean,
    setBoxTwoDoubleTap: (Boolean) -> Unit,

    /*************** BOX THREE PARAMETERS***********************************/
    boxThreeOffsetY: Float,
    setBoxThreeOffset:(Float) ->Unit,
    boxThreeDragState: DraggableState,
    boxThreeSection: Sections,
    boxThreeIndex:Int,
    boxThreeDragging: Boolean,
    setBoxThreeDragging: (Boolean) -> Unit,
    setBoxThreeIndex:(Int)->Unit,
    deleteBoxThree:Boolean,
    boxThreeHeight:Dp,
    boxThreeDoubleTap:Boolean,
    setBoxThreeDoubleTap: (Boolean) -> Unit,

    /***************** GENERIC PARAMETERS *****************************************/
    updateIndex:(Int)->Unit,
    showError: Boolean,
    sectionTwoHeight:Float,
    sectionThreeHeight:Float,
    closeModView: () -> Unit,
    fullChatMode:Boolean,
    deleteOffset: Float,
    smallChat: @Composable (setDraggingTrue: () -> Unit) -> Unit,
    fullChat: @Composable (setDraggingTrue: () -> Unit) -> Unit,

    modActionStatus: WebSocketResponse<Boolean>,
    modActionsList: List<ModActionData>,
    autoModMessageList:List<AutoModQueueMessage>,
    autoModStatus: WebSocketResponse<Boolean>,
    manageAutoModMessage:(String,String)-> Unit,

    changeAutoModQueueChecked: (Boolean) -> Unit,
    changeModActionsChecked: (Boolean) -> Unit,
    modActionsChecked: Boolean,
    autoModQueueChecked: Boolean,

    doubleClickAndDrag:Boolean,
    setDoubleClickAndDragFalse:()->Unit




) {
    //TODO: TAKE ALL OF THIS CODE AND MOVE IT TO A VIEWMODEL



    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()


    ModalNavigationDrawer(
        drawerState = drawerState,

        drawerContent = {
            ModalDrawerSheet {

                ModViewDrawerContent(
                    checkIndexAvailability ={newValue ->updateIndex(newValue)},
                    showError = showError,
                    autoModQueueChecked = autoModQueueChecked,
                    modActionsChecked =modActionsChecked,
                    changeAutoModQueueChecked={newValue ->changeAutoModQueueChecked(newValue)},
                    changeModActionsChecked={newValue ->changeModActionsChecked(newValue)}
                )
            }


        },
    ) {

    Scaffold(
        topBar = {
            CustomTopBar(
                showDrawerFunc = {
                    scope.launch { drawerState.open() }
                },
                closeModView ={closeModView()}
            )
        },
        bottomBar = {},
        floatingActionButton = {}
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.Black)
        ) {
            /**************************BOX ONE **************************/
            DragBox(
                boxHeight = boxOneHeight,
                boxOffset = boxOneOffsetY,
                dragState = boxOneDragState,
                modifier = Modifier.zIndex(if (boxOneDragging) 2f else 1f),
                onDragStoppedFuc = {
                    Log.d("BoxOneOffsetLogging", "section -->${boxOneSection}")
                    setBoxOneDragging(false)
                    setBoxOneDoubleTap(false)
                    if (deleteBoxOne) {
                        setBoxOneIndex(0)
                    }
                    when (boxOneSection) {
                        Sections.ONE -> {
                            setBoxOneOffset(0f)
                        }

                        Sections.TWO -> {
                            setBoxOneOffset(sectionTwoHeight)
                        }

                        Sections.THREE -> {
                            setBoxOneOffset(sectionThreeHeight)
                        }
                    }
                },

            ) {
                ContentDragBox(
                    boxOneIndex,
                    fullChatMode =fullChatMode,
                    smallChat={
                        smallChat(
                            setDraggingTrue={
                                Log.d("DOUBLECLICKDRAGGING","THIS BEING SHOWN MEANS THAT IT IS WORKING")
                                setBoxOneDragging(true)
                                setBoxOneDoubleTap(true)
                            }
                        )
                    },
                    fullChat={
                        fullChat(
                            setDraggingTrue={
                                Log.d("DOUBLECLICKDRAGGING","Full chat boxOne working")
                                setBoxOneDragging(true)
                                setBoxOneDoubleTap(true)
                            }
                        )
                    },
                    modActions={
                        NewModActions(
                            setDragging={newValue ->
                                setBoxOneDoubleTap(newValue)
                                setBoxOneDragging(true)
                                        },
                            modActionStatus =modActionStatus,
                            modActionsList=modActionsList,
                            doubleClickAndDrag =doubleClickAndDrag,
                            setDoubleClickAndDragFalse={setDoubleClickAndDragFalse()}
                        )
                    },
                    autoModQueue = {
                        NewAutoModQueueBox(
                            setDragging={newValue ->
                                setBoxOneDoubleTap(newValue)
                                setBoxOneDragging(true)
                                        },
                            autoModMessageList = autoModMessageList,
                            manageAutoModMessage ={messageId,action ->manageAutoModMessage(messageId,action)},
                            connectionError =Response.Success(true),
                            reconnect = {},
                            autoModStatus=autoModStatus,
                            doubleClickAndDrag =doubleClickAndDrag,
                            setDoubleClickAndDragFalse={setDoubleClickAndDragFalse()}
                        )
                    }
                )
                if(boxOneDoubleTap){
                    DetectDoubleClickSpacer(
                        setDragging={ setBoxOneDoubleTap(false)}
                    )
                }

            }

            /**************************BOX TWO **************************/
            DragBox(
                boxHeight = boxTwoHeight,
                boxOffset = boxTwoOffsetY,

                dragState = boxTwoDragState,
                modifier = Modifier.zIndex(if (boxTwoDragging) 2f else 1f),
                onDragStoppedFuc = {
                    setBoxTwoDragging(false)
                    setBoxTwoDoubleTap(false)
                    if (deleteBoxTwo) {
                        setBoxTwoIndex(0)
                    }
                    when (boxTwoSection) {

                        //todo: change these two box one
                        Sections.ONE -> {
                            setBoxTwoOffset(0f)
                        }

                        Sections.TWO -> {
                            setBoxTwoOffset(sectionTwoHeight)
                        }

                        Sections.THREE -> {
                            setBoxTwoOffset(sectionThreeHeight)
                        }

                    }
                },

            ) {
                ContentDragBox(
                    boxTwoIndex,
                    fullChatMode =fullChatMode,
                    smallChat={
                        smallChat(
                            setDraggingTrue={
                                setBoxTwoDragging(true)
                                setBoxTwoDoubleTap(true)
                            }
                        )
                              },
                    fullChat={
                        fullChat(
                            setDraggingTrue={
                                Log.d("DOUBLECLICKDRAGGING","Full chat boxTwo working")
                                setBoxTwoDragging(true)
                                setBoxTwoDoubleTap(true)
                            }
                        )
                    },
                    modActions={
                        NewModActions(
                            setDragging={newValue ->
                                setBoxTwoDoubleTap(newValue)
                                setBoxTwoDragging(true)
                                        },
                            modActionStatus =modActionStatus,
                            modActionsList=modActionsList,
                            doubleClickAndDrag =doubleClickAndDrag,
                            setDoubleClickAndDragFalse={setDoubleClickAndDragFalse()}
                        )
                    },
                    autoModQueue = {
                        NewAutoModQueueBox(
                            setDragging={newValue ->
                                setBoxTwoDoubleTap(newValue)
                                setBoxTwoDragging(true)
                                        },
                            autoModMessageList = autoModMessageList,
                            manageAutoModMessage ={messageId,action ->manageAutoModMessage(messageId,action)},
                            connectionError =Response.Success(true),
                            reconnect = {},
                            autoModStatus=autoModStatus,
                            doubleClickAndDrag =doubleClickAndDrag,
                            setDoubleClickAndDragFalse={setDoubleClickAndDragFalse()}
                        )
                    }

                )
                if(boxTwoDoubleTap){
                    DetectDoubleClickSpacer(
                        setDragging={ setBoxTwoDoubleTap(false)}
                    )
                }



            }
            /**************************BOX THREE **************************/
            DragBox(
                boxHeight = boxThreeHeight,
                boxOffset = boxThreeOffsetY,
                dragState = boxThreeDragState,
                modifier = Modifier.zIndex(if (boxThreeDragging) 2f else 1f),
                onDragStoppedFuc = {
                    setBoxThreeDragging(false)
                    setBoxThreeDoubleTap(false)
                    if (deleteBoxThree) {
                        setBoxThreeIndex(0)
                    }
                    when (boxThreeSection) {
                        //todo: change these two box one
                        Sections.ONE -> {
                            setBoxThreeOffset(0f)
                        }

                        Sections.TWO -> {
                            setBoxThreeOffset(sectionTwoHeight)
                        }

                        Sections.THREE -> {
                            setBoxThreeOffset(sectionThreeHeight)
                        }
                    }
                },
                //todo: I should remove these when I get the UI for all the sections implemented

            ) {
                ContentDragBox(
                    boxThreeIndex,
                    fullChatMode =fullChatMode,
                    smallChat={
                        smallChat(
                            setDraggingTrue={
                                setBoxThreeDragging(true)
                                setBoxThreeDoubleTap(true)
                            }
                        )
                    },
                    fullChat={
                        fullChat(
                            setDraggingTrue={
                                Log.d("DOUBLECLICKDRAGGING","Full chat boxThree working")
                                setBoxThreeDragging(true)
                                setBoxThreeDoubleTap(true)
                            }
                        )
                    },
                    modActions={
                        NewModActions(
                            setDragging={newValue ->
                                setBoxThreeDoubleTap(newValue)
                                setBoxThreeDoubleTap(true)
                                        },
                            modActionStatus =modActionStatus,
                            modActionsList=modActionsList,
                            doubleClickAndDrag =doubleClickAndDrag,
                            setDoubleClickAndDragFalse={setDoubleClickAndDragFalse()}
                        )
                    },
                    autoModQueue = {
                        NewAutoModQueueBox(
                            setDragging={newValue ->
                                setBoxThreeDoubleTap(newValue)
                                setBoxThreeDoubleTap(true)
                                        },
                            autoModMessageList = autoModMessageList,
                            manageAutoModMessage ={messageId,action ->manageAutoModMessage(messageId,action)},
                            connectionError =Response.Success(true),
                            reconnect = {},
                            autoModStatus=autoModStatus,
                            doubleClickAndDrag =doubleClickAndDrag,
                            setDoubleClickAndDragFalse={setDoubleClickAndDragFalse()}
                        )
                    }


                )
                if(boxThreeDoubleTap){
                    DetectDoubleClickSpacer(
                        setDragging={ setBoxThreeDoubleTap(false)}
                    )
                }



            }


            // todo: I need to change boxThreeDragging, boxTwoDragging and boxOneDragging

            BoxDeleteSection(
                boxThreeDoubleTap, boxTwoDoubleTap, boxOneDoubleTap,
                deleteBoxThree,deleteBoxTwo,deleteBoxOne,
                Modifier.align(Alignment.BottomCenter)
            )


        }
        /******END OF THE BOX*********/

    }
    /******END OF THE SCAFFOLD*********/
}

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DragBox(
    boxHeight: Dp,
    boxOffset:Float,
    dragState: DraggableState,
    modifier: Modifier,
    onDragStoppedFuc:()->Unit,
    content: @Composable () -> Unit
){
    Box(
        modifier = modifier
            .height(boxHeight)
            .fillMaxWidth()
            .offset { IntOffset(0, boxOffset.roundToInt()) }
            .draggable(
                orientation = Orientation.Vertical,
                state = dragState,
                onDragStopped = {
                    onDragStoppedFuc()
                }
            )


    ){
        content()

    }
}

@Composable
fun CustomTopBar(
    showDrawerFunc:()->Unit,
    closeModView:()->Unit
){
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.primary)
        .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Row(

        ){
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Show the Mod options",
                modifier = Modifier
                    .clickable {
                        showDrawerFunc()
                    }
                    .size(35.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Text("Mod View", fontSize = MaterialTheme.typography.headlineLarge.fontSize, color = MaterialTheme.colorScheme.onPrimary)
        }

        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "close mod view",
            modifier = Modifier
                .clickable {
                    closeModView()
                }
                .size(35.dp),
            tint = MaterialTheme.colorScheme.onPrimary
        )


    }
}

@Composable
fun ContentDragBox(
    contentIndex:Int,
    fullChatMode:Boolean, //change this to fullchatMode
    smallChat: @Composable ()-> Unit,
    fullChat: @Composable ()-> Unit,
    modActions:@Composable () ->Unit,
    autoModQueue:@Composable () -> Unit,
){
    when(contentIndex){
        1 ->{
            Column(modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)) {
                Box(modifier = Modifier.fillMaxSize()){
                    if(fullChatMode){
                        fullChat()
                    }else{
                        smallChat()
                    }


                }

            }
        }
        99->{
            //this is meant to help with the doubles and triples
            //The UI is the same as an empty box. However, it can not be overriden and will count as if there is
            //an actual item inside of the place
            Column(modifier = Modifier
                .fillMaxSize()
            ) {

            }
        }
        0 ->{
            Column(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)) {

            }
        }
        2 ->{
            Column(modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)) {
                Box(modifier = Modifier.fillMaxSize()){
                    autoModQueue()
                }
            }
        }
        3 ->{
            Column(modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)) {
                Box(modifier = Modifier.fillMaxSize()){
                    modActions()
                }
            }
        }
    }

}



/***********************************BELOW IS ALL THE SCAFFOLD DRAWER CONTENT**************************************************************/


@Composable
fun ModViewDrawerContent(
    checkIndexAvailability:(Int)->Unit,
    showError:Boolean,
    autoModQueueChecked:Boolean,
    changeAutoModQueueChecked:(Boolean)->Unit,

    modActionsChecked:Boolean,
    changeModActionsChecked:(Boolean)->Unit,
){
    Box(modifier = Modifier.fillMaxSize()){
        LazyColumn(
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            item {
                ElevatedCardSwitchTextRow(
                    "Chat",
                    checkIndexAvailability={checkIndexAvailability(1)},
                    painter = painterResource(id = R.drawable.keyboard_24),
                )

            }
            item{
                ElevatedCardSwitchRow(
                    "AutoMod Queue",
                    checkIndexAvailability={checkIndexAvailability(2)},
                    painter = painterResource(id = R.drawable.mod_view_24),
                    checked = autoModQueueChecked,
                    changeChecked = {value -> changeAutoModQueueChecked(value)}
                )
            }
            item{
                ElevatedCardSwitchRow(
                    "Mod actions",
                    checkIndexAvailability={checkIndexAvailability(3)},
                    painter = painterResource(id = R.drawable.clear_chat_alt_24),
                    checked = modActionsChecked,
                    changeChecked = {value -> changeModActionsChecked(value)}
                )


            }

//            item{
//                ElevatedCardExample(
//                    Color.Yellow,
//                    "Un-ban requests",
//                    checkIndexAvailability={checkIndexAvailability(4)}
//                )
//            }
//
//            item{
//                ElevatedCardExample(
//                    Color.LightGray,
//                    "Discord",
//                    checkIndexAvailability={checkIndexAvailability(5)}
//                )
//            }
//
//            item{
//                ElevatedCardExample(
//                    Color.Cyan,
//                    "Moderators",
//                    checkIndexAvailability={checkIndexAvailability(6)}
//                )
//            }

        }

        if(showError){
            Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                ErrorMessage(
                    modifier = Modifier,
                    message="Error! No space to place "
                )
                Spacer(modifier =Modifier.height(10.dp))
            }
        }

    }

}

@Composable
fun ElevatedCardSwitchRow(
    text:String,
    checkIndexAvailability: () -> Unit,
    painter: Painter,
    checked:Boolean,
    changeChecked:(Boolean) ->Unit,
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        ElevatedCardWithIcon(
            text,
            checkIndexAvailability={checkIndexAvailability()},
            painter = painter
        )

        SwitchWithIcon(
            checked = checked,
            changeChecked ={value -> changeChecked(value)}
        )
    }
}

@Composable
fun ElevatedCardSwitchTextRow(
    text:String,
    checkIndexAvailability: () -> Unit,
    painter: Painter
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        ElevatedCardWithIcon(
            text,
            checkIndexAvailability={checkIndexAvailability()},
            painter = painter,

            )

        TextColumn(text="Notifications")
    }
}

@Composable
fun SwitchWithIcon(
    checked:Boolean,
    changeChecked:(Boolean) ->Unit,

    ) {


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .padding(top = 13.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Switch(
            checked = checked,
            onCheckedChange = {
                changeChecked(it)
            },
            thumbContent = if (checked) {
                {
                    androidx.compose.material.Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize),
                        tint = Color.White
                    )
                }
            } else {
                null
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.secondary,
                uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                checkedTrackColor = Color.DarkGray,
                uncheckedTrackColor = Color.DarkGray,
            )
        )
    }

}

@Composable
fun TextColumn(
    text:String
) {


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .padding(top = 13.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 20.sp
        )
    }

}

@Composable
fun ElevatedCardWithIcon(
    type:String,
    checkIndexAvailability:()->Unit,
    painter: Painter
) {
    Column() {
        Spacer(modifier =Modifier.height(15.dp))
        ElevatedCard(
            colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .size(width = 200.dp, height = 80.dp)
                .clickable { checkIndexAvailability() }
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Text(
                    text = type,
                    color = Color.White,
                    modifier = Modifier,
                    fontSize = 20.sp
                )
                androidx.compose.material.Icon(
                    painter = painter,
                    contentDescription = "",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )

            }


        }
        Spacer(modifier =Modifier.height(15.dp))
    }

}
@Composable
fun ErrorMessage(
    modifier: Modifier,
    message:String,
){

    Row(
        modifier = modifier
            .clip(
                RoundedCornerShape(20.dp)
            )
            .background(Color.Red)
            .padding(vertical = 5.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,


        ) {
        androidx.compose.material.Icon(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "modderz logo",
            modifier = Modifier.size(25.dp),
            tint = Color.White
        )

        Text(
            text = message,
            color = Color.White,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
        )

    }
}
/******************************************/
@Composable
fun DetectDoubleClickSpacer(
    setDragging:(Boolean) ->Unit,

    ){
    Spacer(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(7f)
            .background(Color.Black.copy(alpha = 0.5f))
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        //I think I detect the long press here and then have the drag up top
                        setDragging(false)
                    }
                ) {

                }
            }
    )
}
/*********************************** SMALL CHAT COMPOSABLES ****************************************/

@Composable
fun SmallChat(
    twitchUserChat: List<TwitchUserData>,
    showBottomModal:()->Unit,
    updateClickedUser: (String, String, Boolean, Boolean) -> Unit,
    showTimeoutDialog:()->Unit,
    showBanDialog:()->Unit,
    doubleClickMessage:(String)->Unit,
    deleteChatMessage:(String)->Unit,
    isMod: Boolean,
    inlineContentMap: EmoteListMap,
    setDragging: (Boolean) -> Unit,
    doubleClickAndDrag:Boolean,
    setDoubleClickAndDragFalse:()->Unit

    ){
    val lazyColumnListState = rememberLazyListState()
    var autoscroll by remember { mutableStateOf(true) }
    SmallChatUIBox(
        chatUI = { modifier ->
            SmallChatUILazyColumn(
//                lazyColumnListState=lazyColumnListState,
                twitchUserChat=twitchUserChat,
//                autoscroll=autoscroll,
//                showBottomModal={showBottomModal()},
//                showTimeoutDialog={showTimeoutDialog()},
//                showBanDialog={showBanDialog()},
//                updateClickedUser = {  username, userId,isBanned,isMod ->
//                    updateClickedUser(
//                        username,
//                        userId,
//                        isBanned,
//                        isMod
//                    )
//                },
//                doubleClickMessage={username ->doubleClickMessage(username)},
                modifier=modifier,
//                deleteChatMessage={messageId ->deleteChatMessage(messageId)},
//                isMod = isMod,
                inlineContentMap=inlineContentMap,
//                setDragging = {value -> setDragging(value)},
                doubleClickAndDrag=doubleClickAndDrag,
//                setDoubleClickAndDragFalse={setDoubleClickAndDragFalse()}

                )
        },
        scrollToBottom = { modifier ->
            SmallChatScrollToBottom(
                scrollingPaused =!autoscroll,
                enableAutoScroll = { autoscroll = true },
                modifier = modifier
            )
        },
        determineScrollState = {
            SmallChatDetermineScrollState(
                lazyColumnListState = lazyColumnListState,
                setAutoScrollFalse = { autoscroll = false },
                setAutoScrollTrue = { autoscroll = true },
            )
        }
    )
}



@Composable
fun SmallChatScrollToBottom(
    scrollingPaused: Boolean,
    enableAutoScroll: () -> Unit,
    modifier: Modifier
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (scrollingPaused) {
            DualIconsButton(
                buttonAction = { enableAutoScroll() },
                iconImageVector = Icons.Default.ArrowDropDown,
                iconDescription = stringResource(R.string.arrow_drop_down_description),
                buttonText = stringResource(R.string.scroll_to_bottom)

            )
        }
    }

}

@Composable
fun SmallChatUIBox(
    chatUI: @Composable ImprovedChatUI.(modifier: Modifier) -> Unit,
    determineScrollState: @Composable () -> Unit,
    scrollToBottom: @Composable (modifier:Modifier) -> Unit,
){
    val chatUIScope = remember(){ ImprovedChatUI() }
    Box(modifier = Modifier.fillMaxSize()){
        determineScrollState()
        with(chatUIScope){
            chatUI(modifier = Modifier.fillMaxSize())
        }
        scrollToBottom(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
                .zIndex(5f)
        )


    }

}

@Composable
fun SmallChatDetermineScrollState(
    lazyColumnListState: LazyListState,
    setAutoScrollFalse:()->Unit,
    setAutoScrollTrue:()->Unit,
){
    val interactionSource = lazyColumnListState.interactionSource
    val endOfListReached by remember {
        derivedStateOf {
            lazyColumnListState.isScrolledToEnd()
        }
    }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is DragInteraction.Start -> {
                    setAutoScrollFalse()
                }
                is PressInteraction.Press -> {
                    setAutoScrollFalse()
                }
            }
        }
    }

    // observer when reached end of list
    LaunchedEffect(endOfListReached) {
        // do your stuff
        if (endOfListReached) {
            setAutoScrollTrue()
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SmallChatUILazyColumn(
//    lazyColumnListState: LazyListState,
    twitchUserChat: List<TwitchUserData>,
//    autoscroll:Boolean,
//    showBottomModal:()->Unit,
//    showTimeoutDialog:()->Unit,
//    showBanDialog:()->Unit,
//    updateClickedUser: (String, String, Boolean, Boolean) -> Unit,
//    doubleClickMessage:(String)->Unit,
//    deleteChatMessage:(String)->Unit,
    modifier: Modifier,
//    isMod: Boolean,
    inlineContentMap: EmoteListMap,
//    setDragging: (Boolean) -> Unit,
    doubleClickAndDrag:Boolean,
//    setDoubleClickAndDragFalse:()->Unit
){
    val coroutineScope = rememberCoroutineScope()
    val chatUIScope = remember(){ ImprovedChatUI() }
    val hapticFeedback = LocalHapticFeedback.current
    LazyColumn(
        modifier =modifier,
//        state = lazyColumnListState
    ){
        coroutineScope.launch {
//            if (autoscroll) {
//                lazyColumnListState.scrollToItem(twitchUserChat.size)
//            }
        }
        stickyHeader {
            ChatHeader(
                setDragging ={newValue ->
                    //setDragging(newValue)
                             },
                doubleClickAndDrag =doubleClickAndDrag,
                setDoubleClickAndDragFalse={
                   // setDoubleClickAndDragFalse()
                }
            )

        }
        with(chatUIScope){
            items(
                twitchUserChat,
            ) {indivChatMessage ->
                Log.d("SmallChatUILazyColumn","${indivChatMessage.userType}")

                ChatMessages(
                    indivChatMessage,
                    showBottomModal={
//                        showBottomModal()
                                    },
                    updateClickedUser = {  username, userId,isBanned,isMod ->
//                        updateClickedUser(
//                            username,
//                            userId,
//                            isBanned,
//                            isMod
//                        )
                    },
                    showTimeoutDialog ={
                        //showTimeoutDialog()
                                       },
                    showBanDialog={
                        //showBanDialog()
                                  },
                    doubleClickMessage={username ->
                        //doubleClickMessage(username)
                                       },
                    deleteChatMessage={messageId->
                        //deleteChatMessage(messageId)
                                      },
                    isMod = false,
                    inlineContentMap=inlineContentMap

                )

            }
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatHeader(
    setDragging: (Boolean) -> Unit,
    doubleClickAndDrag:Boolean,
    setDoubleClickAndDragFalse:()->Unit

){
    val hapticFeedback = LocalHapticFeedback.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
            .combinedClickable(
                onDoubleClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    setDragging(true)
                    setDoubleClickAndDragFalse()
                },
                onClick = {}
            )
            .padding(horizontal = 10.dp),
        horizontalArrangement =Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically

    ){
        Text(
            "Chat",
            color = MaterialTheme.colorScheme.onSecondary,
            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
        )
        if(doubleClickAndDrag){
            Text(
                "Double click and drag",
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,

                )
        }

    }
}
/*********************************** DELETE BOX SECTION ************************************************/

@Composable
fun BoxDeleteSection(
    boxThreeDragging:Boolean,
    boxTwoDragging:Boolean,
    boxOneDragging:Boolean,

    deleteBoxOne:Boolean,
    deleteBoxTwo:Boolean,
    deleteBoxThree: Boolean,

    modifier: Modifier

){
    val colorStops = arrayOf(
        0.0f to Color.Red.copy(0.0f),
        0.2f to Color.Red.copy(0.2f),
        0.4f to Color.Red.copy(0.4f),
        0.6f to Color.Red.copy(0.6f),
        1f to Color.Red
    )

    if(boxThreeDragging  || boxTwoDragging || boxOneDragging){
        if(deleteBoxOne||  deleteBoxTwo || deleteBoxThree){
            Row(modifier = modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Brush.verticalGradient(colorStops = colorStops))
                .zIndex(10f)
            ){

            }
        }else{
            Row(modifier = modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(Brush.verticalGradient(colorStops = colorStops))
                .zIndex(10f)
            ){

            }
        }

    }
}

/****************************** MOD ACTION COMPOSABLE*********************************************************/
/**
 * ModActions is the composable function that is used  to represent the actions that
 * have been taken by moderators in the chat
 * */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NewModActions(
    setDragging:(Boolean)->Unit,
    modActionStatus: WebSocketResponse<Boolean>,
    modActionsList: List<ModActionData>,
    doubleClickAndDrag:Boolean,
    setDoubleClickAndDragFalse:()->Unit
){
    val hapticFeedback = LocalHapticFeedback.current
    //todo: GET THIS LIST FROM THE WEBSOCKET


    val listState = rememberLazyListState()

    val scope = rememberCoroutineScope()
    var autoscroll by remember { mutableStateOf(true) }
    val interactionSource = listState.interactionSource



    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is DragInteraction.Start -> {
                    autoscroll = false
                }
                is PressInteraction.Press -> {
                    autoscroll = false
                }
            }
        }
    }

    val endOfListReached by remember {
        derivedStateOf {
            listState.isScrolledToEnd()
        }
    }
    // observer when reached end of list
    LaunchedEffect(endOfListReached) {
        // do your stuff
        if (endOfListReached) {
            autoscroll = true
        }
    }


    Box(modifier = Modifier
        .fillMaxSize()
    ) {

        when(modActionStatus){
            is WebSocketResponse.Loading -> {

                LoadingIndicator(
                    hapticFeedback =hapticFeedback,
                    setDragging={value -> setDragging(value)},
                    title = "MOD ACTIONS: ${modActionsList.size}"
                )

            }
            is WebSocketResponse.Success -> {
                // this should be the individual moderation actions

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(vertical = 5.dp)

                ) {
                    stickyHeader {
                        ModActionsHeader(
                            headerText ="MOD ACTIONS: ${modActionsList.size} ",
                            setDragging ={newValue ->setDragging(newValue)},
                            doubleClickAndDrag =doubleClickAndDrag,
                            setDoubleClickAndDragFalse={setDoubleClickAndDragFalse()}

                        )
                    }
                    items(modActionsList){modAction->
                        ModActionNotificationMessage(
                            title=modAction.title,
                            message=modAction.message,
                            icon = painterResource(id =modAction.iconId),
                            secondaryErrorMessage = modAction.secondaryMessage
                        )
                    }

                    scope.launch {
                        if(autoscroll){
                            listState.scrollToItem(modActionsList.size)
                        }
                    }


                }
                if(!autoscroll){
                    ScrollToBottomModView(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 20.dp),
                        enableAutoScroll={
                            scope.launch {
                                listState.scrollToItem(modActionsList.size)
                                autoscroll = true
                            }
                        }
                    )
                }

            }

            is WebSocketResponse.Failure -> {
                //should be a button to retry
                FailedClickToTryAgainBox(
                    hapticFeedback =hapticFeedback,
                    setDragging={value -> setDragging(value)},
                    title = "MOD ACTIONS: ${modActionsList.size}"
                )

            }
            is WebSocketResponse.FailureAuth403 -> {
                NewErrorMessage403(
                    setDragging = { value -> setDragging(value) },
                    title = "MOD ACTIONS: ${modActionsList.size}",
                    doubleClickAndDrag =doubleClickAndDrag,
                    setDoubleClickAndDragFalse={setDoubleClickAndDragFalse()}
                )


            }



        }



    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModActionsHeader(
    setDragging:(Boolean)->Unit,
    setDoubleClickAndDragFalse:() ->Unit,
    doubleClickAndDrag:Boolean,
    headerText:String,
){
    val hapticFeedback = LocalHapticFeedback.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
            .combinedClickable(
                onDoubleClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    setDragging(true)
                    setDoubleClickAndDragFalse()
                },
                onClick = {}
            )
            .padding(horizontal = 10.dp),
        horizontalArrangement =Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically

    ){
        Text(
            headerText,
            color = MaterialTheme.colorScheme.onSecondary,
            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
        )
        if(doubleClickAndDrag){
            Text(
                "Double click and drag",
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,

                )
        }

    }

}

/************************ AutoModQueueBox ****************************************************************/
/**
 * AutoModQueueBox is the composable function that is used inside of [DraggableBackground] to represent the AutoModQue messages
 * shown to the user
 *
 * @param dragging a Boolean used to determine if the user is dragging this component
 * @param setDragging a function used to set the value of [dragging]
 * */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NewAutoModQueueBox(
    setDragging: (Boolean) -> Unit,
    autoModMessageList:List<AutoModQueueMessage>,
    manageAutoModMessage:(String,String)-> Unit,
    autoModStatus: WebSocketResponse<Boolean>,
    connectionError: Response<Boolean>,
    reconnect:()->Unit,
    doubleClickAndDrag:Boolean,
    setDoubleClickAndDragFalse:()->Unit

){
    val hapticFeedback = LocalHapticFeedback.current
    val listState = rememberLazyListState()

    val scope = rememberCoroutineScope()
    var autoscroll by remember { mutableStateOf(true) }
    val interactionSource = listState.interactionSource



    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is DragInteraction.Start -> {
                    autoscroll = false
                }
                is PressInteraction.Press -> {
                    autoscroll = false
                }
            }
        }
    }
    val endOfListReached by remember {
        derivedStateOf {
            listState.isScrolledToEnd()
        }
    }
    // observer when reached end of list
    LaunchedEffect(endOfListReached) {
        // do your stuff
        if (endOfListReached) {
            autoscroll = true
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.primary)

    ){
        when(autoModStatus){
            is WebSocketResponse.Loading->{
                LoadingIndicator(
                    hapticFeedback =hapticFeedback,
                    setDragging={value -> setDragging(value)},
                    title = "AutoMod Queue"
                )

            }
            is WebSocketResponse.Success->{
                LazyColumn(
                    state=listState,
                    modifier =Modifier.fillMaxSize()
                ){
                    scope.launch {
                        if(autoscroll){
                            listState.scrollToItem(autoModMessageList.size)
                        }
                    }
                    stickyHeader {

                        AutoModHeader(
                            setDragging ={newValue -> setDragging(newValue)},
                            doubleClickAndDrag =doubleClickAndDrag,
                            setDoubleClickAndDragFalse={setDoubleClickAndDragFalse()}
                        )
                    }



                    items(autoModMessageList){autoModMessage->
                        AutoModBoxHorizontalDragBox(
                            autoModMessage=autoModMessage,
                            manageAutoModMessage={
                                    messageId,action->manageAutoModMessage(messageId,action)
                            }
                        )
                    }



                }
                if(!autoscroll){
                    ScrollToBottomModView(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 20.dp),
                        enableAutoScroll={
                            scope.launch {
                                listState.scrollToItem(autoModMessageList.size)
                                autoscroll = true
                            }
                        }
                    )
                }
            }
            is WebSocketResponse.Failure->{
                FailedClickToTryAgainBox(
                    hapticFeedback =hapticFeedback,
                    setDragging={value -> setDragging(value)},
                    title = "AutoMod Queue"
                )

            }
            is WebSocketResponse.FailureAuth403->{

                NewErrorMessage403(
                    setDragging = { value -> setDragging(value) },
                    title = "AutoMod Queue",
                    doubleClickAndDrag =doubleClickAndDrag,
                    setDoubleClickAndDragFalse={setDoubleClickAndDragFalse()}
                )

            }

        }


    }
    ConnectionErrorResponse(
        connectionError,
        reconnect ={reconnect()}
    )


}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AutoModHeader(
    setDragging: (Boolean) -> Unit,
    doubleClickAndDrag:Boolean,
    setDoubleClickAndDragFalse:()->Unit
){

    val hapticFeedback = LocalHapticFeedback.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
            .combinedClickable(
                onDoubleClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    setDragging(true)
                    setDoubleClickAndDragFalse()
                },
                onClick = {}
            )
            .padding(horizontal = 10.dp),
        horizontalArrangement =Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically

    ){
        Text(
            "AutoMod Queue",
            color = MaterialTheme.colorScheme.onSecondary,
            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
        )
        if(doubleClickAndDrag){
            Text(
                "Double click and drag",
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,

                )
        }

    }

}
/********************************** Response messages **********************************************/
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NewErrorMessage403(
    setDragging: (Boolean) -> Unit,
    title:String,
    setDoubleClickAndDragFalse:() ->Unit,
    doubleClickAndDrag:Boolean
){
    val hapticFeedback = LocalHapticFeedback.current
    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.primary)){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary)
                .combinedClickable(
                    onDoubleClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        setDragging(true)
                        setDoubleClickAndDragFalse()
                    },
                    onClick = {}
                )
                .align(Alignment.TopCenter)
                .padding(horizontal = 10.dp),
            horizontalArrangement =Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ){
            Text(
                title,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,

                )
            if(doubleClickAndDrag){
                Text(
                    "Double click and drag",
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,

                    )
            }

        }

        NewIconTextRow(
            modifier = Modifier.align(Alignment.Center)
        )

    }
}

@Composable
fun NewIconTextRow(
    modifier:Modifier
) {

    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
        Icon(painter = painterResource(id =R.drawable.error_outline_24), contentDescription = "error",tint=Color.Red)
        Text(
            text = "Token error! Please login again to be issued a new token from Twitch",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }


}