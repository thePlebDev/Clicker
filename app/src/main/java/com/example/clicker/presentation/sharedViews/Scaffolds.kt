package com.example.clicker.presentation.sharedViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp



    /**
     * NoDrawerScaffold is a [Scaffold] composable meant to be used to build a scaffold containing a [topBar],
     * [bottomBar] and [content] but no drawer
     * - a UI demonstration can be found [HERE](https://github.com/thePlebDev/Clicker/wiki/Shared-Components#NoDrawerScaffold)
     *
     * @param topBar a [ScaffoldTopBarScope] composable that will be displayed in the Scaffold's topBar
     * @param bottomBar a [ScaffoldTopBarScope] composable that will be displayed in the Scaffold's bottomBar
     * @param content a normal composable that will be displayed in the Scaffold's content section
     *
     * */
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

    /**
     * NoDrawerScaffold is a [Scaffold] composable meant to be used to build a scaffold containing a [topBar],
     * [bottomBar] and [content] with a drawer
     * - a UI demonstration can be found [HERE](https://github.com/thePlebDev/Clicker/wiki/Shared-Components#DrawerScaffold)
     *
     * @param scaffoldState a [ScaffoldState] which is used to open and close the Scaffold's drawer
     * @param topBar a [ScaffoldTopBarScope] composable that will be displayed in the Scaffold's topBar
     * @param bottomBar a [ScaffoldTopBarScope] composable that will be displayed in the Scaffold's bottomBar
     * @param drawerContent a normal composable that will be displayed in the Scaffold's drawerContent
     * @param content a normal composable that will be displayed in the Scaffold's content section
     *
     * */
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


        Scaffold(
//            scaffoldState = scaffoldState,
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
//            drawerContent = {
//                drawerContent()
//            },
            bottomBar = {
                with(bottomBarScaffoldScope){
                    bottomBar()
                }
            },

            ) { contentPadding ->
            content(contentPadding)
        }



    }
