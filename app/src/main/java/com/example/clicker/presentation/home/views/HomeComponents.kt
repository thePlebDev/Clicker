package com.example.clicker.presentation.home.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.R
import com.example.clicker.presentation.home.CustomTopBar
import com.example.clicker.presentation.home.HomeView
import com.example.clicker.presentation.home.disableClickAndRipple
import kotlinx.coroutines.launch

object HomeComponents {

    @Composable
    fun HomeImplementationScaffold(
        scaffoldState: ScaffoldState,
        logout:()->Unit,
        login: () -> Unit,
        userIsAuthenticated:Boolean,
        updateAuthenticatedUser:()->Unit,
        homeView:@Composable () -> Unit
    ){
        Scaffold(
            backgroundColor=MaterialTheme.colorScheme.primary,
            scaffoldState = scaffoldState,
            drawerContent = {
                ScaffoldDrawer(
                    logout = {
                        logout()
                    },
                    loginWithTwitch = {
                        login()
                    },
                    scaffoldState = scaffoldState,
                    userIsLoggedIn = userIsAuthenticated
                )
            },
            topBar = {
                CustomTopBar(
                    scaffoldState = scaffoldState
                )
            }
        ) { contentPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                if (userIsAuthenticated) {
                    updateAuthenticatedUser()
                }

                homeView()
            }
        }
    }

    //parts
    @Composable
    fun GettingStreamsError() {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .clickable { },
            elevation = 10.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(R.string.pull_to_refresh_icon_description),
                    tint = Color.Black,
                    modifier = Modifier.size(35.dp)
                )
                Text(stringResource(R.string.error_pull_to_refresh), fontSize = 20.sp)
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(R.string.pull_to_refresh_icon_description),
                    tint = Color.Black,
                    modifier = Modifier.size(35.dp)
                )
            }
        }
    }
    @Composable
    fun EmptyFollowingList() {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .clickable { },
            elevation = 10.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(R.string.pull_to_refresh_icon_description),
                    tint = Color.Black,
                    modifier = Modifier.size(35.dp)
                )
                Text(stringResource(R.string.no_live_streams), fontSize = 20.sp)
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(R.string.pull_to_refresh_icon_description),
                    tint = Color.Black,
                    modifier = Modifier.size(35.dp)
                )
            }
        }
    }

    @Composable
    fun LoginWithTwitchBottomModalButton(
        modalText:String,
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
                modalText,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 30.sp,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Button(onClick = { loginWithTwitch() }) {
                Text(text = stringResource(R.string.login_with_twitch))
            }
        }
    }


    @Composable
    fun AccountActionCard(
        scaffoldState: ScaffoldState,
        accountAction: () -> Unit,
        title:String,
        iconImageVector: ImageVector
    ) {
        val scope = rememberCoroutineScope()

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .clickable {
                    scope.launch {
                        scaffoldState.drawerState.close()
                    }
                    accountAction()
                },
            elevation = 10.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.secondary),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(title, fontSize = 20.sp,color = MaterialTheme.colorScheme.onSecondary)
                Icon(
                    iconImageVector,
                    stringResource(R.string.logout_icon_description),
                    modifier = Modifier.size(35.dp),
                    tint =  MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
    @Composable
    fun ScaffoldDrawer(
        logout: () -> Unit,
        loginWithTwitch: () -> Unit,
        scaffoldState: ScaffoldState,
        userIsLoggedIn: Boolean
    ) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary)){
            if (userIsLoggedIn) {

                AccountActionCard(
                    scaffoldState,
                    accountAction = {logout() },
                    title= stringResource(R.string.logout_icon_description),
                    iconImageVector =Icons.Default.ExitToApp
                )
            } else {
                AccountActionCard(
                    scaffoldState,
                    accountAction = { loginWithTwitch() },
                    title =stringResource(R.string.login_with_twitch),
                    iconImageVector = Icons.Default.AccountCircle
                )
            }
        }

    }
    @Composable
    fun DisableForceRegister(
        addToLinks: () -> Unit
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Spacer(
                modifier = Modifier
                    .disableClickAndRipple()
                    .background(
                        color = Color.Gray.copy(alpha = .7f)
                    )
                    .matchParentSize()
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
                    .align(Alignment.Center)
                    .clickable { },
                backgroundColor = MaterialTheme.colorScheme.primary,
                elevation = 10.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        stringResource(R.string.you_must_add),
                        fontSize = 25.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        stringResource(R.string.package_name),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        stringResource(R.string.enable_login_with_Android_12),
                        fontSize = 25.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { addToLinks() },
                        modifier = Modifier.padding(top = 20.dp, bottom = 20.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(text =stringResource(R.string.add_to_links), fontSize = 25.sp, color = MaterialTheme.colorScheme.onSecondary)
                    }
                }
            }
        } // end of the box
    }
}