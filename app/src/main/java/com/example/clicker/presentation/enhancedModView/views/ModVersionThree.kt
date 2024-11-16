package com.example.clicker.presentation.enhancedModView.views

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.clicker.R
import com.example.clicker.network.clients.UnbanRequestItem
import com.example.clicker.network.domain.UnbanStatusFilter
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.network.repository.ClickedUnbanRequestInfo

import com.example.clicker.network.repository.models.EmoteListMap
import com.example.clicker.network.repository.util.AutoModQueueMessage
import com.example.clicker.presentation.enhancedModView.AutoModMessageListImmutableCollection
import com.example.clicker.presentation.enhancedModView.ModActionData
import com.example.clicker.presentation.enhancedModView.ModActionListImmutableCollection
import com.example.clicker.presentation.enhancedModView.Sections
import com.example.clicker.presentation.enhancedModView.UnbanRequestItemImmutableCollection

import com.example.clicker.presentation.stream.views.chat.DualIconsButton
import com.example.clicker.presentation.stream.views.chat.ImprovedChatUI
import com.example.clicker.presentation.stream.views.chat.isScrolledToEnd
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import com.example.clicker.presentation.sharedViews.SwitchWithIcon
import com.example.clicker.presentation.stream.models.ClickedUsernameChatsWithDateSentImmutable
import com.example.clicker.util.Response
import com.example.clicker.util.WebSocketResponse
import com.example.clicker.util.UnAuthorizedResponse


//todo: this need to go inside of the Fragment, where the old modView is

/**
 * - **ModViewComponentVersionThree**
 *
 * */


/**
 * - restartable
 * */
