package com.example.clicker.presentation.home

import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.work.Operation
import androidx.work.WorkInfo
import coil.compose.AsyncImage
import com.example.clicker.util.Response
import com.example.clicker.R
import com.example.clicker.network.models.AuthenticatedUser
import com.example.clicker.network.models.StreamData
import com.example.clicker.network.models.ValidatedUser
import com.example.clicker.presentation.stream.StreamViewModel
import com.google.gson.Gson


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeView(
    homeViewModel: HomeViewModel,
    streamViewModel: StreamViewModel,
    loginWithTwitch:() -> Unit,
    onNavigate: (Int) -> Unit,
    dataStoreViewModel:DataStoreViewModel,
    workerViewModel:WorkerViewModel
){
    val hideModal = homeViewModel.state.value.hideModal

   // val stating = workerViewModel.another.observeAsState().value
    workerViewModel.liveDataWork?.let {
        val response =it.observeAsState().value
        ObserveAsState(
            response,
            setAuthenticatedUser = {authUser:AuthenticatedUser -> workerViewModel.setAuthenticatedUser(authUser)},
            failedManagerValidation = {workerViewModel.oAuthTokenValidationFailed()}
        )
    }
//    val stating = workerViewModel.validationWorker.observeAsState().value
//    ObserveAsState(stating)
    val bottomSheetValue = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
//    if(hideModal){
//        LaunchedEffect(key1 = bottomSheetValue){
//            Log.d("GITHUB","LaunchedEffect RECOMP")
//            bottomSheetValue.hide()
//        }
//    }

    val validationStatus = dataStoreViewModel.showLogin.value
    val authState = dataStoreViewModel.state.value.authState

    ModalBottomSheetLayout(
        sheetState = bottomSheetValue,
        //TODO: THIS sheetContent WILL GET CHANGED OUT TO BE THE STREAMING VIDEO
        sheetContent = {


        }
    ){

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {Text("Channels you moderate")},
                    modifier = Modifier.padding(bottom =10.dp)
                )
            }
        ){contentPadding->

//            ValidationStatus(
//                validationStatus = validationStatus,
//                contentPadding = contentPadding,
//                loginWithTwitch ={loginWithTwitch()},
//                urlList = dataStoreViewModel.urlList,
//                onNavigate= {dest -> onNavigate(dest)},
//                updateStreamerName ={streamerName ->
//                    streamViewModel.updateChannelName(streamerName)
//                    streamViewModel.startWebSocket(streamerName)
//                },
//                authState = authState
//            )
            ValidationState(
                contentPadding = contentPadding,
                status = workerViewModel.state.value.authStatus,
                validationStatus = workerViewModel.state.value.streamStatus,
                loginWithTwitch ={loginWithTwitch()},
                urlList = dataStoreViewModel.urlList,
                onNavigate= {dest -> onNavigate(dest)},
                updateStreamerName ={streamerName ->
                    streamViewModel.updateChannelName(streamerName)
                    streamViewModel.startWebSocket(streamerName)
                },
            )

        }
        //THIS IS WHAT WILL GET COVERED

    }

}

@Composable
fun ValidationState(
    status:String,
    contentPadding: PaddingValues,
    validationStatus: Response<List<StreamData>>,
    loginWithTwitch:() -> Unit,
    urlList:List<StreamInfo>,
    onNavigate: (Int) -> Unit,
    updateStreamerName: (String) -> Unit,
){
    when(validationStatus){
        is Response.Loading ->{
            Column(){
                CircularProgressIndicator(modifier = Modifier.then(Modifier.size(62.dp)))
                Text(text = status, fontSize = 30.sp,modifier = Modifier.padding(contentPadding))
            }

        }
        is Response.Success ->{

            UrlImages(
                urlList = urlList,
                onNavigate= {dest -> onNavigate(dest)},
                updateStreamerName ={streamerName -> updateStreamerName(streamerName)}
            )
        }
        is Response.Failure ->{
            Column(){
                Text(text = status, fontSize = 30.sp,modifier = Modifier.padding(contentPadding))
                LoginWithTwitch(
                    loginWithTwitch = { loginWithTwitch() }
                )
            }
        }
    }

}



@Composable
fun ObserveAsState(
    workInfo: WorkInfo?,
    setAuthenticatedUser: (AuthenticatedUser) -> Unit,
    failedManagerValidation:()-> Unit
){

    when(workInfo?.state){
        WorkInfo.State.SUCCEEDED ->{
            val serializedValue = workInfo.outputData.getString("result_key")
            val customObject = Gson().fromJson(serializedValue, ValidatedUser::class.java)
            Log.d("OAuthTokenThingy","WorkInfo -->  ${customObject.login}")
            setAuthenticatedUser(
                AuthenticatedUser(
                    customObject.clientId,
                    customObject.userId,
                    customObject.login
                )
            )
            Log.d("ObserveAsStateModel","SUCCEEDED")
        }
        WorkInfo.State.ENQUEUED ->{
            Log.d("ObserveAsStateModel","ENQUEUED")
        }
        WorkInfo.State.RUNNING ->{
            Log.d("ObserveAsStateModel","RUNNING")
        }
        WorkInfo.State.FAILED ->{
            Log.d("ObserveAsStateModel","FAILED")
            failedManagerValidation()
        }
        WorkInfo.State.CANCELLED ->{

        }
        WorkInfo.State.BLOCKED ->{

        }

        else -> {
            //this runs when WorkInfo is null

        }
    }

}

