package com.example.clicker.network.repository

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
import com.example.clicker.network.clients.TwitchEmoteClient
import com.example.clicker.network.domain.TwitchEmoteRepo
import com.example.clicker.network.repository.util.handleException
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TwitchEmoteImpl @Inject constructor(
    private val twitchEmoteClient: TwitchEmoteClient
): TwitchEmoteRepo {

    private val modBadge = "https://static-cdn.jtvnw.net/badges/v1/3267646d-33f0-4b17-b3df-f923a41db1d0/1"
    private val subBadge = "https://static-cdn.jtvnw.net/badges/v1/5d9f2208-5dd8-11e7-8513-2ff4adfae661/1"
    private val feelsGood = "https://static-cdn.jtvnw.net/emoticons/v2/64138/static/light/1.0"
    private val feelsGoodId ="SeemsGood"
    private val modId = "modIcon"
    private val subId = "subIcon"
    private val monitorId ="monitorIcon"
    /** - inlineContentMap represents the inlineConent for the sub,mod and SeemsGood icons.
     * This is created before the [getGlobalEmotes] method is called so that there can still be mod and sub icons as soon as the
     * user loads into chat
     * */
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
    private val _emoteList: MutableState<EmoteListMap> = mutableStateOf(EmoteListMap(inlineContentMap))

    override val emoteList: State<EmoteListMap> = _emoteList

      override fun getGlobalEmotes(
        oAuthToken: String,
        clientId: String,
    ): Flow<Response<Boolean>> = flow {
        emit(Response.Loading)
         val response = twitchEmoteClient.getGlobalEmotes(
             authorization = "Bearer $oAuthToken",
             clientId = clientId
         )
          val innerInlineContentMap: MutableMap<String, InlineTextContent> = mutableMapOf()

          if (response.isSuccessful) {
              val data = response.body()?.data
                  inlineContentMap.forEach{
                      innerInlineContentMap[it.key] = it.value
                  }

            val parsedEmoteData = data?.map { EmoteNameUrl(it.name,it.images.url_1x) }
              parsedEmoteData?.forEach {emoteValue ->
                createMapValue(
                    emoteValue,
                    innerInlineContentMap
                )

            }
              _emoteList.value = emoteList.value.copy(
              map = innerInlineContentMap
          )




            emit(Response.Success(true))
        } else {
            Log.d("getGlobalEmotes","FAIL")
            Log.d("getGlobalEmotes","MESSAGE --> ${response.code()}")
            Log.d("getGlobalEmotes","MESSAGE--> ${response.message()}")
            emit(Response.Failure(Exception("Unable to delete message")))
        }
    }.catch { cause ->
          Log.d("getGlobalEmotes","caught error message ->${cause.message}")
          Log.d("getGlobalEmotes","caught error cause ->${cause.cause}")

        handleException(cause)
    }



}

fun createMapValue(
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
data class EmoteNameUrl(
    val name:String,
    val url:String
)

@Immutable
data class EmoteListMap(
    val map:Map<String, InlineTextContent>
)