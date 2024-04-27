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
import androidx.compose.runtime.Stable
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
            DialogHeader(
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
           DialogRadioButtons(
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
                closeDialog = { onDismissRequest() },
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
            DialogHeader(
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
            DialogRadioButtons(
                dialogDuration = timeoutDuration,
                changeDialogDuration = { duration -> changeTimeoutDuration(duration) },
                timeList = timeList
            )
        },
        dialogTextFieldContent = {
            DialogTextField(
                timeoutReason = timeoutReason,
                changeTimeoutReason = { changeTimeoutReason(it) },
            )
        },
        dialogConfirmCancelContent = {
            DialogConfirmCancelButtonRow(
                onDismissRequest = { onDismissRequest() },
                closeDialog = { onDismissRequest() },
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

@Composable
private fun DialogBuilder(
    dialogHeaderContent:@Composable ImprovedDialog.() -> Unit,
    dialogSubHeaderContent:@Composable ImprovedDialog.() -> Unit,
    dialogRadioButtonsContent:@Composable ImprovedDialog.() -> Unit,
    dialogTextFieldContent:@Composable ImprovedDialog.() -> Unit,
    dialogConfirmCancelContent:@Composable ImprovedDialog.() -> Unit,
    onDismissRequest: () -> Unit,
    primary: Color,
    onPrimary: Color,
    secondary: Color
){
    val dialogScope = remember{ImprovedDialog(onPrimary,secondary)}
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

@Stable
private class ImprovedDialog(
    val onPrimary: Color,
    val secondary: Color
){

    @Composable
    fun DialogHeader(
        username:String,
        headerText:String,
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

    @Composable
    fun SubHeader(
        subTitleText:String
    ){
        Divider(color = secondary, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
        Text(subTitleText,color = onPrimary, fontSize = MaterialTheme.typography.headlineMedium.fontSize)
    }

    @Composable
    fun DialogRadioButtons(
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
    @Composable
    fun DialogTextField(
        timeoutReason:String,
        changeTimeoutReason: (String) -> Unit
    ){
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
    @Composable
    fun DialogConfirmCancelButtonRow(
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

}


