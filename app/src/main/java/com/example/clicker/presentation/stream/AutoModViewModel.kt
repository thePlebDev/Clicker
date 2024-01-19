package com.example.clicker.presentation.stream

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.domain.TwitchDataStore

import com.example.clicker.network.domain.TwitchStream
import com.example.clicker.network.models.twitchStream.IndividualAutoModSettings
import com.example.clicker.presentation.stream.views.TitleSubTitle
import com.example.clicker.util.Response
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * a class that is used by [updateSelectedIndex][AutoModViewModel.updateSelectedIndex] to represent which section of the
 * AutoMod settings are being changed
 * */
enum class FilterType {
    DISABILITY,
    SEXUALITY,
    SEXBASEDTERMS,
    MISOGYNY,
    RACE,
    AGGRESSION,
    BULLYING,
    SWEARING
}


data class AutoModCredentials(
    val oAuthToken:String = "",
    val broadcastId:String="",
    val clientId:String="",
    val moderatorId:String="",
    val isModerator:Boolean = true
)

data class AutoModUIState(
    val sliderValue:Float = 0.0.toFloat(),
    val filterText:String = "No filtering",
    val filterList:List<String> = listOf("No filtering", "Less filtering", "Some filtering", "More filtering", "Maximum filtering"),
    // PROFANITY
    val swearing:Int =0,
    //SEXUALITY
    val sexBasedTerms:Int =0,
    //HOSTILITY
    val aggression:Int =0,
    val bullying: Int =0,
    //DISCRIMINATION
    val disability:Int =0,
    val sexuality:Int =0,
    val misogyny:Int = 0,
    val race: Int = 0,



)

