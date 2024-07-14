package com.example.clicker.presentation.authentication.views

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.R
import com.example.clicker.presentation.home.disableClickAndRipple
import androidx.compose.foundation.Image


@Composable
fun GradientStyleBox(
    logo: @Composable (modifier:Modifier) -> Unit,
    headline: @Composable (modifier:Modifier) -> Unit,
    buttons: @Composable (modifier:Modifier) -> Unit,
    loadingIndicator: @Composable (modifier:Modifier) -> Unit,
    errorMessage:@Composable (modifier:Modifier) -> Unit,
){
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
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
        logo(Modifier.align(Alignment.TopCenter))
        headline(Modifier.align(Alignment.Center))
        buttons(Modifier.align(Alignment.BottomEnd))
        loadingIndicator(Modifier.align(Alignment.Center))
        errorMessage(Modifier.align(Alignment.BottomCenter))
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
            Text(text ="You must verify the link before you can login with Twitch",color = Color.White, fontSize = 30.sp)
            VisualDescriptionOfAddingLinks()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VisualDescriptionOfAddingLinks(){
    val pagerState = rememberPagerState(pageCount = {
        3
    })
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.height(200.dp).fillMaxWidth()
    ) { page ->

        // Our page content
        when(page){
            0 ->{
                Box(modifier = Modifier.fillMaxSize()){
                    Image(
                        painter = painterResource(id = R.mipmap.verify_links_start_arrow),
                        contentDescription = "toggle open supported links ",
                        modifier = Modifier.height(200.dp).width(300.dp).align(Alignment.Center)
                    )
                }
            }
            1->{
                Box(modifier = Modifier.fillMaxSize()){
                    Image(
                        painter = painterResource(id = R.mipmap.verify_links_second_arrow),
                        contentDescription = "click add links",
                        modifier = Modifier.height(200.dp).width(300.dp).align(Alignment.Center)
                    )
                }
            }
            2 ->{
                Box(modifier = Modifier.fillMaxSize()){
                    Image(
                        painter = painterResource(id = R.mipmap.verify_links_last_arrow),
                        contentDescription = "confirm add links",
                        modifier = Modifier.height(200.dp).width(300.dp).align(Alignment.Center)
                    )
                }
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
            val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.White
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(12.dp)
            )
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

@Composable
fun LoadingIndicator(
    showLoading:Boolean,
    modifier: Modifier
){
    if(showLoading){
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
            modifier = modifier
                .size(50.dp)
        )
    }
}

@Composable
fun ShowButtonsConditional(
    showLoginWithTwitchButton:Boolean,
    loginWithTwitch: () -> Unit,
    verifyDomain: () -> Unit,
    clickEnabled:Boolean,
    modifier: Modifier
){
    if(showLoginWithTwitchButton){
        LoginWithTwitchButton(
            modifier = modifier,
            loginTwitch = {
                loginWithTwitch()
            },
            clickEnabled=clickEnabled
        )
    }else{
        VerifyDomainButtons(
            modifier = modifier,
            verifyDomain ={verifyDomain()}
        )
    }
}

@Composable
fun VerifyDomainButtons(
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
            Text("Verify the link", color = Color.White, fontSize = 18.sp)
        }
    }

}

@Composable
fun LoginWithTwitchButton(
    modifier: Modifier,
    loginTwitch:()->Unit,
    clickEnabled:Boolean
){
    Column(
        modifier = modifier

            .padding(20.dp)
    ) {
        Button(
            onClick ={loginTwitch()},
            enabled = clickEnabled,
            colors = ButtonDefaults.buttonColors(
                disabledBackgroundColor = ButtonDefaults.buttonColors().backgroundColor(enabled = true).value
            )

        ) {
            Text("Login with Twitch", color = Color.White, fontSize = 18.sp)
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