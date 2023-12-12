package com.example.clicker.presentation.stream.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.R
import com.example.clicker.network.models.ChatSettingsData


object ChatSettingsContainer {

    @Composable
    fun SettingsSwitches(
        enableSwitches:Boolean,
        showChatSettingAlert: Boolean,
        chatSettingsData:ChatSettingsData,
        updateChatSettings:(ChatSettingsData)->Unit,
        closeAlertHeader:()->Unit
    ) {
        ChatSettingsBuilder.ChatSettingsSwitchBox(
            slowModeSwitch = {
                ChatSettingsParts.SwitchPart(
                    enableSwitches = enableSwitches,
                    checked =chatSettingsData.slowMode,
                    switchLabel = "Slow mode",
                    switchFunction = {
                        val newChatSettingsData = chatSettingsData.copy(slowMode = it)
                        updateChatSettings(newChatSettingsData)
                    }

                )
            },
            followerModeSwitch ={
                ChatSettingsParts.SwitchPart(
                    enableSwitches = enableSwitches,
                    checked =chatSettingsData.followerMode,
                    switchLabel = "Follower mode",
                    switchFunction = {
                        val newChatSettingsData = chatSettingsData.copy(followerMode = it)
                        updateChatSettings(newChatSettingsData)
                    }

                )
            },
            subscriberModeSwitch={
                ChatSettingsParts.SwitchPart(
                    enableSwitches = enableSwitches,
                    checked =chatSettingsData.subscriberMode,
                    switchLabel = "Subscriber mode",
                    switchFunction = {
                        val newChatSettingsData = chatSettingsData.copy(subscriberMode = it)
                        updateChatSettings(newChatSettingsData)
                    }

                )
            },
            emoteModeSwitch={
                ChatSettingsParts.SwitchPart(
                    enableSwitches = enableSwitches,
                    checked =chatSettingsData.emoteMode,
                    switchLabel = "Emote mode",
                    switchFunction = {
                        val newChatSettingsData = chatSettingsData.copy(emoteMode = it)
                        updateChatSettings(newChatSettingsData)
                    }

                )
            },
            alertHeader = {
                if(showChatSettingAlert){
                    ChatSettingsParts.AlertRowMessage(
                        alertMessage = "request failed",
                        closeAlert = {
                            closeAlertHeader()
                        }
                    )
                }
            },
            chatSettingsHeader ={
                ChatSettingsParts.ChatSettingsHeader()
            }

        )
    }


    // end of ChatSettingsBox


    private object ChatSettingsBuilder {
        @Composable
        fun ChatSettingsSwitchBox(
            slowModeSwitch: @Composable () -> Unit,
            followerModeSwitch:@Composable () -> Unit,
            subscriberModeSwitch:@Composable () -> Unit,
            emoteModeSwitch:@Composable () -> Unit,
            alertHeader:@Composable () -> Unit,
            chatSettingsHeader:@Composable () -> Unit,

        ) {
            //This will be a builder
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.primary)
            ) {
                chatSettingsHeader()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    slowModeSwitch()
                    followerModeSwitch()
                    subscriberModeSwitch()
                    emoteModeSwitch()
                    alertHeader()
                }

            }

        }
    }// end of the builder
    private object ChatSettingsParts {

        @Composable
        fun ChatSettingsHeader(){
            val secondary =androidx.compose.material3.MaterialTheme.colorScheme.secondary
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
                .background(secondary)
                .padding(vertical = 10.dp)
                .fillMaxWidth()) {
                Text("Chat room settings", fontSize = 25.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary)
            }
        }
        @Composable
        fun SwitchPart(
            enableSwitches: Boolean,
            checked: Boolean,
            switchLabel: String,
            switchFunction: (Boolean) -> Unit
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = switchLabel,
                    fontSize = 25.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                )
                Switch(
                    checked = checked,
                    enabled = enableSwitches,
                    modifier = Modifier.size(40.dp),
                    onCheckedChange = {
                        switchFunction(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = androidx.compose.material3.MaterialTheme.colorScheme.secondary,
                        uncheckedThumbColor = androidx.compose.material3.MaterialTheme.colorScheme.secondary,
                        checkedTrackColor = Color.DarkGray,
                        uncheckedTrackColor = Color.DarkGray,
                    )
                )
            }
        }
        @Composable
        fun AlertRowMessage(
            alertMessage:String,
            closeAlert: () -> Unit,
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .clip(shape = RoundedCornerShape(10.dp))
                    .background(Color.Red.copy(alpha = 0.6f))
                    .clickable {
                        closeAlert()
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.close_icon_description),
                    modifier = Modifier
                        .size(30.dp),
                    tint = Color.White
                )
                Text(
                    text = alertMessage,
                    color = Color.White,
                    fontSize = 20.sp
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.close_icon_description),
                    modifier = Modifier
                        .size(30.dp),
                    tint = Color.White
                )
            }
        }
    }
}

