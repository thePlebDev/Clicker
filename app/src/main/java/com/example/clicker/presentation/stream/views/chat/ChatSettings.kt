package com.example.clicker.presentation.stream.views.chat

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.clicker.R
import com.example.clicker.presentation.modView.ListTitleValue
import com.example.clicker.presentation.stream.AdvancedChatSettings


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


@Composable
fun ChatSettingsColumn(
     advancedChatSettings: AdvancedChatSettings,
     changeAdvancedChatSettings: (AdvancedChatSettings)->Unit,
     changeNoChatMode:(Boolean)->Unit,


     followerModeList: List<ListTitleValue>,
     selectedFollowersModeItem: ListTitleValue,
     changeSelectedFollowersModeItem: (ListTitleValue) -> Unit,

     slowModeList: List<ListTitleValue>,
     selectedSlowModeItem: ListTitleValue,
     changeSelectedSlowModeItem: (ListTitleValue) -> Unit,
     chatSettingsEnabled:Boolean,
     emoteOnly:Boolean,
     setEmoteOnly:(Boolean) ->Unit,
     subscriberOnly:Boolean,
     setSubscriberOnly:(Boolean) ->Unit,

){
    Log.d("ChatSettingsColumn","Recomping")


    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .background(MaterialTheme.colorScheme.primary)) {
        Text("Moderator chat settings",color = Color.White,
            modifier= Modifier.padding(start=15.dp),
            fontSize = MaterialTheme.typography.headlineLarge.fontSize
        )
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.secondary.copy(0.8f), modifier = Modifier.padding(horizontal = 15.dp))
        Spacer(modifier =Modifier.size(10.dp))

        EmoteOnlySwitch(
            setExpanded ={newValue -> },
            emoteOnly =emoteOnly,
            setEmoteOnly={newValue ->setEmoteOnly(newValue)},
            switchEnabled=chatSettingsEnabled
        )
        SubscriberOnlySwitch(
            setExpanded ={newValue -> },
            subscriberOnly = subscriberOnly,
            setSubscriberOnly = {newValue -> setSubscriberOnly(newValue) },
            switchEnabled=chatSettingsEnabled
        )

        //todo: these are the modal items the pop up
        SlowModeCheck(
            setExpanded ={newValue -> },
            chatSettingsEnabled=chatSettingsEnabled,
            slowModeList=slowModeList,
            selectedSlowModeItem=selectedSlowModeItem,
            changeSelectedSlowModeItem ={newValue -> changeSelectedSlowModeItem(newValue)},
        )

        FollowersOnlyCheck(
            chatSettingsEnabled=chatSettingsEnabled,
            setExpanded ={newValue -> },
            followersOnlyList=followerModeList,
            selectedFollowersModeItem=selectedFollowersModeItem,
            changeSelectedFollowersModeItem ={newValue -> changeSelectedFollowersModeItem(newValue)}
        )
        //todo: these are the modal items the pop up





        AdvancedChatSettings(
            advancedChatSettings = advancedChatSettings,
            changeAdvancedChatSettings = {newValue ->changeAdvancedChatSettings(newValue)},
            changeNoChatMode ={newValue ->changeNoChatMode(newValue)}
        )


    }

}

@Composable
fun AdvancedChatSettings(
    advancedChatSettings: AdvancedChatSettings,
    changeAdvancedChatSettings: (AdvancedChatSettings)->Unit,
    changeNoChatMode:(Boolean)->Unit
){
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Advanced chat settings",color = Color.White,
            modifier= Modifier.padding(start=15.dp),
            fontSize = MaterialTheme.typography.headlineLarge.fontSize
        )
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.secondary.copy(0.8f), modifier = Modifier.padding(horizontal = 15.dp))
        Spacer(modifier =Modifier.size(10.dp))

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
                Switch(
                    enabled =switchEnabled,
                    checked = emoteOnly,
                    onCheckedChange = {
                        setEmoteOnly(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.secondary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                        checkedTrackColor = Color.DarkGray,
                        uncheckedTrackColor = Color.DarkGray,
                    )
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
                Switch(
                    enabled=switchEnabled,
                    checked = subscriberOnly,
                    onCheckedChange = {
                        setSubscriberOnly(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.secondary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                        checkedTrackColor = Color.DarkGray,
                        uncheckedTrackColor = Color.DarkGray,
                    )
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
                    chatSettingsEnabled=chatSettingsEnabled
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
                    chatSettingsEnabled =chatSettingsEnabled
                )
            }
        }
    )
}

@Composable
fun EmbeddedDropDownMenu(
    titleList: List<ListTitleValue>, //this is the list shown to the user
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
            for (item in titleList){
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
        Switch(
            checked = advancedChatSettings.showSubs,
            enabled = true,
            onCheckedChange = {
                val newValue =advancedChatSettings.copy(showSubs = it)
                changeAdvancedChatSettings(newValue)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.secondary,
                uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                checkedTrackColor = Color.DarkGray,
                uncheckedTrackColor = Color.DarkGray,
            )
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
        Switch(
            checked = advancedChatSettings.showGiftSubs,
            enabled = true,
            onCheckedChange = {
                val newValue =advancedChatSettings.copy(showGiftSubs = it)
                changeAdvancedChatSettings(newValue)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.secondary,
                uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                checkedTrackColor = Color.DarkGray,
                uncheckedTrackColor = Color.DarkGray,
            )
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
        Switch(
            checked = advancedChatSettings.showAnonSubs,
            enabled = true,
            onCheckedChange = {
                val newValue =advancedChatSettings.copy(showAnonSubs = it)
                changeAdvancedChatSettings(newValue)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.secondary,
                uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                checkedTrackColor = Color.DarkGray,
                uncheckedTrackColor = Color.DarkGray,
            )
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
        Switch(
            checked = advancedChatSettings.showReSubs,
            enabled = true,
            onCheckedChange = {
                val newValue =advancedChatSettings.copy(showReSubs = it)
                changeAdvancedChatSettings(newValue)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.secondary,
                uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                checkedTrackColor = Color.DarkGray,
                uncheckedTrackColor = Color.DarkGray,
            )
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
        Switch(
            checked = advancedChatSettings.noChatMode,
            enabled = true,
            onCheckedChange = {
                changeNoChatMode(it)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.secondary,
                uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                checkedTrackColor = Color.DarkGray,
                uncheckedTrackColor = Color.DarkGray,
            )
        )
    }
}