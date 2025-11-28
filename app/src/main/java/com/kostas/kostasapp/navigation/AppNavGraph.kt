package com.kostas.kostasapp.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.kostas.kostasapp.feature.hero_details.HeroDetailsRoute
import com.kostas.kostasapp.feature.heroes.HeroesViewModel
import com.kostas.kostasapp.feature.heroes.HeroesRoute
import kotlinx.serialization.Serializable



// ---------- NAVIGATION ROUTES ----------
@Serializable
object HeroesScreen

@Serializable
data class HeroDetailsScreen(val heroId: Int)

// ---------- ROOT NAVGRAPH ----------
@Composable
fun AppNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = HeroesScreen
    ) {

        // HEROES LIST
        composable<HeroesScreen> {
            val vm = hiltViewModel<HeroesViewModel>()
            val uiState = vm.uiState.collectAsState()

            HeroesRoute(
                heroes = uiState.value.heroes,
                onHeroClick = { id -> navController.navigate(HeroDetailsScreen(id)) }
            )
        }

        // HERO DETAILS
        composable<HeroDetailsScreen> { entry ->
            val args = entry.toRoute<HeroDetailsScreen>()

            HeroDetailsRoute(
                heroId = args.heroId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

private fun Any.collectAsState() {
    TODO("Not yet implemented")
}
