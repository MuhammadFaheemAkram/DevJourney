package com.example.devjourney.feature.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devjourney.core.designsystem.component.DevJourneyCard
import com.example.devjourney.core.designsystem.component.EmptyState
import com.example.devjourney.core.designsystem.component.ErrorState
import com.example.devjourney.core.designsystem.component.LoadingState
import com.example.devjourney.core.designsystem.component.SearchBar
import com.example.devjourney.domain.usecase.search.SearchResultItem

@Composable
fun SearchRoute(
    onRoadmapClick: (String) -> Unit,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SearchScreen(
        uiState = uiState,
        onQueryChanged = viewModel::onQueryChanged,
        onRoadmapClick = onRoadmapClick,
        onTopicClick = onTopicClick,
        modifier = modifier,
    )
}

@Composable
private fun SearchScreen(
    uiState: SearchUiState,
    onQueryChanged: (String) -> Unit,
    onRoadmapClick: (String) -> Unit,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Search", style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = "Find roadmaps, topics, notes, resources, and challenges offline.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        item {
            SearchBar(
                value = uiState.query,
                onValueChange = onQueryChanged,
                placeholder = "Search everything",
            )
        }

        when {
            uiState.isLoading -> {
                item { LoadingState(message = "Preparing search") }
            }
            uiState.errorMessage != null -> {
                item {
                    ErrorState(
                        title = "Search unavailable",
                        message = uiState.errorMessage,
                    )
                }
            }
            !uiState.hasQuery -> {
                item {
                    EmptyState(
                        title = "Start typing",
                        message = "Search across your local learning catalog and saved work.",
                    )
                }
            }
            uiState.results.isEmpty -> {
                item {
                    EmptyState(
                        title = "No matches",
                        message = "Try a broader keyword or another topic name.",
                    )
                }
            }
            else -> {
                searchSection(
                    title = "Roadmaps",
                    items = uiState.results.roadmaps,
                    onItemClick = onRoadmapClick,
                )
                searchSection(
                    title = "Topics",
                    items = uiState.results.topics,
                    onItemClick = onTopicClick,
                )
                searchSection(
                    title = "Notes",
                    items = uiState.results.notes,
                    onItemClick = null,
                )
                searchSection(
                    title = "Resources",
                    items = uiState.results.resources,
                    onItemClick = null,
                )
                searchSection(
                    title = "Challenges",
                    items = uiState.results.challenges,
                    onItemClick = null,
                )
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.searchSection(
    title: String,
    items: List<SearchResultItem>,
    onItemClick: ((String) -> Unit)?,
) {
    if (items.isEmpty()) return

    item(key = "$title-header") {
        Text(text = "$title (${items.size})", style = MaterialTheme.typography.titleLarge)
    }
    items(
        count = items.size,
        key = { index -> "$title-${items[index].id}" },
    ) { index ->
        val item = items[index]
        SearchResultCard(
            item = item,
            onClick = onItemClick?.let { click -> { click(item.id) } },
        )
    }
}

@Composable
private fun SearchResultCard(
    item: SearchResultItem,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    DevJourneyCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Text(text = item.title, style = MaterialTheme.typography.titleMedium)
        Text(
            text = item.metadata,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = item.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
