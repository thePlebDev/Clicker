package com.example.clicker.presentation.enhancedModView

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.clicker.network.clients.UnbanRequestItem
import com.example.clicker.network.models.twitchStream.ChatSettingsData
import com.example.clicker.network.repository.util.AutoModQueueMessage
import com.example.clicker.presentation.enhancedModView.Positions.BOTTOM
import com.example.clicker.presentation.enhancedModView.Positions.CENTER
import com.example.clicker.presentation.enhancedModView.Positions.TOP
import com.example.clicker.presentation.enhancedModView.Sections.ONE
import com.example.clicker.presentation.enhancedModView.Sections.THREE
import com.example.clicker.presentation.enhancedModView.Sections.TWO
import com.example.clicker.util.Response
import com.example.clicker.util.WebSocketResponse


/**
 * ----------------------------------------MOD VIEW DRAG STATE---------------------------------------------------------------------------
 * */
/**
 * - **Sections** is a object meant to be used to determine which section each piece of the enhanced modView is in. As it relates
 * to the UI
 *
 * @property ONE represents that the section is in section one
 * @property TWO represents that the section is in section two
 * @property THREE represents that the section is in section three
 * */
enum class Sections {
    ONE, TWO, THREE
}


/**
 * - **BoxDragStateOffsets** is data class used to determine the offset of the enhancedModeView sections.
 *
 * @param boxOneOffsetY a Float used to determine where on the screen box one should be placed
 * @param boxTwoOffsetY a Float used to determine where on the screen two one should be placed
 * @param boxThreeOffsetY a Float used to determine where on the screen three one should be placed
 *
 * */
data class BoxDragStateOffsets(
    val boxOneOffsetY:Float = 0f,
    val boxTwoOffsetY:Float = 700f,
    val boxThreeOffsetY:Float = 700f *2,
)

/**
 * - **IsBoxDragging** is data class used to determine if any of the boxes of the enhanced mod view are in -drag mode-.
 *
 * @param boxOneDragging a Boolean used to determine where on the screen box one should be placed inside of the enhanced mod view feature
 * @param boxTwoDragging a Boolean used to determine where on the screen box two should be placed inside of the enhanced mod view feature
 * @param boxThreeDragging a Boolean used to determine where on the screen box three should be placed inside of the enhanced mod view feature
 *
 * */
data class IsBoxDragging(
    val boxOneDragging:Boolean = false,
    val boxTwoDragging:Boolean = false,
    val boxThreeDragging:Boolean = false,
)

/**
 * - **BoxZIndexes** is data class used to determine the z-index of the individual boxes inside of the enhanced mod view feature
 *
 * @param boxOneZIndex a Float used to determine the z-index box one inside the layout of the enhanced mod view feature
 * @param boxTwoZIndex a Float used to determine the z-index box two inside the layout of the enhanced mod view feature
 * @param boxThreeZIndex a Float used to determine the z-index box three inside the layout of the enhanced mod view feature
 *
 * */
data class BoxZIndexes(
    val boxOneZIndex:Float = 0F,
    val boxTwoZIndex:Float = 0F,
    val boxThreeZIndex:Float = 0F,

    )


/**
 * - **BoxTypeIndex** is data class used to determine the contents(chat,unban requests...)  of the individual boxes inside of the enhanced mod view feature
 *
 * @param boxOneIndex a Integer used to determine the contents of box one inside the layout of the enhanced mod view feature
 * @param boxTwoIndex a Integer used to determine the contents of box two inside the layout of the enhanced mod view feature
 * @param boxThreeIndex a Integer used to determine the contents of box three inside the layout of the enhanced mod view feature
 *
 * */
data class BoxTypeIndex(
    val boxOneIndex:Int = 1,
    val boxTwoIndex:Int = 2,
    val boxThreeIndex:Int = 3,
)


/**
 * - **IndivBoxHeight** is data class used to determine the height of the individual boxes inside of the enhanced mod view feature
 *
 * @param boxOne a [Dp] used to determine the height of box one inside the layout of the enhanced mod view feature
 * @param boxTwo a [Dp] used to determine the height of box two inside the layout of the enhanced mod view feature
 * @param boxThree a [Dp] used to determine the height of box three inside the layout of the enhanced mod view feature
 *
 * */
data class IndivBoxHeight(
    val boxOne: Dp = 0.dp,
    val boxTwo: Dp = 0.dp,
    val boxThree: Dp = 0.dp
)












/**
 * -----------------------------------------------MOD VERSION 3 MODELS---------------------------------------------------------------------------
 * */


/**
 * - **Positions** is a object meant to be used to determine which position each piece of the enhanced modView is in. As it
 * relates to other position to other boxes
 *
 * @property TOP represents that the section is in section one
 * @property CENTER represents that the section is in section two
 * @property BOTTOM represents that the section is in section three
 * */
