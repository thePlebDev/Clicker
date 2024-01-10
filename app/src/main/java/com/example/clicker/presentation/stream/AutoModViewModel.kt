package com.example.clicker.presentation.stream

import android.util.Log
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.domain.TwitchDataStore
import com.example.clicker.network.domain.TwitchStream
import com.example.clicker.presentation.stream.views.TitleSubTitle
import com.example.clicker.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * a class that is used by [updateSelectedIndex][AutoModViewModel.updateSelectedIndex] to represent which section of the
 * AutoMod settings are being changed
 * */
enum class FilterType {
    DISABILITY,
    SEXUALITY,
    MISOGYNY,
    RACE,
    AGGRESSION,
    BULLYING,
    SWEARING
}

/**
 *  A object that represents all the values of the Discrimination section
 * of AutoMod
 * */
data class DiscriminationIndexData(
    val disabilityIndex:Int =0,
    val sexualityIndex:Int =0,
    val misogynyIndex:Int = 0,
    val raceIndex: Int = 0,
)
/**
 *  A object that represents all the values of the Hostility section
 * of AutoMod
 * */
data class HostilityIndexData(
    val aggression:Int =0,
    val bullying:Int =0,
)
/**
 *  A object that represents all the values of the Sexual content section
 * of AutoMod
 * */
data class SexualIndexData(
    val sexBasedTerms:Int =0
)
/**
 *  A object that represents all the values of the Profanity section
 * of AutoMod
 * */
data class ProfanityIndexData(
    val swearing:Int =0
)

data class AutoModCredentials(
    val oAuthToken:String = "",
    val broadcastId:String="",
    val clientId:String="",
    val moderatorId:String="",
    val isModerator:Boolean = false
)

data class AutoModUIState(
    val sliderValue:Float = 0.0.toFloat(),
    val sliderText:String = "No filtering",
    val filterList:List<String> = listOf("No filtering", "Less filtering", "Some filtering", "More filtering", "Maximum filtering"),
    val discriminationList:List<TitleSubTitle> = listOf<TitleSubTitle>(
        TitleSubTitle("Disability","Demonstrating hatred or prejudice based on perceived or actual mental or physical abilities",0),
        TitleSubTitle("Sexuality, sex, or gender","Demonstrating hatred or prejudice based on sexual identity, sexual orientation, gender identity, or gender expression",0),
        TitleSubTitle("Misogyny","Demonstrating hatred or prejudice against women, including sexual objectification",0),
        TitleSubTitle("Race, ethnicity, or religion","Demonstrating hatred or prejudice based on race, ethnicity, or religion",0),
    ),

    val sexualFilterList:List<String> = listOf("No filtering", "Less filtering", "Some filtering", "More filtering", "Maximum filtering"),
    val sexualFilterIndex:Int=0,

    val profanityFilterList:List<String> = listOf("No filtering", "Less filtering", "Some filtering", "More filtering", "Maximum filtering"),
    val profanityFilterIndex:Int=0,

    val selectedIndex:Int =0,

)

