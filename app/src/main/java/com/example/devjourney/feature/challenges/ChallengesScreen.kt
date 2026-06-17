package com.example.devjourney.feature.challenges

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devjourney.core.designsystem.component.DevJourneyCard
import com.example.devjourney.core.designsystem.component.EmptyState
import com.example.devjourney.core.designsystem.component.ErrorState
import com.example.devjourney.core.designsystem.component.FilterChipGroup
import com.example.devjourney.core.designsystem.component.LoadingState
import com.example.devjourney.domain.model.ChallengeStatus
import com.example.devjourney.domain.model.CodingChallenge

@Composable
fun ChallengesRoute(
    modifier: Modifier = Modifier,
    viewModel: ChallengesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ChallengesEffect.ChallengeCompleted -> snackbarHostState.showSnackbar("${effect.title} completed")
            }
        }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        ChallengesScreen(
            uiState = uiState,
            onDifficultySelected = viewModel::onDifficultySelected,
            onStatusSelected = viewModel::onStatusSelected,
            onCompleteClick = viewModel::onCompleteClick,
            onCompletionNotesChanged = viewModel::onCompletionNotesChanged,
            onCompletionDismissed = viewModel::onCompletionDismissed,
            onCompletionSaved = viewModel::onCompletionSaved,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun ChallengesScreen(
    uiState: ChallengesUiState,
    onDifficultySelected: (String) -> Unit,
    onStatusSelected: (String) -> Unit,
    onCompleteClick: (CodingChallenge) -> Unit,
    onCompletionNotesChanged: (String) -> Unit,
    onCompletionDismissed: () -> Unit,
    onCompletionSaved: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Coding challenges", style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = "Practice implementation and design problems tied to your learning topics.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        item {
            Text(text = "Difficulty", style = MaterialTheme.typography.titleMedium)
            FilterChipGroup(
                options = ChallengeDifficultyFilters,
                selectedOption = uiState.selectedDifficulty,
                onSelectedOptionChange = onDifficultySelected,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        item {
            Text(text = "Status", style = MaterialTheme.typography.titleMedium)
            FilterChipGroup(
                options = ChallengeStatusFilters,
                selectedOption = uiState.selectedStatus,
                onSelectedOptionChange = onStatusSelected,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        when {
            uiState.isLoading -> {
                item { LoadingState(message = "Loading challenges") }
            }
            uiState.errorMessage != null -> {
                item {
                    ErrorState(
                        title = "Challenges unavailable",
                        message = uiState.errorMessage,
                    )
                }
            }
            uiState.isEmpty -> {
                item {
                    EmptyState(
                        title = "No challenges found",
                        message = "Try another difficulty or status filter.",
                    )
                }
            }
            else -> {
                items(
                    items = uiState.challenges,
                    key = { it.id },
                ) { challenge ->
                    ChallengeCard(
                        challenge = challenge,
                        onCompleteClick = { onCompleteClick(challenge) },
                    )
                }
            }
        }
    }

    uiState.completionEditor?.let { editor ->
        ChallengeCompletionDialog(
            editor = editor,
            onNotesChanged = onCompletionNotesChanged,
            onDismiss = onCompletionDismissed,
            onSave = onCompletionSaved,
        )
    }
}

@Composable
private fun ChallengeCard(
    challenge: CodingChallenge,
    onCompleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isCompleted = challenge.status == ChallengeStatus.COMPLETED

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
                Text(text = challenge.title, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = challenge.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = challenge.difficulty.displayName,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AssistChip(
                onClick = {},
                label = { Text(text = challenge.status.displayName) },
            )
            if (!challenge.solutionNotes.isNullOrBlank()) {
                AssistChip(
                    onClick = {},
                    leadingIcon = { Icon(imageVector = Icons.Filled.Edit, contentDescription = null) },
                    label = { Text(text = "Solution saved") },
                )
            }
        }
        if (challenge.solutionNotes != null) {
            Text(
                text = challenge.solutionNotes,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (isCompleted) {
            OutlinedButton(onClick = onCompleteClick) {
                Icon(imageVector = Icons.Filled.Edit, contentDescription = null)
                Text(text = "Edit solution", modifier = Modifier.padding(start = 8.dp))
            }
        } else {
            Button(onClick = onCompleteClick) {
                Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                Text(text = "Complete challenge", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
private fun ChallengeCompletionDialog(
    editor: ChallengeCompletionEditor,
    onNotesChanged: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = editor.challengeTitle) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Save solution notes and mark this challenge completed.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                OutlinedTextField(
                    value = editor.solutionNotes,
                    onValueChange = onNotesChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 140.dp),
                    minLines = 5,
                    label = { Text(text = "Solution notes") },
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) {
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
