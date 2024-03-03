package com.example.clicker.presentation.stream.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.R
import com.example.clicker.network.clients.BanUser
import com.example.clicker.presentation.stream.views.dialogs.Dialogs

import kotlinx.coroutines.launch

/**
 * BottomModal contains all the composables responsible for creating the entire experience when a user clicks on a
 * individual chat message.
 *
 * */
object BottomModal{

    /**
     * BanTimeOutDialogs contains everything that is shown inside of a [ModalBottomSheetLayout] when a user clicks the
     * individual chat. Also, it contains [Dialogs.TimeoutDialog] and [Dialogs.BanDialog] to handle if the user wants to
     * ban or timeout a user
     *
     * @param clickedUsernameChats a list of all the clicked username's chat messages. It will update in real time
     * @param clickedUsername the username of the clicked chat message
     * @param bottomModalState the state for the [ModalBottomSheetLayout] and determines if the bottom modal should pop up or no
     * @param textFieldValue A string value meant to represent what the user is typing. This is used when the user hits the reply
     * functionality on the [BottomModalParts.ContentBanner]
     * @param closeBottomModal A function meant to close the [ModalBottomSheetLayout]
     * @param banned A boolean to determine if the user is banned or not
     * @param isMod A boolean to determine if the user is a mod or not
     * @param unbanUser A function that will be used to unban a user
     * @param openTimeoutDialog A function that when trigger will shown the [Dialogs.TimeoutDialog]
     * @param closeTimeoutDialog A function that when trigger will close the [Dialogs.TimeoutDialog]
     * @param timeOutDialogOpen A Boolean to determine if the [Dialogs.TimeoutDialog] should be shown or not
     *
     *
     * @param timeoutDuration An integer meant to represent the amount of time(in seconds) a user is timed out
     * @param timeoutReason A String that is given to represent why the user is timed out
     * @param changeTimeoutDuration a function meant to change the [timeoutDuration]
     * @param changeTimeoutReason A function meant to change the [timeoutReason]
     * @param closeDialog A function meant to close the Dialogs.TimeoutDialog]
     * @param timeOutUser A function that will actually send the request to timeout the user
     *
     * @param banDialogOpen A boolean to determine if the [Dialogs.BanDialog] should be shown
     * @param openBanDialog A function that is used to show the [Dialogs.BanDialog]
     * @param closeBanDialog A function that is used to close the [Dialogs.BanDialog]
     * @param banReason A string meant to represent the reason a user is getting banned
     * @param changeBanReason  A function meant the change the [banReason]
     * @param banUser A function that will actually ban the user
     * @param clickedUserId A string representing the UserId of the user that we have clicked
     * */
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun BanTimeOutDialogs(
        clickedUsernameChats: List<String>,
        clickedUsername: String,
        bottomModalState: ModalBottomSheetState,
        textFieldValue: MutableState<TextFieldValue>,

        closeBottomModal: () -> Unit,
        banned: Boolean,
        isMod: Boolean,
        unbanUser: () -> Unit,
        openTimeoutDialog: () -> Unit,
        closeTimeoutDialog: () -> Unit,
        timeOutDialogOpen:Boolean,

        timeoutDuration: Int,
        timeoutReason: String,
        changeTimeoutDuration: (Int) -> Unit,
        changeTimeoutReason: (String) -> Unit,
        closeDialog: () -> Unit,
        timeOutUser: () -> Unit,

        banDialogOpen:Boolean,
        openBanDialog: () -> Unit,
        closeBanDialog: () -> Unit,
        banReason: String,
        changeBanReason: (String) -> Unit,
        banUser: (BanUser) -> Unit,
        clickedUserId: String,
        updateShouldMonitorUser: () -> Unit,
        shouldMonitorUser:Boolean
    ){
        BottomModalBuilders.BottomModalContent(

            // TODO: this should 100% not be filteredChat. Need to create new variable
            clickedUsernameChats = clickedUsernameChats,


            timeoutDialogContent ={
                if(timeOutDialogOpen){
                    Dialogs.TimeoutDialog(
                        onDismissRequest = {
                            closeTimeoutDialog()
                        },
                        username = clickedUsername,
                        timeoutDuration = timeoutDuration,
                        timeoutReason = timeoutReason,
                        changeTimeoutDuration = { duration ->
                            changeTimeoutDuration(duration)
                        },
                        changeTimeoutReason = { reason ->
                            changeTimeoutReason(reason)
                        },
                        closeDialog = {
                            closeDialog()

                        },
                        timeOutUser = {
                            timeOutUser()
                        }
                    )
                }
            },
            banDialogContent ={
                if(banDialogOpen){
                    Dialogs.BanDialog(
                        onDismissRequest = {
                            closeBanDialog()
                        },
                        username = clickedUsername,
                        banReason = banReason,
                        changeBanReason = { reason -> changeBanReason(reason) },
                        banUser = { banUser -> banUser(banUser) },
                        clickedUserId = clickedUserId,
                        closeDialog = {
                            closeBanDialog()
                        },
                    )
                }
            },
            clickedUsernameBanner ={
                BottomModalParts.ContentBanner(
                    clickedUsername = clickedUsername,
                    bottomModalState = bottomModalState,
                    textFieldValue = textFieldValue

                )
            },
            clickedUserBottomBanner ={
                BottomModalParts.ContentBottom(
                    banned =banned,
                    isMod =isMod,
                    closeBottomModal ={closeBottomModal()},
                    unbanUser ={unbanUser()},
                    openTimeoutDialog={openTimeoutDialog()},
                    openBanDialog ={openBanDialog()},
                    updateShouldMonitorUser = {updateShouldMonitorUser()},
                    shouldMonitorUser = shouldMonitorUser
                )
            }

        )
    }

