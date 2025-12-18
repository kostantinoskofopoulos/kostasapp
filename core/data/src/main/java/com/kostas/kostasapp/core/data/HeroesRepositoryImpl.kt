package com.kostas.kostasapp.core.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.kostas.common.coroutines.ApplicationScope
import com.kostas.common.coroutines.IoDispatcher
import com.kostas.common.logging.Logger
import com.kostas.kostasapp.core.data.mapper.toDomain
import com.kostas.kostasapp.core.data.mapper.toEntity
import com.kostas.kostasapp.core.database.DisneyDatabase
import com.kostas.kostasapp.core.database.dao.HeroDao
import com.kostas.kostasapp.core.domain.repository.HeroesRepository
import com.kostas.kostasapp.core.image.HeroImagePrefetcher
import com.kostas.kostasapp.core.model.Hero
import com.kostas.kostasapp.core.network.DisneyApiService
import com.kostas.kostasapp.core.network.NetworkMonitor
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Singleton
class HeroesRepositoryImpl @Inject constructor(
    private val api: DisneyApiService,
    private val heroDao: HeroDao,
    private val db: DisneyDatabase,
    private val networkMonitor: NetworkMonitor,
    private val imagePrefetcher: HeroImagePrefetcher,
    private val logger: Logger,
    @ApplicationScope private val appScope: CoroutineScope,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : HeroesRepository {

    private val tag = "HeroesRepository"

    @OptIn(ExperimentalPagingApi::class)
    override fun getPagedHeroes(nameQuery: String?): Flow<PagingData<Hero>> {
        val trimmed = nameQuery?.takeIf { it.isNotBlank() }

        return if (trimmed == null) {
            Pager(
                config = HeroesPagingConfig.PAGING_CONFIG,
                remoteMediator = HeroesRemoteMediator(
                    api = api,
                    db = db,
                    heroDao = heroDao,
                    keysDao = db.heroRemoteKeysDao(),
                    networkMonitor = networkMonitor,
                    logger = logger,
                    imagePrefetcher = imagePrefetcher,
                    appScope = appScope,
                    ioDispatcher = ioDispatcher
                ),
                pagingSourceFactory = { heroDao.catalogPagingSource() }
            ).flow.map { pagingData ->
                pagingData.map { entity -> entity.toDomain() }
            }
        } else {
            Pager(
                config = HeroesPagingConfig.PAGING_CONFIG,
                pagingSourceFactory = { HeroesPagingSource(api = api, nameQuery = trimmed) }
            ).flow
        }
    }

    override suspend fun getHeroDetails(id: Int): Hero = withContext(ioDispatcher) {
        val now = System.currentTimeMillis()
        val isOnline = networkMonitor.isOnlineNow()

        val cached = heroDao.getHeroById(id)
        if (cached != null) {
            heroDao.touchSeen(id, now)

            val isFresh = (now - cached.lastFetchedAtMillis) < DETAILS_STALE_MS
            if (isFresh || !isOnline) {
                logger.d(tag, "Details from cache id=$id fresh=$isFresh online=$isOnline")
                return@withContext cached.toDomain()
            }
        } else if (!isOnline) {
            throw OfflineException()
        }

        val remoteDto = api.getCharacterById(id).data

        val preservedPinned = cached?.pinned ?: false
        val preservedSortIndex = cached?.sortIndex ?: com.kostas.kostasapp.core.database.CATALOG_SENTINEL_SORT_INDEX
        val preservedLastSeen = cached?.lastSeenAtMillis ?: now

        val entity = remoteDto.toEntity(
            sortIndex = preservedSortIndex,
            fetchedAtMillis = now
        ).copy(
            pinned = preservedPinned,
            lastSeenAtMillis = preservedLastSeen
        )

        db.withTransaction {
            heroDao.upsertAll(listOf(entity))
            heroDao.touchSeen(id, now)
        }

        entity.imageUrl?.let { imagePrefetcher.prefetch(it) }
        entity.toDomain()
    }

    private companion object {
        private const val DETAILS_STALE_MS = 24 * 60 * 60 * 1000L
    }
}