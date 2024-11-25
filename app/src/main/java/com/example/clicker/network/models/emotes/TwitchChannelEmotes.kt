package com.example.clicker.network.models.emotes




/**
 * - **ChannelEmoteResponse** represents the channel specific Twitch emotes
 *
 * @param data a List of [ChannelEmote] objects, each representing a unique set of Twitch channel emotes
 * */
data class ChannelEmoteResponse(
    val data: List<ChannelEmote>
)

/**
 * - **Emote** represents the Twitch global emotes
 *
 * @param id a String used to represent the unique identifier for the Emote
 * @param name a String used to represent the
 * @param images a [ChannelImages] object used to represent the all the sizes of the Emote
 * @param format a List of Strings used to represent the format of the emote
 * @param scale a List of Strings used to represent the scale of the emote
 * @param theme_mode a List of Strings used to represent the themes of the emote
 * @param emote_type a  Strings used to represent the type of the channel emote
 * */
data class ChannelEmote(
    val id: String,
    val name: String,
    val images: ChannelImages,
    val format: List<String>,
    val scale: List<String>,
    val theme_mode: List<String>,
    val emote_type:String
)


/**
 * - **ChannelImages** represents the possible sizes of the channel specific Twitch emotes
 *
 * @param url_1x a String representing a URL to the small version (28px x 28px) of the emote
 * @param url_2x a String representing a URL to the medium version (56px x 56px) of the emote.
 * @param url_4x a String representing a URL to the large version (112px x 112px) of the emote.
 * */
data class ChannelImages(
    val url_1x: String,
    val url_2x: String,
    val url_4x: String
)