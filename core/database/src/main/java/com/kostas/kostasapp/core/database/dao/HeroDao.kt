package com.kostas.kostasapp.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kostas.kostasapp.core.database.CATALOG_SENTINEL_SORT_INDEX
import com.kostas.kostasapp.core.database.entity.HeroEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HeroDao {

    @Query(
        """
        SELECT * FROM heroes
        WHERE sortIndex != $CATALOG_SENTINEL_SORT_INDEX
        ORDER BY sortIndex ASC
        """
    )
    fun catalogPagingSource(): PagingSource<Int, HeroEntity>

    @Query(
        """
        SELECT COUNT(*) FROM heroes
        WHERE sortIndex != $CATALOG_SENTINEL_SORT_INDEX
        """
    )
    suspend fun getCatalogCount(): Int

    @Query(
        """
        SELECT MAX(lastFetchedAtMillis) FROM heroes
        WHERE sortIndex != $CATALOG_SENTINEL_SORT_INDEX
        """
    )
    suspend fun getCatalogNewestFetchTimeMillis(): Long?

    @Query(
        """
        SELECT MAX(sortIndex) FROM heroes
        WHERE sortIndex != $CATALOG_SENTINEL_SORT_INDEX
        """
    )
    suspend fun getCatalogMaxSortIndex(): Long?

    @Query(
        """
        DELETE FROM heroes
        WHERE sortIndex != $CATALOG_SENTINEL_SORT_INDEX
        """
    )
    suspend fun clearCatalog()

    @Query("SELECT * FROM heroes WHERE pinned = 1")
    suspend fun getPinnedEntities(): List<HeroEntity>

    @Query(
        """
        SELECT * FROM heroes
        WHERE pinned = 1 OR lastSeenAtMillis > 0
        ORDER BY pinned DESC, lastSeenAtMillis DESC, name ASC
        LIMIT :limit
        """
    )
    fun observeOfflineLibrary(limit: Int): Flow<List<HeroEntity>>

    @Query("SELECT * FROM heroes WHERE id = :id LIMIT 1")
    suspend fun getHeroById(id: Int): HeroEntity?

    @Query("SELECT COUNT(*) FROM heroes")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(heroes: List<HeroEntity>)

    @Query("UPDATE heroes SET lastSeenAtMillis = :now WHERE id = :id")
    suspend fun touchSeen(id: Int, now: Long)

    @Query("UPDATE heroes SET pinned = :pinned WHERE id = :id")
    suspend fun setPinned(id: Int, pinned: Boolean)

    @Query(
        """
        DELETE FROM heroes
        WHERE id IN (
            SELECT id FROM heroes
            WHERE pinned = 0
            ORDER BY sortIndex ASC
            LIMIT :deleteCount
        )
        """
    )
    suspend fun deleteOldestNonPinned(deleteCount: Int)
}