package com.example.clicker.presentation.stream

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.clicker.presentation.stream.views.TitleSubTitle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
enum class FilterType {
    HOSTILITY,
    DISCRIMINATION,
    SEXUAL,
    PROFANITY,
    DISABILITY,
    SEXUALITY,
    MISOGYNY,
    RACE
}
data class DiscriminationIndexData(
    val disabilityIndex:Int =0,
    val sexualityIndex:Int =1,
    val misogynyIndex:Int = 2,
    val raceIndex: Int = 3,
)


data class AutoModUIState(
    val sliderValue:Float = 0.0.toFloat(),
    val hostilityFilterList:List<String> = listOf("No filtering", "Less filtering", "Some filtering", "More filtering", "Maximum filtering"),
    val hostilityFilterIndex:Int =0,


    val discriminationFilterList:List<String> = listOf("No filtering", "Less filtering", "Some filtering", "More filtering", "Maximum filtering"),
    val discriminationFilterIndex:Int=0,
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

    val selectedIndex:Int =0
)

class AutoModViewModel: ViewModel() {

    private val _autoModUIState: MutableState<AutoModUIState> = mutableStateOf(AutoModUIState())
    val autoModUIState: State<AutoModUIState> = _autoModUIState

    private val _discriminationIndexData = mutableStateOf(DiscriminationIndexData())
    val discriminationIndexData:State<DiscriminationIndexData> = _discriminationIndexData

    fun updateSliderValue(currentValue:Float){
        _autoModUIState.value = _autoModUIState.value.copy(
            sliderValue = currentValue
        )
    }
    fun updateSelectedIndex(newIndex: Int,filterType: FilterType){


        when(filterType){
            FilterType.DISCRIMINATION ->{
                _autoModUIState.value = _autoModUIState.value.copy(
                    discriminationFilterIndex = newIndex
                )
            }
            FilterType.HOSTILITY ->{
                _autoModUIState.value = _autoModUIState.value.copy(
                    hostilityFilterIndex = newIndex
                )
            }
            FilterType.PROFANITY->{
                _autoModUIState.value = _autoModUIState.value.copy(
                    profanityFilterIndex = newIndex
                )
            }
            FilterType.SEXUAL ->{
                _autoModUIState.value = _autoModUIState.value.copy(
                    sexualFilterIndex = newIndex
                )
            }
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
                _discriminationIndexData.value = _discriminationIndexData.value.copy(
                    sexualityIndex = newIndex
                )
            }
        }


    }


}