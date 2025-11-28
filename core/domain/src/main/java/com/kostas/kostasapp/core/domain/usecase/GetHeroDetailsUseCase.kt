package com.kostas.kostasapp.core.domain.usecase

import com.kostas.kostasapp.core.domain.repository.HeroesRepository
import com.kostas.kostasapp.core.model.Hero

class GetHeroDetailsUseCase(
    private val repository: HeroesRepository
) {
    suspend operator fun invoke(id: Int): Hero = repository.getHeroDetails(id)
}