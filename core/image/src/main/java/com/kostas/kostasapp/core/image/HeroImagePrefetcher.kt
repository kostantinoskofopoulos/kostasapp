package com.kostas.kostasapp.core.image

import android.content.Context
import coil.ImageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HeroImagePrefetcher @Inject constructor(
    private val imageLoader: ImageLoader,
    @param:ApplicationContext private val context: Context
)
 {
    suspend fun prefetch(url: String) {
        val request = ImageRequest.Builder(context)
            .data(url)
            .diskCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build()

        imageLoader.execute(request)
    }
}