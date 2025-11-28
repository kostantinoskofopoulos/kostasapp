package com.kostas.kostasapp.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Purple = Color(0xFF6200EE)
private val PurpleDark = Color(0xFF3700B3)
private val SquadRed = Color(0xFFD32F2F)

private val LightColors = lightColorScheme(
    primary = Purple,
    onPrimary = Color.White,
    secondary = PurpleDark,
    error = SquadRed
)

@Composable
fun SuperheroAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = MaterialTheme.typography,
        content = content
    )
}