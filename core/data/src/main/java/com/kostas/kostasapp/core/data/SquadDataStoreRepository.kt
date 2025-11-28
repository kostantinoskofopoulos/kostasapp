package com.kostas.kostasapp.core.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.kostas.kostasapp.core.domain.repository.SquadRepository
import com.kostas.kostasapp.core.model.Hero
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

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

    override val squad: Flow<List<Hero>> =
        context.squadDataStore.data.map { prefs ->
            val set = prefs[SQUAD_HEROES_KEY] ?: emptySet()
            set.mapNotNull { json ->
                runCatching { gson.fromJson(json, Hero::class.java) }.getOrNull()
            }
        }

    override suspend fun addToSquad(hero: Hero) {
        context.squadDataStore.edit { prefs ->
            val current = prefs[SQUAD_HEROES_KEY] ?: emptySet()
            if (current.any { it.contains("\"id\":${hero.id}") }) return@edit
            val updated = current + gson.toJson(hero)
            prefs[SQUAD_HEROES_KEY] = updated
        }
    }

    override suspend fun removeFromSquad(hero: Hero) {
        context.squadDataStore.edit { prefs ->
            val current = prefs[SQUAD_HEROES_KEY] ?: emptySet()
            val updated = current.filterNot { it.contains("\"id\":${hero.id}") }.toSet()
            prefs[SQUAD_HEROES_KEY] = updated
        }
    }

    override suspend fun isInSquad(heroId: Int): Boolean {
        val prefs = context.squadDataStore.data
            .map { it[SQUAD_HEROES_KEY] ?: emptySet() }
            .firstOrNull() ?: emptySet()

        return prefs.any { it.contains("\"id\":$heroId") }
    }
}