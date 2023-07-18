package com.example.clicker.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clicker.presentation.HomeView
import com.example.clicker.presentation.HomeViewModel

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
        composable(Screen.EmbeddedScreen.route) { DetailNav() }

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
fun DetailNav(){
    Text("TWITCH VIDEO", fontSize = 30.sp)
}