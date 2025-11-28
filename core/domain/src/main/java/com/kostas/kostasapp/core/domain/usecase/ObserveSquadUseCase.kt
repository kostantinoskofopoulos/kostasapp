package com.kostas.kostasapp.core.domain.usecase

import com.kostas.kostasapp.core.domain.repository.SquadRepository
import com.kostas.kostasapp.core.model.Hero
import kotlinx.coroutines.flow.Flow

class ObserveSquadUseCase(
    private val squadRepository: SquadRepository
) {
    operator fun invoke(): Flow<List<Hero>> = squadRepository.squad
}