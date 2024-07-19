package com.example.clicker.presentation.stream.views.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.clicker.R
import com.example.clicker.network.clients.BanUser
import com.example.clicker.network.clients.BanUserData
import com.example.clicker.presentation.sharedViews.ButtonScope


/**
 * TimeListData is used inside of the dialogs to model the internal duration(sent to server) of the timeout/ban and the visual
 * information shown to the user
 *
 * @param time a Int representing the data that is sent to the server
 * @param textDescription a String representing the data that is shown to the user
 * */
data class TimeListData(
    val time:Int,
    val textDescription:String
)

/**
 * ImprovedBanDialog is a composable that represents the dialog a user sees when they want to ban a user
 * - UI demonstration of ImprovedBanDialog is [HERE](https://github.com/thePlebDev/Clicker/wiki/Dialogs#improvedbandialog)
 *
 * @param onDismissRequest a function that is used to close this dialog
 * @param username a String representing the display name for the user that this dialog will affect
 * @param banReason a String representing the reason that this user is going to get banned
 * @param changeBanReason a function used to change the [banReason]
 * @param banUser a function that is used to make the server call to twitch to ban the user

 * */
@Composable
fun ImprovedBanDialog(
    onDismissRequest: () -> Unit,
    username: String,
    banReason: String,
    changeBanReason: (String) -> Unit,
    banUser: () -> Unit,
){
    val timeList = listOf<TimeListData>(
        TimeListData(0, stringResource(R.string.permanently))
    )
    val secondary = MaterialTheme.colorScheme.secondary
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary =MaterialTheme.colorScheme.onPrimary
    val onSecondary = MaterialTheme.colorScheme.onSecondary
    DialogBuilder(
        dialogHeaderContent = {
            DialogHeaderRow(
                username,
                stringResource(R.string.ban)
            )
        },
        dialogSubHeaderContent = {
           SubHeader(
                subTitleText = stringResource(R.string.duration_text)
            )
        },
        dialogRadioButtonsContent = {
            DialogRadioButtonsRow(
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
           DialogConfirmCancelButtonRow(
                onDismissRequest = { onDismissRequest() },
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
        onPrimary = onPrimary

        )

}
@Composable
fun WarningDialog(
    onDismissRequest: () -> Unit
){
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val secondary= MaterialTheme.colorScheme.secondary
    var text by remember { mutableStateOf("") }

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
                WarningDialogHeaderRow(
                    "username",
                    "Warn :"
                )
                WarningSubHeader(
                    "username must acknowledge the warning before chatting in this channel again"
                )
                OutlinedTextField(
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = onPrimary,
                        focusedLabelColor = onPrimary,
                        focusedIndicatorColor = onPrimary,
                        unfocusedIndicatorColor = onPrimary,
                        unfocusedLabelColor = onPrimary,
                    ),
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(stringResource(R.string.reason), color = onPrimary) },
                    modifier = Modifier.fillMaxWidth()
                )
                WarningDialogConfirmCancelButtonRow(
                    cancelText="Cancel",
                    confirmText="Warn",
                    confirmAction={onDismissRequest()},
                    onDismissRequest={onDismissRequest()},

                )

            }
        }
    }

}
@Composable
fun WarningDialogHeaderRow(
    username:String,
    headerText:String,
){
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val largeFontSize = MaterialTheme.typography.headlineLarge.fontSize
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Text(headerText, fontSize = largeFontSize,color = onPrimary)
        Text(username, fontSize = largeFontSize,color = onPrimary)
    }
}
@Composable
fun WarningSubHeader(
    subTitleText:String
){
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val secondary = MaterialTheme.colorScheme.secondary
    val smallFontSize = MaterialTheme.typography.headlineSmall.fontSize

    Divider(color = secondary, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
    Spacer(modifier =Modifier.height(5.dp))
    Text(subTitleText,color = onPrimary.copy(0.8f), fontSize = smallFontSize)
    Spacer(modifier =Modifier.height(5.dp))
}

@Composable
fun WarningDialogConfirmCancelButtonRow(
    onDismissRequest: () -> Unit,
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
                    onDismissRequest()
                    confirmAction()
                },

                )
        }


    }
}

/**
 * ImprovedTimeoutDialog is a composable that represents the dialog a user sees when they want to timeout a user
 * - UI demonstration of ImprovedTimeoutDialog is [HERE](https://github.com/thePlebDev/Clicker/wiki/Dialogs#improvedtimeoutdialog)
 *
 * @param onDismissRequest a function that is used to close this dialog
 * @param username a String representing the display name for the user that this dialog will affect
 * @param timeoutReason a String representing the reason that this user is going to get banned
 * @param changeTimeoutReason a function used to change the [timeoutReason]
 * @param timeoutDuration a Int used to represent what the current timeout duration
 * @param changeTimeoutDuration a function used to change the [timeoutDuration] duration
 * @param timeOutUser a function that is used to make the server call to Twitch to timeout the user

 * */
