package com.kostas.kostasapp.feature.heroes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.kostas.kostasapp.core.domain.repository.SquadRepository
import com.kostas.kostasapp.core.domain.usecase.GetHeroDetailsUseCase
import com.kostas.kostasapp.core.domain.usecase.GetPagedHeroesUseCase
import com.kostas.kostasapp.core.domain.usecase.ObserveSquadUseCase
import com.kostas.kostasapp.core.model.Hero
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HeroesViewModel @Inject constructor(
    private val getPagedHeroes: GetPagedHeroesUseCase,
    observeSquad: ObserveSquadUseCase
) : ViewModel() {

    val squad = observeSquad().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    val heroesPaging = getPagedHeroes()
        .cachedIn(viewModelScope)
}