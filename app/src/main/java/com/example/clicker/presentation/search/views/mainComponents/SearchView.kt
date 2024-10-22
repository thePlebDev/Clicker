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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.clicker.R
import com.example.clicker.network.clients.TopGame
import com.example.clicker.presentation.sharedViews.ErrorScope
import com.example.clicker.presentation.sharedViews.LogoutDialog
import com.example.clicker.presentation.stream.views.chat.chatSettings.TextMenuItem
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
                    categoryDoubleClickedRemove={id->categoryDoubleClickedRemove(id)}
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
                    categoryDoubleClickedRemove={}
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
){

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier= modifier
            .padding(horizontal = 5.dp)
            .background(MaterialTheme.colorScheme.primary),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {

        if(!pinned){
            items(topGamesList){ topGame ->
                var isVisible by remember { mutableStateOf(false) }
                // Animate the scale for smooth appearance
                Box(){
                    Column(
                        modifier =Modifier .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    categoryDoubleClickedAdd(topGame.id) // Show the icon on double tap
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

