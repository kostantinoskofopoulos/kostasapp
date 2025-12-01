package com.kostas.kostasapp.core.domain.usecase

import com.kostas.kostasapp.core.domain.repository.SquadRepository
import com.kostas.kostasapp.core.model.Hero

/**
 * Use case for toggling a [Hero] in the user's squad.
 *
 * If the hero is already in the squad, they are removed.
 * Otherwise, they are added.
 */
class ToggleSquadUseCase(
    private val squadRepository: SquadRepository
) {

    /**
     * Toggles the presence of [hero] in the squad.
     */
    suspend operator fun invoke(hero: Hero) {
        val isInSquad = squadRepository.isInSquad(hero.id)
        if (isInSquad) {
            squadRepository.removeFromSquad(hero)
        } else {
            squadRepository.addToSquad(hero)
        }
    }
}