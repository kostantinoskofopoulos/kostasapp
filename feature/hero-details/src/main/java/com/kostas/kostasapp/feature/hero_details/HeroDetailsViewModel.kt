package com.kostas.kostasapp.feature.hero_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kostas.common.logging.Logger
import com.kostas.kostasapp.core.domain.repository.SquadRepository
import com.kostas.kostasapp.core.domain.usecase.GetHeroDetailsUseCase
import com.kostas.kostasapp.core.domain.usecase.ToggleSquadUseCase
import com.kostas.kostasapp.core.model.Hero
import com.kostas.kostasapp.hero_details.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HeroDetailsViewModel @Inject constructor(
    private val getHeroDetails: GetHeroDetailsUseCase,
    private val toggleSquad: ToggleSquadUseCase,
    private val squadRepository: SquadRepository,
    private val logger: Logger
) : ViewModel() {

    private val tag = "HeroDetailsViewModel"

    private val _uiState = MutableStateFlow(HeroDetailsUiState())
    val uiState: StateFlow<HeroDetailsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HeroDetailsEvent>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val events: SharedFlow<HeroDetailsEvent> = _events.asSharedFlow()

    private var currentHeroId: Int? = null

    fun loadHero(heroId: Int, forceRefresh: Boolean = false) {
        if (!forceRefresh && heroId == currentHeroId && _uiState.value.hero != null) return
        currentHeroId = heroId

        viewModelScope.launch {
            _uiState.setLoading()
            logger.d(tag, "Loading hero details id=$heroId")

            runCatching {
                coroutineScope {
                    val heroDeferred = async { getHeroDetails(heroId) }
                    val isInSquadDeferred = async { squadRepository.isInSquad(heroId) }

                    val hero = heroDeferred.await()
                    val isInSquad = isInSquadDeferred.await()
                    hero to isInSquad
                }
            }.onSuccess { (hero, isInSquad) ->
                logger.d(tag, "Hero details loaded id=$heroId isInSquad=$isInSquad")
                _uiState.update {
                    it.copy(
                        hero = hero,
                        isInSquad = isInSquad,
                        sections = hero.toSections(),
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                logger.e(tag, "Error loading hero details id=$heroId", throwable)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Unknown error"
                    )
                }
                _events.emit(
                    HeroDetailsEvent.ShowErrorSnackbar(
                        R.string.hero_details_generic_error
                    )
                )
            }
        }
    }

    fun onRecruitClick() {
        logger.d(tag, "onRecruitClick")
        toggleSquadState()
    }

    fun onFireClick() {
        logger.d(tag, "onFireClick -> show dialog")
        _uiState.update { it.copy(showFireConfirmDialog = true) }
    }

    fun onFireConfirm() {
        logger.d(tag, "onFireConfirm -> toggle squad")
        _uiState.update { it.copy(showFireConfirmDialog = false) }
        toggleSquadState()
    }

    fun onFireDismiss() {
        logger.d(tag, "onFireDismiss")
        _uiState.update { it.copy(showFireConfirmDialog = false) }
    }

    private fun toggleSquadState() {
        val hero = _uiState.value.hero ?: return

        viewModelScope.launch {
            runCatching { toggleSquad(hero) }
                .onSuccess { isNowInSquad ->
                    logger.d(tag, "Squad toggled for hero id=${hero.id}, isInSquad=$isNowInSquad")
                    _uiState.update { state ->
                        state.copy(isInSquad = isNowInSquad)
                    }
                }
                .onFailure { throwable ->
                    logger.e(tag, "Error toggling squad for hero id=${hero.id}", throwable)
                    _events.emit(
                        HeroDetailsEvent.ShowErrorSnackbar(
                            R.string.hero_details_toggle_squad_error
                        )
                    )
                }
        }
    }

    private fun MutableStateFlow<HeroDetailsUiState>.setLoading() {
        update { it.copy(isLoading = true, errorMessage = null) }
    }

    private fun Hero.toSections(): List<HeroDetailsSection> =
        buildList {
            addSection(R.string.hero_details_section_films, films)
            addSection(R.string.hero_details_section_tv_shows, tvShows)
            addSection(R.string.hero_details_section_video_games, videoGames)
            addSection(R.string.hero_details_section_allies, allies)
            addSection(R.string.hero_details_section_enemies, enemies)
        }

    private fun MutableList<HeroDetailsSection>.addSection(
        @androidx.annotation.StringRes titleRes: Int,
        values: List<String>
    ) {
        if (values.isNotEmpty()) {
            add(
                HeroDetailsSection(
                    titleRes = titleRes,
                    values = values
                )
            )
        }
    }
}