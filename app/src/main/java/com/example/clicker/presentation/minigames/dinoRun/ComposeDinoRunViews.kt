package com.example.clicker.presentation.minigames.dinoRun

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.example.clicker.presentation.minigames.PixelContainer
import com.example.clicker.presentation.minigames.TextShadowTitle
import com.example.clicker.presentation.minigames.views.PingPongView
import kotlinx.coroutines.delay


@Composable
fun ComposeDinoRunViews(
    context: Context,
    modifier: Modifier
) {
    // Store a reference to PingPongView
    val dinoRunViewRef = remember { mutableStateOf<DinoRunView?>(null) }
    val value = remember {
        mutableStateOf("START GAME")
    }
    val timerClock = remember {
        mutableStateOf(0)
    }
    val timerBoolean = remember {
        mutableStateOf(true)
    }
    var imageVisible = remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        DinoRunJNI.setOnTextUpdateCallback { newText ->
            value.value = newText
            timerClock.value =0
        }
        DinoRunJNI.setOnSpeedIncreaseCallback {
            imageVisible.value = true
        }
    }
    LaunchedEffect(Unit) {

        DinoRunJNI.setOnSpeedIncreaseCallback {
            imageVisible.value = true
        }
    }


    LaunchedEffect(timerBoolean.value) {
        while (timerBoolean.value) {
            timerClock.value += 1
            delay(200L)
        }
    }
    LaunchedEffect(imageVisible.value) {
        if(imageVisible.value){
            delay(1200)
            imageVisible.value = false
        }
    }


    Box(
        modifier = modifier
    ){
        AndroidView(
            factory = {
                DinoRunView(context).apply {
                    dinoRunViewRef.value = this // Store reference
                }

            },

        )


        Box(modifier= Modifier.align(Alignment.TopCenter).padding(top=30.dp).clickable {
            imageVisible.value = !imageVisible.value
        }) {
            TextShadowTitle("Score: ${timerClock.value}")
        }
        AnimatedVisibility(
            modifier= Modifier.align(Alignment.CenterStart).padding(start = 20.dp,bottom=130.dp),
            visible = imageVisible.value,
            enter = fadeIn(animationSpec = tween(1000)),
            exit = fadeOut(animationSpec = tween(1000))
        ) {
            Box() {
                TextShadowTitle2("SPEED ↑")
            }
        }






    }


}

@Composable
fun TextShadowTitle2(
    text:String
) {
    val offset = Offset(5.0f, 10.0f)

    // Applying retro blocky text style with multiple shadows and custom styling
    Column {
        Text(


            text = text,
            style = TextStyle(
                fontSize = 25.sp,  // Larger font size for retro impact
                fontWeight = FontWeight.Bold,  // Bold font for blocky look
                color = Color.Green,  // Bright color for a retro feel
                letterSpacing = 3.sp,  // Increased letter spacing for a blocky, spaced out look
                shadow = Shadow(
                    color = Color.Red,  // Red shadow for a retro 3D effect
                    offset = Offset(4f, 4f),  // Shadow offset for depth
                    blurRadius = 6f  // Blur radius to create a more retro 3D effect
                ),
            ),
            textAlign = TextAlign.Center
        )

    }
}