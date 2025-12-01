package com.kostas.kostasapp.feature.heroes

import com.kostas.kostasapp.core.model.Hero

data class HeroesUiState(
    val squad: List<Hero> = emptyList(),
    val isSquadLoading: Boolean = false,
    val squadErrorMessage: String? = null
)