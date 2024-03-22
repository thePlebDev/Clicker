package com.example.clicker.presentation.stream.views.streamManager

import android.util.Log
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.clicker.R
import com.example.clicker.presentation.stream.views.isScrolledToEnd

object ModView {

    @Composable
    fun SectionHeaderRow(title:String,){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,

            ) {
            Text(
                title,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize
            )

        }
    }
    @Composable
    fun SectionHeaderIconRow(title:String){
        var expanded by remember { mutableStateOf(false) }
        //todo: animate the icon change
        Box(){
            DropdownDemo(
                expanded,
                setExpanded ={newValue -> expanded = newValue}
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray)
                    .padding(horizontal = 20.dp)
                ,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                Text(
                    title,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize
                )
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary.copy(.3f))
                        .padding(horizontal = 5.dp)
                        .clickable {
                            expanded = true
                        },
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(
                        imageVector =Icons.Default.Settings,
                        contentDescription ="Settings"
                    )
                    Text("Modes",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                        modifier = Modifier.padding(horizontal = 5.dp)
                    )
                    Icon(
                        imageVector =if(expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp ,
                        contentDescription ="Settings"
                    )
                }

            }
        }





    }

    @Composable
    fun DropdownDemo(
        expanded:Boolean,
        setExpanded:(Boolean)->Unit
    ) {

        DropdownMenu(
                expanded = expanded,
                onDismissRequest = { setExpanded(false) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.DarkGray
                    )
            ) {
                Text("THEPLEBDEV CHANNEL MODES",color = Color.White,modifier=Modifier.padding(start=13.dp,bottom=13.dp))


            EmoteOnlySwitch(
                setExpanded ={newValue -> setExpanded(newValue)}
            )
            SubscriberOnlySwitch(
                setExpanded ={newValue -> setExpanded(newValue)}
            )
            FollowersOnlyCheck(
                setExpanded ={newValue -> setExpanded(newValue)}
            )
            SlowModeCheck(
                setExpanded ={newValue -> setExpanded(newValue)}
            )
            Spacer(modifier =Modifier.height(10.dp))



            }
    }

    @Composable
    fun FollowersOnlyCheck(
        setExpanded: (Boolean) -> Unit
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
                        Text("Followers-only chat", color = MaterialTheme.colorScheme.onPrimary)
                    }
                   // Text("Off", color = MaterialTheme.colorScheme.onPrimary)
                    EmbeddedDropDownMenu(
                        titleList =listOf(
                            "Off","0 minutes(any followers)","10 minutes(most used)",
                            "30 minutes", "1 hour","1 day","1 month","3 months"
                        )
                    )
                }
            }
        )
    }

    @Composable
    fun EmbeddedDropDownMenu(
        titleList: List<String>
    ) {

        var text by remember { mutableStateOf("Off") }
        var expanded by remember {
            mutableStateOf(false)
        }
        var selectedIndex by remember { mutableStateOf(0) }

        Box(modifier = Modifier.wrapContentSize(Alignment.BottomCenter)){

            OutlinedTextField(
                modifier = Modifier
                    .width(200.dp)
                    .clickable {
                        Log.d("OutlinedTextFieldClickking", "CLICK")
                        expanded = true
                    },
                enabled = false,
                value = titleList[selectedIndex],
                onValueChange = { text = it },
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
                for (item in titleList){
                    TextMenuItem(
                        setExpanded={newValue -> expanded=newValue},
                        title = item,
                        selectText={selectedIndex =titleList.indexOf(item) }
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
    fun SlowModeCheck(
        setExpanded: (Boolean) -> Unit
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
                        Text("Slow Mode", color = MaterialTheme.colorScheme.onPrimary)
                    }
                    // Text("Off", color = MaterialTheme.colorScheme.onPrimary)
                    EmbeddedDropDownMenu(
                        titleList =listOf(
                            "Off","3s","5s",
                            "10s", "20s","30s","60s","120s"
                        )
                    )
                }
            }
        )
    }

    @Composable
    fun SubscriberOnlySwitch(
        setExpanded:(Boolean)->Unit,
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
                    SwitchMinimalExample()
                }
            }
        )
    }

    @Composable
    fun EmoteOnlySwitch(
        setExpanded:(Boolean)->Unit,
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
                        Icon(imageVector =Icons.Default.Face, contentDescription ="Emote icon" )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Emotes-only chat", color = MaterialTheme.colorScheme.onPrimary)
                    }
                    SwitchMinimalExample()
                }
            }
        )
    }


    @Composable
    fun SwitchMinimalExample() {
        var checked by remember { mutableStateOf(true) }

        Switch(
            checked = checked,
            onCheckedChange = {
                checked = it
            }
        )
    }

    @Composable
    fun DetectDoubleClickSpacer(
        opacity:Float,
        setDragging:(Boolean) ->Unit
    ){
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = opacity))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            //I think I detect the long press here and then have the drag up top
                            setDragging(true)
                        }
                    ) {

                    }
                }
        )

    }

    @Composable
    fun DetectDraggingOrNotAtBottomButton(
        dragging:Boolean,
        listState: LazyListState,
        scrollToBottomOfList:()->Unit,
        modifier: Modifier
    ){
        if(!dragging && !listState.isScrolledToEnd()){
            DualIconsButton(
                buttonAction = {
                    scrollToBottomOfList()
                },
                iconImageVector= Icons.Default.ArrowDropDown,
                iconDescription = stringResource(R.string.arrow_drop_down_description),
                buttonText = stringResource(R.string.scroll_to_bottom),
                modifier = modifier

            )
        }
    }
    // I need to create chat for subscribers, non-subscribers and moderators



}