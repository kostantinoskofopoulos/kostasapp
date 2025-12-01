package com.kostas.kostasapp.feature.hero_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

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

    Scaffold { padding ->
        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            hero != null -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {

                    // ---------- IMAGE + Χ BACK ----------
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AsyncImage(
                                model = hero.imageUrl,
                                contentDescription = hero.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(260.dp),
                                contentScale = ContentScale.Crop
                            )

                            IconButton(
                                onClick = onBack,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Close",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    // ---------- NAME + BIG BUTTON ----------
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp)
                        ) {
                            Text(
                                text = hero.name.orEmpty(),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            val isInSquad = uiState.isInSquad

                            val buttonLabel: String
                            val buttonColor: Color
                            val buttonClick: () -> Unit

                            if (isInSquad) {
                                buttonLabel = "🔥 Fire from Squad"

                                buttonColor = MaterialTheme.colorScheme.error
                                buttonClick = onFireClick
                            } else {
                                buttonLabel = "💪 Hire to Squad"
                                buttonColor = MaterialTheme.colorScheme.primary
                                buttonClick = onRecruitClick
                            }

                            Button(
                                onClick = buttonClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = buttonColor,
                                    contentColor = Color.White
                                ),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(buttonLabel)
                            }
                        }
                    }

                    // ---------- DETAILS SECTIONS ----------
                    section("Films", hero.films)
                    section("TV Shows", hero.tvShows)
                    section("Video Games", hero.videoGames)
                    section("Allies", hero.allies)
                    section("Enemies", hero.enemies)
                }
            }
        }

        // ---------- CONFIRM DIALOG for Fire from Squad ----------
        if (uiState.showFireConfirmDialog) {
            AlertDialog(
                onDismissRequest = onFireDismiss,
                title = { Text("Remove from Squad") },
                text = {
                    Text("Are you sure you want to fire this hero from your squad?")
                },
                confirmButton = {
                    TextButton(onClick = onFireConfirm) {
                        Icon(
                            imageVector = Icons.Filled.Whatshot,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Yes, fire")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onFireDismiss) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

private fun LazyListScope.section(
    title: String,
    values: List<String>
) {
    if (values.isNotEmpty()) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        items(values) { value ->
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 2.dp)
            )
        }
        item {
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}