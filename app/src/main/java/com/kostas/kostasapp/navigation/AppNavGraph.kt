package com.kostas.kostasapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import com.kostas.kostasapp.feature.hero_details.HeroDetailsRoute
import com.kostas.kostasapp.feature.heroes.HeroesRoute
import com.kostas.kostasapp.feature.heroes.HeroesViewModel
import kotlinx.serialization.Serializable

// ---------- NAV ROUTES ----------
@Serializable
object HeroesScreen

@Serializable
data class HeroDetailsScreen(val heroId: Int)

// ---------- ROOT GRAPH ----------
@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = HeroesScreen
    ) {

        // LIST SCREEN
        composable<HeroesScreen> {
            val viewModel: HeroesViewModel = hiltViewModel()

            val uiState by viewModel.uiState.collectAsState()
            val heroesPaging = viewModel.heroesPaging.collectAsLazyPagingItems()

            HeroesRoute(
                uiState = uiState,
                heroes = heroesPaging,
                onHeroClick = { id ->
                    navController.navigate(HeroDetailsScreen(id)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // DETAILS SCREEN
        composable<HeroDetailsScreen> {
            HeroDetailsRoute(
                onBack = { navController.navigateUp() }
            )
        }
    }
}