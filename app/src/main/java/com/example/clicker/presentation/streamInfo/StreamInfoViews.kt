package com.example.clicker.presentation.streamInfo

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.clicker.R
import com.example.clicker.network.clients.Game
import com.example.clicker.presentation.stream.views.chat.chatSettings.ContentClassificationTextMenuItem
import com.example.clicker.presentation.stream.views.chat.chatSettings.CustomCheckBox
import com.example.clicker.util.Response


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChannelInfoLazyColumn(
    streamTitle:String,
    changeStreamTitle:(String)->Unit,
    titleLength:Int,

    tagList:List<String>,
    tagTitle:String,
    tagTitleLength:Int,
    changeTagTitle:(String)->Unit,
    addTag:(String)->Unit,
    removeTag:(String)->Unit,

    contentClassificationCheckBox: ContentClassificationCheckBox,
    changeContentClassification:(ContentClassificationCheckBox)->Unit,

    selectedLanguage:String?,
    changeSelectedLanguage:(String)->Unit,
    closeChannelInfoModal:()->Unit,

    checkedBrandedContent:Boolean,
    changeBrandedContent:(Boolean)->Unit,

    categoryResponse: Response<Game?>,
    refreshChannelInformation:()->Unit,

    removeCategory:() ->Unit,
){


    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.primary)) {
        stickyHeader {
            SaveCloseChannelInformationHeader(
                closeModal={closeChannelInfoModal()}
            )
        }

        item{
            ChannelInfoTitle(
                streamTitle=streamTitle,
                changeStreamTitle={newValue ->changeStreamTitle(newValue)},
                titleLength=titleLength
            )
        }
        item {
            GameCategory(
                categoryResponse=categoryResponse,
                refreshChannelInformation= { refreshChannelInformation() },
                removeCategory={removeCategory()}
            )
        }

        item{
            ChannelTagsInfo(
                tagList =tagList,
                tagTitle=tagTitle,
                tagTitleLength=tagTitleLength,
                changeTagTitle={newValue ->changeTagTitle(newValue)},
                addTag={newTag ->addTag(newTag)},
                removeTag={oldTag -> removeTag(oldTag)}
            )

        }
        item{
            Spacer(modifier = Modifier.height(20.dp))
        }

        item{
            ContentClassificationBox(
                contentClassificationCheckBox=contentClassificationCheckBox,
                changeContentClassification={newClassification ->changeContentClassification(newClassification)}
            )
        }
        item{
            Spacer(modifier = Modifier.height(20.dp))
        }

        item{
            StreamLanguage(
                selectedLanguage =selectedLanguage,
                changeSelectedLanguage = {newValue ->
                    changeSelectedLanguage(newValue)
                }
            )
        }
        item{
            Spacer(modifier = Modifier.height(20.dp))
        }
        item{
            BrandedContentInfo(
                checkedBrandedContent=checkedBrandedContent,
                changedCheckedBrandedContent={newValue -> changeBrandedContent(newValue)}
            )
        }


        item{
            Spacer(modifier = Modifier.height(10.dp))
        }
        item{
            ShareButton()
        }

    }
}

@Composable
fun GameCategory(
    categoryResponse: Response<Game?>,
    refreshChannelInformation:()->Unit,
    removeCategory:() ->Unit,
){
    Column(
        modifier = Modifier.padding(10.dp)
    ) {
        Text("Category",color = MaterialTheme.colorScheme.onPrimary,
            fontSize = MaterialTheme.typography.headlineMedium.fontSize
        )
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.secondary.copy(0.8f))
        Spacer(modifier = Modifier.height(5.dp))
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier.fillMaxWidth(),

        ) {

                when(val data = categoryResponse){
                    is Response.Loading ->{
                        Box( modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                        ){
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }
                    is Response.Success->{
                        if(data.data != null){

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.secondary,
                                        RoundedCornerShape(5.dp)
                                    )
                                    .height(72.dp)
                            ){

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    SubcomposeAsyncImage(
                                        modifier = Modifier
                                            .height(72.dp)
                                            .width(52.dp)
                                            .clip(RoundedCornerShape(5.dp)),
                                        model = data.data.box_art_url,
                                        loading = {
                                            Column(modifier = Modifier
                                                .height(72.dp)
                                                .width(52.dp)
                                                .background(MaterialTheme.colorScheme.primary),
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ){
                                                CircularProgressIndicator()
                                            }
                                        },
                                        contentDescription ="Loading thumbnail of stream game"
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(data.data.name,
                                        color = Color.White,
                                        fontSize = MaterialTheme.typography.headlineMedium.fontSize,maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_close_24),
                                    contentDescription ="delete category" ,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(10.dp)
                                        .clickable {
                                            removeCategory()
                                        },
                                    tint = Color.White
                                )

                            }

                        }else{
                            SearchCategories()
                        }

                    }
                    is Response.Failure ->{
                        RefreshCategoryButton(
                            refreshChannelInformation={refreshChannelInformation()}
                        )
                    }

                }


           // }


        }

    }
}

