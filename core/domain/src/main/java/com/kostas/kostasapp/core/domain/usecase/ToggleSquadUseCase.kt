package com.kostas.kostasapp.core.domain.usecase

import com.kostas.kostasapp.core.domain.repository.SquadRepository
import com.kostas.kostasapp.core.model.Hero

class ToggleSquadUseCase(
    private val squadRepository: SquadRepository
) {
    suspend operator fun invoke(hero: Hero) {
        val isInSquad = squadRepository.isInSquad(hero.id)
        if (isInSquad) {
            squadRepository.removeFromSquad(hero)
        } else {
            squadRepository.addToSquad(hero)
        }
    }
}