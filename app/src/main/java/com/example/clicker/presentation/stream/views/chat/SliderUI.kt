package com.example.clicker.presentation.stream.views.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderPositions
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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
fun SliderAdvanced(
    badgeSize: Float,
    changeBadgeSliderValue: (Float) -> Unit,
    usernameSize:Float,
    changeUsernameSize: (Float) -> Unit,
    messageSize:Float,
    changeMessageSize: (Float) -> Unit,
    emoteSize:Float,
    changeEmoteSize: (Float) -> Unit,
    lineHeight:Float,
    changeLineHeight: (Float) -> Unit,
    customUsernameColor:Boolean,
    changeCustomUsernameColor:(Boolean)->Unit
) {

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp)) {
        ChatSlider(
            slideText = "Badge Size",
            sliderValue = badgeSize,
            changeSliderValue= {newValue ->changeBadgeSliderValue(newValue)}
        )

        ChatSlider(
            slideText = "Username Size",
            sliderValue = usernameSize,
            changeSliderValue= {newValue ->changeUsernameSize(newValue)}

        )
        ChatSlider(
            slideText = "Message Size",
            sliderValue = messageSize,
            changeSliderValue= {newValue ->changeMessageSize(newValue)}
        )
        ChatSlider(
            slideText = "Emote Size",
            sliderValue = emoteSize,
            changeSliderValue= {newValue ->changeEmoteSize(newValue)},
            startValue = 35f,
            endValue = 60f
        )
        ChatSlider(
            slideText = "Line height",
            sliderValue = lineHeight,
            changeSliderValue= {newValue ->changeLineHeight(newValue)},
            startValue = (15*1.6).toFloat(),
            endValue = (30*1.6).toFloat()
        )
        UsernameColorSwitch(
            customUsernameColor=customUsernameColor,
            changeCustomUsernameColor ={newValue->changeCustomUsernameColor(newValue)}
        )


    }
}

@Composable
fun UsernameColorSwitch(
    customUsernameColor:Boolean,
    changeCustomUsernameColor:(Boolean)->Unit
){
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text("Custom username color",color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineMedium.fontSize,)
        SwitchWithIcon(
            checkedValue = customUsernameColor,
            changeCheckedValue = {newValue->changeCustomUsernameColor(newValue)}
        )
    }
}

@Composable
fun SwitchWithIcon(
    checkedValue:Boolean,
    changeCheckedValue:(Boolean)->Unit
) {


    Switch(
        checked = checkedValue,
        onCheckedChange = {
            changeCheckedValue(it)
        },
        thumbContent = if (checkedValue) {
            {
                Icon(
                    imageVector = Icons.Filled.Check,
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

@Composable
fun ChatSlider(
    slideText:String,
    sliderValue:Float,
    changeSliderValue:(Float)->Unit,
    startValue:Float = 15f,
    endValue:Float = 35f
){

    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val textFontSize = MaterialTheme.typography.headlineMedium.fontSize
    Column {
        Text(slideText,color = onPrimaryColor, fontSize = textFontSize,)
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Slider(
                modifier = Modifier.fillMaxWidth(0.9f),
                value = sliderValue,
                onValueChange = { changeSliderValue(it) },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.secondary,
                    activeTrackColor = MaterialTheme.colorScheme.secondary,
                    inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                steps = 3,
                valueRange = startValue..endValue
            )

            Spacer(modifier = Modifier.size(10.dp))
        }
    }

}


@Composable
fun ExampleText(
    badgeSize: Float,
    usernameSize: Float,
    messageSize: Float,
    emoteSize: Float,
    lineHeight: Float,
    customUsernameColor:Boolean
){
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
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
                    width = badgeSize.sp,
                    height = badgeSize.sp,
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
                    width = badgeSize.sp,
                    height = badgeSize.sp,
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
                    width = emoteSize.sp,
                    height = emoteSize.sp,
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
        withStyle(style =
        SpanStyle(color = if(customUsernameColor) secondaryColor else onPrimaryColor, fontSize = usernameSize.sp)
        ) {
            append("TestUsername : ")
        }
        withStyle(style = SpanStyle(color = Color.White)) {
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onPrimary, fontSize = messageSize.sp)) {
                append("Test message used to show how chat can look")
            }
            appendInlineContent(feelsGoodId, "[icon]")
            appendInlineContent(feelsGoodId, "[icon]")

            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onPrimary, fontSize = messageSize.sp)) {
                append(" and edit the chat experience")
            }

        }

        appendInlineContent(feelsGoodId, "[icon]")


    }
    Text(text = text,
        inlineContent = inlineContent,
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary).padding(5.dp),
        color = MaterialTheme.colorScheme.onPrimary,
        lineHeight = lineHeight.sp,

        )
}
