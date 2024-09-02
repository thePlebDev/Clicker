package com.example.clicker.presentation.sharedViews

import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector


@Composable
fun SwitchWithIcon(
    checkedValue:Boolean,
    changeCheckedValue:(Boolean)->Unit,
    icon: ImageVector,
    switchEnabled:Boolean = true
) {


    Switch(
        enabled =switchEnabled,
        checked = checkedValue,
        onCheckedChange = {
            changeCheckedValue(it)
        },
        thumbContent = if (checkedValue) {
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                )
            }
        } else {
            null
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.secondary,
            uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
            checkedTrackColor = Color.DarkGray,
            uncheckedTrackColor = Color.DarkGray,
        )
    )
}