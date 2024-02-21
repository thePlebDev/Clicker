package com.example.clicker.presentation.stream.views.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
fun OverlayView(){
    TestingOverlayUI()
}

@Composable
fun TestingOverlayUI(){
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("AlveusSanctuary", fontSize = 25.sp,color = Color.White,maxLines = 1,overflow = TextOverflow.Ellipsis)
        Text("Animal Ambassador 24/7 Live Cams | !alveus !vid !hat !merch !plush", fontSize = 17.sp,color = Color.White,maxLines = 1,overflow = TextOverflow.Ellipsis)
        Text("Animals, Aquariums, and Zoos", fontSize = 17.sp,color = Color.White,maxLines = 1,overflow = TextOverflow.Ellipsis)
        Row(modifier = Modifier.padding(top=5.dp)){
            TagText("English")
            TagText("24HLIVESTREAM")
            TagText("24hour")
            TagText("Animals")
        }
    }

}

@Composable
fun TagText(
    tagText:String
){
    Box(
        Modifier
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 5.dp)){
        Text(
            text =tagText,
            Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.DarkGray)
                .padding(horizontal = 10.dp, vertical = 5.dp)
            ,
            color = Color.White,
            fontSize = 15.sp

            )
    }
}