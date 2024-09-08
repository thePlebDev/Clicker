package com.example.clicker.network.repository.models

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.Immutable
import com.example.clicker.network.models.emotes.IndivBetterTTVEmote


/**
 * EmoteNameEmoteType represents a single Twitch Emote from the Twitch servers, when calling get channel emotes
 * - you can read more about getting channel emotes, [HERE](https://dev.twitch.tv/docs/api/reference/#get-channel-emotes)
 *
 * @param name the name of the Twitch emote
 * @param url the url that is hosted on the twitch servers and is what we use to load the image,
 * @param emoteType a [EmoteTypes] used to represent the type of emote that it is
 * */
data class EmoteNameUrlEmoteType(
    val name:String,
    val url:String,
    val emoteType:EmoteTypes,
    val channelName:String =""
)

/**
 * EmoteTypes represents the two types of emotes, subscribers and followers. subscriber emotes are only available to subscribers
 * */
enum class EmoteTypes {
    SUBS, FOLLOWERS,
}

/**
 * EmoteNameUrlList is the immutable wrapper for [list]
 *
 * @param list a list of [EmoteNameUrl] objects where each one represents an individual emote
 * */
@Immutable
data class EmoteNameUrlList(
    val list:List<EmoteNameUrl> = listOf()
)


/**
 * EmoteNameUrl represents a single Twitch Emote from the Twitch servers. Each instance of this class is a unique Emote
 *
 * @param name the name of the Twitch emote
 * @param url the url that is hosted on the twitch servers and is what we use to load the image
 * */
data class EmoteNameUrl(
    val name:String,
    val url:String,
    val channelName: String=""
)


/**
 * EmoteListMap is the immutable wrapper for [map]. The map is used to display a Text composable's inlineContent
 *
 * @param map a map representing individual emotes
 * */
@Immutable
data class EmoteListMap(
    val map:Map<String, InlineTextContent>
)

/**
 * EmoteNameUrlEmoteTypeList is the immutable wrapper for [list].
 *
 * @param list a list of [EmoteNameUrlEmoteType] objects
 * */
@Immutable
data class EmoteNameUrlEmoteTypeList(
    val list:List<EmoteNameUrlEmoteType> = listOf()
)


/**
 * IndivBetterTTVEmoteList is the immutable wrapper for [list].
 *
 * @param list a list of [IndivBetterTTVEmote] objects.
 * */
@Immutable
data class IndivBetterTTVEmoteList(
    val list: List<IndivBetterTTVEmote> = listOf()
)
