package com.kostas.kostasapp.feature.hero_details

import com.kostas.kostasapp.core.model.Hero

data class HeroDetailsUiState(
    val hero: Hero? = null,
    val isInSquad: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val showFireConfirmDialog: Boolean = false
)
