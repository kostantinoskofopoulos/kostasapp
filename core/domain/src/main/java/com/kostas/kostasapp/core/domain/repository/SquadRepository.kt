package com.kostas.kostasapp.core.domain.repository

import com.kostas.kostasapp.core.model.Hero
import kotlinx.coroutines.flow.Flow

interface SquadRepository {

    val squad: Flow<List<Hero>>

    suspend fun addToSquad(hero: Hero)

    suspend fun removeFromSquad(hero: Hero)

    suspend fun isInSquad(heroId: Int): Boolean
}