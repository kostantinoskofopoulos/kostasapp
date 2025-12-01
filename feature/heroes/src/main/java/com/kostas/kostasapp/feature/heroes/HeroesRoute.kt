package com.kostas.kostasapp.feature.heroes

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems

@Composable
fun HeroesRoute(
    onHeroClick: (Int) -> Unit,
    viewModel: HeroesViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val heroes = viewModel.heroesPaging.collectAsLazyPagingItems()

    HeroesScreen(
        uiState = uiState,
        heroes = heroes,
        onHeroClick = onHeroClick
    )
}