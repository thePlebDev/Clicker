package com.example.clicker.presentation.stream.views.streamManager

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp

import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.network.websockets.AutoModQueueMessage
import com.example.clicker.presentation.modView.ModViewDragStateViewModel
import com.example.clicker.presentation.modView.ModViewViewModel
import com.example.clicker.presentation.stream.ClickedUIState
import com.example.clicker.presentation.stream.FilterType
import com.example.clicker.presentation.stream.views.AutoMod
import com.example.clicker.presentation.modView.views.ModViewDragSection
import com.example.clicker.util.Response
import kotlinx.coroutines.launch


@Composable
fun ManageStreamInformation(
    closeStreamInfo:()->Unit,
    streamTitle:String,
    streamCategory:String,
    updateStreamTitle:(String)->Unit,
    showAutoModSettings:Boolean,
    changeSelectedIndex:(Int, FilterType)->Unit,

    swearingIndex:Int,
    sexBasedTermsIndex:Int,
    aggressionIndex:Int,
    bullyingIndex:Int,
    disabilityIndex:Int,
    sexualityIndex:Int,
    misogynyIndex:Int,
    raceIndex:Int,
    sliderPosition: Float,
    changSliderPosition:(Float)->Unit,
    filterText:String,
    isModerator: Response<Boolean>,
    updateAutoModSettings:()->Unit,
    updateAutoModSettingsStatus:Response<Boolean>?,
    updateAutoModSettingsStatusToNull:()->Unit,
    updateChannelInfo:()->Unit,
    modViewDragStateViewModel: ModViewDragStateViewModel,
    chatMessages: List<TwitchUserData>,
    clickedUserData: ClickedUIState,
    clickedUserChatMessages:List<String>,
    updateClickedUser: (String, String, Boolean, Boolean) -> Unit,
    timeoutDuration:Int,
    changeTimeoutDuration:(Int)->Unit,
    timeoutReason: String,
    changeTimeoutReason: (String) -> Unit,
    banDuration:Int,
    changeBanDuration:(Int)->Unit,
    banReason:String,
    changeBanReason: (String) -> Unit,

    loggedInUserIsMod:Boolean,
    clickedUserIsMod:Boolean,
    timeoutUser:()->Unit,
    showTimeoutErrorMessage:Boolean,
    setTimeoutShowErrorMessage:(Boolean)->Unit,
    showBanErrorMessage:Boolean,
    setBanShowErrorMessage:(Boolean)->Unit,
    banUser:()->Unit,
    modActionList: List<TwitchUserData>,
    autoModMessageList:List<AutoModQueueMessage>,
    manageAutoModMessage:(String,String,String)-> Unit,
    connectionError: Response<Boolean>,
    reconnect:()->Unit

){
    if(showAutoModSettings){

        EditAutoModSettingsScaffold(
            closeStreamInfo={closeStreamInfo()},
            changeSelectedIndex = {item,filterType ->changeSelectedIndex(item,filterType)},
            swearingIndex = swearingIndex,
            sexBasedTermsIndex = sexBasedTermsIndex,
            aggressionIndex = aggressionIndex,
            bullyingIndex = bullyingIndex,
            disabilityIndex = disabilityIndex,
            sexualityIndex = sexualityIndex,
            misogynyIndex = misogynyIndex,
            raceIndex = raceIndex,
            sliderPosition =sliderPosition,
            changSliderPosition = {float -> changSliderPosition(float)},
            filterText=filterText,
            isModerator =isModerator,
            updateAutoModSettings={updateAutoModSettings()},
            updateAutoModSettingsStatus=updateAutoModSettingsStatus,
            updateAutoModSettingsStatusToNull ={updateAutoModSettingsStatusToNull()}


        )
    }else{
        ModView.ModViewScaffold(
            closeStreamInfo={closeStreamInfo()},
            modViewDragStateViewModel =modViewDragStateViewModel,
            chatMessages =chatMessages,
            clickedUserData=clickedUserData,
            clickedUserChats = clickedUserChatMessages,
            updateClickedUser = {  username, userId,isBanned,isMod ->
                updateClickedUser(
                    username,
                    userId,
                    isBanned,
                    isMod
                )
            },
            timeoutDuration=timeoutDuration,
            changeTimeoutDuration={newValue ->changeTimeoutDuration(newValue)},
            timeoutReason = timeoutReason,
            changeTimeoutReason = {newValue->changeTimeoutReason(newValue)},
            banDuration = banDuration,
            changeBanDuration={newValue ->changeBanDuration(newValue)},
            banReason= banReason,
            changeBanReason = {newValue ->changeBanReason(newValue)},
            loggedInUserIsMod =loggedInUserIsMod,
            clickedUserIsMod=clickedUserIsMod,
            timeoutUser = {timeoutUser()},
            showTimeoutErrorMessage= showTimeoutErrorMessage,
            setTimeoutShowErrorMessage ={newValue ->setTimeoutShowErrorMessage(newValue)},
            showBanErrorMessage= showBanErrorMessage,
            setBanShowErrorMessage ={newValue ->setBanShowErrorMessage(newValue)},
            banUser={banUser()},
            modActionList = modActionList,
            autoModMessageList =autoModMessageList,
            manageAutoModMessage={messageId,userId, action ->manageAutoModMessage(messageId,userId,action)},
            connectionError =connectionError,
            reconnect ={reconnect()}

        )
    }


}
/**
 * EditAutoModSettingsScaffold is a [Scaffold] based component that is responsible for showing the user all the information related to
 * editing the channels automod settings.
 *
 *
 * */
