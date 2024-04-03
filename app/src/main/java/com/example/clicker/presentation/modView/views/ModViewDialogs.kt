package com.example.clicker.presentation.modView.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
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
import androidx.compose.runtime.CompositionLocalProvider
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
    fun ModViewBanDialog(
        closeDialog: () -> Unit,
        swipedMessageUsername:String,
        banDuration:Int,
        changeBanDuration:(Int)->Unit,
        banReason: String,
        changeBanReason: (String) -> Unit
    ){
        val timeList = listOf<TimeListData>(
            TimeListData(0, stringResource(R.string.permanently))
        )
        RadioButtonDialog(
            dialogHeaderContent={
                DialogHeader(username =swipedMessageUsername, headerText = "Timeout:")
            },
            dialogSubHeaderContent = {
                SubHeader(dividerColor = MaterialTheme.colorScheme.secondary, subTitleText ="Duration:" )
            },
            dialogRadioButtonsContent={
                DialogRadioButtonsRow(
                    unselectedColor = MaterialTheme.colorScheme.onPrimary,
                    selectedColor = MaterialTheme.colorScheme.secondary,
                    textColor = MaterialTheme.colorScheme.onPrimary,
                    dialogDuration = banDuration,
                    changeDialogDuration = {newValue->changeBanDuration(newValue) },
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
                    timeoutReason = banReason,
                    textLabel = stringResource(R.string.reason),
                    changeTimeoutReason = {newValue ->changeBanReason(newValue)}
                )
            },
            onDismissRequest = {closeDialog()},
            primary = MaterialTheme.colorScheme.primary,
            secondary = MaterialTheme.colorScheme.secondary
        )

    }

    @Composable
    fun ModViewTimeoutDialog(
        closeDialog: () -> Unit,
        swipedMessageUsername:String,
        timeoutDuration:Int,
        changeTimeoutDuration:(Int)->Unit,
        timeoutReason: String,
        changeTimeoutReason: (String) -> Unit
    ){
        val timeList = listOf<TimeListData>(
            TimeListData(60, stringResource(R.string.one_minute)),
            TimeListData(600, stringResource(R.string.ten_minutes)),
            TimeListData(1800, stringResource(R.string.thirty_minutes)),
            TimeListData(604800, stringResource(R.string.one_week))
        )
        RadioButtonDialog(
            dialogHeaderContent={
                DialogHeader(username =swipedMessageUsername, headerText = "Timeout:")
            },
            dialogSubHeaderContent = {
                SubHeader(dividerColor = MaterialTheme.colorScheme.secondary, subTitleText ="Duration:" )
            },
            dialogRadioButtonsContent={
                DialogRadioButtonsRow(
                    unselectedColor = MaterialTheme.colorScheme.onPrimary,
                    selectedColor = MaterialTheme.colorScheme.secondary,
                    textColor = MaterialTheme.colorScheme.onPrimary,
                    dialogDuration = timeoutDuration,
                    changeDialogDuration = {newValue->changeTimeoutDuration(newValue) },
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
                    timeoutReason = timeoutReason,
                    textLabel = stringResource(R.string.reason),
                    changeTimeoutReason = {newValue ->changeTimeoutReason(newValue)}
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
        dialogConfirmCancelContent:@Composable DialogButtonsScope.() -> Unit,
        onDismissRequest: () -> Unit,
        primary: Color,
        secondary: Color
    ){
        val textColor = MaterialTheme.colorScheme.onPrimary
        val buttonContainerColor = MaterialTheme.colorScheme.secondary
        val headerScope = remember{ DialogHeaderScope(textColor = textColor) }
        val dialogContentScope = remember{ DialogContentScope() }
        val dialogButtonScope  = remember{ DialogButtonsScope(buttonContainerColor = buttonContainerColor,textColor =textColor) }
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

    /**
     * DialogButtonsScope contains all of the composables that should when buttons on created inside of a [Dialog]
     *
     * @property DialogConfirmCancel
     *
     * @param buttonContainerColor a shared Color that will determine the color of the Button's container color
     * @param textColor a shared Color that is used for the Text shown on the buttons
     * */
    @Stable
    class DialogButtonsScope(
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

    /**
     * DialogContentScope contains all of the composables that should when creating what is meant to be displayed inside of a [Dialog]
     *
     * @property DialogRadioButtonsRow
     * @property OutlinedTextContent
     * */
    @Stable
    class DialogContentScope(){

        /**
         * DialogRadioButtonsRow a [Row] composable that will show a [RadioButton] for every [timeList] object
         *
         * @param unselectedColor a Color that is used for the unselected color of the RadioButtons inside of this composable
         * @param selectedColor a Color that is used for the selected color of the RadioButtons inside of this composable
         * @param textColor a Color that is used for the [Text] that will be shown under every [RadioButton]
         * @param dialogDuration a Int that is used for determining if the radio button is selected or not
         * @param changeDialogDuration a function used to change the external value of [dialogDuration]
         * @param timeList a list of [TimeListData] objects. Used to determine how many [RadioButton] composables are shown
         * */
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

        /**
         * OutlinedTextContent a [OutlinedTextField] composable that will show the user a text field where they can type a message out
         *
         * @param textColor a Color that is used for the text of the internal [OutlinedTextField]
         * @param timeoutReason a String that is stored externally and used to represent what the user is typing
         * @param textLabel a String that will represent the label of the [OutlinedTextField]
         * @param changeTimeoutReason  a function used to change the [timeoutReason]
         * */
        @Composable
        fun OutlinedTextContent(
            textColor: Color,
            timeoutReason:String,
            textLabel:String,
            changeTimeoutReason:(String)->Unit,
        ){
            val customTextSelectionColors = TextSelectionColors(
                handleColor = MaterialTheme.colorScheme.secondary,
                backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
            )
            CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
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

    }
    // should document and put UI examples on the wiki
    /**
     * DialogHeaderScope contains all of the composables that should be used when a header is needed when building a Dialog
     *
     * @property DialogHeader
     * @property SubHeader
     * */
    @Stable
    class DialogHeaderScope(
        private val textColor: Color
    ){
        /**
         * DialogHeader is meant to show the user two words,[username] and [headerText], in a [Row] with a horizontalArrangement of
         * [Arrangement.SpaceAround]. The words shown in this composable are in the highest possible font size, `MaterialTheme.typography.headlineLarge.fontSize`
         *
         * @param username a String meant to represent the who this dialog is targeted at
         * @param headerText a String meant to represent what this dialog is about
         * */
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

        /**
         * SubHeader is meant to show the user a [Divider] of color [dividerColor] followed by a [Text] containing the [subTitleText].
         * The font size of [subTitleText] is `MaterialTheme.typography.headlineMedium.fontSize`
         *
         * @param dividerColor a Color that is used to determine the color of the internal [Divider]
         * @param subTitleText a String meant to represent information that is of less priority that text inside of [DialogHeader]
         * */
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





} /*********************************************END OF  DIALOGS*****************************************************************/