enum class Positions{
    TOP,CENTER, BOTTOM
}

/**
 * - **BoxNumber** is a object meant to be used to determine where the drag state be
 *
 * @property ONE represents that the drag state should be set to top
 * @property TWO represents that the drag state should be set to center
 * @property THREE represents that the drag state should be set to bottom
 * */
enum class BoxNumber{
    ONE,TWO,THREE
}

/**
 * - **boxOneIndex** is a object meant to be used to determine the z-index of the individual boxes inside of the
 * enhanced mod view feature. The main difference between **boxOneIndex** and [BoxZIndexes] is that this object is boolean
 * and not Float based
 * - This was a test and I have determined that I like this boolean based z-index better
 *
 * @param boxOneIndex a Boolean used to determine if box one should have a higher z-index that the other boxes
 * @param boxTwoIndex a Boolean used to determine if box two should have a higher z-index that the other boxes
 * @param boxThreeIndex a Boolean used to determine if box three should have a higher z-index that the other boxes
 * */
data class BoxZIndexs(
    val boxOneIndex: Boolean,
    val boxTwoIndex: Boolean,
    val boxThreeIndex: Boolean,
)

/**
 * - **DoubleTap** is a object meant to be used to determine if which boxes inside of the enhaced mod view feature the user
 * has double tapped on
 * @param boxOneDoubleTap a Boolean used to determine if box one has been double tapped on
 * @param boxTwoDoubleTap a Boolean used to determine if box two has been double tapped on
 * @param boxThreeDoubleTap a Boolean used to determine if box three has been double tapped on
 * */
data class DoubleTap(
    val boxOneDoubleTap: Boolean,
    val boxTwoDoubleTap: Boolean,
    val boxThreeDoubleTap: Boolean,
)




/**
 * - **ModArrayData** is a object meant to be used to add extra metadata to the individual box states
 *
 * @param height a [Dp] object used to determine the height of the box
 * @param index a Integer used to determine the contents of a box
 * @param position a [Positions] object used to determine where the box should be placed on the scree
 * @param boxNumber a [BoxNumber] object used to determine the position relative to other boxes
 * @param dragging a Boolean used to determine if the box is being dragged or not
 * @param doubleSize a Boolean used to determine if the box is in the double size state
 * @param tripleSize a Boolean used to determine if the box is in the triple size state
 * */
data class ModArrayData(
    val height:Dp,
    val index:Int,
    val position: Positions, // this would be top center or bottom
    val boxNumber: BoxNumber,
    val dragging:Boolean,
    val doubleSize:Boolean,
    val tripleSize:Boolean
)













/**
 * ---------------------------------------------------------MOD VIEW MODELS--------------------------------------------------------------------
 * */

/**
 * - **RequestIds** is a object meant to represent all of the String values that are needed to make network request
 *
 * @param oAuthToken A String that represents a Authentication token of this logged in session
 * @param clientId A String that represents the unique identifier of this application
 * @param broadcasterId A String that represents the unique identifier of a broadcaster
 * @param moderatorId A String that represents the unique identifier of a moderator
 * @param sessionId A String that represents the unique identifier of a chat session
 * */
data class RequestIds(
    val oAuthToken:String ="",
    val clientId:String="",
    val broadcasterId:String="",
    val moderatorId:String ="",
    val sessionId:String =""
)


/**
 * - **ModViewViewModelUIState** is a object meant to represent all of the main Ui data of the Mod View
 *
 * @param showSubscriptionEventError a [Response] object containing a Boolean used to determine the status of a websocket event
 * subscription
 * @param showAutoModMessageQueueErrorMessage a Boolean used to determine if an error should be shown or not
 * @param chatSettings a [ChatSettingsData] object used to determine the state of the chat
 * @param enabledChatSettings a Boolean used to determine if the chat settings is enabled or not
 * @param selectedSlowMode a [ListTitleValue] object used to determine if the slow mode is on or off
 * @param selectedFollowerMode a [ListTitleValue] object used to determine if the follow mode is on or off
 * @param modViewTotalNotifications a Integer used to show the user if there are any notifications from the enhanced mod view
 * @param modActionNotifications A Boolean used to determine if the user wants to be shown notifications or not
 * @param autoModMessagesNotifications a Boolean used to determine if the user wants to be shown any auto mod notifications
 * @param unbanRequestNotifications a Boolean used to determine if the user wants to be shown any unban request notifications
 * @param emoteOnly a Boolean used to determine if the chat is in emote only mode or not
 * @param subscriberOnly a Boolean used to determine if the chat is in subscriber only mode or not
 * */
