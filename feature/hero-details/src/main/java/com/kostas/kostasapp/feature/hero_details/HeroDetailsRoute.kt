package com.kostas.kostasapp.feature.hero_details

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HeroDetailsRoute(
    heroId: Int,
    onBack: () -> Unit,
    viewModel: HeroDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(heroId) {
        viewModel.loadHero(heroId)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HeroDetailsEvent.ShowErrorSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(event.messageRes)
                    )
                }
            }
        }
    }

    HeroDetailsScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onRecruitClick = viewModel::onRecruitClick,
        onFireClick = viewModel::onFireClick,
        onFireConfirm = viewModel::onFireConfirm,
        onFireDismiss = viewModel::onFireDismiss
    )
}