package com.example.clicker.presentation.stream.views.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.clicker.R
import com.example.clicker.network.clients.BanUser
import com.example.clicker.network.clients.BanUserData
import com.example.clicker.presentation.sharedViews.ButtonScope


data class TimeListData(
    val time:Int,
    val textDescription:String
)

/**
 * Dialogs contains all the composables responsible for creating all the dialogs shown throughout this application
 *
 * */
object Dialogs{

    /**
     * TimeoutDialog is a implementation used inside of [BottomModal.BanTimeOutDialogs] to shown to the user when they want
     * to time out a user
     *
     * @param onDismissRequest A function that is used to hide this dialog
     * @param username A string to be shown on the dialog. This will be the user who's message was clicked on
     * @param timeoutDuration A integer representing the amount of time (in seconds) a user is banned for
     * @param timeoutReason A String representing a reason why the user is timed out
     * @param changeTimeoutDuration A function used to change the [timeoutDuration]
     * @param changeTimeoutReason A function used to change the [timeoutReason]
     * @param closeDialog a function that is used to close the dialog and the bottom modal
     * @param timeOutUser a function that will trigger the proccess to ban the user
     *
     * */
    @Composable
    fun TimeoutDialog(
        onDismissRequest: () -> Unit,
        username: String,
        timeoutDuration: Int,
        timeoutReason: String,
        changeTimeoutDuration: (Int) -> Unit,
        changeTimeoutReason: (String) -> Unit,
        closeDialog: () -> Unit,
        timeOutUser: () -> Unit
    ) {
        val secondary = MaterialTheme.colorScheme.secondary
        val primary = MaterialTheme.colorScheme.primary
        val onPrimary =MaterialTheme.colorScheme.onPrimary
        val onSecondary = MaterialTheme.colorScheme.onSecondary
        val timeList = listOf<TimeListData>(
            TimeListData(60, stringResource(R.string.one_minute)),
            TimeListData(600, stringResource(R.string.ten_minutes)),
            TimeListData(1800, stringResource(R.string.thirty_minutes)),
            TimeListData(604800, stringResource(R.string.one_week))
        )
        DialogBuilder.RadioButtonDialog(
            dialogHeaderContent = {
                DialogParts.DialogHeader(
                    username,
                    stringResource(R.string.timeout_text), onPrimary
                )
            },
            dialogSubHeaderContent = {
                DialogParts.SubHeader(
                    secondary = secondary,
                    onPrimary = onPrimary,
                    subTitleText = stringResource(R.string.duration_text)
                )
            },
            dialogRadioButtonsContent = {
                DialogParts.DialogRadioButtons(
                    onPrimary = onPrimary,
                    secondary = secondary,
                    dialogDuration = timeoutDuration,
                    changeDialogDuration = { duration -> changeTimeoutDuration(duration) },
                    timeList = timeList
                )
            },
            dialogTextFieldContent = {
                OutlinedTextField(
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = onPrimary,
                        focusedLabelColor = onPrimary,
                        focusedIndicatorColor = onPrimary,
                        unfocusedIndicatorColor = onPrimary,
                        unfocusedLabelColor = onPrimary
                    ),
                    value = timeoutReason,
                    onValueChange = { changeTimeoutReason(it) },
                    label = { Text(stringResource(R.string.reason)) },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            dialogConfirmCancelContent = {
                DialogParts.DialogConfirmCancel(
                    onDismissRequest = { onDismissRequest() },
                    closeDialog = { closeDialog() },
                    confirmAction = { timeOutUser() },
                    confirmText = stringResource(R.string.timeout_confirm),
                    cancelText = stringResource(R.string.cancel)
                )
            },
            onDismissRequest = { onDismissRequest() },
            secondary = secondary,
            primary = primary

        )

    }



    /**
     * BanDialog is a implementation used inside of [BottomModal.BanTimeOutDialogs] to shown to the user when they want
     * to ban a user
     *
     * @param onDismissRequest a function meant to close the dialog
     * @param username A string representing the username of the user's chat message clicked
     * @param banReason A string representing the reasons the user is getting banned
     * @param changeBanReason a function meant to change the [banReason]
     * @param banUser a function used to send the ban request to the Twitch server
     * @param clickedUserId a String representing the clicked user's id, this is used inside the [banUser] function
     * @param closeDialog a function meant to close the dialog and the bottom modal
     * */
    @Composable
    fun BanDialog(
        onDismissRequest: () -> Unit,
        username: String,
        banReason: String,
        changeBanReason: (String) -> Unit,
        banUser: () -> Unit,
        closeDialog: () -> Unit,
    ) {
        val secondary = androidx.compose.material3.MaterialTheme.colorScheme.secondary
        val primary = androidx.compose.material3.MaterialTheme.colorScheme.primary
        val onPrimary = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
        val onSecondary = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
        val timeList = listOf<TimeListData>(
            TimeListData(0, stringResource(R.string.permanently))
        )

        DialogBuilder.RadioButtonDialog(
            dialogHeaderContent = {
                DialogParts.DialogHeader(
                    username,
                    stringResource(R.string.ban), onPrimary
                )
            },
            dialogSubHeaderContent = {
                DialogParts.SubHeader(
                    secondary = secondary,
                    onPrimary = onPrimary,
                    subTitleText = stringResource(R.string.duration_text)
                )
            },
            dialogRadioButtonsContent = {
                DialogParts.DialogRadioButtons(
                    onPrimary = onPrimary,
                    secondary = secondary,
                    dialogDuration = 0,
                    changeDialogDuration = {},
                    timeList = timeList
                )
            },
            dialogTextFieldContent = {
                OutlinedTextField(
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = onPrimary,
                        focusedLabelColor = onPrimary,
                        focusedIndicatorColor = onPrimary,
                        unfocusedIndicatorColor = onPrimary,
                        unfocusedLabelColor = onPrimary,
                    ),
                    value = banReason,
                    onValueChange = { changeBanReason(it) },
                    label = { Text(stringResource(R.string.reason), color = onPrimary) },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            dialogConfirmCancelContent = {
                DialogParts.DialogConfirmCancel(
                    onDismissRequest = { onDismissRequest() },
                    closeDialog = { closeDialog() },
                    cancelText = stringResource(R.string.cancel),
                    confirmText = stringResource(R.string.ban),
                    confirmAction = {
                        banUser()
                    }
                )
            },
            onDismissRequest = { onDismissRequest() },
            primary = primary,
            secondary = secondary,

            )

    }
    private object DialogBuilder {

        /**
         * - The typical Dialog shown to the user when a choice is needed to be made. Typically contains radio buttons and a text field
         *
         * - A typical RadioButtonDialog UI demonstration is shown, [HERE](https://theplebdev.github.io/Modderz-style-guide/#RadioButtonDialog)
         *
         * @param dialogHeaderContent Header shown to the user. Should immediate tell the use what this dialog is meant to do
         * @param dialogSubHeaderContent A header smaller than [dialogHeaderContent] and placed below it. Should display additional text the user needs to know
         * @param dialogRadioButtonsContent Will contain all the possible radio button choices show to the user. Typically no more than 4 is shown
         * @param dialogTextFieldContent A typical [OutlinedTextField] allowing the user to type and fill it out
         * @param dialogConfirmCancelContent Should be a [Row] of Buttons giving the user the ability to confirm or deny their choices
         * @param onDismissRequest A function that will be run to close the Dialog
         * @param primary The primary color of the Dialog
         * @param secondary the color or the text in the dialog
         * */
        @Composable
        fun RadioButtonDialog(
            dialogHeaderContent:@Composable () -> Unit,
            dialogSubHeaderContent:@Composable () -> Unit,
            dialogRadioButtonsContent:@Composable () -> Unit,
            dialogTextFieldContent:@Composable () -> Unit,
            dialogConfirmCancelContent:@Composable () -> Unit,
            onDismissRequest: () -> Unit,
            primary: Color,
            secondary: Color
        ){
            Dialog(onDismissRequest = { onDismissRequest() }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = primary,
                    border = BorderStroke(2.dp, secondary)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .background(primary)
                    ) {
                        dialogHeaderContent()
                        dialogSubHeaderContent()
                        dialogRadioButtonsContent()
                        dialogTextFieldContent()
                        dialogConfirmCancelContent()
                    }
                }
            }

        }

    }
     /**
     * DialogParts contains all the possible composables that can be used to create a Dialog. Currently there are only 4
     * composables:
     *
     * - [DialogConfirmCancel] : A [Row] containing two buttons, one for confirmation and another for cancellation
     *
     * - [DialogRadioButtons] : A [Row] containing radio buttons. It is a main component of [RadioButtonDialog]
     *
     * - [SubHeader] : A simple [Divider] and text, should be placed below a [DialogHeader]. Used to display secondary data
     *
     * - [DialogHeader] : A [Row] containing important information that the user must know
     *
     * */
    private object DialogParts{

