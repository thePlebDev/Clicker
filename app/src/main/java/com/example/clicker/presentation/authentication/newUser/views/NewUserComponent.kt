package com.example.clicker.presentation.authentication.newUser.views

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
import com.example.clicker.presentation.authentication.logout.LogoutViewModel
import com.example.clicker.presentation.authentication.views.ErrorMessage
import com.example.clicker.presentation.authentication.views.GradientStyleBox
import com.example.clicker.presentation.authentication.views.LoadingIndicator
import com.example.clicker.presentation.authentication.views.LogoIcon
import com.example.clicker.presentation.authentication.views.ModderzTagLine
import com.example.clicker.presentation.authentication.views.ShowButtonsConditional
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

//

    if (logoutViewModel.newUserNavigateHome.value) {
        logoutViewModel.setNewUserNavigateHome(false)
        navigateToHomeFragment()
    }
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
                    loginWithTwitch()
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

