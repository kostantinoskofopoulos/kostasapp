package com.kostas.kostasapp.feature.hero_details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.kostas.kostasapp.core.model.Hero

@Composable
fun HeroDetailsRoute(
    heroId: Int,
    onBack: () -> Unit,
    viewModel: HeroDetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(heroId) {
        viewModel.loadHero(heroId)
    }

    val ui = viewModel.uiState.collectAsState().value

    ui.hero?.let { hero ->
        HeroDetailsScreen(
            hero = hero,
            isInSquad = ui.isInSquad,
            onBack = onBack,
            onHire = viewModel::hire,
            onFire = viewModel::fire
        )
    }
}

@Composable
private fun HeroDetailsScreen(
    hero: Hero,
    isInSquad: Boolean,
    onBack: () -> Unit,
    onHire: () -> Unit,
    onFire: () -> Unit
) {
    Column {
        AsyncImage(
            hero.imageUrl,
            hero.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        )

        IconButton(onClick = onBack) {
            Icon(Icons.Default.Close, contentDescription = "Back")
        }

        Text(
            hero.name.orEmpty(),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )

        if (!isInSquad) {
            Button(
                onClick = onHire,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Text("💪 Hire to Squad")
            }
        } else {
            Button(
                onClick = onFire,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE57373)
                ),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Text("🔥 Fire from Squad")
            }
        }

        Spacer(Modifier.height(16.dp))

        DetailsText(hero)
    }
}

@Composable
private fun DetailsText(hero: Hero) {
    Column(Modifier.padding(16.dp)) {
        if (hero.films.isNotEmpty()) Text("Films: ${hero.films.joinToString()}")
        if (hero.tvShows.isNotEmpty()) Text("Tv Shows: ${hero.tvShows.joinToString()}")
        if (hero.videoGames.isNotEmpty()) Text("Video Games: ${hero.videoGames.joinToString()}")
    }
}
