package com.example.clicker.presentation.search.views

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.clicker.R
import com.example.clicker.network.clients.Game
import com.example.clicker.network.clients.SearchStreamData
import com.example.clicker.network.clients.TopGame

import com.example.clicker.presentation.stream.models.ClickedStreamInfo
import com.example.clicker.util.Response
import androidx.compose.ui.text.input.TransformedText
import com.example.clicker.presentation.sharedViews.NoDrawerScaffold
import com.example.clicker.presentation.sharedViews.PullToRefreshComponent


/**
 * - **SearchViewComponent** is the
 *
 * @param showNetworkRefreshError
 * @param topGamesListResponse
 * @param hapticFeedBackError
 * @param topGamesList
 * @param categoryDoubleClickedAdd
 * @param categoryDoubleClickedRemove
 * @param pinned
 * @param pinnedList
 * @param fetchMoreTopGames
 * @param openCategoryModal
 * @param getGameInfo
 * @param getGameStreams
 * */

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
    getGameInfo:(String,String)->Unit,
    getGameStreams:(String)->Unit


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
                    getGameInfo={id,gameName ->getGameInfo(id,gameName)},
                    getGameStreams={id->getGameStreams(id)}

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
                    getGameInfo={id,gameName ->},
                    getGameStreams={id->}

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
    getGameInfo:(String,String)->Unit,
    getGameStreams:(String)->Unit,

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
        columns = GridCells.Adaptive(minSize = 100.dp),
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
                                    getGameStreams(topGame.id)
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
                                        getGameStreams(topGame.id)
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
            pinned=pinned,

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
                pinned=pinned,

                )


        }
    }
}
@Composable
fun SearchTextMenuItem(
    setExpanded: (Boolean) -> Unit,
    title:String,
    changePinnedListFilterStatus:()->Unit,
    pinned: Boolean,


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

//TODO: this is where the response needs to go
@Composable
fun CategoryModal(
    gameInfoResponse: Response<Game?>,
    gameTitle: String,
    liveGameStreamsResponse:Response<Boolean>,
    streamDataList: List<SearchStreamData>,
    updateStreamerName: (String, String, String, String) -> Unit,
    updateClickedStreamInfo: (ClickedStreamInfo) -> Unit,
    getMoreStreams:()->Unit,
    clientId: String,
    userId: String,
    onNavigate: (Int) -> Unit,
    height: Int,
    width: Int,
    density: Float,
    closeModal:()->Unit,
    selectedLanguage:String?,
    changeSelectedLanguage: (String) -> Unit,
    webViewAnimation:(String)->Unit,
){
    Column(
        modifier= Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        CategoryModalHeader(
            gameTitle=gameTitle,
            gameInfoResponse=gameInfoResponse,
            closeModal={closeModal()},
            height=height,
            width=width,
            density=density,
        )
        when(liveGameStreamsResponse){
            is Response.Loading ->{
                LiveGameLoading()

            }
            is Response.Success ->{
                LiveGameSuccess(
                    streamData=streamDataList,
                    updateStreamerName={streamerName,clientId,broadcasterId,userId ->
                        updateStreamerName(streamerName,clientId,broadcasterId,userId)
                    },
                    updateClickedStreamInfo={clickedStreamInfo ->  updateClickedStreamInfo(clickedStreamInfo)},
                    clientId=clientId,
                    userId=userId,
                    onNavigate={navItem->onNavigate(navItem)},
                    height=height,
                    width=width,
                    density=density,
                    getMoreStreams={getMoreStreams()},
                    selectedLanguage=selectedLanguage,
                    changeSelectedLanguage={newValue ->changeSelectedLanguage(newValue)},
                    webViewAnimation={channelName ->webViewAnimation(channelName)}

                )

            }
            is Response.Failure ->{
                LiveGameFailure()
            }
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LiveGameSuccess(
    streamData:List<SearchStreamData>,
    updateStreamerName: (String, String, String, String) -> Unit,
    updateClickedStreamInfo: (ClickedStreamInfo) -> Unit,
    clientId: String,
    userId: String,
    onNavigate: (Int) -> Unit,
    height: Int,
    width: Int,
    density: Float,
    getMoreStreams:()->Unit,
    selectedLanguage:String?,
    changeSelectedLanguage: (String) -> Unit,
    webViewAnimation:(String)->Unit,
){

    val scrollStateColumn = rememberLazyListState()


    LaunchedEffect(scrollStateColumn) {
        snapshotFlow { scrollStateColumn.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
            .collect { lastVisibleIndex ->
                val totalItems = scrollStateColumn.layoutInfo.totalItemsCount
                if (totalItems - lastVisibleIndex <= 2 && totalItems >= streamData.size) {
                    Log.d("FetchingList","Fetching")
                    getMoreStreams()
                }
            }
    }
    LazyColumn(state = scrollStateColumn){
        stickyHeader {
            Box(
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary).padding(5.dp)
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        "Live Channels",
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier =Modifier.padding(5.dp),
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize
                    )
                    SearchStreamLanguage(
                        selectedLanguage=selectedLanguage,
                        changeSelectedLanguage={newValue ->changeSelectedLanguage(newValue)}
                    )
                }
            }


        }
        items(streamData){searchStreamItem->
            CategoryModalBody(
                updateStreamerName={streamerName,clientId,broadcasterId,userId ->
                    updateStreamerName(streamerName,clientId,broadcasterId,userId)
                },
                updateClickedStreamInfo={clickedStreamInfo ->  updateClickedStreamInfo(clickedStreamInfo)},
                searchStreamItem=searchStreamItem,
                clientId=clientId,
                userId=userId,
                onNavigate={navItem->onNavigate(navItem)},
                height=height,
                width=width,
                density=density,
                webViewAnimation={channelName ->webViewAnimation(channelName)}
            )
        }

    }
}


//this is the individual items
@Composable
fun CategoryModalBody(
    updateStreamerName: (String, String, String, String) -> Unit,
    updateClickedStreamInfo: (ClickedStreamInfo) -> Unit,
    searchStreamItem: SearchStreamData,
    clientId: String,
    userId: String,
    onNavigate: (Int) -> Unit,
    height: Int,
    width: Int,
    density: Float,
    webViewAnimation:(String)->Unit,

){
    SearchLiveChannelRowItem(
        updateStreamerName={streamerName,clientId,broadcasterId,userId ->
            updateStreamerName(streamerName,clientId,broadcasterId,userId)
                           },
        updateClickedStreamInfo={clickedStreamInfo ->  updateClickedStreamInfo(clickedStreamInfo)},
        searchStreamItem=searchStreamItem,
        clientId=clientId,
        userId=userId,
        onNavigate={navItem->onNavigate(navItem)},
        height=height,
        width=width,
        density=density,
        webViewAnimation={channelName ->webViewAnimation(channelName)}

    )
}

@Composable
fun SearchLiveChannelRowItem(
    updateStreamerName: (String, String, String, String) -> Unit,
    updateClickedStreamInfo:(ClickedStreamInfo)->Unit,
    searchStreamItem: SearchStreamData,
    clientId: String,
    userId:String,
    onNavigate: (Int) -> Unit,
    height: Int,
    width: Int,
    density:Float,
    webViewAnimation:(String)->Unit,

){

    Row(
        modifier = Modifier.clickable {
            updateClickedStreamInfo(
                ClickedStreamInfo(
                    channelName = searchStreamItem.user_login,
                    streamTitle = searchStreamItem.title,
                    category =  searchStreamItem.game_name,
                    tags = searchStreamItem.tags ?: listOf(),
                    adjustedUrl = searchStreamItem.thumbnail_url
                )
            )
            webViewAnimation(searchStreamItem.user_login)

            updateStreamerName(
                searchStreamItem.user_login,
                clientId,
                searchStreamItem.user_id,
                userId
            )
            // this needs to go to the home page
            onNavigate(R.id.action_searchFragment_to_streamFragment)
        }
    ){
        SearchImageWithViewCount(
            url = searchStreamItem.thumbnail_url,
            height = height,
            width = width,
            viewCount = searchStreamItem.viewer_count,
            density =density
        )
        SearchTitleWithInfo(
            streamerName = searchStreamItem.user_login,
            streamTitle = searchStreamItem.title,
            gameTitle = searchStreamItem.game_name,
            tags= searchStreamItem.tags ?: listOf()
        )

    }
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
    )
}




@Composable
fun LiveGameLoading(){
    Spacer(modifier =Modifier.height(20.dp))
    CircularProgressIndicator(modifier=Modifier.size(45.dp), color = MaterialTheme.colorScheme.secondary)
    Spacer(modifier =Modifier.fillMaxSize())
}

@Composable
fun LiveGameFailure(){
    Column(
        modifier=Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier =Modifier.height(20.dp))
        Icon(
            modifier = Modifier.size(40.dp),
            painter = painterResource(id =R.drawable.baseline_close_24),
            contentDescription = "failed request",
            tint =MaterialTheme.colorScheme.onPrimary.copy(0.6f),
        )
        Text("Failed Request. Please try again",color =MaterialTheme.colorScheme.onPrimary.copy(0.6f),)
        Button(
            onClick = {  },
            colors =  ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            shape = RoundedCornerShape(4.dp)
        )
        {
            Text(text = "Retry", color = MaterialTheme.colorScheme.onSecondary, fontSize = MaterialTheme.typography.headlineMedium.fontSize)
        }

        Spacer(modifier =Modifier.fillMaxSize())
    }

}


