package com.example.clicker.presentation.modChannels

import androidx.compose.runtime.Composable
import com.example.clicker.presentation.home.HomeViewModel
import com.example.clicker.presentation.home.StreamInfo
import com.example.clicker.presentation.modChannels.views.ModChannelComponents


@Composable
fun ModChannelView(
    homeViewModel: HomeViewModel,
    onNavigate: (Int) -> Unit,
){

    ModChannelComponents.MainModView(
        onNavigate ={destination ->onNavigate(destination)},
        height = homeViewModel.state.value.aspectHeight,
        width = homeViewModel.state.value.width,
        density = homeViewModel.state.value.screenDensity,
        offlineModChannelList =homeViewModel.state.value.offlineModChannelList,
        liveModChannelList =homeViewModel.state.value.liveModChannelList,
        modChannelResponseState= homeViewModel.state.value.modChannelResponseState

    )

}