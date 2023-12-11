package com.example.clicker.presentation.stream.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
        updateChatSettings:(ChatSettingsData)->Unit
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
            }
        )
    }


    // end of ChatSettingsBox


    private object ChatSettingsBuilder {
        @Composable
        fun ChatSettingsSwitchBox(
            slowModeSwitch: @Composable () -> Unit,
//            followerModeSwitch:@Composable () -> Unit,
//            subscriberModeSwitch:@Composable () -> Unit,
//            emoteModeSwitch:@Composable () -> Unit,

        ) {
            //This will be a builder
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                slowModeSwitch()
//                followerModeSwitch()
//                subscriberModeSwitch()
//                emoteModeSwitch()


            }
        }
    }// end of the builder
    private object ChatSettingsParts {
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
                    color = androidx.compose.material3.MaterialTheme.colorScheme.secondary
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
    }
}


//todo: THIS IS OUTSIDE OF THE MAIN OBJECT

@Composable
fun MessageAlertText(
    message: String,
    closeChatSettingsAlert: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .clickable { },
        border = BorderStroke(2.dp, Color.Red),
        elevation = 10.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.close_icon_description),
                modifier = Modifier
                    .clickable { closeChatSettingsAlert() }
                    .padding(2.dp)
                    .size(25.dp),
                tint = Color.Red
            )
            Text(
                stringResource(R.string.failed_request_notification),
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.close_icon_description),
                modifier = Modifier
                    .clickable { closeChatSettingsAlert() }
                    .padding(2.dp)
                    .size(25.dp),
                tint = Color.Red
            )
        }
    }
}