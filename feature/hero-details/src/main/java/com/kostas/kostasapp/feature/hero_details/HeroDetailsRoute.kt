package com.kostas.kostasapp.feature.hero_details

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HeroDetailsRoute(
    onBack: () -> Unit,
    viewModel: HeroDetailsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    HeroDetailsScreen(
        uiState = uiState,
        onBack = onBack,
        onRecruitClick = viewModel::onRecruitClick,
        onFireClick = viewModel::onFireClick,
        onFireConfirm = viewModel::onFireConfirm,
        onFireDismiss = viewModel::onFireDismiss
    )
}