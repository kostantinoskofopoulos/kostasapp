package com.kostas.kostasapp.core.network

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@Module
@InstallIn(SingletonComponent::class)
object OkHttpModule {

    @Provides
    @Singleton
    fun provideOkHttpCache(@ApplicationContext context: Context): Cache =
        Cache(
            directory = File(context.cacheDir, "okhttp_cache"),
            maxSize = 50L * 1024 * 1024 // 50MB
        )

    @Provides
    @Singleton
    fun provideOkHttpClient(
        cache: Cache,
        networkStatusProvider: NetworkStatusProvider
    ): OkHttpClient {

        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        return OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(OfflineOnlyIfCachedInterceptor(networkStatusProvider))
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun bindNetworkStatusProvider(
        impl: NetworkMonitor
    ): NetworkStatusProvider = impl
}