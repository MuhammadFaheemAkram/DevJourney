package co.bitfuse.devjourney.feature.topicdetail

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.bitfuse.devjourney.core.designsystem.component.CompletionBadge
import co.bitfuse.devjourney.core.designsystem.component.DevJourneyCard
import co.bitfuse.devjourney.core.designsystem.component.EmptyState
import co.bitfuse.devjourney.core.designsystem.component.ErrorState
import co.bitfuse.devjourney.core.designsystem.component.LoadingState
import co.bitfuse.devjourney.domain.model.LearningResource
import co.bitfuse.devjourney.domain.model.Note
import co.bitfuse.devjourney.domain.model.Topic

@Composable
fun TopicDetailRoute(
    modifier: Modifier = Modifier,
    viewModel: TopicDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is TopicDetailEffect.BookmarkChanged -> {
                    val status = if (effect.isBookmarked) "Bookmarked" else "Bookmark removed"
                    snackbarHostState.showSnackbar(status)
                }
                is TopicDetailEffect.CompletionChanged -> {
                    val status = if (effect.isCompleted) "Topic completed" else "Topic reopened"
                    snackbarHostState.showSnackbar(status)
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        TopicDetailScreen(
            uiState = uiState,
            onCompletedChange = viewModel::onCompletedChange,
            onBookmarkChange = viewModel::onBookmarkChange,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun TopicDetailScreen(
    uiState: TopicDetailUiState,
    onCompletedChange: (Boolean) -> Unit,
    onBookmarkChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        uiState.isLoading -> {
            LoadingState(
                modifier = modifier.fillMaxWidth(),
                message = "Loading topic",
            )
        }

        uiState.errorMessage != null -> {
            ErrorState(
                modifier = modifier.fillMaxWidth(),
                title = "Topic unavailable",
                message = uiState.errorMessage,
            )
        }

        uiState.topic == null -> {
            EmptyState(
                modifier = modifier.fillMaxWidth(),
                title = "Topic not found",
                message = "This topic is not available in the local catalog.",
            )
        }

        else -> {
            TopicDetailContent(
                topic = uiState.topic,
                resources = uiState.resources,
                notes = uiState.notes,
                onCompletedChange = onCompletedChange,
                onBookmarkChange = onBookmarkChange,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun TopicDetailContent(
    topic: Topic,
    resources: List<LearningResource>,
    notes: List<Note>,
    onCompletedChange: (Boolean) -> Unit,
    onBookmarkChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            TopicHeaderCard(
                topic = topic,
                onCompletedChange = onCompletedChange,
                onBookmarkChange = onBookmarkChange,
            )
        }
        item {
            ObjectivesCard(objectives = topic.objectives)
        }
        item {
            SectionTitle(title = "Resources", count = resources.size)
        }
        if (resources.isEmpty()) {
            item {
                DevJourneyCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Resources for this topic will appear here as the catalog grows.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            items(
                items = resources,
                key = { it.id },
            ) { resource ->
                TopicResourceCard(resource = resource)
            }
        }
        item {
            SectionTitle(title = "Notes", count = notes.size)
        }
        if (notes.isEmpty()) {
            item {
                DevJourneyCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No notes attached yet. Notes CRUD arrives in Phase 4.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            items(
                items = notes,
                key = { it.id },
            ) { note ->
                TopicNoteCard(note = note)
            }
        }
    }
}

@Composable
private fun TopicHeaderCard(
    topic: Topic,
    onCompletedChange: (Boolean) -> Unit,
    onBookmarkChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    DevJourneyCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = topic.sectionTitle,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Text(text = topic.title, style = MaterialTheme.typography.headlineSmall)
            }
            IconButton(onClick = { onBookmarkChange(!topic.isBookmarked) }) {
                Icon(
                    imageVector = if (topic.isBookmarked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (topic.isBookmarked) "Remove topic bookmark" else "Bookmark topic",
                    tint = if (topic.isBookmarked) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Text(
            text = topic.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AssistChip(
                onClick = {},
                label = { Text(text = topic.difficulty.name.lowercase().replaceFirstChar { it.uppercase() }) },
            )
            AssistChip(
                onClick = {},
                label = { Text(text = "${topic.estimatedMinutes} min") },
            )
            CompletionBadge(isCompleted = topic.isCompleted)
        }
        if (topic.isCompleted) {
            OutlinedButton(onClick = { onCompletedChange(false) }) {
                Text(text = "Reopen topic")
            }
        } else {
            Button(onClick = { onCompletedChange(true) }) {
                Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                Text(text = "Mark completed", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
private fun ObjectivesCard(
    objectives: List<String>,
    modifier: Modifier = Modifier,
) {
    DevJourneyCard(modifier = modifier.fillMaxWidth()) {
        Text(text = "Learning objectives", style = MaterialTheme.typography.titleLarge)
        if (objectives.isEmpty()) {
            Text(
                text = "Objectives will be added with expanded catalog content.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            objectives.forEach { objective ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = objective,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun TopicResourceCard(
    resource: LearningResource,
    modifier: Modifier = Modifier,
) {
    DevJourneyCard(modifier = modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = resource.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${resource.type.name.lowercase().replaceFirstChar { it.uppercase() }} - ${resource.source}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = resource.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = resource.url,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun TopicNoteCard(
    note: Note,
    modifier: Modifier = Modifier,
) {
    DevJourneyCard(modifier = modifier.fillMaxWidth()) {
        Text(text = note.title, style = MaterialTheme.typography.titleMedium)
        Text(
            text = note.content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "Updated ${note.updatedAt}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SectionTitle(
    title: String,
    count: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
        )
    }
}
