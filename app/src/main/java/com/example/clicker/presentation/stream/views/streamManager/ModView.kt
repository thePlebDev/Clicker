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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

import com.example.clicker.R
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.network.websockets.AutoModQueueMessage
import com.example.clicker.presentation.modView.ModViewDragStateViewModel
import com.example.clicker.presentation.modView.ModViewViewModel
import com.example.clicker.presentation.sharedViews.SharedComponents
import com.example.clicker.presentation.stream.ClickedUIState
import com.example.clicker.presentation.modView.views.SharedBottomModal

import com.example.clicker.presentation.stream.views.isScrolledToEnd
import com.example.clicker.presentation.modView.views.ModViewDragSection

/**
 * ModView contains all the composable functions that are used to create the `chat modes header`
 *
 * @property ModViewScaffold
 * @property SectionHeaderRow
 * @property ModesHeaderRow
 * @property DropDownMenuHeaderBox
 * @property DropdownMenuColumn
 * @property BlockedTermsDropdownMenuItem
 * @property PermittedWordsDropdownMenuItem
 * @property AddSearchPermittedTermsDropdownMenu
 * @property PermittedTermsLazyColumn
 * @property FollowersOnlyCheck
 * @property EmbeddedDropDownMenu
 * @property TextMenuItem
 * @property SlowModeCheck
 * @property SubscriberOnlySwitch
 * @property EmoteOnlySwitch
 * @property DetectDoubleClickSpacer
 * @property DetectDraggingOrNotAtBottomButton
 * */
object ModView {
    /**
     * ModViewScaffold is a `Scaffold` based component that is responsible for showing the user all the information related to
     * the created modView. The ModView shows the user 3 sections. 1) chat, 2) mod actions, 3) automod queue
     *
     *
     * @param closeStreamInfo represents the action needed to be taken when the close button is pressed
     * */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ModViewScaffold(
        closeStreamInfo:()->Unit,
        modViewDragStateViewModel: ModViewDragStateViewModel,
        chatMessages:List<TwitchUserData>,
        clickedUserData: ClickedUIState,
        clickedUserChats:List<String>,
        updateClickedUser: (String, String, Boolean, Boolean) -> Unit,
        timeoutDuration:Int,
        changeTimeoutDuration:(Int)->Unit,
        timeoutReason: String,
        changeTimeoutReason: (String) -> Unit,

        banDuration:Int,
        changeBanDuration:(Int)->Unit,
        banReason:String,
        changeBanReason: (String) -> Unit,
        loggedInUserIsMod:Boolean,
        clickedUserIsMod:Boolean,
        timeoutUser:()->Unit,
        showTimeoutErrorMessage:Boolean,
        setTimeoutShowErrorMessage:(Boolean)->Unit,

        showBanErrorMessage:Boolean,
        setBanShowErrorMessage:(Boolean)->Unit,
        banUser:()->Unit,
        modActionList: List<TwitchUserData>,
        autoModMessageList:List<AutoModQueueMessage>

    ){
        //todo: this is where the draggable boxes go

        val state = rememberModalBottomSheetState(skipPartiallyExpanded =false)
        var showBottomSheet by remember { mutableStateOf(false) }
        val textFieldValue =remember { mutableStateOf(TextFieldValue("Testing")) }


        if(showBottomSheet){
            ModalBottomSheet(
                sheetState = state,
                onDismissRequest = {
                    showBottomSheet = false
                },
                containerColor = MaterialTheme.colorScheme.primary,
                dragHandle= {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .size(30.dp)
                    )

                }
            ){
                SharedBottomModal.ClickedUserBottomModal(
                    bottomModalHeaders = {
                        this.ContentHeaderRow(
                            clickedUsername = clickedUserData.clickedUsername,
                            textFieldValue = textFieldValue,
                            closeBottomModal={showBottomSheet = false}
                        )
                    },
                    bottomModalButtons = {
                        this.ContentBottom(
                            banned =clickedUserData.clickedUsernameBanned,
                            loggedInUserIsMod =loggedInUserIsMod,
                            clickedUserIsMod=clickedUserIsMod,
                            closeBottomModal = { /*TODO*/ },
                            unbanUser = { /*TODO*/ },
                            openTimeoutDialog = { /*TODO*/ },
                            openBanDialog = { /*TODO*/ },
                            shouldMonitorUser = clickedUserData.shouldMonitorUser
                        ) {

                        }
                    },
                    bottomModalRecentMessages={
                        this.ClickedUserMessages(
                            clickedUsernameChats = clickedUserChats
                        )
                    }
                )

            }
        }

