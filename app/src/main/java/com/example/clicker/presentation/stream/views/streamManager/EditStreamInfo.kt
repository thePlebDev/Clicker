package com.example.clicker.presentation.stream.views.streamManager

import android.animation.ObjectAnimator
import android.content.res.Resources
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.clicker.R


@Composable
fun EditStreamInfo(
    closeEditStreamInfo:()->Unit
){
    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .background(Color.Red),
    ){
        Button(onClick = {
            closeEditStreamInfo()
        }) {
            Text("ANOTHER ONE", fontSize = 30.sp,color = Color.Yellow)
        }
    }


}