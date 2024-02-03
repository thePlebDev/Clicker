package com.example.clicker.presentation.modChannels.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.R
import com.example.clicker.presentation.home.StreamInfo
import com.example.clicker.presentation.home.views.ScaffoldComponents
import com.example.clicker.util.PullRefreshState
import com.example.clicker.util.PullToRefreshNestedScrollConnection
import kotlinx.coroutines.launch


@Composable
fun MainModChannelView(
    onNavigate: (Int) -> Unit,
    height: Int,
    width: Int,
    density:Float
){
    ScaffoldBuilder(
        onNavigate ={destination -> onNavigate(destination)},
        height, width, density
    )
}

@Composable
fun ScaffoldBuilder(
    onNavigate: (Int) -> Unit,
    height: Int,
    width: Int,
    density:Float
){

    Scaffold(
        backgroundColor= MaterialTheme.colorScheme.primary,


        topBar = {
            CustomTopBar(
                onNavigate ={destination -> onNavigate(destination)}
            )
        },
        bottomBar = {
            CustomBottomBar(
                onNavigate ={destination -> onNavigate(destination)}
            )
        },
    ) { contentPadding ->
        ModChannelsList(
            contentPadding,height, width, density)
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModChannelsList(
    contentPadding: PaddingValues,
    height: Int,
    width: Int,
    density:Float,
    liveList:List<String> = listOf("Bob","Soda","Urza","Poppin"),
    offLineList:List<String> = listOf("Bob","Soda","Urza","Poppin")

){
    LazyColumn(modifier = Modifier.padding(contentPadding)) {
        stickyHeader {
            ModHeader("Live")
        }
        if(liveList.isEmpty()){
            item{
                EmptyList(
                    message ="No live moderated channels found"
                )
            }
        }
        items(liveList){

        }

        // Add 5 items

        stickyHeader {
            ModHeader("Offline")
        }
        if(offLineList.isEmpty()){
            item{
                EmptyList(
                    message ="No offline moderated channels found"
                )
            }
        }


        items(offLineList){channelName ->
            OfflineModChannelItem(
                height,
                width,
                density,
                channelName = channelName
            )
        }



    }
}

@Composable
fun ModHeader(
    headerTitle:String
){
    Text(
        headerTitle,
        color =MaterialTheme.colorScheme.onPrimary,
        fontSize = 20.sp,
        modifier =Modifier.padding(5.dp).background(MaterialTheme.colorScheme.primary)
    )
}



@Composable
fun OfflineModChannelItem(
    //updateStreamerName: (String, String, String, String) -> Unit,
   // streamItem: StreamInfo,
//    clientId: String,
//    userId:String,
//    onNavigate: (Int) -> Unit,
    height: Int,
    width: Int,
    density:Float,
    channelName:String


){
    Row(
        modifier = Modifier.padding(10.dp).clickable {}
    ){
        ExampleBox(height,width, density)
        Column(modifier = Modifier.padding(start = 10.dp)) {
            Text(
                channelName,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

    }
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
    )
}


@Composable
fun EmptyList(
    message:String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .clickable { },
        elevation = 10.dp,
        backgroundColor = MaterialTheme.colorScheme.primary,
        border = BorderStroke(2.dp,MaterialTheme.colorScheme.secondary)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Text(message, fontSize = 20.sp,color = MaterialTheme.colorScheme.onPrimary)

        }
    }
}

@Composable
fun CustomTopBar(
    onNavigate: (Int) -> Unit

) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.ArrowBack,
                "Navigate back to home Fragment",
                modifier = Modifier
                    .size(35.dp)
                    .clickable {
                        onNavigate(R.id.action_modChannelsFragment_to_homeFragment)
                    },
                tint = MaterialTheme.colorScheme.onSecondary
            )
            androidx.compose.material.Text(
                "Channels you mod for",
                fontSize = 25.sp,
                modifier = Modifier.padding(start = 20.dp),
                color = MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}

@Composable
fun CustomBottomBar(
    onNavigate: (Int) -> Unit,
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home Icon",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(35.dp).clickable {
                    onNavigate(R.id.action_modChannelsFragment_to_homeFragment)
                }
            )
            androidx.compose.material.Text("Home", color = MaterialTheme.colorScheme.onPrimary)
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.moderator_secondary_color),
                "Moderation Icon",
                tint= MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(35.dp)
            )
            androidx.compose.material.Text(
                "Mod Channels",
                color = MaterialTheme.colorScheme.onPrimary
            )
        }


    }
}


@Composable
fun ExampleBox(
    height: Int,
    width: Int,
    density:Float
){
    val adjustedHeight = height/density
    val adjustedWidth = width/density
    Column() {
        Box(
            modifier = Modifier.height(adjustedHeight.dp).width(adjustedWidth.dp).clip(RectangleShape).background(Color.DarkGray)
        ){
            Text("Offline",modifier = Modifier.align(Alignment.Center), fontSize = 20.sp,color = Color.White)
        }
        Spacer(modifier=Modifier.height(10.dp))
    }
}