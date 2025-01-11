package com.example.clicker.presentation.selfStreaming.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.presentation.selfStreaming.viewModels.SelfStreamingViewModel


@Composable
fun SelfStreamingView(
    selfStreamingViewModel:SelfStreamingViewModel,
    startStream:()->Unit,
    stopStream:()->Unit,
){
    val streamIsLive = selfStreamingViewModel.streamIsLive.value
    Box(
        modifier = Modifier.fillMaxSize()

    ){

        if(streamIsLive){
            LiveUI(
                Modifier.align(Alignment.TopCenter)
            )
            StopButton(
                Modifier.align(Alignment.BottomStart),
                stopStream={stopStream()}
            )
        }else{
            StartButton(
                Modifier.align(Alignment.BottomEnd),
                startStream={startStream()}
            )
        }



    }
}

@Composable
fun LiveUI(
    modifier: Modifier
){
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Red)
            .padding(5.dp)
    ){
        Text("LIVE", fontSize = 20.sp, color = Color.White)
    }
}

@Composable
fun StartButton(
    modifier: Modifier,
    startStream:()->Unit
){
    Column(modifier.padding(end = 50.dp)){
        Button(
            onClick ={startStream()}
        ) {
            Text(text ="Start")
        }
        Spacer(modifier =Modifier.height(50.dp))
    }


}
@Composable
fun StopButton(
    modifier: Modifier,
    stopStream:()->Unit
){
    Column(modifier.padding(start = 50.dp)){
        Button(
            onClick ={stopStream()}
        ) {
            Text(text ="Stop")
        }
        Spacer(modifier =Modifier.height(50.dp))
    }


}