@Composable
fun ModVersionThree(
    boxOneOffsetY: Float,
    setBoxOneOffset:(Float) ->Unit,
    boxOneDragState: DraggableState, //unstable
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
    boxTwoDragState: DraggableState,//unstable
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
    boxThreeDragState: DraggableState, //unstable
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
    modActionsList: List<ModActionData>, //unstable
    autoModMessageList:List<AutoModQueueMessage>, //unstable
    autoModStatus: WebSocketResponse<Boolean>,
    manageAutoModMessage:(String,String)-> Unit,

    changeAutoModQueueChecked: (Boolean) -> Unit,
    changeModActionsChecked: (Boolean) -> Unit,
    modActionsChecked: Boolean,
    autoModQueueChecked: Boolean,

    doubleClickAndDrag:Boolean,
    setDoubleClickAndDragFalse:()->Unit,

    //immutable Lists
    autoModMessageListImmutableCollection: AutoModMessageListImmutableCollection,
    modActionListImmutableCollection: ModActionListImmutableCollection,
    immutableUnbanRequestList: UnbanRequestItemImmutableCollection,

    unbanRequestResponse: UnAuthorizedResponse<List<UnbanRequestItem>>,
    showUnbanRequestModal:()->Unit,
    updateClickedUnbanRequest:(String,String,String,String,String)->Unit,
    sortUnbanRequest:(String)->Unit,
    retryUnbanRequests:()->Unit,

    unbanRequestChecked:Boolean,
    changeUnbanRequestChecked:(Boolean)->Unit,





    ) {
    //TODO: TAKE ALL OF THIS CODE AND MOVE IT TO A VIEWMODEL



    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()


    ModalNavigationDrawer(
        drawerState = drawerState,

        drawerContent = {
            ModalDrawerSheet {

                ModViewDrawerContent(
                    checkIndexAvailability ={newValue ->
                        updateIndex(newValue)
                                            },
                    showError = showError,
                    autoModQueueChecked = autoModQueueChecked,
                    modActionsChecked =modActionsChecked,
                    changeAutoModQueueChecked={newValue ->
                        changeAutoModQueueChecked(newValue)
                                              },
                    changeModActionsChecked={newValue ->
                        changeModActionsChecked(newValue)
                    },
                    unbanRequestChecked = unbanRequestChecked,
                    changeUnbanRequestChecked = {value ->
                        changeUnbanRequestChecked(value)
                    }
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
                            doubleClickAndDrag =doubleClickAndDrag,
                            setDoubleClickAndDragFalse={setDoubleClickAndDragFalse()},
                            modActionListImmutableCollection=modActionListImmutableCollection
                        )
                    },
                    autoModQueue = {
                        NewAutoModQueueBox(
                            setDragging={newValue ->
                                setBoxOneDoubleTap(newValue)
                                setBoxOneDragging(true)
                                        },
                            manageAutoModMessage ={messageId,action ->manageAutoModMessage(messageId,action)},
                            connectionError =Response.Success(true),
                            reconnect = {},
                            autoModStatus=autoModStatus,
                            doubleClickAndDrag =doubleClickAndDrag,
                            setDoubleClickAndDragFalse={
                                setDoubleClickAndDragFalse()
                            },
                            autoModMessageListImmutableCollection=autoModMessageListImmutableCollection
                        )
                    },
                    unbanRequests ={
                        UnbanRequests(
                            setDragging={newValue ->
                                setBoxOneDoubleTap(newValue)
                                setBoxOneDragging(true)
                            },
                            doubleClickAndDrag =doubleClickAndDrag,
                            setDoubleClickAndDragFalse={
                                setDoubleClickAndDragFalse()
                            },
                            unbanRequestResponse=unbanRequestResponse,
                            showUnbanRequestModal={showUnbanRequestModal()},
                            updateClickedUnbanRequest={username,text,userId,requestId,status ->updateClickedUnbanRequest(username,text,userId,requestId,status)},
                            immutableUnbanRequestList=immutableUnbanRequestList,
                            sortUnbanRequest={status->sortUnbanRequest(status)},
                            retryUnbanRequests={retryUnbanRequests()}
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
                            doubleClickAndDrag =doubleClickAndDrag,
                            setDoubleClickAndDragFalse={setDoubleClickAndDragFalse()},
                            modActionListImmutableCollection=modActionListImmutableCollection
                        )
                    },
                    autoModQueue = {
                        NewAutoModQueueBox(
                            setDragging={newValue ->
                                setBoxTwoDoubleTap(newValue)
                                setBoxTwoDragging(true)
                                        },
                          // autoModMessageList = autoModMessageList,
                            manageAutoModMessage ={messageId,action ->manageAutoModMessage(messageId,action)},
                            connectionError =Response.Success(true),
                            reconnect = {},
                            autoModStatus=autoModStatus,
                            doubleClickAndDrag =doubleClickAndDrag,
                            setDoubleClickAndDragFalse={
                                setDoubleClickAndDragFalse() //todo: this is causing a recomp
                            },
                            autoModMessageListImmutableCollection=autoModMessageListImmutableCollection
                        )
                    },
                    unbanRequests ={
                        UnbanRequests(
                            setDragging={newValue ->
                                setBoxTwoDoubleTap(newValue)
                                setBoxTwoDragging(true)
                            },
                            doubleClickAndDrag =doubleClickAndDrag,
                            setDoubleClickAndDragFalse={
                                setDoubleClickAndDragFalse()
                            },
                            unbanRequestResponse=unbanRequestResponse,
                            showUnbanRequestModal={showUnbanRequestModal()},
                            updateClickedUnbanRequest={username,text,userId,requestId,status ->updateClickedUnbanRequest(username,text,userId,requestId,status)},
                            immutableUnbanRequestList=immutableUnbanRequestList,
                            sortUnbanRequest={status->sortUnbanRequest(status)},
                            retryUnbanRequests={retryUnbanRequests()}
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
                            doubleClickAndDrag =doubleClickAndDrag,
                            setDoubleClickAndDragFalse={setDoubleClickAndDragFalse()},
                            modActionListImmutableCollection=modActionListImmutableCollection
                        )
                    },
                    autoModQueue = {
                        NewAutoModQueueBox(
                            setDragging={newValue ->
                                setBoxThreeDoubleTap(newValue)
                                setBoxThreeDoubleTap(true)
                                        },
                          //  autoModMessageList = autoModMessageList,
                            manageAutoModMessage ={messageId,action ->manageAutoModMessage(messageId,action)},
                            connectionError =Response.Success(true),
                            reconnect = {},
                            autoModStatus=autoModStatus,
                            doubleClickAndDrag =doubleClickAndDrag,
                            setDoubleClickAndDragFalse={
                                setDoubleClickAndDragFalse() //todo: this is causing a recomp
                            },
                            autoModMessageListImmutableCollection=autoModMessageListImmutableCollection
                        )
                    },
                    unbanRequests = {
                        UnbanRequests(
                            setDragging={newValue ->
                                setBoxThreeDoubleTap(newValue)
                                setBoxThreeDoubleTap(true)
                            },
                            doubleClickAndDrag =doubleClickAndDrag,
                            setDoubleClickAndDragFalse={
                                setDoubleClickAndDragFalse() //todo: this is causing a recomp
                            },
                            unbanRequestResponse=unbanRequestResponse,
                            showUnbanRequestModal={showUnbanRequestModal()},
                            updateClickedUnbanRequest={username,text,userId,requestId,status ->updateClickedUnbanRequest(username,text,userId,requestId,status)},
                            immutableUnbanRequestList=immutableUnbanRequestList,
                            sortUnbanRequest={status->sortUnbanRequest(status)},
                            retryUnbanRequests={retryUnbanRequests()}

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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UnbanRequests(
    setDragging: (Boolean) -> Unit,
    setDoubleClickAndDragFalse: () -> Unit,
    doubleClickAndDrag: Boolean,
    unbanRequestResponse: UnAuthorizedResponse<List<UnbanRequestItem>>,
    showUnbanRequestModal:()->Unit,
    updateClickedUnbanRequest: (String, String, String,String,String) -> Unit,
    immutableUnbanRequestList: UnbanRequestItemImmutableCollection,
    sortUnbanRequest:(String)->Unit,
    retryUnbanRequests:()->Unit,
){
    Log.d("UnbanRequestsRecomp","Recomping")


    when(unbanRequestResponse){
        is UnAuthorizedResponse.Loading ->{
            LoadingIndicator(
                setDragging={value -> setDragging(value)},
                title = "Unban requests"
            )
        }
        is UnAuthorizedResponse.Success ->{
            UnbanRequestLazyColumn(
                doubleClickAndDrag =doubleClickAndDrag,
                setDoubleClickAndDragFalse={setDoubleClickAndDragFalse()},
                sortUnbanRequest={status->sortUnbanRequest(status)},
                showUnbanRequestModal={showUnbanRequestModal()},
                updateClickedUnbanRequest={username,text,userId,requestId,status ->updateClickedUnbanRequest(username,text,userId,requestId,status)},
                setDragging = { value -> setDragging(value) },
                immutableUnbanRequestList=immutableUnbanRequestList


            )

        }
        is UnAuthorizedResponse.Failure ->{
            FailedClickToTryAgainBox(
                setDragging={value -> setDragging(value)},
                title = "Unban requests",
                retryRequest={retryUnbanRequests()}
            )

        }
        is UnAuthorizedResponse.Auth401Failure->{
            NewErrorMessage403(
                setDragging = { value -> setDragging(value) },
                title = "Unban requests",
                doubleClickAndDrag =doubleClickAndDrag,
                setDoubleClickAndDragFalse={setDoubleClickAndDragFalse()}
            )

        }

    }

}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UnbanRequestLazyColumn(
    setDragging: (Boolean) -> Unit,
    setDoubleClickAndDragFalse: () -> Unit,
    doubleClickAndDrag: Boolean,
    showUnbanRequestModal:()->Unit,
    updateClickedUnbanRequest: (String, String, String,String,String) -> Unit,
    immutableUnbanRequestList: UnbanRequestItemImmutableCollection,
    sortUnbanRequest:(String)->Unit,

    ){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ){
        stickyHeader{
            Column() {

                LazyColumnStickyClickableHeaderRow(
                    setDragging = { value -> setDragging(value) },
                    title = "Unban requests",
                    doubleClickAndDrag = doubleClickAndDrag,
                    setDoubleClickAndDragFalse = { setDoubleClickAndDragFalse() }
                )
                SortingDropDownMenu(
                    sortUnbanRequest={status->sortUnbanRequest(status)}
                )
            }
        }

        items(immutableUnbanRequestList.list){
            IndivUnbanItem(
                username = it.user_login,
                text = it.text,
                status = it.status,
                time = it.created_at,
                userId=it.user_id,
                requestId = it.id,
                showUnbanRequestModal={showUnbanRequestModal()},
                updateClickedUnbanRequest={username,text,userId,requestId,status ->updateClickedUnbanRequest(username,text,userId,requestId,status)}
            )

        }
        if(immutableUnbanRequestList.list.isEmpty()){
            item{
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Icon(painterResource(id = R.drawable.autorenew_24),contentDescription = "no unban requests",modifier = Modifier.size(35.dp))
                    Text("Ready to receive Unban Requests",color = MaterialTheme.colorScheme.onPrimary.copy(0.8f), fontSize = MaterialTheme.typography.headlineMedium.fontSize)
                    Text("Requests you receive from banned users will display here",color = MaterialTheme.colorScheme.onPrimary.copy(0.8f),fontSize = MaterialTheme.typography.headlineSmall.fontSize)

                }
            }

        }
    }
}

@Composable
fun IndivUnbanItem(
    username:String,
    text:String,
    status:String,
    time:String,
    userId:String,
    requestId:String,
    showUnbanRequestModal: () -> Unit,
    updateClickedUnbanRequest:(String,String,String,String,String)->Unit
){
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable {
                updateClickedUnbanRequest(
                    username, text, userId, requestId,status
                )
                showUnbanRequestModal()
            }
    ) {
        Box(modifier = Modifier.padding(10.dp)){
            Column(){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    ){
                    Icon(
                        painter = painterResource(id =R.drawable.person_outline_24),
                        contentDescription ="user profile",
                        tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(35.dp) )
                    Spacer(modifier = Modifier.width(10.dp))

                    Text(username, fontSize = MaterialTheme.typography.headlineLarge.fontSize, color = Color.White)
                }
                Text("$time: $text", fontSize = MaterialTheme.typography.headlineSmall.fontSize, color = Color.White)
            }
            when(status){
                "pending"->{
                    Text(status,modifier = Modifier.align(Alignment.TopEnd),color = Color.Yellow, fontSize = 13.sp)
                }
                "approved"->{
                    Text(status,modifier = Modifier.align(Alignment.TopEnd),color = Color.Green, fontSize = 13.sp)
                }
                "denied"->{
                    Text(status,modifier = Modifier.align(Alignment.TopEnd),color = Color.Red, fontSize = 13.sp)
                }
                "acknowledged"->{
                    Text(status,modifier = Modifier.align(Alignment.TopEnd),color = Color(0xFF008080), fontSize = 13.sp)
                }
                "canceled"->{
                    Text(status,modifier = Modifier.align(Alignment.TopEnd),color = Color(0xFF7F8C8D), fontSize = 13.sp)
                }
            }


        }
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
    unbanRequests:@Composable () -> Unit,
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
            //The UI is the same as an empty box. However, item can not be overriden and will count as if there is
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
        4 ->{
            Column(modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)) {
                Box(modifier = Modifier.fillMaxSize()){
                    unbanRequests()
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
    unbanRequestChecked:Boolean,
    changeUnbanRequestChecked:(Boolean)->Unit,
    changeModActionsChecked:(Boolean)->Unit,
){
    Log.d("ModViewDrawerContentRecomp","Recomp")
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
            item{
                ElevatedCardSwitchRow(
                    "Unban request",
                    checkIndexAvailability={checkIndexAvailability(4)},
                    painter = painterResource(id = R.drawable.autorenew_24),
                    checked = unbanRequestChecked,
                    changeChecked = {value ->
                        changeUnbanRequestChecked(value)
                    }
                )
            }


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
            checkedValue = checked,
            changeCheckedValue ={value -> changeChecked(value)},
            icon = Icons.Filled.Check,
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

/**
 * - restartable
 *
 * */
@Composable
fun SmallChat(
    twitchUserChat: List<TwitchUserData>, // this is unstable
    showBottomModal:()->Unit,
    updateClickedUser: (String, String, Boolean, Boolean) -> Unit,
    showTimeoutDialog:()->Unit,
    showBanDialog:()->Unit,
    doubleClickMessage:(String)->Unit,
    deleteChatMessage:(String)->Unit,
    isMod: Boolean,
    globalTwitchEmoteContentMap:EmoteListMap,
    channelTwitchEmoteContentMap:EmoteListMap,
    globalBetterTTVEmoteContentMap:EmoteListMap,
    channelBetterTTVEmoteContentMap:EmoteListMap,
    sharedBetterTTVEmoteContentMap:EmoteListMap,
    badgeListMap:EmoteListMap,
    setDragging: (Boolean) -> Unit,
    doubleClickAndDrag:Boolean,
    setDoubleClickAndDragFalse:()->Unit,
    usernameSize:Float,
    messageSize:Float,
    lineHeight:Float,
    useCustomUsernameColors:Boolean

    ){
    val lazyColumnListState = rememberLazyListState()
    var autoscroll by remember { mutableStateOf(true) }
    SmallChatUIBox(
        chatUI = { modifier ->
            SmallChatUILazyColumn(
                lazyColumnListState=lazyColumnListState,
                twitchUserChat=twitchUserChat,
                autoscroll=autoscroll,
                showBottomModal={showBottomModal()},
                showTimeoutDialog={showTimeoutDialog()},
                showBanDialog={showBanDialog()},
                updateClickedUser = {  username, userId,isBanned,isMod ->
                    updateClickedUser(
                        username,
                        userId,
                        isBanned,
                        isMod
                    )
                },
                doubleClickMessage={username ->doubleClickMessage(username)},
                modifier=modifier,
                deleteChatMessage={messageId ->deleteChatMessage(messageId)},
                isMod = isMod,
                setDragging = {value -> setDragging(value)},
                doubleClickAndDrag=doubleClickAndDrag,
                setDoubleClickAndDragFalse={setDoubleClickAndDragFalse()},
                badgeListMap=badgeListMap,
                usernameSize=usernameSize,
                messageSize=messageSize,
                lineHeight=lineHeight,
                useCustomUsernameColors=useCustomUsernameColors,
                globalTwitchEmoteContentMap=globalTwitchEmoteContentMap,
                channelTwitchEmoteContentMap=channelTwitchEmoteContentMap,
                globalBetterTTVEmoteContentMap=globalBetterTTVEmoteContentMap,
                channelBetterTTVEmoteContentMap=channelBetterTTVEmoteContentMap,
                sharedBetterTTVEmoteContentMap=sharedBetterTTVEmoteContentMap

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


/**
 * -restartable
 * - skippable
 * */
@Composable
fun SmallChatScrollToBottom(
    scrollingPaused: Boolean,
    enableAutoScroll: () -> Unit,
    modifier: Modifier
) {
    Log.d("SmallChatScrollToBottomRecomp","RECOMP")

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
/**
 * - restartable
 * - skippable
 *
 * */
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

/**
 * - restartable
 * - skippeable
 * */
@Composable
fun SmallChatDetermineScrollState(
    lazyColumnListState: LazyListState,
    setAutoScrollFalse:()->Unit,
    setAutoScrollTrue:()->Unit,
){
    Log.d("SmallChatDetermineScrollStateRecomp","Recomp")
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

/**
 * - restartable
 * */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SmallChatUILazyColumn(
    lazyColumnListState: LazyListState,
    twitchUserChat: List<TwitchUserData>, //unstable, todo: change to stable
    autoscroll:Boolean,
    showBottomModal:()->Unit,
    showTimeoutDialog:()->Unit,
    showBanDialog:()->Unit,
    updateClickedUser: (String, String, Boolean, Boolean) -> Unit,
    doubleClickMessage:(String)->Unit,
    deleteChatMessage:(String)->Unit,
    modifier: Modifier,
    isMod: Boolean,
    globalTwitchEmoteContentMap:EmoteListMap,
    channelTwitchEmoteContentMap:EmoteListMap,
    globalBetterTTVEmoteContentMap: EmoteListMap,
    channelBetterTTVEmoteContentMap: EmoteListMap,
    sharedBetterTTVEmoteContentMap: EmoteListMap,
    badgeListMap:EmoteListMap,
    setDragging: (Boolean) -> Unit,
    doubleClickAndDrag:Boolean,
    setDoubleClickAndDragFalse:()->Unit,
    usernameSize:Float,
    messageSize:Float,
    lineHeight:Float,
    useCustomUsernameColors:Boolean,



    ){
    val coroutineScope = rememberCoroutineScope()
    val chatUIScope = remember(){ ImprovedChatUI() }
    val hapticFeedback = LocalHapticFeedback.current
    LazyColumn(
        modifier =modifier,
        state = lazyColumnListState
    ){
        coroutineScope.launch {
            if (autoscroll) {
                lazyColumnListState.scrollToItem(twitchUserChat.size)
            }
        }
        stickyHeader {
            ChatHeader(
                setDragging ={newValue ->
                    setDragging(newValue)
                             },
                doubleClickAndDrag =doubleClickAndDrag,
                setDoubleClickAndDragFalse={
                    setDoubleClickAndDragFalse()
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
                        showBottomModal()
                                    },
                    updateClickedUser = {  username, userId,isBanned,isMod ->
                        updateClickedUser(
                            username,
                            userId,
                            isBanned,
                            isMod
                        )
                    },
                    showTimeoutDialog ={
                        showTimeoutDialog()
                                       },
                    showBanDialog={
                        showBanDialog()
                                  },
                    doubleClickMessage={username ->
                        doubleClickMessage(username)
                                       },
                    deleteChatMessage={messageId->
                        deleteChatMessage(messageId)
                                      },
                    isMod = false,
                    badgeListMap=badgeListMap,
                    usernameSize=usernameSize,
                    messageSize=messageSize,
                    lineHeight=lineHeight,
                    useCustomUsernameColors=useCustomUsernameColors,
                    globalTwitchEmoteContentMap=globalTwitchEmoteContentMap,
                    channelTwitchEmoteContentMap=channelTwitchEmoteContentMap,
                    globalBetterTTVEmoteContentMap=globalBetterTTVEmoteContentMap,
                    channelBetterTTVEmoteContentMap=channelBetterTTVEmoteContentMap,
                    sharedBetterTTVEmoteContentMap =sharedBetterTTVEmoteContentMap


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
    modActionListImmutableCollection: ModActionListImmutableCollection,
    doubleClickAndDrag:Boolean,
    setDoubleClickAndDragFalse:()->Unit
){
    //todo: GET THIS LIST FROM THE WEBSOCKET
    Log.d("NewModActionsRecomping","Recomp")


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
                    setDragging={value -> setDragging(value)},
                    title = "MOD ACTIONS: ${modActionListImmutableCollection.modActionList.size}"
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
                            headerText ="MOD ACTIONS: ${modActionListImmutableCollection.modActionList.size} ",
                            setDragging ={newValue ->setDragging(newValue)},
                            doubleClickAndDrag =doubleClickAndDrag,
                            setDoubleClickAndDragFalse={setDoubleClickAndDragFalse()}

                        )
                    }
                    items(modActionListImmutableCollection.modActionList){modAction->
                        ModActionNotificationMessage(
                            title=modAction.title,
                            message=modAction.message,
                            icon = painterResource(id =modAction.iconId),
                            secondaryErrorMessage = modAction.secondaryMessage
                        )
                    }

                    scope.launch {
                        if(autoscroll){
                            listState.scrollToItem(modActionListImmutableCollection.modActionList.size)
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
                                listState.scrollToItem(modActionListImmutableCollection.modActionList.size)
                                autoscroll = true
                            }
                        }
                    )
                }

            }

            is WebSocketResponse.Failure -> {
                //should be a button to retry
                FailedClickToTryAgainBox(
                    setDragging={value -> setDragging(value)},
                    title = "MOD ACTIONS: ${modActionListImmutableCollection.modActionList.size}",
                    retryRequest={}
                )

            }
            is WebSocketResponse.FailureAuth403 -> {
                NewErrorMessage403(
                    setDragging = { value -> setDragging(value) },
                    title = "MOD ACTIONS: ${modActionListImmutableCollection.modActionList.size}",
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
    Log.d("ModActionsHeaderRecomp","RECOMP")
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
/************************ Unban requests ****************************************************************/






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
    autoModMessageListImmutableCollection: AutoModMessageListImmutableCollection,
    manageAutoModMessage:(String,String)-> Unit,
    autoModStatus: WebSocketResponse<Boolean>,
    connectionError: Response<Boolean>,
    reconnect:()->Unit,
    doubleClickAndDrag:Boolean,
    setDoubleClickAndDragFalse:()->Unit

){

    val listState = rememberLazyListState()

    val scope = rememberCoroutineScope()
    var autoscroll by remember { mutableStateOf(true) }
    val interactionSource = listState.interactionSource


    Log.d("NewAutoModQueueBoxRecomp","Recomp")


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
//
        NewErrorBroken(
            setDragging = { value -> setDragging(value) },
            title = "AutoMod Queue",
            doubleClickAndDrag =doubleClickAndDrag,
            setDoubleClickAndDragFalse={setDoubleClickAndDragFalse()}
        )
//
//
//        when(autoModStatus){
//            is WebSocketResponse.Loading->{
//                LoadingIndicator(
//                    setDragging={value -> setDragging(value)},
//                    title = "AutoMod Queue"
//                )
//
//            }
//            is WebSocketResponse.Success->{
//                LazyColumn(
//                    state=listState,
//                    modifier =Modifier.fillMaxSize()
//                ){
//                    scope.launch {
//                        if(autoscroll){
//                            listState.scrollToItem(autoModMessageListImmutableCollection.autoModList.size)
//                        }
//                    }
//                    stickyHeader {
//
//                        AutoModHeader(
//                            setDragging ={newValue -> setDragging(newValue)},
//                            doubleClickAndDrag =doubleClickAndDrag,
//                            setDoubleClickAndDragFalse={setDoubleClickAndDragFalse()}
//                        )
//                    }
//
//
//
//                    items(autoModMessageListImmutableCollection.autoModList){autoModMessage->
//                        AutoModBoxHorizontalDragBox(
//                            manageAutoModMessage={
//                                    messageId,action->manageAutoModMessage(messageId,action)
//                            },
//                            username = autoModMessage.username,
//                            fullText = autoModMessage.fullText,
//                            approved = autoModMessage.approved,
//                            messageCategory = autoModMessage.category,
//                            messageId = autoModMessage.messageId,
//                            swiped = autoModMessage.swiped
//                        )
//                    }
//
//
//
//                }
//                if(!autoscroll){
//                    ScrollToBottomModView(
//                        modifier = Modifier
//                            .align(Alignment.BottomCenter)
//                            .padding(bottom = 20.dp),
//                        enableAutoScroll={
//                            scope.launch {
//                                listState.scrollToItem(autoModMessageListImmutableCollection.autoModList.size)
//                                autoscroll = true
//                            }
//                       }
//                    )
//                }
            }
//            is WebSocketResponse.Failure->{
//                FailedClickToTryAgainBox(
//                    setDragging={value -> setDragging(value)},
//                    title = "AutoMod Queue"
//                )
//
//            }
//            is WebSocketResponse.FailureAuth403->{
//
//                NewErrorMessage403(
//                    setDragging = { value -> setDragging(value) },
//                    title = "AutoMod Queue",
//                    doubleClickAndDrag =doubleClickAndDrag,
//                    setDoubleClickAndDragFalse={setDoubleClickAndDragFalse()}
//                )
//
//            }
//
//        }


//        }
//    ConnectionErrorResponse(
//        connectionError,
//        reconnect ={reconnect()}
//    )


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



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyColumnStickyClickableHeaderRow(
    setDragging: (Boolean) -> Unit,
    title:String,
    setDoubleClickAndDragFalse:() ->Unit,
    doubleClickAndDrag:Boolean
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
}

@Composable
fun SortingDropDownMenu(
    sortUnbanRequest:(String)->Unit
){
    var expanded by remember { mutableStateOf(false) }
    val iconId = if(expanded)R.drawable.baseline_keyboard_arrow_up_24 else R.drawable.keyboard_arrow_down_24
    val contentDescription =if(expanded) "open" else "closed"
    var selectedIndex by remember { mutableStateOf(0) }
    val items = listOf("pending", "approved", "denied", "canceled", "acknowledged")
    Box(modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.TopStart)) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                modifier = Modifier
                    .width(200.dp)
                    .background(Color.DarkGray)
                    .clickable {
                        expanded = !expanded
                    }
                ,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ){
                Text(items[selectedIndex],color = Color.White)

                Icon(painter = painterResource(id =iconId),
                    contentDescription = contentDescription,
                    tint = Color.White,modifier = Modifier.size(30.dp))
            }

        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(300.dp)
        ) {

            items.forEachIndexed{index, name->
                DropdownMenuItem(
                    onClick = {
                        selectedIndex = index
                        expanded = false
                        sortUnbanRequest(items[selectedIndex])
                    },
                    text={
                        Text(name,
                            color = Color.White,
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                    }
                )
            }


        }

    }//end of the box


}

@Composable
fun NewIconTextRow(
    modifier:Modifier,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NewErrorBroken(
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

        NewIconTextRowBroken(
            modifier = Modifier.align(Alignment.Center)
        )

    }
}

@Composable
fun NewIconTextRowBroken(
    modifier:Modifier,
) {

    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
        Icon(painter = painterResource(id =R.drawable.lock_24), contentDescription = "error",tint=MaterialTheme.colorScheme.secondary)
        Text(
            text = "Feature not implemented! Will be available next update. ",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }


}




@Composable
fun ModActionNotificationMessage(
    title:String,
    message:String,
    icon:Painter,
    secondaryErrorMessage:String? = null

){
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp)) {
        Spacer(modifier = Modifier.height(5.dp))

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 0.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(painter = icon, modifier = Modifier.size(25.dp), contentDescription = "message deleted",tint=MaterialTheme.colorScheme.onPrimary)
                Text(text ="  $title", color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineMedium.fontSize,modifier = Modifier.padding(bottom=5.dp))
            }
            Text(text =message, color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineSmall.fontSize,)
            if(secondaryErrorMessage!= null){
                Text(text =secondaryErrorMessage, color = Color.Red.copy(alpha = 0.7f), fontSize = MaterialTheme.typography.headlineSmall.fontSize,)
            }
            Spacer(modifier = Modifier.height(5.dp))

        }
        Spacer(modifier = Modifier
            .height(2.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onPrimary.copy(0.5f)))

    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoadingIndicator(
    setDragging: (Boolean) -> Unit,
    title: String
){
    val hapticFeedback = LocalHapticFeedback.current
    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.primary)){
        Text(
            title,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary) //todo: this is what I want to change
                .combinedClickable(
                    onDoubleClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        setDragging(true)
                    },
                    onClick = {}
                )
                .padding(horizontal = 10.dp)
                .align(Alignment.TopCenter)
        )
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
        )

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FailedClickToTryAgainBox(
    setDragging: (Boolean) -> Unit,
    title:String,
    retryRequest:()->Unit

){
    val hapticFeedback = LocalHapticFeedback.current
    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.primary)){
        Text(
            title,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary) //todo: this is what I want to change
                .combinedClickable(
                    onDoubleClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        setDragging(true)
                    },
                    onClick = {}
                )
                .padding(horizontal = 10.dp)
                .align(Alignment.TopCenter)
        )

        ElevatedButton(
            onClick = {
                retryRequest()
            },
            modifier = Modifier.align(Alignment.Center),
            border = BorderStroke(1.dp,Color.Red),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(painter = painterResource(id =R.drawable.error_outline_24), contentDescription = "error",tint=Color.Red)
                Spacer(modifier = Modifier.width(15.dp))
                Text(
                    text = "Try again",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize
                )
                Spacer(modifier = Modifier.width(15.dp))
                Icon(painter = painterResource(id =R.drawable.error_outline_24), contentDescription = "error",tint=Color.Red)
            }
        }




    }
}