@Composable
fun ImprovedTimeoutDialog(
    onDismissRequest: () -> Unit,
    username:String,
    timeoutDuration: Int,
    timeoutReason: String,
    changeTimeoutDuration: (Int) -> Unit,
    changeTimeoutReason: (String) -> Unit,
    timeOutUser: () -> Unit

){
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
    DialogBuilder(
        dialogHeaderContent = {
            DialogHeaderRow(
                username,
                stringResource(R.string.timeout_text),
            )
        },
        dialogSubHeaderContent = {
            SubHeader(
                subTitleText = stringResource(R.string.duration_text)
            )
        },
        dialogRadioButtonsContent = {
            DialogRadioButtonsRow(
                dialogDuration = timeoutDuration,
                changeDialogDuration = { duration -> changeTimeoutDuration(duration) },
                timeList = timeList
            )
        },
        dialogTextFieldContent = {
            DialogOutlinedTextField(
                timeoutReason = timeoutReason,
                changeTimeoutReason = { changeTimeoutReason(it) },
            )
        },
        dialogConfirmCancelContent = {
            DialogConfirmCancelButtonRow(
                onDismissRequest = { onDismissRequest() },
                confirmAction = { timeOutUser() },
                confirmText = stringResource(R.string.timeout_confirm),
                cancelText = stringResource(R.string.cancel)
            )
        },
        onDismissRequest = { onDismissRequest() },
        secondary = secondary,
        primary=primary,
        onPrimary = onPrimary

    )

}

/**
 * DialogBuilder is a private function that is used to quickly and consistently create properly styled [Dialog] composables
 * */
@Composable
private fun DialogBuilder(
    dialogHeaderContent:@Composable ImprovedDialogScope.() -> Unit,
    dialogSubHeaderContent:@Composable ImprovedDialogScope.() -> Unit,
    dialogRadioButtonsContent:@Composable ImprovedDialogScope.() -> Unit,
    dialogTextFieldContent:@Composable ImprovedDialogScope.() -> Unit,
    dialogConfirmCancelContent:@Composable ImprovedDialogScope.() -> Unit,
    onDismissRequest: () -> Unit,
    primary: Color,
    onPrimary: Color,
    secondary: Color,

){
    val largeFontSize = MaterialTheme.typography.headlineLarge.fontSize
    val mediumFontSize = MaterialTheme.typography.headlineMedium.fontSize
    val dialogScope = remember{
        ImprovedDialogScope(onPrimary,secondary,largeFontSize,mediumFontSize)
    }
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
                with(dialogScope){
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
 * ImprovedDialogScope is a private class that contains all the composables that are used to create Dialogs inside of this application
 *
 * @param onPrimary a [Color] that is used to show information that is on a dark background
 * @param secondary a [Color] that is represents a purple color and is used to highlight non-primary information
 *
 * @property DialogHeaderRow
 * @property SubHeader
 * @property DialogRadioButtonsRow
 * @property DialogOutlinedTextField
 * @property DialogConfirmCancelButtonRow
 * */
@Stable
private class ImprovedDialogScope(
    val onPrimary: Color,
    val secondary: Color,
    val largeFontSize:TextUnit,
    val mediumFontSize:TextUnit,
){

    /**
     * DialogHeaderRow is a [Row] composable that is meant to show the user very important information.
     * [username] and [headerText] are shown to the user in the largest possible font size, `MaterialTheme.typography.headlineLarge.fontSize`
     *
     * @param username the display name of the user the dialog is about
     * @param headerText the direct information that is meant to be shown to the user of this dialog, ie, `Timeout`/`Ban`.
     *
     * */
    @Composable
    fun DialogHeaderRow(
        username:String,
        headerText:String,
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(headerText, fontSize = largeFontSize,color = onPrimary)
            Text(username, fontSize = largeFontSize,color = onPrimary)
        }
    }

    /**
     * SubHeader is a composable that is meant to display information that is meant to be `secondary`. Information that is
     * not as important as the information displayed in [DialogHeaderRow]
     *
     * @param subTitleText a String representing a small amount of information displayed to the user
     *
     * */
    @Composable
    fun SubHeader(
        subTitleText:String
    ){
        Divider(color = secondary, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
        Text(subTitleText,color = onPrimary, fontSize = mediumFontSize)
    }



    /**
     * DialogRadioButtonsRow a [Row] composable will show the a number of [RadioButton] composables
     *
     * @param dialogDuration a Int used to determine what [RadioButton] is currently selected
     * @param changeDialogDuration a function used to change the [dialogDuration]
     * @param timeList a list of [TimeListData] objects. The length of this list will determine the number of
     * [RadioButton] composables
     *
     * */
    @Composable
    fun DialogRadioButtonsRow(
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
     * DialogOutlinedTextField a [OutlinedTextField] composables that is used to capture user input
     *
     * @param timeoutReason a String meant ot represent the reason the dialog is being used
     * @param changeTimeoutReason a function used to change [timeoutReason]
     *
     * */
    @Composable
    fun DialogOutlinedTextField(
        timeoutReason:String,
        changeTimeoutReason: (String) -> Unit
    ){
        val customTextSelectionColors = TextSelectionColors(
            handleColor = MaterialTheme.colorScheme.secondary,
            backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
        )
        CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
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
                label = {
                    Text(stringResource(R.string.reason))
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

    }

    /**
     * DialogConfirmCancelButtonRow a
     *

     * @param onDismissRequest a function that is used to close the current dialog
     * @param confirmAction a function that is used to confirm the action of the current dialog
     * @param cancelText a String meant to represent the cancelation of the dialogs intended action.
     * @param confirmText a String meant to represent the confirmation of the dialogs intended action.
     *
     * */
    @Composable
    fun DialogConfirmCancelButtonRow(
        onDismissRequest: () -> Unit,
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
                        onDismissRequest()
                        confirmAction()
                    },

                    )
            }


        }
    }

}


