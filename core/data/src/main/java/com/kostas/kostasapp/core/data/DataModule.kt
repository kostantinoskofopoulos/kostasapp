package com.kostas.kostasapp.core.data

import com.kostas.kostasapp.core.domain.repository.ConnectivityRepository
import com.kostas.kostasapp.core.domain.repository.HeroesRepository
import com.kostas.kostasapp.core.domain.repository.SquadRepository
import com.kostas.kostasapp.core.domain.usecase.GetHeroDetailsUseCase
import com.kostas.kostasapp.core.domain.usecase.GetPagedHeroesUseCase
import com.kostas.kostasapp.core.domain.usecase.ObserveConnectivityUseCase
import com.kostas.kostasapp.core.domain.usecase.ObserveSquadUseCase
import com.kostas.kostasapp.core.domain.usecase.ToggleSquadUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideHeroesRepository(
        impl: HeroesRepositoryImpl
    ): HeroesRepository = impl

    @Provides
    @Singleton
    fun provideSquadRepository(
        impl: SquadDataStoreRepository
    ): SquadRepository = impl

    @Provides
    @Singleton
    fun provideConnectivityRepository(
        impl: ConnectivityRepositoryImpl
    ): ConnectivityRepository = impl

    @Provides
    fun provideGetPagedHeroesUseCase(repo: HeroesRepository) =
        GetPagedHeroesUseCase(repo)

    @Provides
    fun provideGetHeroDetailsUseCase(repo: HeroesRepository) =
        GetHeroDetailsUseCase(repo)

    @Provides
    fun provideToggleSquadUseCase(squadRepository: SquadRepository) =
        ToggleSquadUseCase(squadRepository)

    @Provides
    fun provideObserveSquadUseCase(squadRepository: SquadRepository) =
        ObserveSquadUseCase(squadRepository)

    @Provides
    fun provideObserveConnectivityUseCase(repo: ConnectivityRepository) =
        ObserveConnectivityUseCase(repo)
}