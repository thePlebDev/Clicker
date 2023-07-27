package com.example.clicker.di.modules

import com.example.clicker.network.TwitchClient
import com.example.clicker.network.TwitchRetrofitInstance
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.repository.TwitchRepoImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// As a dependency of another class.
@Module
@InstallIn(ViewModelComponent::class)
abstract class ViewModelModule {

    @Binds
    abstract fun bindsTwitchRepo(
        twitchRepoImpl: TwitchRepoImpl
    ):TwitchRepo



}