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

object ChatSettingsContainer{

    enum class SwitchTypes {
        SLOW
    }


    @Composable
    fun ChatSettingsBox(
        chatSettingsData: ChatSettingsData,
        showChatSettingAlert: Boolean,
        closeChatSettingsAlert: () -> Unit,
        slowModeToggle: (ChatSettingsData) -> Unit,
        followerModeToggle: (ChatSettingsData) -> Unit,
        subscriberModeToggle: (ChatSettingsData) -> Unit,
        emoteModeToggle: (ChatSettingsData) -> Unit,
        oneClickActionsChecked:Boolean,
        changeOneClickActionsStatus:(Boolean) -> Unit,

        enableSlowModeSwitch: Boolean,
        enableFollowerModeSwitch: Boolean,
        enableSubscriberSwitch: Boolean,
        enableEmoteModeSwitch: Boolean,
        chatSettingsFailedMessage: String
    ) {
        val slowMode = chatSettingsData.slowMode
        val followerMode = chatSettingsData.followerMode
        val subscriberMode = chatSettingsData.subscriberMode
        val emoteMode = chatSettingsData.emoteMode
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

//            SlowSwitchRow(
//                switchLabel = stringResource(R.string.slow_mode),
//                enableSwitch = enableSlowModeSwitch,
//                switchCheck = slowMode,
//                chatSettingsData = chatSettingsData,
//                slowModeToggle = { chatSettingsData -> slowModeToggle(chatSettingsData) }
//            )
//
//            FollowerSwitchRow(
//                switchLabel = stringResource(R.string.follower_mode),
//                enableSwitch = enableFollowerModeSwitch,
//                switchCheck = followerMode,
//                chatSettingsData = chatSettingsData,
//                followerModeToggle = { chatSettingsData -> followerModeToggle(chatSettingsData) }
//            )
//
//            SubscriberSwitchRow(
//                switchLabel = stringResource(R.string.subscriber_mode),
//                enableSwitch = enableSubscriberSwitch,
//                switchCheck = subscriberMode,
//                chatSettingsData = chatSettingsData,
//                subscriberModeToggle = { chatSettingsData -> subscriberModeToggle(chatSettingsData) }
//            )
//
//            EmoteSwitchRow(
//                switchLabel = stringResource(R.string.emote_mode),
//                enableSwitch = enableEmoteModeSwitch,
//                switchCheck = emoteMode,
//                chatSettingsData = chatSettingsData,
//                emoteModeToggle = { chatSettingsData -> emoteModeToggle(chatSettingsData) }
//
//            )
//
//            AnimatedVisibility(visible = showChatSettingAlert) {
//                MessageAlertText(
//                    message = chatSettingsFailedMessage,
//                    closeChatSettingsAlert = { closeChatSettingsAlert() }
//                )
//            }
        } // end of the Column
    } // end of ChatSettingsBox
    @Composable
    fun SlowModeSwitch(
        switchLabel: String,
        enableSwitch: Boolean,
        checked: Boolean,
        switchFunction:() ->Unit,

        ){
        ChatSettingsBuilder.SwitchBuilder(
            switchLabel = switchLabel,
            enableSwitch =enableSwitch,
            checked =checked,
            switchFunction ={clicked,switchType -> switchFunction()},


        )
    }
}

    private object ChatSettingsBuilder{


        @Composable
        fun SwitchBuilder(
            switchLabel: String,
            enableSwitch: Boolean,
            checked: Boolean,
            switchFunction:(Boolean, ChatSettingsContainer.SwitchTypes) ->Unit,
            ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = switchLabel, fontSize = 25.sp,color = MaterialTheme.colorScheme.onPrimary)
                Switch(
                    checked = checked,
                    enabled = enableSwitch,
                    modifier = Modifier.size(40.dp),
                    onCheckedChange = {
                        switchFunction(it, ChatSettingsContainer.SwitchTypes.SLOW)
                    }
                )

            }
    }

    @Composable
    fun SlowSwitchRow(
        switchLabel: String,
        enableSwitch: Boolean,
        switchCheck: Boolean,
        chatSettingsData: ChatSettingsData,
        slowModeToggle: (ChatSettingsData) -> Unit
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = switchLabel, fontSize = 25.sp,color = MaterialTheme.colorScheme.onPrimary)
            Switch(
                checked = switchCheck,
                enabled = enableSwitch,
                modifier = Modifier.size(40.dp),
                onCheckedChange = {
                    slowModeToggle(
                        ChatSettingsData(
                            slowMode = it,
                            slowModeWaitTime = chatSettingsData.slowModeWaitTime,
                            followerMode = chatSettingsData.followerMode,
                            followerModeDuration = chatSettingsData.followerModeDuration,
                            subscriberMode = chatSettingsData.subscriberMode,
                            emoteMode = chatSettingsData.emoteMode,


                            )
                    )
                }
            )
        }
    }

    @Composable
    fun EmoteSwitchRow(
        switchLabel: String,
        enableSwitch: Boolean,
        switchCheck: Boolean,
        chatSettingsData: ChatSettingsData,
        emoteModeToggle: (ChatSettingsData) -> Unit
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = switchLabel, fontSize = 25.sp,color = MaterialTheme.colorScheme.onPrimary)
            Switch(
                checked = switchCheck,
                enabled = enableSwitch,
                modifier = Modifier.size(40.dp),
                onCheckedChange = {
                    emoteModeToggle(
                        ChatSettingsData(
                            slowMode = chatSettingsData.slowMode,
                            slowModeWaitTime = chatSettingsData.slowModeWaitTime,
                            followerMode = chatSettingsData.followerMode,
                            followerModeDuration = chatSettingsData.followerModeDuration,
                            subscriberMode = chatSettingsData.subscriberMode,
                            emoteMode = it,

                            )
                    )
                }
            )
        }
    }

    @Composable
    fun SubscriberSwitchRow(
        switchLabel: String,
        enableSwitch: Boolean,
        switchCheck: Boolean,
        chatSettingsData: ChatSettingsData,
        subscriberModeToggle: (ChatSettingsData) -> Unit
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = switchLabel, fontSize = 25.sp,color = MaterialTheme.colorScheme.onPrimary)
            Switch(
                checked = switchCheck,
                enabled = enableSwitch,
                modifier = Modifier.size(40.dp),
                onCheckedChange = {
                    subscriberModeToggle(
                        ChatSettingsData(
                            slowMode = chatSettingsData.slowMode,
                            slowModeWaitTime = chatSettingsData.slowModeWaitTime,
                            followerMode = chatSettingsData.followerMode,
                            followerModeDuration = chatSettingsData.followerModeDuration,
                            subscriberMode = it,
                            emoteMode = chatSettingsData.emoteMode,


                            )
                    )
                }
            )
        }
    }

    @Composable
    fun FollowerSwitchRow(
        switchLabel: String,
        enableSwitch: Boolean,
        switchCheck: Boolean,
        chatSettingsData: ChatSettingsData,
        followerModeToggle: (ChatSettingsData) -> Unit
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = switchLabel, fontSize = 25.sp,color = MaterialTheme.colorScheme.onPrimary)
            Switch(
                checked = switchCheck,
                enabled = enableSwitch,
                modifier = Modifier.size(40.dp),
                onCheckedChange = {
                    followerModeToggle(
                        ChatSettingsData(
                            slowMode = chatSettingsData.slowMode,
                            slowModeWaitTime = chatSettingsData.slowModeWaitTime,
                            followerMode = it,
                            followerModeDuration = chatSettingsData.followerModeDuration,
                            subscriberMode = chatSettingsData.subscriberMode,
                            emoteMode = chatSettingsData.emoteMode,

                            )
                    )
                }
            )
        }
    }

    // TODO: MAKE IT SO THE X CLICK REMOVES THE REQUEST MESSAGE
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
}