    /**
     * BottomModalBuilders represents the most generic parts of all [BottomModal]. The builder is meant to be a design layout
     * meaning that all implementations that use this builder will share the same basic design layout
     * */
    private object BottomModalBuilders{


        /**
         *
         * BottomModalContent is the basic layout for the bottom modal and dialog experience. A example of what the typical UI looks like
         * with this builder can be found [HERE](https://theplebdev.github.io/Modderz-style-guide/#BottomModalContent)
         * */
        @OptIn(ExperimentalMaterialApi::class)
        @Composable
        fun BottomModalContent(
            clickedUsernameChats: List<String>,

            timeoutDialogContent:@Composable () -> Unit,

            banDialogContent:@Composable () -> Unit,
            clickedUsernameBanner: @Composable () -> Unit,
            clickedUserBottomBanner: @Composable () -> Unit,

            ) {


            timeoutDialogContent()
            banDialogContent()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                clickedUsernameBanner()
                clickedUserBottomBanner()
                Text(stringResource(R.string.recent_messages),color = MaterialTheme.colorScheme.onPrimary,modifier = Modifier.padding(bottom=5.dp))

                BottomModalParts.ClickedUserMessages(clickedUsernameChats)

            } // END OF THE COLUMN


        }
    }


    private object BottomModalParts{
        /**
         * Composable function meant to display a scrollable list of a users recent chats in a [LazyColumn]
         *
         * @param clickedUsernameChats String list of a users most recent chat messages
         * */
        @Composable
        fun ClickedUserMessages(
            clickedUsernameChats: List<String>
        ){
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .height(100.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                items(clickedUsernameChats) {message->

                    Text(

                        buildAnnotatedString {
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary, fontSize = MaterialTheme.typography.headlineSmall.fontSize)) {
                                append("Message: ")
                            }


                            withStyle(style = SpanStyle(fontSize = MaterialTheme.typography.headlineSmall.fontSize, color = MaterialTheme.colorScheme.onPrimary)) {
                                append(message)
                            }

                        },
                        modifier = Modifier.fillMaxWidth().padding(5.dp)
                    )


                }
            }
        }

        /**
         * Will display a [Row] containing a username and a reply button
         *
         *
         * @param clickedUsername String meant to represent the username of a individual user
         * @param bottomModalState the state of a [ModalBottomSheetLayout] and used to close the modal when the reply button is pressed
         * @param textFieldValue the state of what the user is typing. When the user clicks the reply button the [clickedUsername] will be added to it
         * */
        @OptIn(ExperimentalMaterialApi::class)
        @Composable
        fun ContentBanner(
            clickedUsername: String,
            bottomModalState: ModalBottomSheetState,
            textFieldValue: MutableState<TextFieldValue>,
        ){
            val scope = rememberCoroutineScope()
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = stringResource(R.string.user_icon_description),
                        modifier = Modifier
                            .clickable { }
                            .size(35.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text(clickedUsername, color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineMedium.fontSize)
                }

                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary),
                    onClick = {
                        scope.launch {
                            textFieldValue.value = TextFieldValue(
                                text = textFieldValue.value.text + "@$clickedUsername ",
                                selection = TextRange(textFieldValue.value.selection.start+"@$clickedUsername ".length)
                            )
                            bottomModalState.hide()
                        }
                    }) {
                    Text(stringResource(R.string.reply),color = MaterialTheme.colorScheme.onSecondary)
                }
            }
        }

        /**
         * Will display a [Row] containing the ban, unban, timeout buttons
         *
         *
         * @param banned Boolean to determine if the chatter is already banned or not
         * @param isMod Boolean to determine if the chatter is a mod or not
         * @param closeBottomModal function used to close the [ModalBottomSheetLayout] which the composable should be in
         * @param unbanUser function used to unban user if [banned] is true
         * @param openTimeoutDialog function used to open the TimeoutDialog
         * @param openBanDialog function used to open the BanDialog
         * */
        @Composable
        fun ContentBottom(
            banned: Boolean,
            isMod: Boolean,
            closeBottomModal: () -> Unit,
            unbanUser: () -> Unit,
            openTimeoutDialog:() -> Unit,
            openBanDialog:() -> Unit,
            shouldMonitorUser:Boolean,
            updateShouldMonitorUser:()->Unit
        ){

            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (isMod) {
                        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.End) {
                            Button(
                                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary),
                                onClick = {
                                    openTimeoutDialog()
                                },
                                modifier = Modifier.padding(end = 20.dp)
                            ) {
                                Text(stringResource(R.string.timeout),color = MaterialTheme.colorScheme.onSecondary)
                            }
                            if (banned) {
                                Button(
                                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary),
                                    onClick = {
                                        closeBottomModal()
                                        unbanUser()
                                    }) {
                                    Text(stringResource(R.string.unban),color = MaterialTheme.colorScheme.onSecondary)
                                }
                            } else {
                                Button(
                                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary),
                                    onClick = {
                                        openBanDialog()
                                    }) {
                                    Text(stringResource(R.string.ban),color = MaterialTheme.colorScheme.onSecondary)
                                }
                            }
                        }
                    }
                }
                Row(modifier=Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){

                        Icon(
                            painter = painterResource(id = R.drawable.visibility_24),
                            "Moderation Icon",
                            tint= if(shouldMonitorUser) Color.Yellow else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(35.dp)
                        )


                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary),
                        onClick = {
                            updateShouldMonitorUser()
                        }) {
                        Text(
                            if(shouldMonitorUser)"UnMonitor" else "Monitor",
                            color = MaterialTheme.colorScheme.onSecondary)
                    }
                }


            }/**End of the column**/

        }
    }





}// end of BottomModal


