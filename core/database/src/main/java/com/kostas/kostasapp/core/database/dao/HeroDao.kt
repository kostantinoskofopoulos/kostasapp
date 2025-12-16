// core/database/.../dao/HeroDao.kt
package com.kostas.kostasapp.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kostas.kostasapp.core.database.entity.HeroEntity

@Dao
interface HeroDao {

    @Query(
        """
        SELECT * FROM heroes
        WHERE (:nameQuery IS NULL OR name LIKE '%' || :nameQuery || '%')
        ORDER BY name
        """
    )
    fun pagingSource(nameQuery: String?): PagingSource<Int, HeroEntity>

    @Query("SELECT * FROM heroes WHERE id = :id LIMIT 1")
    suspend fun getHeroById(id: Int): HeroEntity?

    @Query("SELECT COUNT(*) FROM heroes")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(heroes: List<HeroEntity>)

    @Query("DELETE FROM heroes")
    suspend fun clearAll()
}