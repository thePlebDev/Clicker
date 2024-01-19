package com.example.clicker.network.models.twitchStream


/**
 * BanUserData represents all of the data necessary to ban a user from chat
 *
 * @param user_id the unique identifier of this user
 * @param reason The reason a user was banned
 * @param duration a integer used to represent the length of the users ban
 * */
data class BanUserResponse(
    val data: List<BanUserResponseData>
)

data class BanUserResponseData(
    val broadcaster_id: String,
    val moderator_id: String,
    val user_id: String,
    val created_at: String,
    val end_time: String? // Note that end_time can be null
)