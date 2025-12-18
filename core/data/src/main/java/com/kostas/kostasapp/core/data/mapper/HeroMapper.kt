package com.kostas.kostasapp.core.data.mapper

import com.kostas.kostasapp.core.database.entity.HeroEntity
import com.kostas.kostasapp.core.model.Hero
import com.kostas.kostasapp.core.network.model.HeroDto

fun HeroDto.toDomain(): Hero =
    Hero(
        id = id,
        name = name?.trim(),
        imageUrl = imageUrl?.takeIf { it.isNotBlank() },
        sourceUrl = sourceUrl?.takeIf { it.isNotBlank() },
        films = films.orEmpty(),
        tvShows = tvShows.orEmpty(),
        videoGames = videoGames.orEmpty(),
        allies = allies.orEmpty(),
        enemies = enemies.orEmpty()
    )

fun HeroEntity.toDomain(): Hero =
    Hero(
        id = id,
        name = name,
        imageUrl = imageUrl,
        sourceUrl = sourceUrl,
        films = films,
        tvShows = tvShows,
        videoGames = videoGames,
        allies = allies,
        enemies = enemies
    )

fun HeroDto.toEntity(
    sortIndex: Long,
    fetchedAtMillis: Long
): HeroEntity =
    HeroEntity(
        id = id,
        name = name?.trim(),
        imageUrl = imageUrl?.takeIf { it.isNotBlank() },
        sourceUrl = sourceUrl?.takeIf { it.isNotBlank() },
        films = films.orEmpty(),
        tvShows = tvShows.orEmpty(),
        videoGames = videoGames.orEmpty(),
        allies = allies.orEmpty(),
        enemies = enemies.orEmpty(),
        sortIndex = sortIndex,
        lastFetchedAtMillis = fetchedAtMillis
    )