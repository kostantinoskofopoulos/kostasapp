package com.kostas.kostasapp.feature.hero_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kostas.kostasapp.core.domain.repository.SquadRepository
import com.kostas.kostasapp.core.domain.usecase.GetHeroDetailsUseCase
import com.kostas.kostasapp.core.domain.usecase.ToggleSquadUseCase
import com.kostas.kostasapp.core.model.Hero
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HeroDetailsViewModel @Inject constructor(
    private val getHeroDetails: GetHeroDetailsUseCase,
    private val toggleSquad: ToggleSquadUseCase,
    private val squadRepository: SquadRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HeroDetailsUiState())
    val uiState: StateFlow<HeroDetailsUiState> = _uiState.asStateFlow()

    fun load(heroId: Int) {
        if (_uiState.value.hero?.id == heroId && !_uiState.value.isLoading) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val hero = getHeroDetails(heroId)
                val inSquad = squadRepository.isInSquad(heroId)

                _uiState.update {
                    it.copy(
                        hero = hero,
                        isInSquad = inSquad,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = t.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun onRecruitClick() {
        val hero = _uiState.value.hero ?: return
        viewModelScope.launch {
            toggleSquad(hero)
            _uiState.update { it.copy(isInSquad = true) }
        }
    }

    fun onFireClick() {
        // just show confirm dialog
        _uiState.update { it.copy(showFireConfirmDialog = true) }
    }

    fun onFireConfirm() {
        val hero = _uiState.value.hero ?: return
        viewModelScope.launch {
            toggleSquad(hero)
            _uiState.update {
                it.copy(
                    isInSquad = false,
                    showFireConfirmDialog = false
                )
            }
        }
    }

    fun onFireDismiss() {
        _uiState.update { it.copy(showFireConfirmDialog = false) }
    }
}