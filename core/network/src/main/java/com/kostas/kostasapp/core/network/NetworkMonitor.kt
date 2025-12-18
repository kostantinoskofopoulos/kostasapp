package com.kostas.kostasapp.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import com.kostas.common.coroutines.ApplicationScope
import com.kostas.common.coroutines.IoDispatcher

@Singleton
class NetworkMonitor @Inject constructor(
    @ApplicationContext context: Context,
    @ApplicationScope appScope: CoroutineScope,
    @IoDispatcher private val io: CoroutineDispatcher
) : NetworkStatusProvider {

    private val cm: ConnectivityManager =
        requireNotNull(context.getSystemService(ConnectivityManager::class.java)) {
            "ConnectivityManager is null"
        }

    override val isOnline: StateFlow<Boolean> =
        callbackFlow {
            fun emit(value: Boolean) { trySend(value).isSuccess }

            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    emit(isOnlineFor(network))
                }

                override fun onLost(network: Network) {
                    emit(isOnlineNow())
                }

                override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                    emit(isOnlineFor(caps))
                }
            }

            emit(isOnlineNow())
            runCatching { cm.registerDefaultNetworkCallback(callback) }

            awaitClose { runCatching { cm.unregisterNetworkCallback(callback) } }
        }
            .distinctUntilChanged()
            .flowOn(io)
            .stateIn(
                scope = appScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = isOnlineNow()
            )

    override fun isOnlineNow(): Boolean {
        val active = cm.activeNetwork ?: return false
        return isOnlineFor(active)
    }

    private fun isOnlineFor(network: Network): Boolean {
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return isOnlineFor(caps)
    }

    private fun isOnlineFor(caps: NetworkCapabilities): Boolean {
        val hasInternet = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val isValidated = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        val hasTransport =
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)

        return hasInternet && isValidated && hasTransport
    }
}