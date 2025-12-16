package com.kostas.kostasapp.core.database

import android.content.Context
import androidx.room.Room
import com.kostas.kostasapp.core.database.dao.HeroDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDisneyDatabase(
        @ApplicationContext context: Context
    ): DisneyDatabase =
        Room.databaseBuilder(
            context,
            DisneyDatabase::class.java,
            "disney.db"
        ).build()

    @Provides
    fun provideHeroDao(
        db: DisneyDatabase
    ): HeroDao = db.heroDao()
}