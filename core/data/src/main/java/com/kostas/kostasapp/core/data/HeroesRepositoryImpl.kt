package com.kostas.kostasapp.core.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.kostas.common.coroutines.IoDispatcher
import com.kostas.common.logging.Logger
import com.kostas.kostasapp.core.data.mapper.toDomain
import com.kostas.kostasapp.core.data.mapper.toEntity
import com.kostas.kostasapp.core.database.DisneyDatabase
import com.kostas.kostasapp.core.domain.repository.HeroesRepository
import com.kostas.kostasapp.core.image.HeroImagePrefetcher
import com.kostas.kostasapp.core.model.Hero
import com.kostas.kostasapp.core.network.DisneyApiService
import com.kostas.kostasapp.core.network.NetworkMonitor
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

@Singleton
class HeroesRepositoryImpl @Inject constructor(
    private val api: DisneyApiService,
    private val db: DisneyDatabase,
    private val networkMonitor: NetworkMonitor,
    private val imagePrefetcher: HeroImagePrefetcher,
    private val logger: Logger,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : HeroesRepository {

    private val tag = "HeroesRepository"

    override fun getPagedHeroes(
        nameQuery: String?
    ): Flow<PagingData<Hero>> {
        val trimmedQuery = nameQuery?.takeIf { it.isNotBlank() }

        val isOnline = networkMonitor.isOnline()
        logger.d(tag, "getPagedHeroes(nameQuery=$trimmedQuery, isOnline=$isOnline)")

        return if (isOnline) {
            logger.i(tag, "Online → using network paging")

            val networkFlow = Pager(
                config = PAGING_CONFIG,
                pagingSourceFactory = {
                    HeroesPagingSource(
                        api = api,
                        nameQuery = trimmedQuery
                    )
                }
            ).flow

            if (trimmedQuery == null) {
                networkFlow.onStart {
                    logger.d(tag, "Online → caching top $TOP_CACHE_SIZE heroes to Room")
                    withContext(ioDispatcher) {
                        cacheTopHeroesIfNeeded()
                    }
                }
            } else {
                networkFlow
            }
        } else {
            logger.w(tag, "Offline → using Room cache (top $TOP_CACHE_SIZE heroes only)")

            Pager(
                config = PAGING_CONFIG,
                pagingSourceFactory = {
                    db.heroDao().pagingSource(nameQuery = trimmedQuery)
                }
            ).flow.map { pagingData ->
                pagingData.map { entity -> entity.toDomain() }
            }
        }
    }

    override suspend fun getHeroDetails(id: Int): Hero = withContext(ioDispatcher) {
        val isOnline = networkMonitor.isOnline()
        logger.d(tag, "getHeroDetails(id=$id, isOnline=$isOnline)")

        if (!isOnline) {
            logger.i(tag, "Offline → trying to load hero $id from Room")

            db.heroDao().getHeroById(id)?.let { entity ->
                logger.i(tag, "Offline → hero $id loaded from Room")
                return@withContext entity.toDomain()
            }

            logger.w(tag, "Offline → hero $id NOT found in Room, will throw")
            throw IllegalStateException("Hero $id not available offline")
        }

        logger.d(tag, "Online → fetching hero details from network id=$id")
        val remote = api.getCharacterById(id).data.toDomain()

        db.heroDao().insertAll(listOf(remote.toEntity()))
        remote.imageUrl?.let { url -> imagePrefetcher.prefetch(url) }

        logger.d(tag, "Online → hero $id loaded from network and cached")

        remote
    }

    private suspend fun cacheTopHeroesIfNeeded() {
        if (!networkMonitor.isOnline()) return

        val currentCount = db.heroDao().getCount()
        if (currentCount >= TOP_CACHE_SIZE) return

        val response = api.getCharacters(
            page = 1,
            pageSize = TOP_CACHE_SIZE
        )

        val heroesDomain = response.data
            .map { it.toDomain() }
            .sortedBy { it.name.orEmpty() }

        val entities = heroesDomain.map { it.toEntity() }

        db.withTransaction {
            db.heroDao().clearAll()
            db.heroDao().insertAll(entities)
        }

        // Prefetch images
        for (hero in heroesDomain) {
            hero.imageUrl?.let { imagePrefetcher.prefetch(it) }
        }
    }

    private companion object {
        private const val PAGE_SIZE = 20
        private const val INITIAL_LOAD_SIZE = 40
        private const val TOP_CACHE_SIZE = 100

        val PAGING_CONFIG = PagingConfig(
            pageSize = PAGE_SIZE,
            initialLoadSize = INITIAL_LOAD_SIZE,
            enablePlaceholders = false
        )
    }
}