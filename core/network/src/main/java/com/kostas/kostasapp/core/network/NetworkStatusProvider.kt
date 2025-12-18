package com.kostas.kostasapp.core.network

import kotlinx.coroutines.flow.StateFlow

interface NetworkStatusProvider {
    val isOnline: StateFlow<Boolean>
    fun isOnlineNow(): Boolean
}