package com.example.clicker.presentation.streamIndo

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StreamInfoViewModel @Inject constructor(): ViewModel()  {


    /**
     * private mutable version of [clientId]
     * */
    private val _channelTitle: MutableState<String> = mutableStateOf("")
    /**
     * a [State] nullable-String object used to hold the unique identifier of the Android application
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

}

data class ContentClassificationCheckBox(
    val drugsIntoxication:Boolean = false,
    val significantProfanity:Boolean= false,
    val gambling:Boolean= false,
    val sexualThemes:Boolean= false,
    val violentGraphic:Boolean= false,
    val matureRatedGame:Boolean= false,
)