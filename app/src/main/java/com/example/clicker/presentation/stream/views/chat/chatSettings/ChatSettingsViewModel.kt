package com.example.clicker.presentation.stream.views.chat.chatSettings

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.clicker.R
import com.example.clicker.domain.ChatSettingsDataStore
import com.example.clicker.domain.TwitchDataStore
import com.example.clicker.network.domain.TwitchEmoteRepo
import com.example.clicker.network.repository.EmoteListMap
import com.example.clicker.network.repository.EmoteNameUrl
import com.example.clicker.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.LinkedList
import java.util.Queue
import javax.inject.Inject

data class ChatBadgePair(
    val url:String,
    val id:String
)
@HiltViewModel
class ChatSettingsViewModel @Inject constructor(
    private val twitchEmoteImpl: TwitchEmoteRepo,
    private val chatSettingsDataStore: ChatSettingsDataStore,
    @ApplicationContext private val context: Context
): ViewModel() {

    //todo: Make a request to get all the global chat badges


/************************ ALL THE CHAT SIZE RELATED THINGS*******************************/
    private val _badgeSize = mutableStateOf(20f)  // Initial value
    val badgeSize: State<Float> = _badgeSize
    //todo:this is going to be saved for last
    private val _emoteSize = mutableStateOf(35f)  // Initial value
    val emoteSize: State<Float> = _emoteSize
    //todo:1) DONE
    private val _usernameSize = mutableStateOf(15f)  // Initial value
    val usernameSize: State<Float> = _usernameSize
    //todo:2) DONE
    private val _messageSize = mutableStateOf(15f)  // Initial value
    val messageSize: State<Float> = _messageSize
    //todo:3) DONE
    private val _lineHeight= mutableStateOf((15f *1.6f))  // Initial value
    val lineHeight: State<Float> = _lineHeight
    //todo:4)
    //THIS NEEDS TO BE STORED LOCALLY WHEN I GET BACK!!!!!!
    private val _customUsernameColor= mutableStateOf(true)  // Initial value
    val customUsernameColor: State<Boolean> = _customUsernameColor





    val chatBadgeList =   mutableStateListOf<ChatBadgePair>(
        // HARD CODED SO EVEN IF REQUEST TO GET BADGES FAILS, USER CAN STILL SEE SUBS AND MODS
        ChatBadgePair(
            url ="https://static-cdn.jtvnw.net/badges/v1/5d9f2208-5dd8-11e7-8513-2ff4adfae661/1",
            id="subscriber"
        ),
        ChatBadgePair(
            url ="https://static-cdn.jtvnw.net/badges/v1/3267646d-33f0-4b17-b3df-f923a41db1d0/1",
            id="moderator"
        )
    )
    //todo: add global emotes(DONE)
    private val globalEmoteList =  mutableListOf<EmoteNameUrl>()
    //todo: add channel emotes(DONE)
    private val channelEmoteList =  mutableListOf<EmoteNameUrl>()
    //todo: add global betterTTVEmotes(DONE)
    private val globalBetterTTVEmoteList =  mutableListOf<EmoteNameUrl>()
    //todo: add channel betterTTVEmotes(DONE)
    val channelBetterTTVEmoteList =  mutableListOf<EmoteNameUrl>()
    //todo: add shared betterTTVEmotes
    val sharedBetterTTVEmoteList =  mutableListOf<EmoteNameUrl>()

    /***********EMOTE AND BADGE RELATED THINGS***************/
    private val inlineContentMapGlobalBadgeList = mutableStateOf( EmoteListMap(createNewBadgeMap()))
    val globalChatBadgesMap: State<EmoteListMap> =inlineContentMapGlobalBadgeList

    private val _globalEmoteInlineContentMap = mutableStateOf( EmoteListMap(mapOf()))
    val globalEmoteMap: State<EmoteListMap> =_globalEmoteInlineContentMap

    private val _channelEmoteInlineContentMap= mutableStateOf( EmoteListMap(mapOf()))
    val inlineContentMapChannelEmoteList: State<EmoteListMap> =_channelEmoteInlineContentMap

    private val _betterTTVGlobalInlineContentMapChannelEmoteList = mutableStateOf( EmoteListMap(mapOf()))
    val betterTTVGlobalInlineContentMapChannelEmoteList: State<EmoteListMap> =_betterTTVGlobalInlineContentMapChannelEmoteList

    private val _betterTTVChannelInlineContentMapChannelEmoteList = mutableStateOf( EmoteListMap(mapOf()))
    val betterTTVChannelInlineContentMapChannelEmoteList: State<EmoteListMap> =_betterTTVChannelInlineContentMapChannelEmoteList

    private val _betterTTVSharedInlineContentMapChannelEmoteList = mutableStateOf( EmoteListMap(mapOf()))
    val betterTTVSharedInlineContentMapChannelEmoteList: State<EmoteListMap> =_betterTTVSharedInlineContentMapChannelEmoteList
    init{
        getStoredBadgeSize()
        getUsernameSize()
        getMessageSize()
        getLineHeight()
        getShowCustomUsernameColor()
        getEmoteSize()
    }
    init {

        monitorForChannelTwitchEmotes()
    }
    init {
        monitorForGlobalTwitchEmotes()
    }
    init{
        monitorForGlobalBetterTTVEmotes()
    }
    init{
        monitorForChannelBetterTTVEmotes()
    }
    init{
        monitorForSharedBetterTTVEmotes()
    }

    private fun monitorForSharedBetterTTVEmotes(){

        viewModelScope.launch {
            twitchEmoteImpl.sharedBetterTTVEmoteList.collect{response ->
                if (sharedBetterTTVEmoteList.isEmpty()){
                    sharedBetterTTVEmoteList.addAll(response)
                    //the map creator needs to be changed
                    _betterTTVSharedInlineContentMapChannelEmoteList.value = EmoteListMap(createBetterTTVSharedEmoteMap())
                }
                Log.d("sharedBetterTTVEmoteList","response ->${response}")
            }
        }
    }
    private fun monitorForChannelBetterTTVEmotes(){
        viewModelScope.launch {
            twitchEmoteImpl.channelBetterTTVEmoteList.collect{response ->
                if (channelBetterTTVEmoteList.isEmpty()){
                    channelBetterTTVEmoteList.addAll(response)
                    _betterTTVChannelInlineContentMapChannelEmoteList.value = EmoteListMap(createBetterTTVChanelEmoteMap())
                }

                Log.d("globalBetterTTVEmoteList","response ->${response}")
            }
        }
    }
    private fun monitorForGlobalBetterTTVEmotes(){
        viewModelScope.launch {
            twitchEmoteImpl.globalBetterTTVEmoteList.collect{response ->
                if (globalBetterTTVEmoteList.isEmpty()){
                    globalBetterTTVEmoteList.addAll(response)
                    _betterTTVGlobalInlineContentMapChannelEmoteList.value = EmoteListMap(createBetterTTVGlobalEmoteMap())
                }

                Log.d("globalBetterTTVEmoteList","response ->${response}")
            }
        }
    }
    private fun monitorForGlobalTwitchEmotes(){
        viewModelScope.launch {
            twitchEmoteImpl.globalTwitchEmoteList.collect{response ->
                if (globalEmoteList.isEmpty()){
                    globalEmoteList.addAll(response)
                    _globalEmoteInlineContentMap.value = EmoteListMap(createNewGlobalEmoteMap())
                }

                Log.d("channelEmoteList","response ->${response}")
            }
        }
    }
    private fun monitorForChannelTwitchEmotes(){
        viewModelScope.launch {
            twitchEmoteImpl.channelEmoteList.collect{response ->

                if (channelEmoteList.isEmpty()){
                    channelEmoteList.addAll(response)
                    _channelEmoteInlineContentMap.value = EmoteListMap(createNewChannelEmoteMap())
                }

                Log.d("combinedEmoteListing","response ->${response}")
            }
        }
    }
    private fun getStoredBadgeSize(){
        viewModelScope.launch {
            chatSettingsDataStore.getBadgeSize().collect{storedBadgeSize ->
                _badgeSize.value = storedBadgeSize
            }
            createNewBadgeMap()
        }
    }
    fun changeBadgeSize(newValue:Float){

        _badgeSize.value = newValue
        inlineContentMapGlobalBadgeList.value = EmoteListMap(createNewBadgeMap())
        storeBadgeSizeLocally(newValue)

    }
    private fun storeBadgeSizeLocally(newValue: Float)=viewModelScope.launch(Dispatchers.IO){
        chatSettingsDataStore.setBadgeSize(newValue)
    }




    private fun createNewBadgeMap():Map<String, InlineTextContent>{

        val newMap = chatBadgeList.map {chatBadgeValue ->
            Pair(
                chatBadgeValue.id,
                InlineTextContent(

                    Placeholder(
                        width = _badgeSize.value.sp,
                        height = _badgeSize.value.sp,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                    )
                ) {
                    AsyncImage(
                        model =chatBadgeValue.url ,
                        contentDescription = "${chatBadgeValue.id} badge",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp)
                    )
                }
            )
        }.toMap()

        return newMap

    }
    private fun createNewGlobalEmoteMap():Map<String, InlineTextContent>{
        val imageLoader = ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()

        val newMap = globalEmoteList.map {emote ->
            Pair(
                emote.name,
                InlineTextContent(

                    Placeholder(
                        width = _emoteSize.value.sp,
                        height = _emoteSize.value.sp,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                    )
                ) {
                    AsyncImage(
                        model =emote.url ,
                        contentDescription = "${emote.name} badge",
                        imageLoader = imageLoader,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp)
                    )
                }
            )

        }.toMap()

        return newMap

    }
    private fun createNewChannelEmoteMap():Map<String, InlineTextContent>{
        val imageLoader = ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()

        val newMap = channelEmoteList.map {emote ->
            Pair(
                emote.name,
                InlineTextContent(

                    Placeholder(
                        width = _emoteSize.value.sp,
                        height = _emoteSize.value.sp,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                    )
                ) {
                    AsyncImage(
                        model =emote.url ,
                        contentDescription = "${emote.name} badge",
                        imageLoader = imageLoader,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp)
                    )
                }
            )

        }.toMap()

        return newMap

    }
    private fun createBetterTTVGlobalEmoteMap():Map<String, InlineTextContent>{
        val imageLoader = ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()

        val newMap = globalBetterTTVEmoteList.map {emote ->
            Pair(
                emote.name,
                InlineTextContent(

                    Placeholder(
                        width = _emoteSize.value.sp,
                        height = _emoteSize.value.sp,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                    )
                ) {
                    AsyncImage(
                        model =emote.url ,
                        contentDescription = "${emote.name} badge",
                        imageLoader = imageLoader,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp)
                    )
                }
            )

        }.toMap()

        return newMap

    }
    private fun createBetterTTVChanelEmoteMap():Map<String, InlineTextContent>{
        val imageLoader = ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()

        val newMap = channelBetterTTVEmoteList.map {emote ->
            Pair(
                emote.name,
                InlineTextContent(

                    Placeholder(
                        width = _emoteSize.value.sp,
                        height = _emoteSize.value.sp,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                    )
                ) {
                    AsyncImage(
                        model =emote.url ,
                        contentDescription = "${emote.name} badge",
                        imageLoader = imageLoader,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp)
                    )
                }
            )

        }.toMap()

        return newMap

    }
    private fun createBetterTTVSharedEmoteMap():Map<String, InlineTextContent>{
        val imageLoader = ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()

        val newMap = sharedBetterTTVEmoteList.map {emote ->
            Pair(
                emote.name,
                InlineTextContent(

                    Placeholder(
                        width = _emoteSize.value.sp,
                        height = _emoteSize.value.sp,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                    )
                ) {
                    AsyncImage(
                        model =emote.url ,
                        contentDescription = "${emote.name} badge",
                        imageLoader = imageLoader,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp)
                    )
                }
            )

        }.toMap()

        return newMap

    }


    fun changeEmoteSize(newValue:Float){
        _emoteSize.value = newValue
        _globalEmoteInlineContentMap.value = EmoteListMap(createNewGlobalEmoteMap())
        _channelEmoteInlineContentMap.value = EmoteListMap(createNewChannelEmoteMap())
        _betterTTVGlobalInlineContentMapChannelEmoteList.value = EmoteListMap(createBetterTTVGlobalEmoteMap())
        _betterTTVChannelInlineContentMapChannelEmoteList.value = EmoteListMap(createBetterTTVChanelEmoteMap())
        _betterTTVSharedInlineContentMapChannelEmoteList.value = EmoteListMap(createBetterTTVSharedEmoteMap())
        storeEmoteSizeLocally(newValue)



    }
    private fun getEmoteSize()=viewModelScope.launch{
        chatSettingsDataStore.getEmoteSize().collect{storedEmoteSize ->
            _emoteSize.value = storedEmoteSize
        }
    }
    private fun storeEmoteSizeLocally(newValue: Float)=viewModelScope.launch(Dispatchers.IO){
        chatSettingsDataStore.setEmoteSize(newValue)
    }

    fun changeUsernameSize(newValue:Float){
        _usernameSize.value = newValue
        storeUsernameSizeLocally(newValue)
    }
    private fun getUsernameSize()=viewModelScope.launch{
        chatSettingsDataStore.getUsernameSize().collect{storedUsernameSize ->
            _usernameSize.value = storedUsernameSize
        }
    }
    private fun storeUsernameSizeLocally(newValue: Float)=viewModelScope.launch(Dispatchers.IO){
        chatSettingsDataStore.setUsernameSize(newValue)
    }
    fun changeMessageSize(newValue:Float){
        _messageSize.value = newValue
        storeMessageSizeLocally(newValue)
    }
    private fun getMessageSize()=viewModelScope.launch{
        chatSettingsDataStore.getMessageSize().collect{storedMessageSize ->
            _messageSize.value = storedMessageSize
        }
    }
    private fun storeMessageSizeLocally(newValue: Float)=viewModelScope.launch(Dispatchers.IO){
        chatSettingsDataStore.setMessageSize(newValue)
    }
    fun changeLineHeight(newValue:Float){
        _lineHeight.value = newValue
        storeLineHeightLocally(newValue)
    }
    private fun getLineHeight()=viewModelScope.launch{
        chatSettingsDataStore.getLineHeight().collect{storedLineHeight ->
            _lineHeight.value = storedLineHeight
        }
    }
    private fun storeLineHeightLocally(newValue: Float)=viewModelScope.launch(Dispatchers.IO){
        chatSettingsDataStore.setLineHeight(newValue)
    }

    fun changeCustomUsernameColor(newValue:Boolean){
        _customUsernameColor.value = newValue
        storeCustomUsernameLocally(newValue)
    }
    private fun getShowCustomUsernameColor()=viewModelScope.launch{
        chatSettingsDataStore.getCustomUsernameColor().collect{showCustomUsernameColor ->
            _customUsernameColor.value = showCustomUsernameColor
        }
    }
    private fun storeCustomUsernameLocally(newValue: Boolean)=viewModelScope.launch(Dispatchers.IO){
        chatSettingsDataStore.setCustomUsernameColor(newValue)
    }

    /************************ ALL THE CHAT SIZE RELATED THINGS END*******************************/





    fun getGlobalChatBadges(
        oAuthToken: String,
        clientId: String,
    )= viewModelScope.launch(Dispatchers.IO){

        if(chatBadgeList.toList().size==2){ //this being true indicates that there has been no call to
            twitchEmoteImpl.getGlobalChatBadges(
                oAuthToken,clientId
            ).collect{response->

                when(response){
                    is Response.Loading->{}
                    is Response.Success->{
                        if(response.data.isNotEmpty()){
                            chatBadgeList.clear()
                            chatBadgeList.addAll(response.data)
                            inlineContentMapGlobalBadgeList.value = EmoteListMap(createNewBadgeMap())

                        }

                    }
                    is Response.Failure->{}
                }

            }
        }

        }


}
