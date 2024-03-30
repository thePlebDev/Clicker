package com.example.clicker.presentation.home

import android.annotation.SuppressLint
import android.content.res.Resources
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import coil.compose.SubcomposeAsyncImage
import com.example.clicker.R
import com.example.clicker.presentation.authentication.AuthenticationViewModel
import com.example.clicker.presentation.home.views.HomeViewImplementation
import com.example.clicker.presentation.sharedViews.SharedComponents

import com.example.clicker.presentation.stream.AutoModViewModel
import com.example.clicker.presentation.stream.StreamViewModel
import com.example.clicker.presentation.stream.views.dialogs.CreateNewPollDialog
import com.example.clicker.presentation.stream.views.streamManager.ModView
import com.example.clicker.presentation.stream.views.streamManager.util.Section
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.launch
import kotlin.math.log
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ValidationView(
    homeViewModel: HomeViewModel,
    streamViewModel: StreamViewModel,
    loginWithTwitch: () -> Unit,
    onNavigate: (Int) -> Unit,
    addToLinks: () -> Unit,
    autoModViewModel: AutoModViewModel
) {
    val bottomModalState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val domainIsRegistered = homeViewModel.state.value.domainIsRegistered
    val scope = rememberCoroutineScope()


    val userIsAuthenticated = homeViewModel.validatedUser.collectAsState().value?.clientId != null
    val userId = homeViewModel.validatedUser.collectAsState().value?.userId
    val clientId = homeViewModel.validatedUser.collectAsState().value?.clientId
    val oAuthToken = homeViewModel.state.value.oAuthToken
    val isUserLoggedIn = homeViewModel.state.value.userIsLoggedIn



//    HomeViewImplementation(
//        bottomModalState =bottomModalState,
//        loginWithTwitch ={loginWithTwitch()},
//        domainIsRegistered =domainIsRegistered,
//        addToLinks = { addToLinks() },
//        onNavigate = {id -> onNavigate(id) },
//        updateStreamerName = { streamerName, clientId,broadcasterId,userId->
//            streamViewModel.updateChannelNameAndClientIdAndUserId(
//                streamerName,
//                clientId,
//                broadcasterId,
//                userId,
//                login =homeViewModel.validatedUser.value?.login ?:""
//            )
//            autoModViewModel.updateAutoModCredentials(
//                oAuthToken = homeViewModel.state.value.oAuthToken,
//                clientId = streamViewModel.state.value.clientId,
//                moderatorId = streamViewModel.state.value.userId,
//                broadcasterId = streamViewModel.state.value.broadcasterId,
//            )
//
//        },
//        updateClickedStreamInfo={clickedStreamInfo ->streamViewModel.updateClickedStreamInfo(clickedStreamInfo)  },
//        followedStreamerList = homeViewModel.state.value.streamersListLoading,
//        clientId = clientId ?: "",
//        userId = userId ?: "",
//        height = homeViewModel.state.value.aspectHeight,
//        width = homeViewModel.state.value.width,
//        logout = {
//            homeViewModel.beginLogout(
//                clientId = clientId?:"",
//                oAuthToken = oAuthToken
//            )
//            //homeViewModel.logout()
//            homeViewModel.hideLogoutDialog()
//
//        },
//        userIsAuthenticated =userIsAuthenticated,
//        screenDensity = homeViewModel.state.value.screenDensity,
//        homeRefreshing =homeViewModel.state.value.homeRefreshing,
//        homeRefreshFunc = {homeViewModel.pullToRefreshGetLiveStreams()},
//        networkMessageColor=Color.Red,
//        networkMessage =homeViewModel.state.value.homeNetworkErrorMessage,
//        showNetworkMessage = homeViewModel.state.value.networkConnectionState,
//        logoutDialogIsOpen =homeViewModel.state.value.logoutDialogIsOpen,
//        hideLogoutDialog ={homeViewModel.hideLogoutDialog()},
//        showLogoutDialog ={homeViewModel.showLogoutDialog()},
//        currentUsername = homeViewModel.validatedUser.collectAsState().value?.login ?: "Username not found",
//        isUserLoggedIn=isUserLoggedIn,
//        showFailedDialog = homeViewModel.state.value.showFailedDialog,
//        hideDialog = {homeViewModel.hideDialog()}
//
//    )
    ReworkingModView()


}


fun Modifier.disableClickAndRipple(): Modifier = composed {
    clickable(
        enabled = false,
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = { }
    )
}

@Composable
fun rememberDraggableActions():ModViewDragState{
    return remember {ModViewDragState()}
}

@Stable
class ModViewDragState(){
     val boxOne = "BOXONE"
     val boxTwo = "BOXTWO"
     val boxThree= "BOXTHREE"



    var boxOneZIndex =mutableStateOf(0f)
    var boxTwoZIndex =mutableStateOf(0f)
    var boxThreeZIndex =mutableStateOf(0f)

    var boxOneDragging =mutableStateOf(false)
    var boxTwoDragging =mutableStateOf(false)
    var boxThreeDragging =mutableStateOf(false)

