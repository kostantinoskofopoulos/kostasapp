package com.kostas.kostasapp.feature.heroes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
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
    private val observeSquadUseCase: ObserveSquadUseCase
) : ViewModel() {

    val heroesPaging: Flow<PagingData<Hero>> =
        getPagedHeroesUseCase().cachedIn(viewModelScope)

    private val _uiState = MutableStateFlow(
        HeroesUiState(isSquadLoading = true)
    )
    val uiState: StateFlow<HeroesUiState> = _uiState.asStateFlow()

    init {
        observeSquad()
    }

    private fun observeSquad() {
        viewModelScope.launch {
            observeSquadUseCase()
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isSquadLoading = false,
                            squadErrorMessage = throwable.message ?: "Failed to load squad."
                        )
                    }
                }
                .collect { squad ->
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