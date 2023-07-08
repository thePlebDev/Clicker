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
import androidx.compose.foundation.layout.fillMaxSize


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeView(
    loginRequest:() -> Unit ={},
    homeViewModel: HomeViewModel
){
    val bottomSheetValue = rememberModalBottomSheetState(ModalBottomSheetValue.Expanded)
    val scope = rememberCoroutineScope()
    ModalBottom(
        bottomSheetValue,
        loginRequest={loginRequest()},
        text = homeViewModel.state.value
    )


}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModalBottom(
    bottomSheetValue: ModalBottomSheetState,
    loginRequest:() -> Unit,
    text:String,
){

    Box(modifier = Modifier.fillMaxSize()){
        ModalBottomSheetLayout(
            sheetState = bottomSheetValue,
            sheetContent = { SheetContent(loginRequest={loginRequest()}) }
        ) {
            Text(text, fontSize = 30.sp, modifier = Modifier.align(Alignment.Center))


        }
    }


}


@Composable
fun SheetContent(loginRequest:() -> Unit){

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)){
        Button(
            onClick ={loginRequest()},
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text("Login with GitHub", fontSize = 20.sp)

        }
    }

}


@Composable
fun Thingers(context:Activity){
    val context = LocalContext.current
    val activity = context.findActivity()
    val uri:Uri? = activity.intent.data
    if(uri != null){
        Log.d("GITHUB",uri.toString())
    }
}

