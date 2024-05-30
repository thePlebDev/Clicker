package com.example.clicker.presentation.newUser.views

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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
    verifyDomain:() ->Unit,
    failedHapticFeedback:() ->Unit,
){
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    var showErrorMessage by remember { mutableStateOf(false) }
//

    if (logoutViewModel.newUserNavigateHome.value) {
        Log.d("AnotherOneTestingagain", "value -> ${logoutViewModel.navigateHome.value}")
        logoutViewModel.setNewUserNavigateHome(false)
        navigateToHomeFragment()
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
            showLoginWithTwitchButton = logoutViewModel.showLoginWithTwitchButton.value
        )
        ShowButtonsConditional(
            showLoginWithTwitchButton = logoutViewModel.showLoginWithTwitchButton.value,
            loginWithTwitch = {loginWithTwitch()},
            verifyDomain={verifyDomain()},
            modifier = Modifier.align(Alignment.BottomEnd)
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

            AnimatedVisibility(
                logoutViewModel.showErrorMessage.value,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                ErrorMessage(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    message = logoutViewModel.errorMessage.value
                )
            }


    }
}
@Composable
fun ShowButtonsConditional(
    showLoginWithTwitchButton:Boolean,
    loginWithTwitch: () -> Unit,
    verifyDomain: () -> Unit,
    modifier: Modifier
){
    if(showLoginWithTwitchButton){
        LoginWithTwitchButton(
            modifier = modifier,
            loginTwitch={
                loginWithTwitch()
            }
        )
    }else{
        VerifyDomainButton(
            modifier = modifier,
            verifyDomain ={verifyDomain()}
        )
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

        ) {
            Text("Login with Twitch", color = Color.White, fontSize = 18.sp)
        }
    }
}

@Composable
fun ModderzTagLine(
    modifier: Modifier,
    showLoginWithTwitchButton:Boolean

){
    Column(modifier= modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text ="Modderz",color = Color.White, fontSize = 40.sp)
        Spacer(modifier = Modifier.height(10.dp))
        if(showLoginWithTwitchButton){
            Text(text ="Because mobile moderators deserve love too",color = Color.White, fontSize = 30.sp)
        }else{
            Text(text ="You are new here! To comply with Google's Authentication you must first verify the domain, `com.example.modderz` before you can login with Twitch",color = Color.White, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(10.dp))
            TestingImage()
        }


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
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        model = "https://github.com/thePlebDev/Clicker/assets/47083513/d006257a-a800-44bb-b4f1-6f6c1636d3e3",
                        loading = {
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        model = "https://github.com/thePlebDev/Clicker/assets/47083513/415d67a7-65c8-46f7-8755-5502658cdd61",
                        loading = {
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        model = "https://github.com/thePlebDev/Clicker/assets/47083513/f0fac6b7-8dc4-4007-99b3-4e4a0b6ad6bd",
                        loading = {
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
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
@Composable
fun ErrorMessage(
    modifier: Modifier,
    message:String,
){

        Row(
            modifier = modifier
                .clip(
                    RoundedCornerShape(20.dp)
                )
                .background(Color.Red)
                .padding(vertical = 5.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,


        ) {
            Icon(
                painter = painterResource(id =R.drawable.ic_launcher_foreground),
                contentDescription ="modderz logo",
                modifier = Modifier.size(25.dp),
                tint = Color.White
            )

            Text(
                text =message,
                color = Color.White,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
            )

        }
}