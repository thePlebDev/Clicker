package com.example.clicker.presentation.stream.util

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import javax.inject.Inject

class TextParsing @Inject constructor() {

    val textFieldValue = mutableStateOf(
        TextFieldValue(
            text = "",
            selection = TextRange(0)
        )
    )
}