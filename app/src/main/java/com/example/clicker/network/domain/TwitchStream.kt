package com.example.clicker.network.domain

import com.example.clicker.network.BanUser
import com.example.clicker.network.BanUserResponse
import com.example.clicker.network.models.ChatSettings
import com.example.clicker.network.models.UpdateChatSettings
import com.example.clicker.util.Response
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.Flow
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

//data class UpdateAutoModSettings(
//    val data: List<UpdateAutoModSettingsItem>
//)
//
//data class UpdateAutoModSettingsItem(
//    @SerializedName("broadcaster_id")
//    val broadcasterId: String,
//    @SerializedName("moderator_id")
//    val moderatorId: String,
//    val overallLevel: Int?,
//    val disability: Int,
//    val aggression: Int,
//    @SerializedName("sexuality_sex_or_gender")
//    val sexualitySexOrGender: Int,
//    val misogyny: Int,
//    val bullying: Int,
//    val swearing: Int,
//    @SerializedName("race_ethnicity_or_religion")
//    val raceEthnicityOrReligion: Int,
//    @SerializedName("sex_based_terms")
//    val sexBasedTerms: Int
//)

interface TwitchStream {

    suspend fun getChatSettings(oAuthToken: String, clientId: String, broadcasterId: String): Flow<Response<ChatSettings>>

    suspend fun updateChatSettings(
        oAuthToken: String,
        clientId: String,
        broadcasterId: String,
        moderatorId: String,
        body: UpdateChatSettings
    ): Flow<Response<Boolean>>

    suspend fun deleteChatMessage(
        oAuthToken: String,
        clientId: String,
        broadcasterId: String,
        moderatorId: String,
        messageId: String

    ): Flow<Response<Boolean>>

    suspend fun banUser(
        oAuthToken: String,
        clientId: String,
        broadcasterId: String,
        moderatorId: String,
        body: BanUser
    ): Flow<Response<BanUserResponse>>

    suspend fun unBanUser(
        oAuthToken: String,
        clientId: String,
        broadcasterId: String,
        moderatorId: String,
        userId: String

    ): Flow<Response<Boolean>>

    suspend fun getAutoModSettings(
        oAuthToken: String,
        clientId: String,
        broadcasterId: String,
        moderatorId: String,
    ):Flow<Response<AutoModSettings>>

    suspend fun updateAutoModSettings(
        oAuthToken: String,
        clientId: String,
        autoModSettings: IndividualAutoModSettings
    ):Flow<Response<AutoModSettings>>
}