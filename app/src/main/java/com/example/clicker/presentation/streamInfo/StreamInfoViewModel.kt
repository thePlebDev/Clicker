package com.example.clicker.presentation.streamInfo

import android.util.JsonToken
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.network.clients.Game
import com.example.clicker.network.domain.StreamInfoRepo
import com.example.clicker.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StreamInfoViewModel @Inject constructor(
    private val streamInfoRepo: StreamInfoRepo,
    private val ioDispatcher: CoroutineDispatcher,
): ViewModel()  {


    /**
     * private mutable version of [channelTitle]
     * */
    private val _userInfo: MutableState<UserInfo> = mutableStateOf(UserInfo("","",""))
    /**
     * a [State] String object used to represent the name of the stream
     * */
    val userInfo: State<UserInfo> = _userInfo

    /**
     * private mutable version of [channelTitle]
     * */
    private val _channelTitle: MutableState<String> = mutableStateOf("")
    /**
     * a [State] String object used to represent the name of the stream
     * */
    val channelTitle: State<String> = _channelTitle

    private val maxStreamTitleLength =140
    private val maxTagTitleLength = 25

    private val _maxLengthOfTitle: MutableState<Int> = mutableStateOf(maxStreamTitleLength)
    val maxLengthOfTitle: State<Int> = _maxLengthOfTitle



    private val _maxLengthOfTag: MutableState<Int> = mutableStateOf(maxTagTitleLength)
    val maxLengthOfTag: State<Int> = _maxLengthOfTag
    private val _tagTitle: MutableState<String> = mutableStateOf("")
    val tagTitle: State<String> = _tagTitle

     val tagList = mutableStateListOf<String>()

    private val _selectedStreamLanguage: MutableState<String?> = mutableStateOf(null)
    val selectedStreamLanguage: State<String?> = _selectedStreamLanguage

    fun changeSelectedStreamLanguage(newValue:String){
        _selectedStreamLanguage.value = newValue
    }

    private val _brandedContent: MutableState<Boolean> = mutableStateOf(false)
    val brandedContent: State<Boolean> = _brandedContent

    fun changeBrandedContent(newValue:Boolean){
        _brandedContent.value = newValue
    }





    /**
     * private mutable version of [clientId]
     * */
    private val _contentClassification: MutableState<ContentClassificationCheckBox> = mutableStateOf(ContentClassificationCheckBox())
    /**
     * a [State] nullable-String object used to hold the unique identifier of the Android application
     * */
    val contentClassification: State<ContentClassificationCheckBox> = _contentClassification

    /**
     * private mutable version of [gameCategoryResponse]
     * */
    private val _gameCategoryResponse: MutableState<Response<Game>> = mutableStateOf(Response.Loading)
    /**
     * a [State] nullable-[Game] object
     * */
    val gameCategoryResponse: State<Response<Game>> = _gameCategoryResponse





    fun changeChannelTitle(newValue:String){
            _channelTitle.value = newValue
            _maxLengthOfTitle.value = (maxStreamTitleLength - newValue.length)
    }

    fun changeTagTitle(newValue:String){
        _tagTitle.value = newValue
        _maxLengthOfTag.value = (maxTagTitleLength - newValue.length)
    }
    fun addToTagList(newTag:String){
        if(tagTitle.value.isNotEmpty()){
            tagList.add(newTag)
            _tagTitle.value =""
            _maxLengthOfTag.value = maxTagTitleLength
        }
    }
    fun removeTagFromList(oldTag: String){
        tagList.remove(oldTag)
    }

    fun changeContentClassification(newClassificationCheckBox: ContentClassificationCheckBox){
        _contentClassification.value = _contentClassification.value.copy(
            drugsIntoxication =newClassificationCheckBox.drugsIntoxication,
            significantProfanity =newClassificationCheckBox.significantProfanity,
            gambling=newClassificationCheckBox.gambling,
            sexualThemes=newClassificationCheckBox.sexualThemes,
            violentGraphic=newClassificationCheckBox.violentGraphic,
            matureRatedGame=newClassificationCheckBox.matureRatedGame
        )

    }

   private fun getGameInfo(
        authorizationToken: String,
        clientId: String,
        gameName: String,
        gameId:String,
    )=viewModelScope.launch(ioDispatcher){
        streamInfoRepo.getCategoryInformation(
            authorizationToken=authorizationToken,
            clientId=clientId,
            gameName = gameName,
            gameId = gameId
        ).collect{response ->
            when(response){
                is Response.Loading ->{
                    _gameCategoryResponse.value = Response.Loading
                }
                is Response.Success ->{
                    //todo: I need to filter out the list
                    if(response.data.isNotEmpty()){
                        _gameCategoryResponse.value = Response.Success(response.data[0])
                    }
                }
                is Response.Failure ->{
                    _gameCategoryResponse.value = Response.Failure(Exception("Failed to get category "))
                }
            }

        }
    }



    fun refreshStreamInfo(){
        getStreamInfo(
            authorizationToken= _userInfo.value.oAuthToken,
            clientId= _userInfo.value.clientId,
            broadcasterId= _userInfo.value.broadcasterId,
        )
    }

    fun getStreamInfo(
        authorizationToken: String,
        clientId: String,
        broadcasterId: String
    )=viewModelScope.launch(ioDispatcher){
        Log.d("getStreamInfoTesting","autorizationTOken ->$authorizationToken")
        Log.d("getStreamInfoTesting","clientId ->$clientId")
        Log.d("getStreamInfoTesting","broadcasterId ->$broadcasterId")
        streamInfoRepo.getChannelInformation(
            authorizationToken=authorizationToken,
            clientId=clientId,
            broadcasterId = broadcasterId
        ).collect{response ->
            when(val data =response){
                is Response.Loading ->{}
                is Response.Success ->{

                    //https://static-cdn.jtvnw.net/ttv-boxart/5718_IGDB-52x72.jpg
                    val channelInfo =data.data
                    _channelTitle.value = channelInfo.title
                    tagList.addAll(channelInfo.tags)
                    val gameId = channelInfo.game_id
                    val gameName = channelInfo.game_name //todo: I think I actually use this to get the category info

                    Log.d("getStreamInfoTesting","gameName ->$gameName")
                    //todo: we need to
                    Log.d("getStreamInfoTesting","game_id ->$gameId")
                    getGameInfo(
                        authorizationToken=authorizationToken,
                        clientId=clientId,
                        gameName = gameName,
                        gameId =gameId
                    )
                    //content classification
                    //branded content
                    val contentClassification = channelInfo.content_classification_labels
                    for (item in contentClassification){
                       when(item){
                           "DrugsIntoxication"->{
                               _contentClassification.value = _contentClassification.value.copy(
                                   drugsIntoxication = true
                               )
                           }
                           "Gambling"->{
                               _contentClassification.value = _contentClassification.value.copy(
                                   gambling = true
                               )
                           }
                           "ProfanityVulgarity"->{
                               _contentClassification.value = _contentClassification.value.copy(
                                   significantProfanity = true
                               )
                           }
                           "SexualThemes"->{
                               _contentClassification.value = _contentClassification.value.copy(
                                   sexualThemes = true
                               )
                           }
                           "ViolentGraphic"->{
                               _contentClassification.value = _contentClassification.value.copy(
                                   violentGraphic = true
                               )
                           }
                       }
                    }

                    val language = channelInfo.broadcaster_language
                    if(languageHashMap.containsKey(language)){
                        _selectedStreamLanguage.value = languageHashMap[language]
                    }else{
                        _selectedStreamLanguage.value = "Other"
                    }



                }
                is Response.Failure ->{}
            }

        }

    }

}
var languageHashMap:HashMap<String, String>
        = HashMap<String, String> ().apply {
            put("ar","Arabic")
    put("bg","Bulgarian")
    put("ca","Catalan")
    put("zh","Chinese")
    put("cs","Czech",)
    put("da","Danish")
    put("da","Danish")
    put("nl","Dutch")
    put("en","English")
    put("fi","Finnish")
    put("fr","French")
    put("de","German")
    put("el","Greek")
    put("hi","Hindi")
    put("hu","Hungarian")
    put("id","Indonesian")
    put("it","Italian")
    put("ja","Japanese")
    put("Ko","Korean")
    put("ms","Malay")
    put("no","Norwegian")
    put("pl","Polish")
    put("pt","Portuguese")
    put("ro","Romanian")
    put("ru","Russian")
    put("sk","Slovak")
    put("es","Spanish")
    put("sv","Swedish")
    put("tl","Tagalog")
    put("th","Thai")
    put("tr","Turkish")
    put("uk","Ukrainian")
    put("ase","American Sign Language")

}

data class ContentClassificationCheckBox(
    val drugsIntoxication:Boolean = false,
    val significantProfanity:Boolean= false,
    val gambling:Boolean= false,
    val sexualThemes:Boolean= false,
    val violentGraphic:Boolean= false,
    val matureRatedGame:Boolean= false,
)

data class UserInfo(
    val oAuthToken: String,
    val clientId: String,
    val broadcasterId: String
)