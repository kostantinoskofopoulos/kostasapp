package com.kostas.kostasapp.feature.heroes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import com.kostas.kostasapp.core.model.Hero
import com.kostas.kostasapp.heroes.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeroesScreen(
    uiState: HeroesUiState,
    heroes: LazyPagingItems<Hero>,
    onHeroClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.heroes_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            if (uiState.squad.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.heroes_my_squad),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.squad, key = { it.id }) { hero ->
                        SquadHeroChip(hero = hero, onClick = { onHeroClick(hero.id) })
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
            }

            when (val refresh = heroes.loadState.refresh) {
                is LoadState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is LoadState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(refresh.error.message ?: "Failed to load.")
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { heroes.retry() }) { Text("Retry") }
                        }
                    }
                }

                is LoadState.NotLoading -> {
                    HeroesList(
                        heroes = heroes,
                        onHeroClick = onHeroClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroesList(
    heroes: LazyPagingItems<Hero>,
    onHeroClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        items(
            count = heroes.itemCount,
            key = { index -> heroes[index]?.id ?: index }
        ) { index ->
            val hero = heroes[index]
            if (hero != null) {
                HeroRow(hero = hero, onClick = { onHeroClick(hero.id) })
            }
        }

        item {
            when (val append = heroes.loadState.append) {
                is LoadState.Loading -> {
                    Box(
                        Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }

                is LoadState.Error -> {
                    Box(
                        Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(append.error.message ?: "Failed to load more.")
                            Spacer(Modifier.height(8.dp))
                            OutlinedButton(onClick = { heroes.retry() }) { Text("Retry") }
                        }
                    }
                }

                else -> Unit
            }
        }
    }
}

@Composable
private fun HeroRow(
    hero: Hero,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = hero.imageUrl,
                contentDescription = hero.name,
                modifier = Modifier.size(40.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = hero.name.orEmpty(), style = MaterialTheme.typography.bodyMedium)
        }
        HorizontalDivider()
    }
}

@Composable
private fun SquadHeroChip(
    hero: Hero,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(72.dp).clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = hero.imageUrl,
            contentDescription = hero.name,
            modifier = Modifier.size(56.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = hero.name.orEmpty(),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}