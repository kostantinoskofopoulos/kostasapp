package com.kostas.kostasapp.core.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.kostas.kostasapp.core.domain.repository.SquadRepository
import com.kostas.kostasapp.core.model.Hero
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private const val SQUAD_DATASTORE_NAME = "squad_prefs"
private val SQUAD_HEROES_KEY = stringSetPreferencesKey("squad_heroes")

private val Context.squadDataStore by preferencesDataStore(
    name = SQUAD_DATASTORE_NAME
)

@Singleton
class SquadDataStoreRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : SquadRepository {

    private val gson = Gson()

    private fun decodeSquad(raw: Set<String>): List<Hero> =
        raw.mapNotNull { json ->
            runCatching { gson.fromJson(json, Hero::class.java) }.getOrNull()
        }

    private fun encodeSquad(heroes: List<Hero>): Set<String> =
        heroes.map { gson.toJson(it) }.toSet()

    override val squad: Flow<List<Hero>> =
        context.squadDataStore.data
            .map { prefs ->
                val rawSet = prefs[SQUAD_HEROES_KEY] ?: emptySet()
                decodeSquad(rawSet)
            }

    private suspend fun updateSquad(
        transform: (List<Hero>) -> List<Hero>
    ) {
        context.squadDataStore.edit { prefs ->
            val currentRaw = prefs[SQUAD_HEROES_KEY] ?: emptySet()
            val currentHeroes = decodeSquad(currentRaw)
            val updated = transform(currentHeroes)
            prefs[SQUAD_HEROES_KEY] = encodeSquad(updated)
        }
    }

    override suspend fun addToSquad(hero: Hero) {
        updateSquad { current ->
            if (current.any { it.id == hero.id }) current else current + hero
        }
    }

    override suspend fun removeFromSquad(hero: Hero) {
        updateSquad { current ->
            current.filterNot { it.id == hero.id }
        }
    }

    override suspend fun isInSquad(heroId: Int): Boolean {
        val heroes = squad.firstOrNull().orEmpty()
        return heroes.any { it.id == heroId }
    }
}