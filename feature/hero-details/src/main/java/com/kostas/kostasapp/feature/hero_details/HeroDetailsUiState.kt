package com.kostas.kostasapp.feature.hero_details

import com.kostas.kostasapp.core.model.Hero

data class HeroDetailsUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val hero: Hero? = null,
    val isInSquad: Boolean = false,
    val showFireConfirmDialog: Boolean = false
)