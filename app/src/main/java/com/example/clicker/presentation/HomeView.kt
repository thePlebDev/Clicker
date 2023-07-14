package com.example.clicker.presentation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.util.findActivity
import kotlinx.coroutines.launch
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import com.example.clicker.BuildConfig
import com.example.clicker.util.Response


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeView(
    loginRequest:() -> Unit ={}, //This will start the implicit intent and send it to the user application
    homeViewModel: HomeViewModel,
    loginWithTwitch:() -> Unit
){
    val hideModal = homeViewModel.state.value.hideModal
    val bottomSheetValue = rememberModalBottomSheetState(ModalBottomSheetValue.Expanded)
//    if(hideModal){
//        LaunchedEffect(key1 = bottomSheetValue){
//            Log.d("GITHUB","LaunchedEffect RECOMP")
//            bottomSheetValue.hide()
//        }
//    }

//    ModalBottom(
//        bottomSheetValue,
//        loginRequest={loginRequest()},
//        userIsLoggedIn = homeViewModel.state.value.userLogginIn,
//        homeViewModel = homeViewModel,
//        profileName = homeViewModel.state.value.userProfile
//    )
    ModalBottomSheetLayout(
        sheetState = bottomSheetValue,
        sheetContent = {
            TwitchLogin(
                loginWithTwitch =loginWithTwitch
            )
        }
    ){

    }

}

@Composable
fun TwitchLogin(
    loginWithTwitch:() -> Unit
){
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
    ){
        Button(onClick ={loginWithTwitch()},modifier = Modifier.align(Alignment.Center)) {
            Text("Login with Twitch", fontSize = 30.sp)
        }

    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModalBottom(
    bottomSheetValue: ModalBottomSheetState,
    loginRequest:() -> Unit, //starts the implicit intent
    userIsLoggedIn:Boolean,
    homeViewModel: HomeViewModel,
    profileName:String?
){

    Box(modifier = Modifier.fillMaxSize()){
        ModalBottomSheetLayout(
            sheetState = bottomSheetValue,
            sheetContent = {
               if(userIsLoggedIn){
                   LoadingLogin(
                       loadingText = homeViewModel.state.value.loadingLoginText,
                       loginStep1 = homeViewModel.state.value.loginStep1,
                       loginStep2 = homeViewModel.state.value.loginStep2,
                       loginStep3 = homeViewModel.state.value.loginStep3,
                   )
               }else{
                   SheetContent(
                       loginRequest={loginRequest()},
                       homeViewModel = homeViewModel
                   )
               }
            }
        ) {
            if(profileName !=null){
                Text("Welcome $profileName", fontSize = 30.sp, modifier = Modifier.align(Alignment.Center))
            }else{
                Text("Please login", fontSize = 30.sp, modifier = Modifier.align(Alignment.Center))
            }



        }
    }


}


@Composable
fun SheetContent(
    loginRequest:() -> Unit,
    homeViewModel: HomeViewModel

){


    Box(modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)){
        Column(
            modifier = Modifier.matchParentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick ={
                    homeViewModel.userIsLogginIn()
                    loginRequest()
                         },
            ) {
                Text("Login with GitHub", fontSize = 20.sp)

            }

        }

    }

}

@Composable
fun LoadingLogin(
    loadingText:String,
    loginStep1:Response<Boolean>?,
    loginStep2:Response<Boolean>?,
    loginStep3:Response<Boolean>?,

){
    val configuration = LocalConfiguration.current

    val widthInDp = configuration.screenWidthDp.dp
    val halfScreenWidth = (widthInDp.value / 3.5).dp

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)){
        Row(modifier =Modifier.matchParentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            LoadingIcon(loginStep1)
            Divider(
                color = Color.Red,
                thickness = 1.dp,
                modifier = Modifier
                    .width(halfScreenWidth)
                    .padding(10.dp)
            )
            LoadingIcon(loginStep2)
            Divider(
                color = Color.Red,
                thickness = 1.dp,
                modifier = Modifier
                    .width(halfScreenWidth)
                    .padding(10.dp)
            )
            LoadingIcon(loginStep3)
        }
        Text(loadingText, modifier = Modifier
            .padding(bottom = 40.dp)
            .align(Alignment.BottomCenter))
    }

}

@Composable
fun LoadingIcon(response:Response<Boolean>?){
    when(response){
        is Response.Loading ->{
            CircularProgressIndicator()
        }
        is Response.Success ->{
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription ="Circle with checkmark indicating a process is complete",
                tint = Color.Green,
                modifier = Modifier.size(35.dp)
            )
        }
        is Response.Failure ->{
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription ="Circle with checkmark indicating a process is complete",
                tint = Color.Red,
                modifier = Modifier.size(40.dp)
            )
        }

        else -> {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription ="Circle with checkmark indicating a process is complete",
                tint = Color.Gray,
                modifier = Modifier.size(40.dp)
            )
        }
    }

}



