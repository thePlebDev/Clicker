package com.example.clicker.presentation.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.SubcomposeAsyncImage
import com.example.clicker.R
import com.example.clicker.presentation.authentication.AuthenticationViewModel
import com.example.clicker.presentation.home.views.HomeComponents
import com.example.clicker.presentation.home.views.HomeComponents.HomeViewImplementation

import com.example.clicker.presentation.home.views.HomeComponents.Parts.DisableForceRegister
import com.example.clicker.presentation.home.views.HomeComponents.Parts.LoginWithTwitchBottomModalButton
import com.example.clicker.presentation.stream.AutoModViewModel
import com.example.clicker.presentation.stream.StreamViewModel
import com.example.clicker.presentation.stream.views.dialogs.CreateNewPollDialog

import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ValidationView(
    homeViewModel: HomeViewModel,
    streamViewModel: StreamViewModel,
    authenticationViewModel: AuthenticationViewModel,
    loginWithTwitch: () -> Unit,
    onNavigate: (Int) -> Unit,
    addToLinks: () -> Unit,
    autoModViewModel: AutoModViewModel
) {
    val bottomModalState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    val modalText = authenticationViewModel.authenticationUIState.value.modalText
    val showModalState = homeViewModel.state.value.showLoginModal
    val domainIsRegistered = homeViewModel.state.value.domainIsRegistered
    if (showModalState) {
        LaunchedEffect(bottomModalState) { // the key define when the block is relaunched
            // Your coroutine code here
            scope.launch {
                bottomModalState.show()
            }
        }
    } else {
        LaunchedEffect(bottomModalState) { // the key define when the block is relaunched
            // Your coroutine code here
            scope.launch {
                bottomModalState.hide()
            }
        }
    }

    val userIsAuthenticated = homeViewModel.validatedUser.collectAsState().value?.clientId != null
    val userId = homeViewModel.validatedUser.collectAsState().value?.userId
    val clientId = homeViewModel.validatedUser.collectAsState().value?.clientId
    val oAuthToken = homeViewModel.state.value.oAuthToken



    HomeViewImplementation(
        bottomModalState =bottomModalState,
        modalText =modalText,
        loginWithTwitch ={loginWithTwitch()},
        domainIsRegistered =domainIsRegistered,
        addToLinks = { addToLinks() },
        onNavigate = {id -> onNavigate(id) },
        updateStreamerName = { streamerName, clientId,broadcasterId,userId->
            streamViewModel.updateChannelNameAndClientIdAndUserId(
                streamerName,
                clientId,
                broadcasterId,
                userId
            )
            autoModViewModel.updateAutoModCredentials(
                oAuthToken = homeViewModel.state.value.oAuthToken,
                clientId = streamViewModel.state.value.clientId,
                moderatorId = streamViewModel.state.value.userId,
                broadcasterId = streamViewModel.state.value.broadcasterId,
            )

        },
        updateClickedStreamInfo={clickedStreamInfo ->streamViewModel.updateClickedStreamInfo(clickedStreamInfo)  },
        streamersListLoading = homeViewModel.state.value.streamersListLoading,
        urlList =homeViewModel.newUrlList.collectAsState().value,
        clientId = clientId ?: "",
        userId = userId ?: "",
        height = homeViewModel.state.value.aspectHeight,
        width = homeViewModel.state.value.width,
        logout = {
            authenticationViewModel.beginLogout(
                clientId = clientId?:"",
                oAuthToken = oAuthToken
            )
            //homeViewModel.logout()
            homeViewModel.hideLogoutDialog()

        },
        userIsAuthenticated =userIsAuthenticated,
        screenDensity = homeViewModel.state.value.screenDensity,
        homeRefreshing =homeViewModel.state.value.homeRefreshing,
        homeRefreshFunc = {homeViewModel.pullToRefreshGetLiveStreams()},
        networkMessageColor=Color.Red,
        networkMessage =homeViewModel.state.value.homeNetworkErrorMessage,
        showNetworkMessage = homeViewModel.state.value.networkConnectionState,
        logoutDialogIsOpen =homeViewModel.state.value.logoutDialogIsOpen,
        hideLogoutDialog ={homeViewModel.hideLogoutDialog()},
        showLogoutDialog ={homeViewModel.showLogoutDialog()},
        currentUsername = homeViewModel.validatedUser.collectAsState().value?.login ?: "Username not found"


    )
}


fun Modifier.disableClickAndRipple(): Modifier = composed {
    clickable(
        enabled = false,
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = { }
    )
}


