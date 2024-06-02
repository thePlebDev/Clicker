package com.example.clicker.presentation.authentication.logout.views

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
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
import com.example.clicker.presentation.authentication.logout.LogoutViewModel
import com.example.clicker.presentation.authentication.views.ErrorMessage
import com.example.clicker.presentation.authentication.views.GradientStyleBox
import com.example.clicker.presentation.authentication.views.LoadingIndicator
import com.example.clicker.presentation.authentication.views.LogoIcon
import com.example.clicker.presentation.authentication.views.ModderzTagLine
import com.example.clicker.presentation.authentication.views.ShowButtonsConditional
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun LogoutMainComponent(
    loginWithTwitch:()-> Unit,
    logoutViewModel: LogoutViewModel,
    navigateToHomeFragment:() ->Unit,
    verifyDomain:() ->Unit,
){

    val loggedInStatus = logoutViewModel.loggedOutStatus.value == "WAITING"

    NavigationComposable(
        navigateHome =logoutViewModel.navigateHome.value == true,
        navigateToHomeFragment ={navigateToHomeFragment()},
        setNavigationFalse = {logoutViewModel.setNavigateHome(false)},
        loginWithTwitch ={loginWithTwitch()},
        navigateToTwitch = logoutViewModel.navigateToLoginWithTwitch.value,
    )
    GradientStyleBox(
        logo = {modifier ->
            LogoIcon(modifier = modifier)
        },
        headline ={modifier ->
            ModderzTagLine(
                modifier = modifier,
                showLoginWithTwitchButton= logoutViewModel.showLoginWithTwitchButton.value
            )
        },
        buttons={modifier ->
            ShowButtonsConditional(
                showLoginWithTwitchButton = logoutViewModel.showLoginWithTwitchButton.value,
                loginWithTwitch = {
                    if(loggedInStatus){
                        logoutViewModel.logoutAgain()
                    }else{
                        loginWithTwitch()
                    }
                },
                verifyDomain = { verifyDomain() },
                modifier = modifier,
                clickEnabled=logoutViewModel.buttonEnabled.value
            )
        },
        loadingIndicator={modifier ->
            LoadingIndicator(
                logoutViewModel.showLoading.value,
                modifier = modifier
            )
        },
        errorMessage={modifier ->
            AnimatedVisibility(
                logoutViewModel.showErrorMessage.value,
                modifier = modifier
            ) {
                ErrorMessage(
                    modifier = modifier,
                    message = logoutViewModel.errorMessage.value
                )
            }
        }
    )
}


@Composable
fun NavigationComposable(
    navigateHome:Boolean,
    navigateToHomeFragment: () -> Unit,
    setNavigationFalse:() -> Unit,
    navigateToTwitch:Boolean,
    loginWithTwitch: () -> Unit
){
    if (navigateHome) {

        navigateToHomeFragment()
        setNavigationFalse()
    }
    if(navigateToTwitch){
        loginWithTwitch()
    }

}



