package com.example.clicker.presentation.search.views.mainComponents

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.clicker.R
import com.example.clicker.network.clients.Game
import com.example.clicker.network.clients.GameInfoResponse
import com.example.clicker.network.clients.TopGame
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.presentation.home.views.ImageWithViewCount
import com.example.clicker.presentation.home.views.StreamTitleWithInfo
import com.example.clicker.presentation.stream.models.ClickedStreamInfo
import com.example.clicker.util.Response


@Composable
fun SearchViewComponent(
    topGamesListResponse: Response<Boolean>,
    showNetworkRefreshError:Boolean,
    hapticFeedBackError:()->Unit,
    topGamesList: List<TopGame>,
    categoryDoubleClickedAdd:(String)->Unit,
    categoryDoubleClickedRemove:(TopGame)->Unit,
    pinned:Boolean,
    pinnedList:List<TopGame>,
    fetchMoreTopGames:()->Unit,
    openCategoryModal:()->Unit,
    getGameInfo:(String,String)->Unit


){
    //still need to add the pager and the header


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ){
        when(topGamesListResponse){
            is Response.Loading->{
                Log.d("topGamesListResponse","LOADING")
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .size(40.dp),
                    color = MaterialTheme.colorScheme.secondary
                )

            }
            is Response.Success->{
                Log.d("topGamesListResponse","SUCCESS")
                TopGamesLazyGrid(
                    modifier = Modifier.matchParentSize(),
                    topGamesList = topGamesList,
                    categoryDoubleClickedAdd={id -> categoryDoubleClickedAdd(id)},
                    pinned = pinned,
                    pinnedList = pinnedList,
                    categoryDoubleClickedRemove={id->categoryDoubleClickedRemove(id)},
                    fetchMoreTopGames={fetchMoreTopGames()},
                    openCategoryModal={openCategoryModal()},
                    getGameInfo={id,gameName ->getGameInfo(id,gameName)}

                )

            }
            is Response.Failure->{
                Log.d("topGamesListResponse","FAILED")
                TopGamesLazyGrid(
                    modifier = Modifier.matchParentSize(),
                    topGamesList = topGamesList,
                    categoryDoubleClickedAdd={id -> },
                    pinned = false,
                    pinnedList = pinnedList,
                    categoryDoubleClickedRemove={},
                    fetchMoreTopGames={},
                    openCategoryModal={openCategoryModal()},
                    getGameInfo={id,gameName ->}

                )

            }
        }
        if(showNetworkRefreshError){
            Box(modifier = Modifier.align(Alignment.BottomCenter)){
                hapticFeedBackError()
                SearchNetworkErrorMessage(
                    "Error! Please try again"
                )
            }

        }

    }//end of the box

}

