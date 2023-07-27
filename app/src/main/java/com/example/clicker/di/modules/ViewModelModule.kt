package com.example.clicker.di.modules

import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.repository.TwitchRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

// As a dependency of another class.
@Module
@InstallIn(ViewModelComponent::class)
abstract class ViewModelModule {

    @Binds
    abstract fun bindsTwitchRepo(
        twitchRepoImpl: TwitchRepoImpl
    ):TwitchRepo



}