data class ModViewViewModelUIState(
    val showSubscriptionEventError: Response<Boolean> = Response.Loading,
    val showAutoModMessageQueueErrorMessage:Boolean = false,
    val chatSettings: ChatSettingsData = ChatSettingsData(false,null,false,null,false,false),
    val enabledChatSettings:Boolean = true,
    val selectedSlowMode: ListTitleValue = ListTitleValue("Off",null),
    val selectedFollowerMode: ListTitleValue = ListTitleValue("Off",null),
    val modViewTotalNotifications:Int =0,

    val modActionNotifications:Boolean = true,
    val autoModMessagesNotifications:Boolean = true,
    val unbanRequestNotifications:Boolean = true,


    val emoteOnly:Boolean = false, //todo: THESE TWO ARE REALLY MESSING THINGS UP
    val subscriberOnly:Boolean = false,//todo: THESE TWO ARE REALLY MESSING THINGS UP

)

/**
 * - **ListTitleValue** is a object meant to represent the values of follower/subscriber mode in chat settings
 *
 * @param title a String value representing the title
 * @param value a Nullable Integer meant to represent the entire value of this object. null means 0
 * */
data class ListTitleValue(
    val title:String,
    val value:Int?
)

/**
 * - **ModViewStatus** is a object meant to represent the current status of moderation subscription events
 *
 * @param modActions a [WebSocketResponse] containing a Boolean and used to determine the status of the moderation action subscription evert
 * @param autoModMessageStatus a [WebSocketResponse] containing a Boolean and used to determine the status of the auto mod action subscription evert
 * @param channelPointsRewardQueueStatus a [WebSocketResponse] containing a Boolean and used to determine the status of the channel points reward queue
 * */
data class ModViewStatus(
    val modActions: WebSocketResponse<Boolean> = WebSocketResponse.Loading,
    val autoModMessageStatus: WebSocketResponse<Boolean> = WebSocketResponse.Loading,
    val channelPointsRewardQueueStatus: WebSocketResponse<Boolean> = WebSocketResponse.Loading,
)

/**
 * - ModActionData represents an individual event sent by the Twitch servers when a moderator takes action inside of the chat
 * - You can read more about the moderation action [HERE](https://dev.twitch.tv/docs/eventsub/eventsub-subscription-types/#channelmoderate)
 *
 * @param title a String that represents the main information shown to the user when a moderation action takes place. This should be as short as possible
 * @param message a String that represents information that needs to be shown to the user. It is meant to elaborate on [title].
 * Should tell the details of this moderation action
 * @param iconId a Int that represents the id of the drawable resource that is going to be used as the icon.
 * This will be turned into a [Painter] object and shown to the user as an icon next to [title]
 * @param secondaryMessage a nullable String object that represents a message that can be shown to the user. The text is shown in red.
 * This is mainly only used for displaying text that was deleted during a message deleted moderation event.
 *
 * */
data class ModActionData(
    val title:String,
    val message:String,
    val iconId: Int,
    val secondaryMessage:String? =null
)

/**
 * - **ClickedUnbanRequestUser** is a object meant to represent the clicked un-ban request UI
 *
 * @param message a String meant to represent the message sent from the unban request
 * @param userName a String meant to represent the message sent from the unban request
 * @param requestId a String meant to represent the request id of the un-ban request
 * @param status a String meant to represent the status of the request
 * */
data class ClickedUnbanRequestUser(
    val message:String,
    val userName:String,
    val requestId: String,
    val status:String
)

/**
 * - **ImmutableModeList** is a wrapper class meant to stabilize this object when passed to composables. This wrapper
 * class allows compose to skip recompositions while being passed this list
 *
 * @param modeList a List of [ListTitleValue] objects
 * */
@Immutable
data class ImmutableModeList(
    val modeList:List<ListTitleValue>
)

/**
 * AutoModMessageListImmutableCollection is a Wrapper object created specifically to handle the problem of the Compose compiler
 *  always marking  List as unstable.
 *  - You can read more about this Wrapper solution, [HERE](https://developer.android.com/develop/ui/compose/performance/stability/fix#annotated-classes)
 *
 * */
@Immutable
data class AutoModMessageListImmutableCollection(
    val autoModList: List<AutoModQueueMessage>
)


/**
 * ModActionListImmutableCollection is a Wrapper object created specifically to handle the problem of the Compose compiler
 *  always marking List as unstable.
 *  - You can read more about this Wrapper solution, [HERE](https://developer.android.com/develop/ui/compose/performance/stability/fix#annotated-classes)
 *
 * @param modActionList a list of [ModActionData] objects.
 * */
@Immutable
data class ModActionListImmutableCollection(
    val modActionList: List<ModActionData>
)



/**
 * - **UnbanRequestItemImmutableCollection** is a wrapper class meant to stabilize this object when passed to composables. This wrapper
 * class allows compose to skip recompositions while being passed this list
 *
 * @param list a List of [ListTitleValue] objects representing un-banned items
 * */
@Immutable
data class UnbanRequestItemImmutableCollection(
    val list:List<UnbanRequestItem>
)