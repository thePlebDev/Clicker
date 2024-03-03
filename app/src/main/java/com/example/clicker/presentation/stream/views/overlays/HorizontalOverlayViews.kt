package com.example.clicker.presentation.stream.views.overlays

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.example.clicker.presentation.stream.StreamViewModel


@Composable
fun HorizontalOverlayView(
    streamViewModel: StreamViewModel,

){
    val channelName = streamViewModel.clickedStreamInfo.value.channelName
    val streamTitle = streamViewModel.clickedStreamInfo.value.streamTitle
    val category = streamViewModel.clickedStreamInfo.value.category
    val tags = streamViewModel.clickedStreamInfo.value.tags

    TestingOverlayUI(channelName, streamTitle, category,tags)
}

@Composable
fun TestingOverlayUI(
    channelName:String,
    streamTitle:String,
    category: String,
    tags:List<String>
){
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp)) {
        Text(channelName, fontSize = MaterialTheme.typography.headlineLarge.fontSize,color = Color.White,maxLines = 1,overflow = TextOverflow.Ellipsis)
        Text(streamTitle, fontSize = MaterialTheme.typography.headlineSmall.fontSize,color = Color.White, lineHeight = MaterialTheme.typography.headlineSmall.fontSize,maxLines = 2,overflow = TextOverflow.Ellipsis)
        Text(category, fontSize = MaterialTheme.typography.headlineSmall.fontSize,color = Color.White.copy(alpha = 0.8f),maxLines = 1,overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(vertical = 5.dp))
        LazyRow(
            modifier = Modifier.background(Color.Transparent),

        ){
            items(tags){
                TagText(it)
            }

        }

    }

}

@Composable
fun TagText(
    tagText:String
){
    Box(
        Modifier
            .background(Color.Transparent)
            .padding(horizontal = 3.dp)
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