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
     *
     * @return true if the hero is now in the squad, false if they were removed.
     */
    suspend operator fun invoke(hero: Hero): Boolean =
        squadRepository.toggle(hero)
}