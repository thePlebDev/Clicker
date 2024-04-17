package com.example.clicker.presentation.sharedViews

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

/**
 * ButtonScope contains all the Button components that are used throughout the application
 *
 * @property DualIconsButton
 * @property Button
 *
 * @param textSize a [TextUnit] that is used to determine the the size of the text displayed on the button
 *
 * */
@Stable
class ButtonScope(
    private val textSize: TextUnit
) {

    /**
     * DualIconsButton is a [Button] that displays a [Text] surrounded by two [Icon] composables
     * - UI demonstration of DualIconsButton is [HERE](https://github.com/thePlebDev/Clicker/wiki/Buttons#DualIconsButton)
     *
     * @param buttonAction a function that will be called when the internal [Button] is clicked
     * @param iconImageVector a [ImageVector] that represents the two icons shown to the user
     * @param iconDescription a String representing the content description of the [iconImageVector]
     * @param text a String representing the information that is displayed to the user
     * @param modifier a [Modifier] that will be applied to the internal [Button]
     * */
    @Composable
    fun DualIconsButton(
        buttonAction: () -> Unit,
        iconImageVector: ImageVector,
        iconDescription: String,
        text: String,
        modifier: Modifier = Modifier
    ) {
        Button(
            modifier = modifier,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            shape = RoundedCornerShape(4.dp),
            onClick = { buttonAction() }
        ) {
            Icon(
                imageVector = iconImageVector,
                contentDescription = iconDescription,
                tint = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier
            )
            Text(
                text,
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = textSize
            )
            Icon(
                imageVector = iconImageVector,
                contentDescription = iconDescription,
                tint = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier
            )
        }
    }

    /**
     * Button is a basic [Button] that displays a [Text] that will display a short message to the user
     * - UI demonstration of Button is [HERE](https://github.com/thePlebDev/Clicker/wiki/Buttons#button)
     *
     * @param text a String representing the information that is displayed to the user
     * @param onClick a function that will be called when the internal [Button] is clicked
     * @param modifier a [Modifier] that will be applied to the internal [Button]
     * @param textColor a [Color] that will be used to change the color of the [text]
     * @param buttonColors a [ButtonColors] object that will be used to change all the colors of the internal [Button]
     * */
    @Composable
    fun Button(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        textColor: Color = MaterialTheme.colorScheme.onSecondary,
        buttonColors: ButtonColors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Button(
            onClick = { onClick() },
            colors = buttonColors,
            modifier = modifier,
            shape = RoundedCornerShape(4.dp)
        )
        {
            Text(text = text, color = textColor, fontSize = textSize)
        }
    }
}