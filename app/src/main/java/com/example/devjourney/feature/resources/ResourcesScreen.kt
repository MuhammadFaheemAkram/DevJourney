package com.example.devjourney.feature.resources

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devjourney.core.designsystem.component.DevJourneyCard
import com.example.devjourney.core.designsystem.component.EmptyState
import com.example.devjourney.core.designsystem.component.ErrorState
import com.example.devjourney.core.designsystem.component.FilterChipGroup
import com.example.devjourney.core.designsystem.component.LoadingState
import com.example.devjourney.core.designsystem.component.SearchBar
import com.example.devjourney.domain.model.LearningResource

@Composable
fun ResourcesRoute(
    modifier: Modifier = Modifier,
    viewModel: ResourcesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ResourcesEffect.BookmarkChanged -> {
                    val status = if (effect.isBookmarked) "Bookmarked" else "Bookmark removed"
                    snackbarHostState.showSnackbar("$status: ${effect.title}")
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        ResourcesScreen(
            uiState = uiState,
            onSearchQueryChanged = viewModel::onSearchQueryChanged,
            onTypeSelected = viewModel::onTypeSelected,
            onBookmarkClick = viewModel::onBookmarkClick,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun ResourcesScreen(
    uiState: ResourcesUiState,
    onSearchQueryChanged: (String) -> Unit,
    onTypeSelected: (String) -> Unit,
    onBookmarkClick: (LearningResource) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Resources", style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = "Search, filter, and bookmark learning material for later.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        item {
            SearchBar(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = "Search resources",
            )
        }
        item {
            FilterChipGroup(
                options = ResourceTypeFilters,
                selectedOption = uiState.selectedType,
                onSelectedOptionChange = onTypeSelected,
            )
        }

        when {
            uiState.isLoading -> {
                item { LoadingState(message = "Loading resources") }
            }
            uiState.errorMessage != null -> {
                item {
                    ErrorState(
                        title = "Resources unavailable",
                        message = uiState.errorMessage,
                    )
                }
            }
            uiState.isEmpty -> {
                item {
                    EmptyState(
                        title = "No resources found",
                        message = "Try another search term or resource type.",
                    )
                }
            }
            else -> {
                items(
                    items = uiState.resources,
                    key = { it.id },
                ) { resource ->
                    LearningResourceCard(
                        resource = resource,
                        onBookmarkClick = { onBookmarkClick(resource) },
                    )
                }
            }
        }
    }
}

@Composable
private fun LearningResourceCard(
    resource: LearningResource,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DevJourneyCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = resource.title, style = MaterialTheme.typography.titleLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssistChip(
                            onClick = {},
                            label = { Text(text = resource.type.displayName) },
                        )
                        AssistChip(
                            onClick = {},
                            label = { Text(text = resource.source) },
                        )
                    }
                }
            }
            IconButton(onClick = onBookmarkClick) {
                Icon(
                    imageVector = if (resource.isBookmarked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (resource.isBookmarked) "Remove bookmark" else "Bookmark resource",
                    tint = if (resource.isBookmarked) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Text(
            text = resource.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = resource.url,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
