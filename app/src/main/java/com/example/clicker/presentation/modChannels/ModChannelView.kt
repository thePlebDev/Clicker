package com.example.clicker.presentation.modChannels

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.R
import com.example.clicker.presentation.home.HomeViewModel
import com.example.clicker.presentation.home.StreamInfo
import com.example.clicker.presentation.modChannels.views.ModChannelComponents
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModChannelView(
    homeViewModel: HomeViewModel,
    onNavigate: () -> Unit,
){
    val bottomModalState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    val showModal = homeViewModel.state.value.modChannelShowBottomModal


        Log.d("showModalConditional","showModal --> true")
        LaunchedEffect(showModal) { // the key define when the block is relaunched
            // Your coroutine code here
            Log.d("showModalConditional","showModal --> $showModal")
            when(showModal){
                true ->{
                    scope.launch {
                        bottomModalState.show()
                    }
                }
                false ->{
                    scope.launch {
                        bottomModalState.hide()
                    }
                }
            }

        }


    ModalBottomSheetLayout(
        sheetState = bottomModalState,
        sheetContent = {
            BottomModalSheetContent()
        }
    ) {

        ModChannelComponents.MainModView(
            onNavigate = { onNavigate() },
            height = homeViewModel.state.value.aspectHeight,
            width = homeViewModel.state.value.width,
            density = homeViewModel.state.value.screenDensity,
            offlineModChannelList = homeViewModel.state.value.offlineModChannelList,
            liveModChannelList = homeViewModel.state.value.liveModChannelList,
            modChannelResponseState = homeViewModel.state.value.modChannelResponseState,
            refreshing = homeViewModel.state.value.modRefreshing,
            refreshFunc = {homeViewModel.pullToRefreshModChannels()},
            showNetworkMessage = !homeViewModel.state.value.networkConnectionState

        )
    }

}

@Composable
fun BottomModalSheetContent(){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(MaterialTheme.colorScheme.primary),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Authentication error. Please login with Twitch again",
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(bottom = 10.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Button(
            onClick = {  },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text(
                text = stringResource(R.string.login_with_twitch),
                color = MaterialTheme.colorScheme.onSecondary,fontSize = 20.sp)
        }
    }
}