@Composable
fun ScrollToBottomModView(
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
        DualIconsButton(
            buttonAction = { enableAutoScroll() },
            iconImageVector = Icons.Default.ArrowDropDown,
            iconDescription = stringResource(R.string.arrow_drop_down_description),
            buttonText = stringResource(R.string.scroll_to_bottom)

        )
    }
}



@Composable
fun SubscriptionConnectionLoading(){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.7f))){
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}


@Composable
fun ConnectionError(
    message:String,
    reconnect:()->Unit
){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.7f))){
        Column(
            modifier= Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(message,color = Color.Red, fontSize = 25.sp)
            Button(
                onClick ={
                    reconnect()
                },
                colors = ButtonDefaults.buttonColors(containerColor=MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text("Re-connect", color = MaterialTheme.colorScheme.onSecondary)
            }
        }


    }
}

@Composable
fun ClickedUserMessages(

    clickedUsernameChatsWithDateSentImmutable: ClickedUsernameChatsWithDateSentImmutable,
    globalTwitchEmoteContentMap:EmoteListMap,
    channelTwitchEmoteContentMap:EmoteListMap,
    globalBetterTTVEmoteContentMap:EmoteListMap,
    channelBetterTTVEmoteContentMap:EmoteListMap,
    sharedBetterTTVEmoteContentMap:EmoteListMap,
    resolveUnbanRequest: (String, UnbanStatusFilter) -> Unit,
    clickedRequestId:String,
    clickedStatus:String,
    resolutionText:String,
    updateResolutionText: (String) -> Unit
){
    val newMap = globalTwitchEmoteContentMap.map +channelTwitchEmoteContentMap.map + globalBetterTTVEmoteContentMap.map +channelBetterTTVEmoteContentMap.map +sharedBetterTTVEmoteContentMap.map

    // I think it should use this: .weight(1f) instead of the .fillMaxSize()
    Column(
        Modifier
            .fillMaxSize()
            .padding(5.dp)) {

       // Text("", modifier = Modifier.weight(1f))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
//            .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {

            items(clickedUsernameChatsWithDateSentImmutable.clickedChats) { message ->

                val annotatedString = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize
                        )
                    ) {
                        append("${message.dateSent} ")
                    }

                    for (item in message.messageTokenList) {
                        withStyle(
                            style = SpanStyle(
                                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            appendInlineContent("${item.messageValue}", "${item.messageValue} ")
                        }
                    }


                }

                Text(
                    text = annotatedString,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    inlineContent = newMap
                )


            }
        }
        ApproveDenyRow(
            resolveUnbanRequest={status->
                resolveUnbanRequest(clickedRequestId,status)
            },
            modifier = Modifier,
            clickedStatus=clickedStatus,
            resolutionText=resolutionText,
            updateResolutionText={newText->updateResolutionText(newText)}
        )
    }//this is the end of the column
}

