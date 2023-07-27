package com.example.clicker.di.modules

import android.content.Context
import com.example.clicker.data.TokenDataStore
import com.example.clicker.network.TwitchClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
}