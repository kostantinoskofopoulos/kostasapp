package com.kostas.kostasapp.core.domain.repository

import com.kostas.kostasapp.core.model.Hero
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing the user's squad.
 *
 * The implementation is responsible for persistence
 * (e.g. DataStore, database, etc.), while the domain/UI
 * layers depend only on this abstraction.
 */
interface SquadRepository {

    /**
     * A [Flow] that emits the current squad as a list of [Hero]s.
     *
     * The list is typically kept in a stable order (e.g. sorted by name)
     * by the implementation, so UI can safely render it directly.
     */
    val squad: Flow<List<Hero>>

    /**
     * Adds the given [hero] to the squad.
     *
     * If the hero is already in the squad, the implementation
     * should behave as a no-op.
     */
    suspend fun addToSquad(hero: Hero)

    /**
     * Removes the given [hero] from the squad.
     *
     * If the hero is not in the squad, the implementation
     * should behave as a no-op.
     */
    suspend fun removeFromSquad(hero: Hero)

    /**
     * Checks if a hero with the given [heroId] is currently in the squad.
     *
     * @return true if the hero is in the squad, false otherwise.
     */
    suspend fun isInSquad(heroId: Int): Boolean

    /**
     * Atomically toggles the given [hero] in the squad.
     *
     * @return true if the hero is now in the squad, false if they were removed.
     */
    suspend fun toggle(hero: Hero): Boolean
}