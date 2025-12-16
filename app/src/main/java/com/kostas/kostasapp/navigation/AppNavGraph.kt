package com.kostas.kostasapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.kostas.common.logging.Logger
import com.kostas.kostasapp.feature.hero_details.HeroDetailsRoute
import com.kostas.kostasapp.feature.heroes.HeroesRoute


private const val NAV_TAG = "Navigation"
// Helper: pop
private fun NavBackStack<NavKey>.popNotRoot(
    logger: Logger,
    reason: String,
): Boolean {
    if (size <= 1) {
        logger.d(
            NAV_TAG,
            "popNotRoot($reason) ignored – at root (top=${lastOrNull()}, size=$size)"
        )
        return false
    }

    val removed = removeLastOrNull()
    logger.d(
        NAV_TAG,
        "popNotRoot($reason) -> removed=$removed, newTop=${lastOrNull()}, size=$size"
    )
    return true
}

@Composable
fun AppNavGraph(
    logger: Logger
) {
    val backStack: NavBackStack<NavKey> = rememberNavBackStack(HeroesScreen)

    NavDisplay(
        backStack = backStack,
        // System back (gesture / hardware back)
        onBack = {
            backStack.popNotRoot(logger, reason = "SystemBack")
        },
        entryDecorators = listOf(
            // saveable state per entry
            rememberSaveableStateHolderNavEntryDecorator(),
            // ViewModel scoping per entry
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {

            // ----------------- LIST SCREEN -----------------
            entry<HeroesScreen> {
                logger.d(
                    NAV_TAG,
                    "Showing HeroesScreen (stackSize=${backStack.size})"
                )

                HeroesRoute(
                    onHeroClick = { id ->
                        val destination = HeroDetailsScreen(heroId = id)
                        logger.d(
                            NAV_TAG,
                            "Navigate: HeroesScreen → $destination (beforeSize=${backStack.size})"
                        )
                        backStack.add(destination)
                        logger.d(
                            NAV_TAG,
                            "After navigate: top=${backStack.lastOrNull()}, size=${backStack.size}"
                        )
                    }
                )
            }

            // ----------------- DETAILS SCREEN -----------------
            entry<HeroDetailsScreen> { key ->
                logger.d(
                    NAV_TAG,
                    "Showing HeroDetailsScreen(id=${key.heroId}) (stackSize=${backStack.size})"
                )

                HeroDetailsRoute(
                    heroId = key.heroId,
                    onBack = {
                        backStack.popNotRoot(logger, reason = "HeroDetailsRoute onBack")
                    }
                )
            }
        }
    )
}