@Composable
fun SearchCategories(){
    var text by remember { mutableStateOf("") }
    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.secondary,
        backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
    )


    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        Column(){
            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true,

                value = text,

                shape = RoundedCornerShape(5.dp),
                onValueChange = {
                    text = it
                },
                colors = androidx.compose.material.TextFieldDefaults.textFieldColors(
                    textColor = Color.White,
                    backgroundColor = Color.DarkGray,
                    cursorColor = Color.White,
                    disabledLabelColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                placeholder = {
                    Text("Search Categories",color = Color.White)
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Search category",
                        modifier = Modifier
                            .size(35.dp)
                            .clickable {
                                //addTag(tagTitle)
                            }
                            .padding(start = 5.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            )

            //this will be the loading section
            SearchCategoryItemRow()
            SearchCategoryItemRow()
            SearchCategoryItemRow()

        }


    }

}

@Composable
fun SearchCategoryItemRow(){
    Column() {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(0.6f),
                RoundedCornerShape(5.dp)
            ),
            verticalAlignment = Alignment.CenterVertically
        ){
            SubcomposeAsyncImage(
                modifier = Modifier
                    .height(72.dp)
                    .width(52.dp)
                    .clip(RoundedCornerShape(5.dp)),
                model = "https://static-cdn.jtvnw.net/ttv-boxart/509658-52x72.jpg",
                loading = {
                    Column(modifier = Modifier
                        .height(72.dp)
                        .width(52.dp)
                        .background(MaterialTheme.colorScheme.primary),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        CircularProgressIndicator()
                    }
                },
                contentDescription ="Loading thumbnail of stream game"
            )

            Spacer(modifier = Modifier.width(10.dp))
            Text("Fortnite",
                color = Color.White,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }

}

@Composable
fun RefreshCategoryButton(
    refreshChannelInformation:()->Unit,
){
    Column(modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text("Failed to get Category",color = Color.Red)
        Button(
            onClick={
                refreshChannelInformation()
            },
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ){

            Text("Refresh", color = MaterialTheme.colorScheme.onSecondary)
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun BrandedContentInfo(
    checkedBrandedContent:Boolean,
    changedCheckedBrandedContent:(Boolean)->Unit,
){
    Column(
        modifier = Modifier.padding(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text("Branded Content",color = MaterialTheme.colorScheme.onPrimary,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize
            )
            CustomCheckBox(
                checked=checkedBrandedContent,
                changeChecked={changedCheckedBrandedContent(!checkedBrandedContent)}
            )
        }
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.secondary.copy(0.8f))
        Text("Let viewers know if your stream features branded content. This includes paid product placement, endorsement, or" +
                " other commercial relationships", color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineSmall.fontSize)
    }
}

@Composable
fun SaveCloseChannelInformationHeader(
    closeModal:()->Unit
){
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.primary)
        .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(painter = painterResource(id = R.drawable.baseline_close_24),
            contentDescription ="close" ,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.clickable {
                closeModal()
            }
        )
        Button(
            onClick={},
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ){
            Text("Save", color = MaterialTheme.colorScheme.onSecondary)
        }

    }
}
@Composable
fun ShareButton(){

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)){
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick={},
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ){
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween

            ){
                Icon(
                    painter = painterResource(id = R.drawable.baseline_rocket_launch_24),
                    contentDescription ="Share link to channel",
                    tint = MaterialTheme.colorScheme.onSecondary
                )
                Text("Share link to channel" ,
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize
                )
                Icon(
                    painter = painterResource(id = R.drawable.baseline_rocket_launch_24),
                    contentDescription ="Share link to channel",
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }


}

