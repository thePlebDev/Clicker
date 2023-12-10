package com.example.clicker.presentation.stream.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.clicker.R
import com.example.clicker.network.BanUser
import com.example.clicker.network.BanUserData


data class TimeListData(
    val time:Int,
    val textDescription:String
)

/**
 * DialogBuilder outlines for the possible Dialogs being inside of this app. It currently contains 1 component:
 *
 * - [RadioButtonDialog] : Meant to represent a dialog that pops up to the user,
 * giving a number of choices, text field and a confirm/deny button
 *
 * */
object Dialogs{
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
        val secondary = androidx.compose.material3.MaterialTheme.colorScheme.secondary
        val primary = androidx.compose.material3.MaterialTheme.colorScheme.primary
        val onPrimary = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
        val onSecondary = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
        val timeList = listOf<TimeListData>(
            TimeListData(60, stringResource(R.string.one_minute)),
            TimeListData(600, stringResource(R.string.ten_minutes)),
            TimeListData(1800, stringResource(R.string.thirty_minutes)),
            TimeListData(604800, stringResource(R.string.one_week))
        )
        DialogBuilder.RadioButtonDialog(
            dialogHeaderContent ={DialogParts.DialogHeader(username,
                stringResource(R.string.timeout_text),onPrimary)},
            dialogSubHeaderContent={
                DialogParts.SubHeader(
                    secondary = secondary,
                    onPrimary = onPrimary,
                    subTitleText = stringResource(R.string.duration_text)
                )
            },
            dialogRadioButtonsContent={
                DialogParts.DialogRadioButtons(
                    onPrimary = onPrimary,
                    secondary = secondary,
                    dialogDuration = timeoutDuration,
                    changeDialogDuration={duration ->changeTimeoutDuration(duration)},
                    timeList =timeList
                )
            },
            dialogTextFieldContent={
                OutlinedTextField(
                    colors= TextFieldDefaults.textFieldColors(
                        textColor = onPrimary, focusedLabelColor = onPrimary,
                        focusedIndicatorColor = onPrimary, unfocusedIndicatorColor = onPrimary, unfocusedLabelColor = onPrimary),
                    value = timeoutReason,
                    onValueChange = { changeTimeoutReason(it) },
                    label = { Text(stringResource(R.string.reason)) }
                )
            },
            dialogConfirmCancelContent={
                DialogParts.DialogConfirmCancel(
                    secondary = secondary,
                    onSecondary = onSecondary,
                    onDismissRequest ={onDismissRequest()},
                    closeDialog = {closeDialog()},
                    confirmAction = {timeOutUser()},
                    confirmText = stringResource(R.string.timeout_confirm),
                    cancelText = stringResource(R.string.cancel)
                )
            },
            onDismissRequest={onDismissRequest()},
            secondary = secondary,
            primary =  primary

        )

    }


    @Composable
    fun BanDialog(
        onDismissRequest: () -> Unit,
        username: String,
        banReason: String,
        changeBanReason: (String) -> Unit,
        banUser: (BanUser) -> Unit,
        clickedUserId: String,
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
            dialogHeaderContent = {DialogParts.DialogHeader(username,
                stringResource(R.string.ban),onPrimary) },
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
                    changeDialogDuration={},
                    timeList =timeList
                )
            },
            dialogTextFieldContent = {
                OutlinedTextField(
                    colors= TextFieldDefaults.textFieldColors(
                        textColor = onPrimary, focusedLabelColor = onPrimary,
                        focusedIndicatorColor = onPrimary, unfocusedIndicatorColor = onPrimary, unfocusedLabelColor = onPrimary),
                    value = banReason,
                    onValueChange = { changeBanReason(it) },
                    label = { Text(stringResource(R.string.reason),color = onPrimary) }
                )
            },
            dialogConfirmCancelContent = {
                DialogParts.DialogConfirmCancel(
                    secondary = secondary,
                    onSecondary = onSecondary,
                    onDismissRequest ={onDismissRequest()},
                    closeDialog = {closeDialog()},
                    cancelText = stringResource(R.string.cancel),
                    confirmText = stringResource(R.string.ban),
                    confirmAction = {
                        banUser(
                            BanUser(
                                data = BanUserData(
                                    user_id = clickedUserId,
                                    reason = banReason
                                )
                            )
                        )
                    }
                )
            },
            onDismissRequest = { onDismissRequest() },
            primary =primary ,
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
            secondary: Color,
            onSecondary: Color,
            onDismissRequest: () -> Unit,
            closeDialog: () -> Unit,
            confirmAction: () -> Unit,
            cancelText:String,
            confirmText:String

        ){
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = { onDismissRequest() },
                    modifier = Modifier.padding(10.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = secondary)
                ) {
                    Text(cancelText,color = onSecondary)
                }
                // todo: Implement the details of the timeout implementation
                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = secondary),
                    onClick = {
                        closeDialog()
                        confirmAction()
                    }, modifier = Modifier.padding(10.dp)) {
                    Text(confirmText,color = onSecondary)
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
            Text(subTitleText,color = onPrimary, fontSize = 20.sp)
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
                Text(headerText, fontSize = 22.sp,color = onPrimary)
                Text(username, fontSize = 22.sp,color = onPrimary)
            }
        }

    }
}