@HiltViewModel
class AutoModViewModel @Inject constructor(
    private val tokenDataStore: TwitchDataStore,
    private val twitchRepoImpl: TwitchStream,
    private val ioDispatcher: CoroutineDispatcher,
): ViewModel() {

    private val _autoModUIState: MutableState<AutoModUIState> = mutableStateOf(AutoModUIState())
    val autoModUIState: State<AutoModUIState> = _autoModUIState

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
            withContext(ioDispatcher){
                twitchRepoImpl.getAutoModSettings(
                    oAuthToken = oAuthToken,
                    clientId =clientId,
                    broadcasterId =broadcasterId,
                    moderatorId =moderatorId
                ).collect{response ->


                    when (response){
                        is Response.Loading ->{
                            Log.d("getAutoModStatus","LOADING")

                        }
                        is Response.Success ->{
                            val data = response.data.data[0]
                            val overallLevel = data.overallLevel?.toFloat() ?: 0f
                            Log.d("updateSliderValue","SliderValue Success-> $overallLevel")

                            updateSliderValue(overallLevel)
                            _autoModUIState.value = _autoModUIState.value.copy(
                                swearing = data.swearing,
                                aggression = data.aggression,
                                bullying = data.bullying,
                                sexBasedTerms = data.sexBasedTerms,
                                sexuality = data.sexualitySexOrGender,
                                race = data.raceEthnicityOrReligion,
                                disability = data.disability,
                                misogyny = data.misogyny,


                                )
                            _autoModCredentials.value = _autoModCredentials.value.copy(
                                isModerator = true
                            )

                        }
                        is Response.Failure ->{
                            _autoModCredentials.value = _autoModCredentials.value.copy(
                                isModerator = false
                            )

                        }
                    }
                }
            }

        }
    }
    fun updateAutoMod(){
        updateAutoModSettings(
            broadcastId =_autoModCredentials.value.broadcastId,
            moderatorId =_autoModCredentials.value.moderatorId,
            clientId =_autoModCredentials.value.clientId,
            oAuthToken =_autoModCredentials.value.oAuthToken,
        )
    }
    private fun updateAutoModSettings(
        broadcastId:String,
        moderatorId:String,
        clientId: String,
        oAuthToken: String
    ){
        viewModelScope.launch {

            val updatedIndividualAutoModSettings = IndividualAutoModSettings(
                broadcasterId = broadcastId,
                moderatorId = moderatorId,
                overallLevel=null,
                sexualitySexOrGender = _autoModUIState.value.sexuality,
                raceEthnicityOrReligion = _autoModUIState.value.race,
                sexBasedTerms = _autoModUIState.value.sexBasedTerms,
                disability = _autoModUIState.value.disability,
                aggression =_autoModUIState.value.aggression,
                misogyny = _autoModUIState.value.misogyny,
                bullying = _autoModUIState.value.bullying,
                swearing = _autoModUIState.value.swearing
            )

            withContext(ioDispatcher){
                twitchRepoImpl.updateAutoModSettings(
                    oAuthToken = oAuthToken,
                    clientId = clientId,
                    autoModSettings =updatedIndividualAutoModSettings
                ).collect{response ->
                    when(response){
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
    }

    fun updateSliderValue(currentValue:Float){
        Log.d("updateSliderValue","updateSliderValue() -> $currentValue")
        when(currentValue){
            0f ->{
                _autoModUIState.value = _autoModUIState.value.copy(
                    sliderValue = currentValue,
                    filterText = "AutoMod is off"
                )
                setAllIndexData(0)
            }
            1f ->{
                _autoModUIState.value = _autoModUIState.value.copy(
                    sliderValue = currentValue,
                    filterText ="A little moderation"
                )
                setAllIndexData(1)
            }

            2f ->{
                _autoModUIState.value = _autoModUIState.value.copy(
                    sliderValue = currentValue,
                    filterText ="Some moderation"
                )
                setAllIndexData(2)
            }

            3f ->{
                _autoModUIState.value = _autoModUIState.value.copy(
                    sliderValue = currentValue,
                    filterText ="More moderation"
                )
                setAllIndexData(3)
            }

            4f ->{
                _autoModUIState.value = _autoModUIState.value.copy(
                    sliderValue = currentValue,
                    filterText ="A lot of moderation"
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

        _autoModUIState.value = _autoModUIState.value.copy(
            disability = newIndex,
            sexuality = newIndex,
            misogyny= newIndex,
            race= newIndex,
        )
    }
    private fun setAllHostilityIndexData(newIndex:Int){
        _autoModUIState.value = _autoModUIState.value.copy(
            aggression = newIndex,
            bullying = newIndex
        )
    }
    private fun setAllSexualIndexData(newIndex: Int){
        _autoModUIState.value = _autoModUIState.value.copy(
            sexBasedTerms = newIndex
        )
    }
    private fun setAllProfanityIndexData(newIndex: Int){
        _autoModUIState.value = _autoModUIState.value.copy(
            swearing = newIndex
        )
    }
    fun updateSelectedIndex(newIndex: Int,filterType: FilterType){


        when(filterType){

            FilterType.DISABILITY ->{

                _autoModUIState.value = _autoModUIState.value.copy(
                    disability = newIndex,
                )
            }
            FilterType.MISOGYNY ->{
                _autoModUIState.value = _autoModUIState.value.copy(
                    misogyny= newIndex,
                )
            }
            FilterType.RACE ->{

                _autoModUIState.value = _autoModUIState.value.copy(
                    race= newIndex,
                )
            }
            FilterType.SEXUALITY ->{
                _autoModUIState.value = _autoModUIState.value.copy(
                    sexuality = newIndex,
                )
            }
            FilterType.AGGRESSION ->{

                _autoModUIState.value = _autoModUIState.value.copy(
                    aggression = newIndex
                )
            }
            FilterType.BULLYING ->{

                _autoModUIState.value = _autoModUIState.value.copy(
                    bullying = newIndex
                )
            }
            FilterType.SWEARING ->{
                _autoModUIState.value = _autoModUIState.value.copy(
                    swearing = newIndex
                )
            }
            FilterType.SEXBASEDTERMS ->{
                _autoModUIState.value = _autoModUIState.value.copy(
                    sexBasedTerms = newIndex
                )
            }

        }


    }


}