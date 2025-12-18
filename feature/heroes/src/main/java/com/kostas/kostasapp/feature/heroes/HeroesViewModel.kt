package com.kostas.kostasapp.feature.heroes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kostas.common.logging.Logger
import com.kostas.kostasapp.core.domain.usecase.GetPagedHeroesUseCase
import com.kostas.kostasapp.core.domain.usecase.ObserveConnectivityUseCase
import com.kostas.kostasapp.core.domain.usecase.ObserveSquadUseCase
import com.kostas.kostasapp.core.model.Hero
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class HeroesViewModel @Inject constructor(
    private val getPagedHeroesUseCase: GetPagedHeroesUseCase,
    private val observeSquadUseCase: ObserveSquadUseCase,
    private val observeConnectivityUseCase: ObserveConnectivityUseCase,
    private val logger: Logger
) : ViewModel() {

    private val tag = "HeroesViewModel"


    private val _pagingRetry = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val pagingRetry: SharedFlow<Unit> = _pagingRetry.asSharedFlow()

    val heroesPaging: Flow<PagingData<Hero>> =
        getPagedHeroesUseCase()
            .cachedIn(viewModelScope)

    private val _uiState = MutableStateFlow(HeroesUiState(isSquadLoading = true))
    val uiState: StateFlow<HeroesUiState> = _uiState.asStateFlow()

    init {
        observeSquad()
        autoRetryWhenBackOnline()
    }

    private fun autoRetryWhenBackOnline() {
        viewModelScope.launch {
            observeConnectivityUseCase()
                .distinctUntilChanged()
                .catch { t -> logger.e(tag, "Connectivity observe failed", t) }
                .pairwise() // helper παρακάτω
                .collect { (prev, now) ->
                    if (prev == false && now == true) {
                        logger.d(tag, "Back online -> paging retry")
                        _pagingRetry.tryEmit(Unit)
                    }
                }
        }
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

private fun <T> Flow<T>.pairwise(): Flow<Pair<T?, T>> = flow {
    var prev: T? = null
    collect { current ->
        emit(prev to current)
        prev = current
    }
}