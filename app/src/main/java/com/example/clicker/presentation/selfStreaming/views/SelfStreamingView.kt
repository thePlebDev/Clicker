package com.example.clicker.presentation.selfStreaming.views

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.R
import com.example.clicker.presentation.authentication.views.LoadingIndicator
import com.example.clicker.presentation.home.testing3DCode.TestingGLSurfaceViewComposable
import com.example.clicker.presentation.selfStreaming.viewModels.SelfStreamingViewModel
import com.example.clicker.util.NetworkAuthResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelfStreamingView(
    selfStreamingViewModel:SelfStreamingViewModel,
    startStream:()->Unit,
    stopStream:()->Unit,
){
    val streamIsLive = selfStreamingViewModel.streamIsLive.value
    val streamKeyResponse = selfStreamingViewModel.streamKeyResponse.value
    val bottomModalState = rememberModalBottomSheetState()

    var showSheet = selfStreamingViewModel.showBottomModalSheet.value
    val context = LocalContext.current


    Box(
        modifier = Modifier.fillMaxSize()

    ){


        if(streamIsLive){
            LiveUI(
                Modifier.align(Alignment.TopCenter)
            )
            StopButton(
                Modifier.align(Alignment.BottomStart),
                stopStream={stopStream()}
            )
        }else{
            //I need a response that does 4 things
            //DONE 1) loading/waiting for key
            //2) success
            //3) failed due to improper scope authentication
            //4) failed to any other reason

            StartButtonResponse(
                Modifier.align(Alignment.BottomEnd),
                startStream={startStream()},
                streamKeyResponse=streamKeyResponse,
                showSheet=showSheet,
                closeBottomModalSheet={
                    selfStreamingViewModel.setShowBottomModalSheet(false)
                },
                bottomModalState=bottomModalState,
                context=context,

            )
        }




    }
}

@Composable
fun LiveUI(
    modifier: Modifier
){
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Red)
            .padding(5.dp)
    ){
        Text("LIVE", fontSize = 20.sp, color = Color.White)
    }
}

@Composable
fun StopButton(
    modifier: Modifier,
    stopStream:()->Unit
){
    Column(modifier.padding(start = 50.dp)){
        Button(
            onClick ={stopStream()}
        ) {
            Text(text ="Stop")
        }
        Spacer(modifier =Modifier.height(50.dp))
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartButtonResponse(
    modifier: Modifier,
    startStream:()->Unit,
    streamKeyResponse: NetworkAuthResponse<String>,
    showSheet:Boolean,
    closeBottomModalSheet:()->Unit,
    bottomModalState: SheetState,
    context: Context,
){
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        when(streamKeyResponse){
            is NetworkAuthResponse.Loading->{

                StartButtonLoading(
                    modifier,
                    startStream={startStream()},
                )
            }
            is NetworkAuthResponse.Success->{
                StartButton(
                    modifier,
                    startStream={startStream()},
                    enabled=true
                )
            }
            is NetworkAuthResponse.Failure->{
                //
                StartButton(
                    modifier,
                    startStream={startStream()},
                    enabled=true
                )
                Text("Failed",color = Color.Red, fontSize = 50.sp,modifier = Modifier.align(
                    Alignment.Center))
            }
            is NetworkAuthResponse.Auth401Failure->{
                StartButton(
                    modifier,
                    startStream={startStream()},
                    enabled=true
                )
                if (showSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { closeBottomModalSheet() },
                        sheetState = bottomModalState,
                        containerColor= MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                        dragHandle = {
                            TestingGLSurfaceViewComposable(context,Modifier.height(50.dp).width(50.dp))
                        }
                    ) {

                        LoginWithTwitchBottomModalButtonColumn(
                            loginWithTwitch={}
                        )
                    }

                }
            }
            is NetworkAuthResponse.NetworkFailure->{
                StartButton(
                    modifier,
                    startStream={startStream()},
                    enabled=true
                )
                Text("Network error. Try again",color = Color.Red, fontSize = 50.sp,modifier = Modifier.align(
                    Alignment.Center))
            }

        }
    }


}
@Composable
fun StartButton(
    modifier: Modifier,
    startStream:()->Unit,
    enabled: Boolean,
){
    Column(modifier.padding(end = 50.dp)){
        Button(
            onClick ={startStream()},
            enabled=enabled,
        ) {

            Text(text ="Start")

        }
        Spacer(modifier =Modifier.height(50.dp))
    }

}
@Composable
fun StartButtonLoading(
    modifier: Modifier,
    startStream:()->Unit,

){
    Column(modifier.padding(end = 50.dp)){
        Button(
            onClick ={startStream()},
            enabled=false,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically){
                Text(text ="Start")
                Spacer(modifier = Modifier.width(5.dp))
                CircularProgressIndicator(modifier= Modifier.size(20.dp),color= Color.White, strokeWidth = 2.dp)
            }

        }
        Spacer(modifier =Modifier.height(50.dp))
    }

}



/**

 *
 *  **LoginWithTwitchBottomModalButtonColumn** is a composable function representing a Button and text that is shown to the user when they are not logged in
 *
 * @param loginWithTwitch a function, when called, will log the user out
 * */
@Composable
fun LoginWithTwitchBottomModalButtonColumn(
    loginWithTwitch:()->Unit
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(MaterialTheme.colorScheme.primary),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Oops!",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
        )

        Text(
            "A permission error has occurred. Log out and login back in to grant the proper permissions for streaming",
            color = MaterialTheme.colorScheme.onPrimary.copy(0.7f),
            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
            modifier = Modifier
                .padding(bottom = 10.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Button(onClick = { loginWithTwitch() }) {
            Text(text = "Log out of Twitch")
        }
    }
}