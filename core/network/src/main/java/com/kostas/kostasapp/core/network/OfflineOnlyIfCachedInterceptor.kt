package com.kostas.kostasapp.core.network

import java.util.concurrent.TimeUnit
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response


internal class OfflineOnlyIfCachedInterceptor(
    private val networkStatusProvider: NetworkStatusProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (networkStatusProvider.isOnlineNow()) {
            return chain.proceed(request)
        }

        val cacheControl = CacheControl.Builder()
            .onlyIfCached()
            .maxStale(7, TimeUnit.DAYS)
            .build()

        val offlineRequest = request.newBuilder()
            .cacheControl(cacheControl)
            .build()

        return chain.proceed(offlineRequest)
    }
}