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

class Buttons {
}
//MaterialTheme.typography.headlineMedium.fontSize,
@Stable
class ButtonScope(
    private val textSize: TextUnit
) {

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