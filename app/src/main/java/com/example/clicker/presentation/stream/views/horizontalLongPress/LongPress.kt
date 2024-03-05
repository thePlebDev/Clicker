package com.example.clicker.presentation.stream.views.horizontalLongPress

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.clicker.R
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.presentation.home.HomeViewModel
import com.example.clicker.presentation.sharedViews.PullToRefreshComponent
import com.example.clicker.presentation.sharedViews.ScaffoldBottomBarScope
import com.example.clicker.presentation.sharedViews.ScaffoldTopBarScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HorizontalLongPressView(
    homeViewModel: HomeViewModel
){
    val clicked = remember { mutableStateOf(true) }
    val text = if (clicked.value) "Live channels" else "Mod channels"
    val listStreamData = listOf<StreamData>(
        StreamData(
       "0",
            "0","piratesoftware","","",
            "Software and Game Development","","GAME DEV Q/A Go Make Games @FerretSoftware !Heartbound !Website !Vote !TTS",4000,"",
            "",
            "https://static-cdn.jtvnw.net/previews-ttv/live_user_piratesoftware-270x151.jpg",
            listOf(""),listOf(""),false
    ),
        StreamData(
            "1",
            "1","piratesoftware","","",
            "Software and Game Development","","GAME DEV Q/A Go Make Games @FerretSoftware !Heartbound !Website !Vote !TTS",4000,"",
            "",
            "https://static-cdn.jtvnw.net/previews-ttv/live_user_piratesoftware-270x151.jpg",
            listOf(""),listOf(""),false
        ),
        StreamData(
            "2",
            "2","piratesoftware","","",
            "Software and Game Development","","GAME DEV Q/A Go Make Games @FerretSoftware !Heartbound !Website !Vote !TTS",4000,"",
            "",
            "https://static-cdn.jtvnw.net/previews-ttv/live_user_piratesoftware-270x151.jpg",
            listOf(""),listOf(""),false
        ),
        StreamData(
            "3",
            "3","piratesoftware","","",
            "Software and Game Development","","GAME DEV Q/A Go Make Games @FerretSoftware !Heartbound !Website !Vote !TTS",4000,"",
            "",
            "https://static-cdn.jtvnw.net/previews-ttv/live_user_piratesoftware-270x151.jpg",
            listOf(""),listOf(""),false
        ),
        StreamData(
            "4",
            "4","piratesoftware","","",
            "Software and Game Development","","GAME DEV Q/A Go Make Games @FerretSoftware !Heartbound !Website !Vote !TTS",4000,"",
            "",
            "https://static-cdn.jtvnw.net/previews-ttv/live_user_piratesoftware-270x151.jpg",
            listOf(""),listOf(""),false
        ),
    )

    LongPress
        .MainView(
            topBar = {
                TopBarText(text)
            },
            bottomBar = {
                this.DualButtonNavigationBottomBar(
                    bottomRowHeight = 50.dp,
                    firstButton ={
                        this.IconOverText(
                            iconColor =if(clicked.value) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onPrimary,
                            text = "Home",
                            imageVector = Icons.Default.Home,
                            iconContentDescription = "View live followed channels",
                            onClick = {
                                clicked.value = true
                            }
                        )
                    } ,
                    secondButton = {
                        this.PainterResourceIconOverText(
                            iconColor =if(!clicked.value) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onPrimary,
                            text = "Mod Channels",
                            painter = painterResource(id = R.drawable.mod_view_24),
                            iconContentDescription = "View live mod channels",
                            onClick = {
                                clicked.value = false
                            }
                        )
                    }
                )
            },
            content = { contentPadding ->

                LongPressPullToRefresh(
                    contentPadding =contentPadding,
                    content ={
                        TestingLazyColumnItem(
                            height = homeViewModel.state.value.aspectHeight,
                            width = homeViewModel.state.value.width,
                            density =homeViewModel.state.value.screenDensity,
                            listData = listStreamData
                        )
                    }
                )

            },
        )
}

@Composable
fun LongPressPullToRefresh(
    contentPadding: PaddingValues,
    content:@Composable () -> Unit,
){
    val refreshing = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    PullToRefreshComponent(
        padding = contentPadding,
        refreshing =refreshing.value,
        refreshFunc = {

            refreshing.value = true
            scope.launch {
                delay(1000)
                refreshing.value = false
            }

        },
        content = {
                  content()
        },
        networkStatus = {},
        showNetworkMessage = false


    )
}

object LongPress{


    @Composable
    fun MainView(
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
}

@Composable
fun TestingLazyColumnItem(
    height: Int,
    width: Int,
    density:Float,
    listData: List<StreamData>
    ){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
    ) {


        items(listData,key = { streamItem -> streamItem.userId }) { streamItem ->
            RowItem(
                streamerName = streamItem.userLogin,
                streamTitle = streamItem.title,
                gameTitle = streamItem.gameName,
                url = streamItem.thumbNailUrl,
                height = height,
                width = width,
                viewCount = streamItem.viewerCount,
                density =density
            )
        }

    }
}

@Composable
fun RowItem(
    streamerName:String,
    streamTitle:String,
    gameTitle:String,
    url: String,
    height: Int,
    width: Int,
    viewCount:Int,
    density:Float
){

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable { },
        verticalAlignment = Alignment.CenterVertically
    ){
        ImageWithViewCount(
            url =url,
            height = height,
            width = width,
            viewCount = viewCount,
            density = density
        )
        StreamTitleWithInfo(
            streamerName = streamerName,
            gameTitle = gameTitle,
            streamTitle = streamTitle

        )


    }
}

@Composable
fun ImageWithViewCount(
    url: String,
    height: Int,
    width: Int,
    viewCount:Int,
    density:Float
){
    val adjustedHeight = height/density
    val adjustedWidth = width/density
    Log.d("ImageHeightWidth","url -> $url")
    Box(
    ) {

        SubcomposeAsyncImage(
            model = url,
            loading = {
                Column(modifier = Modifier
                    .height(adjustedHeight.dp)
                    .width(adjustedWidth.dp)
                    .background(MaterialTheme.colorScheme.primary),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    CircularProgressIndicator()
                }
            },
            contentDescription = stringResource(R.string.sub_compose_async_image_description)
        )
        Text(
            "${viewCount}",
            style = TextStyle(
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                fontWeight = FontWeight.ExtraBold
            ),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(5.dp)
        )
    }
}

@Composable
fun StreamTitleWithInfo(
    streamerName:String,
    streamTitle:String,
    gameTitle:String
){
    Column(modifier = Modifier.padding(start = 10.dp)) {
        Text(
            streamerName,
            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            streamTitle,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            modifier = Modifier.alpha(0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            gameTitle,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.alpha(0.7f),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}


