package com.example.clicker.presentation.stream.views.chat.chatSettings

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkOut
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.TextField
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.R
import com.example.clicker.presentation.modView.ImmutableModeList
import com.example.clicker.presentation.modView.ListTitleValue
import com.example.clicker.presentation.sharedViews.SwitchWithIcon
import com.example.clicker.presentation.stream.models.AdvancedChatSettings
import com.example.clicker.presentation.stream.views.chat.ExampleText
import com.example.clicker.presentation.stream.views.chat.SliderAdvanced
import kotlinx.coroutines.launch


val followerModeList =listOf(
    ListTitleValue("Off",null), ListTitleValue("0 minutes(any followers)",0),
    ListTitleValue("10 minutes(most used)",10),
    ListTitleValue("30 minutes",30), ListTitleValue( "1 hour",60),
    ListTitleValue("1 day",1440),
    ListTitleValue("1 week",10080 ),
    ListTitleValue("1 month",43200 ),
    ListTitleValue("3 months",129600 )

)
//1 week 10080
//1 month 43200
//3 months 129600

val slowModeList =listOf(
    ListTitleValue("Off",null),
    ListTitleValue("3s",3),
    ListTitleValue("5s",5),
    ListTitleValue("10s",10),
    ListTitleValue( "20s",20),
    ListTitleValue("30s",30),
    ListTitleValue("60s",60 )
)


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatSettingsColumn(
    advancedChatSettings: AdvancedChatSettings,
    changeAdvancedChatSettings: (AdvancedChatSettings)->Unit,
    changeNoChatMode:(Boolean)->Unit,

    followerModeListImmutable: ImmutableModeList,
    slowModeListImmutable: ImmutableModeList,
    selectedFollowersModeItem: ListTitleValue,
    changeSelectedFollowersModeItem: (ListTitleValue) -> Unit,

    selectedSlowModeItem: ListTitleValue,
    changeSelectedSlowModeItem: (ListTitleValue) -> Unit,
    chatSettingsEnabled:Boolean,
    emoteOnly:Boolean,
    setEmoteOnly:(Boolean) ->Unit,
    subscriberOnly:Boolean,
    setSubscriberOnly:(Boolean) ->Unit,

    badgeSize:Float,
    changeBadgeSize:(Float)->Unit,
    emoteSize:Float,
    changeEmoteSize:(Float)->Unit,
    usernameSize:Float,
    changeUsernameSize:(Float)->Unit,
    messageSize:Float,
    changeMessageSize:(Float)->Unit,
    lineHeight: Float,
    changeLineHeight:(Float)->Unit,
    customUsernameColor: Boolean,
    changeCustomUsernameColor: (Boolean)->Unit

){
    Log.d("ChatSettingsColumn","Recomping")

    val scope = rememberCoroutineScope()
    val secondaryColor =MaterialTheme.colorScheme.secondary

    //modifier =Modifier.weight(1f)
    val pagerState = rememberPagerState(pageCount = {
        2
    })
    val underlineModifier = Modifier.drawBehind {
        val strokeWidthPx = 1.dp.toPx()
        val verticalOffset = size.height - 2.sp.toPx()
        drawLine(
            color = secondaryColor,
            strokeWidth = strokeWidthPx,
            start = Offset(0f, verticalOffset),
            end = Offset(size.width, verticalOffset)
        )
    }

    Column(){
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)){
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                "Chat settings", color = MaterialTheme.colorScheme.onPrimary,
                modifier = if (pagerState.currentPage == 0) underlineModifier else Modifier.clickable {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                },
            )
            Spacer(modifier = Modifier.width(25.dp))
           Text(
                "Stream info",
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = if (pagerState.currentPage == 1) underlineModifier else Modifier.clickable {
                    scope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                },
            )

        }



        HorizontalPager(state = pagerState) { page ->
            // Our page content
            when(page) {
                0 -> {
                    ChatSettingsLazyColumn(
                        advancedChatSettings = advancedChatSettings,
                        changeAdvancedChatSettings = {newValue -> changeAdvancedChatSettings(newValue) },
                        changeNoChatMode = {newValue -> changeNoChatMode(newValue) },
                        chatSettingsEnabled = chatSettingsEnabled,
                        followerModeListImmutable = followerModeListImmutable,
                        slowModeListImmutable= slowModeListImmutable,
                        selectedFollowersModeItem=selectedFollowersModeItem,
                        changeSelectedFollowersModeItem ={newValue -> changeSelectedFollowersModeItem(newValue) },
                        selectedSlowModeItem=selectedSlowModeItem,
                        changeSelectedSlowModeItem ={newValue -> changeSelectedSlowModeItem(newValue) },
                        emoteOnly = emoteOnly,
                        setEmoteOnly = {newValue -> setEmoteOnly(newValue) },
                        subscriberOnly =subscriberOnly,
                        setSubscriberOnly={newValue -> setSubscriberOnly(newValue) },

                        badgeSize = badgeSize,
                        changeBadgeSize = {newValue-> changeBadgeSize(newValue)},
                        emoteSize = emoteSize,
                        changeEmoteSize={newValue -> changeEmoteSize(newValue)},
                        usernameSize = usernameSize,
                        changeUsernameSize ={newValue ->changeUsernameSize(newValue)},
                        messageSize = messageSize,
                        changeMessageSize={newValue ->changeMessageSize(newValue)},
                        lineHeight = lineHeight,
                        changeLineHeight = {newValue -> changeLineHeight(newValue)},
                        customUsernameColor = customUsernameColor,
                        changeCustomUsernameColor = {newValue -> changeCustomUsernameColor(newValue)}
                    )
                }
                1->{
                    ChannelInfoLazyColumn()
                }
            }
        }


    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChannelInfoLazyColumn(){


    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.primary)) {
        stickyHeader {
            StickyHeaderColumn("Stream Title")
        }
        item{ChannelInfoTitle() }
        stickyHeader {
            StickyHeaderColumn("Category")
        }
        item{ CircularProgressIndicator()} // change at the end
        stickyHeader {
            StickyHeaderColumn("Stream Tags")
        }
        item{
            ChannelTagsInfo()

        }
        stickyHeader {
            StickyHeaderColumn("Content Classification")
        }
        item{
            ContentClassificationBox()
        }
        stickyHeader {
            StickyHeaderColumn("Stream Language")
        }
        item{
            StreamLanguage()
        }
        item{
            Spacer(modifier =Modifier.height(10.dp))
        }
        item{
            ShareButton()
        }

    }
}
@Composable
fun ShareButton(){

    Box(modifier = Modifier.fillMaxWidth().padding(10.dp)){
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
fun StreamLanguage(){
    var selectedLanguage by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }
    val languages = listOf("American Sign Language","Arabic","Bulgarian","Catalan","Chinese","Czech","Danish","Dutch","English","Finish","French","German",
    "German","Greek","Hindi","Hungarian","Indonesian","Italian","Japanese","Korean","Malay","Norwegian","Polish","Portuguese","Romanian",
    "Russian","Slovak","Spanish","Swedish","Tagalog","Thai","Turkish","Ukrainian","Vietnamese","Other")

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
            value = selectedLanguage?:"Select language" ,
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
                .padding(horizontal = 10.dp)
        ){
            Box(modifier = Modifier.fillMaxWidth()){
                Column() {
                    for(item in languages){
                        Spacer(modifier =Modifier.height(10.dp))
                        Text(
                            item,
                            fontSize =MaterialTheme.typography.headlineMedium.fontSize,
                            color = Color.White,
                            modifier = Modifier
                                .clickable {
                                    selectedLanguage = item
                                    expanded = false
                                }
                        )

                    }
                }


                Icon(
                    painter = painterResource(id =R.drawable.baseline_close_24),
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

@Composable
fun ContentClassificationBox() {
    var expanded by remember { mutableStateOf(false) }

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
                                ", discussions of illegal drugs"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ContentClassificationTextMenuItem(
                        setExpanded={newValue -> },
                        title = "Gambling",
                        selectText={},
                        subtitle = "Participating in online or in-person gambling , poker or fantasy sports, that involve the exchange of real money"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ContentClassificationTextMenuItem(
                        setExpanded={newValue -> },
                        title = "Significant Profanity or Vulgarity",
                        selectText={},
                        subtitle = "Prolonged, and repeated use of obscenities, profanities, and vulgarities, especially as a regular part" +
                                "of speech"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ContentClassificationTextMenuItem(
                        setExpanded={newValue -> },
                        title = "Sexual Themes",
                        selectText={},
                        subtitle = "Content that focuses on sexualized physical attributes and activities, sexual topics, or experiences"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ContentClassificationTextMenuItem(
                        setExpanded={newValue -> },
                        title = "Violent and Graphic depictions",
                        selectText={},
                        subtitle = "Simulations and/or depictions of realistic violence, gore, extreme injury or death"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ContentClassificationTextMenuItem(
                        setExpanded={newValue -> },
                        title = "Mature-rated game",
                        selectText={},
                        subtitle = "Games that are rated Mature or less suitable for a younger audience"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                Icon(
                    painter = painterResource(id =R.drawable.baseline_close_24),
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
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChannelTagsInfo(){
    var text by remember { mutableStateOf("Hello") }
    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.secondary,
        backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
    )
    Column(modifier= Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp)){
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
            Text("Tags",color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineMedium.fontSize)
            Text("25",color = MaterialTheme.colorScheme.onPrimary.copy(0.7f))
        }
        CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {

            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = false,
                maxLines = 5,
                value = text,

                shape = RoundedCornerShape(8.dp),
                onValueChange = { text = it},
                colors = androidx.compose.material.TextFieldDefaults.textFieldColors(
                    textColor = Color.White,
                    backgroundColor = Color.DarkGray,
                    cursorColor = Color.White,
                    disabledLabelColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                placeholder = {
                              Text("Enter your own tag")
                },
                trailingIcon = {}
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                verticalArrangement = Arrangement.spacedBy(10.dp)

            ) {
                Box(modifier= Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.DarkGray)
                    .padding(5.dp)
                ){
                    Row(){
                        Text("Talking",color = Color.White)
                        Icon(
                            painter = painterResource(id =R.drawable.baseline_close_24),
                            contentDescription = "Remove tag",
                            tint = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.width(5.dp))
                Box(modifier= Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.DarkGray)
                    .padding(5.dp)){
                    Row(){
                        Text("Gaming",color = Color.White)
                        Icon(
                            painter = painterResource(id =R.drawable.baseline_close_24),
                            contentDescription = "Remove tag",
                            tint = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.width(5.dp))
                Box(modifier= Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.DarkGray)
                    .padding(5.dp)){
                    Row(){
                        Text("Eating and talking",color = Color.White)
                        Icon(
                            painter = painterResource(id =R.drawable.baseline_close_24),
                            contentDescription = "Remove tag",
                            tint = Color.White
                        )
                    }
                }
                Box(modifier= Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.DarkGray)
                    .padding(5.dp)){
                    Row(){
                        Text("More things and more gami",color = Color.White)
                        Icon(
                            painter = painterResource(id =R.drawable.baseline_close_24),
                            contentDescription = "Remove tag",
                            tint = Color.White
                        )
                    }
                }
            }


    }
}
@Composable
fun ChannelInfoTitle(){
    var text by remember { mutableStateOf("Hello") }
    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.secondary,
        backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
    )
    Column(modifier= Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp)){
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
            Text("Title",color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineMedium.fontSize)
            Text("140",color = MaterialTheme.colorScheme.onPrimary.copy(0.7f))
        }
        Spacer(modifier = Modifier.height(5.dp))
        CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {

            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = false,
                maxLines = 5,
                value = text,

                shape = RoundedCornerShape(8.dp),
                onValueChange = { text = it},
                colors = androidx.compose.material.TextFieldDefaults.textFieldColors(
                    textColor = Color.White,
                    backgroundColor = Color.DarkGray,
                    cursorColor = Color.White,
                    disabledLabelColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                placeholder = {},
                trailingIcon = {}
            )
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatSettingsLazyColumn(
    advancedChatSettings: AdvancedChatSettings,
    changeAdvancedChatSettings: (AdvancedChatSettings)->Unit,
    changeNoChatMode:(Boolean)->Unit,

    followerModeListImmutable: ImmutableModeList,
    slowModeListImmutable: ImmutableModeList,
    selectedFollowersModeItem: ListTitleValue,
    changeSelectedFollowersModeItem: (ListTitleValue) -> Unit,

    selectedSlowModeItem: ListTitleValue,
    changeSelectedSlowModeItem: (ListTitleValue) -> Unit,
    chatSettingsEnabled:Boolean,
    emoteOnly:Boolean,
    setEmoteOnly:(Boolean) ->Unit,
    subscriberOnly:Boolean,
    setSubscriberOnly:(Boolean) ->Unit,

    badgeSize:Float,
    changeBadgeSize:(Float)->Unit,
    emoteSize:Float,
    changeEmoteSize:(Float)->Unit,
    usernameSize:Float,
    changeUsernameSize:(Float)->Unit,
    messageSize:Float,
    changeMessageSize:(Float)->Unit,
    lineHeight: Float,
    changeLineHeight:(Float)->Unit,
    customUsernameColor: Boolean,
    changeCustomUsernameColor: (Boolean)->Unit
){
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.primary)) {
        stickyHeader {
            StickyHeaderColumn("Moderator chat settings")
        }
        item{
            EmoteOnlySwitch(
                setExpanded ={newValue -> },
                emoteOnly =emoteOnly,
                setEmoteOnly={newValue ->setEmoteOnly(newValue)},
                switchEnabled=chatSettingsEnabled
            )
        }

        item{
            SubscriberOnlySwitch(
                setExpanded ={newValue -> },
                subscriberOnly = subscriberOnly,
                setSubscriberOnly = {newValue -> setSubscriberOnly(newValue) },
                switchEnabled=chatSettingsEnabled
            )
        }

        item{
            //todo: these are the modal items the pop up
            SlowModeCheck(
                setExpanded ={newValue -> },
                chatSettingsEnabled=chatSettingsEnabled,
                slowModeList= slowModeList,
                selectedSlowModeItem=selectedSlowModeItem,
                changeSelectedSlowModeItem ={newValue -> changeSelectedSlowModeItem(newValue)},
                titleListImmutable = slowModeListImmutable

            )
        }
        item{
            FollowersOnlyCheck(
                chatSettingsEnabled=chatSettingsEnabled,
                setExpanded ={newValue -> },
                followersOnlyList= followerModeList,
                selectedFollowersModeItem=selectedFollowersModeItem,
                changeSelectedFollowersModeItem ={newValue -> changeSelectedFollowersModeItem(newValue)},
                titleListImmutable = followerModeListImmutable
            )
        }
        stickyHeader {
            StickyHeaderColumn("Advanced chat settings")
        }
        item{
            AdvancedChatSettings(
                advancedChatSettings = advancedChatSettings,
                changeAdvancedChatSettings = {newValue ->changeAdvancedChatSettings(newValue)},
                changeNoChatMode ={newValue ->changeNoChatMode(newValue)}
            )
        }
        stickyHeader {
            Column(){
                StickyHeaderColumn("Chat Experience")
                ExampleText(
                    badgeSize = badgeSize,
                    usernameSize=usernameSize,
                    messageSize = messageSize,
                    emoteSize = emoteSize,
                    lineHeight=lineHeight,
                    customUsernameColor=customUsernameColor
                )
            }
        }


        item{
            SliderAdvanced(
                badgeSize=badgeSize,
                changeBadgeSliderValue={newValue -> changeBadgeSize(newValue)},
                usernameSize=usernameSize,
                changeUsernameSize = {newValue -> changeUsernameSize(newValue)},
                messageSize = messageSize,
                changeMessageSize = {newValue ->changeMessageSize(newValue)},
                emoteSize = emoteSize,
                changeEmoteSize = {newValue -> changeEmoteSize(newValue)},
                lineHeight =lineHeight,
                changeLineHeight = {newValue -> changeLineHeight(newValue)},
                customUsernameColor =customUsernameColor,
                changeCustomUsernameColor ={newValue -> changeCustomUsernameColor(newValue)}
            )
        }

    }

}

@Composable
fun StickyHeaderColumn(
    headerText:String,
    fontSize: TextUnit = MaterialTheme.typography.headlineLarge.fontSize
){
    Column(modifier = Modifier.background(MaterialTheme.colorScheme.primary)){
        Text(headerText,color = MaterialTheme.colorScheme.onPrimary,
            modifier= Modifier.padding(start=15.dp),
            fontSize = fontSize
        )
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.secondary.copy(0.8f), modifier = Modifier.padding(horizontal = 15.dp))
        Spacer(modifier =Modifier.size(10.dp))
    }
}

@Composable
fun AdvancedChatSettings(
    advancedChatSettings: AdvancedChatSettings,
    changeAdvancedChatSettings: (AdvancedChatSettings)->Unit,
    changeNoChatMode:(Boolean)->Unit
){
    Column(modifier = Modifier.fillMaxWidth()) {

       ReSubsSwitch(
            advancedChatSettings=advancedChatSettings,
            changeAdvancedChatSettings={ newValue ->

                changeAdvancedChatSettings(newValue)
            }
        )

        SubsSwitch(
            advancedChatSettings=advancedChatSettings,
            changeAdvancedChatSettings={ newValue ->

                changeAdvancedChatSettings(newValue)
            }
        )
        AnonGiftSubsSwitch(
            advancedChatSettings=advancedChatSettings,
            changeAdvancedChatSettings={ newValue ->

                changeAdvancedChatSettings(newValue)
            }
        )
        GiftSubsSwitch(
            advancedChatSettings=advancedChatSettings,
            changeAdvancedChatSettings={ newValue ->

                changeAdvancedChatSettings(newValue)
            }
        )
        NoChatSwitch(
            advancedChatSettings = advancedChatSettings,
            changeNoChatMode={newValue ->changeNoChatMode(newValue)}
        )
    }
}

@Composable
fun EmoteOnlySwitch(
    setExpanded:(Boolean)->Unit,
    emoteOnly:Boolean,
    setEmoteOnly:(Boolean) ->Unit,
    switchEnabled:Boolean
){
    Log.d("EMOTEONLYSWITCH","emoteOnly -->$emoteOnly")
    DropdownMenuItem(
        onClick = {
            setExpanded(false)
        },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Row(verticalAlignment = Alignment.CenterVertically){
                    Icon(imageVector = Icons.Default.Face, contentDescription ="Emote icon" )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Emotes-only chat", color = MaterialTheme.colorScheme.onPrimary)
                }

                SwitchWithIcon(
                    checkedValue = emoteOnly,
                    changeCheckedValue = {newValue->setEmoteOnly(newValue)},
                    icon = Icons.Filled.Check,
                    switchEnabled =switchEnabled
                )


            }
        }
    )
}

@Composable
fun SubscriberOnlySwitch(
    setExpanded:(Boolean)->Unit,
    subscriberOnly:Boolean,
    setSubscriberOnly:(Boolean) ->Unit,
    switchEnabled:Boolean

){
    DropdownMenuItem(
        onClick = {
            setExpanded(false)
        },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Row(verticalAlignment = Alignment.CenterVertically){
                    Icon(imageVector =Icons.Default.Person, contentDescription ="Emote icon" )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Subscriber-only chat", color = MaterialTheme.colorScheme.onPrimary)
                }
                SwitchWithIcon(
                    checkedValue = subscriberOnly,
                    changeCheckedValue = {newValue->setSubscriberOnly(newValue)},
                    icon = Icons.Filled.Check,
                    switchEnabled =switchEnabled
                )
            }
        }
    )
}


@Composable
fun SlowModeCheck(
    setExpanded: (Boolean) -> Unit,
    chatSettingsEnabled:Boolean,
    selectedSlowModeItem:ListTitleValue,
    changeSelectedSlowModeItem:(ListTitleValue)->Unit,
    slowModeList: List<ListTitleValue>,
    titleListImmutable: ImmutableModeList,
){

    DropdownMenuItem(
        onClick = {
            setExpanded(false)
        },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Row(verticalAlignment = Alignment.CenterVertically){
                    Icon(painter = painterResource(id = R.drawable.baseline_hourglass_empty_24), contentDescription = "slow mode identifier")
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Slow Mode", color = MaterialTheme.colorScheme.onPrimary)
                }

                EmbeddedDropDownMenu(
                    titleList =slowModeList,
                    selectedItem = selectedSlowModeItem,
                    changeSelectedItem = {selectedValue ->changeSelectedSlowModeItem(selectedValue) },
                    chatSettingsEnabled=chatSettingsEnabled,
                    titleListImmutable =titleListImmutable
                )
            }
        }
    )
}

