package com.example.clicker.presentation.stream.views.streamManager

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.clicker.R
import com.example.clicker.network.clients.BlockedTerm
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.network.repository.EmoteListMap
import com.example.clicker.network.repository.EmoteNameUrl
import com.example.clicker.network.repository.util.AutoModQueueMessage

import com.example.clicker.presentation.modView.ListTitleValue
import com.example.clicker.presentation.modView.ModActionData
import com.example.clicker.presentation.modView.ModViewDragStateViewModel
import com.example.clicker.presentation.modView.ModViewViewModel
import com.example.clicker.presentation.modView.followerModeList
import com.example.clicker.presentation.modView.slowModeList
import com.example.clicker.presentation.modView.views.DraggableModViewBox
import com.example.clicker.presentation.sharedViews.SharedComponents
import com.example.clicker.presentation.stream.ClickedUIState
import com.example.clicker.presentation.modView.views.SharedBottomModal
import com.example.clicker.presentation.modView.views.SmallChat

import com.example.clicker.presentation.sharedViews.ButtonScope
import com.example.clicker.presentation.sharedViews.ModViewScaffoldWithDrawer
import com.example.clicker.presentation.stream.StreamViewModel
import com.example.clicker.presentation.stream.views.BottomModal
import com.example.clicker.presentation.stream.views.chat.ChatSettingsColumn
import com.example.clicker.presentation.stream.views.chat.ChatUI
import com.example.clicker.presentation.stream.views.chat.EmoteOnlySwitch
import com.example.clicker.presentation.stream.views.chat.FollowersOnlyCheck
import com.example.clicker.presentation.stream.views.chat.FullChatModView
import com.example.clicker.presentation.stream.views.chat.SlowModeCheck
import com.example.clicker.presentation.stream.views.chat.SubscriberOnlySwitch
import com.example.clicker.presentation.stream.views.chat.isScrolledToEnd
import com.example.clicker.util.Response
import com.example.clicker.util.WebSocketResponse
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModViewComponent(
    closeModView:()->Unit,
    modViewDragStateViewModel: ModViewDragStateViewModel,
    inlineContentMap: EmoteListMap,
    twitchUserChat: List<TwitchUserData>,
    streamViewModel:StreamViewModel,
    modViewViewModel:ModViewViewModel,
    hideSoftKeyboard:()->Unit,
){
    val clickedChatterUserState = androidx.compose.material.rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = false
    )
    val chatSettingModal = androidx.compose.material.rememberModalBottomSheetState(
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
    val doubleClickChat:(String)->Unit =remember(streamViewModel) { {
        streamViewModel.sendDoubleTapEmote(it)
    } }
    val updateMostFrequentEmoteList:(EmoteNameUrl)->Unit =remember(streamViewModel) { {
        streamViewModel.updateMostFrequentEmoteListTesting(it)
    } }

    val fullModeActive = modViewDragStateViewModel.fullModeActive.value
    val scope = rememberCoroutineScope()
    ModalBottomSheetLayout(
        sheetBackgroundColor = MaterialTheme.colorScheme.primary,
        sheetState = clickedChatterUserState,
        sheetContent = {
            BottomModal.BottomModalBuilder(
                clickedUsernameChats = streamViewModel.clickedUsernameChats,
                clickedUsername = streamViewModel.clickedUIState.value.clickedUsername,
                bottomModalState = clickedChatterUserState,
                textFieldValue = streamViewModel.textFieldValue,
                closeBottomModal = {},
                banned = streamViewModel.clickedUIState.value.clickedUsernameBanned,
                unbanUser = {
                    //  streamViewModel.unBanUser()
                },
                //todo: turn this back into --> streamViewModel.state.value.loggedInUserData?.mod ?: false
                isMod = true,
                openTimeoutDialog = { streamViewModel.openTimeoutDialog.value = true },
                openBanDialog = { streamViewModel.openBanDialog.value = true },
                shouldMonitorUser = streamViewModel.shouldMonitorUser.value,
                updateShouldMonitorUser = {},
            )
        }

    ){
        ModViewScaffold(
            modViewDragStateViewModel=modViewDragStateViewModel,
            closeModView ={
                closeModView()
            },

            fullModeActive=fullModeActive,
            fullChat = {setDraggingFunc->
                //todo: I need to implement a ModalBottomSheetLayout with the chat settings
                ModalBottomSheetLayout(
                    sheetState = chatSettingModal,
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
                    //todo: this is where FullChatModView goes
                    FullChatModView(
                        twitchUserChat = twitchUserChat,
                        showBottomModal={
                            scope.launch {
                                clickedChatterUserState.show()
                            }
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
                            Log.d("BottomModalClicked","showOuterBottomModalState Clicked")
                            //seems fine that it is empy
                            scope.launch {

                            }
                        },
                        newFilterMethod={newTextValue -> streamViewModel.newParsingAgain(newTextValue)},

                        orientationIsVertical =true,

                        //todo:change back to --> streamViewModel.state.value.loggedInUserData?.mod ?: false
                        isMod = true,
                        filteredChatList = streamViewModel.filteredChatList,
                        clickedAutoCompleteText = { username ->
                            streamViewModel.autoTextChange(username)
                        },
                        showModal = {
                            //todo: This is what is clicked when I want to launch the bottom modal
                            scope.launch {
                                chatSettingModal.show()
                            }
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
                        hideSoftKeyboard={
                            hideSoftKeyboard()

                        },
                        emoteBoardGlobalList = streamViewModel.globalEmoteUrlList.value,
                        updateTextWithEmote = {newValue -> streamViewModel.addEmoteToText(newValue)},
                        emoteBoardChannelList =streamViewModel.channelEmoteUrlList.value,
                        deleteEmote={streamViewModel.deleteEmote()},
                        showModView={
                            closeModView()
                        },
                        fullMode = fullModeActive,
                        setDragging = {
                            Log.d("doubleClickingThings","CLICKED")
                            setDraggingFunc()
                        },
                        emoteBoardMostFrequentList= streamViewModel.mostFrequentEmoteListTesting.value,
                        updateMostFrequentEmoteList={value ->updateMostFrequentEmoteList(value)},
                        globalBetterTTVResponse= streamViewModel.globalBetterTTVEmotes.value
                    )
                }

            },
            modActionsList = modViewViewModel.modActionsList,
            modActionStatus = modViewViewModel.modViewStatus.value.modActions,
            smallChat = {setDraggingFunc->
                SmallChat(
                    twitchUserChat=twitchUserChat,
                    showBottomModal ={
                        scope.launch {
                            clickedChatterUserState.show()
                        }
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
                    deleteChatMessage={messageId ->streamViewModel.deleteChatMessage(messageId)},
                    //todo:change back to --> true for testing
                    isMod =streamViewModel.state.value.loggedInUserData?.mod ?: false,
                    inlineContentMap =inlineContentMap,
                    setDragging = {value -> setDraggingFunc()},
                )
            },
            autoModMessageList = modViewViewModel.autoModMessageList,
            manageAutoModMessage ={
                    messageId,action -> modViewViewModel.manageAutoModMessage(messageId,action)
                                  },
            autoModQueueChecked = modViewViewModel.uiState.value.autoModMessagesNotifications,
            changeAutoModQueueChecked ={value ->modViewViewModel.changeAutoModQueueChecked(value)},
            modActionsChecked=modViewViewModel.uiState.value.modActionNotifications,
            changeModActionsChecked ={value ->modViewViewModel.changeModActionsChecked(value)},
            autoModStatus = modViewViewModel.modViewStatus.value.autoModMessageStatus
        )

    }

}


    @Composable
    fun ModViewScaffold(
        closeModView:()->Unit,
        modViewDragStateViewModel: ModViewDragStateViewModel,

        modActionStatus: WebSocketResponse<Boolean>,
        modActionsList: List<ModActionData>,
        autoModMessageList: List<AutoModQueueMessage>,

        fullModeActive:Boolean,
        manageAutoModMessage:(String,String)-> Unit,
        autoModQueueChecked:Boolean,
        changeAutoModQueueChecked:(Boolean)->Unit,

        modActionsChecked:Boolean,
        changeModActionsChecked:(Boolean)->Unit,
        autoModStatus: WebSocketResponse<Boolean>,
        fullChat: @Composable ( setDraggingTrue: () -> Unit)-> Unit,
        smallChat: @Composable ( setDraggingTrue: () -> Unit)-> Unit
        ){

        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()


        ModViewScaffoldWithDrawer(
            topBar = {showDrawerFunc ->
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
                            contentDescription = "close mod view",
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
            },
            drawerState=drawerState,
            bottomBar = {
                //there is no bottom bar intentionally
            },
            showError = modViewDragStateViewModel.showDrawerError.value,
            checkIndexAvailability ={index ->modViewDragStateViewModel.checkBoxIndexAvailability(index)},
            autoModQueueChecked = autoModQueueChecked,
            changeAutoModQueueChecked ={value ->changeAutoModQueueChecked(value)},
            modActionsChecked=modActionsChecked,
            changeModActionsChecked ={value ->changeModActionsChecked(value)}
        ) {contentPadding ->
            Log.d("contentPaddingModView","contentPadding --> ${contentPadding.calculateTopPadding().value}")

            DraggableModViewBox(
                contentPaddingValues = contentPadding,
                boxOneOffsetY =modViewDragStateViewModel.dragStateOffsets.value.boxOneOffsetY,
                setBoxOneOffset={newValue-> modViewDragStateViewModel.setBoxOneOffset(newValue)},
                boxOneDragState = modViewDragStateViewModel.boxOneDragState,
                boxOneZIndex = modViewDragStateViewModel.boxIndexes.value.boxOneZIndex,
                animateToOnDragStop = modViewDragStateViewModel.animateToOnDragStop,
                indivBoxSize = modViewDragStateViewModel.indivBoxSize,
                sectionBreakPoint = modViewDragStateViewModel.sectionBreakPoint,

                boxTwoOffsetY = modViewDragStateViewModel.dragStateOffsets.value.boxTwoOffsetY,
                boxTwoZIndex = modViewDragStateViewModel.boxIndexes.value.boxTwoZIndex,
                setBoxTwoOffset = {newValue ->modViewDragStateViewModel.setBoxTwoOffset(newValue)},
                boxTwoDragState = modViewDragStateViewModel.boxTwoDragState,

                boxThreeZIndex = modViewDragStateViewModel.boxIndexes.value.boxThreeZIndex,
                boxThreeOffsetY = modViewDragStateViewModel.dragStateOffsets.value.boxThreeOffsetY,
                setBoxThreeOffset = {newValue ->modViewDragStateViewModel.setBoxThreeOffset(newValue)},
                boxThreeDragState = modViewDragStateViewModel.boxThreeDragState,

                boxOneDragging = modViewDragStateViewModel.isDragging.value.boxOneDragging,
                setBoxOneDragging = {newValue -> modViewDragStateViewModel.setBoxOneDragging(newValue)},

                boxThreeDragging =modViewDragStateViewModel.isDragging.value.boxThreeDragging,
                boxTwoDragging =modViewDragStateViewModel.isDragging.value.boxTwoDragging,
                setBoxThreeDragging ={
                        newValue -> modViewDragStateViewModel.setBoxThreeDragging(newValue)
                    Log.d("WHERETHEDOUBLEIS","DraggableModViewBox")
                                     },
                setBoxTwoDragging ={newValue -> modViewDragStateViewModel.setBoxTwoDragging(newValue)},

                deleteOffsetY = modViewDragStateViewModel.deleteOffset.value,
                boxOneIndex = modViewDragStateViewModel.boxTypeIndex.value.boxOneIndex,
                boxTwoIndex = modViewDragStateViewModel.boxTypeIndex.value.boxTwoIndex,
                boxThreeIndex = modViewDragStateViewModel.boxTypeIndex.value.boxThreeIndex,
                setBoxIndex = {box,value -> modViewDragStateViewModel.changeBoxTypeIndex(box,value)},

                boxOneHeight = modViewDragStateViewModel.indivBoxHeight.value.boxOne,
                boxTwoHeight = modViewDragStateViewModel.indivBoxHeight.value.boxTwo,
                boxThreeHeight = modViewDragStateViewModel.indivBoxHeight.value.boxThree,

                modActionStatus=modActionStatus,
                modActionsList=modActionsList,

                fullModeActive=fullModeActive,
                fullChat={ setDraggingFunc ->
                    fullChat(
                        setDraggingTrue={setDraggingFunc()}
                    )
                },
                smallChat={ setDraggingFunc ->
                    smallChat(
                        setDraggingTrue={setDraggingFunc()}
                    )
                },
                autoModMessageList=autoModMessageList,
                manageAutoModMessage ={messageId,action ->manageAutoModMessage(messageId,action)},
                autoModStatus=autoModStatus


            )


        }

    }

    /**
     * SectionHeaderRow is a [Row] composable meant to show a [Text] containing [title] and a [ModesHeaderRow] side by side
     *
     * @param title meant to represents the title of this header
     * @param horizontalArrangement the arrangement meant to determine how the items in this row will appear
     * @param expanded a conditional to determine if the embedded [ModesHeaderRow] should show its view or not
     * @param setExpanded a function used to change the value of [expanded]
     * **/
    @Composable
    fun SectionHeaderRow(
        title:String,
        horizontalArrangement:Arrangement.Horizontal = Arrangement.Start,
        expanded:Boolean,
        setExpanded: (Boolean) -> Unit

    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = horizontalArrangement

            ) {
            Text(
                title,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize
            )
            ModesHeaderRow(
                expanded = expanded,
                changeExpanded = {newValue ->setExpanded(newValue)}
            )

        }
    }

    /**
     * ModesHeaderRow a composable meant to be used with the [SectionHeaderRow] to inform the use what to press
     *
     * @param expanded a conditional used to determine if a [DropdownMenuColumn] should open up or not
     * @param changeExpanded a function used to set the value of [expanded]
     * */
    @Composable
    fun ModesHeaderRow(
        expanded: Boolean,
        changeExpanded:(Boolean)->Unit,
    ){
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary.copy(.3f))
                .padding(horizontal = 5.dp)
                .clickable {
                    changeExpanded(true)
                },
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                imageVector =Icons.Default.Settings,
                contentDescription ="Settings",
                tint = Color.White
            )
            Text("Modes",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                modifier = Modifier.padding(horizontal = 5.dp)
            )
            Icon(
                imageVector =if(expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp ,
                contentDescription ="Settings",
                tint = Color.White
            )
        }
    }

    /**
     * DropDownMenuHeaderBox is a header meant to combine the [SectionHeaderRow] and a [DropdownMenuColumn]. It has an internal state
     * of `expanded` to determine if the [DropdownMenuColumn] should show or not.
     * */
    @Composable
    fun DropDownMenuHeaderBox(
        headerTitle:String,
        blockedTerms:List<BlockedTerm>,
        deleteBlockedTerm:(String) ->Unit,
        emoteOnly:Boolean,
        setEmoteOnly:(Boolean) ->Unit,
        subscriberOnly:Boolean,
        setSubscriberOnly:(Boolean) ->Unit,
        chatSettingsEnabled:Boolean,
        switchEnabled: Boolean,

        followersOnlyList: List<ListTitleValue>,
        selectedFollowersModeItem: ListTitleValue,
        changeSelectedFollowersModeItem: (ListTitleValue) -> Unit,
        slowModeList: List<ListTitleValue>,
        selectedSlowModeItem: ListTitleValue,
        changeSelectedSlowModeItem: (ListTitleValue) -> Unit,
    ){
        var expanded by remember { mutableStateOf(false) }
        //todo: animate the icon change
        Box(){
            DropdownMenuColumn(
                expanded,
                setExpanded ={newValue -> expanded = newValue},
                blockedTerms=blockedTerms,
                deleteBlockedTerm ={blockedTermId ->deleteBlockedTerm(blockedTermId)},
                emoteOnly =emoteOnly,
                setEmoteOnly={newValue -> setEmoteOnly(newValue)},
                subscriberOnly =subscriberOnly,
                setSubscriberOnly={newValue ->setSubscriberOnly(newValue)},
                chatSettingsEnabled=chatSettingsEnabled,
                switchEnabled=switchEnabled,

                followersOnlyList=followersOnlyList,
                selectedFollowersModeItem=selectedFollowersModeItem,
                changeSelectedFollowersModeItem ={newValue -> changeSelectedFollowersModeItem(newValue)},
                slowModeList=slowModeList,
                selectedSlowModeItem=selectedSlowModeItem,
                changeSelectedSlowModeItem ={newValue ->changeSelectedSlowModeItem(newValue)},

            )
            SectionHeaderRow(
                title = headerTitle,
                horizontalArrangement = Arrangement.SpaceBetween,
                expanded = expanded,
                setExpanded ={newValue -> expanded = newValue}
            )

        }
    }


    /*******************BELOW IS ALL THE COMPOSABLES USED TO BUILD THE MODES SECTION OF CHATBOX************************************/

    @Composable
    fun DropdownMenuColumn(
        expanded:Boolean,
        setExpanded:(Boolean)->Unit,
        blockedTerms:List<BlockedTerm>,
        deleteBlockedTerm:(String) ->Unit,

        emoteOnly:Boolean,
        setEmoteOnly:(Boolean) ->Unit,
        subscriberOnly:Boolean,
        setSubscriberOnly:(Boolean) ->Unit,

        chatSettingsEnabled:Boolean,
        switchEnabled: Boolean,

        followersOnlyList: List<ListTitleValue>,
        selectedFollowersModeItem: ListTitleValue,
        changeSelectedFollowersModeItem: (ListTitleValue) -> Unit,
        slowModeList: List<ListTitleValue>,
        selectedSlowModeItem: ListTitleValue,
        changeSelectedSlowModeItem: (ListTitleValue) -> Unit,
    ) {
        var permittedWordsExpanded by remember {
            mutableStateOf(false)
        }
        var bannedWordsExpanded by remember {
            mutableStateOf(false)
        }


        DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    setExpanded(false)
                    permittedWordsExpanded = false
                                   },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.DarkGray
                    )
            ) {
                Text("THEPLEBDEV CHANNEL MODES",color = Color.White,modifier=Modifier.padding(start=13.dp,bottom=13.dp))


            EmoteOnlySwitch(
                setExpanded ={newValue -> setExpanded(newValue)},
                emoteOnly =emoteOnly,
                setEmoteOnly={newValue ->setEmoteOnly(newValue)},
                switchEnabled=switchEnabled
            )
            SubscriberOnlySwitch(
                setExpanded ={newValue -> setExpanded(newValue)},
                subscriberOnly = subscriberOnly,
                setSubscriberOnly = {newValue -> setSubscriberOnly(newValue) },
                switchEnabled=switchEnabled
            )
            FollowersOnlyCheck(
                chatSettingsEnabled=chatSettingsEnabled,
                setExpanded ={newValue -> setExpanded(newValue)},
                followersOnlyList=followersOnlyList,
                selectedFollowersModeItem=selectedFollowersModeItem,
                changeSelectedFollowersModeItem ={newValue -> changeSelectedFollowersModeItem(newValue)}
            )
            SlowModeCheck(
                setExpanded ={newValue -> setExpanded(newValue)},
                chatSettingsEnabled=chatSettingsEnabled,
                slowModeList=slowModeList,
                selectedSlowModeItem=selectedSlowModeItem,
                changeSelectedSlowModeItem ={newValue ->changeSelectedSlowModeItem(newValue)},
            )
            Spacer(modifier =Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                Divider(modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth(.94f),
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
                )
            }
            Spacer(modifier =Modifier.height(10.dp))
            BlockedTermsDropdownMenuItem(
                bannedWordsExpanded =bannedWordsExpanded,
                changeBannedWordsExpanded={newValue -> bannedWordsExpanded = newValue},
                numberOfTermsBanned = blockedTerms.size,
                blockedTerms =blockedTerms,
                deleteBlockedTerm ={blockedTermId ->deleteBlockedTerm(blockedTermId)}
            )

            }
    }

    @Composable
    fun BlockedTermsDropdownMenuItem(
        bannedWordsExpanded:Boolean,
        changeBannedWordsExpanded:(Boolean)->Unit,
        numberOfTermsBanned:Int,
        blockedTerms:List<BlockedTerm>,
        deleteBlockedTerm:(String) ->Unit,
    ){
        //so we need another Item that opens up
        DropdownMenuItem(
            onClick = {
                changeBannedWordsExpanded(true)
            },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween

                ){
                    Text("Banned Terms")
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text("$numberOfTermsBanned")
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "")
                    }
                }
            }
        ) //end of DropdownMenuItem

        AddSearchPermittedTermsDropdownMenu(
            expanded= bannedWordsExpanded,
            changeExpanded={newValue ->changeBannedWordsExpanded(newValue)},
            blockedTerms =blockedTerms,
            deleteBlockedTerm ={blockedTermId ->deleteBlockedTerm(blockedTermId)}
        )


    }

    @Composable
    fun AddSearchPermittedTermsDropdownMenu(
        expanded:Boolean,
        changeExpanded: (Boolean) -> Unit,
        blockedTerms:List<BlockedTerm>,
        deleteBlockedTerm:(String) ->Unit,
    ){

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { changeExpanded(false) },
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color.DarkGray
                )
        ){
            DropdownMenuItem(
                onClick = {changeExpanded(false) },
                text = {
                    Column(){
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween

                        ){
                            Text("Permitted Terms", fontSize = MaterialTheme.typography.headlineLarge.fontSize)
                            Icon(Icons.Default.Close, contentDescription = "",modifier = Modifier.size(30.dp))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth(.94f),
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Text("ACTIVE TERMS",fontSize = MaterialTheme.typography.headlineMedium.fontSize)
                        //todo: MAKE A LAZYCOLUMN OF MAX SIZE

                        PermittedTermsLazyColumn(
                            listOfBlockedTerms = blockedTerms,
                            deleteBlockedTerm ={blockedTermId ->deleteBlockedTerm(blockedTermId)}
                        )
                    }
                }
            )
        }
    }

    @Composable
    fun PermittedTermsLazyColumn(
        listOfBlockedTerms:List<BlockedTerm>,
        deleteBlockedTerm:(String) ->Unit,
    ){
            LazyColumn(
                modifier =Modifier.size(width =600.dp, height =200.dp)
            ){

                items(listOfBlockedTerms){blockedTerm ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(blockedTerm.text)
                        Row(verticalAlignment = Alignment.CenterVertically){

                            Spacer(modifier =Modifier.width(10.dp))
                            Icon(painter = painterResource(id =R.drawable.delete_outline_24),
                                contentDescription = "delete permitted term",modifier=Modifier.clickable {
                                    deleteBlockedTerm(blockedTerm.id)
                                })
                        }

                    }
                    Spacer(modifier =Modifier.height(10.dp))
                }

        }


    }











    /**
     * DetectDoubleClickSpacer is a composable used to overlay items inside of the [DraggingBox][com.example.clicker.presentation.stream.views.streamManager.util.ModViewDragSection]
     * and allowing the drag functionality to bubble up and be consumed by the draggingBox
     *
     * @param opacity a value used to determine the darkness level of the Spacer's background. The values should be between 0 and .5
     * @param setDragging a function used to set the value of a dragging condition passed to [DraggingBox][com.example.clicker.presentation.stream.views.streamManager.util.ModViewDragSection]
     * @param hapticFeedback a function that will initiate the the Android's haptic feedback system
     * */
    @Composable
    fun DetectDoubleClickSpacer(
        opacity:Float,
        setDragging:(Boolean) ->Unit,
        hapticFeedback:()->Unit,

    ){
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = opacity))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            //I think I detect the long press here and then have the drag up top
                            hapticFeedback()
                            setDragging(false)
                        }
                    ) {

                    }
                }
        )
    }

    @Composable
    fun DetectDraggingOrNotAtBottomButton(
        dragging:Boolean,
        listState: LazyListState,
        scrollToBottomOfList:()->Unit,
        modifier: Modifier
    ){
        val fontSize =MaterialTheme.typography.headlineSmall.fontSize
        val buttonScope = remember(){ ButtonScope(fontSize) }

        if(!dragging && !listState.isScrolledToEnd()){
            with(buttonScope){
                this.DualIconsButton(
                    buttonAction ={scrollToBottomOfList()},
                    iconImageVector = Icons.Default.ArrowDropDown,
                    iconDescription = stringResource(R.string.arrow_drop_down_description),
                    text = stringResource(R.string.scroll_to_bottom),
                    modifier = modifier
                )
            }
        }
    }

