package com.example.clicker.presentation.vods.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.clicker.network.clients.VOD


@Composable
fun Vods(
    height:Int,
    vodList:List<VOD>
){
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ){
        items(vodList){vod ->
            IndivVod(
                height = height,
                username = vod.user_name,
                description = vod.title,
                viewCount = vod.view_count,
                duration = vod.duration
            )
        }

    }
}

@Composable
fun IndivVod(
    height: Int,
    username:String,
    description:String,
    viewCount:Int,
    duration:String
){

    Column(modifier = Modifier.padding(horizontal = 5.dp)){
        Box(modifier = Modifier.fillMaxWidth().height(height.dp).background(Color.Red).padding(5.dp)){
            Text(duration,modifier = Modifier.align(Alignment.TopStart))
            Text("$viewCount Views",modifier = Modifier.align(Alignment.BottomStart))
            Text("18 hours ago",modifier = Modifier.align(Alignment.BottomEnd))

        }
        Text(username,color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineMedium.fontSize)
        Text(description
            ,color = MaterialTheme.colorScheme.onPrimary,maxLines = 2, fontSize = MaterialTheme.typography.headlineSmall.fontSize,overflow = TextOverflow.Ellipsis)
        Spacer(modifier =Modifier.height(20.dp) )
    }

}