@Composable
fun TopGamesLazyGrid(
    modifier:Modifier,
    topGamesList:List<TopGame>,
    categoryDoubleClickedAdd:(String)->Unit,
    categoryDoubleClickedRemove:(TopGame)->Unit,
    pinned:Boolean,
    pinnedList:List<TopGame>,
    fetchMoreTopGames:()->Unit,
    openCategoryModal:()->Unit,
    getGameInfo:(String,String)->Unit

){

    val scrollState = rememberLazyGridState()

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
            .collect { lastVisibleIndex ->
                val totalItems = scrollState.layoutInfo.totalItemsCount
                if (totalItems - lastVisibleIndex <= 2 && totalItems >= topGamesList.size) {
                    Log.d("FetchingList","Fetching")
                    fetchMoreTopGames()
                }
            }
    }

    LazyVerticalGrid(
        state=scrollState,
        columns = GridCells.Fixed(3),
        modifier= modifier
            .padding(horizontal = 5.dp)
            .background(MaterialTheme.colorScheme.primary),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {

        if(!pinned){
            items(topGamesList){ topGame ->

                // Animate the scale for smooth appearance
                Box(){
                    Column(
                        modifier =Modifier .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    categoryDoubleClickedAdd(topGame.id) // Show the icon on double tap
                                },
                                onTap = {
                                    getGameInfo(topGame.id,topGame.name)
                                    openCategoryModal()
                                }
                            )
                        }
                    ){
                        SubcomposeAsyncImage(
                            modifier = Modifier
                                .height(200.dp)
                                .width(180.dp),
                            model = topGame.box_art_url,
                            loading = {
                                Column(modifier = Modifier
                                    .height((200).dp)
                                    .width((180).dp)
                                    .background(MaterialTheme.colorScheme.primary),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ){
                                    CircularProgressIndicator()
                                }
                            },
                            contentDescription = stringResource(R.string.sub_compose_async_image_description)
                        )
                        Text("${topGame.name}",
                            maxLines=1,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                            overflow = TextOverflow.Ellipsis)

                    }
                    PinnedAnimation(
                        modifier = Modifier.align(Alignment.TopEnd),
                        isVisible = topGame.clicked
                    )
                }

            }
        }else{
            items(pinnedList, key ={topGame ->topGame.id} ){ topGame ->
                Log.d("PinnedListEmptyCHeck","${pinnedList.isEmpty()}")


                // Animate the scale for smooth appearance
                Box(){

                        Column(
                            modifier =Modifier .pointerInput(Unit) {
                                detectTapGestures(
                                    onDoubleTap = {
                                        categoryDoubleClickedRemove(topGame) // Show the icon on double tap

                                    },
                                    onTap = {
                                        getGameInfo(topGame.id,topGame.name)
                                        openCategoryModal()
                                    }
                                )
                            }
                        ){
                            SubcomposeAsyncImage(
                                modifier = Modifier
                                    .height(200.dp)
                                    .width(180.dp),
                                model = topGame.box_art_url,
                                loading = {
                                    Column(modifier = Modifier
                                        .height((200).dp)
                                        .width((180).dp)
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
                                topGame.name,
                                maxLines=1,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                                overflow = TextOverflow.Ellipsis)

                        }
                        PinnedAnimation(
                            modifier = Modifier.align(Alignment.TopEnd),
                            isVisible = true
                        )
                    }

                }

        }


    }
}

@Composable
fun PinnedAnimation(
    modifier: Modifier,
    isVisible:Boolean
){
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f // Animate from 0 to 1
    )

    // Animate the opacity for smooth appearance
    val alphaTesting by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f // Animate from 0 to 1
    )
    if (isVisible || scale > 0f) { // Keep showing the icon while animating
        Icon(
            modifier = modifier
                .size(30.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    alpha = alphaTesting
                    rotationZ = 45f // Rotate left by 45 degrees
                },
            painter = painterResource(id = R.drawable.push_pin_24),
            contentDescription = "Push Pin",
            tint = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Composable
fun SearchBarUI(
    changePinnedListFilterStatus:()->Unit,
    pinned: Boolean,
){
    var expanded by remember {
        mutableStateOf(false)
    }
    Column( modifier = Modifier.padding(horizontal = 10.dp, vertical =5.dp)){
        StylizedTextField()
        Spacer(modifier =Modifier.size(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text("Categories",color = MaterialTheme.colorScheme.onPrimary)
            Icon(painter = painterResource(id =R.drawable.menu_open_24),
                contentDescription ="Open filter" , tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        expanded = !expanded
                    }
            )
        }
        SearchFilterDropDownMenu(
            expanded=expanded,
            setExpanded = {newValue ->expanded = newValue},
            changePinnedListFilterStatus={changePinnedListFilterStatus()},
            pinned=pinned
        )
    }

}
//after THis needs to go inside of a new file
@Composable
fun StylizedTextField(){

    Log.d("StylizedTextFieldRecomp","RECOMP")



    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.secondary,
        backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
    )
    var text by remember { mutableStateOf("") }



    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {




            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true,
                maxLines = 5,
                value = text,

                shape = RoundedCornerShape(8.dp),
                onValueChange = { newText ->
                    text = newText

                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search // This sets the search icon on the keyboard
                ),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedContainerColor = Color.DarkGray,
                    focusedContainerColor = Color.DarkGray,
                    cursorColor = Color.White,
                    disabledLabelColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        "Search",
                        tint = Color.White

                    )
                }
            )



    }


}

