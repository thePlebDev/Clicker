package com.example.clicker.presentation.stream.views.overlays

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun VerticalOverlayView(
    channelName:String,
    streamTitle:String,
    category:String,
    tags:List<String>,
    showStreamDetails:Boolean
){
    AnimatedVisibility(
        visible = showStreamDetails,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(durationMillis = 300)
        ),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(durationMillis = 300)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().background(Color.Black)
        ){
            VerticalTestingOverlayUI(
                channelName, streamTitle, category, tags
            )
        }
    }




}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VerticalTestingOverlayUI(
    channelName:String,
    streamTitle:String,
    category: String,
    tags:List<String>
){

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp)) {
        Text(
            channelName,
            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
            color = Color.White,

        )
        Text(
            streamTitle,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            color = Color.White,
            lineHeight = MaterialTheme.typography.headlineSmall.fontSize,

        )
        Text(
            category,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.padding(vertical = 5.dp)
        )
        FlowRow(
            modifier = Modifier.background(Color.Transparent).padding(bottom = 10.dp)
        ) {
            tags.forEach { tagTitle ->
                VerticalTagText(tagTitle)
            }
        }

    }

}

@Composable
fun VerticalTagText(
    tagText:String
){
    Box(
        Modifier
            .background(Color.Transparent)
            .padding(3.dp)
    ){
        Text(
            text =tagText,
            Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.DarkGray)
                .padding(horizontal = 10.dp, vertical = 5.dp)
            ,
            color = Color.White,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize

        )
    }
}