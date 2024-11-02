package com.example.clicker.presentation.stream.clearHorizontalChat

import android.content.res.Resources
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.presentation.stream.StreamViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
fun ClearHorizontalChatView(
    streamViewModel:StreamViewModel
){
    val twitchUserChat = streamViewModel.listChats.toList()
    DraggableClearChat(
        (streamViewModel.fullImmersionWidth.value)*-1,
        twitchUserChat=twitchUserChat,
    )

}

@Composable
fun DraggableClearChat(
    fullImmersionWidth:Int,
    twitchUserChat: List<TwitchUserData>,
){
    var offsetX by remember { mutableStateOf(0f) }

    val maxWidthHalf = (Resources.getSystem().displayMetrics.widthPixels/2.5)*-1
    var clearChatWidth by remember { mutableStateOf(0) }
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
            .draggable(
                orientation = Orientation.Horizontal,
                state = draggableState,
                onDragStopped = {

                    Log.d("TestingMaxWidthClear","threshold crossed-->${offsetX<=maxWidthHalf}")
                    if(offsetX<=maxWidthHalf){
                        draggableState.drag(MutatePriority.PreventUserInput) {
                            Animatable(offsetX).animateTo(
                                targetValue = (fullImmersionWidth + clearChatWidth).toFloat(),
                                tween(durationMillis = 300)
                            ) {
                                dragBy(value - offsetX)
                            }
                        }

                    }else{
                        draggableState.drag(MutatePriority.PreventUserInput) {
                            Animatable(offsetX).animateTo(
                                targetValue = 0f,
                                tween(durationMillis = 300)
                            ) {
                                dragBy(value - offsetX)
                            }
                        }
                    }


                }
            ).onGloballyPositioned {
                clearChatWidth = it.size.width
            }

        ){
            // this is where the chat needs to go
            ClearChatLazyColumn(
                twitchUserChat=twitchUserChat
            )

        }

    }
}
@Composable
fun ClearChatLazyColumn(
    twitchUserChat: List<TwitchUserData>,
){
    var autoscroll by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val lazyColumnListState = rememberLazyListState()
    LazyColumn(
        modifier =Modifier.fillMaxSize().padding(10.dp),
        state = lazyColumnListState
    ){
        coroutineScope.launch {
            if (autoscroll) {
                lazyColumnListState.scrollToItem(twitchUserChat.size)
            }
        }
        items(twitchUserChat){twitchUser->
            Text("${twitchUser.displayName} : ${twitchUser.userType}")
        }

    }

}