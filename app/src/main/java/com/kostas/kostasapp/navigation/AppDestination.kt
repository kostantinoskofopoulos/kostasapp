package com.kostas.kostasapp.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface AppDestination : NavKey

@Serializable
data object HeroesScreen : AppDestination

@Serializable
data class HeroDetailsScreen(val heroId: Int) : AppDestination