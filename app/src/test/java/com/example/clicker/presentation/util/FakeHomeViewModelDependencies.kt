package com.example.clicker.presentation.util

import com.example.clicker.domain.TwitchDataStore
import com.example.clicker.network.clients.GetModChannels
import com.example.clicker.network.domain.TwitchAuthentication
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.util.NetworkAuthResponse
import com.example.clicker.util.NetworkNewUserResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAuthentication(

): TwitchAuthentication {
    private var validateTokenReturnType:NetworkNewUserResponse<ValidatedUser> =NetworkNewUserResponse.Failure(Exception("RETURN TYPE SET TO NetworkNewUserResponse.Failure"))
    private var logoutReturnType:NetworkAuthResponse<Boolean> = NetworkAuthResponse.Loading

    override suspend fun validateToken(
        token: String
    ): Flow<NetworkNewUserResponse<ValidatedUser>> = flow{

        emit(validateTokenReturnType)
    }



    override fun logout(clientId: String, token: String): Flow<NetworkAuthResponse<Boolean>> = flow{
        emit(logoutReturnType)
    }
    fun setValidateTokenReturnType(returnType:NetworkNewUserResponse<ValidatedUser>){
        this.validateTokenReturnType = returnType
    }
    fun setLogoutReturnType(returnType:NetworkAuthResponse<Boolean>){
        this.logoutReturnType = returnType
    }

}
class FakeTwitchImplRepo: TwitchRepo {
    override suspend fun getFollowedLiveStreams(
        authorizationToken: String,
        clientId: String,
        userId: String
    ): Flow<NetworkNewUserResponse<List<StreamData>>> = flow{
        emit(NetworkNewUserResponse.Loading)
    }

    override suspend fun getModeratedChannels(
        authorizationToken: String,
        clientId: String,
        userId: String
    ): Flow<NetworkAuthResponse<GetModChannels>> = flow{
        emit(NetworkAuthResponse.Loading)
    }

}
class FakeTokenDataStore(
    private val userIsNewUser:Boolean
): TwitchDataStore {

    override suspend fun setOAuthToken(oAuthToken: String) {

    }

    override fun getOAuthToken(): Flow<String> = flow{
        if(userIsNewUser){
            emit("")
        }else{
            emit("fakeOAuthToken")
        }

    }

    override suspend fun setUsername(username: String) {

    }

    override fun getUsername(): Flow<String> = flow{
        emit("")
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