@Composable
fun ValidationStatus(
    validationStatus:Response<Boolean>,
    contentPadding: PaddingValues,
    loginWithTwitch:() -> Unit,
    urlList:List<StreamInfo>,
    onNavigate: (Int) -> Unit,
    updateStreamerName: (String) -> Unit,
    authState:String?
){
    Column(modifier = Modifier
        .padding(contentPadding)
        .fillMaxSize()) {
        when(validationStatus){
            is Response.Loading ->{
                Column(){
                    CircularProgressIndicator(modifier = Modifier.then(Modifier.size(62.dp)))
                    authState?.let{
                        Text(authState)
                    }
                }

            }
            is Response.Success ->{
                UrlImages(
                    urlList = urlList,
                    onNavigate= {dest -> onNavigate(dest)},
                    updateStreamerName ={streamerName -> updateStreamerName(streamerName)}
                )
            }
            is Response.Failure ->{
                Column() {
                    LoginWithTwitch(
                        loginWithTwitch = { loginWithTwitch() }
                    )
                    authState?.let{
                        Text(authState)
                    }
                }

            }
        }
    }


}

@Composable
fun LoginWithTwitch(
    loginWithTwitch:() -> Unit,
){
    Button(onClick ={
        loginWithTwitch()
    }) {
        Text("Login with Twitch")
    }
}

@Composable
fun LoginView(
    loggedIn:Boolean,
    loginWithTwitch:() -> Unit,
    homeViewModel: HomeViewModel,
    changeLoginStatus:()-> Unit,

){
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)){
        if(loggedIn){
            LoadingLogin(
                loadingText = homeViewModel.state.value.loadingLoginText,
                loginStep1 = homeViewModel.state.value.loginStep1,
                loginStep2 = homeViewModel.state.value.loginStep2,
                loginStep3 = homeViewModel.state.value.loginStep3,
            )

        }else{
            TwitchLogin(
                loginWithTwitch ={loginWithTwitch()},
                changeLoginStatus = {changeLoginStatus()},
                modifier = Modifier.matchParentSize()
            )
        }

    }
}

@Composable
fun UrlImages(
    urlList:List<StreamInfo>,
    onNavigate: (Int) -> Unit,
    updateStreamerName: (String) -> Unit,


){

    LazyColumn(modifier = Modifier

        .padding(horizontal = 5.dp)){
        items(urlList){streamItem ->
            Row(modifier = Modifier.clickable {
                updateStreamerName(streamItem.streamerName)
                onNavigate(R.id.action_homeFragment_to_streamFragment)
            }
            ){
                Box() {

                    AsyncImage(
                        model = streamItem.url,
                        contentDescription = null
                    )
                    Text("${streamItem.views}",
                        style = TextStyle(color = Color.White, fontSize = 15.sp,fontWeight = FontWeight.ExtraBold),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(5.dp)
                    )
                }
                Column(modifier = Modifier.padding(start = 10.dp)){
                    Text(streamItem.streamerName, fontSize = 20.sp)
                    Text(streamItem.streamTitle, fontSize = 15.sp,modifier = Modifier.alpha(0.5f), maxLines = 1,overflow = TextOverflow.Ellipsis)
                    Text(streamItem.gameTitle, fontSize = 15.sp,modifier = Modifier.alpha(0.5f))
                }

            }

            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(10.dp))
        }
    }

}



@Composable
fun TwitchLogin(
    loginWithTwitch:() -> Unit, //THis will make the request
    changeLoginStatus:()-> Unit,
    modifier: Modifier

){
    Box(modifier = modifier){
            Button(
                onClick ={
                    changeLoginStatus()
                    loginWithTwitch()
                         },
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text("Login with Twitch", fontSize = 20.sp)

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

@Composable
fun AnotherTesting(){
    //TODO: THIS DOES NOT WORK, FOR THE MOMENT WE ARE MOVING BACK TO THE CLICKABLE MODIFIER
    val context = LocalContext.current
    val html = "<iframe src=\"https://player.twitch.tv/?channel=Sacriel&parent=com.example.modderz\" height=\"400\" width=\"330\" allowfullscreen/>"
   val src="https://player.twitch.tv/?<channel, video, or collection>&parent=streamernews.example.com"
   // val channelName1 = "Robbaz"
    //Log.d("twitchNameonCreateView",channelName)
    val url="https://player.twitch.tv/?channel=Sacriel&parent=modderz"
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(400.dp)){
        AndroidView(

            factory = {
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    webChromeClient = WebChromeClient()
                    settings.javaScriptEnabled = true


                    loadUrl(url)


                }
            }
        )
    }

}


