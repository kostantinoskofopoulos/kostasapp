package com.kostas.kostasapp.core.domain.repository

import androidx.paging.PagingData
import com.kostas.kostasapp.core.model.Hero
import kotlinx.coroutines.flow.Flow

interface HeroesRepository {

    fun getPagedHeroes(
        nameQuery: String? = null
    ): Flow<PagingData<Hero>>

    suspend fun getHeroDetails(id: Int): Hero
}