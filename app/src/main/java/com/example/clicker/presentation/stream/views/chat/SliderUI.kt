package com.example.clicker.presentation.stream.views.chat

import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderPositions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.clicker.R


@Composable
fun SliderAdvancedExample() {


    ExampleText()
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp)) {

        ChatSlider(
            "Badge Size"
        )
        ChatSlider(
            "Username Size"
        )
        ChatSlider(
            "Message Size"
        )
        ChatSlider(
            "Emote Size"
        )
        ChatSlider(
            "Line height"
        )
    }
}

@Composable
fun ChatSlider(
    slideText:String,
){
    var sliderPosition by remember { mutableFloatStateOf(15f) }
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val textFontSize = MaterialTheme.typography.headlineMedium.fontSize
    Column {
        Text(slideText,color = onPrimaryColor, fontSize = textFontSize,)
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Slider(
                modifier = Modifier.fillMaxWidth(0.9f),
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.secondary,
                    activeTrackColor = MaterialTheme.colorScheme.secondary,
                    inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                steps = 3,
                valueRange = 15f..35f
            )

            Spacer(modifier = Modifier.size(10.dp))
        }
    }

}

@Composable
fun ExampleText(){
     val modBadge = "https://static-cdn.jtvnw.net/badges/v1/3267646d-33f0-4b17-b3df-f923a41db1d0/1"
     val subBadge = "https://static-cdn.jtvnw.net/badges/v1/5d9f2208-5dd8-11e7-8513-2ff4adfae661/1"
     val feelsGood = "https://static-cdn.jtvnw.net/emoticons/v2/64138/static/light/1.0"

     val feelsGoodId ="SeemsGood"
     val modId = "moderator"
     val subId = "subscriber"

    val inlineContent = mapOf(
        Pair(

            modId,
            InlineTextContent(

                Placeholder(
                    width = 20.sp,
                    height = 20.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                AsyncImage(
                    model = modBadge,
                    contentDescription = stringResource(R.string.moderator_badge_icon_description),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(2.dp)
                )
            }
        ),
        Pair(

            subId,
            InlineTextContent(

                Placeholder(
                    width = 20.sp,
                    height = 20.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                AsyncImage(
                    model = subBadge,
                    contentDescription = stringResource(R.string.sub_badge_icon_description),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(2.dp)
                )
            }
        ),
        Pair(

            feelsGoodId,
            InlineTextContent(

                Placeholder(
                    width = 35.sp,
                    height = 35.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                AsyncImage(
                    model = feelsGood,
                    contentDescription = stringResource(R.string.moderator_badge_icon_description),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(2.dp)
                )
            }
        ),

        )
    val text = buildAnnotatedString {

        appendInlineContent(modId, "[icon]")
        appendInlineContent(subId, "[icon]")
        withStyle(style = SpanStyle(color = Color.White)) {
            append("TestUsername: ")
        }
        withStyle(style = SpanStyle(color = Color.White)) {
            append("This test message is used to show how chat can look")
            appendInlineContent(feelsGoodId, "[icon]")
            appendInlineContent(feelsGoodId, "[icon]")
            append(" and demonstrate the UI possibilities of editing the chat experience")
        }

        appendInlineContent(feelsGoodId, "[icon]")


    }
    Text(text = text,
        inlineContent = inlineContent,
        modifier = Modifier.fillMaxWidth().padding(5.dp),

        )
}
