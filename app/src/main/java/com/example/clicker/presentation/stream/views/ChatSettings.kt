package com.example.clicker.presentation.stream.views

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
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.R
import com.example.clicker.network.models.ChatSettingsData
import com.example.clicker.presentation.stream.AdvancedChatSettings


/**
 * ChatSettingsContainer contains all of the composables that are used to create the chat settings experience
 *
 *  - ChatSettingsContainer contains 1 top level implementation:
 *  1) [EnhancedChatSettingsBox]
 *
 *   - ChatSettingsContainer contains 2 top level private implementations:
 *   1) [SettingsSwitches]
 *   2) [AdvancedChatSettings]
 * */
object ChatSettingsContainer {

    /**
     * EnhancedChatSettingsBox is the implementation that represents the entire feature of the tabbed chat settings.
     *
     * - EnhancedChatSettingsBox implements the [TabbedChatSettingsSwitchBox][Builders.TabbedChatSettingsSwitchBox] builder
     *
     *
     * @param enableSwitches a Boolean determining if the switches of the [TabbedChatSettingsSwitchBox][Builders.TabbedChatSettingsSwitchBox] should be enabled.
     *
     * @param showChatSettingAlert a Boolean determining if a message should be shown to the user
     * @param chatSettingsData a ChatSettingsData[com.example.clicker.network.models.ChatSettingsData] object that represents all the
     * data meant to represent the mod data
     * @param updateChatSettings a function that is used to update new versions of the [chatSettingsData]
     * @param closeAlertHeader a function that is used to close the header
     * @param showUndoButton a boolean used to determine if the user is to be shown the undo button
     * @param showUndoButtonStatus a boolean used to determine if the user is to be shown the response from the undo button
     * @param noChatMode a boolean used to determine if the user is in ***no chat mode***
     * @param setNoChatMode a function used to change the status of [setNoChatMode]
     * @param advancedChatSettings a [AdvancedChatSettings][com.example.clicker.presentation.stream.AdvancedChatSettings] object
     * used to represent all the non-moderator related chat setting state
     * @param updateAdvancedChatSettings a function used to update the [advancedChatSettings] object
     * @param userIsModerator a boolean used to determine if the user is a moderator or not
     *
     * */
    @Composable
    fun EnhancedChatSettingsBox(
        enableSwitches: Boolean,
        showChatSettingAlert: Boolean,
        chatSettingsData: ChatSettingsData,
        updateChatSettings: (ChatSettingsData) -> Unit,
        closeAlertHeader: () -> Unit,
        showUndoButton: (Boolean) -> Unit,
        showUndoButtonStatus: Boolean,
        noChatMode: Boolean,
        setNoChatMode: (Boolean) -> Unit,
        advancedChatSettings: AdvancedChatSettings,
        updateAdvancedChatSettings: (AdvancedChatSettings) -> Unit,
        userIsModerator:Boolean
    ){
        Builders.TabbedChatSettingsSwitchBox(
            modSettings = {
                ChatSettingsContainer.SettingsSwitches(
                    enableSwitches =enableSwitches,
                    showChatSettingAlert = showChatSettingAlert,
                    chatSettingsData =chatSettingsData,
                    updateChatSettings = {newData -> updateChatSettings(newData)},
                    closeAlertHeader = {closeAlertHeader()},
                    showUndoButton = {showStatus ->showUndoButton(showStatus)},
                    showUndoButtonStatus = showUndoButtonStatus,
                    userIsModerator=userIsModerator
                )
            },
            advancedChatSettings = {
                AdvancedChatSection(
                    noChatMode = noChatMode,
                    setNoChatMode = {state ->setNoChatMode(state)},
                    advancedChatSettings = advancedChatSettings,
                    updateAdvancedChatSettings ={advancedChatSettings ->
                        updateAdvancedChatSettings(advancedChatSettings)
                    }

                )
            }
        )
    }

