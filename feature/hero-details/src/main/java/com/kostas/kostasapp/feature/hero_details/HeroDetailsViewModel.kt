package com.kostas.kostasapp.feature.hero_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kostas.kostasapp.core.domain.repository.SquadRepository
import com.kostas.kostasapp.core.domain.usecase.GetHeroDetailsUseCase
import com.kostas.kostasapp.core.domain.usecase.ToggleSquadUseCase
import com.kostas.kostasapp.core.model.Hero
import com.kostas.kostasapp.hero_details.R
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
    private val toggleSquad: ToggleSquadUseCase,
    private val squadRepository: SquadRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val heroId: Int = savedStateHandle.get<Int>("heroId")
        ?: error("heroId is required")

    private val _uiState = MutableStateFlow(HeroDetailsUiState())
    val uiState: StateFlow<HeroDetailsUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        if (_uiState.value.hero?.id == heroId && !_uiState.value.isLoading) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val hero = getHeroDetails(heroId)
                val inSquad = squadRepository.isInSquad(heroId)
                val sections = buildSections(hero)

                _uiState.update {
                    it.copy(
                        hero = hero,
                        isInSquad = inSquad,
                        sections = sections,
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

    private fun buildSections(hero: Hero): List<HeroDetailsSection> {
        return listOf(
            HeroDetailsSection(
                titleRes = R.string.hero_details_section_films,
                values = hero.films
            ),
            HeroDetailsSection(
                titleRes = R.string.hero_details_section_tv_shows,
                values = hero.tvShows
            ),
            HeroDetailsSection(
                titleRes = R.string.hero_details_section_video_games,
                values = hero.videoGames
            ),
            HeroDetailsSection(
                titleRes = R.string.hero_details_section_allies,
                values = hero.allies
            ),
            HeroDetailsSection(
                titleRes = R.string.hero_details_section_enemies,
                values = hero.enemies
            )
        ).filter { it.values.isNotEmpty() }
    }

    fun onRecruitClick() {
        val hero = _uiState.value.hero ?: return
        viewModelScope.launch {
            toggleSquad(hero)
            _uiState.update { it.copy(isInSquad = true) }
        }
    }

    fun onFireClick() {
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