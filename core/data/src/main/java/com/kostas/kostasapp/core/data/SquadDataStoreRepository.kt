package com.kostas.kostasapp.core.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.kostas.common.coroutines.IoDispatcher
import com.kostas.common.logging.Logger
import com.kostas.kostasapp.core.database.dao.HeroDao
import com.kostas.kostasapp.core.database.entity.HeroEntity
import com.kostas.kostasapp.core.domain.repository.SquadRepository
import com.kostas.kostasapp.core.image.HeroImagePrefetcher
import com.kostas.kostasapp.core.model.Hero
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private const val SQUAD_DATASTORE_NAME = "squad_prefs"
private val SQUAD_HEROES_KEY = stringSetPreferencesKey("squad_heroes")

private val Context.squadDataStore by preferencesDataStore(name = SQUAD_DATASTORE_NAME)

@Singleton
class SquadDataStoreRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val imagePrefetcher: HeroImagePrefetcher,
    private val heroDao: HeroDao,
    private val logger: Logger,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SquadRepository {

    private val tag = "SquadRepository"
    private val gson = Gson()

    override val squad: Flow<List<Hero>> =
        context.squadDataStore.data
            .map { prefs ->
                val raw = prefs[SQUAD_HEROES_KEY] ?: emptySet()
                decodeSquad(raw)
            }
            .flowOn(ioDispatcher)
    override suspend fun addToSquad(hero: Hero): Unit = withContext(ioDispatcher) {
        val now = System.currentTimeMillis()

        updateSquad { current ->
            if (current.any { it.id == hero.id }) current else current + hero
        }


        upsertPinned(hero = hero, now = now, pinned = true)
        val url = hero.imageUrl
        if (!url.isNullOrBlank()) {
            imagePrefetcher.prefetch(url)
        }

        Unit
    }



    override suspend fun removeFromSquad(hero: Hero): Unit = withContext(ioDispatcher) {
        updateSquad { current -> current.filterNot { it.id == hero.id } }

        runCatching { heroDao.setPinned(hero.id, false) }
            .onFailure { logger.w(tag, "Failed to unpin heroId=${hero.id}", it) }

        Unit
    }

    override suspend fun isInSquad(heroId: Int): Boolean = withContext(ioDispatcher) {
        val heroes = squad.firstOrNull().orEmpty()
        heroes.any { it.id == heroId }
    }

    override suspend fun toggle(hero: Hero): Boolean = withContext(ioDispatcher) {
        val now = System.currentTimeMillis()
        var nowInSquad = false

        updateSquad { current ->
            val exists = current.any { it.id == hero.id }
            nowInSquad = !exists
            if (exists) current.filterNot { it.id == hero.id } else current + hero
        }

        upsertPinned(hero = hero, now = now, pinned = nowInSquad)

        if (nowInSquad) {
            hero.imageUrl?.let { imagePrefetcher.prefetch(it) }
        }

        nowInSquad
    }

    private suspend fun updateSquad(transform: (List<Hero>) -> List<Hero>) {
        context.squadDataStore.edit { prefs ->
            val currentRaw = prefs[SQUAD_HEROES_KEY] ?: emptySet()
            val current = decodeSquad(currentRaw)
            val updated = transform(current)
            prefs[SQUAD_HEROES_KEY] = encodeSquad(updated)
        }
    }

    private suspend fun upsertPinned(hero: Hero, now: Long, pinned: Boolean) {
        runCatching {
            val existing = heroDao.getHeroById(hero.id)
            val entity = hero.toPinnedEntity(
                now = now,
                sortIndex = existing?.sortIndex ?: Long.MAX_VALUE
            )
            heroDao.upsertAll(listOf(entity))
            heroDao.setPinned(hero.id, pinned)
        }.onFailure { logger.w(tag, "Failed to update pinned state heroId=${hero.id}", it) }
    }

    private fun decodeSquad(raw: Set<String>): List<Hero> =
        raw.mapNotNull { json ->
            runCatching { gson.fromJson(json, Hero::class.java) }.getOrNull()
        }

    private fun encodeSquad(heroes: List<Hero>): Set<String> =
        heroes.map { gson.toJson(it) }.toSet()

    private fun Hero.toPinnedEntity(now: Long, sortIndex: Long): HeroEntity =
        HeroEntity(
            id = id,
            name = name,
            imageUrl = imageUrl,
            sourceUrl = sourceUrl,
            films = films,
            tvShows = tvShows,
            videoGames = videoGames,
            allies = allies,
            enemies = enemies,
            sortIndex = sortIndex,
            lastFetchedAtMillis = now,
            lastSeenAtMillis = now,
            pinned = true
        )
}