@Composable
fun SearchNetworkErrorMessage(
    errorMessage:String
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)

    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Text(
                errorMessage,
                color = Color.Red.copy(alpha = 0.9f),
                fontSize = MaterialTheme.typography.headlineMedium.fontSize
            )

        }
    }

}

@Composable
fun SearchFilterDropDownMenu(
    expanded:Boolean,
    setExpanded: (Boolean) -> Unit,
    changePinnedListFilterStatus:()->Unit,
    pinned: Boolean,

) {

    Box(modifier = Modifier.wrapContentSize(Alignment.BottomCenter)) {


        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { setExpanded(false) },
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color.DarkGray
                )
        ) {

            SearchTextMenuItem(
                setExpanded = { newValue -> setExpanded(newValue) },
                title = "Filter pinned categories",
                changePinnedListFilterStatus={changePinnedListFilterStatus()},
                pinned=pinned

                )


        }
    }
}
@Composable
fun SearchTextMenuItem(
    setExpanded: (Boolean) -> Unit,
    title:String,
    changePinnedListFilterStatus:()->Unit,
    pinned: Boolean

){
    DropdownMenuItem(
        onClick = {
            setExpanded(false)
            changePinnedListFilterStatus()
        },
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    painter = painterResource(id =R.drawable.push_pin_24),
                    contentDescription = "filter for pinned categories",
                    tint=if(pinned) MaterialTheme.colorScheme.secondary else Color.White,
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier =Modifier.width(10.dp))
                Column() {
                    Text(title, color = Color.White)
                    Text("Double click on categories to pin and unpin",
                        color = Color.White.copy(0.6f),
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )
                }

            }

        }
    )
}

