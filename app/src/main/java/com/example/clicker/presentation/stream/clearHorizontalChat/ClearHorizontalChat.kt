package com.example.clicker.presentation.stream.clearHorizontalChat

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
fun ClearHorizontalChatView(){
    var offsetX by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()
    val draggableState = DraggableState { delta ->
        offsetX += delta
    }
    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        Box(modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), 0) }
            .fillMaxHeight()
            .fillMaxWidth(.25f)
            .align(Alignment.CenterEnd)
            .background(Color.Black.copy(alpha = 0.7f))
            .draggable(
                orientation = Orientation.Horizontal,
                state = draggableState,
                onDragStopped = {
                    draggableState.drag(MutatePriority.PreventUserInput) {
                        Animatable(offsetX).animateTo(
                            targetValue = 0f,
                            tween(durationMillis = 300)
                        ) {
                            dragBy(value - offsetX)
                        }
                    }

                }
            )
        ){

        }
        // Your content here
    }
}


