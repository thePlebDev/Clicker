package com.example.clicker.network.models.emotes

/**
 * IndivBetterTTVEmote is a return type for [getGlobalEmotes][com.example.clicker.network.clients.BetterTTVEmoteClient.getGlobalEmotes] represents an individual BetterTTV emote
 *
 * @param id a String representing the unique identifier for the betterTTV emote. This is used in the URL to identify the emote
 * @param code a String representing the name of the emote
 * @param imageType a String representing the type of emote
 * @param userId a String representing what emote group this belongs to
 * @param modifier a Boolean indicating if there should be any special modifiers used
 *
 * */
data class IndivBetterTTVEmote( //TODO: I DON'T THINK THIS BELONGS HERE
    val id: String,
    val code: String,
    val imageType: String,
    val animated: Boolean,
    val userId: String,
    val modifier: Boolean
)


/**
 * BetterTTVChannelEmotes is a return type for [getChannelEmotes][com.example.clicker.network.clients.BetterTTVEmoteClient.getChannelEmotes]
 *
 * @param id a String representing the unique identifier for the betterTTV emote. This is used in the URL to identify the emote
 * @param bots a List of strings representing bots in the channel
 * @param avatar a String representing the avatar of the channel
 * @param channelEmotes a List of [BetterTTVIndivChannelEmote] objects
 * @param sharedEmotes a List of [BetterTTVSharedEmote] objects
 *
 * */
data class BetterTTVChannelEmotes(
    val id: String="",
    val bots: List<String> = listOf(),
    val avatar: String="",
    val channelEmotes: List<BetterTTVIndivChannelEmote> = listOf(),
    val sharedEmotes:List<BetterTTVSharedEmote> = listOf()
)

/**
 * BetterTTVIndivChannelEmote represents an individual betterTTV channel emote
 *
 * @param id a String representing the unique identifier for the betterTTV emote. This is used in the URL to identify the emote
 * @param code a String representing the name of the emote
 * @param imageType a String representing the type of emote
 * @param userId a String representing what emote group this belongs to
 * @param animated a Boolean indicating if the emote is animated or not
 *
 * */
data class BetterTTVIndivChannelEmote(
    val id: String,
    val code: String,
    val imageType: String,
    val animated: Boolean,
    val userId: String,
)

/**
 * BetterTTVSharedEmote represents an individual betterTTV shared channel emote
 * @param id a String representing the unique identifier for the betterTTV emote. This is used in the URL to identify the emote
 * @param code a String representing the name of the emote
 * @param imageType a String representing the type of emote
 * @param user a [BetterTTVSharedEmoteUser] object representing what user this emote belongs to
 * @param animated a Boolean indicating if the emote is animated or not
 * */
data class BetterTTVSharedEmote(
    val id: String,
    val code: String,
    val imageType: String,
    val animated: Boolean,
    val user: BetterTTVSharedEmoteUser
)

/**
 * BetterTTVSharedEmoteUser represents an individual betterTTV channel emote user
 * @param id a String representing the unique identifier for the betterTTV emote. This is used in the URL to identify the emote
 * @param name a String representing the login of the betterttv user
 * @param displayName a String representing the username of the betterttv user
 * @param providerId a String representing the unique identifier of the betterttv user
 * */
data class BetterTTVSharedEmoteUser(
    val id: String,
    val name: String,
    val displayName: String,
    val providerId: String
)