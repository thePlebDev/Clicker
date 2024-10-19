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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.presentation.sharedViews.LogoutDialog


@Composable
fun SearchViewComponent(){
    //still need to add the pager and the header
    val listOfThings = listOf<String>("One","Two","Three","Four","Five","size","seven","One","Two","Three","Four","Five","size","seven")
    Box(
        modifier = Modifier.padding(horizontal = 10.dp, vertical =5.dp)
    ){
        LazyVerticalGrid(

            columns = GridCells.Fixed(3),
            modifier= Modifier
                .fillMaxSize()
                .padding(horizontal = 5.dp)
                .background(MaterialTheme.colorScheme.primary),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(listOfThings.size) { photo ->
                Column(){
                    Column(
                        modifier = Modifier.height(200.dp).width(180.dp).background(Color.Red)
                    ){}
                    Text("Software and Game Development",
                        maxLines=1,
                        color = Color.Red,
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                        overflow = TextOverflow.Ellipsis)

                }

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