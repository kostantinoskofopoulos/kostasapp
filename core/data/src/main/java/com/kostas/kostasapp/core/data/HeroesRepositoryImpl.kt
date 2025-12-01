package com.kostas.kostasapp.core.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kostas.kostasapp.core.domain.repository.HeroesRepository
import com.kostas.kostasapp.core.data.mapper.toDomain
import com.kostas.kostasapp.core.model.Hero
import com.kostas.kostasapp.core.network.DisneyApiService
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class HeroesRepositoryImpl @Inject constructor(
    private val api: DisneyApiService
) : HeroesRepository {

    override fun getPagedHeroes(
        nameQuery: String?
    ): Flow<PagingData<Hero>> =
        Pager(
            config = PAGING_CONFIG,
            pagingSourceFactory = {
                HeroesPagingSource(
                    api = api,
                    nameQuery = nameQuery
                )
            }
        ).flow

    override suspend fun getHeroDetails(id: Int): Hero =
        api.getCharacterById(id).data.toDomain()

    private companion object {
        private const val PAGE_SIZE = 20
        private const val INITIAL_LOAD_SIZE = 40

        val PAGING_CONFIG = PagingConfig(
            pageSize = PAGE_SIZE,
            initialLoadSize = INITIAL_LOAD_SIZE,
            enablePlaceholders = false
        )
    }
}