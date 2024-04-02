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
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextField
//import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
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
import com.example.clicker.presentation.modView.ModViewViewModel
import com.example.clicker.presentation.sharedViews.SharedComponents

import com.example.clicker.presentation.stream.AutoModViewModel
import com.example.clicker.presentation.stream.StreamViewModel
import com.example.clicker.presentation.stream.views.dialogs.CreateNewPollDialog
import com.example.clicker.presentation.stream.views.streamManager.ModView
import com.example.clicker.presentation.stream.views.streamManager.util.ModViewDragSection
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
    autoModViewModel: AutoModViewModel,
    modViewViewModel:ModViewViewModel
) {
    val bottomModalState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val domainIsRegistered = homeViewModel.state.value.domainIsRegistered
    val scope = rememberCoroutineScope()


    val userIsAuthenticated = homeViewModel.validatedUser.collectAsState().value?.clientId != null
    val userId = homeViewModel.validatedUser.collectAsState().value?.userId
    val clientId = homeViewModel.validatedUser.collectAsState().value?.clientId
    val oAuthToken = homeViewModel.state.value.oAuthToken
    val isUserLoggedIn = homeViewModel.state.value.userIsLoggedIn
    val haptic = LocalHapticFeedback.current
    modViewViewModel.hapticFeedBack ={haptic.performHapticFeedback(HapticFeedbackType.LongPress)}



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
    DraggableModViewBox(
        boxOneOffsetY =modViewViewModel.dragStateOffsets.value.boxOneOffsetY,
        setBoxOneOffset={newValue-> modViewViewModel.setBoxOneOffset(newValue)},
        boxOneDragState = modViewViewModel.boxOneDragState,
        boxOneZIndex = modViewViewModel.boxIndexes.value.boxOneZIndex,
        animateToOnDragStop = modViewViewModel.animateToOnDragStop,
        indivBoxSize = modViewViewModel.indivBoxSize,
        sectionBreakPoint = modViewViewModel.sectionBreakPoint,

        boxTwoOffsetY = modViewViewModel.dragStateOffsets.value.boxTwoOffsetY,
        boxTwoZIndex = modViewViewModel.boxIndexes.value.boxTwoZIndex,
        setBoxTwoOffset = {newValue ->modViewViewModel.setBoxTwoOffset(newValue)},
        boxTwoDragState = modViewViewModel.boxTwoDragState,

        boxThreeZIndex = modViewViewModel.boxIndexes.value.boxThreeZIndex,
        boxThreeOffsetY = modViewViewModel.dragStateOffsets.value.boxThreeOffsetY,
        setBoxThreeOffset = {newValue ->modViewViewModel.setBoxThreeOffset(newValue)},
        boxThreeDragState = modViewViewModel.boxThreeDragState,

        boxOneDragging = modViewViewModel.isDragging.value.boxOneDragging,
        setBoxOneDragging = {newValue -> modViewViewModel.setBoxOneDragging(newValue)},

        boxThreeDragging =modViewViewModel.isDragging.value.boxThreeDragging,
        boxTwoDragging =modViewViewModel.isDragging.value.boxTwoDragging,
        setBoxThreeDragging ={newValue -> modViewViewModel.setBoxThreeDragging(newValue)},
        setBoxTwoDragging ={newValue -> modViewViewModel.setBoxTwoDragging(newValue)}
    )


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
private fun DraggableModViewBox(
    boxOneOffsetY:Float,
    boxTwoOffsetY:Float,
    boxThreeOffsetY:Float,

    boxOneDragState: DraggableState,
    boxTwoDragState: DraggableState,
    boxThreeDragState: DraggableState,

    setBoxTwoOffset:(Float) ->Unit,
    setBoxOneOffset:(Float) ->Unit,
    setBoxThreeOffset:(Float) ->Unit,

    boxOneZIndex:Float,
    boxTwoZIndex:Float,
    boxThreeZIndex:Float,
    indivBoxSize:Dp,
    animateToOnDragStop: Float,
    sectionBreakPoint:Int,

    boxOneDragging:Boolean,
    setBoxOneDragging:(Boolean)->Unit,

    boxTwoDragging:Boolean,
    setBoxTwoDragging:(Boolean)->Unit,

    boxThreeDragging:Boolean,
    setBoxThreeDragging:(Boolean)->Unit,

) {

        Box(
            modifier = Modifier.fillMaxSize()
        ){
            /**THIS IS THE FIRST BOX*/
            ModViewDragSection.DraggingBox(
                boxOffsetY =boxOneOffsetY,
                boxDragState=boxOneDragState,
                boxZIndex =boxOneZIndex,
                setBoxOffset ={newValue->setBoxOneOffset(newValue)},
                height = indivBoxSize,
                boxColor =Color.Red,
                sectionBreakPoint =sectionBreakPoint,
                animateToOnDragStop=animateToOnDragStop,
                dragging = boxOneDragging,
                setDragging={newValue->setBoxOneDragging(newValue)},
                content={
                    ModViewDragSection.ChatBox(
                        dragging = boxOneDragging,
                        chatMessageList = ModViewDragSection.fakeMessageDataList,
                        setDragging = {newValue ->setBoxOneDragging(newValue)},
                        triggerBottomModal = {}
                    )
                }

            )


            /*************START OF THE SECOND BOX***********************/
            ModViewDragSection.DraggingBox(
                boxOffsetY =boxTwoOffsetY,
                boxDragState=boxTwoDragState,
                boxZIndex =boxTwoZIndex,
                setBoxOffset ={newValue->setBoxTwoOffset(newValue)},
                height = indivBoxSize,
                boxColor =Color.Cyan,
                sectionBreakPoint =sectionBreakPoint,
                animateToOnDragStop=animateToOnDragStop,
                dragging = boxTwoDragging,
                setDragging={newValue -> setBoxTwoDragging(newValue)},
                content={
                    ModViewDragSection.AutoModQueueBox(
                        dragging =boxTwoDragging,
                        setDragging={newValue -> setBoxTwoDragging(newValue)},
                    )
                }
            )

            /*************START OF THE THIRD BOX***********************/
            ModViewDragSection.DraggingBox(
                boxOffsetY =boxThreeOffsetY,
                boxDragState=boxThreeDragState,
                boxZIndex =boxThreeZIndex,
                setBoxOffset ={newValue->setBoxThreeOffset(newValue)},
                height = indivBoxSize,
                boxColor =Color.Magenta,
                sectionBreakPoint =sectionBreakPoint,
                animateToOnDragStop=animateToOnDragStop,
                dragging = boxThreeDragging,
                setDragging={newValue -> setBoxThreeDragging(newValue)},
                content={
                    ModViewDragSection.ModActions(
                        dragging =boxThreeDragging,
                        setDragging={newValue -> setBoxThreeDragging(newValue)},
                        length =20
                    )
                }


            )
        }

}





