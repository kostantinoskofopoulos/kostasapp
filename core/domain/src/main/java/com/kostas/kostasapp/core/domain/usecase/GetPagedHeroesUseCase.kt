package com.kostas.kostasapp.core.domain.usecase

import androidx.paging.PagingData
import com.kostas.kostasapp.core.domain.repository.HeroesRepository
import com.kostas.kostasapp.core.model.Hero
import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing a paginated list of [Hero]s.
 */
class GetPagedHeroesUseCase(
    private val repository: HeroesRepository
) {

    /**
     * Returns a [Flow] of [PagingData] for heroes.
     *
     * @param nameQuery Optional name filter. If null or blank, returns all heroes.
     */
    operator fun invoke(
        nameQuery: String? = null
    ): Flow<PagingData<Hero>> =
        repository.getPagedHeroes(nameQuery)
}