        /**
         *  Used to display a choice between success and cancel to a user. The Buttons will be displayed in a row at the end of that row
         *
         *  @param secondary Color for the background of the buttons
         *  @param onSecondary Color for the text that appears on the buttons
         *  @param onDismissRequest function called to dismiss the dialog
         *  @param closeDialog function called to close dialog
         *  @param confirmAction function called when the confirm button is pressed
         *  @param cancelText String placed on the button to represent the cancel action
         *  @param confirmText String placed on the button to represent the confirm action
         *
         * */
        @Composable
        fun DialogConfirmCancel(
            onDismissRequest: () -> Unit,
            closeDialog: () -> Unit,
            confirmAction: () -> Unit,
            cancelText:String,
            confirmText:String

        ){
            val fontSize =MaterialTheme.typography.headlineSmall.fontSize

            val buttonScope = remember(){ ButtonScope(fontSize) }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                with(buttonScope){
                    this.Button(
                        text =cancelText,
                        onClick = { onDismissRequest()},

                    )
                    Spacer(modifier =Modifier.width(15.dp))
                    this.Button(
                        text =confirmText,
                        onClick = {
                            closeDialog()
                            confirmAction()
                        },

                    )
                }


            }
        }

        /**
         *  A group of radio buttons meant to present a choice to the user
         *
         *  @param onPrimary Color that represents the text and the unselected choice of the radio buttons
         *  @param secondary Color that represents the selected color of the Radio button
         *  @param dialogDuration Integer used to determine which radio button is selected
         *  @param changeDialogDuration function used to change the [dialogDuration] and show a new selected radio button
         *  @param timeList a list of [TimeListData] that determines how many radio buttons are displayed. A recommend size is 4
         *
         * */
        @Composable
        fun DialogRadioButtons(
            onPrimary: Color,
            secondary: Color,
            dialogDuration: Int,
            changeDialogDuration: (Int) -> Unit,
            timeList:List<TimeListData>,

            ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if(timeList.size ==1) Arrangement.Start  else Arrangement.SpaceEvenly
            ) {
                for(timeData in timeList){
                    Column {

                        RadioButton(
                            colors =  RadioButtonDefaults.colors( selectedColor=secondary, unselectedColor = onPrimary),
                            selected = dialogDuration == timeData.time,
                            onClick = { changeDialogDuration(timeData.time) }
                        )
                        Text(timeData.textDescription,color = onPrimary)
                    }
                }


            }
        }


        /**
         *  A smaller header meant to display secondary data.
         *  This data is not majorly important but it is still nice to know
         *
         *  @param secondary Color that resents the color of the [Divider]
         *  @param onPrimary Color that represents the text of the [subTitleText]
         *  @param subTitleText String used to display the secondary information to the user
         *
         * */
        @Composable
        fun SubHeader(
            secondary: Color,
            onPrimary: Color,
            subTitleText:String
        ){
            Divider(color = secondary, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
            Text(subTitleText,color = onPrimary, fontSize = MaterialTheme.typography.headlineMedium.fontSize)
        }

        /**
         *  A large header meant to display critical information to the user. Usualy placed above a [SubHeader]
         *
         *  @param username String meant to represent an individual
         *  @param headerText String meant to convey critical information to the person looking at it
         *  @param onPrimary Color that will be used for both [username] and [onPrimary]
         *
         * */
        @Composable
        fun DialogHeader(
            username:String,
            headerText:String,
            onPrimary: Color
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(headerText, fontSize = MaterialTheme.typography.headlineLarge.fontSize,color = onPrimary)
                Text(username, fontSize = MaterialTheme.typography.headlineLarge.fontSize,color = onPrimary)
            }
        }

    }
}


