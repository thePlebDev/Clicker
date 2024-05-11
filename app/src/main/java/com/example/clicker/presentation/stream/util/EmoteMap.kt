package com.example.clicker.presentation.stream.util

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material.Icon
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.clicker.R
import com.example.clicker.network.repository.TwitchEmoteImpl
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EmoteMap @Inject constructor(
    private val twitchEmoteImpl: TwitchEmoteImpl
){
    private val modBadge = "https://static-cdn.jtvnw.net/badges/v1/3267646d-33f0-4b17-b3df-f923a41db1d0/1"
    private val subBadge = "https://static-cdn.jtvnw.net/badges/v1/5d9f2208-5dd8-11e7-8513-2ff4adfae661/1"
    private val feelsGood = "https://static-cdn.jtvnw.net/emoticons/v2/64138/static/light/1.0"
    private val feelsGoodId ="SeemsGood"
    private val modId = "modIcon"
    private val subId = "subIcon"
    private val monitorId ="monitorIcon"
    private val inlineContentMap = mapOf(
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

            monitorId,
            InlineTextContent(

                Placeholder(
                    width = 20.sp,
                    height = 20.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.visibility_24),
                    "contentDescription",
                    tint= Color.Yellow,
                    modifier = Modifier.size(35.dp)
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
    private val _emoteList: MutableState<EmoteListTest> = mutableStateOf(EmoteListTest(inlineContentMap))
    val emoteList: State<EmoteListTest> = _emoteList

    /**so basically I need to be able to make a request that gets the global emotes and updated [_emoteList]
     * - That request needs the name and the url of every emote and turn it into a map of Pair()
     * */
    fun getGlobalEmotes(
        oAuthToken: String,
        clientId: String,
    ): Flow<Response<Boolean>> {
        Log.d("getGlobalEmotes","EmoteMap.getGlobalEmotes called")
       return twitchEmoteImpl.getGlobalEmotes(oAuthToken, clientId)

    }
}

@Immutable
data class EmoteListTest(
    val map:Map<String, InlineTextContent>
)