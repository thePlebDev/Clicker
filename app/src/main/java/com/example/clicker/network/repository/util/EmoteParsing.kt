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
import com.example.clicker.network.repository.EmoteNameUrlEmoteType

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
                contentDescription = "${emoteValue.name} emote",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
            )
        }

        innerInlineContentMap[emoteValue.name] = value

    }

    /**
     * createMapValueForComposeChatChannelEmotes takes values from [emoteValue] and maps them to [innerInlineContentMap]
     *
     * @param emoteValue a [EmoteNameUrlEmoteType] object that is used to represent a emote collected from the server
     * @param innerInlineContentMap a [MutableMap] object that is used to hold values for the chat UI
     * */
    fun createMapValueForComposeChatChannelEmotes(
        emoteValue: EmoteNameUrlEmoteType,
        innerInlineContentMap: MutableMap<String, InlineTextContent>
    ){
        val url = emoteValue.url
        val value = InlineTextContent(
            Placeholder(
                width = 25.sp,
                height = 25.sp,
                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
            )
        ) {
            AsyncImage(
                model = url,
                contentDescription = "${emoteValue.name} emote",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
            )
        }

        innerInlineContentMap[emoteValue.name] = value

    }
}