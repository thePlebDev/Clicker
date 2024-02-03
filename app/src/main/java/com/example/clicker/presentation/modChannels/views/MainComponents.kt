package com.example.clicker.presentation.modChannels.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.R
import com.example.clicker.util.PullRefreshState
import com.example.clicker.util.PullToRefreshNestedScrollConnection
import kotlinx.coroutines.launch


@Composable
fun ModChannelView(
    onNavigate: (Int) -> Unit,
){
    ScaffoldBuilder(
        onNavigate ={destination -> onNavigate(destination)}
    )
}

@Composable
fun ScaffoldBuilder(onNavigate: (Int) -> Unit,){

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .background(MaterialTheme.colorScheme.primary)
        ) {


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