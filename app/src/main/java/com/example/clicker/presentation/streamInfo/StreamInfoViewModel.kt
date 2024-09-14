package com.example.clicker.presentation.streamInfo

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.network.domain.StreamInfoRepo
import com.example.clicker.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StreamInfoViewModel @Inject constructor(
    private val streamInfoRepo: StreamInfoRepo
): ViewModel()  {


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

    fun getStreamInfo(
        authorizationToken: String,
        clientId: String,
        broadcasterId: String
    )=viewModelScope.launch(Dispatchers.IO){
        streamInfoRepo.getChannelInformation(
            authorizationToken=authorizationToken,
            clientId=clientId,
            broadcasterId = broadcasterId
        ).collect{response ->
            when(val data =response){
                is Response.Loading ->{}
                is Response.Success ->{

                    val channelInfo =data.data
                    _channelTitle.value = channelInfo.title
                    tagList.addAll(channelInfo.tags)
                    val channelId = channelInfo.game_id // use this to get the category
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