package com.example.clicker.network.models.twitchStream

import com.google.gson.annotations.SerializedName

/**
 * AutoModSettings is used to represent data received from the ***https://api.twitch.tv/helix/moderation/automod/settings***
 * end point
 * - Read more about the end point on the official documentation, [HERE](https://dev.twitch.tv/docs/api/reference/#get-automod-settings)
 * */
data class AutoModSettings(
    val data: List<IndividualAutoModSettings>
)

/**
 * IndividualAutoModSettings is used to represent the individual AutoMod settings received from the ***https://api.twitch.tv/helix/moderation/automod/settings***
 * end point
 * - Read more about the end point on the official documentation, [HERE](https://dev.twitch.tv/docs/api/reference/#get-automod-settings)
 * */
data class IndividualAutoModSettings(
    @SerializedName("broadcaster_id")
    val broadcasterId: String,
    @SerializedName("moderator_id")
    val moderatorId: String,
    @SerializedName("overall_level")
    val overallLevel: Int?,
    @SerializedName("sexuality_sex_or_gender")
    val sexualitySexOrGender: Int,
    @SerializedName("race_ethnicity_or_religion")
    val raceEthnicityOrReligion: Int,
    @SerializedName("sex_based_terms")
    val sexBasedTerms: Int,

    val disability: Int,
    val aggression: Int,
    val misogyny:Int,
    val bullying:Int,
    val swearing:Int
)