    @Deprecated(
        "Removed from external implementation to interanl ",
        replaceWith = ReplaceWith(
            expression = "EnhancedChatSettingsBox",
        ),
        level = DeprecationLevel.WARNING
    )
    /**
     * SettingsSwitches is the implementation that represents the entire feature of chat settings.
     *
     * - SettingsSwitches implements the [ChatSettingsSwitchBox][Builders.ChatSettingsSwitchBox] builder
     *
     * @param enableSwitches a boolean used to determine if all the switches inside the [ChatSettingsSwitchBox][ChatSettingsBuilder.ChatSettingsSwitchBox]
     * should be enabled or not. This is needed because I don't want the user sending another request while they are still waiting for
     * a request to send a response back
     * @param showChatSettingAlert a boolean to determine if a [ChatSettingsHeader][ChatSettingsParts.ChatSettingsHeader] should
     * be shown or not. This is triggered when a failure is registered in the ViewModel
     * @param chatSettingsData a [ChatSettingsData][com.example.clicker.network.models] object used to represent all the data the
     * Switches are manipulating
     * @param updateChatSettings a function used to send a request to update the chat's settings to the Twitch server.
     * @param closeAlertHeader a function used to close the [ChatSettingsHeader][ChatSettingsParts.ChatSettingsHeader] triggered by
     * [showChatSettingAlert]
     *  @param userIsModerator a boolean used to determine if the user is a moderator or not
     * */
    @Composable
    private fun SettingsSwitches(
        enableSwitches:Boolean,
        showChatSettingAlert: Boolean,
        chatSettingsData:ChatSettingsData,
        updateChatSettings:(ChatSettingsData)->Unit,
        closeAlertHeader:()->Unit,
        showUndoButton: (Boolean) -> Unit,
        showUndoButtonStatus:Boolean,
        userIsModerator:Boolean,
    ) {
        Builders.ChatSettingsSwitchBox(
            showUndoButtonSwitch ={
                Parts.SwitchPart(
                                enableSwitches =userIsModerator,
                                checked = showUndoButtonStatus,
                                switchLabel = "Show undo button",
                                switchFunction ={showUndoButton(it)}
                            )
            },
            slowModeSwitch = {
                Parts.SwitchPart(
                    enableSwitches = enableSwitches && userIsModerator,
                    checked =chatSettingsData.slowMode,
                    switchLabel = "Slow mode",
                    switchFunction = {
                        val newChatSettingsData = chatSettingsData.copy(slowMode = it)
                        updateChatSettings(newChatSettingsData)
                    }

                )
            },
            followerModeSwitch ={
                Parts.SwitchPart(
                    enableSwitches = enableSwitches && userIsModerator,
                    checked =chatSettingsData.followerMode,
                    switchLabel = "Follower mode",
                    switchFunction = {
                        val newChatSettingsData = chatSettingsData.copy(followerMode = it)
                        updateChatSettings(newChatSettingsData)
                    }

                )
            },
            subscriberModeSwitch={
                Parts.SwitchPart(
                    enableSwitches = enableSwitches && userIsModerator,
                    checked =chatSettingsData.subscriberMode,
                    switchLabel = "Subscriber mode",
                    switchFunction = {
                        val newChatSettingsData = chatSettingsData.copy(subscriberMode = it)
                        updateChatSettings(newChatSettingsData)
                    }

                )
            },
            emoteModeSwitch={
                Parts.SwitchPart(
                    enableSwitches = enableSwitches && userIsModerator,
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
                    Parts.AlertRowMessage(
                        alertMessage = "request failed",
                        closeAlert = {
                            closeAlertHeader()
                        }
                    )
                }
            },
            chatSettingsHeader ={
                Parts.ChatSettingsHeader(
                    userIsModerator =userIsModerator
                )
            }

        )
    }

