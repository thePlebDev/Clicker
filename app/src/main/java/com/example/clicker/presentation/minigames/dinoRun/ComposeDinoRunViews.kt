package com.example.clicker.presentation.minigames.dinoRun

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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


    LaunchedEffect(Unit) {
        DinoRunJNI.setOnTextUpdateCallback { newText ->
            value.value = newText
            timerClock.value =0
        }
    }

    LaunchedEffect(timerBoolean.value) {
        while (timerBoolean.value) {
            timerClock.value += 1
            delay(200L)
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


        Box(modifier= Modifier.align(Alignment.TopCenter).padding(top=30.dp)) {
            TextShadowTitle("Score: ${timerClock.value}")
        }






    }


}