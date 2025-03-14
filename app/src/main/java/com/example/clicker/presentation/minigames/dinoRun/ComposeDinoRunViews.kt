package com.example.clicker.presentation.minigames.dinoRun

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.clicker.presentation.minigames.PixelContainer
import com.example.clicker.presentation.minigames.TextShadowTitle
import com.example.clicker.presentation.minigames.views.PingPongView


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

    LaunchedEffect(Unit) {
        DinoRunJNI.setOnTextUpdateCallback { newText ->
            value.value = newText
        }
    }


    Box(){
        AndroidView(
            factory = {
                DinoRunView(context).apply {
                    dinoRunViewRef.value = this // Store reference
                }

            },
            modifier = modifier
        )

        Box(
            modifier = Modifier.align(Alignment.Center).clickable {
              //  DinoRunJNI.triggerUpdate() // Call C++ to trigger the update
            }
        ) {


                TextShadowTitle(value.value)

        }


    }


}