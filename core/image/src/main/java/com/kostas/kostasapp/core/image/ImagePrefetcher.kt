package com.kostas.kostasapp.core.image

interface ImagePrefetcher {
    suspend fun prefetch(url: String)
    suspend fun prefetchAll(urls: List<String>, max: Int = 40)
}