    /**
     * AdvancedChatSettings is the private implementation that represents chat setting section dealing with non moderator
     * related tasks.
     *
     * - AdvancedChatSettings implements the [AdvancedChatBuilder][Builders.AdvancedChat] builder
     *
     * @param noChatMode a boolean used to determine which state the internal [Switch] is in
     * @param setNoChatMode a function used by the internal [Switch] when [noChatMode] is set to true and
     * is used to sever the connection with the websocket that is connecting the Twitch IRC server
     * */
    @Composable
    private fun AdvancedChatSection(
        noChatMode:Boolean,
        setNoChatMode:(Boolean)->Unit,

        advancedChatSettings: AdvancedChatSettings,
        updateAdvancedChatSettings:(AdvancedChatSettings)-> Unit

    ){
        Builders.AdvancedChat(
            chatSettingsHeader ={},
            noChatModeSwitch={
                Parts.SwitchPart(
                    enableSwitches = true,
                    checked = noChatMode,
                    switchLabel = "No chat mode",
                    switchFunction ={setNoChatMode(it)}
                )
            },
            subscriptionSwitch={
                Parts.SwitchPart(
                    enableSwitches = true,
                    checked = advancedChatSettings.showSubs,
                    switchLabel = "Sub notifications",
                    switchFunction ={
                        val updateSubs = advancedChatSettings.copy(showSubs = it)
                        updateAdvancedChatSettings(updateSubs)
                    }
                )
            },
            anonymousSubscriptionSwitch ={
                Parts.SwitchPart(
                    enableSwitches = true,
                    checked = advancedChatSettings.showAnonSubs,
                    switchLabel = "Anon sub notifications",
                    switchFunction ={
                        val updateAnonSubs = advancedChatSettings.copy(showAnonSubs = it)
                        updateAdvancedChatSettings(updateAnonSubs)
                    }
                )
            },
            reSubSwitch ={
                Parts.SwitchPart(
                    enableSwitches = true,
                    checked = advancedChatSettings.showReSubs,
                    switchLabel = "Re-sub notifications",
                    switchFunction ={
                        val updateReSubs =  advancedChatSettings.copy(showReSubs = it)
                        updateAdvancedChatSettings(updateReSubs)
                    }
                )
            },
            giftSubSwitch={
                Parts.SwitchPart(
                    enableSwitches = true,
                    checked = advancedChatSettings.showGiftSubs,
                    switchLabel = "Gift sub notifications",
                    switchFunction ={
                        val updateGiftSubs =  advancedChatSettings.copy(showGiftSubs = it)
                        updateAdvancedChatSettings(updateGiftSubs)
                    }
                )
            }

        )
    }



    // end of ChatSettingsBox


    /**
     * Builders represents the most generic parts of [ChatSettingsContainer] and should be thought of as UI layout guides used
     * by the implementations above
     * */
    private object Builders {

