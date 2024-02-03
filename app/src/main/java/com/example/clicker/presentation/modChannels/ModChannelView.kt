package com.example.clicker.presentation.modChannels

import androidx.compose.runtime.Composable
import com.example.clicker.presentation.home.HomeViewModel
import com.example.clicker.presentation.modChannels.views.MainModChannelView


@Composable
fun ModChannelView(
    homeViewModel: HomeViewModel,
    onNavigate: (Int) -> Unit,
){

    MainModChannelView(
        onNavigate ={destination ->onNavigate(destination)},
        height = homeViewModel.state.value.aspectHeight,
        width = homeViewModel.state.value.width,
        density = homeViewModel.state.value.screenDensity

    )

}