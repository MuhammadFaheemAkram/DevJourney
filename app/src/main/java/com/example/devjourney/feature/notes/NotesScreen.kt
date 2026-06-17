package com.example.devjourney.feature.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.devjourney.domain.model.Note
import com.example.devjourney.domain.model.Topic

@Composable
fun NotesRoute(
    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                NotesEffect.NoteDeleted -> snackbarHostState.showSnackbar("Note deleted")
                is NotesEffect.NoteSaved -> {
                    snackbarHostState.showSnackbar(if (effect.isUpdate) "Note updated" else "Note created")
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = viewModel::onCreateNoteClick,
                icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = null) },
                text = { Text(text = "New note") },
            )
        },
    ) { innerPadding ->
        NotesScreen(
            uiState = uiState,
            onSearchQueryChanged = viewModel::onSearchQueryChanged,
            onCreateNoteClick = viewModel::onCreateNoteClick,
            onEditNoteClick = viewModel::onEditNoteClick,
            onDeleteNoteClick = viewModel::onDeleteNoteClick,
            onEditorDismissed = viewModel::onEditorDismissed,
            onEditorTitleChanged = viewModel::onEditorTitleChanged,
            onEditorContentChanged = viewModel::onEditorContentChanged,
            onEditorTopicChanged = viewModel::onEditorTopicChanged,
            onSaveNoteClick = viewModel::onSaveNoteClick,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun NotesScreen(
    uiState: NotesUiState,
    onSearchQueryChanged: (String) -> Unit,
    onCreateNoteClick: () -> Unit,
    onEditNoteClick: (Note) -> Unit,
    onDeleteNoteClick: (String) -> Unit,
    onEditorDismissed: () -> Unit,
    onEditorTitleChanged: (String) -> Unit,
    onEditorContentChanged: (String) -> Unit,
    onEditorTopicChanged: (String?) -> Unit,
    onSaveNoteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Notes", style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = "Capture ideas and attach them to learning topics.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        item {
            SearchBar(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = "Search notes",
            )
        }

        when {
            uiState.isLoading -> {
                item { LoadingState(message = "Loading notes") }
            }
            uiState.errorMessage != null -> {
                item {
                    ErrorState(
                        title = "Notes unavailable",
                        message = uiState.errorMessage,
                    )
                }
            }
            uiState.isEmpty -> {
                item {
                    EmptyState(
                        title = "No notes yet",
                        message = "Create a note to capture learning context for a topic.",
                        actionLabel = "Create note",
                        onAction = onCreateNoteClick,
                    )
                }
            }
            else -> {
                items(
                    items = uiState.notes,
                    key = { it.id },
                ) { note ->
                    NoteCard(
                        note = note,
                        topicTitle = uiState.topics.firstOrNull { it.id == note.topicId }?.title,
                        onEditClick = { onEditNoteClick(note) },
                        onDeleteClick = { onDeleteNoteClick(note.id) },
                    )
                }
            }
        }
    }

    uiState.editorState?.let { editor ->
        NoteEditorDialog(
            editorState = editor,
            topics = uiState.topics,
            onDismiss = onEditorDismissed,
            onTitleChange = onEditorTitleChanged,
            onContentChange = onEditorContentChanged,
            onTopicChange = onEditorTopicChanged,
            onSaveClick = onSaveNoteClick,
        )
    }
}

@Composable
private fun NoteCard(
    note: Note,
    topicTitle: String?,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
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
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(text = note.title, style = MaterialTheme.typography.titleLarge)
                if (topicTitle != null) {
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                text = topicTitle,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                    )
                }
            }
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit note")
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete note")
                }
            }
        }
        Text(
            text = note.content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun NoteEditorDialog(
    editorState: NoteEditorUiState,
    topics: List<Topic>,
    onDismiss: () -> Unit,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onTopicChange: (String?) -> Unit,
    onSaveClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (editorState.isEditing) "Edit note" else "Create note") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                OutlinedTextField(
                    value = editorState.title,
                    onValueChange = onTitleChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text(text = "Title") },
                )
                OutlinedTextField(
                    value = editorState.content,
                    onValueChange = onContentChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                    minLines = 4,
                    label = { Text(text = "Markdown note") },
                )
                Text(text = "Attach to topic", style = MaterialTheme.typography.titleMedium)
                FilterChipGroup(
                    options = listOf("General") + topics.map { it.title },
                    selectedOption = topics.firstOrNull { it.id == editorState.selectedTopicId }?.title ?: "General",
                    onSelectedOptionChange = { label ->
                        val topicId = topics.firstOrNull { it.title == label }?.id
                        onTopicChange(topicId)
                    },
                )
                if (editorState.validationMessage != null) {
                    Text(
                        text = editorState.validationMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSaveClick) {
                Text(text = "Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
    )
}