        /**
         *
         * The basic layout of how the the chat settings section will look. A UI demonstration can be seen
         * [HERE](https://theplebdev.github.io/Modderz-style-guide/#ChatSettingsSwitchBox)
         *
         * - ChatSettingsSwitchBox is used inside of [SettingsSwitches]
         *
         * @param slowModeSwitch a [Switch][androidx.compose.material] used to toggle the slow mode setting for the chat's settings
         * @param followerModeSwitch a [Switch][androidx.compose.material] used to toggle the follower mode setting for the chat's settings
         * @param subscriberModeSwitch a [Switch][androidx.compose.material] used to toggle the Subscriber mode setting for the chat's settings
         * @param emoteModeSwitch a [Switch][androidx.compose.material] used to toggle the emote only mode setting for the chat's settings
         * @param alertHeader a header that is used to convey a short, urgent message
         * @param chatSettingsHeader a header that is used to represent the title of the chat settings section
         * */
        @Composable
        fun ChatSettingsSwitchBox(
            showUndoButtonSwitch: @Composable () -> Unit,
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

                    showUndoButtonSwitch()
                    slowModeSwitch()
                    followerModeSwitch()
                    subscriberModeSwitch()
                    emoteModeSwitch()
                    alertHeader()
                }

            }

        }

        /**
         *
         * The basic layout of how the the chat settings section dealing with non-moderator tasks will look.
         *
         * - AdvancedChat is used inside of [AdvancedChatSettings]
         *
         * @param chatSettingsHeader a header that is used to represent the title of the chat settings section
         * @param noChatModeSwitch a [Switch] that is used to toggle the chat into ***no chat mode***
         *
         * @param subscriptionSwitch a [Switch] that is used to toggle the UI subscription messages
         * @param anonymousSubscriptionSwitch a [Switch] that is used to toggle the UI anonymous subscription messages
         * @param reSubSwitch a [Switch] that is used to toggle the UI re-subscription UI messages
         * @param giftSubSwitch a [Switch] that is used to toggle the gifted sub messages
         * */
        @Composable
        fun AdvancedChat(
            chatSettingsHeader:@Composable () -> Unit,
            noChatModeSwitch: @Composable () -> Unit,

            subscriptionSwitch: @Composable () -> Unit,
            anonymousSubscriptionSwitch:@Composable () -> Unit,
            reSubSwitch:@Composable () -> Unit,
            giftSubSwitch:@Composable () -> Unit,

        ){
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

                    noChatModeSwitch()
                    subscriptionSwitch()
                    anonymousSubscriptionSwitch()
                    reSubSwitch()
                    giftSubSwitch()
                }

            }
        }


        /**
         *
         * The basic layout of how the enhanced chat settings UI will look
         *
         * - AdvancedChat is used inside of [EnhancedChatSettingsBox]
         *
         * @param modSettings a section of code that will represent what the moderator settings will look like
         * @param advancedChatSettings a section of code that will represent what the advanced  non- moderator settings will look like
         * */
        @Composable
        fun TabbedChatSettingsSwitchBox(
            modSettings:@Composable () -> Unit,
            advancedChatSettings:@Composable () -> Unit,
        ){
            var tabIndex by remember { mutableIntStateOf(0) }


            val tabs = listOf("Mod Settings", "Advanced chat settings")

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                TabRow(
                    selectedTabIndex = tabIndex,

                    ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            modifier = Modifier.background(MaterialTheme.colorScheme.secondary),
                            text = { Text(title) },
                            selected = tabIndex == index,
                            onClick = { tabIndex = index }
                        )
                    }
                }
            }

            when (tabIndex) {
                0 -> modSettings()
                1 -> advancedChatSettings()
            }
        }
    }// end of the builder

    /**
     * Parts represents the most individual parts of [ChatSettingsContainer] and should be thought of as the individual
     * pieces that are used inside of a [Builders] to create a [ChatSettingsContainer] implementation
     * */
    private object Parts {

        /**
         * - Contains 0 extra parts
         *
         * - A [Row] containing a Text Composable and a background color of secondary. This composable is used to convey the
         * short title to the user and let the user know what section of the app they are in
         *
         *  @param userIsModerator a boolean used to determine if the user is a moderator or not
         *
         * */
        @Composable
        fun ChatSettingsHeader(
            userIsModerator:Boolean
        ){
            val secondary =androidx.compose.material3.MaterialTheme.colorScheme.primary
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
                .background(secondary)
                .padding(vertical = 10.dp)
                .fillMaxWidth()) {
                if(!userIsModerator){
                    Text("You are not a moderator in chat ", fontSize = 20.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary)
                }

            }
        }

        /**
         * - Contains 0 extra parts
         *
         * - A [Row] containing a Text Composable and a Switch. This is the main part that is used heavily in the [SettingsSwitches]
         * implementation
         *
         * @param enableSwitches a boolean used to determine if the switch should be enabled or not
         * @param checked a boolean used to determine if the switch is in the on or off position
         * @param switchLabel the `label` of the switch. The user will use this to determine what the switch will do
         * @param switchFunction a function that will get called whenever the switch is clicked
         * */
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

        /**
         * - Contains 0 extra parts
         *
         * - A [Row] meant to display text surrounded by two icons. This Composable is meant to represent the type of
         * error that has occurred
         *
         * @param alertMessage a String representing the error. Should only be one word or two
         * @param closeAlert a function used to close and hide this message from the user
         * */
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

