package com.example.clicker.presentation.util

import com.example.clicker.domain.TwitchDataStore
import com.example.clicker.network.clients.AllFollowedStreamers
import com.example.clicker.network.clients.GetModChannels
import com.example.clicker.network.domain.TwitchAuthentication
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.presentation.util.FakeAuthentication.Companion.validateToken
import com.example.clicker.util.NetworkAuthResponse
import com.example.clicker.util.NetworkNewUserResponse
import com.example.clicker.util.Response
import com.example.clicker.util.objectMothers.TwitchUserDataObjectMother
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

//todo: I NEED TO RETURN ALL OF THIS BACK INTO THE BUILD PATTERN
/**
 * FakeAuthentication is a STUB builder class  meant to return hard coded values that mimic interacting with a
 *  implementation of [TwitchAuthentication]
 * */
class FakeAuthentication  private constructor() {


    companion object:TwitchAuthentication{
        private var validateTokenReturnType:NetworkNewUserResponse<ValidatedUser> =NetworkNewUserResponse.Loading
        private var logoutReturnType:NetworkAuthResponse<Boolean> = NetworkAuthResponse.Loading

        /**
         * - build is the main builder function
         * - Once this method is called, the class is created
         * @return [TwitchAuthentication]
         * */
        fun build(): TwitchAuthentication {
            return this
        }
        /**
         * - setValidateTokenReturn_Success is a builder function.
         * - ensures that [validateToken] will return a [NetworkNewUserResponse.Success] value
         * */
        fun validateTokenReturn_Success(): Companion {
            validateTokenReturnType = NetworkNewUserResponse.Success(
                ValidatedUser("","",
                    listOf(),"11",2
                )
            )
            return this

        }

        override suspend fun validateToken(
            token: String
        ): Flow<NetworkNewUserResponse<ValidatedUser>> = flow{

            emit(validateTokenReturnType)
        }

        override fun logout(clientId: String, token: String): Flow<NetworkAuthResponse<Boolean>> = flow{
            emit(logoutReturnType)
        }

    }





}

/*******************************************START OF TwitchRepo*************************************************************************/
class FakeTwitchImplRepo {


    companion object:TwitchRepo{

        private var getFollowedLiveStreamsReturnType:NetworkNewUserResponse<List<StreamData>> =NetworkNewUserResponse.Loading
        private var getModeratedChannelsReturnType:NetworkAuthResponse<GetModChannels> = NetworkAuthResponse.Loading
        override suspend fun getFollowedLiveStreams(
            authorizationToken: String,
            clientId: String,
            userId: String
        ): Flow<NetworkNewUserResponse<List<StreamData>>> = flow{
            emit(getFollowedLiveStreamsReturnType)
        }

        override suspend fun getModeratedChannels(
            authorizationToken: String,
            clientId: String,
            userId: String
        ): Flow<NetworkAuthResponse<GetModChannels>> = flow{
            emit(getModeratedChannelsReturnType)
        }
        fun build():TwitchRepo{
            return this
        }
        fun getFollowedLiveStreams_Failure():Companion{
            getFollowedLiveStreamsReturnType =NetworkNewUserResponse.Failure(Exception("Failed"))
            return this
        }
        fun getFollowedLiveStreams_Success():Companion{
            getFollowedLiveStreamsReturnType =NetworkNewUserResponse.Success(listOf())
            return this
        }

    }




}
/*******************************************START OF TwitchDataStore*************************************************************************/


/**
 * FakeTokenDataStore is a STUB builder class meant to return hard coded values that mimic interacting with a
 *  implementation of [TwitchDataStore]
 * */
class FakeTokenDataStore private constructor() {

    companion object:TwitchDataStore{
        private var oAuthToken:String =""

        /**
         * - build() is the main builder function
         * - Once this method is called, the class is created
         * @return [TwitchDataStore]
         * */
        fun build(): TwitchDataStore {
            return this
        }

        /**
         * - emptyOAuthToken is a builder function.
         * - ensures that [getOAuthToken] will return a String object with a length less than 2
         * */
        fun emptyOAuthToken(): Companion {
            oAuthToken =""
            return this
        }
        /**
         * - fullOAuthToken is a builder function.
         * - ensures that [getOAuthToken] will return a String object with a length greater than 2
         * */
        fun fullOAuthToken():Companion{
            oAuthToken ="FakeOAuthToken"
            return this
        }


        override suspend fun setOAuthToken(oAuthToken: String) {

        }

        override fun getOAuthToken(): Flow<String> = flow{
            emit(oAuthToken)

        }
        override suspend fun setUsername(username: String) {
            TODO("Not yet implemented")
        }

        override fun getUsername(): Flow<String> {
            TODO("Not yet implemented")
        }

        override suspend fun setLoggedOutStatus(loggedOut: String) {
            TODO("Not yet implemented")
        }

        override fun getLoggedOutStatus(): Flow<String?> {
            TODO("Not yet implemented")
        }

        override suspend fun setLoggedOutLoading(loggedOutStatus: Boolean) {
            TODO("Not yet implemented")
        }

        override fun getLoggedOutLoading(): Flow<Boolean> {
            TODO("Not yet implemented")
        }

        override suspend fun setClientId(clientId: String) {
            TODO("Not yet implemented")
        }

        override fun getClientId(): Flow<String> {
            TODO("Not yet implemented")
        }

    }


}