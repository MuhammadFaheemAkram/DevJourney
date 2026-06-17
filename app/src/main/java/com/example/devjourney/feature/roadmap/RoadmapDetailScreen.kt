package com.example.devjourney.feature.roadmap

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devjourney.core.designsystem.component.CompletionBadge
import com.example.devjourney.core.designsystem.component.DevJourneyCard
import com.example.devjourney.core.designsystem.component.EmptyState
import com.example.devjourney.core.designsystem.component.ErrorState
import com.example.devjourney.core.designsystem.component.LoadingState
import com.example.devjourney.domain.model.RoadmapDetails
import com.example.devjourney.domain.model.RoadmapSection
import com.example.devjourney.domain.model.Topic
import kotlin.math.roundToInt

@Composable
fun RoadmapDetailRoute(
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoadmapDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is RoadmapDetailEffect.SectionCompleted -> {
                    snackbarHostState.showSnackbar("${effect.sectionTitle} marked complete")
                }
                is RoadmapDetailEffect.TopicUpdated -> {
                    val status = if (effect.isCompleted) "completed" else "reopened"
                    snackbarHostState.showSnackbar("${effect.topicTitle} $status")
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        RoadmapDetailScreen(
            uiState = uiState,
            onSectionToggle = viewModel::onSectionToggle,
            onMarkSectionCompleted = viewModel::onMarkSectionCompleted,
            onTopicCompletedChange = viewModel::onTopicCompletedChange,
            onTopicClick = onTopicClick,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun RoadmapDetailScreen(
    uiState: RoadmapDetailUiState,
    onSectionToggle: (String) -> Unit,
    onMarkSectionCompleted: (String) -> Unit,
    onTopicCompletedChange: (String, Boolean) -> Unit,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        uiState.isLoading -> {
            LoadingState(
                modifier = modifier.fillMaxWidth(),
                message = "Loading roadmap detail",
            )
        }

        uiState.errorMessage != null -> {
            ErrorState(
                modifier = modifier.fillMaxWidth(),
                title = "Roadmap unavailable",
                message = uiState.errorMessage,
            )
        }

        uiState.details == null -> {
            EmptyState(
                modifier = modifier.fillMaxWidth(),
                title = "Roadmap not found",
                message = "This roadmap is not available in the local catalog.",
            )
        }

        else -> {
            RoadmapDetailContent(
                details = uiState.details,
                expandedSectionTitles = uiState.expandedSectionTitles,
                onSectionToggle = onSectionToggle,
                onMarkSectionCompleted = onMarkSectionCompleted,
                onTopicCompletedChange = onTopicCompletedChange,
                onTopicClick = onTopicClick,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun RoadmapDetailContent(
    details: RoadmapDetails,
    expandedSectionTitles: Set<String>,
    onSectionToggle: (String) -> Unit,
    onMarkSectionCompleted: (String) -> Unit,
    onTopicCompletedChange: (String, Boolean) -> Unit,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            RoadmapHero(details = details)
        }

        details.sections.forEach { section ->
            item(key = "header-${section.title}") {
                RoadmapSectionHeader(
                    section = section,
                    isExpanded = section.title in expandedSectionTitles,
                    onToggle = { onSectionToggle(section.title) },
                    onMarkCompleted = { onMarkSectionCompleted(section.title) },
                )
            }
            if (section.title in expandedSectionTitles) {
                items(
                    items = section.topics,
                    key = { it.id },
                ) { topic ->
                    RoadmapTopicCard(
                        topic = topic,
                        onClick = { onTopicClick(topic.id) },
                        onCompletedChange = { isCompleted ->
                            onTopicCompletedChange(topic.id, isCompleted)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun RoadmapHero(
    details: RoadmapDetails,
    modifier: Modifier = Modifier,
) {
    val completionPercent = (details.roadmap.completionPercentage * 100).roundToInt()
    val completedTopics = details.sections.sumOf { section -> section.topics.count { it.isCompleted } }
    val totalTopics = details.sections.sumOf { it.topics.size }

    DevJourneyCard(modifier = modifier.fillMaxWidth()) {
        Text(text = details.roadmap.category, style = MaterialTheme.typography.labelLarge)
        Text(text = details.roadmap.title, style = MaterialTheme.typography.headlineSmall)
        Text(
            text = details.roadmap.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "$completionPercent% complete",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "$completedTopics of $totalTopics topics",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        LinearProgressIndicator(
            progress = { details.roadmap.completionPercentage.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun RoadmapSectionHeader(
    section: RoadmapSection,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onMarkCompleted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val completionPercent = (section.completionPercentage * 100).roundToInt()
    val isCompleted = section.topics.isNotEmpty() && section.topics.all { it.isCompleted }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(text = section.title, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "$completionPercent% complete",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                IconButton(onClick = onToggle) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse section" else "Expand section",
                    )
                }
            }
            LinearProgressIndicator(
                progress = { section.completionPercentage.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedButton(
                onClick = onMarkCompleted,
                enabled = !isCompleted,
            ) {
                Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                Text(
                    text = if (isCompleted) "Section completed" else "Mark section completed",
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun RoadmapTopicCard(
    topic: Topic,
    onClick: () -> Unit,
    onCompletedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    DevJourneyCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Checkbox(
                checked = topic.isCompleted,
                onCheckedChange = onCompletedChange,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        text = topic.title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                    )
                    if (topic.isBookmarked) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Bookmarked",
                            tint = MaterialTheme.colorScheme.tertiary,
                        )
                    }
                }
                Text(
                    text = topic.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CompletionBadge(isCompleted = topic.isCompleted)
                    Text(
                        text = "${topic.difficulty.name.lowercase().replaceFirstChar { it.uppercase() }} - ${topic.estimatedMinutes} min",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
