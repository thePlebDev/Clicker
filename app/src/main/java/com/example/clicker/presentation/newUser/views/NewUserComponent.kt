package com.example.clicker.presentation.newUser.views

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.example.clicker.R
import com.example.clicker.presentation.home.disableClickAndRipple
import com.example.clicker.presentation.logout.LogoutViewModel
import kotlinx.coroutines.launch


@Composable
fun NewUserComponent(
    loginWithTwitch:()-> Unit,
    logoutViewModel: LogoutViewModel,
    navigateToHomeFragment:() ->Unit,
    verifyDomain:() ->Unit
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
        ModderzTagLine(
            modifier = Modifier.align(Alignment.Center),
        )
        VerifyDomainButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            verifyDomain ={verifyDomain()}
        )
//        LoginWithTwitchButton(
//            modifier = Modifier.align(Alignment.BottomEnd),
//            loginTwitch={
//                logoutViewModel.setShowLogin(true)
//                scope.launch {
//
//                    loginWithTwitch()
//                }
//
//            }
//        )
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
fun VerifyDomainButton(
    modifier: Modifier,
    verifyDomain: () -> Unit
){
    Column(
        modifier = modifier

            .padding(20.dp)
    ) {
        Button(
            onClick ={verifyDomain()},
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
        ) {
            Text("Verify the domain", color = Color.White, fontSize = 18.sp)
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
fun ModderzTagLine(
    modifier: Modifier,

){
    Column(modifier= modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text ="Modderz",color = Color.White, fontSize = 40.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text ="You are new here! To comply with Google's Authentication you must first verify the domain, `com.example.modderz` before you can login with Twitch",color = Color.White, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(10.dp))
        TestingImage()

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
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TestingImage(){
    Column(
        modifier = Modifier.fillMaxWidth().height(130.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val pagerState = rememberPagerState(pageCount = {
            3
        })
        HorizontalPager(
            state = pagerState,
            beyondBoundsPageCount = 3
        ) { page ->
            // Our page content

            when(page){
                0 ->{

                    SubcomposeAsyncImage(
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        model = "https://github.com/thePlebDev/Clicker/assets/47083513/d006257a-a800-44bb-b4f1-6f6c1636d3e3",
                        loading = {
                            Column(modifier = Modifier.fillMaxWidth().height(100.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                CircularProgressIndicator(color = Color.Red)
                            }
                        },
                        contentDescription = stringResource(R.string.sub_compose_async_image_description)
                    )

                }
                1 ->{
                    SubcomposeAsyncImage(
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        model = "https://github.com/thePlebDev/Clicker/assets/47083513/415d67a7-65c8-46f7-8755-5502658cdd61",
                        loading = {
                            Column(modifier = Modifier.fillMaxWidth().height(100.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                CircularProgressIndicator(color = Color.Red)
                            }
                        },
                        contentDescription = stringResource(R.string.sub_compose_async_image_description)
                    )
                }
                2 ->{

                    SubcomposeAsyncImage(
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        model = "https://github.com/thePlebDev/Clicker/assets/47083513/f0fac6b7-8dc4-4007-99b3-4e4a0b6ad6bd",
                        loading = {
                            Column(modifier = Modifier.fillMaxWidth().height(100.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                CircularProgressIndicator(color = Color.Red)
                            }
                        },
                        contentDescription = stringResource(R.string.sub_compose_async_image_description)
                    )
                }


            }
        }
        Row(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.White else Color.DarkGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(10.dp)
                )
            }
        }


    }

}