@Composable
fun FollowersOnlyCheck(
    setExpanded: (Boolean) -> Unit,
    chatSettingsEnabled:Boolean,
    followersOnlyList: List<ListTitleValue>,
    titleListImmutable: ImmutableModeList,
    selectedFollowersModeItem:ListTitleValue,
    changeSelectedFollowersModeItem:(ListTitleValue)->Unit,
){
    DropdownMenuItem(
        onClick = {
            setExpanded(false)
        },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Row(verticalAlignment = Alignment.CenterVertically){
                    Icon(imageVector =Icons.Default.Favorite, contentDescription ="Emote icon" )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Followers-only", color = MaterialTheme.colorScheme.onPrimary)
                }
                // Text("Off", color = MaterialTheme.colorScheme.onPrimary)
                EmbeddedDropDownMenu(
                    titleList =followersOnlyList,
                    selectedItem = selectedFollowersModeItem,
                    changeSelectedItem = {selectedItem ->changeSelectedFollowersModeItem(selectedItem)},
                    chatSettingsEnabled =chatSettingsEnabled,
                    titleListImmutable=titleListImmutable
                )
            }
        }
    )
}

@Composable
fun EmbeddedDropDownMenu(
    titleList: List<ListTitleValue>, //this is the list shown to the user
    titleListImmutable: ImmutableModeList,
    selectedItem:ListTitleValue,
    changeSelectedItem:(ListTitleValue)->Unit,
    chatSettingsEnabled:Boolean
) {

    //var text by remember { mutableStateOf("Off") }
    var expanded by remember {
        mutableStateOf(false)
    }
    //todo: change this value to actual title stored in the viewModel

    Box(modifier = Modifier.wrapContentSize(Alignment.BottomCenter)){

        OutlinedTextField(
            modifier = Modifier
                .width(200.dp)
                .clickable {
                    if (chatSettingsEnabled) {
                        expanded = true
                    }
                },
            enabled = false,
            //todo: this is what is shown to the user as the selected choice
            value = selectedItem.title,
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
            )
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
            for (item in titleListImmutable.modeList){
                TextMenuItem(
                    setExpanded={newValue -> expanded=newValue},
                    title = item.title,
                    selectText={
                        //todo: changeSelectedTitle(it.title)
                        changeSelectedItem(item)
                    }
                )
            }

        }

    }


}
@Composable
fun TextMenuItem(
    setExpanded: (Boolean) -> Unit,
    selectText:()->Unit,
    title:String,
){
    DropdownMenuItem(
        onClick = {
            setExpanded(false)
            selectText()
        },
        text = {
            Text(title, color = MaterialTheme.colorScheme.onPrimary)
        }
    )
}
@Composable
fun ContentClassificationTextMenuItem(
    setExpanded: (Boolean) -> Unit,
    selectText:()->Unit,
    title:String,
    subtitle:String,
){
    DropdownMenuItem(
        onClick = {
            setExpanded(false)
            selectText()
        },
        text = {
            Column(
            ){
                Row(verticalAlignment = Alignment.CenterVertically){
                    CustomCheckBox()
                    Spacer(modifier=Modifier.width(10.dp))
                    Text(title, color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineMedium.fontSize)
                }
                Text(subtitle, color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    )
}

@Composable
fun CustomCheckBox(){
    var checked by remember { mutableStateOf(false) }
    val onPrimaryColor = if(checked) MaterialTheme.colorScheme.secondary else  MaterialTheme.colorScheme.onPrimary
    val primaryColor = MaterialTheme.colorScheme.primary
    val animatedColor by animateColorAsState(
        if (checked) MaterialTheme.colorScheme.secondary else primaryColor,
        label = "color"
    )

    Box(modifier = Modifier
        .border(1.dp, onPrimaryColor, RoundedCornerShape(5.dp))
        .height(20.dp)
        .width(20.dp)
        .drawBehind {
            val cornerRadius =
                5.dp.toPx() // Convert DP to pixels for the rounded corners
            drawRoundRect(
                color = animatedColor,
                cornerRadius = CornerRadius(cornerRadius, cornerRadius)
            )
        }
        .clip(
            RoundedCornerShape(5.dp)
        )
        .clickable {
            checked = !checked
        }
    ){
        Column( modifier = Modifier.align(Alignment.Center),) {
            AnimatedVisibility(
                checked,
                enter = scaleIn(initialScale = 0.5f), // Scale in animation
                exit = shrinkOut(shrinkTowards = Alignment.Center)
            ) {
                Icon(painter = painterResource(R.drawable.baseline_check_24),
                    contentDescription = "checked",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

    }
}

@Composable
fun SubsSwitch(
    advancedChatSettings: AdvancedChatSettings,
    changeAdvancedChatSettings: (AdvancedChatSettings) ->Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Sub messages",
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            color = MaterialTheme.colorScheme.onPrimary
        )
        SwitchWithIcon(
            checkedValue = advancedChatSettings.showSubs,
            changeCheckedValue = {newValue->
                val value =advancedChatSettings.copy(showSubs = newValue)
                changeAdvancedChatSettings(value)
                                 },
            icon = Icons.Filled.Check,
        )
    }
}

@Composable
fun GiftSubsSwitch(
    advancedChatSettings: AdvancedChatSettings,
    changeAdvancedChatSettings: (AdvancedChatSettings) ->Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Gift Sub messages",
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            color = MaterialTheme.colorScheme.onPrimary
        )
        SwitchWithIcon(
            checkedValue = advancedChatSettings.showGiftSubs,
            changeCheckedValue = {newValue->
                val value =advancedChatSettings.copy(showGiftSubs = newValue)
                changeAdvancedChatSettings(value)
            },
            icon = Icons.Filled.Check,
        )
    }
}


@Composable
fun AnonGiftSubsSwitch(
    advancedChatSettings: AdvancedChatSettings,
    changeAdvancedChatSettings: (AdvancedChatSettings) ->Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Anonymous Gift Sub messages",
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            color = MaterialTheme.colorScheme.onPrimary
        )

        SwitchWithIcon(
            checkedValue = advancedChatSettings.showAnonSubs,
            changeCheckedValue = {newValue->
                val value =advancedChatSettings.copy(showAnonSubs = newValue)
                changeAdvancedChatSettings(value)
            },
            icon = Icons.Filled.Check,
        )

    }
}
//"Re-Sub messages"

@Composable
fun ReSubsSwitch(
    advancedChatSettings: AdvancedChatSettings,
    changeAdvancedChatSettings: (AdvancedChatSettings) ->Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Re-Sub messages",
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            color = MaterialTheme.colorScheme.onPrimary
        )

        SwitchWithIcon(
            checkedValue = advancedChatSettings.showReSubs,
            changeCheckedValue = {newValue->
                val value =advancedChatSettings.copy(showReSubs = newValue)
                changeAdvancedChatSettings(value)
            },
            icon = Icons.Filled.Check,
        )
    }
}

@Composable
fun NoChatSwitch(
    advancedChatSettings: AdvancedChatSettings,
    changeNoChatMode:(Boolean)->Unit,
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("No chat mode",
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            color = MaterialTheme.colorScheme.onPrimary
        )
        SwitchWithIcon(
            checkedValue = advancedChatSettings.noChatMode,
            changeCheckedValue = {newValue->
                changeNoChatMode(newValue)
            },
            icon = Icons.Filled.Check,
        )
    }
}