@Composable
fun NewContentBanner(
    clickedUsername:String,
    clickedMessage:String,
    clickedStatus:String,
    clickedUserInfo:Response<ClickedUnbanRequestInfo>,
){


    Column {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically){
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = stringResource(R.string.user_icon_description),
                        modifier = Modifier
                            .clickable { }
                            .size(35.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        clickedUsername,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize
                    )
                }
                when(clickedStatus){
                    "pending"->{
                        Text(clickedStatus,color = Color.Yellow, fontSize = 13.sp)
                    }
                    "approved"->{
                        Text(clickedStatus,color = Color.Green, fontSize = 13.sp)
                    }
                    "denied"->{
                        Text(clickedStatus,color = Color.Red, fontSize = 13.sp)
                    }
                    "acknowledged"->{
                        Text(clickedStatus,color = Color(0xFF008080), fontSize = 13.sp)
                    }
                    "canceled"->{
                        Text(clickedStatus,color = Color(0xFF7F8C8D), fontSize = 13.sp)
                    }
                }
            }


        when(clickedUserInfo){
            is Response.Loading ->{
                Row(){
                    Text("Created at: ", fontSize = MaterialTheme.typography.headlineSmall.fontSize,color = MaterialTheme.colorScheme.onPrimary)
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                }

            }
            is Response.Success ->{
                Text("Created at: ${clickedUserInfo.data.profileCreatedAt}", fontSize = MaterialTheme.typography.headlineSmall.fontSize,color = MaterialTheme.colorScheme.onPrimary)

            }
            is Response.Failure ->{
                Row(){
                    Text("Created at: ", fontSize = MaterialTheme.typography.headlineSmall.fontSize,color = MaterialTheme.colorScheme.onPrimary)
                    Icon(painter = painterResource(id =R.drawable.baseline_close_24), contentDescription = "failed request",tint = Color.Red)
                }
            }

        }
        Spacer(modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary))
        Spacer(modifier = Modifier.height(10.dp))
        Text("Unban request message: $clickedMessage", fontSize = MaterialTheme.typography.headlineSmall.fontSize,color = MaterialTheme.colorScheme.onPrimary)
        Spacer(modifier = Modifier.height(10.dp))

    }
}

