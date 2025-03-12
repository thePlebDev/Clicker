package com.example.clicker.presentation.minigames.dinoRun

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView




@Composable
fun ComposeDinoRunViews(
    context: Context,
    modifier: Modifier
) {
    // Store a reference to PingPongView

    Box(){
        AndroidView(
            factory = {
                DinoRunView(context)
            },
            modifier = modifier
        )




    }


}