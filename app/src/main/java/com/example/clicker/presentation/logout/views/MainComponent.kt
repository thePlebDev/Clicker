package com.example.clicker.presentation.logout.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.R
import com.example.clicker.presentation.home.disableClickAndRipple
import com.example.clicker.presentation.logout.LogoutViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun MainComponent(
    loginWithTwitch:()-> Unit,
    logoutViewModel: LogoutViewModel,
    navigateToHomeFragment:() ->Unit
){
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    val scope = rememberCoroutineScope()
//

        if (logoutViewModel.navigateHome.value == true) {
            Log.d("AnotherOneTestingagain", "value -> ${logoutViewModel.navigateHome.value}")
            navigateToHomeFragment()
            logoutViewModel.setNavigateHome(false)
        }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Red,
                        Color.Black
                    ),
                    startY = 0f,
                    endY = (screenHeight * 1.6).toFloat()

                )
            )
    ) {
        LogoIcon(modifier = Modifier.align(Alignment.TopCenter))
        ModderzTagLine(modifier = Modifier.align(Alignment.Center))
        LoginWithTwitchButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            loginTwitch={
                logoutViewModel.setShowLogin(true)
                scope.launch {
                   // navigateToHomeFragment()
                    loginWithTwitch()
                }

            }
        )
        Log.d("showLogingState","state -> ${logoutViewModel.showLoading.value}")
        if(logoutViewModel.showLoading.value){
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .disableClickAndRipple()
                    .background(
                        color = Color.Black.copy(alpha = .7f)
                    )
            )

                    CircularProgressIndicator(
                        color = Color.Red,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(50.dp)
                    )
        }
    }
}

@Composable
fun LoginWithTwitchButton(
    modifier: Modifier,
    loginTwitch:()->Unit
){
    Column(
        modifier = modifier

            .padding(20.dp)
    ) {
        Button(
            onClick ={loginTwitch()},
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
        ) {
            Text("Login with Twitch", color = Color.White, fontSize = 18.sp)
        }
    }
}

@Composable
fun ModderzTagLine(modifier: Modifier){
    Column(modifier= modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text ="Modderz",color = Color.White, fontSize = 40.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text ="Because mobile moderators deserve love too",color = Color.White, fontSize = 30.sp)
    }
}

@Composable
fun LogoIcon(
    modifier: Modifier
){
    Column(
        modifier =modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(90.dp))
        Icon(
            tint = Color.White,
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Modderz logo",
            modifier = Modifier
                .size(100.dp)
        )
    }
}