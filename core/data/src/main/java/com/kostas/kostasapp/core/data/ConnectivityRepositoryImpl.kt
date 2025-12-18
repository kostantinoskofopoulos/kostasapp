package com.kostas.kostasapp.core.data

import com.kostas.kostasapp.core.domain.repository.ConnectivityRepository
import com.kostas.kostasapp.core.network.NetworkMonitor
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class ConnectivityRepositoryImpl @Inject constructor(
    private val networkMonitor: NetworkMonitor
) : ConnectivityRepository {
    override val isOnline: Flow<Boolean> = networkMonitor.isOnline
}