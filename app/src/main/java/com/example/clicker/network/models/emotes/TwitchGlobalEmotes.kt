package com.example.clicker.network.models.emotes

/**
 * - **GlobalChatBadgesData** represents the Twitch global emotes
 *
 * @param data a List of [GlobalBadgesSet] objects, each representing a unique set of Twitch global emotes
 * */
data class GlobalChatBadgesData(
    val data: List<GlobalBadgesSet>
)
/**
 * - **GlobalBadgesSet** represents the set of Twitch global emotes
 *
 * @param set_id a String representing the unique ID of Twitch global emotes
 * @param versions a List of [GlobalBadgesVersion] objects, where each one represents a Global Twitch emote
 * */
data class GlobalBadgesSet(
    val set_id: String,
    val versions: List<GlobalBadgesVersion>
)

/**
 * - **GlobalBadgesVersion** represents the set of Twitch global emotes
 *
 * @param id a String representing an ID that identifies this emote
 * @param image_url_1x a String representing a URL to the small version (28px x 28px) of the emote
 * @param image_url_2x a String representing a URL to the medium version (56px x 56px) of the emote.
 * @param image_url_4x a String representing a URL to the large version (112px x 112px) of the emote.
 * @param title a String representing the name of the Emote
 * @param description a String representing a the description of the badge.
 * @param click_action a String representing a the action to take when clicking on the Emote
 * @param click_url a String representing a the URL to navigate to when clicking on the badge
 * */
data class GlobalBadgesVersion(
    val id: String,
    val image_url_1x: String,
    val image_url_2x: String,
    val image_url_4x: String,
    val title: String,
    val description: String,
    val click_action: String,
    val click_url: String
)


/**
 * - **EmoteData** represents the Twitch global emotes
 *
 * @param data a List of [Emote] objects, each representing a unique set of Twitch global emotes
 * */
data class EmoteData(
    val data: List<Emote>
)
/**
 * - **Emote** represents the Twitch global emotes
 *
 * @param id a String used to represent the unique identifier for the Emote
 * @param name a String used to represent the
 * @param images a [Images] object used to represent the all the sizes of the Emote
 * @param format a List of Strings used to represent the format of the emote
 * @param scale a List of Strings used to represent the scale of the emote
 * @param theme_mode a List of Strings used to represent the themes of the emote
 * */
data class Emote(
    val id: String,
    val name: String,
    val images: Images,
    val format: List<String>,
    val scale: List<String>,
    val theme_mode: List<String>
)

/**
 * - **Images** represents the possible sizes of the Twitch emotes
 *
 * @param url_1x a String representing a URL to the small version (28px x 28px) of the emote
 * @param url_2x a String representing a URL to the medium version (56px x 56px) of the emote.
 * @param url_4x a String representing a URL to the large version (112px x 112px) of the emote.
 * */
data class Images(
    val url_1x: String,
    val url_2x: String,
    val url_4x: String
)