package com.kostas.kostasapp.feature.heroes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems

@Composable
fun HeroesRoute(
    viewModel: HeroesViewModel = hiltViewModel(),
    onHeroClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val heroes = viewModel.heroesPaging.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        viewModel.pagingRetry.collect {
            heroes.retry()
        }
    }

    HeroesScreen(
        uiState = uiState,
        heroes = heroes,
        onHeroClick = onHeroClick
    )
}