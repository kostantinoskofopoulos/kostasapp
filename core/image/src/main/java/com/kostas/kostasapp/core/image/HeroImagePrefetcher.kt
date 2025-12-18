package com.kostas.kostasapp.core.image

import android.content.Context
import coil.ImageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import com.kostas.common.coroutines.IoDispatcher
import com.kostas.common.logging.Logger

@Singleton
class HeroImagePrefetcher @Inject constructor(
    private val imageLoader: ImageLoader,
    @ApplicationContext private val context: Context,
    private val logger: Logger,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ImagePrefetcher {

    private companion object {
        private const val TAG = "HeroImagePrefetcher"
        private const val MAX_PARALLEL = 4
    }

    private val parallelism = Semaphore(MAX_PARALLEL)

    override suspend fun prefetch(url: String) = withContext(ioDispatcher) {
        val trimmed = url.trim()
        if (trimmed.isEmpty()) return@withContext

        val request = ImageRequest.Builder(context)
            .data(trimmed)
            // Για prefetch συνήθως θες disk cache. Memory cache το κρατάμε off για να μην “φουσκώνει” RAM.
            .diskCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.DISABLED)
            .build()

        runCatching {
            imageLoader.execute(request)
        }.onFailure { t ->
            if (t is CancellationException) throw t
            logger.w(TAG, "prefetch failed url=$trimmed", t)
        }
    }

    override suspend fun prefetchAll(urls: List<String>, max: Int) {
        val list = urls
            .asSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .take(max.coerceAtLeast(0))
            .toList()

        if (list.isEmpty()) return

        coroutineScope {
            list.forEach { url ->
                launch {
                    parallelism.withPermit { prefetch(url) }
                }
            }
        }
    }
}