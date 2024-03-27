package com.example.clicker.presentation.stream.views.streamManager.util

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.clicker.R

@Composable
fun rememberDraggableActions():ModViewDragState{
    return remember {ModViewDragState()}
}

@Stable
class ModViewDragState(){
    val offset: State<Float> get() = offsetX
    private var offsetX = mutableStateOf(0f)

    val width: State<Int> get() = swipeWidth
    private var swipeWidth = mutableStateOf(100)

    val halfWidth get() = (swipeWidth.value/2.5).toInt()
    val quarterWidth get() = swipeWidth.value/4

    fun setWidth(width: Int){
        swipeWidth.value = width
    }

    val draggableState = DraggableState { delta ->
        when {
            offsetX.value >= halfWidth -> offsetX.value += delta / 5
            offsetX.value <= -halfWidth -> offsetX.value += delta / 5
            else -> offsetX.value += delta
        }
    }

    suspend fun resetOffset(){
        Log.d("resetOffset","offsetX --> ${offsetX.value}")
        draggableState.drag(MutatePriority.PreventUserInput) {
            Animatable(offsetX.value).animateTo(
                targetValue = 0f,
                tween(durationMillis = 300)
            ) {
                dragBy(value - offsetX.value)
            }
        }
    }
    fun checkDragThresholdCrossed(
        deleteMessageSwipe:()->Unit,
        timeoutUserSwipe:() ->Unit,
        banUserSwipe:() ->Unit,
    ){

        when {
            offset.value >= halfWidth -> {
                deleteMessageSwipe()
            }
            offset.value <= -halfWidth -> {
                deleteMessageSwipe()
            }
            offset.value >= quarterWidth -> {
                Log.d("checkDragThresholdCrossed","banUserSwipe")
                banUserSwipe()
            }
            offset.value <= -quarterWidth -> {
                Log.d("checkDragThresholdCrossed","timeoutUserSwipe")
                timeoutUserSwipe()
            }
        }
    }

    fun checkQuarterSwipeThresholds(
        leftSwipeAction:()->Unit,
        rightSwipeAction:()->Unit,
    ){
        when {

            offset.value >= quarterWidth -> {
                Log.d("checkDragThresholdCrossed","banUserSwipe")
                rightSwipeAction()
            }
            offset.value <= -quarterWidth -> {
                Log.d("checkDragThresholdCrossed","timeoutUserSwipe")
                leftSwipeAction()
            }
        }

    }
}

/**
 * DragDetectionBox is a [Box] that will detect the user's drag movement and will move [itemBeingDragged] accordingly. Also, depending
 * of if the thresholds are dragged across functions such as [quarterSwipeRightAction], [quarterSwipeLeftAction] and [halfSwipeAction]
 * once the drag stopped. Icons such as [halfSwipeIconResource], [quarterSwipeLeftIconResource] and [quarterSwipeRightIconResource] will
 * also be shown when the user crosses those thresholds
 *
 * @param itemBeingDragged a composable function that will be dragged when the drags it accross the screen.
 * @param twoSwipeOnly a boolean that is used to determine of there are functions for quarter swipes and half swipes or just quarter swipes.
 * A true value indicates that [quarterSwipeRightAction] and [quarterSwipeLeftAction] will get triggered. A false value means that
 * [quarterSwipeRightAction], [quarterSwipeLeftAction] and [halfSwipeAction] will get triggered
 * @param quarterSwipeRightAction is a function that will be called if a user swipes and passes the threshold of 0.25 of [itemBeingDragged] width
 * @param quarterSwipeLeftAction is a function that will be called if a user swipes and passes the threshold of -1*(0.25) of [itemBeingDragged] width
 * @param halfSwipeAction a optional function that will be called if [twoSwipeOnly] is set to false and the user's drag passes
 * the threshold of +/- 0.5 of [itemBeingDragged] width
 * @param halfSwipeIconResource is a [Painter] that will be shown to the user if the half swipe threshold is crossed and [twoSwipeOnly] is false
 * @param quarterSwipeLeftIconResource is a [Painter] that will be shown to the user if the -1 *(quarter) swipe threshold is crossed
 * @param quarterSwipeRightIconResource is a [Painter] that will be shown to the user if the quarter swipe threshold is crossed
 * @param hideIconColor: a [Color] that the icons will be set to hide them from the user
 * @param showIconColor: a [Color] that the icons will be set to reveal them to the user
 * */
