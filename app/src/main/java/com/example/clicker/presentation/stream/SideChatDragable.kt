package com.example.clicker.presentation.stream

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.clicker.util.SwipeableActionsState


@Composable
fun rememberChatSwipeActionsState(): ChatSwipeActionsState {
    return remember { ChatSwipeActionsState() }
}


@Stable
class ChatSwipeActionsState(){

    private var offsetState = mutableStateOf(0f)
    val offset: State<Float> get() = offsetState
    private var canSwipeTowardsRight = true
    private var canSwipeTowardsLeft = true


    internal val draggableState = DraggableState { delta ->
        val targetOffset = offsetState.value + delta
        val isAllowed = isResettingOnRelease ||
                targetOffset > 0f && canSwipeTowardsRight ||
                targetOffset < 0f && canSwipeTowardsLeft
        // Add some resistance if needed
        offsetState.value += if (isAllowed) delta else delta / 10


    }
    var isResettingOnRelease: Boolean by mutableStateOf(false)
        private set

    internal suspend fun resetOffset() {
        draggableState.drag(MutatePriority.PreventUserInput) {
            isResettingOnRelease = true
            try {
                Animatable(offsetState.value).animateTo(
                    targetValue = 0f,
                    tween(durationMillis = 300)
                ) {
                    dragBy(value - offsetState.value)
                }
            } finally {
                isResettingOnRelease = false
            }
        }
    }

}