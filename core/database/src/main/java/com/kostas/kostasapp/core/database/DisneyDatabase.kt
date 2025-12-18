package com.kostas.kostasapp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kostas.kostasapp.core.database.converters.HeroTypeConverters
import com.kostas.kostasapp.core.database.dao.HeroDao
import com.kostas.kostasapp.core.database.dao.HeroRemoteKeysDao
import com.kostas.kostasapp.core.database.entity.HeroEntity
import com.kostas.kostasapp.core.database.entity.HeroRemoteKeysEntity

@Database(
    entities = [HeroEntity::class, HeroRemoteKeysEntity::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(HeroTypeConverters::class)
abstract class DisneyDatabase : RoomDatabase() {
    abstract fun heroDao(): HeroDao
    abstract fun heroRemoteKeysDao(): HeroRemoteKeysDao
}