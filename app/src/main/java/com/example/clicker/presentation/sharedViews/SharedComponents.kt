package com.example.clicker.presentation.sharedViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ScaffoldState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.clicker.util.NetworkAuthResponse

/**
 * SharedComponents represents the most used and most stable versions of components used throughout this application
 * **/
object SharedComponents {

    @Composable
    fun NoDrawerScaffold(
        topBar:@Composable ScaffoldTopBarScope.() -> Unit,
        bottomBar:@Composable ScaffoldBottomBarScope.() -> Unit,
        content:@Composable (contentPadding: PaddingValues,) -> Unit,

        ) {
        val topBarScaffoldScope = remember(){ScaffoldTopBarScope(35.dp)}
        val bottomBarScaffoldScope = remember(){ScaffoldBottomBarScope(25.dp)}

        Scaffold(
            containerColor = MaterialTheme.colorScheme.primary,
            topBar = {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)){
                    with(topBarScaffoldScope){
                        topBar()
                    }
                }

            },
            bottomBar = {
                with(bottomBarScaffoldScope){
                    bottomBar()
                }
            },

            ) { contentPadding ->
            content(contentPadding)

        }
    }

    @Composable
    fun DrawerScaffold(
        scaffoldState: ScaffoldState,
        topBar:@Composable ScaffoldTopBarScope.() -> Unit,
        bottomBar:@Composable ScaffoldBottomBarScope.() -> Unit,
        drawerContent:@Composable () -> Unit,
        content:@Composable (contentPadding: PaddingValues,) -> Unit,
    ){
        val topBarScaffoldScope = remember(){ScaffoldTopBarScope(35.dp)}
        val bottomBarScaffoldScope = remember(){ScaffoldBottomBarScope(25.dp)}


        androidx.compose.material.Scaffold(
            scaffoldState = scaffoldState,
            backgroundColor = MaterialTheme.colorScheme.primary,
                topBar = {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)){
                        with(topBarScaffoldScope){
                            topBar()
                        }
                    }
                },
            drawerContent = {
                drawerContent()
            },
                bottomBar = {
                    with(bottomBarScaffoldScope){
                        bottomBar()
                    }
                },

                ) { contentPadding ->
                content(contentPadding)
            }



    }
}