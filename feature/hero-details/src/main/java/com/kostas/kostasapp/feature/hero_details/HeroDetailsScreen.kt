package com.kostas.kostasapp.feature.hero_details

import androidx.annotation.StringRes
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kostas.kostasapp.hero_details.R

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

                    item {
                        HeroDetailsHeader(
                            name = hero.name,
                            imageUrl = hero.imageUrl,
                            onBack = onBack
                        )
                    }

                    item {
                        HeroDetailsPrimaryInfo(
                            name = hero.name,
                            isInSquad = uiState.isInSquad,
                            onRecruitClick = onRecruitClick,
                            onFireClick = onFireClick
                        )
                    }

                    uiState.sections.forEach { section ->
                        detailsSection(
                            titleRes = section.titleRes,
                            values = section.values
                        )
                    }
                }
            }
        }

        if (uiState.showFireConfirmDialog) {
            HeroDetailsFireDialog(
                onConfirm = onFireConfirm,
                onDismiss = onFireDismiss
            )
        }
    }
}

@Composable
private fun HeroDetailsHeader(
    name: String?,
    imageUrl: String?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = name,
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
                contentDescription = stringResource(R.string.hero_details_close),
                tint = Color.White
            )
        }
    }
}

@Composable
private fun HeroDetailsPrimaryInfo(
    name: String?,
    isInSquad: Boolean,
    onRecruitClick: () -> Unit,
    onFireClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            text = name.orEmpty(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        val buttonLabel: String
        val buttonColor: Color
        val buttonClick: () -> Unit

        if (isInSquad) {
            buttonLabel = stringResource(R.string.hero_details_fire_from_squad)
            buttonColor = MaterialTheme.colorScheme.error
            buttonClick = onFireClick
        } else {
            buttonLabel = stringResource(R.string.hero_details_hire_to_squad)
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

@Composable
private fun HeroDetailsFireDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.hero_details_dialog_fire_title)) },
        text = {
            Text(stringResource(R.string.hero_details_dialog_fire_message))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Icon(
                    imageVector = Icons.Filled.Whatshot,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(stringResource(R.string.hero_details_dialog_fire_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.hero_details_dialog_fire_cancel))
            }
        }
    )
}

private fun LazyListScope.detailsSection(
    @StringRes titleRes: Int,
    values: List<String>
) {
    if (values.isNotEmpty()) {
        item {
            Text(
                text = stringResource(id = titleRes),
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