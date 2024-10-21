package com.example.clicker.presentation.home.models

import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.util.NetworkAuthResponse
import com.example.clicker.util.NetworkNewUserResponse


/**
 * UserTypes is used to in the [determineUserType()][com.example.clicker.presentation.home.HomeViewModel.determineUserType] method
 * to determine the type of user the current user is
 * */
enum class UserTypes {
    NEW, RETURNING, LOGGEDOUT,
}
/**
 * StreamInfo is a data class that represents all the information that is shown to the user when their followed streams
 * are fetched
 *
 * */
data class StreamInfo(
    val streamerName: String,
    val streamTitle: String,
    val gameTitle: String,
    val views: Int,
    val url: String,
    val broadcasterId: String
)
data class ModChannelUIState(
    val offlineModChannelList:List<String> =listOf(),
    val liveModChannelList:List<StreamData> = listOf(),
    val modChannelResponseState: NetworkNewUserResponse<Boolean> = NetworkNewUserResponse.Loading,
    val modRefreshing:Boolean = false,
)
data class HomeUIState(

    val width: Int = 0,
    val aspectHeight: Int = 0,
    val screenDensity: Float = 0f,
    val streamersListLoading: NetworkNewUserResponse<List<StreamData>> = NetworkNewUserResponse.Loading,

    val networkConnectionState:Boolean = true,

    val homeRefreshing:Boolean = false,
    val homeNetworkErrorMessage:String ="Disconnected from network",
    val logoutDialogIsOpen:Boolean=false,
    val horizontalLongHoldStreamList: NetworkNewUserResponse<List<StreamData>> = NetworkNewUserResponse.Loading,
    val userIsLoggedIn: NetworkAuthResponse<Boolean> = NetworkAuthResponse.Loading,
    val showFailedDialog:Boolean = false,
    val showNetworkRefreshError:Boolean = false,

    )