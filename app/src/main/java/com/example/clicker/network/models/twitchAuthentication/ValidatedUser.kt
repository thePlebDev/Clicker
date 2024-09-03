package com.example.clicker.network.models.twitchAuthentication

import com.google.gson.annotations.SerializedName

/**
 * - ValidatedUser is a class that represents all of the data returned from a successful GET request
 * to the ***https://id.twitch.tv/oauth2/validate*** endpoint.
 * - You can read more about the token validation process in the [Twitch Documentation](https://dev.twitch.tv/docs/authentication/validate-tokens/)
 *
 * @param clientId the individual identifier of the application
 * @param login the username of the user logged in
 * @param scopes a list of Strings representing all the information this application has access to
 * @param userId the unique identifier of this user
 * @param expiresIn the time a user's session expires in
 *
 * */
data class ValidatedUser(
    @SerializedName("client_id")
    val clientId: String,
    val login: String,
    val scopes: List<String>,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("expires_in")
    val expiresIn: Int

)
