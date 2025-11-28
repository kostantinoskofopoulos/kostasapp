package com.kostas.kostasapp.feature.heroes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.kostas.kostasapp.core.model.Hero

@Composable
fun HeroesRoute(
    onHeroClick: (Int) -> Unit,
    viewModel: HeroesViewModel = hiltViewModel()
) {
    val squad by viewModel.squad.collectAsState()
    val heroes = viewModel.heroesPaging.collectAsLazyPagingItems()

    HeroesScreen(
        squad = squad,
        heroes = heroes,
        onHeroClick = onHeroClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HeroesScreen(
    squad: List<Hero>,
    heroes: androidx.paging.compose.LazyPagingItems<Hero>,
    onHeroClick: (Int) -> Unit
) {
    Column {
        TopAppBar(
            title = { Text("Superhero Squad Maker") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF6200EE),
                titleContentColor = Color.White
            )
        )

        if (squad.isNotEmpty()) {
            Text(
                "My Squad",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.titleMedium
            )

            Row(Modifier.padding(start = 12.dp)) {
                squad.take(5).forEach { hero ->
                    AsyncImage(
                        model = hero.imageUrl,
                        contentDescription = hero.name,
                        modifier = Modifier
                            .size(52.dp)
                            .padding(end = 8.dp)
                            .clip(MaterialTheme.shapes.medium)
                    )
                }
            }
        }

        Divider()

        LazyColumn {
            items(heroes.itemSnapshotList.items) { hero ->
                HeroRow(hero, onHeroClick)
                Divider()
            }
        }
    }
}

@Composable
private fun HeroRow(
    hero: Hero,
    onClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(hero.id) }
            .padding(12.dp)
    ) {
        AsyncImage(
            model = hero.imageUrl,
            contentDescription = hero.name,
            modifier = Modifier
                .size(40.dp)
                .clip(MaterialTheme.shapes.small)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            hero.name.orEmpty(),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}