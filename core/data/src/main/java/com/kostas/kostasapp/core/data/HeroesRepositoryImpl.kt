package com.kostas.kostasapp.core.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kostas.kostasapp.core.domain.repository.HeroesRepository
import com.kostas.kostasapp.core.model.Hero
import com.kostas.kostasapp.core.network.DisneyApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HeroesRepositoryImpl @Inject constructor(
    private val api: DisneyApiService
) : HeroesRepository {

    override fun getPagedHeroes(
        nameQuery: String?
    ): Flow<PagingData<Hero>> =
        Pager(
            PagingConfig(
                pageSize = 20,
                initialLoadSize = 40,
                enablePlaceholders = false
            )
        ) {
            HeroesPagingSource(
                api = api,
                query = nameQuery
            )
        }.flow

    override suspend fun getHeroDetails(id: Int): Hero =
        api.getCharacterById(id).data
}