    // all the complex state goes in here
    var boxOneOffsetY = mutableStateOf(0f)
    var boxTwoOffsetY = mutableStateOf(739f)
    var boxThreeOffsetY = mutableStateOf(739f*2)
    var stateList = listOf(boxOne,boxTwo,boxThree)//todo: this doesn't work when there is multiple draggings


    var boxOneDragState =DraggableState { delta ->
        boxOneZIndex.value = 1f
        boxTwoZIndex.value=0f
        boxThreeZIndex.value=0f
        boxOneDragging.value = true
        boxTwoDragging.value = false
        boxThreeDragging.value = false
        val itemInPositionOne = stateList[0]
        val itemInPositionTwo = stateList[1]
        val itemInPositionThree = stateList[2]

       // Log.d("AnotherTherasdf","delta  -> ${delta >0}")

        when{


            boxOneOffsetY.value <539 ->{

                if(itemInPositionOne != boxOne){
                    stateList =listOf(boxOne,itemInPositionOne,itemInPositionThree)

                }

            }
            boxOneOffsetY.value >539 && boxOneOffsetY.value <(539*2) ->{


                if(itemInPositionOne == boxOne){

                    stateList = listOf(itemInPositionTwo,itemInPositionOne,itemInPositionThree)
                    Log.d("itemInPositionOneChecking","boxOne---> $stateList")
                }
                else if(itemInPositionThree == boxOne){

                    stateList = listOf(itemInPositionOne,itemInPositionThree,itemInPositionTwo)
                    Log.d("itemInPositionOneChecking","boxThree---> $stateList")
                }
                else if(itemInPositionTwo == boxOne){
                    stateList = listOf(itemInPositionOne,itemInPositionTwo,itemInPositionThree)
                    Log.d("itemInPositionOneChecking","boxTwo---> $stateList")
                }

            }

            boxOneOffsetY.value >=(539*2)->{

                if(itemInPositionThree != boxOne){
                    stateList = listOf(itemInPositionOne,itemInPositionThree,boxOne)

                }
            }
        }
        boxOneOffsetY.value += delta
    }// end drag state one


    var boxTwoDragState =DraggableState { delta ->
        boxOneZIndex.value = 0f
        boxTwoZIndex.value = 1f
        boxThreeZIndex.value=0f
        boxOneDragging.value = false
        boxTwoDragging.value = true
        boxThreeDragging.value = false
        val itemInPositionOne = stateList[0]
        val itemInPositionTwo = stateList[1]
        val itemInPositionThree = stateList[2]
        when{


            boxTwoOffsetY.value <539 ->{

                if(itemInPositionOne != boxTwo){
                    stateList =listOf(boxTwo,itemInPositionOne,itemInPositionThree)

                }

            }
            boxTwoOffsetY.value >539 && boxTwoOffsetY.value <(539*2) ->{


                if(itemInPositionOne == boxTwo){

                    stateList = listOf(itemInPositionTwo,itemInPositionOne,itemInPositionThree)
                    Log.d("itemInPositionOneChecking","boxOne---> $stateList")
                }
                else if(itemInPositionThree == boxTwo){

                    stateList = listOf(itemInPositionOne,itemInPositionThree,itemInPositionTwo)
                    Log.d("itemInPositionOneChecking","boxThree---> $stateList")
                }
                else if(itemInPositionTwo == boxTwo){
                    stateList = listOf(itemInPositionOne,itemInPositionTwo,itemInPositionThree)
                    Log.d("itemInPositionOneChecking","boxTwo---> $stateList")
                }

            }

            boxTwoOffsetY.value >=(539*2)->{

                if(itemInPositionThree != boxTwo){
                    stateList = listOf(itemInPositionOne,itemInPositionThree,boxTwo)

                }
            }
        }





        boxTwoOffsetY.value += delta
    }
    fun setBoxOneOffset(newValue:Float){
        boxOneOffsetY.value = newValue
    }
    fun setBoxTwoOffset(newValue:Float){
        boxTwoOffsetY.value = newValue
    }
    fun setBoxThreeOffset(newValue:Float){
        boxThreeOffsetY.value = newValue
    }


}