        SharedComponents.NoDrawerScaffold(
            topBar = {
                IconTextTopBarRow(
                    icon = {
                        BasicIcon(color = MaterialTheme.colorScheme.onPrimary,
                            imageVector = Icons.Default.Close,
                            contentDescription = "close this section of UI",
                            onClick = {
                                closeStreamInfo()
                            }
                        )
                    },
                    text="Mod View",
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                    horizontalArrangement = Arrangement.SpaceBetween
                )
            },
            bottomBar = {
                //there is no bottom bar intentionally
            }
        ) {contentPadding ->
            Log.d("contentPaddingModView","contentPadding --> ${contentPadding.calculateTopPadding().value}")

            ModViewDragSection.DraggableModViewBox(
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
                setBoxThreeDragging ={newValue -> modViewDragStateViewModel.setBoxThreeDragging(newValue)},
                setBoxTwoDragging ={newValue -> modViewDragStateViewModel.setBoxTwoDragging(newValue)},
                chatMessages =chatMessages,
                triggerBottomModal = {showBottomSheet = true},
                updateClickedUser = {  username, userId,isBanned,isMod ->
                    updateClickedUser(
                        username,
                        userId,
                        isBanned,
                        isMod
                    )
                },
                clickedUserData = clickedUserData,
                timeoutDuration=timeoutDuration,
                changeTimeoutDuration={newValue ->changeTimeoutDuration(newValue)},
                timeoutReason = timeoutReason,
                changeTimeoutReason = {newValue->changeTimeoutReason(newValue)},
                banDuration = banDuration,
                changeBanDuration={newValue ->changeBanDuration(newValue)},
                banReason= banReason,
                changeBanReason = {newValue ->changeBanReason(newValue)},
                timeoutUser = {timeoutUser()},
                showTimeoutErrorMessage= showTimeoutErrorMessage,
                setTimeoutShowErrorMessage ={newValue ->setTimeoutShowErrorMessage(newValue)},
                showBanErrorMessage= showBanErrorMessage,
                setBanShowErrorMessage ={newValue ->setBanShowErrorMessage(newValue)},
                banUser={banUser()},
                modActionList =modActionList,
                autoModMessageList =autoModMessageList

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
    fun DropDownMenuHeaderBox(headerTitle:String){
        var expanded by remember { mutableStateOf(false) }
        //todo: animate the icon change
        Box(){
            DropdownMenuColumn(
                expanded,
                setExpanded ={newValue -> expanded = newValue}
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
        setExpanded:(Boolean)->Unit
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
                setExpanded ={newValue -> setExpanded(newValue)}
            )
            SubscriberOnlySwitch(
                setExpanded ={newValue -> setExpanded(newValue)}
            )
            FollowersOnlyCheck(
                setExpanded ={newValue -> setExpanded(newValue)}
            )
            SlowModeCheck(
                setExpanded ={newValue -> setExpanded(newValue)}
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
                numberOfTermsBanned = 33
            )
            PermittedWordsDropdownMenuItem(
                numberOfTerms = 3,
                permittedWordsExpanded=permittedWordsExpanded,
                changePermittedWordsExpanded={newValue -> permittedWordsExpanded = newValue}
            )


            }
    }

    @Composable
    fun BlockedTermsDropdownMenuItem(
        bannedWordsExpanded:Boolean,
        changeBannedWordsExpanded:(Boolean)->Unit,
        numberOfTermsBanned:Int,
    ){
        //so we need another Item that opens up
        DropdownMenuItem(
            onClick = {},
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



    }


    @Composable
    fun PermittedWordsDropdownMenuItem(

        numberOfTerms:Int,
        permittedWordsExpanded:Boolean,
        changePermittedWordsExpanded:(Boolean)->Unit
    ){

        DropdownMenuItem(
            onClick = {changePermittedWordsExpanded(true)},
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween

                ){
                    Text("Permitted Terms")
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text("$numberOfTerms")
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "")
                    }
                }
            }


        )//end of DropdownMenuItem
        AddSearchPermittedTermsDropdownMenu(
            expanded =permittedWordsExpanded,
            changeExpanded={newValue -> changePermittedWordsExpanded(newValue)}
        )

    }

    @Composable
    fun AddSearchPermittedTermsDropdownMenu(
        expanded:Boolean,
        changeExpanded: (Boolean) -> Unit
    ){
        var text by remember { mutableStateOf("Hello") }
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
                        Text("SEARCH FOR A TERM TO ADD",fontSize = MaterialTheme.typography.headlineMedium.fontSize)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            OutlinedTextField(
                                value = text,
                                onValueChange = { text = it },
                                label = { Text("Label") }
                            )
                            Button(
                                onClick ={},
                                shape = RoundedCornerShape(4),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Text(" Add ",fontSize = MaterialTheme.typography.headlineMedium.fontSize, color = MaterialTheme.colorScheme.onSecondary)
                            }
                        }

                        Text("ACTIVE TERMS (1)",fontSize = MaterialTheme.typography.headlineMedium.fontSize)
                        //todo: MAKE A LAZYCOLUMN OF MAX SIZE
                        PermittedTermsLazyColumn()
                    }
                }
            )
        }
    }

    @Composable
    fun PermittedTermsLazyColumn(){
            LazyColumn(
                modifier =Modifier.size(width =600.dp, height =200.dp)
            ){
                items(20){
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text("Fuck")
                        Row(verticalAlignment = Alignment.CenterVertically){
                            Icon(painter = painterResource(id =R.drawable.edit_24),
                                contentDescription = "edit permitted term",modifier=Modifier.clickable {  })
                            Spacer(modifier =Modifier.width(10.dp))
                            Icon(painter = painterResource(id =R.drawable.delete_outline_24),
                                contentDescription = "delete permitted term",modifier=Modifier.clickable {  })
                        }

                    }
                    Spacer(modifier =Modifier.height(10.dp))
                }

        }


    }


    @Composable
    fun FollowersOnlyCheck(
        setExpanded: (Boolean) -> Unit
    ){
        DropdownMenuItem(
            onClick = {
                setExpanded(false)
            },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Row(verticalAlignment = Alignment.CenterVertically){
                        Icon(imageVector =Icons.Default.Favorite, contentDescription ="Emote icon" )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Followers-only chat", color = MaterialTheme.colorScheme.onPrimary)
                    }
                   // Text("Off", color = MaterialTheme.colorScheme.onPrimary)
                    EmbeddedDropDownMenu(
                        titleList =listOf(
                            "Off","0 minutes(any followers)","10 minutes(most used)",
                            "30 minutes", "1 hour","1 day","1 month","3 months"
                        )
                    )
                }
            }
        )
    }

    @Composable
    fun EmbeddedDropDownMenu(
        titleList: List<String>
    ) {

        var text by remember { mutableStateOf("Off") }
        var expanded by remember {
            mutableStateOf(false)
        }
        var selectedIndex by remember { mutableStateOf(0) }

        Box(modifier = Modifier.wrapContentSize(Alignment.BottomCenter)){

            OutlinedTextField(
                modifier = Modifier
                    .width(200.dp)
                    .clickable {
                        Log.d("OutlinedTextFieldClickking", "CLICK")
                        expanded = true
                    },
                enabled = false,
                value = titleList[selectedIndex],
                onValueChange = { text = it },
                label = {  },
                colors = TextFieldDefaults.colors(
                    disabledTextColor = Color.White,
                    disabledContainerColor = Color.DarkGray,
                    disabledTrailingIconColor = Color.Unspecified,
                    disabledLabelColor = Color.Unspecified,
                    disabledPlaceholderColor = Color.Unspecified,
                    disabledSupportingTextColor = Color.Unspecified,
                    disabledPrefixColor = Color.Unspecified,
                    disabledSuffixColor = Color.Unspecified
                )
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.DarkGray
                    )
            ){
                for (item in titleList){
                    TextMenuItem(
                        setExpanded={newValue -> expanded=newValue},
                        title = item,
                        selectText={selectedIndex =titleList.indexOf(item) }
                    )
                }

            }

        }


    }
    @Composable
    fun TextMenuItem(
        setExpanded: (Boolean) -> Unit,
        selectText:()->Unit,
        title:String,
    ){
        DropdownMenuItem(
            onClick = {
                setExpanded(false)
                selectText()
            },
            text = {
                Text(title, color = MaterialTheme.colorScheme.onPrimary)
                    }
        )
    }

    @Composable
    fun SlowModeCheck(
        setExpanded: (Boolean) -> Unit
    ){

        DropdownMenuItem(
            onClick = {
                setExpanded(false)
            },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Row(verticalAlignment = Alignment.CenterVertically){
                        Icon(painter = painterResource(id =R.drawable.baseline_hourglass_empty_24), contentDescription = "slow mode identifier")
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Slow Mode", color = MaterialTheme.colorScheme.onPrimary)
                    }
                    // Text("Off", color = MaterialTheme.colorScheme.onPrimary)
                    EmbeddedDropDownMenu(
                        titleList =listOf(
                            "Off","3s","5s",
                            "10s", "20s","30s","60s","120s"
                        )
                    )
                }
            }
        )
    }

    @Composable
    fun SubscriberOnlySwitch(
        setExpanded:(Boolean)->Unit,
    ){
        DropdownMenuItem(
            onClick = {
                setExpanded(false)
            },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Row(verticalAlignment = Alignment.CenterVertically){
                        Icon(imageVector =Icons.Default.Person, contentDescription ="Emote icon" )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Subscriber-only chat", color = MaterialTheme.colorScheme.onPrimary)
                    }
                    SwitchMinimalExample()
                }
            }
        )
    }

    @Composable
    fun EmoteOnlySwitch(
        setExpanded:(Boolean)->Unit,
    ){
        DropdownMenuItem(
            onClick = {
                setExpanded(false)
            },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Row(verticalAlignment = Alignment.CenterVertically){
                        Icon(imageVector =Icons.Default.Face, contentDescription ="Emote icon" )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Emotes-only chat", color = MaterialTheme.colorScheme.onPrimary)
                    }
                    SwitchMinimalExample()
                }
            }
        )
    }


    @Composable
    fun SwitchMinimalExample() {
        var checked by remember { mutableStateOf(true) }

        Switch(
            checked = checked,
            onCheckedChange = {
                checked = it
            }
        )
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
        if(!dragging && !listState.isScrolledToEnd()){
            DualIconsButton(
                buttonAction = {
                    scrollToBottomOfList()
                },
                iconImageVector= Icons.Default.ArrowDropDown,
                iconDescription = stringResource(R.string.arrow_drop_down_description),
                buttonText = stringResource(R.string.scroll_to_bottom),
                modifier = modifier

            )
        }
    }


    @Composable
    fun DualIconsButton(
        buttonAction: () -> Unit,
        iconImageVector: ImageVector,
        iconDescription:String,
        buttonText:String,
        modifier:Modifier
    ){
        Button(
            modifier = modifier,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            shape = RoundedCornerShape(4.dp),
            onClick = { buttonAction() }
        ) {
            Icon(
                imageVector = iconImageVector,
                contentDescription = iconDescription,
                tint =  MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier
            )
            Text(buttonText,color =  MaterialTheme.colorScheme.onSecondary,)
            Icon(
                imageVector = iconImageVector,
                contentDescription = iconDescription,
                tint =  MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier
            )
        }
    }
    // I need to create chat for subscribers, non-subscribers and moderators



}