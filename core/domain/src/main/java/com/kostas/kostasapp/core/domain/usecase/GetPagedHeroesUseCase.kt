package com.kostas.kostasapp.core.domain.usecase

import androidx.paging.PagingData
import com.kostas.kostasapp.core.domain.repository.HeroesRepository
import com.kostas.kostasapp.core.model.Hero
import kotlinx.coroutines.flow.Flow

class GetPagedHeroesUseCase(
    private val repository: HeroesRepository
) {
    operator fun invoke(
        nameQuery: String? = null
    ): Flow<PagingData<Hero>> = repository.getPagedHeroes(nameQuery)
}