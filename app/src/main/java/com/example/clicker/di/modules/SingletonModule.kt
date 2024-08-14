package com.example.clicker.di.modules

import android.content.Context
import android.util.Log
import com.example.clicker.data.TokenDataStore
import com.example.clicker.domain.TwitchDataStore
import com.example.clicker.network.clients.BetterTTVEmoteClient
import com.example.clicker.network.clients.TwitchAuthenticationClient
import com.example.clicker.network.clients.TwitchClient
import com.example.clicker.network.clients.TwitchEmoteClient
import com.example.clicker.network.clients.TwitchHomeClient
import com.example.clicker.network.clients.TwitchModClient
import com.example.clicker.network.domain.BetterTTVEmotes
import com.example.clicker.network.domain.NetworkMonitorRepo
import com.example.clicker.network.domain.TwitchAuthentication
import com.example.clicker.network.domain.TwitchEmoteRepo
import com.example.clicker.network.domain.TwitchEventSubscriptionWebSocket
import com.example.clicker.network.domain.TwitchEventSubscriptions
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.domain.TwitchStream
import com.example.clicker.network.repository.TwitchRepoImpl
import com.example.clicker.network.websockets.ParsingEngine
import com.example.clicker.network.websockets.TwitchWebSocket
import com.example.clicker.network.domain.TwitchSocket
import com.example.clicker.network.interceptors.LiveNetworkMonitor
import com.example.clicker.network.interceptors.LoggingInterceptor
import com.example.clicker.network.interceptors.NetworkMonitor
import com.example.clicker.network.interceptors.NetworkMonitorInterceptor
import com.example.clicker.network.interceptors.RetryInterceptor
import com.example.clicker.network.interceptors.responseCodeInterceptors.Authentication401Interceptor
import com.example.clicker.network.interceptors.responseCodeInterceptors.ResponseChecker
import com.example.clicker.network.repository.BetterTTVEmotesImpl
import com.example.clicker.network.repository.DebugRetrofitResponse
import com.example.clicker.network.repository.NetworkMonitorImpl
import com.example.clicker.network.repository.TwitchAuthenticationImpl
import com.example.clicker.network.repository.TwitchEmoteImpl
import com.example.clicker.network.repository.TwitchEventSub
import com.example.clicker.network.repository.TwitchStreamImpl
import com.example.clicker.network.repository.util.AutoModMessageParsing
import com.example.clicker.network.repository.util.ChatSettingsParsing
import com.example.clicker.network.repository.util.ModActionParsing
import com.example.clicker.network.websockets.TwitchEventSubWebSocket
import com.example.clicker.presentation.AuthenticationEvent
import com.example.clicker.presentation.AuthenticationEventBus
import com.example.clicker.presentation.stream.util.NetworkMonitoring
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {

    @Provides
    fun provideNetworkMonitor(
        @ApplicationContext appContext: Context
    ): NetworkMonitor{
        return LiveNetworkMonitor(appContext)
    }
    @Provides
    fun provideNetworkMonitoring(
        @ApplicationContext appContext: Context
    ): NetworkMonitoring {
        return NetworkMonitoring(appContext)
    }

    /***************I NEED TO ADD A LOGGING INTERCEPTOR*****************************/
    @Singleton //scope binding
    @Provides
    fun providesBetterTTVClient(): BetterTTVEmoteClient {
//        val loggingInterceptor = OkHttpClient.Builder()
//            .addInterceptor(LoggingInterceptor())
//            .build()
        return Retrofit.Builder()
            .baseUrl("https://api.betterttv.net/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(BetterTTVEmoteClient::class.java)

    }
    @Provides
    fun providesBetterTTVEmotes(betterTTVEmotesImpl: BetterTTVEmotesImpl): BetterTTVEmotes {
        return betterTTVEmotesImpl
    }


    @Singleton //scope binding
    @Provides
    fun providesTwitchClient(
        liveNetworkMonitor: NetworkMonitor
    ): TwitchClient {
         val monitorClient = OkHttpClient.Builder()
            .addInterceptor(NetworkMonitorInterceptor(liveNetworkMonitor))
             .addInterceptor(Authentication401Interceptor(ResponseChecker()))
            .build()
        return Retrofit.Builder()
            .baseUrl("https://api.twitch.tv/helix/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(monitorClient)
            .build().create(TwitchClient::class.java)
    }

    @Singleton //scope binding
    @Provides
    fun providersTwitchEmoteClient():TwitchEmoteClient{
        val debugRetrofit = OkHttpClient.Builder()
            .addInterceptor(DebugRetrofitResponse())
            .build()
        return Retrofit.Builder()
            .baseUrl("https://api.twitch.tv/helix/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(TwitchEmoteClient::class.java)
    }

    @Singleton //scope binding
    @Provides
    fun providesTwitchAuthenticationClient(
        liveNetworkMonitor: NetworkMonitor
    ): TwitchAuthenticationClient {
        val monitorClient = OkHttpClient.Builder()
            .addInterceptor(NetworkMonitorInterceptor(liveNetworkMonitor))
            .addNetworkInterceptor(Authentication401Interceptor(ResponseChecker()))

            .build()
        return Retrofit.Builder()
            .baseUrl("https://id.twitch.tv/oauth2/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(monitorClient)
            .build().create(TwitchAuthenticationClient::class.java)
    }

    @Singleton
    @Provides
    fun providesTwitchHomeClient(
        liveNetworkMonitor: NetworkMonitor
    ):TwitchHomeClient{
        val monitorClient = OkHttpClient.Builder()
            .addInterceptor(NetworkMonitorInterceptor(liveNetworkMonitor))
            .addInterceptor(Authentication401Interceptor(ResponseChecker()))
            .build()
        return Retrofit.Builder()
            .baseUrl("https://api.twitch.tv/helix/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(monitorClient)
            .build().create(TwitchHomeClient::class.java)
    }

    @Singleton
    @Provides
    fun providesTwitchModClient(): TwitchModClient {
//        val monitorClient = OkHttpClient.Builder()
//            .addInterceptor(NetworkMonitorInterceptor(liveNetworkMonitor))
//            .addInterceptor(Authentication401Interceptor(ResponseChecker()))
//            .build()
        return Retrofit.Builder()
            .baseUrl("https://api.twitch.tv/helix/")
            .addConverterFactory(GsonConverterFactory.create())
            //.client(monitorClient)
            .build().create(TwitchModClient::class.java)
    }

    @Singleton
    @Provides
    fun providesTokenDataStore(
        @ApplicationContext appContext: Context
    ): TwitchDataStore {
        return TokenDataStore(appContext)
    }

    @Singleton
    @Provides
    fun providesNetworkMonitorRepo(): NetworkMonitorRepo {
        return NetworkMonitorImpl()
    }


    @Provides
    fun provideTwitchRepo(twitchRepoImpl: TwitchRepoImpl): TwitchRepo {
        return twitchRepoImpl
    }


    @Singleton
    @Provides
    fun providesTwitchEmoteRepo(
       twitchEmoteClient: TwitchEmoteClient,
       betterTTVClient: BetterTTVEmoteClient,
    ): TwitchEmoteRepo {
        return TwitchEmoteImpl(twitchEmoteClient,betterTTVClient)
    }

    @Singleton
    @Provides
    fun provideTwitchAuthRepo(twitchAuthenticationImpl: TwitchAuthenticationImpl): TwitchAuthentication {
        Log.d("provideTwitchAuthRepo","${twitchAuthenticationImpl.hashCode()}")
        return twitchAuthenticationImpl
    }

    @Provides
    fun provideTwitchStreamRepo(twitchStreamImpl: TwitchStreamImpl): TwitchStream {
        return twitchStreamImpl
    }

    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }
    @Provides
    fun provideTwitchWebSocket(
        tokenDataStore: TwitchDataStore,
        twitchParsingEngine: ParsingEngine
    ): TwitchSocket {
        return TwitchWebSocket(tokenDataStore,twitchParsingEngine)
    }


    @Singleton
    @Provides
    fun provideAuthenticationEvent(): AuthenticationEvent {
        return AuthenticationEventBus()
    }

    @Provides
    fun providesTwitchEventSubscriptionWebSocket(): TwitchEventSubscriptionWebSocket {
        val modActionParsing:ModActionParsing = ModActionParsing()
         val channelSettingsParsing: ChatSettingsParsing =ChatSettingsParsing()
        val autoModMessageParsing: AutoModMessageParsing = AutoModMessageParsing()
        return TwitchEventSubWebSocket(modActionParsing,channelSettingsParsing,autoModMessageParsing)
    }
    @Provides
    fun providesTwitchEventSubscriptions(
        twitchClient: TwitchClient
    ): TwitchEventSubscriptions{
        return TwitchEventSub(twitchClient)
    }
}
