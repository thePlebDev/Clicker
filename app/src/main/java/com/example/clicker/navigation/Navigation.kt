package com.example.clicker.navigation

import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.clicker.presentation.HomeView
import com.example.clicker.presentation.HomeViewModel
import com.google.accompanist.web.WebView

import com.google.accompanist.web.rememberWebViewState

sealed class Screen(val route:String){
    object MainScreen:Screen("main_screen")
    object EmbeddedScreen:Screen("embedded_screen")
}


@Composable
fun Navigation(
    homeViewModel: HomeViewModel,
    loginWithTwitch:() -> Unit
){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.MainScreen.route){
        composable(Screen.MainScreen.route) {
            HomeView(
                homeViewModel = homeViewModel,
                loginWithTwitch = {loginWithTwitch()},
                navController = navController
            )
        }
        composable(
            Screen.EmbeddedScreen.route + "/{channelName}",
            arguments = listOf(
                navArgument("channelName"){
                    type = NavType.StringType
                }
            )
        ) { entry ->
            DetailNav(
                channelName = entry.arguments?.getString("channelName")!!
            )
        }

    }
}

@Composable
fun HomeNav(navController: NavController){
    Column() {
        Text("HOME SCREEN", fontSize = 30.sp)
//        Button(onClick ={navController.navigate(Screen.DetailScreen.route)}) {
//            Text(text = "NAVIGATE TO DETAIL SCREEN")
//        }
    }

}

@Composable
fun DetailNav(
    channelName:String
){
    val url="https://player.twitch.tv/?channel=$channelName&parent=Modderz&muted=false&height=100&width=100"
    val devUrl = "https://dev.to/"

    //val html = "<iframe src=\"${clipUrl}&parent=Modderz}\" height=\"360\" width=\"640\" allowfullscreen/>"
    val channyUrl ="https://player.twitch.tv/?channel=$channelName&parent=twitch.tv"
    val context = LocalContext.current
    val state = rememberWebViewState(url)
    Column() {



        val context = LocalContext.current
        WebView(
            state = state,
            onCreated = { it.settings.javaScriptEnabled = true }
        )

        Text(channelName, fontSize = 30.sp)
    }



}




















