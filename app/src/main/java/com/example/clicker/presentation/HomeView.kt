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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import com.example.clicker.BuildConfig
import com.example.clicker.util.Response


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeView(
    loginRequest:() -> Unit ={},
    homeViewModel: HomeViewModel
){
    val bottomSheetValue = rememberModalBottomSheetState(ModalBottomSheetValue.Expanded)

    ModalBottom(
        bottomSheetValue,
        loginRequest={loginRequest()},
        text = homeViewModel.state.value,
        getToken = { code->homeViewModel.makeGitHubRequest(BuildConfig.CLIENT_ID,BuildConfig.CLIENT_SECRET,code)}
    )


}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModalBottom(
    bottomSheetValue: ModalBottomSheetState,
    loginRequest:() -> Unit,
    getToken:(String) -> Unit,
    text:String?,
){

    Box(modifier = Modifier.fillMaxSize()){
        ModalBottomSheetLayout(
            sheetState = bottomSheetValue,
            sheetContent = {
                //Text("another")
//                SheetContent(
//                    loginRequest={loginRequest()},
//                    getToken = {code -> getToken(code)},
//                    code = text
//                )
                LoadingLogin()
            }
        ) {
            Text(text.toString(), fontSize = 30.sp, modifier = Modifier.align(Alignment.Center))


        }
    }


}


@Composable
fun SheetContent(
    loginRequest:() -> Unit,
    getToken:(String) -> Unit,
    code:String?
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
                onClick ={loginRequest()},
            ) {
                Text("Login with GitHub", fontSize = 20.sp)

            }
            if(code!= null){

                Button(
                    onClick ={getToken(code)},
                ) {
                    Text("GetToken", fontSize = 20.sp)

                }
            }
        }

    }

}

@Composable
fun LoadingLogin(){
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
            LoadingIcon(Response.Success(true))
            Divider(
                color = Color.Red,
                thickness = 1.dp,
                modifier = Modifier
                    .width(halfScreenWidth)
                    .padding(10.dp)
            )
            LoadingIcon(Response.Success(true))
            Divider(
                color = Color.Red,
                thickness = 1.dp,
                modifier = Modifier
                    .width(halfScreenWidth)
                    .padding(10.dp)
            )
            LoadingIcon(Response.Loading)
        }
        Text("Performing R&D on the Japanese crab computer", modifier = Modifier.padding(bottom = 40.dp).align(Alignment.BottomCenter))
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



