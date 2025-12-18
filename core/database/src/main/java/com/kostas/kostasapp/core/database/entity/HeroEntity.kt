package com.kostas.kostasapp.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kostas.kostasapp.core.database.CATALOG_SENTINEL_SORT_INDEX

@Entity(tableName = "heroes")
data class HeroEntity(
    @PrimaryKey val id: Int,
    val name: String?,
    val imageUrl: String?,
    val sourceUrl: String?,
    val films: List<String> = emptyList(),
    val tvShows: List<String> = emptyList(),
    val videoGames: List<String> = emptyList(),
    val allies: List<String> = emptyList(),
    val enemies: List<String> = emptyList(),

    val sortIndex: Long = CATALOG_SENTINEL_SORT_INDEX,
    val lastFetchedAtMillis: Long = 0L,
    val lastSeenAtMillis: Long = 0L,
    val pinned: Boolean = false
)