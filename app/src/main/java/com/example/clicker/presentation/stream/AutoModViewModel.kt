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
import com.example.clicker.network.clients.ChannelInformation

import com.example.clicker.network.domain.TwitchStream
import com.example.clicker.network.models.twitchStream.IndividualAutoModSettings

import com.example.clicker.util.Response
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
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
    val updateAutoModSettingsStatus:Response<Boolean>? = null



)

@HiltViewModel
class AutoModViewModel @Inject constructor(
    private val twitchRepoImpl: TwitchStream,
    private val ioDispatcher: CoroutineDispatcher,
): ViewModel() {

    private val _autoModUIState: MutableState<AutoModUIState> = mutableStateOf(AutoModUIState())
    val autoModUIState: State<AutoModUIState> = _autoModUIState

    private val _autoModCredentials:MutableSharedFlow<AutoModCredentials?> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val _autoModCredentialsState = mutableStateOf(AutoModCredentials())

    private val _isModerator: MutableState<Response<Boolean>> = mutableStateOf(Response.Loading)
    val isModerator:State<Response<Boolean>> = _isModerator

    // Backing property to avoid state updates from other classes
    private val _horizontalOverlayIsVisible = MutableStateFlow(false)
    // The UI collects from this StateFlow to get its state updates
    private val _verticalOverlayIsVisible = MutableStateFlow(false)
     val verticalOverlayIsVisible = _verticalOverlayIsVisible


    var singleTapHideHorizontalVisibility={}


    init {
        monitorForNewAutoModCredentials()
    }

    init {
        viewModelScope.launch {
            _verticalOverlayIsVisible.collect{verticalOverlayIsVisible ->
                Log.d("_overlayIsVisible","_overlayIsVisible -->${verticalOverlayIsVisible}")
                if(verticalOverlayIsVisible){
                    delay(3000)
                    _verticalOverlayIsVisible.tryEmit(false)
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            _horizontalOverlayIsVisible.collect{horizontalOverlayIsVisible ->
                Log.d("_overlayIsVisible","_overlayIsVisible -->${_horizontalOverlayIsVisible.value}")
                if(horizontalOverlayIsVisible){
                    delay(3000)
                    singleTapHideHorizontalVisibility()
                    _horizontalOverlayIsVisible.tryEmit(false)
                }
            }
        }
    }
    // the horizontal overlay UI
    fun setHorizontalOverlayToVisible(){
        _horizontalOverlayIsVisible.tryEmit(true)
        Log.d("setOverlayToVisible","setOverlayToVisible -->${_horizontalOverlayIsVisible.value}")
    }
    fun setHorizontalOverlayToHidden(){
        _horizontalOverlayIsVisible.tryEmit(false)
        Log.d("setOverlayToHidden","setOverlayToVisible -->${_horizontalOverlayIsVisible.value}")
    }

    //the vertical overlay UI
    fun setVerticalOverlayToVisible(){
        _verticalOverlayIsVisible.tryEmit(true)
        Log.d("setOverlayToVisible","setOverlayToVisible -->${_verticalOverlayIsVisible.value}")
    }
    fun setVerticalOverlayToHidden(){
        _verticalOverlayIsVisible.tryEmit(false)
        Log.d("setOverlayToHidden","setOverlayToVisible -->${_verticalOverlayIsVisible.value}")
    }


    fun updateAutoModCredentials(
        moderatorId: String,
        clientId: String,
        broadcasterId: String,
        oAuthToken: String
    ){
        viewModelScope.launch {
            val newAutoModCredentials = AutoModCredentials(oAuthToken,broadcasterId,clientId,moderatorId)
            _autoModCredentials.emit(newAutoModCredentials)
        }

    }

    private fun monitorForNewAutoModCredentials(){
        viewModelScope.launch {
            _autoModCredentials.collect{nullableCredentials ->
                nullableCredentials?.let{nonNullCredentials ->
                    Log.d("updateAutoModCredentials","new credentials ->$nonNullCredentials")
                    _autoModCredentialsState.value = AutoModCredentials(
                        oAuthToken = nonNullCredentials.oAuthToken,
                        broadcastId = nonNullCredentials.broadcastId,
                        clientId = nonNullCredentials.clientId,
                        moderatorId = nonNullCredentials.moderatorId
                    )
                    getAutoModStatus(
                        oAuthToken=nonNullCredentials.oAuthToken,
                        clientId=nonNullCredentials.clientId,
                        broadcasterId=nonNullCredentials.broadcastId,
                        moderatorId=nonNullCredentials.moderatorId
                    )
                }
            }
        }
    }
    fun updateAutoModSettingsStatusToNull(){
        _autoModUIState.value = _autoModUIState.value.copy(
            updateAutoModSettingsStatus = null
        )
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
                  //  Log.d("Auto")


                    when (response){
                        is Response.Loading ->{
                            Log.d("getAutoModStatus","LOADING")
                            _isModerator.value = Response.Loading
                            _autoModUIState.value = _autoModUIState.value.copy(
                                updateAutoModSettingsStatus = Response.Loading
                            )

                        }
                        is Response.Success ->{
                            val data = response.data.data[0]
                            val overallLevel = data.overallLevel?.toFloat() ?: 0f
                            Log.d("getAutoModStatus","SliderValue Success-> $overallLevel")

                            updateSliderValue(overallLevel)
                            _isModerator.value = Response.Success(true)
                            _autoModUIState.value = _autoModUIState.value.copy(
                                updateAutoModSettingsStatus = null
                            )
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


                        }
                        is Response.Failure ->{
                            _isModerator.value = Response.Failure(Exception("You are not moderator"))
                            _autoModUIState.value = _autoModUIState.value.copy(
                                updateAutoModSettingsStatus = Response.Failure(Exception("You are not moderator"))
                            )
                            Log.d("getAutoModStatus","RESPONSE --> FAILED")

                        }
                    }
                }
            }

        }
    }
    fun updateAutoMod(){
        updateAutoModSettings(
            broadcastId =_autoModCredentialsState.value.broadcastId,
            moderatorId =_autoModCredentialsState.value.moderatorId,
            clientId =_autoModCredentialsState.value.clientId,
            oAuthToken =_autoModCredentialsState.value.oAuthToken,
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
                            _isModerator.value = Response.Loading
                            _autoModUIState.value = _autoModUIState.value.copy(
                                updateAutoModSettingsStatus = Response.Loading
                            )

                        }
                        is Response.Success ->{
                            _isModerator.value = Response.Success(true)
                            _autoModUIState.value = _autoModUIState.value.copy(
                                updateAutoModSettingsStatus = Response.Success(true)
                            )
                            delay(2000)
                            _autoModUIState.value = _autoModUIState.value.copy(
                                updateAutoModSettingsStatus = null
                            )

                        }
                        is Response.Failure ->{
                            _isModerator.value = Response.Failure(Exception("Attempt failed"))
                            _autoModUIState.value = _autoModUIState.value.copy(
                                updateAutoModSettingsStatus = Response.Failure(Exception("Attempt failed"))
                            )
                            delay(2000)
                            _autoModUIState.value = _autoModUIState.value.copy(
                                updateAutoModSettingsStatus = null
                            )
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