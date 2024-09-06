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

import com.example.clicker.network.clients.BetterTTVEmoteClient
import com.example.clicker.network.clients.ChannelEmote
import com.example.clicker.network.clients.TwitchEmoteClient
import com.example.clicker.network.domain.BetterTTVEmotes
import com.example.clicker.network.domain.TwitchEmoteRepo
import com.example.clicker.network.models.emotes.BetterTTVIndivChannelEmote
import com.example.clicker.network.models.emotes.BetterTTVChannelEmotes
import com.example.clicker.network.models.emotes.BetterTTVSharedEmote
import com.example.clicker.network.models.emotes.IndivBetterTTVEmote
import com.example.clicker.network.repository.models.EmoteListMap
import com.example.clicker.network.repository.models.EmoteNameUrl
import com.example.clicker.network.repository.models.EmoteNameUrlEmoteType
import com.example.clicker.network.repository.models.EmoteNameUrlEmoteTypeList
import com.example.clicker.network.repository.models.EmoteNameUrlList
import com.example.clicker.network.repository.models.EmoteTypes
import com.example.clicker.network.repository.models.IndivBetterTTVEmoteList
import com.example.clicker.network.repository.util.EmoteParsing
import com.example.clicker.network.repository.util.handleException
import com.example.clicker.presentation.stream.views.chat.chatSettings.ChatBadgePair
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TwitchEmoteImpl @Inject constructor(
    private val twitchEmoteClient: TwitchEmoteClient,
    private val betterTTVClient: BetterTTVEmoteClient,
    private val emoteParsing:EmoteParsing = EmoteParsing() //todo: this can be deleted

): TwitchEmoteRepo {

    // values are hard coded so if all requests fail, the user can still see who the mods and subs are
    private val modBadge = "https://static-cdn.jtvnw.net/badges/v1/3267646d-33f0-4b17-b3df-f923a41db1d0/1"
    private val subBadge = "https://static-cdn.jtvnw.net/badges/v1/5d9f2208-5dd8-11e7-8513-2ff4adfae661/1"
    private val feelsGood = "https://static-cdn.jtvnw.net/emoticons/v2/64138/static/light/1.0"
    private val feelsGoodId ="SeemsGood"
    //moderator subscriber
    private val modId = "moderator"
    private val subId = "subscriber"
    private val monitorId ="monitorIcon"
    private val badgeSize:Float =20f



    //todo: this needs to be moved to the badgeListMap
    /** - inlineContentMap represents the inlineConent for the sub,mod and SeemsGood icons.
     * This is created before the [getGlobalEmotes] method is called so that there can still be mod and sub icons as soon as the
     * user loads into chat
     * - This value is hardcoded, so that even if all the other requests fail, the user will still be able to see the sub and mod badges
     *
     * */
    private val inlineContentMap = mapOf(
        Pair( //todo: This one can stay

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
    /***********************END OF THE inlineContentMap****************************************/
    private val inlineContentMapGlobalBadgeList = mapOf(
        Pair( //todo: This should get moved

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

        )

//    private val _emoteList: MutableState<EmoteListMap> = mutableStateOf(EmoteListMap(inlineContentMap))
//    override val emoteList: State<EmoteListMap> = _emoteList //this is what is shown in the chat UI(not emote box UI but chat UI )

    /**
     * private mutable version of [globalChatBadges]
     * */
    private val _globalChatBadges: MutableState<EmoteListMap> = mutableStateOf(EmoteListMap(inlineContentMapGlobalBadgeList))
    override val globalChatBadges: State<EmoteListMap> = _globalChatBadges

    /**
     * private mutable version of [emoteBoardGlobalList]
     * */
    private val _emoteBoardGlobalList = mutableStateOf<EmoteNameUrlList>(EmoteNameUrlList())
    override val emoteBoardGlobalList:State<EmoteNameUrlList> = _emoteBoardGlobalList

    /**
     * private mutable version of [emoteBoardChannelList]
     * */
    private val _emoteBoardChannelList = mutableStateOf<EmoteNameUrlEmoteTypeList>(EmoteNameUrlEmoteTypeList())
    override val emoteBoardChannelList:State<EmoteNameUrlEmoteTypeList> = _emoteBoardChannelList

    /**
     * private mutable version of [globalBetterTTVEmotes]
     * */
    private val _globalBetterTTVEmotes = mutableStateOf<IndivBetterTTVEmoteList>(IndivBetterTTVEmoteList())
    override val globalBetterTTVEmotes:State<IndivBetterTTVEmoteList> = _globalBetterTTVEmotes

    /**
     * private mutable version of [channelBetterTTVEmotes]
     * */
    private val _channelBetterTTVEmotes = mutableStateOf<IndivBetterTTVEmoteList>(IndivBetterTTVEmoteList())
    override val channelBetterTTVEmotes:State<IndivBetterTTVEmoteList> = _channelBetterTTVEmotes

    /**
     * private mutable version of [sharedBetterTTVEmotes]
     * */
    private val _sharedBetterTTVEmotes = mutableStateOf<IndivBetterTTVEmoteList>(IndivBetterTTVEmoteList())
    override val sharedBetterTTVEmotes:State<IndivBetterTTVEmoteList> = _sharedBetterTTVEmotes



    /**
     * private mutable version of [globalTwitchEmoteList]
     * */
    private val _globalTwitchEmoteList = MutableStateFlow(listOf<EmoteNameUrl>())
    override val globalTwitchEmoteList: StateFlow<List<EmoteNameUrl>> = _globalTwitchEmoteList


    /**
     * private mutable version of [channelEmoteList]
     * */
    private val _channelEmoteList = MutableStateFlow(listOf<EmoteNameUrl>())
    override val channelEmoteList: StateFlow<List<EmoteNameUrl>> = _channelEmoteList

    private val _globalBetterTTVEmoteList = MutableStateFlow(listOf<EmoteNameUrl>())
    override val globalBetterTTVEmoteList: StateFlow<List<EmoteNameUrl>> = _globalBetterTTVEmoteList

    /**
     * private mutable version of [channelBetterTTVEmoteList]
     * */
    private val _channelBetterTTVEmoteList = MutableStateFlow(listOf<EmoteNameUrl>())
    /**
     * channelBetterTTVEmoteList represents the list of emotes shown to the user inside of the streamer's chat
     * and the clicked user messages. This is not responsible for showing the emotes inside of the user's emote box
     * */
    override val channelBetterTTVEmoteList: StateFlow<List<EmoteNameUrl>> = _channelBetterTTVEmoteList

    private val _sharedBetterTTVEmoteList = MutableStateFlow(listOf<EmoteNameUrl>())
    override val sharedBetterTTVEmoteList: StateFlow<List<EmoteNameUrl>> = _sharedBetterTTVEmoteList



    override fun getGlobalEmotes(
        oAuthToken: String,
        clientId: String,
    ): Flow<Response<Boolean>> = flow {
        emit(Response.Loading)
        Log.d("getGlobalEmotes","OAUTHTOKEN ->${oAuthToken}")
        Log.d("getGlobalEmotes","clientId ->${clientId}")

         val response = twitchEmoteClient.getGlobalEmotes(
             authorization = "Bearer $oAuthToken",
             clientId = clientId
         )

          if (response.isSuccessful) {
              Log.d("getGlobalEmotes","SUCCESS")
              val data = response.body()?.data ?: listOf()
              if(data.isNotEmpty()){
                  val parsedEmoteData = data.map {
                      EmoteNameUrl(it.name,it.images.url_1x)
                  }
                  _emoteBoardGlobalList.value = _emoteBoardGlobalList.value.copy(
                      list = parsedEmoteData
                  )
                  _globalTwitchEmoteList.tryEmit(parsedEmoteData)
              }

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
            val data = response.body()?.data?: listOf()
            emit(Response.Success(true))
            if(data.isNotEmpty()){
               // Log.d("CheckkinggetChannelEmotes","emotes ->${data}")

                val parsedEmoteData = convertDataToEmoteNameUrlEmoteType(data)
                val newChannelEmoteList = parsedEmoteData.map{

                    EmoteNameUrl(
                        name = it.name,
                        url = it.url
                    )
                }

                _channelEmoteList.tryEmit(newChannelEmoteList)


                val followerEmotes =parsedEmoteData.filter { it.emoteType == EmoteTypes.FOLLOWERS}
                val subscriberEmotes = parsedEmoteData.filter { it.emoteType == EmoteTypes.SUBS}
                val sortedEmoteData = followerEmotes + subscriberEmotes
                Log.d("CheckkinggetChannelEmotes","emotes ->${sortedEmoteData}")
                _emoteBoardChannelList.value = _emoteBoardChannelList.value.copy(
                    list = sortedEmoteData
                )

            }

            Log.d("getChannelEmotes","SUCCESS")
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
    private fun convertDataToEmoteNameUrlEmoteType(
        data: List<ChannelEmote>
    ): List<EmoteNameUrlEmoteType>{
       return data.map {// getting data from the request
            val emoteType = if(it.emote_type =="subscriptions") EmoteTypes.SUBS else EmoteTypes.FOLLOWERS
            EmoteNameUrlEmoteType(it.name,it.images.url_1x,emoteType)
        }

    }



    override suspend fun getBetterTTVGlobalEmotes()= flow{
        //1) get the emotes //2)update the _emoteList
        emit(Response.Loading)
        Log.d("getGlobalBetterTTVEmotes", "LOADING")
        val response = betterTTVClient.getGlobalEmotes()
        if (response.isSuccessful) {

            val data = response.body() ?: listOf()
            Log.d("getGlobalBetterTTVEmotes", "DATA ->${data}")
            if(data.isNotEmpty()){
                val parsedEmoteData = data.map { EmoteNameUrlEmoteType(
                    name = it.code,
                    url="https://cdn.betterttv.net/emote/${it.id}/1x",
                    emoteType = EmoteTypes.FOLLOWERS
                )}
                val globalBetterTTVEmoteList = parsedEmoteData.map{
                    EmoteNameUrl(
                        name = it.name,
                        url = it.url
                    )
                }
                _globalBetterTTVEmoteList.tryEmit(globalBetterTTVEmoteList ?: listOf())
                val innerInlineContentMap: MutableMap<String, InlineTextContent> = mutableMapOf()

//                _emoteList.value = emoteList.value.copy(
//                    map = _emoteList.value.map + innerInlineContentMap
//                )
                _globalBetterTTVEmotes.value = _globalBetterTTVEmotes.value.copy(
                    list = data
                )
            }




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

            //todo: same thing but for shared
            val channelBetterTTVEmoteList = channelEmotes?.map{
                EmoteNameUrl(
                    name = it.code,
                    url = "https://cdn.betterttv.net/emote/${it.id}/1x"
                )
            }
            val sharedBetterTTVEmoteList = sharedEmotes?.map{
                EmoteNameUrl(
                    name = it.code,
                    url = "https://cdn.betterttv.net/emote/${it.id}/1x"
                )
            }
            _channelBetterTTVEmoteList.tryEmit(channelBetterTTVEmoteList ?: listOf())
            _sharedBetterTTVEmoteList.tryEmit(sharedBetterTTVEmoteList ?: listOf())


            /**************/
            //"https://cdn.betterttv.net/emote/${it.id}/1x"
            /******below adds the emotes to the emote board*******/

            channelEmotes?.also{listOfChannelEmotes ->
                emitIndivBetterTTVChannelEmotes(listOfChannelEmotes)

            }
            /***************************************************************************************************************/

            sharedEmotes?.also{listOfChannelEmotes ->
                emitIndivBetterTTVSharedEmotes(listOfChannelEmotes)
            }

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
    /**
     * emitIndivBetterTTVChannelEmotes parses a list of [BetterTTVChannelEmote] objects and emits a
     * list shown to the user in the emote board
     * */
    private fun emitIndivBetterTTVChannelEmotes(listOfChannelEmotes: List<BetterTTVIndivChannelEmote>){
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

    }

    /**
     * emitIndivBetterTTVSharedEmotes parses a list of [BetterTTVSharedEmote] objects and emits a
     * list shown to the user in the emote board
     * */
    private fun emitIndivBetterTTVSharedEmotes(listOfChannelEmotes: List<BetterTTVSharedEmote>){
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
                ChatBadgePair(
                    id = it.set_id,
                    url = it.versions[0].image_url_1x,

                )
            }?: listOf()


            emit(Response.Success(parsedEmoteData))
            Log.d("getGlobalChatBadges", "SUCCESS")
            Log.d("getGlobalChatBadges", "data ->${data}")
        }else{
            emit(Response.Failure(Exception(Exception("Failed"))))
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


