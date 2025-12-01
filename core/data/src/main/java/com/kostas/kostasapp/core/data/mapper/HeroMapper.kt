package com.kostas.kostasapp.core.data.mapper

import com.kostas.kostasapp.core.model.Hero
import com.kostas.kostasapp.core.network.model.HeroDto

fun HeroDto.toDomain(): Hero =
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