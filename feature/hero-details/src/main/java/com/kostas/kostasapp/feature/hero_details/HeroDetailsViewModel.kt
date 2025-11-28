package com.kostas.kostasapp.feature.hero_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kostas.kostasapp.core.domain.repository.SquadRepository
import com.kostas.kostasapp.core.domain.usecase.GetHeroDetailsUseCase
import com.kostas.kostasapp.core.domain.usecase.ObserveSquadUseCase
import com.kostas.kostasapp.core.domain.usecase.ToggleSquadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HeroDetailsViewModel @Inject constructor(
    private val getHeroDetails: GetHeroDetailsUseCase,
    private val squadRepository: SquadRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HeroDetailsUiState())
    val uiState = _uiState.asStateFlow()

    fun loadHero(id: Int) {
        viewModelScope.launch {
            val hero = getHeroDetails(id)
            val recruited = squadRepository.isInSquad(id)
            _uiState.value = HeroDetailsUiState(hero, recruited)
        }
    }

    fun hire() {
        uiState.value.hero?.let { hero ->
            viewModelScope.launch {
                squadRepository.addToSquad(hero)
                _uiState.update { it.copy(isInSquad = true) }
            }
        }
    }

    fun fire() {
        uiState.value.hero?.let { hero ->
            viewModelScope.launch {
                squadRepository.removeFromSquad(hero)
                _uiState.update { it.copy(isInSquad = false) }
            }
        }
    }
}