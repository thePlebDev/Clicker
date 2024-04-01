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

