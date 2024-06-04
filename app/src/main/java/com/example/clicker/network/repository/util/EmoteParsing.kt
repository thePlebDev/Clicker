package com.example.clicker.network.repository.util

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.clicker.R
import com.example.clicker.network.repository.EmoteNameUrl

class EmoteParsing {

     fun createMapValueForComposeChat(
        emoteValue: EmoteNameUrl,
        innerInlineContentMap: MutableMap<String, InlineTextContent>
    ){
        val url = emoteValue.url
        val value = InlineTextContent(
            Placeholder(
                width = 35.sp,
                height = 35.sp,
                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
            )
        ) {
            AsyncImage(
                model = url,
                contentDescription = stringResource(R.string.moderator_badge_icon_description),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
            )
        }

        innerInlineContentMap[emoteValue.name] = value

    }
}