package com.kostas.kostasapp.core.image

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageModule {

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context
    ): ImageLoader {
        val diskCacheDir = File(context.cacheDir, "coil_image_cache")

        return ImageLoader.Builder(context)
            .diskCache {
                DiskCache.Builder()
                    .directory(diskCacheDir)
                    .maxSizeBytes(250L * 1024 * 1024) // 250MB
                    .build()
            }
            .crossfade(true)
            .respectCacheHeaders(false)
            .build()
    }
}