@Composable
fun DragDetectionBox(
    itemBeingDragged:@Composable (dragOffset:Float) -> Unit,
    twoSwipeOnly:Boolean,
    quarterSwipeRightAction:()->Unit,
    quarterSwipeLeftAction:()->Unit,
    halfSwipeAction:()->Unit={},
    halfSwipeIconResource: Painter = painterResource(id = R.drawable.delete_outline_24),
    quarterSwipeLeftIconResource: Painter = painterResource(id = R.drawable.time_out_24),
    quarterSwipeRightIconResource: Painter = painterResource(id = R.drawable.ban_24),
    hideIconColor: Color = MaterialTheme.colorScheme.primary,
    showIconColor: Color = MaterialTheme.colorScheme.onPrimary,
){
    var iconPainterResource: Painter = painterResource(id = R.drawable.ban_24)
    var dragging by remember{ mutableStateOf(true) }



    val state = rememberDraggableActions()
    var iconColor = hideIconColor

    if(dragging && !twoSwipeOnly){
        if (state.offset.value >= (state.halfWidth)) {
            iconPainterResource =halfSwipeIconResource
            iconColor = showIconColor
        }
        else if (state.offset.value <= -(state.halfWidth)){
            iconPainterResource =halfSwipeIconResource
            iconColor = showIconColor
        }
        else if (state.offset.value <= -(state.quarterWidth)){
            iconPainterResource =quarterSwipeLeftIconResource
            iconColor = showIconColor
        }
        else if (state.offset.value >= (state.quarterWidth)){
            iconPainterResource = quarterSwipeRightIconResource
            iconColor = showIconColor
        }
    }
    else if(dragging && twoSwipeOnly){
        if (state.offset.value <= -(state.quarterWidth)){
            iconPainterResource =quarterSwipeLeftIconResource
            iconColor = showIconColor
        }
        else if (state.offset.value >= (state.quarterWidth)){
            iconPainterResource = quarterSwipeRightIconResource
            iconColor = showIconColor
        }
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .draggable(
                orientation = Orientation.Horizontal,
                onDragStopped = {
                    if (twoSwipeOnly) {
                        state.checkQuarterSwipeThresholds(
                            leftSwipeAction = {
                                quarterSwipeLeftAction()
                            },
                            rightSwipeAction = {
                                quarterSwipeRightAction()
                            }
                        )
                    } else {
                        state.checkDragThresholdCrossed(
                            deleteMessageSwipe = {
                                halfSwipeAction()
                            },
                            timeoutUserSwipe = {
                                quarterSwipeLeftAction()
                            },
                            banUserSwipe = {
                                quarterSwipeRightAction()
                            }
                        )
                    }

                    dragging = false
                    state.resetOffset()
                },
                onDragStarted = {
                    dragging = true
                },


                enabled = true,
                state = state.draggableState
            )
            .onGloballyPositioned { layoutCoordinates ->
                state.setWidth(layoutCoordinates.size.width)
            }
    ){

        Icon(painter = iconPainterResource, contentDescription = "",tint = iconColor, modifier = Modifier
            .align(Alignment.CenterEnd)
            .padding(end = 10.dp)
        )
        Icon(painter = iconPainterResource, contentDescription = "",tint = iconColor,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 10.dp)
        )


        itemBeingDragged(state.offset.value)

    }


}