@HiltViewModel
class AutoModViewModel @Inject constructor(
    private val tokenDataStore: TwitchDataStore,
    private val twitchRepoImpl: TwitchStream,
): ViewModel() {

    private val _autoModUIState: MutableState<AutoModUIState> = mutableStateOf(AutoModUIState())
    val autoModUIState: State<AutoModUIState> = _autoModUIState


    private val _discriminationIndexData = mutableStateOf(DiscriminationIndexData())
    val discriminationIndexData:State<DiscriminationIndexData> = _discriminationIndexData

    private val _hostilityIndexData = mutableStateOf(HostilityIndexData())
    val hostilityIndexData:State<HostilityIndexData> = _hostilityIndexData

    private val _sexBasedIndexData = mutableStateOf(SexualIndexData())
    val sexBasedIndexData:State<SexualIndexData> = _sexBasedIndexData

    private val _profanityIndexData = mutableStateOf(ProfanityIndexData())
    val profanityIndexData:State<ProfanityIndexData> = _profanityIndexData

    private val _autoModCredentials = mutableStateOf(AutoModCredentials())
     val autoModCredentials = _autoModCredentials

    init{
        fetOAuthToken()
    }

    private fun fetOAuthToken() {
        viewModelScope.launch {
            tokenDataStore.getOAuthToken().collect { oAuthToken ->
                Log.d("updateAutoModCredentials",oAuthToken)
                _autoModCredentials.value = _autoModCredentials.value.copy(
                    oAuthToken = oAuthToken
                )
            }
        }
    }

    fun updateAutoModCredentials(
        moderatorId: String,
        clientId: String,
        broadcasterId: String
    ){

        if(_autoModCredentials.value.oAuthToken.isNotEmpty()){
            _autoModCredentials.value = _autoModCredentials.value.copy(
                broadcastId =broadcasterId,
                moderatorId = moderatorId,
                clientId =  clientId

            )
            // make the request
            getAutoModStatus(
                oAuthToken = _autoModCredentials.value.oAuthToken,
                clientId =clientId,
                broadcasterId =broadcasterId,
                moderatorId = moderatorId
            )
        }



    }
    private fun getAutoModStatus(
        oAuthToken:String,
        clientId: String,
        broadcasterId: String,
        moderatorId:String
    ){
        viewModelScope.launch {
            twitchRepoImpl.getAutoModSettings(
                oAuthToken = oAuthToken,
                clientId =clientId,
                broadcasterId =broadcasterId,
                moderatorId =moderatorId
            ).collect{response ->

                
                when (response){
                    is Response.Loading ->{

                    }
                    is Response.Success ->{

                    }
                    is Response.Failure ->{

                    }
                }
            }
        }


    }

    fun updateSliderValue(currentValue:Float){
        when(currentValue){
            0.0.toFloat() ->{

                _autoModUIState.value = _autoModUIState.value.copy(
                    sliderText = "No filtering",
                    sliderValue = currentValue
                )
                setAllIndexData(0)
            }
            2.5.toFloat() ->{
                _autoModUIState.value = _autoModUIState.value.copy(
                    sliderText = "Less filtering",
                    sliderValue = currentValue
                )
                setAllIndexData(1)
            }
            5.0.toFloat() ->{

                _autoModUIState.value = _autoModUIState.value.copy(
                    sliderText = "Some filtering",
                    sliderValue = currentValue
                )
                setAllIndexData(2)
            }
            7.5.toFloat() ->{

                _autoModUIState.value = _autoModUIState.value.copy(
                    sliderText = "More filtering",
                    sliderValue = currentValue
                )
                setAllIndexData(3)
            }
            10.0.toFloat() ->{
                _autoModUIState.value = _autoModUIState.value.copy(
                    sliderText = "Max filtering",
                    sliderValue = currentValue
                )
                setAllIndexData(4)
            }
        }

    }
    private fun setAllIndexData(newIndex: Int){
        setAllDiscriminationIndexData(newIndex)
        setAllHostilityIndexData(newIndex)
        setAllSexualIndexData(newIndex)
        setAllProfanityIndexData(newIndex)
    }
    private fun setAllDiscriminationIndexData(newIndex:Int){
        _discriminationIndexData.value = _discriminationIndexData.value.copy(
            disabilityIndex = newIndex,
           sexualityIndex= newIndex,
          misogynyIndex= newIndex,
          raceIndex= newIndex,
        )
    }
    private fun setAllHostilityIndexData(newIndex:Int){
        _hostilityIndexData.value = _hostilityIndexData.value.copy(
            aggression = newIndex,
            bullying = newIndex
        )
    }
    private fun setAllSexualIndexData(newIndex: Int){
        _sexBasedIndexData.value = _sexBasedIndexData.value.copy(
            sexBasedTerms = newIndex
        )
    }
    private fun setAllProfanityIndexData(newIndex: Int){
        _profanityIndexData.value = _profanityIndexData.value.copy(
            swearing = newIndex
        )
    }
    fun updateSelectedIndex(newIndex: Int,filterType: FilterType){


        when(filterType){

            FilterType.DISABILITY ->{
                _discriminationIndexData.value = _discriminationIndexData.value.copy(
                    disabilityIndex = newIndex
                )
            }
            FilterType.MISOGYNY ->{
                _discriminationIndexData.value = _discriminationIndexData.value.copy(
                    misogynyIndex = newIndex
                )
            }
            FilterType.RACE ->{
                _discriminationIndexData.value = _discriminationIndexData.value.copy(
                    raceIndex = newIndex
                )
            }
            FilterType.SEXUALITY ->{
                _sexBasedIndexData.value = _sexBasedIndexData.value.copy(
                    sexBasedTerms = newIndex
                )
            }
            FilterType.AGGRESSION ->{
                _hostilityIndexData.value = _hostilityIndexData.value.copy(
                    aggression = newIndex
                )
            }
            FilterType.BULLYING ->{
                _hostilityIndexData.value = _hostilityIndexData.value.copy(
                    bullying = newIndex
                )
            }
            FilterType.SWEARING ->{
                _profanityIndexData.value = _profanityIndexData.value.copy(
                    swearing = newIndex
                )
            }
        }


    }


}