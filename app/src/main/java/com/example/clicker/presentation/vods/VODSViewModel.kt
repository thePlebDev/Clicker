package com.example.clicker.presentation.vods

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.domain.TwitchDataStore
import com.example.clicker.network.clients.VOD
import com.example.clicker.network.domain.TwitchEmoteRepo
import com.example.clicker.network.domain.TwitchSocket
import com.example.clicker.network.domain.TwitchStream
import com.example.clicker.network.domain.TwitchVODRepo
import com.example.clicker.network.repository.BetterTTVEmotesImpl
import com.example.clicker.presentation.stream.AutoCompleteChat
import com.example.clicker.presentation.stream.util.NetworkMonitoring
import com.example.clicker.presentation.stream.util.TextParsing
import com.example.clicker.presentation.stream.util.TokenCommand
import com.example.clicker.presentation.stream.util.TokenMonitoring
import com.example.clicker.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class VODViewModel @Inject constructor(
    private val twitchVODRepo: TwitchVODRepo
) : ViewModel() {

    var vodList = mutableStateListOf<VOD>()

    init{
        getVods()
    }


    fun getVods() = viewModelScope.launch{
        twitchVODRepo.getChannelVODs(
            oAuthToken="6scxh3bz6rbwgvkk82y4lubp8qu5a3",
            clientId = "xk7p10b4gwoacyi40rlktnxvyjn990",
            userId = "207813352"
        ).collect{response ->
            when(response){
                is Response.Loading->{

                }
                is Response.Success->{
                    vodList.addAll(response.data.data)

                }
                is Response.Failure->{

                }
            }

        }
    }

}

























