package co.bitfuse.devjourney.feature.roadmap

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.bitfuse.devjourney.core.designsystem.component.DevJourneyCard
import co.bitfuse.devjourney.core.designsystem.component.EmptyState
import co.bitfuse.devjourney.core.designsystem.component.ErrorState
import co.bitfuse.devjourney.core.designsystem.component.FilterChipGroup
import co.bitfuse.devjourney.core.designsystem.component.LoadingState
import co.bitfuse.devjourney.core.designsystem.component.SearchBar
import co.bitfuse.devjourney.domain.model.Roadmap
import kotlin.math.roundToInt

@Composable
fun RoadmapsRoute(
    onRoadmapClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoadmapsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RoadmapsScreen(
        uiState = uiState,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onCategorySelected = viewModel::onCategorySelected,
        onRefresh = viewModel::refresh,
        onRoadmapClick = onRoadmapClick,
        modifier = modifier,
    )
}

@Composable
private fun RoadmapsScreen(
    uiState: RoadmapsUiState,
    onSearchQueryChanged: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onRefresh: () -> Unit,
    onRoadmapClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Learning roadmaps",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Text(
                        text = "Choose a track and keep moving through the path.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = onRefresh) {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Refresh roadmaps")
                }
            }
        }
        item {
            SearchBar(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = "Search roadmaps",
            )
        }
        item {
            FilterChipGroup(
                options = uiState.categories,
                selectedOption = uiState.selectedCategory,
                onSelectedOptionChange = onCategorySelected,
            )
        }

        when {
            uiState.isLoading -> {
                item {
                    LoadingState(message = "Loading roadmaps")
                }
            }

            uiState.errorMessage != null -> {
                item {
                    ErrorState(
                        title = "Roadmaps unavailable",
                        message = uiState.errorMessage,
                        onRetry = onRefresh,
                    )
                }
            }

            uiState.isEmpty -> {
                item {
                    EmptyState(
                        title = "No roadmaps found",
                        message = "Try a different search or category filter.",
                        actionLabel = "Reset filters",
                        onAction = {
                            onSearchQueryChanged("")
                            onCategorySelected("All")
                        },
                    )
                }
            }

            else -> {
                items(
                    items = uiState.roadmaps,
                    key = { it.id },
                ) { roadmap ->
                    RoadmapSummaryCard(
                        roadmap = roadmap,
                        onClick = { onRoadmapClick(roadmap.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun RoadmapSummaryCard(
    roadmap: Roadmap,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val completionPercent = (roadmap.completionPercentage * 100).roundToInt()

    DevJourneyCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AssistChip(
                    onClick = onClick,
                    label = { Text(text = roadmap.category) },
                )
                Text(text = roadmap.title, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = roadmap.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "$completionPercent% complete",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Offline-ready",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        LinearProgressIndicator(
            progress = { roadmap.completionPercentage.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
        )
    }
}
