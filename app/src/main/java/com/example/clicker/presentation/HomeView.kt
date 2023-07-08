package com.example.clicker.presentation

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeView(){
    val bottomSheetValue = rememberModalBottomSheetState(ModalBottomSheetValue.Expanded)
    val scope = rememberCoroutineScope()
    ModalBottom(bottomSheetValue)


}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModalBottom(bottomSheetValue: ModalBottomSheetState){

    ModalBottomSheetLayout(
        sheetState = bottomSheetValue,
        sheetContent = { SheetContent() }
    ) {



    }

}


@Composable
fun SheetContent(){
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)){
        Button(
            onClick ={},
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text("Login with GitHub", fontSize = 20.sp)

        }
    }

}