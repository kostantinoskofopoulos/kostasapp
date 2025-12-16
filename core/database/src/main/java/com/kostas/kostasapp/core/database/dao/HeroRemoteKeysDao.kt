package com.kostas.kostasapp.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kostas.kostasapp.core.database.entity.HeroRemoteKeysEntity

@Dao
interface HeroRemoteKeysDao {

    @Query("SELECT * FROM hero_remote_keys WHERE heroId = :heroId")
    suspend fun remoteKeysByHeroId(heroId: Int): HeroRemoteKeysEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<HeroRemoteKeysEntity>)

    @Query("DELETE FROM hero_remote_keys")
    suspend fun clearRemoteKeys()
}