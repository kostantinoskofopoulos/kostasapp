package com.kostas.kostasapp.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hero_remote_keys")
data class HeroRemoteKeysEntity(
    @PrimaryKey val heroId: Int,
    val prevKey: Int?,
    val nextKey: Int?
)