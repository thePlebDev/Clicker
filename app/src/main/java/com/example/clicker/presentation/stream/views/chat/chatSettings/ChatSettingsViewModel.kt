package com.example.clicker.presentation.stream.views.chat.chatSettings

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.example.clicker.R
import com.example.clicker.domain.ChatSettingsDataStore
import com.example.clicker.domain.TwitchDataStore
import com.example.clicker.network.domain.TwitchEmoteRepo
import com.example.clicker.network.repository.EmoteListMap
import com.example.clicker.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatBadgePair(
    val url:String,
    val id:String
)
@HiltViewModel
class ChatSettingsViewModel @Inject constructor(
    private val twitchEmoteImpl: TwitchEmoteRepo,
    private val chatSettingsDataStore: ChatSettingsDataStore,
): ViewModel() {

    //todo: Make a request to get all the global chat badges


/************************ ALL THE CHAT SIZE RELATED THINGS*******************************/
    private val _badgeSize = mutableStateOf(20f)  // Initial value
    val badgeSize: State<Float> = _badgeSize
    private val _emoteSize = mutableStateOf(35f)  // Initial value
    val emoteSize: State<Float> = _emoteSize
    private val _usernameSize = mutableStateOf(15f)  // Initial value
    val usernameSize: State<Float> = _usernameSize
    private val _messageSize = mutableStateOf(15f)  // Initial value
    val messageSize: State<Float> = _messageSize
    private val _lineHeight= mutableStateOf((15f *1.6f))  // Initial value
    val lineHeight: State<Float> = _lineHeight
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
    init{
        getStoredBadgeSize()
    }
    private fun getStoredBadgeSize(){
        viewModelScope.launch {
            chatSettingsDataStore.getBadgeSize().collect{storedBadgeSize ->
                _badgeSize.value = storedBadgeSize
            }
            createNewMap()
        }
    }
    fun changeBadgeSize(newValue:Float){

        _badgeSize.value = newValue
        inlineContentMapGlobalBadgeList.value = EmoteListMap(createNewMap())
        storeBadgeSizeLocally(newValue)


    }
    private fun storeBadgeSizeLocally(newValue: Float)=viewModelScope.launch(Dispatchers.IO){
        chatSettingsDataStore.setBadgeSize(newValue)
    }

    private fun createNewMap():Map<String, InlineTextContent>{

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

    fun changeEmoteSize(newValue:Float){
        _emoteSize.value = newValue

    }
    fun changeUsernameSize(newValue:Float){
        _usernameSize.value = newValue
    }
    fun changeMessageSize(newValue:Float){
        _messageSize.value = newValue
    }
    fun changeLineHeight(newValue:Float){
        _lineHeight.value = newValue
    }
    fun changeCustomUsernameColor(newValue:Boolean){
        _customUsernameColor.value = newValue
    }

    /************************ ALL THE CHAT SIZE RELATED THINGS END*******************************/



     private val inlineContentMapGlobalBadgeList = mutableStateOf( EmoteListMap(createNewMap()))
     val globalChatBadgesMap: State<EmoteListMap> =inlineContentMapGlobalBadgeList

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
                            inlineContentMapGlobalBadgeList.value = EmoteListMap(createNewMap())

                        }

                    }
                    is Response.Failure->{}
                }

            }
        }

        }


}
