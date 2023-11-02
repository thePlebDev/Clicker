package com.example.clicker.di.modules

import android.content.Context
import com.example.clicker.data.TokenValidationWorker
import com.example.clicker.domain.TwitchTokenValidationWorker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

// As a dependency of another class.
@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

//    @Binds
//    abstract fun bindsTwitchRepo(
//        twitchRepoImpl: TwitchRepoImpl
//    ):TwitchRepo

//    @Provides
//    fun provideTwitchRepo(twitchRepoImpl: TwitchRepoImpl): TwitchRepo {
//        return twitchRepoImpl
//    }

    @Provides
    fun provideTokenValidationWorker(
        @ApplicationContext appContext: Context
    ): TwitchTokenValidationWorker {
        return TokenValidationWorker(appContext)
    }
}