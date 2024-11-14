package com.example.clicker.presentation.home.models

import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.util.NetworkAuthResponse
import com.example.clicker.util.NetworkNewUserResponse


/**
 * - **UserTypes** is used to in the [determineUserType()][com.example.clicker.presentation.home.HomeViewModel.determineUserType] method
 * to determine the type of user the current user is
 *
 * - contains 3 constant  objects:
 * 1) **NEW** represents a new user
 * 2) **RETURNING** represents a returning user
 * 3) **LOGGEDOUT** represents a logged out user
 * */
enum class UserTypes {
    /**
     * represents a new user
     * */
    NEW,
    /**
     *
     * */
    RETURNING,
    /**
     *
     * */
    LOGGEDOUT,
}

/**
 * **StreamInfo** is a data class that represents all the information that is shown to the user when their followed streams
 * are fetched
 *
 * @param streamerName a String representing the streamer's name
 * @param streamTitle a String representing the stream's title
 * @param gameTitle a String representing the name of the game the streamer is playing
 * @param views a Integer representing the number of viewers the user has
 * @param url a String representing the url of the stream
 * @param broadcasterId a String representing the unique id of the streamer
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

/**
 * **StreamInfo** is a data class that represents all the information that is shown to the user when their followed streams
 * are fetched
 *
 * @param streamerName a String representing the streamer's name
 * @param streamTitle a String representing the stream's title
 * @param gameTitle a String representing the name of the game the streamer is playing
 * @param views a Integer representing the number of viewers the user has
 * @param url a String representing the url of the stream
 * @param broadcasterId a String representing the unique id of the streamer
 *
 * */

/**
 * **ModChannelUIState** is a data class representing the current state of all the channels the user is currently a moderator in
 *
 * @param offlineModChannelList  a List of Strings representing all the name of all the offline channels the user moderates for
 * @param liveModChannelList a List of [StreamData]  object representing all the name of all the online channels the user moderates for
 * @param modChannelResponseState a [NetworkNewUserResponse] object containing a Boolean. Representing the status of getting and
 * sorting both [offlineModChannelList] and [liveModChannelList]
 * @param modRefreshing a Boolean used to determine if the user is currently trying to reshresh the mod channels
 *
 * */
data class ModChannelUIState(
    val offlineModChannelList:List<String> =listOf(),
    val liveModChannelList:List<StreamData> = listOf(),
    val modChannelResponseState: NetworkNewUserResponse<Boolean> = NetworkNewUserResponse.Loading,
    val modRefreshing:Boolean = false,
)

/**
 * **HomeUIState** is a data class representing the current state of all the channels the user is currently a moderator in
 *
 * @param width  a List of Strings representing all the name of all the offline channels the user moderates for
 * @param aspectHeight a List of [StreamData]  object representing all the name of all the online channels the user moderates for
 * @param screenDensity a Float representing the density of the device's screen
 * @param streamersListLoading a Boolean used to determine if the user is currently trying to refresh the mod channels
 * @param networkConnectionState a Boolean used to determine if there was an error in the network connection
 * @param homeRefreshing a Boolean used to determine if the user is trying to refresh home page
 * @param homeNetworkErrorMessage A String used to show the user an error message if [networkConnectionState] is true
 * @param logoutDialogIsOpen a Boolean used to determine if the user's logged out dialog should be shown
 * @param horizontalLongHoldStreamList a [NetworkNewUserResponse] object containing a List of [StreamData] object. Used to
 * represent a list of followed channels when the user is in the horizontal mode
 * @param userIsLoggedIn a [NetworkAuthResponse] object containing a Boolean and used to determine the entire status of the
 * user's logged in session
 * @param showFailedDialog a Boolean used to determine if the user should be shown a failed dialog
 * @param showNetworkRefreshError a Boolean used to determine if the user should be shown a failed dialog after a refresh attempt
 *
 * */
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