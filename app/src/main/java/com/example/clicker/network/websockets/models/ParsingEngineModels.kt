package com.example.clicker.network.websockets.models

/**
 * PrivateMessageType represents the two types of messages sent in chat, 1) [MESSAGE]
 * and 2) [EMOTE]
 *
 * @property MESSAGE represents the normal message a user will send
 * @property EMOTE represents the emote a user will send
 * **/
enum class PrivateMessageType {
    MESSAGE, EMOTE
}
/**
 * MessageToken represents the individualized token a user sends
 *
 * @param messageType a [PrivateMessageType] object representing if this contains either a message or an emote
 * @param messageValue A String representing what the user sent
 * @param url A String that will be filled with a URL if the [messageType] is EMOTE. Otherwise is will be empty
 * */
data class MessageToken(
    val messageType:PrivateMessageType,
    val messageValue:String ="",
    val url:String=""
)


data class EmoteInText(
    val emoteUrl:String,
    val startIndex:Int,
    val endIndex:Int
)

fun findEmoteNames(input: String, emoteNames: List<String>): List<EmoteInText> {
    val regex = Regex("\\b(?:${emoteNames.joinToString("|")})\\b")
    return regex.findAll(input).map {
        EmoteInText(
            emoteUrl = "https://static-cdn.jtvnw.net/emoticons/v2/64138/static/light/1.0",
            startIndex = it.range.first,
            endIndex = it.range.last
        )
    }.toList()
}