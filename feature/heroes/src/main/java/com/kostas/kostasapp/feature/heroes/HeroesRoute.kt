package com.kostas.kostasapp.feature.heroes

import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems
import com.kostas.kostasapp.core.model.Hero

@Composable
fun HeroesRoute(
    uiState: HeroesUiState,
    heroes: LazyPagingItems<Hero>,
    onHeroClick: (Int) -> Unit
) {
    HeroesScreen(
        uiState = uiState,
        heroes = heroes,
        onHeroClick = onHeroClick
    )
}
