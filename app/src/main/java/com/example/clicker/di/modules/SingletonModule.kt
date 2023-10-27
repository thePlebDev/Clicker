package com.example.clicker.di.modules

import android.content.Context
import com.example.clicker.data.TokenDataStore
import com.example.clicker.network.TwitchClient
import com.example.clicker.network.domain.TwitchAuthentication
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.domain.TwitchStream
import com.example.clicker.network.repository.TwitchRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {


    @Singleton
    @Provides
    fun providesTwitchClient(): TwitchClient {
        return Retrofit.Builder()
            .baseUrl("https://api.twitch.tv/helix/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(TwitchClient::class.java)
    }

    @Singleton
    @Provides
    fun providesTokenDataStore(
        @ApplicationContext appContext: Context
    ): TokenDataStore {
        return TokenDataStore(appContext)
    }

    @Provides
    fun provideTwitchRepo(twitchRepoImpl: TwitchRepoImpl): TwitchRepo {
        return twitchRepoImpl
    }
    @Provides
    fun provideTwitchAuthRepo(twitchRepoImpl: TwitchRepoImpl): TwitchAuthentication {
        return twitchRepoImpl
    }

    @Provides
    fun provideTwitchStreamRepo(twitchRepoImpl: TwitchRepoImpl): TwitchStream {
        return twitchRepoImpl
    }

    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }

}