@Composable
fun ApproveDenyRow(
    resolveUnbanRequest:(UnbanStatusFilter)->Unit,
    modifier:Modifier,
    clickedStatus:String,
    resolutionText:String,
    updateResolutionText: (String) -> Unit
){

    Column(
        modifier =modifier.padding(5.dp)
    ) {
        Text("Add a note to the user (optional)",color = MaterialTheme.colorScheme.onSecondary)

        Row(
            modifier = Modifier.fillMaxWidth(),
        ){
            CustomTextField(
                modifier = Modifier
                    .weight(2f)
                    .align(Alignment.CenterVertically),
                resolutionText=resolutionText,
                updateResolutionText={newText->updateResolutionText(newText)}

            )
            if(clickedStatus !in listOf("approved", "denied","canceled")){
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ){


                    ElevatedCard(
                        modifier = Modifier.clickable {
                            resolveUnbanRequest( UnbanStatusFilter.APPROVED)
                        },

                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.DarkGray
                        )

                    ) {
                        Icon(
                            painter = painterResource(id =R.drawable.baseline_check_24),
                            contentDescription ="approve",
                            tint = Color.Green,
                            modifier = Modifier.size(40.dp)
                        )
                    }


                    ElevatedCard(
                        modifier = Modifier.clickable {
                            resolveUnbanRequest( UnbanStatusFilter.DENIED)
                        },
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.DarkGray
                        )

                    ) {
                        Icon(
                            painter = painterResource(id =R.drawable.baseline_close_24),
                            contentDescription ="deny",
                            tint = Color.Red,
                            modifier = Modifier.size(40.dp)
                        )
                    }


                }
            }



        }
    }


}

@Composable
fun CustomTextField(
    modifier: Modifier,
    resolutionText:String,
    updateResolutionText:(String) ->Unit
){

    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.secondary,
        backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
    )


    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        TextField(
            modifier = modifier,
            singleLine = true,

            value = resolutionText,

            shape = RoundedCornerShape(8.dp),
            onValueChange = {
                if (resolutionText.length <= 480 || it.length < resolutionText.length) {
                    updateResolutionText(it)
                }
                            },
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.White,
                backgroundColor = Color.DarkGray,
                cursorColor = Color.White,
                disabledLabelColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            placeholder = {
                Text("Optional note",color = Color.White)
            }


            )

    }
}

