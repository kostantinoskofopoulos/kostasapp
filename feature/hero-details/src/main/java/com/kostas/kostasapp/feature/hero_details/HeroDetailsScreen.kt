package com.kostas.kostasapp.feature.hero_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeroDetailsScreen(
    uiState: HeroDetailsUiState,
    onBack: () -> Unit,
    onRecruitClick: () -> Unit,
    onFireClick: () -> Unit,
    onFireConfirm: () -> Unit,
    onFireDismiss: () -> Unit
) {
    val hero = uiState.hero

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(hero?.name.orEmpty())
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (hero != null) {
                        if (uiState.isInSquad) {
                            Button(onClick = onFireClick) {
                                Text("Fire from Squad")
                            }
                        } else {
                            Button(onClick = onRecruitClick) {
                                Text("Recruit to Squad")
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            }

            uiState.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.errorMessage,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            hero != null -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    item {
                        AsyncImage(
                            model = hero.imageUrl,
                            contentDescription = hero.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = hero.name.orEmpty(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    section("Films", hero.films)
                    section("TV Shows", hero.tvShows)
                    section("Video Games", hero.videoGames)
                    section("Allies", hero.allies)
                    section("Enemies", hero.enemies)
                }
            }
        }

        // Confirm dialog
        if (uiState.showFireConfirmDialog) {
            AlertDialog(
                onDismissRequest = onFireDismiss,
                title = { Text("Remove from Squad") },
                text = { Text("Are you sure you want to fire this hero from your squad?") },
                confirmButton = {
                    Button(onClick = onFireConfirm) {
                        Text("Yes, fire")
                    }
                },
                dismissButton = {
                    Button(onClick = onFireDismiss) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.section(
    title: String,
    items: List<String>
) {
    if (items.isNotEmpty()) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        }
        items(items) { value ->
            Text(
                text = "• $value",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}