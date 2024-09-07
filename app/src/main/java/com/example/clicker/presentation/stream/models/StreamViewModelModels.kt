package com.example.clicker.presentation.stream.models

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import com.example.clicker.network.models.twitchStream.ChatSettingsData
import com.example.clicker.network.models.websockets.LoggedInUserData
import com.example.clicker.network.websockets.models.MessageToken
import com.example.clicker.util.Response

data class ChattingUser(
    val username: String,
    val message: String
)

data class EmoteBoardData(
    val height:Int,
    val showBoard:Boolean
)

/**
 * ClickedUserNameChats represents a individual chat message
 *
 * @param dateSent a String representing the date the message was sent
 * @param message A String representing the entire message
 * @param messageTokenList A list of [MessageToken] objects where each object is a individual word of [message]
 *
 * */
data class ClickedUserNameChats(
    val dateSent:String,
    val message:String,
    val messageTokenList: List<MessageToken>,
)

@Immutable
data class TextFieldValueImmutable(
    val textFieldValue: TextFieldValue
)
@Immutable
data class ClickedUsernameChatsWithDateSentImmutable(
    val clickedChats:List<ClickedUserNameChats>
)

@Immutable
data class ClickedUserBadgesImmutable(
    val clickedBadges:List<String>
)



/**
 * ChatSettings holds all the data representing the current mod related chat settings
 * */
data class ModChatSettings(
    val showChatSettingAlert: Boolean = false,
    val showUndoButton:Boolean = false,
    val data: ChatSettingsData = ChatSettingsData(
        slowMode = false,slowModeWaitTime = null,
        followerMode = false, followerModeDuration =null ,
        subscriberMode = false,emoteMode=false
    ),

    val switchesEnabled:Boolean = true
)
/**
 * AdvancedChatSettings holds all the data representing the current advanced settings relating to the chat messages
 *
 * @param noChatMode a boolean determining if the user should be shown the chat messages or not
 * @param showSubs a boolean determining if the user should be shown subscription messages or not
 * @param showReSubs a boolean determining if the user should be shown re-subscription messages or not
 * @param showAnonSubs a boolean determining if the user should be shown anonymous subscription messages or not
 * @param showGiftSubs a boolean determining if the user should be shown gift subscription messages or not
 * */
data class AdvancedChatSettings(
    val noChatMode:Boolean = false,
    val showSubs:Boolean = true,
    val showReSubs:Boolean = true,
    val showAnonSubs:Boolean = true,
    val showGiftSubs:Boolean = true,
)
data class StreamUIState(
    val chatSettings: Response<ChatSettingsData> = Response.Loading, //websocket twitchImpl
    val loggedInUserData: LoggedInUserData? = null, //websocket

    val clientId: String = "", //twitchRepoImpl
    val broadcasterId: String = "", //twitchRepoImpl
    val userId: String = "", //twitchRepoImpl. This is also the moderatorId
    val login:String="",
    val oAuthToken: String = "", //twitchRepoImpl


    val oneClickActionsChecked:Boolean = true,
    val noChatMode:Boolean = false,
    val timeoutUserError:Boolean = false,
    val banUserError:Boolean = false,

    val banDuration: Int = 0, //twitchRepoImpl
    val banReason: String = "", //twitchRepoImpl
    val timeoutDuration: Int = 60, //twitchRepoImpl
    val timeoutReason: String = "", //twitchRepoImpl
    val banResponse: Response<Boolean> = Response.Success(false), //twitchRepoImpl
    val banResponseMessage: String = "", //twitchRepoImpl
    val undoBanResponse: Boolean = false, //twitchRepoImpl
    val showStickyHeader: Boolean = false, //twitchRepoImpl

    val chatSettingsFailedMessage: String = "",
)

/**
 * ClickedUIState represents all the information related to a clicked user inside of the chat
 *
 * @param clickedUsername a String representing the username of the clicked user
 * @param clickedUserId a String representing the unique identifier of the clicked user
 * @param clickedUsernameBanned a Boolean representing if the clicked user is banned or not
 * @param clickedUsernameIsMod a Boolean representing if the clicked user is a mod or not
 * */
data class ClickedUIState(
    val clickedUsername:String ="", //websocket
    val clickedUserId: String ="",
    val clickedUsernameBanned: Boolean=false,
    val clickedUsernameIsMod:Boolean =false,
)

/**
 * A object containing all the information related to the stream the user is currently watching
 *
 * @param channelName a String representing the channel name of the stream the user is currently watching
 * @param streamTitle a String representing the stream title the user is currently watching
 * @param category a String representing the stream category the user is currently watching
 * @param tags a List of strings representing the stream tags the user is currently watching
 * @param adjustedUrl a String representing the url to watch the stream
 *
 * */
data class ClickedStreamInfo(
    val channelName: String ="",
    val streamTitle:String ="",
    val category:String="",
    val tags:List<String> = listOf(),
    val adjustedUrl:String=""
)