package com.kostas.kostasapp.feature.hero_details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HeroDetailsRoute(
    heroId: Int,
    onBack: () -> Unit,
    viewModel: HeroDetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(heroId) {
        viewModel.load(heroId)
    }

    val uiState = viewModel.uiState.collectAsState()

    HeroDetailsScreen(
        uiState = uiState.value,
        onBack = onBack,
        onRecruitClick = viewModel::onRecruitClick,
        onFireClick = viewModel::onFireClick,
        onFireConfirm = viewModel::onFireConfirm,
        onFireDismiss = viewModel::onFireDismiss
    )
}