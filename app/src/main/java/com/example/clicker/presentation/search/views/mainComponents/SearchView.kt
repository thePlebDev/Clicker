package com.example.clicker.presentation.search.views.mainComponents

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.clicker.R
import com.example.clicker.network.clients.TopGame
import com.example.clicker.presentation.sharedViews.LogoutDialog
import com.example.clicker.util.Response


@Composable
fun SearchViewComponent(
    topGamesListResponse:Response<List<TopGame>>,
    adjustedHeight:Int,
    adjustedWidth:Int
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
                    modifier =Modifier.align(Alignment.TopCenter).size(40.dp),
                    color = MaterialTheme.colorScheme.secondary
                )

            }
            is Response.Success->{
                Log.d("topGamesListResponse","SUCCESS")
                TopGamesLazyGrid(
                    modifier = Modifier.matchParentSize(),
                    topGamesList = topGamesListResponse.data,
                    adjustedHeight = adjustedHeight,
                    adjustedWidth=adjustedWidth
                )

            }
            is Response.Failure->{
                Log.d("topGamesListResponse","FAILED")
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Icon(
                        painterResource(id = R.drawable.baseline_close_24),
                        contentDescription = "no unban requests",
                        modifier = Modifier.size(35.dp),
                        tint = MaterialTheme.colorScheme.onPrimary.copy(0.8f)
                    )
                    Text("Request failed",color = MaterialTheme.colorScheme.onPrimary.copy(0.8f), fontSize = MaterialTheme.typography.headlineMedium.fontSize)
                    Text("Pull to refresh and try again",color = MaterialTheme.colorScheme.onPrimary.copy(0.8f),fontSize = MaterialTheme.typography.headlineSmall.fontSize)

                }

            }
        }

    }

}

@Composable
fun TopGamesLazyGrid(
    modifier:Modifier,
    topGamesList:List<TopGame>,
    adjustedHeight:Int,
    adjustedWidth:Int
){
    LazyVerticalGrid(

        columns = GridCells.Fixed(3),
        modifier= modifier
            .padding(horizontal = 5.dp)
            .background(MaterialTheme.colorScheme.primary),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        items(topGamesList){ topGame ->
            Column(){
                SubcomposeAsyncImage(
                    modifier = Modifier.height(200.dp).width(180.dp),
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

        }

    }
}

@Composable
fun SearchBarUI(){
    StylizedTextField()
}
//after THis needs to go inside of a new file
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StylizedTextField(){

    Log.d("StylizedTextFieldRecomp","RECOMP")



    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.secondary,
        backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
    )
    var text by remember { mutableStateOf("") }



    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {

        Box(
            modifier = Modifier.padding(horizontal = 10.dp, vertical =5.dp)
        ){


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


}