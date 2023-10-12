package com.example.clicker.util

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

@Composable
fun rememberSwipeableActionsState(): SwipeableActionsState {
    return remember { SwipeableActionsState() }
}
/**
 * A class to hold swipe state
 *
 * This class is used to hold the draggable state for the chat swiping ability
 *
 * @constructor Creates an empty group.
 */

@Stable
class SwipeableActionsState internal constructor() {
    /**
     * The current position (in pixels) of a [SwipeableActionsBox].
     */
    val offset: State<Float> get() = offsetState
    private var offsetState = mutableStateOf(0f)
    private var canSwipeTowardsRight =false
    private var canSwipeTowardsLeft= true

    internal val draggableState = DraggableState { delta ->

        val targetOffset = offsetState.value + delta
        val isAllowed = isResettingOnRelease
                || targetOffset > 0f && canSwipeTowardsRight
                || targetOffset < 0f && canSwipeTowardsLeft
        // Add some resistance if needed
        offsetState.value += if (isAllowed) delta else delta / 10
    }
    /**
     * Whether [SwipeableActionsBox] is currently animating to reset its offset after it was swiped.
     */
    var isResettingOnRelease: Boolean by mutableStateOf(false)
        private set
    internal suspend fun resetOffset() {
        draggableState.drag(MutatePriority.PreventUserInput) {
            isResettingOnRelease = true
            try {
                Animatable(offsetState.value).animateTo(targetValue = 0f, tween(durationMillis = 300)) {
                    dragBy(value - offsetState.value)
                }
            } finally {
                isResettingOnRelease = false
            }
        }
    }
}

@Composable
fun rememberPullToRefreshState(): PullRefreshState {
    return remember { PullRefreshState() }
}

class PullRefreshState internal constructor() {
    private val _contentOffset = Animatable(0f)
    /**
     * The current offset for the content, in pixels.
     */
    val contentOffset: Float get() = _contentOffset.value

    /**
     * Dispatch scroll delta in pixels from touch events.
     */
    internal suspend fun dispatchScrollDelta(delta: Float) {
        Log.d("dispatchScrollDelta",_contentOffset.value.toString())
//        if(_contentOffset.value > 60f){
//            _contentOffset.snapTo(_contentOffset.value + (delta * 0.1f))
//        }else{
            _contentOffset.snapTo(_contentOffset.value + delta)
        //}

    }
    internal suspend fun dispatchToResting() {
        _contentOffset.snapTo(0f)
    }



}

