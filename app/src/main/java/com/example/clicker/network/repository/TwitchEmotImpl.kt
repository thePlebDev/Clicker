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
    private val twitchEmoteClient: TwitchEmoteClient,

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
     * - This value is hardcoded, so that even if all the other requests fail, the user will still be able to see the sub and mod badges
     *
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

    private val _emoteBoardGlobalList = mutableStateOf<EmoteNameUrlList>(EmoteNameUrlList())
    override val emoteBoardGlobalList:State<EmoteNameUrlList> = _emoteBoardGlobalList

    private val _emoteBoardChannelList = mutableStateOf<EmoteNameUrlList>(EmoteNameUrlList())
    override val emoteBoardChannelList:State<EmoteNameUrlList> = _emoteBoardChannelList

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
              val parsedEmoteData = data?.map {
                  EmoteNameUrl(it.name,it.images.url_1x)
              }
              globalEmoteParsing(
                  innerInlineContentMap,
                  parsedEmoteData =parsedEmoteData,
                  updateEmoteListMap={item ->
                      _emoteList.value = emoteList.value.copy(
                          map = item
                      )
                  },
                  updateEmoteList={item ->
                      _emoteBoardGlobalList.value = _emoteBoardGlobalList.value.copy(
                          list = item
                      )
                  },
                  createMapValueForCompose={emoteValue, innerInlineContentMap ->
                      createMapValue(
                          emoteValue,
                          innerInlineContentMap
                      )
                  },
                  updateInlineContent={
                      inlineContentMap.forEach{
                          innerInlineContentMap[it.key] = it.value
                      }
                  }
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


    /**
     * globalEmoteParsing() is a private function used to update the [emoteList], [emoteBoardGlobalList]
     * and the [inlineContentMap].
     *
     * @param innerInlineContentMap is a [MutableMap] used to hold values used by the [InlineTextContent] objects showing the emotes
     * in the text chat
     * @param parsedEmoteData is a nullable List of [EmoteNameUrl] objects that is parsed from the request.
     * @param updateEmoteListMap a function used to update the local [emoteList] object
     * @param updateEmoteList a function used to update the local [emoteBoardGlobalList] object
     * @param createMapValueForCompose a function that is used to take [EmoteNameUrl] objects and add them to the [innerInlineContentMap]
     * @param updateInlineContent a function used to transfer the objects inside of [inlineContentMap] to the newly created [innerInlineContentMap]
     * */
    private fun globalEmoteParsing(
        innerInlineContentMap: MutableMap<String, InlineTextContent>,
        parsedEmoteData: List<EmoteNameUrl>?,
        updateEmoteListMap:(innerInlineContentMap: MutableMap<String, InlineTextContent>) ->Unit,
        updateEmoteList:(item:List<EmoteNameUrl>) ->Unit,
        createMapValueForCompose:(emoteValue: EmoteNameUrl, innerInlineContentMap: MutableMap<String, InlineTextContent>) ->Unit,
        updateInlineContent:()->Unit,

    ){
        updateInlineContent()
        if(parsedEmoteData !== null){
            parsedEmoteData.forEach {emoteValue ->
                createMapValueForCompose(emoteValue,innerInlineContentMap)
            }
            updateEmoteListMap(innerInlineContentMap)
            parsedEmoteData.also {
                updateEmoteList(it)
            }
        }

    }

    override fun getChannelEmotes(
        oAuthToken: String, clientId: String,broadcasterId:String
    ): Flow<Response<Boolean>> =flow{
        emit(Response.Loading)
        val response = twitchEmoteClient.getChannelEmotes(
            authorization = "Bearer $oAuthToken",
            clientId = clientId,
            broadcasterId = broadcasterId
        )
        if(response.isSuccessful){

            val innerInlineContentMap: MutableMap<String, InlineTextContent> = mutableMapOf()
            val data = response.body()?.data
            val parsedEmoteData = data?.map {
                EmoteNameUrl(it.name,it.images.url_1x)
            }

            channelEmoteParsing(
                parsedEmoteData,
                innerInlineContentMap,
                updateEmoteListMap={item ->
                    _emoteList.value = emoteList.value.copy(
                        map = item
                    )
                },
                updateChannelEmoteList={item ->
                    _emoteBoardChannelList.value = _emoteBoardChannelList.value.copy(
                        list = item
                    )
                },
                createMapValueForCompose={emoteValue, innerInlineContentMap ->
                    createMapValue(
                        emoteValue,
                        innerInlineContentMap
                    )
                }
            )


            Log.d("getChannelEmotes","body--> ${response.body()}")

        }else{
            Log.d("getChannelEmotes","FAIL")
            Log.d("getChannelEmotes","MESSAGE --> ${response.code()}")
            Log.d("getChannelEmotes","MESSAGE--> ${response.message()}")
            emit(Response.Failure(Exception("Unable to get emotes")))
        }

    }.catch { cause ->
        Log.d("getChannelEmotes","EXCEPTION error message ->${cause.message}")
        Log.d("getChannelEmotes","EXCEPTION error cause ->${cause.cause}")
        emit(Response.Failure(Exception("Unable to get emotes")))
    }

    /**
     * channelEmoteParsing() is a private function that is used for parsing out the emotes from the request asking Twitch servers
     * to get Channel specific emotes
     * @param innerInlineContentMap is a [MutableMap] used to hold values used by the [InlineTextContent] objects showing the emotes
     * in the text chat
     * @param parsedEmoteData is a nullable List of [EmoteNameUrl] objects that is parsed from the request.
     * @param updateEmoteListMap a function used to update the local [emoteList] object
     * @param createMapValueForCompose a function that is used to take [EmoteNameUrl] objects and add them to the [innerInlineContentMap]
     * */
    private fun channelEmoteParsing(
        parsedEmoteData: List<EmoteNameUrl>?,
        innerInlineContentMap:MutableMap<String, InlineTextContent>,
        updateEmoteListMap:(innerInlineContentMap: MutableMap<String, InlineTextContent>) ->Unit,
        updateChannelEmoteList:(parsedEmoteData: List<EmoteNameUrl>) ->Unit,
        createMapValueForCompose:(emoteValue: EmoteNameUrl, innerInlineContentMap: MutableMap<String, InlineTextContent>) ->Unit,

    ){
        if(parsedEmoteData !== null){
            parsedEmoteData.forEach {emoteValue ->
                createMapValueForCompose(
                    emoteValue,
                    innerInlineContentMap
                )

            }
            updateEmoteListMap(innerInlineContentMap)
            updateChannelEmoteList(parsedEmoteData)
        }
    }



}

/**
 * createMapValue is a private function that creates the a [InlineTextContent] object and adds it to the
 * [innerInlineContentMap] parameter
 *
 * @param emoteValue a [EmoteNameUrl] object used to represent a Twitch emote
 * @param innerInlineContentMap a map used to represent what items are to be shown to the user
 * */
private fun createMapValue(
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

/**
 * EmoteNameUrl represents a single Twitch Emote from the Twitch servers. Each instance of this class is a unique Emote
 *
 * @param name the name of the Twitch emote
 * @param url the url that is hosted on the twitch servers and is what we use to load the image
 * */
data class EmoteNameUrl(
    val name:String,
    val url:String
)

@Immutable
data class EmoteNameUrlList(
 val list:List<EmoteNameUrl> = listOf()
)

@Immutable
data class EmoteListMap(
    val map:Map<String, InlineTextContent>
)