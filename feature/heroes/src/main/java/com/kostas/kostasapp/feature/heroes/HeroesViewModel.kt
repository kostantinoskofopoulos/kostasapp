package com.kostas.kostasapp.feature.heroes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kostas.common.logging.Logger
import com.kostas.kostasapp.core.domain.usecase.GetPagedHeroesUseCase
import com.kostas.kostasapp.core.domain.usecase.ObserveSquadUseCase
import com.kostas.kostasapp.core.model.Hero
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HeroesViewModel @Inject constructor(
    getPagedHeroesUseCase: GetPagedHeroesUseCase,
    private val observeSquadUseCase: ObserveSquadUseCase,
    private val logger: Logger
) : ViewModel() {

    private val tag = "HeroesViewModel"

    val heroesPaging: Flow<PagingData<Hero>> =
        getPagedHeroesUseCase().cachedIn(viewModelScope)

    private val _uiState = MutableStateFlow(
        HeroesUiState(isSquadLoading = true)
    )
    val uiState: StateFlow<HeroesUiState> = _uiState.asStateFlow()

    init {
        logger.d(tag, "init: observing squad")
        observeSquad()
    }

    private fun observeSquad() {
        viewModelScope.launch {
            observeSquadUseCase()
                .catch { throwable ->
                    logger.e(tag, "Error observing squad", throwable)
                    _uiState.update {
                        it.copy(
                            isSquadLoading = false,
                            squadErrorMessage = throwable.message ?: "Failed to load squad."
                        )
                    }
                }
                .collect { squad ->
                    logger.d(tag, "Squad updated: size=${squad.size}")
                    _uiState.update {
                        it.copy(
                            squad = squad,
                            isSquadLoading = false,
                            squadErrorMessage = null
                        )
                    }
                }
        }
    }
}