@Composable
fun CategoryModal(
    gameInfoResponse: Response<Game?>,
    gameTitle: String
){
    Column(
        modifier= Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ){
        CategoryModalHeader(
            gameTitle=gameTitle,
            gameInfoResponse=gameInfoResponse,
        )
        LazyColumn(){
            items(20){
                CategoryModalBody()
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }

}

@Composable
fun CategoryModalHeader(
    gameInfoResponse: Response<Game?>,
    gameTitle: String
){
    Box(
        modifier = Modifier.padding(bottom = 10.dp)
    ){
        when(gameInfoResponse){
            is Response.Loading->{
                GameInformationHeaderLoading(gameTitle)
            }
            is Response.Success->{
                val data = gameInfoResponse.data
                if(data != null){
                    GameInformationHeaderSuccess(gameTitle,data)
                }else{
                    GameInformationHeaderFailed(gameTitle)

                }
            }
            is Response.Failure->{
                GameInformationHeaderFailed(gameTitle)
            }
        }
    }

}
@Composable
fun GameInformationHeaderLoading(
    gameTitle: String
){
    Row(modifier= Modifier
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ){
        SubcomposeAsyncImage(
            modifier = Modifier.height(200.dp).width(180.dp),
            model = "https://static-cdn.jtvnw.net/ttv-static/404_boxart.jpg",
            loading = {
                Column(modifier = Modifier
                    .height((200).dp)
                    .width((150).dp)
                    .background(MaterialTheme.colorScheme.primary),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    CircularProgressIndicator()
                }
            },
            contentDescription = stringResource(R.string.sub_compose_async_image_description)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column() {
            Text(gameTitle,color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineLarge.fontSize)
            Text("Loading....",
                color = MaterialTheme.colorScheme.onPrimary.copy(0.6f),
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                lineHeight = 15.sp
            )
        }

    }
}
@Composable
fun GameInformationHeaderSuccess(
    gameTitle: String,
    game:Game
){
    Row(modifier= Modifier
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ){
        SubcomposeAsyncImage(
            modifier = Modifier.height(200.dp).width(180.dp),
            model = game.box_art_url,
            loading = {
                Column(modifier = Modifier
                    .height((200).dp)
                    .width((150).dp)
                    .background(MaterialTheme.colorScheme.primary),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    CircularProgressIndicator()
                }
            },
            contentDescription = stringResource(R.string.sub_compose_async_image_description)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column() {
            Text(gameTitle,color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineLarge.fontSize)
        }

    }
}
@Composable
fun GameInformationHeaderFailed(
    gameTitle: String
){
    Row(modifier= Modifier
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ){
        SubcomposeAsyncImage(
            modifier = Modifier.height(200.dp).width(180.dp),
            model = "https://static-cdn.jtvnw.net/ttv-static/404_boxart.jpg",
            loading = {
                Column(modifier = Modifier
                    .height((200).dp)
                    .width((150).dp)
                    .background(MaterialTheme.colorScheme.primary),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    androidx.compose.material.CircularProgressIndicator()
                }
            },
            contentDescription = stringResource(R.string.sub_compose_async_image_description)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column() {
            Text(gameTitle,color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineLarge.fontSize)
            Text("Failed to get information about $gameTitle",
                color = MaterialTheme.colorScheme.onPrimary.copy(0.6f),
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                lineHeight = 15.sp
            )
        }

    }
}

@Composable
fun CategoryModalBody(){
    SearchLiveChannelRowItem(
        updateStreamerName={one,two,three,four ->},
        updateClickedStreamInfo={item ->},
        streamItem=StreamData(id="52256588445", userId="415858333", userLogin="beterbabbit", userName="BeterBabbit", gameId="138585", gameName="Hearthstone", type="live", title="good morning | !youtube and more things", viewerCount=1454, startedAt="2024-10-23T16:46:07Z", language="en", thumbNailUrl="https://static-cdn.jtvnw.net/previews-ttv/live_user_beterbabbit-540x303.jpg", tagIds= listOf(), tags=listOf("English", "bgs", "Battlegrounds", "BG", "battleground"), isMature=false),
        clientId="",
        userId="",
        onNavigate={item->},
        height=303,
        width=540,
        density=2.625f

    )
}

@Composable
fun SearchLiveChannelRowItem(
    updateStreamerName: (String, String, String, String) -> Unit,
    updateClickedStreamInfo:(ClickedStreamInfo)->Unit,
    streamItem: StreamData,
    clientId: String,
    userId:String,
    onNavigate: (Int) -> Unit,
    height: Int,
    width: Int,
    density:Float

){

    Row(
        modifier = Modifier.clickable {
            updateClickedStreamInfo(
                ClickedStreamInfo(
                    channelName = streamItem.userLogin,
                    streamTitle = streamItem.title,
                    category =  streamItem.gameName,
                    tags = streamItem.tags,
                    adjustedUrl = streamItem.thumbNailUrl
                )
            )

            updateStreamerName(
                streamItem.userLogin,
                clientId,
                streamItem.userId,
                userId
            )
            onNavigate(R.id.action_homeFragment_to_streamFragment)
        }
    ){
        SearchImageWithViewCount(
            url = streamItem.thumbNailUrl,
            height = height,
            width = width,
            viewCount = streamItem.viewerCount,
            density =density
        )
        SearchTitleWithInfo(
            streamerName = streamItem.userLogin,
            streamTitle = streamItem.title,
            gameTitle = streamItem.gameName,
            tags= streamItem.tags
        )

    }
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
    )
}


@Composable
fun SearchImageWithViewCount(
    url: String,
    height: Int,
    width: Int,
    viewCount:Int,
    density:Float
){
    Log.d("ImageHeightWidth","url -> $url")
    Box() {
        val adjustedHeight = height/density
        val adjustedWidth = width/density
        SubcomposeAsyncImage(
            model = url,
            loading = {
                Column(modifier = Modifier
                    .height((adjustedHeight).dp)
                    .width((adjustedWidth).dp)
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
fun SearchTitleWithInfo(
    streamerName:String,
    streamTitle:String,
    gameTitle:String,
    tags:List<String>
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
            modifier = Modifier.alpha(0.7f),
            color = MaterialTheme.colorScheme.onPrimary
        )
        LazyRow(){
            items(tags){tag->
                Row(){
                    Card(
                        shape = RoundedCornerShape(5.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.DarkGray
                        ),

                    ) {
                        Text(tag,color = Color.White,modifier = Modifier.padding(5.dp))
                    }
                    Spacer(modifier= Modifier.width(5.dp))
                }

            }


        }
    }
}