@Composable
fun CategoryModalHeader(
    gameInfoResponse: Response<Game?>,
    gameTitle: String,
    closeModal:()->Unit,
    height:Int,
    width: Int,
    density: Float,
){
    Box(

    ){
        Icon(
            painter = painterResource(id =R.drawable.baseline_close_24),
            contentDescription = "closing the modal",
            modifier = Modifier.align(Alignment.TopEnd).size(35.dp).clickable { closeModal() },
            tint = MaterialTheme.colorScheme.onPrimary
        )
        when(gameInfoResponse){
            is Response.Loading->{
                GameInformationHeaderLoading(gameTitle,height, width, density)
            }
            is Response.Success->{
                val data = gameInfoResponse.data
                if(data != null){
                    GameInformationHeaderSuccess(
                        gameTitle,
                        data,
                        height, width, density
                    )
                }else{
                    GameInformationHeaderFailed(gameTitle,height, width, density)

                }
            }
            is Response.Failure->{
                GameInformationHeaderFailed(gameTitle,height, width, density)
            }
        }
    }

}
@Composable
fun GameInformationHeaderLoading(
    gameTitle: String,
    height:Int,
    width:Int,
    density:Float
){
    val adjustedHeight = (height/density) * 1.2
    val adjustedWidth = (width/density) /1.5
    Row(modifier= Modifier
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ){
        SubcomposeAsyncImage(
            modifier = Modifier
                .height(adjustedHeight.dp)
                .width(adjustedWidth.dp),
            model = "https://static-cdn.jtvnw.net/ttv-static/404_boxart.jpg",
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
    game:Game,
    height:Int,
    width:Int,
    density:Float
){
    val adjustedHeight = (height/density) *1.2
    val adjustedWidth = (width/density) /1.5
    Row(modifier= Modifier
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ){
        SubcomposeAsyncImage(
            modifier = Modifier
                .height(adjustedHeight.dp)
                .width(adjustedWidth.dp),
            model = game.box_art_url,
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
        Spacer(modifier = Modifier.width(10.dp))
        Column() {
            Text(gameTitle,color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineLarge.fontSize)
        }

    }
}
@Composable
fun GameInformationHeaderFailed(
    gameTitle: String,
    height:Int,
    width:Int,
    density:Float
){

    val adjustedHeight = (height/density) * 1.2
    val adjustedWidth = (width/density) /1.5

    Row(modifier= Modifier
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ){
        SubcomposeAsyncImage(
            modifier = Modifier
                .height(adjustedHeight.dp)
                .width(adjustedWidth.dp),
            model = "https://static-cdn.jtvnw.net/ttv-static/404_boxart.jpg",
            loading = {
                Column(modifier = Modifier
                    .height((adjustedHeight).dp)
                    .width((adjustedWidth).dp)
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
fun SearchStreamLanguage(
    selectedLanguage:String?,
    changeSelectedLanguage:(String)->Unit,
){
    var expanded by remember { mutableStateOf(false) }
    val languages = listOf("American Sign Language","Arabic","Bulgarian","Catalan","Chinese","Czech","Danish","Dutch","English","Finish","French","German",
        "German","Greek","Hindi","Hungarian","Indonesian","Italian","Japanese","Korean","Malay","Norwegian","Polish","Portuguese","Romanian",
        "Russian","Slovak","Spanish","Swedish","Tagalog","Thai","Turkish","Ukrainian","Vietnamese")

    val ellipsisTransformation = remember {
        VisualTransformation { text ->
            val trimmedText = if (text.text.length > 10) {
                text.text.take(10) + "…" // Adjust the character limit as needed
            } else {
                text.text
            }

            TransformedText(
                text = AnnotatedString(trimmedText),
                offsetMapping = OffsetMapping.Identity
            )
        }
    }
    Column(){
        Box(modifier = Modifier) {

            OutlinedTextField(
                modifier = Modifier.width(200.dp)
                    .clickable {
                        expanded = true
                    },
                enabled = false,
                //todo: this is what is shown to the user as the selected choice
                value = selectedLanguage ?: "English",
                onValueChange = { },
                label = { },
                colors = TextFieldDefaults.colors(
                    disabledTextColor = Color.White,
                    disabledContainerColor = Color.DarkGray,
                    disabledTrailingIconColor = Color.Unspecified,
                    disabledLabelColor = Color.Unspecified,
                    disabledPlaceholderColor = Color.Unspecified,
                    disabledSupportingTextColor = Color.Unspecified,
                    disabledPrefixColor = Color.Unspecified,
                    disabledSuffixColor = Color.Unspecified
                ),
                trailingIcon = {
                    if (expanded) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_keyboard_arrow_up_24),
                            contentDescription = "Content Classification open"
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.keyboard_arrow_down_24),
                            contentDescription = "Content Classification closed"
                        )
                    }
                },
                maxLines = 1,
                visualTransformation = ellipsisTransformation


            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.DarkGray
                    )
                    .padding(horizontal = 10.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column() {
                        for (item in languages) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                item,
                                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                                color = Color.White,
                                modifier = Modifier
                                    .clickable {
                                        changeSelectedLanguage(item)
                                        expanded = false
                                    }
                            )

                        }
                    }


                    Icon(
                        painter = painterResource(id = R.drawable.baseline_close_24),
                        contentDescription = "Close language menu",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clickable {
                                expanded = false
                            }
                    )
                }

            }
        }

    }
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