@Composable
fun StreamLanguage(
    selectedLanguage:String?,
    changeSelectedLanguage:(String)->Unit,
){
    var expanded by remember { mutableStateOf(false) }
    val languages = listOf("American Sign Language","Arabic","Bulgarian","Catalan","Chinese","Czech","Danish","Dutch","English","Finish","French","German",
        "German","Greek","Hindi","Hungarian","Indonesian","Italian","Japanese","Korean","Malay","Norwegian","Polish","Portuguese","Romanian",
        "Russian","Slovak","Spanish","Swedish","Tagalog","Thai","Turkish","Ukrainian","Vietnamese","Other")

    Column(){

        Text("Stream language",
            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(start=10.dp)
        )
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.secondary.copy(0.8f), modifier = Modifier.padding(horizontal=10.dp))
        Box(modifier = Modifier.wrapContentSize(Alignment.BottomCenter)) {

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .clickable {
                        expanded = true
                    },
                enabled = false,
                //todo: this is what is shown to the user as the selected choice
                value = selectedLanguage ?: "Select language",
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
                }
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
fun ContentClassificationBox(
    contentClassificationCheckBox: ContentClassificationCheckBox,
    changeContentClassification:(ContentClassificationCheckBox)->Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(){

        Text("Content Classification",
            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(start=10.dp)
        )
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.secondary.copy(0.8f), modifier = Modifier.padding(horizontal=10.dp))
        Box(modifier = Modifier.wrapContentSize(Alignment.BottomCenter)){
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .clickable {
                        expanded = true
                    },
                enabled = false,
                //todo: this is what is shown to the user as the selected choice
                value = "Content Classification",
                onValueChange = { },
                label = {  },
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
                    if(expanded){
                        Icon(painter = painterResource(id = R.drawable.baseline_keyboard_arrow_up_24), contentDescription ="Content Classification open" )
                    }else{
                        Icon(painter = painterResource(id = R.drawable.keyboard_arrow_down_24), contentDescription ="Content Classification closed" )
                    }
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.DarkGray
                    )
            ){
                Box(){

                    Column(){
                        Spacer(modifier = Modifier.height(10.dp))
                        ContentClassificationTextMenuItem(
                            setExpanded={newValue -> //expanded=newValue
                            },
                            title = "Drugs, Intoxication, or Excessive Tobacco Use",
                            selectText={},
                            subtitle = "Excessive tobacco glorification or promotion, any marijuana consumption/use,legal drug and alcohol induced intoxication" +
                                    ", discussions of illegal drugs",
                            checked = contentClassificationCheckBox.drugsIntoxication,
                            changedChecked = {checked ->
                                val newClassification = contentClassificationCheckBox.copy(drugsIntoxication = checked)
                                Log.d("Changingtheclassification","checked ->${checked}")
                                Log.d("Changingtheclassification","newClassification ->${newClassification}")
                                changeContentClassification(newClassification)
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ContentClassificationTextMenuItem(
                            setExpanded={newValue -> },
                            title = "Gambling",
                            selectText={},
                            subtitle = "Participating in online or in-person gambling , poker or fantasy sports, that involve the exchange of real money",
                            checked = contentClassificationCheckBox.gambling,
                            changedChecked = {checked ->
                                val newClassification = contentClassificationCheckBox.copy(gambling = checked)
                                changeContentClassification(newClassification)
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ContentClassificationTextMenuItem(
                            setExpanded={newValue -> },
                            title = "Significant Profanity or Vulgarity",
                            selectText={},
                            subtitle = "Prolonged, and repeated use of obscenities, profanities, and vulgarities, especially as a regular part" +
                                    "of speech",
                            checked = contentClassificationCheckBox.significantProfanity,
                            changedChecked = {checked ->
                                val newClassification = contentClassificationCheckBox.copy(significantProfanity = checked)
                                changeContentClassification(newClassification)
                            }

                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ContentClassificationTextMenuItem(
                            setExpanded={newValue -> },
                            title = "Sexual Themes",
                            selectText={},
                            subtitle = "Content that focuses on sexualized physical attributes and activities, sexual topics, or experiences" ,
                            checked = contentClassificationCheckBox.sexualThemes,
                            changedChecked = {checked ->
                                val newClassification = contentClassificationCheckBox.copy(sexualThemes = checked)
                                changeContentClassification(newClassification)
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ContentClassificationTextMenuItem(
                            setExpanded={newValue -> },
                            title = "Violent and Graphic depictions",
                            selectText={},
                            subtitle = "Simulations and/or depictions of realistic violence, gore, extreme injury or death",
                            checked = contentClassificationCheckBox.violentGraphic,
                            changedChecked = {checked ->
                                val newClassification = contentClassificationCheckBox.copy(violentGraphic = checked)
                                changeContentClassification(newClassification)
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ContentClassificationTextMenuItem(
                            setExpanded={newValue -> },
                            title = "Mature-rated game",
                            selectText={},
                            subtitle = "Games that are rated Mature or less suitable for a younger audience",
                            checked = contentClassificationCheckBox.matureRatedGame,
                            changedChecked = {checked ->
                                val newClassification = contentClassificationCheckBox.copy(matureRatedGame = checked)
                                changeContentClassification(newClassification)
                            }

                        )
                        Spacer(modifier = Modifier.height(10.dp))
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
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChannelTagsInfo(
    tagList:List<String>,
    tagTitle:String,
    tagTitleLength:Int,
    changeTagTitle:(String)->Unit,
    addTag:(String)->Unit,
    removeTag:(String)->Unit,
){

    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.secondary,
        backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
    )
    Column(modifier= Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp)){
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
            Text("Tags",color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineMedium.fontSize)
            Text("$tagTitleLength",color = MaterialTheme.colorScheme.onPrimary.copy(0.7f))
        }
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.secondary.copy(0.8f))
        Spacer(modifier = Modifier.height(5.dp))
        CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {

            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = false,
                maxLines = 5,
                value = tagTitle,

                shape = RoundedCornerShape(8.dp),
                onValueChange = {
                    val maxLength =24
                    if (tagTitle.length <= maxLength || it.length < tagTitle.length) {
                        changeTagTitle(it)
                    }
                },
                colors = androidx.compose.material.TextFieldDefaults.textFieldColors(
                    textColor = Color.White,
                    backgroundColor = Color.DarkGray,
                    cursorColor = Color.White,
                    disabledLabelColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                placeholder = {
                    Text("Enter your own tag",color = Color.White)
                },
                trailingIcon = {

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Add new tag",
                        modifier = Modifier
                            .size(35.dp)
                            .clickable {
                                addTag(tagTitle)
                            }
                            .padding(start = 5.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )

                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        FlowRow(
            verticalArrangement = Arrangement.spacedBy(10.dp)

        ) {
            for(item in tagList){
                Box(modifier= Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.DarkGray)
                    .padding(5.dp)
                ){
                    Row(){
                        Text(item,color = Color.White)
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_close_24),
                            contentDescription = "Remove tag",
                            tint = Color.White,
                            modifier = Modifier.clickable {
                                removeTag(item)
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.width(5.dp))
            }

        }


    }
}
@Composable
fun ChannelInfoTitle(
    streamTitle:String,
    changeStreamTitle:(String)->Unit,
    titleLength:Int
){
    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.secondary,
        backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
    )
    Column(modifier= Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp)){
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
            Text("Title",color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineMedium.fontSize)
            Text("$titleLength",color = MaterialTheme.colorScheme.onPrimary.copy(0.7f))
        }
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.secondary.copy(0.8f))
        Spacer(modifier = Modifier.height(5.dp))
        CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {

            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = false,
                maxLines = 5,
                value = streamTitle,

                shape = RoundedCornerShape(8.dp),
                onValueChange = {
                    val maxLength =139
                    if (streamTitle.length <= maxLength || it.length < streamTitle.length) {
                        changeStreamTitle(it)
                    }
                },
                colors = androidx.compose.material.TextFieldDefaults.textFieldColors(
                    textColor = Color.White,
                    backgroundColor = Color.DarkGray,
                    cursorColor = Color.White,
                    disabledLabelColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                placeholder = {
                    Text("Enter stream title",color = Color.White)
                },
                trailingIcon = {}
            )
        }

    }
}