@Composable
fun ReworkingModView(){
    val state = rememberDraggableActions()
     remember(state.stateList) {
         val currentStateList = state.stateList
         val indexOfBoxOne = currentStateList.indexOf(state.boxOne)
         val indexOfBoxTwo = currentStateList.indexOf(state.boxTwo)
         val indexOfBoxThree = currentStateList.indexOf(state.boxThree)

         if(state.boxOneDragging.value){
             if(indexOfBoxTwo == 0){
                 state.setBoxTwoOffset(0f)
             }
             if(indexOfBoxTwo == 1){
                 state.setBoxTwoOffset(739f)
             }
             if(indexOfBoxTwo == 2){
                 state.setBoxTwoOffset(739f *2)
             }
             if(indexOfBoxThree == 0){
                 state.setBoxThreeOffset(0f)
             }
             if(indexOfBoxThree == 1){
                 state.setBoxThreeOffset(739f)
             }
             if(indexOfBoxThree == 2){
                 state.setBoxThreeOffset(739f *2)
             }
         }
         else if(state.boxTwoDragging.value){
             if(indexOfBoxOne == 0){
                 state.setBoxOneOffset(0f)
             }
             if(indexOfBoxOne == 1){
                 state.setBoxOneOffset(739f)
             }
             if(indexOfBoxOne == 2){
                 state.setBoxOneOffset(739f *2)
             }


             if(indexOfBoxThree == 0){
                 state.setBoxThreeOffset(0f)
             }
             if(indexOfBoxThree == 1){
                 state.setBoxThreeOffset(739f)
             }
             if(indexOfBoxThree == 2){
                 state.setBoxThreeOffset(739f *2)
             }
         }




        Log.d("rememberDraggableActionsStateList","${state.stateList}")



    }


    Box(modifier = Modifier.fillMaxSize()){
        DraggableText(
            boxOneOffsetY =state.boxOneOffsetY.value,
            setBoxOneOffset = {newValue -> state.setBoxOneOffset(newValue)},
            boxOneDragState =state.boxOneDragState,
            boxTwoOffsetY = state.boxTwoOffsetY.value,
            boxThreeOffsetY = state.boxThreeOffsetY.value,
            boxTwoDragState = state.boxTwoDragState,
            boxOneZIndex = state.boxOneZIndex.value,
            boxTwoZIndex = state.boxTwoZIndex.value,
            boxThreeZIndex = state.boxThreeZIndex.value,
            setBoxTwoOffset = {newValue -> state.setBoxTwoOffset(newValue)}
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DraggableText(
    boxOneOffsetY:Float,
    boxTwoOffsetY:Float,
    boxThreeOffsetY:Float,
    setBoxOneOffset:(Float) ->Unit,
    boxOneDragState: DraggableState,


    boxTwoDragState: DraggableState,
    setBoxTwoOffset:(Float) ->Unit,

    boxOneZIndex:Float,
    boxTwoZIndex:Float,
    boxThreeZIndex:Float

) {




    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val thirdOfHeight = (Resources.getSystem().displayMetrics.heightPixels/4).dp
    val secondBoxPosition = thirdOfHeight
    val thirdBoxPosition = (secondBoxPosition *2)
    val height = 263.dp


        Box(
            modifier = Modifier.fillMaxSize()
        ){
            /**THIS IS THE FIRST BOX*/
            DraggingItems(
                boxOneOffsetY =boxOneOffsetY,
                boxOneDragState=boxOneDragState,
                boxOneZIndex =boxOneZIndex,
                setBoxOneOffset ={newValue->setBoxOneOffset(newValue)},
                height = height,
                boxColor =Color.Red

            )
            /*************START OF THE SECOND BOX***********************/
            androidx.compose.material3.Card(
                onClick = { /*TODO*/ },
                colors = CardDefaults.cardColors(
                    containerColor = Color.Cyan
                ),
                modifier = Modifier
                    .offset { IntOffset(0, boxTwoOffsetY.roundToInt()) }
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = boxTwoDragState,
                        onDragStopped = {
                            when {
                                boxTwoOffsetY < 539 -> {
                                    setBoxTwoOffset(0f)
                                }
                                boxTwoOffsetY > 539 && boxTwoOffsetY < (539 * 2) -> {
                                    setBoxTwoOffset(739f)
                                }
                                boxTwoOffsetY >= (539 * 2) -> {

                                    setBoxTwoOffset(739F * 2)
                                }
                            }

                        }
                    )
                    .zIndex(boxTwoZIndex)
                    .height(height)
                    .fillMaxWidth()
            ) {

            }
            /*************START OF THE THIRD BOX***********************/
            androidx.compose.material3.Card(
                onClick = { /*TODO*/ },
                colors = CardDefaults.cardColors(
                    containerColor = Color.Magenta
                ),
                modifier = Modifier
                    .offset { IntOffset(0, boxThreeOffsetY.roundToInt()) }
                    .zIndex(0f)
                    .height(height)
                    .fillMaxWidth()
            ) {

            }
        }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DraggingItems(
    boxOneOffsetY: Float,
    boxOneDragState:DraggableState,
    setBoxOneOffset:(Float)->Unit,
    boxOneZIndex:Float,
    height:Dp,
    boxColor:Color
){
    androidx.compose.material3.Card(
        onClick = { /*TODO*/ },
        colors = CardDefaults.cardColors(
            containerColor = boxColor
        ),
        modifier = Modifier
            .offset { IntOffset(0, boxOneOffsetY.roundToInt()) }
            .draggable(
                orientation = Orientation.Vertical,
                state = boxOneDragState,
                onDragStopped = {
                    when {
                        boxOneOffsetY < 539 -> {
                            setBoxOneOffset(0f)
                        }

                        boxOneOffsetY > 539 && boxOneOffsetY < (539 * 2) -> {
                            setBoxOneOffset(739f)
                        }

                        boxOneOffsetY >= (539 * 2) -> {
                            setBoxOneOffset(739F * 2)
                        }
                    }

                }
            )
            .zIndex(boxOneZIndex)
            .height(height)
            .fillMaxWidth()
    ) {

    }
}


