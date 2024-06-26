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
import com.example.clicker.network.clients.BetterTTVChannelEmotes
import com.example.clicker.network.clients.BetterTTVEmoteClient
import com.example.clicker.network.clients.IndivBetterTTVEmote
import com.example.clicker.network.clients.TwitchEmoteClient
import com.example.clicker.network.domain.BetterTTVEmotes
import com.example.clicker.network.domain.TwitchEmoteRepo
import com.example.clicker.network.repository.util.EmoteParsing
import com.example.clicker.network.repository.util.handleException
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TwitchEmoteImpl @Inject constructor(
    private val twitchEmoteClient: TwitchEmoteClient,
    private val betterTTVClient: BetterTTVEmoteClient,
    private val emoteParsing:EmoteParsing = EmoteParsing()

): TwitchEmoteRepo {

    private val modBadge = "https://static-cdn.jtvnw.net/badges/v1/3267646d-33f0-4b17-b3df-f923a41db1d0/1"
    private val subBadge = "https://static-cdn.jtvnw.net/badges/v1/5d9f2208-5dd8-11e7-8513-2ff4adfae661/1"
    private val feelsGood = "https://static-cdn.jtvnw.net/emoticons/v2/64138/static/light/1.0"
    private val feelsGoodId ="SeemsGood"
    //moderator subscriber
    private val modId = "moderator"
    private val subId = "subscriber"
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

    override val emoteList: State<EmoteListMap> = _emoteList //this is what is shown in the chat UI(not emote box UI but chat UI )

    private val _emoteBoardGlobalList = mutableStateOf<EmoteNameUrlList>(EmoteNameUrlList())
    override val emoteBoardGlobalList:State<EmoteNameUrlList> = _emoteBoardGlobalList

    private val _emoteBoardChannelList = mutableStateOf<EmoteNameUrlEmoteTypeList>(EmoteNameUrlEmoteTypeList())
    override val emoteBoardChannelList:State<EmoteNameUrlEmoteTypeList> = _emoteBoardChannelList

    /**Below are the parameters for the global emotes*/
    private val _globalBetterTTVEmotes = mutableStateOf<IndivBetterTTVEmoteList>(IndivBetterTTVEmoteList())
    override val globalBetterTTVEmotes:State<IndivBetterTTVEmoteList> = _globalBetterTTVEmotes
    private val _channelBetterTTVEmotes = mutableStateOf<IndivBetterTTVEmoteList>(IndivBetterTTVEmoteList())
    override val channelBetterTTVEmotes:State<IndivBetterTTVEmoteList> = _channelBetterTTVEmotes

    private val _sharedBetterTTVEmotes = mutableStateOf<IndivBetterTTVEmoteList>(IndivBetterTTVEmoteList())
    override val sharedBetterTTVEmotes:State<IndivBetterTTVEmoteList> = _sharedBetterTTVEmotes




    override fun getGlobalEmotes(
        oAuthToken: String,
        clientId: String,
    ): Flow<Response<Boolean>> = flow {
        emit(Response.Loading)
         val response = twitchEmoteClient.getGlobalEmotes(
             authorization = "Bearer $oAuthToken",
             clientId = clientId
         )
          val newInnerInlineContentMap: MutableMap<String, InlineTextContent> = mutableMapOf()


          if (response.isSuccessful) {
              val data = response.body()?.data
              val parsedEmoteData = data?.map {
                  EmoteNameUrl(it.name,it.images.url_1x)
              }
              //todo: this function signature is terrible, confusing  and needs to be changed
              globalEmoteParsing(
                  newInnerInlineContentMap=newInnerInlineContentMap,
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
                  createMapValueForCompose={emoteValue, innerInlineContentMapThinger ->
                      createMapValue(
                          emoteValue,
                          innerInlineContentMapThinger
                      )
                  },
                  updateInlineContent={
                      //this is copying over inlineContentMap values to newInnerInlineContentMap
                      inlineContentMap.forEach{
                          newInnerInlineContentMap[it.key] = it.value
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
        newInnerInlineContentMap: MutableMap<String, InlineTextContent>,
        parsedEmoteData: List<EmoteNameUrl>?,
        updateEmoteListMap:(newInnerInlineContentMap: MutableMap<String, InlineTextContent>) ->Unit,
        updateEmoteList:(item:List<EmoteNameUrl>) ->Unit,
        createMapValueForCompose:(emoteValue: EmoteNameUrl, innerInlineContentMap: MutableMap<String, InlineTextContent>) ->Unit,
        updateInlineContent:()->Unit,

    ){
        updateInlineContent()
        if(parsedEmoteData !== null){
            parsedEmoteData.forEach {emoteValue ->
                createMapValueForCompose(emoteValue,newInnerInlineContentMap)
            }
            updateEmoteListMap(newInnerInlineContentMap)
            updateEmoteList(parsedEmoteData)
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
            val data = response.body()?.data


            val parsedEmoteData = data?.map {// getting data from the request
                val emoteType = if(it.emote_type =="subscriptions") EmoteTypes.SUBS else EmoteTypes.FOLLOWERS
                Log.d("getChannelEmotesId","emote_type -->$emoteType")
                EmoteNameUrlEmoteType(it.name,it.images.url_1x,emoteType)
            }
            val followerEmotes =parsedEmoteData?.filter { it.emoteType == EmoteTypes.FOLLOWERS}?: listOf()
            val subscriberEmotes = parsedEmoteData?.filter { it.emoteType == EmoteTypes.SUBS} ?: listOf()
            val sortedEmoteData = followerEmotes + subscriberEmotes

            val innerInlineContentMap: MutableMap<String, InlineTextContent> = mutableMapOf()


            //todo: this function signature is terrible, confusing  and needs to be changed
            newChannelEmoteParsing(
                parsedEmoteData=  sortedEmoteData,
                innerInlineContentMap= innerInlineContentMap,
                convertResponseDataToGlobalEmoteMap={emoteValue,innerMap ->
                    createChannelEmoteMapValue(
                        emoteValue,
                        innerMap
                    )
                },
                updateGlobalEmoteMap={innerMap ->
                    _emoteList.value = emoteList.value.copy( // update _emoteList with the newly updated innerInlineContentMap

                        map = _emoteList.value.map + innerMap
                    )

                },
                updateChannelEmoteBoard={channelEmoteList ->
                    _emoteBoardChannelList.value = _emoteBoardChannelList.value.copy(
                        list = channelEmoteList
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
     * @param convertResponseDataToGlobalEmoteMap a function used to convert the data coming from the request to a map that can be added to the emote map
     * @param updateGlobalEmoteMap a function that takes the newly updated [innerInlineContentMap] and adds it to the [emoteList]
     * @param updateChannelEmoteBoard a function that takes the [parsedEmoteData] and updates the [emoteBoardChannelList]
     * */
    private fun newChannelEmoteParsing(
        parsedEmoteData: List<EmoteNameUrlEmoteType>?,
        innerInlineContentMap: MutableMap<String, InlineTextContent>,
        convertResponseDataToGlobalEmoteMap:(emoteValue: EmoteNameUrlEmoteType, innerInlineContentMap: MutableMap<String, InlineTextContent>) ->Unit,
        updateGlobalEmoteMap:(innerMap: MutableMap<String, InlineTextContent>)->Unit,
        updateChannelEmoteBoard:(channelEmoteList:List<EmoteNameUrlEmoteType>)->Unit
    ){
        if(parsedEmoteData !== null){
            parsedEmoteData.forEach {emoteValue -> // convert the parsed data into values that can be stored into _emoteList
                convertResponseDataToGlobalEmoteMap(emoteValue,innerInlineContentMap)
            }
            updateGlobalEmoteMap(innerInlineContentMap)
            updateChannelEmoteBoard(parsedEmoteData)

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
        //todo: I need to create a version of this that has the EmoteNameUrlEmoteType specifically for channel emotes
        emoteParsing.createMapValueForComposeChat(
            emoteValue,
            innerInlineContentMap
        )

    }

    private fun createChannelEmoteMapValue(
        emoteValue: EmoteNameUrlEmoteType,
        innerInlineContentMap: MutableMap<String, InlineTextContent>
    ){
        emoteParsing.createMapValueForComposeChatChannelEmotes(
            emoteValue,
            innerInlineContentMap
        )

    }

    override suspend fun getBetterTTVGlobalEmotes()= flow{
        //1) get the emotes //2)update the _emoteList
        emit(Response.Loading)
        Log.d("getGlobalBetterTTVEmotes", "LOADING")
        val response = betterTTVClient.getGlobalEmotes()
        if (response.isSuccessful) {

            val data = response.body() ?: listOf()
            Log.d("getGlobalBetterTTVEmotes", "DATA ->${data}")

            val parsedEmoteData = data.map { EmoteNameUrlEmoteType(
                name = it.code,
                url="https://cdn.betterttv.net/emote/${it.id}/1x",
                emoteType = EmoteTypes.FOLLOWERS
            )}
            val innerInlineContentMap: MutableMap<String, InlineTextContent> = mutableMapOf()
            parsedEmoteData.forEach {emoteValue -> // convert the parsed data into values that can be stored into _emoteList
                createChannelEmoteMapValue(emoteValue,innerInlineContentMap)
            }
            _emoteList.value = emoteList.value.copy(
                map = _emoteList.value.map + innerInlineContentMap
            )
            _globalBetterTTVEmotes.value = _globalBetterTTVEmotes.value.copy(
                list = data
            )


            emit(Response.Success(data))
        } else {
            Log.d("getGlobalBetterTTVEmotes", "message ->${response.message()}")
            Log.d("getGlobalBetterTTVEmotes", "code ->${response.code()}")
            Log.d("getGlobalBetterTTVEmotes", "FAILED ->${response.body()}")
            emit(Response.Failure(Exception("Failed to get emote")))
        }
    }.catch { cause ->
        Log.d("getChannelEmotes","EXCEPTION error message ->${cause.message}")
        Log.d("getChannelEmotes","EXCEPTION error cause ->${cause.cause}")
        emit(Response.Failure(Exception("Unable to get emotes")))
    }

    override suspend fun getBetterTTVChannelEmotes(broadCasterId:String)= flow {
        emit(Response.Loading)
        Log.d("getBetterTTVChannelEmotes", "LOADING")
        val response = betterTTVClient.getChannelEmotes(broadCasterId)

        if(response.isSuccessful){
            Log.d("getBetterTTVChannelEmotes", "SUCCESS")
           emit(Response.Success(BetterTTVChannelEmotes()))

            val sharedEmotes = response.body()?.sharedEmotes
            val channelEmotes = response.body()?.channelEmotes
            Log.d("getBetterTTVChannelEmotes", "sharedEmotes ->$sharedEmotes")
            Log.d("getBetterTTVChannelEmotes", "channelEmotes ->$channelEmotes")

            /******BELOW IS HOW THE THINGS ARE GOING*********/
            //todo: So I need to create a  List<EmoteNameUrlEmoteType>
//            val innerInlineContentMap: MutableMap<String, InlineTextContent> = mutableMapOf()
//            parsedEmoteData.forEach {emoteValue -> // convert the parsed data into values that can be stored into _emoteList
//                createChannelEmoteMapValue(emoteValue,innerInlineContentMap)
//            }
//            _emoteList.value = emoteList.value.copy(
//                map = _emoteList.value.map + innerInlineContentMap
//            )
//            _globalBetterTTVEmotes.value = _globalBetterTTVEmotes.value.copy(
//                list = data
//            )


            /**************/
            //"https://cdn.betterttv.net/emote/${it.id}/1x"
            val sharedAndChannelList = mutableListOf<EmoteNameUrlEmoteType>()

            channelEmotes?.also{listOfChannelEmotes ->
                val parsedChannelEmotes =listOfChannelEmotes.map {channelEmote ->
                    IndivBetterTTVEmote(
                        id =channelEmote.id,
                        code=channelEmote.code,
                        imageType=channelEmote.imageType,
                        animated = channelEmote.animated,
                        userId = channelEmote.userId,
                        modifier = false
                    )
                }
                Log.d("getBetterTTVChannelEmotes", "parsedData ->$parsedChannelEmotes")
                _channelBetterTTVEmotes.value = _channelBetterTTVEmotes.value.copy(
                    list = parsedChannelEmotes
                )
                listOfChannelEmotes.forEach{
                    sharedAndChannelList.add(
                        EmoteNameUrlEmoteType(
                            name = it.code,
                            url = "https://cdn.betterttv.net/emote/${it.id}/1x",
                            emoteType = EmoteTypes.FOLLOWERS
                        )
                    )
                }
            }

            sharedEmotes?.also{listOfChannelEmotes ->
                val parsedSharedEmotes =listOfChannelEmotes.map {channelEmote ->
                    IndivBetterTTVEmote(
                        id =channelEmote.id,
                        code=channelEmote.code,
                        imageType=channelEmote.imageType,
                        animated = channelEmote.animated,
                        userId = channelEmote.id,
                        modifier = false
                    )
                }
                _sharedBetterTTVEmotes.value = _sharedBetterTTVEmotes.value.copy(
                    list = parsedSharedEmotes
                )
                listOfChannelEmotes.forEach{
                    sharedAndChannelList.add(
                        EmoteNameUrlEmoteType(
                            name = it.code,
                            url = "https://cdn.betterttv.net/emote/${it.id}/1x",
                            emoteType = EmoteTypes.FOLLOWERS
                        )
                    )
                }
            }
            val innerInlineContentMap: MutableMap<String, InlineTextContent> = mutableMapOf()
            sharedAndChannelList.forEach {emoteValue -> // convert the parsed data into values that can be stored into _emoteList
                createChannelEmoteMapValue(emoteValue,innerInlineContentMap)
            }
            _emoteList.value = emoteList.value.copy(
                map = _emoteList.value.map + innerInlineContentMap
            )
            Log.d("getBetterTTVChannelEmotes", "DONE")

        }else{
            Log.d("getBetterTTVChannelEmotes", "FAILED")
            Log.d("getBetterTTVChannelEmotes", "code ->${response.code()}")
            Log.d("getBetterTTVChannelEmotes", "message ->${response.message()}")

        }
    }.catch { cause ->
        Log.d("getChannelEmotes","EXCEPTION error message ->${cause.message}")
        Log.d("getChannelEmotes","EXCEPTION error cause ->${cause.cause}")
        emit(Response.Failure(Exception("Unable to get emotes")))
    }

    override suspend fun getGlobalChatBadges(oAuthToken: String, clientId: String)= flow{
        emit(Response.Loading)
        val response = twitchEmoteClient.getGlobalChatBadges(
            authorization = "Bearer $oAuthToken",
            clientId = clientId
        )
        if(response.isSuccessful){
            val data = response.body()?.data


            val parsedEmoteData = data?.map{
                EmoteNameUrlEmoteType(
                    name = it.set_id,
                    url = it.versions[0].image_url_1x,
                    emoteType = EmoteTypes.FOLLOWERS
                )
            }?: listOf()

            val innerInlineContentMap: MutableMap<String, InlineTextContent> = mutableMapOf()
            parsedEmoteData.forEach {emoteValue -> // convert the parsed data into values that can be stored into _emoteList
                createChannelEmoteMapValue(emoteValue,innerInlineContentMap)
            }
            _emoteList.value = emoteList.value.copy(
                map = _emoteList.value.map + innerInlineContentMap
            )
            emit(Response.Success(true))
            Log.d("getGlobalChatBadges", "SUCCESS")
            Log.d("getGlobalChatBadges", "data ->${data}")
        }else{
            emit(Response.Success(false))
            Log.d("getGlobalChatBadges", "FAILED")
            Log.d("getGlobalChatBadges", "code ->${response.code()}")
            Log.d("getGlobalChatBadges", "message ->${response.message()}")
        }
    }.catch { cause ->
        Log.d("getGlobalChatBadges","EXCEPTION error message ->${cause.message}")
        Log.d("getGlobalChatBadges","EXCEPTION error cause ->${cause.cause}")
        emit(Response.Failure(Exception("Unable to get emotes")))
    }

}


/**
 * EmoteNameUrl represents a single Twitch Emote from the Twitch servers. Each instance of this class is a unique Emote
 *
 * @param name the name of the Twitch emote
 * @param url the url that is hosted on the twitch servers and is what we use to load the image
 * */
data class EmoteNameUrl(
    val name:String,
    val url:String,
)

/**
 * EmoteNameEmoteType represents a single Twitch Emote from the Twitch servers, when calling get channel emotes
 * - you can read more about getting channel emotes, [HERE](https://dev.twitch.tv/docs/api/reference/#get-channel-emotes)
 *
 * @param name the name of the Twitch emote
 * @param url the url that is hosted on the twitch servers and is what we use to load the image,
 * @param emoteType a [EmoteTypes] used to represent the type of emote that it is
 * */
data class EmoteNameUrlEmoteType(
    val name:String,
    val url:String,
    val emoteType:EmoteTypes
)

/**
 * EmoteTypes represents the two types of emotes, subscribers and followers
 * */
enum class EmoteTypes {
    SUBS, FOLLOWERS,
}

@Immutable
data class EmoteNameUrlList(
 val list:List<EmoteNameUrl> = listOf()
)

@Immutable
data class EmoteListMap(
    val map:Map<String, InlineTextContent>
)

@Immutable
data class EmoteNameUrlEmoteTypeList(
    val list:List<EmoteNameUrlEmoteType> = listOf()
)

/**
 * EmoteNameUrlNumberList
 * */
@Immutable
data class EmoteNameUrlNumberList(
    val list:List<EmoteNameUrlNumber> = listOf()
)

/**
 * class to show the list of individual channel emotes of BetterTTV
 * */
@Immutable
data class IndivBetterTTVEmoteList(
    val list: List<IndivBetterTTVEmote> = listOf()
)

/**
 * EmoteNameUrlNumber represents a single Twitch Emote from the Twitch servers and the number of times it was clicked.
 * This data class is used soley for the purpose of the most frequently clicked emotes
 *
 * @param name the name of the Twitch emote
 * @param url the url that is hosted on the twitch servers and is what we use to load the image
 * @param timesClicked the number of times this emote was clicked inside of the Twitch emote board
 * */
data class EmoteNameUrlNumber(
    val name:String,
    val url:String,
    val timesClicked:Int
)
