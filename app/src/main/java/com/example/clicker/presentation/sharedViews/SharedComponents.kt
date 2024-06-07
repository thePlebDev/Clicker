package com.example.clicker.presentation.sharedViews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.ScaffoldState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.R
import com.example.clicker.util.NetworkAuthResponse

/**
 * SharedComponents represents the most used and most stable versions of components used throughout this application
 * **/
object SharedComponents {

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


@Composable
fun ModViewScaffoldWithDrawer(
    topBar:@Composable ScaffoldTopBarScope.() -> Unit,
    bottomBar:@Composable ScaffoldBottomBarScope.() -> Unit,
    drawerState: DrawerState,
    checkIndexAvailability:(Int)->Unit,
    showError:Boolean,
    content:@Composable (contentPadding: PaddingValues,) -> Unit,

    ) {
    val topBarScaffoldScope = remember(){ScaffoldTopBarScope(35.dp)}
    val bottomBarScaffoldScope = remember(){ScaffoldBottomBarScope(25.dp)}
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                ModViewDrawerContent(
                    checkIndexAvailability={index -> checkIndexAvailability(index)},
                    showError =showError
                )
            }
        },
    ) {
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


}

@Composable
fun ModViewDrawerContent(
    checkIndexAvailability:(Int)->Unit,
    showError:Boolean
){
    Box(modifier = Modifier.fillMaxSize()){
        LazyColumn(
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            item {
                ElevatedCardExample(
                    Color.Green,
                    "Chat",
                    checkIndexAvailability={checkIndexAvailability(1)}
                )
            }
            item{
                ElevatedCardExample(
                    Color.Magenta,
                    "AutoMod Queue",
                    checkIndexAvailability={checkIndexAvailability(2)}
                )
            }
            item{
                ElevatedCardExample(
                    Color.Blue,
                    "Mod actions",
                    checkIndexAvailability={checkIndexAvailability(3)}
                )
            }

//            item{
//                ElevatedCardExample(
//                    Color.Yellow,
//                    "Un-ban requests",
//                    checkIndexAvailability={checkIndexAvailability(4)}
//                )
//            }
//
//            item{
//                ElevatedCardExample(
//                    Color.LightGray,
//                    "Discord",
//                    checkIndexAvailability={checkIndexAvailability(5)}
//                )
//            }
//
//            item{
//                ElevatedCardExample(
//                    Color.Cyan,
//                    "Moderators",
//                    checkIndexAvailability={checkIndexAvailability(6)}
//                )
//            }

        }

        if(showError){
            Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                ErrorMessage(
                    modifier = Modifier,
                    message="Error! No space to place "
                )
                Spacer(modifier =Modifier.height(10.dp))
            }
        }



    }

}

@Composable
fun ElevatedCardExample(
    backgroundColor:Color,
    type:String,
    checkIndexAvailability:()->Unit
) {
    Spacer(modifier =Modifier.height(5.dp))
    ElevatedCard(
        colors = CardDefaults.cardColors(containerColor =backgroundColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier.size(width = 280.dp, height = 140.dp).clickable { checkIndexAvailability() }
    ) {
        Text(
            text = type,
            color = Color.Red,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center,
            fontSize = 30.sp
        )
    }
    Spacer(modifier =Modifier.height(5.dp))
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
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription ="modderz logo",
            modifier = Modifier.size(25.dp),
            tint = Color.White
        )

        Text(
            text = message,
            color = Color.White,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
        )

    }
}