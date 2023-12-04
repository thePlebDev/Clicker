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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.clicker.presentation.stream.views.BottomModal.ClickedUserMessages
import com.example.clicker.presentation.stream.views.BottomModal.ContentBanner
import com.example.clicker.presentation.stream.views.BottomModal.ContentBottom

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
object DialogBuilder {

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
object DialogParts{
    @Composable
    fun DialogConfirmCancel(
        secondary: Color,
        onSecondary: Color,
        onDismissRequest: () -> Unit,
        closeDialog: () -> Unit,
        timeOutUser: () -> Unit,
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
                    timeOutUser()
                }, modifier = Modifier.padding(10.dp)) {
                Text(confirmText,color = onSecondary)
            }
        }
    }
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

    @Composable
    fun SubHeader(
        secondary: Color,
        onPrimary: Color,
        subTitleText:String
    ){
        Divider(color = secondary, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
        Text(subTitleText,color = onPrimary, fontSize = 20.sp)
    }
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