package com.kostas.kostasapp.core.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Domain-only contract.
 * Presentation can observe connectivity WITHOUT knowing Android APIs.
 */
interface ConnectivityRepository {
    val isOnline: Flow<Boolean>
}