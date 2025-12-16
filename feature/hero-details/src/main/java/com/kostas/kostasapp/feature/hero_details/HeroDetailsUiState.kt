package com.kostas.kostasapp.feature.hero_details

import androidx.annotation.StringRes
import com.kostas.kostasapp.core.model.Hero

data class HeroDetailsSection(
    @field:StringRes @param:StringRes val titleRes: Int,
    val values: List<String>
)

data class HeroDetailsUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val hero: Hero? = null,
    val isInSquad: Boolean = false,
    val showFireConfirmDialog: Boolean = false,
    val sections: List<HeroDetailsSection> = emptyList()
)

sealed interface HeroDetailsEvent {
    data class ShowErrorSnackbar(
        @field:StringRes @param:StringRes val messageRes: Int
    ) : HeroDetailsEvent
}