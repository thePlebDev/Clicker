package com.example.clicker.presentation.stream.views.streamManager

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.clicker.R
import com.example.clicker.presentation.stream.views.dialogs.Dialogs
import com.example.clicker.presentation.stream.views.dialogs.TimeListData

object ModViewDialogs {
    //TODO: REFACTOR ALL DIALOG RELATED THINGS ONCE ITEMS START GETTING VIEWMODEL DATA
    @Composable
    fun ModViewTimeoutDialog(
        closeDialog: () -> Unit
    ){
        val timeList = listOf<TimeListData>(
            TimeListData(60, stringResource(R.string.one_minute)),
            TimeListData(600, stringResource(R.string.ten_minutes)),
            TimeListData(1800, stringResource(R.string.thirty_minutes)),
            TimeListData(604800, stringResource(R.string.one_week))
        )
        RadioButtonDialog(
            dialogHeaderContent={
                DialogHeader(username ="thePlebDev", headerText = "Timeout:")
            },
            dialogSubHeaderContent = {
                SubHeader(dividerColor = MaterialTheme.colorScheme.secondary, subTitleText ="Duration:" )
            },
            dialogRadioButtonsContent={
                DialogRadioButtonsRow(
                    unselectedColor = MaterialTheme.colorScheme.onPrimary,
                    selectedColor = MaterialTheme.colorScheme.secondary,
                    textColor = MaterialTheme.colorScheme.onPrimary,
                    dialogDuration = 3,
                    changeDialogDuration = {},
                    timeList = timeList
                )
            },
            dialogConfirmCancelContent = {
                DialogConfirmCancel(
                    closeDialog = { closeDialog() },
                    confirmAction = {  },
                    cancelText = "Cancel",
                    confirmText = "Timeout"
                )
            },
            dialogTextFieldContent = {
                OutlinedTextContent(
                    textColor = MaterialTheme.colorScheme.onPrimary,
                    timeoutReason = "",
                    textLabel = stringResource(R.string.reason),
                    changeTimeoutReason = {}
                )
            },
            onDismissRequest = {closeDialog()},
            primary = MaterialTheme.colorScheme.primary,
            secondary = MaterialTheme.colorScheme.secondary
        )
    }
    @Composable
    fun RadioButtonDialog(
        dialogHeaderContent:@Composable DialogHeaderScope.() -> Unit,
        dialogSubHeaderContent:@Composable DialogHeaderScope.() -> Unit,
        dialogRadioButtonsContent:@Composable DialogContentScope.() -> Unit,
        dialogTextFieldContent:@Composable DialogContentScope.() -> Unit,
        dialogConfirmCancelContent:@Composable DialogButtons.() -> Unit,
        onDismissRequest: () -> Unit,
        primary: Color,
        secondary: Color
    ){
        val textColor = MaterialTheme.colorScheme.onPrimary
        val buttonContainerColor = MaterialTheme.colorScheme.secondary
        val headerScope = remember{ DialogHeaderScope(textColor = textColor) }
        val dialogContentScope = remember{ DialogContentScope() }
        val dialogButtonScope  = remember{ DialogButtons(buttonContainerColor = buttonContainerColor,textColor =textColor) }
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
                    with(headerScope){
                        dialogHeaderContent()
                        dialogSubHeaderContent()
                    }
                    with(dialogContentScope){
                        dialogRadioButtonsContent()
                        dialogTextFieldContent()
                    }
                    with(dialogButtonScope){
                        dialogConfirmCancelContent()
                    }

                }
            }
        }

    }

    @Stable
    class DialogButtons(
        private val buttonContainerColor: Color, //should be secondary
        private val textColor: Color //should be onSecondary
    ){
        @Composable
        fun DialogConfirmCancel(
            closeDialog: () -> Unit,
            confirmAction: () -> Unit,
            cancelText:String,
            confirmText:String

        ){
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = { closeDialog() },
                    modifier = Modifier.padding(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor =buttonContainerColor )
                ) {
                    Text(cancelText, color = textColor)
                }
                // todo: Implement the details of the timeout implementation
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor =buttonContainerColor ),
                    onClick = {
                        closeDialog()
                        confirmAction()
                    }, modifier = Modifier.padding(10.dp)
                ) {
                    Text(confirmText, color = textColor)
                }
            }
        }
    }

    @Stable
    class DialogContentScope(){
        @Composable
        fun DialogRadioButtonsRow(
            unselectedColor: Color,
            selectedColor: Color,
            textColor: Color,
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
                            colors =  RadioButtonDefaults.colors( selectedColor=selectedColor, unselectedColor = unselectedColor),
                            selected = dialogDuration == timeData.time,
                            onClick = { changeDialogDuration(timeData.time) }
                        )
                        Text(timeData.textDescription, color = textColor)
                    }
                }


            }
        }

        @Composable
        fun OutlinedTextContent(
            textColor: Color,
            timeoutReason:String,
            textLabel:String,
            changeTimeoutReason:(String)->Unit,
        ){
            OutlinedTextField(
                colors = TextFieldDefaults.textFieldColors(
                    textColor = textColor,
                    focusedLabelColor = textColor,
                    focusedIndicatorColor = textColor,
                    unfocusedIndicatorColor = textColor,
                    unfocusedLabelColor = textColor
                ),
                value = timeoutReason,
                onValueChange = { changeTimeoutReason(it) },
                label = {
                    Text(textLabel)
                }
            )
        }

    }
    @Stable
    class DialogHeaderScope(
        private val textColor: Color
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
                Text(
                    headerText,
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                    color = textColor
                )
                Text(
                    username,
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                    color = textColor
                )
            }
        }
        @Composable
        fun SubHeader(
            dividerColor: Color,
            subTitleText:String
        ){
            Divider(color = dividerColor, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
            Text(
                subTitleText,
                color = textColor,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize
            )
        }
    }

    @Composable
    fun ModViewBanDialog(
        closeDialog:() ->Unit,
    ){
        Dialogs.BanDialog(
            onDismissRequest={closeDialog()},
            username="thePlebDev",
            banReason="",
            changeBanReason={},
            banUser={},
            clickedUserId="",
            closeDialog={closeDialog()},

            )

    }
    /*********************************************END OF  DIALOGS*****************************************************************/


}