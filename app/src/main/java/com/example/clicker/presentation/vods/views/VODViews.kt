package com.example.clicker.presentation.vods.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp


@Composable
fun Vods(
    height:Int
){
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ){
        item{
            IndivVod(
                height = height
            )
        }
        item{
            IndivVod(
                height = height
            )
        }

        item{
            IndivVod(
                height = height
            )
        }

        item{
            IndivVod(
                height = height
            )
        }

        item{
            IndivVod(
                height = height
            )
        }




    }
}

@Composable
fun IndivVod(
    height: Int
){
    val vodHeight = height *2
    Column(modifier = Modifier.padding(horizontal = 5.dp)){
        Box(modifier = Modifier.fillMaxWidth().height(height.dp).background(Color.Red).padding(5.dp)){
            Text("18:13:50",modifier = Modifier.align(Alignment.TopStart))
            Text("697 Views",modifier = Modifier.align(Alignment.BottomStart))
            Text("18 hours ago",modifier = Modifier.align(Alignment.BottomEnd))

        }
        Text("Username",color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineMedium.fontSize)
        Text("Welcome to Twitch development! Here is a quick overview of our products and information to help you get started."
            ,color = MaterialTheme.colorScheme.onPrimary,maxLines = 2, fontSize = MaterialTheme.typography.headlineSmall.fontSize,overflow = TextOverflow.Ellipsis)
    Spacer(modifier =Modifier.height(20.dp) )
    }

}