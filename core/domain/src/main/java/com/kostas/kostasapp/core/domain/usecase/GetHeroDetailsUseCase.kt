package com.kostas.kostasapp.core.domain.usecase

import com.kostas.kostasapp.core.domain.repository.HeroesRepository
import com.kostas.kostasapp.core.model.Hero

/**
 * Use case for loading detailed information about a single [Hero].
 */
class GetHeroDetailsUseCase(
    private val repository: HeroesRepository
) {

    /**
     * Loads hero details by [id] from the [HeroesRepository].
     */
    suspend operator fun invoke(id: Int): Hero =
        repository.getHeroDetails(id)
}