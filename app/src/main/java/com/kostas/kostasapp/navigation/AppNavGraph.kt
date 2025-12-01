package com.kostas.kostasapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kostas.kostasapp.feature.hero_details.HeroDetailsRoute
import com.kostas.kostasapp.feature.heroes.HeroesRoute
import kotlinx.serialization.Serializable

@Serializable
object HeroesScreen

@Serializable
data class HeroDetailsScreen(val heroId: Int)

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = HeroesScreen
    ) {

        composable<HeroesScreen> {
            HeroesRoute(
                onHeroClick = { id ->
                    navController.navigate(HeroDetailsScreen(id)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<HeroDetailsScreen> {
            HeroDetailsRoute(
                onBack = { navController.navigateUp() }
            )
        }
    }
}