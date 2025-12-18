package com.kostas.kostasapp.core.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.kostas.common.coroutines.ApplicationScope
import com.kostas.common.coroutines.IoDispatcher
import com.kostas.common.logging.Logger
import com.kostas.kostasapp.core.data.mapper.toEntity
import com.kostas.kostasapp.core.database.CATALOG_SENTINEL_SORT_INDEX
import com.kostas.kostasapp.core.database.DisneyDatabase
import com.kostas.kostasapp.core.database.dao.HeroDao
import com.kostas.kostasapp.core.database.dao.HeroRemoteKeysDao
import com.kostas.kostasapp.core.database.entity.HeroEntity
import com.kostas.kostasapp.core.database.entity.HeroRemoteKeysEntity
import com.kostas.kostasapp.core.image.HeroImagePrefetcher
import com.kostas.kostasapp.core.network.DisneyApiService
import com.kostas.kostasapp.core.network.NetworkMonitor
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagingApi::class)
class HeroesRemoteMediator(
    private val api: DisneyApiService,
    private val db: DisneyDatabase,
    private val heroDao: HeroDao,
    private val keysDao: HeroRemoteKeysDao,
    private val networkMonitor: NetworkMonitor,
    private val logger: Logger,
    private val imagePrefetcher: HeroImagePrefetcher,
    @ApplicationScope private val appScope: CoroutineScope,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val maxCacheSize: Int = 400,
    private val cacheTimeoutMillis: Long = 6 * 60 * 60 * 1000L, // 6 hours
) : RemoteMediator<Int, HeroEntity>() {

    private val tag = "HeroesRemoteMediator"

    override suspend fun initialize(): InitializeAction {
        val catalogCount = heroDao.getCatalogCount()
        val keysCount = keysDao.count()

        if (catalogCount > 0 && keysCount == 0) {
            logger.w(tag, "RemoteKeys missing while catalog exists -> forcing refresh")
            return InitializeAction.LAUNCH_INITIAL_REFRESH
        }

        val newestCatalogFetch =
            heroDao.getCatalogNewestFetchTimeMillis()
                ?: return InitializeAction.LAUNCH_INITIAL_REFRESH

        val isFresh = (System.currentTimeMillis() - newestCatalogFetch) < cacheTimeoutMillis
        return if (isFresh) InitializeAction.SKIP_INITIAL_REFRESH
        else InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, HeroEntity>
    ): MediatorResult {

        if (!networkMonitor.isOnlineNow()) {
            return when (loadType) {
                LoadType.APPEND -> MediatorResult.Error(OfflineException())
                LoadType.REFRESH -> {
                    val count = heroDao.getCatalogCount()
                    if (count == 0) MediatorResult.Error(OfflineException())
                    else MediatorResult.Success(endOfPaginationReached = false)
                }
                LoadType.PREPEND -> MediatorResult.Success(endOfPaginationReached = true)
            }
        }

        val page = when (loadType) {
            LoadType.REFRESH -> 1

            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)

            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()
                    ?: return MediatorResult.Success(endOfPaginationReached = true)

                if (lastItem.sortIndex == CATALOG_SENTINEL_SORT_INDEX) {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                val keys = keysDao.remoteKeysByHeroId(lastItem.id)
                val next = keys?.nextKey

                if (next != null) {
                    next
                } else {

                    val pageSize = state.config.pageSize
                    val inferredNextPage = (lastItem.sortIndex / pageSize.toLong() + 2L).toInt()
                    logger.w(
                        tag,
                        "Missing RemoteKeys for lastItemId=${lastItem.id} sortIndex=${lastItem.sortIndex}. " +
                                "Inferred nextPage=$inferredNextPage"
                    )
                    inferredNextPage
                }
            }
        }

        return try {
            val now = System.currentTimeMillis()
            val pageSize = state.config.pageSize

            val response = api.getCharacters(page = page, pageSize = pageSize)

            val totalPages = response.info?.totalPages ?: 1

            val entities = response.data.mapIndexed { indexInPage, dto ->
                val globalIndex = ((page - 1L) * pageSize) + indexInPage
                dto.toEntity(sortIndex = globalIndex, fetchedAtMillis = now)
            }

            val endReached = entities.isEmpty() || (page >= totalPages)

            val urlsToPrefetch = entities.mapNotNull { it.imageUrl }

            db.withTransaction {
                val pinnedBeforeRefresh =
                    if (loadType == LoadType.REFRESH) heroDao.getPinnedEntities() else emptyList()

                if (loadType == LoadType.REFRESH) {
                    keysDao.clearRemoteKeys()
                    heroDao.clearCatalog()
                }

                val keys = entities.map { hero ->
                    HeroRemoteKeysEntity(
                        heroId = hero.id,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (endReached) null else page + 1
                    )
                }

                keysDao.insertAll(keys)
                heroDao.upsertAll(entities)

                if (loadType == LoadType.REFRESH && pinnedBeforeRefresh.isNotEmpty()) {
                    val freshById = entities.associateBy { it.id }
                    val pinnedUpserts = pinnedBeforeRefresh.map { old ->
                        val fresh = freshById[old.id]
                        if (fresh != null) {
                            fresh.copy(
                                pinned = true,
                                lastSeenAtMillis = old.lastSeenAtMillis
                            )
                        } else {
                            old.copy(sortIndex = CATALOG_SENTINEL_SORT_INDEX)
                        }
                    }
                    heroDao.upsertAll(pinnedUpserts)
                }

                val totalCount = heroDao.getCount()
                val excess = totalCount - maxCacheSize
                if (excess > 0) {
                    logger.i(tag, "Pruning cache: count=$totalCount max=$maxCacheSize excess=$excess")
                    heroDao.deleteOldestNonPinned(excess)
                    keysDao.deleteOrphanKeys()
                }
            }

            appScope.launch(ioDispatcher) {
                imagePrefetcher.prefetchAll(urlsToPrefetch)
            }

            MediatorResult.Success(endOfPaginationReached = endReached)
        } catch (t: Throwable) {
            if (t is CancellationException) throw t
            logger.e(tag, "load(loadType=$loadType, page=$page) failed", t)
            MediatorResult.Error(t)
        }
    }
}