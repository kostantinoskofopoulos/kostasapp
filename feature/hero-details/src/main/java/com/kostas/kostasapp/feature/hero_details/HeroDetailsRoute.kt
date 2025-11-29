package com.kostas.kostasapp.feature.hero_details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HeroDetailsScreen(
        uiState = uiState,
        onBack = onBack,
        onRecruitClick = viewModel::onRecruitClick,
        onFireClick = viewModel::onFireClick,
        onFireConfirm = viewModel::onFireConfirm,
        onFireDismiss = viewModel::onFireDismiss
    )
}