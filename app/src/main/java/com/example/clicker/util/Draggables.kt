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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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

    var isRefreshing by mutableStateOf(false)
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
    internal suspend fun dispatchToMid(delta: Float){
        _contentOffset.snapTo(delta)
    }



}

@Composable
fun rememberNestedScrollConnection(
    scope: CoroutineScope,
    state:PullRefreshState,
    animationMidPoint:Float,
    quarterScreenHeight:Float,
    changeColor: (Color) -> Unit,
    changeIsRefreshing:(Boolean)->Unit,

    changeRequest:(Boolean)->Unit
): PullToRefreshNestedScrollConnection {
    return remember { PullToRefreshNestedScrollConnection(
        scope,state,animationMidPoint,
        quarterScreenHeight,
        changeColor,
        changeRequest,
        changeIsRefreshing
    ) }
}


class PullToRefreshNestedScrollConnection(
    private val scope: CoroutineScope,
    private val state:PullRefreshState,
    private val animationMidPoint:Float,
    private val quarterScreenHeight:Float,
    private val changeColor: (Color) -> Unit,

    private val changeRequest:(Boolean)->Unit,
    private val changeIsRefreshing:(Boolean)->Unit
): NestedScrollConnection {



    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        if(NestedScrollSource.Drag == source && available.y > 0){
            Log.d("REFRESHINGSTATETHINGS","${available.y}")
            if(state.contentOffset >=quarterScreenHeight){
                changeColor(Color.Green)

                changeIsRefreshing(true)


            }
            scope.launch {
                state.dispatchScrollDelta(available.y *0.3f)
            }
        }
        return super.onPostScroll(consumed, available, source)
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        if(state.isRefreshing){
            scope.launch {
                // request = true
                changeRequest(true)
                state.dispatchToMid(animationMidPoint)
            }
        }else{
            scope.launch {
                // request = true
                state.dispatchToResting()
                changeColor(Color.White)
            }
        }

        return super.onPreFling(available)
    }

}