@Composable
fun EditAutoModSettingsScaffold(
    closeStreamInfo:()->Unit,
    changeSelectedIndex:(Int, FilterType)->Unit,
    swearingIndex:Int,
    sexBasedTermsIndex:Int,
    aggressionIndex:Int,
    bullyingIndex:Int,
    disabilityIndex:Int,
    sexualityIndex:Int,
    misogynyIndex:Int,
    raceIndex:Int,

    sliderPosition: Float,
    changSliderPosition:(Float)->Unit,
    filterText:String,
    isModerator: Response<Boolean>,
    updateAutoModSettings:()->Unit,
    updateAutoModSettingsStatus:Response<Boolean>?,
    updateAutoModSettingsStatusToNull:()->Unit
){
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            EditAutoModTitle(
                closeStreamInfo={closeStreamInfo()},
                title ="AutoMod Info",
                contentDescription = "close auto mod info",
                isModerator =isModerator,
                updateAutoModSettings ={updateAutoModSettings()}
            )
        }

    ) {contentPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)){
            Column(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.primary)

            ) {

                AutoMod.Settings(
                    sliderPosition =sliderPosition,
                    changSliderPosition = {float -> changSliderPosition(float)},
                    discriminationFilterList = listOf("No filtering", "Less filtering", "Some filtering", "More filtering", "Maximum filtering"),
                    changeSelectedIndex = {item,filterType ->changeSelectedIndex(item,filterType)},
                    updateAutoModSettings = {  },
                    swearingIndex = swearingIndex,
                    sexBasedTermsIndex = sexBasedTermsIndex,
                    aggressionIndex = aggressionIndex,
                    bullyingIndex = bullyingIndex,
                    disabilityIndex = disabilityIndex,
                    sexualityIndex = sexualityIndex,
                    misogynyIndex = misogynyIndex,
                    raceIndex = raceIndex,
                    isModerator = true,
                    filterText = filterText
                )
            } // end of column
            when(updateAutoModSettingsStatus){
                is Response.Loading ->{
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is Response.Success ->{
                    Text("Success",
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 5.dp)
                            .background(Color.Green.copy(0.7f))
                            .align(Alignment.Center)
                            .clickable {
                                scope.launch {
                                    updateAutoModSettingsStatusToNull()
                                }
                            },
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                }
                is Response.Failure ->{
                    Text("Failed",
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 5.dp)
                            .background(Color.Red.copy(0.7f))
                            .align(Alignment.Center)
                            .clickable {
                                scope.launch {
                                    updateAutoModSettingsStatusToNull()
                                }
                            },
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                else ->{

                }
            }

        }//end of box

    }

}
@Composable
fun EditAutoModTitle(
    closeStreamInfo:()->Unit,
    title:String,
    contentDescription:String,
    isModerator: Response<Boolean>,
    updateAutoModSettings:()->Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.DarkGray)
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Row(verticalAlignment = Alignment.CenterVertically){
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = contentDescription,
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        closeStreamInfo()
                    },
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Text(text =title,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,modifier = Modifier.padding(start=20.dp))
        }

        ModViewDragSection.IsModeratorButton(
            isModerator = isModerator,
            updateAutoModSettings={updateAutoModSettings()}
        )

    }
}







