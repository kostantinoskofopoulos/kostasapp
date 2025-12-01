package com.kostas.kostasapp.core.domain.usecase

import com.kostas.kostasapp.core.domain.repository.SquadRepository
import com.kostas.kostasapp.core.model.Hero
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case for observing the user's current squad.
 */
class ObserveSquadUseCase(
    private val squadRepository: SquadRepository
) {

    /**
     * Returns a [Flow] that emits the current squad as a list of [Hero]s.
     */
    operator fun invoke(): Flow<List<Hero>> =
        squadRepository.squad
            .map { heroes ->
                heroes.sortedBy { it.name.orEmpty() }
            }
}