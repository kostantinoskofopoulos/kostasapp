package com.kostas.kostasapp.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class NetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val tag = "NetworkMonitor"

    fun isOnline(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val network = cm.activeNetwork ?: run {
            Timber.tag(tag).d("isOnline() -> false (no active network)")
            return false
        }
        val capabilities = cm.getNetworkCapabilities(network) ?: run {
            Timber.tag(tag).d("isOnline() -> false (no network capabilities)")
            return false
        }

        val result =
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        Timber.tag(tag).d("isOnline() -> $result")
        return result
    }
}