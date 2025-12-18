package com.kostas.kostasapp.core.domain.usecase

import com.kostas.kostasapp.core.domain.repository.ConnectivityRepository
import kotlinx.coroutines.flow.Flow

class ObserveConnectivityUseCase(
    private val repo: ConnectivityRepository
) {
    operator fun invoke(): Flow<Boolean> = repo.isOnline
}