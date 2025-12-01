package com.kostas.kostasapp.core.domain.repository

import androidx.paging.PagingData
import com.kostas.kostasapp.core.model.Hero
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for accessing heroes data.
 *
 * Exposes:
 * - a paginated stream of heroes, optionally filtered by name
 * - a single hero details lookup by id
 *
 * The implementation is provided in the data layer
 * (e.g. [HeroesRepositoryImpl] backed by the Disney API).
 */
interface HeroesRepository {

    /**
     * Returns a [Flow] of [PagingData] of [Hero] items.
     *
     * @param nameQuery Optional name filter for searching heroes.
     *                  If null or blank, all heroes are returned.
     */
    fun getPagedHeroes(
        nameQuery: String? = null
    ): Flow<PagingData<Hero>>

    /**
     * Fetches detailed information for a single hero.
     *
     * @param id The hero id.
     * @return The [Hero] with full details.
     * @throws Exception if the hero cannot be loaded.
     */
    suspend fun getHeroDetails(id: Int): Hero
}