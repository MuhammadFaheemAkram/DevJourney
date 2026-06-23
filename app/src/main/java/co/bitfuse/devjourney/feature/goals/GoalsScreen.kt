package co.bitfuse.devjourney.feature.goals

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import co.bitfuse.devjourney.core.designsystem.component.AnalyticsCard
import co.bitfuse.devjourney.core.designsystem.component.DevJourneyCard
import co.bitfuse.devjourney.core.designsystem.component.EmptyState
import co.bitfuse.devjourney.core.designsystem.component.ErrorState
import co.bitfuse.devjourney.core.designsystem.component.FilterChipGroup
import co.bitfuse.devjourney.core.designsystem.component.LoadingState
import co.bitfuse.devjourney.domain.model.LearningGoal
import co.bitfuse.devjourney.domain.usecase.goals.calculateGoalProgress
import kotlin.math.roundToInt

@Composable
fun GoalsRoute(
    modifier: Modifier = Modifier,
    viewModel: GoalsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is GoalsEffect.ProgressUpdated -> snackbarHostState.showSnackbar("${effect.goalTitle} updated")
            }
        }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        GoalsScreen(
            uiState = uiState,
            onFilterSelected = viewModel::onFilterSelected,
            onIncrementGoal = viewModel::onIncrementGoal,
            onDecrementGoal = viewModel::onDecrementGoal,
            onCompleteGoal = viewModel::onCompleteGoal,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun GoalsScreen(
    uiState: GoalsUiState,
    onFilterSelected: (String) -> Unit,
    onIncrementGoal: (LearningGoal) -> Unit,
    onDecrementGoal: (LearningGoal) -> Unit,
    onCompleteGoal: (LearningGoal) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Goals", style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = "Track daily, weekly, and monthly learning commitments.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        item {
            GoalSummaryRow(summary = uiState.summary)
        }
        item {
            FilterChipGroup(
                options = GoalFilterOptions,
                selectedOption = uiState.selectedFilter,
                onSelectedOptionChange = onFilterSelected,
            )
        }

        when {
            uiState.isLoading -> {
                item { LoadingState(message = "Loading goals") }
            }
            uiState.errorMessage != null -> {
                item {
                    ErrorState(
                        title = "Goals unavailable",
                        message = uiState.errorMessage,
                    )
                }
            }
            uiState.isEmpty -> {
                item {
                    EmptyState(
                        title = "No goals found",
                        message = "Try a different cadence filter.",
                    )
                }
            }
            else -> {
                items(
                    items = uiState.goals,
                    key = { it.id },
                ) { goal ->
                    LearningGoalCard(
                        goal = goal,
                        onIncrement = { onIncrementGoal(goal) },
                        onDecrement = { onDecrementGoal(goal) },
                        onComplete = { onCompleteGoal(goal) },
                    )
                }
            }
        }
    }
}

@Composable
private fun GoalSummaryRow(
    summary: GoalsSummary,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AnalyticsCard(
            title = "Goals complete",
            value = "${summary.completedGoals}/${summary.totalGoals}",
            caption = "Across all cadences",
            modifier = Modifier.weight(1f),
        )
        AnalyticsCard(
            title = "Completion rate",
            value = "${(summary.completionRate * 100).roundToInt()}%",
            caption = "Average progress",
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun LearningGoalCard(
    goal: LearningGoal,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val progress = calculateGoalProgress(goal.currentCount, goal.targetCount)
    val percent = (progress * 100).roundToInt()
    val isCompleted = progress >= 1f

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
                Text(text = goal.title, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = goal.cadence.displayName,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
            Text(
                text = "$percent%",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${goal.currentCount}/${goal.targetCount}",
                style = MaterialTheme.typography.titleMedium,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = onDecrement,
                    enabled = goal.currentCount > 0,
                ) {
                    Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription = "Decrease goal progress")
                }
                IconButton(
                    onClick = onIncrement,
                    enabled = goal.currentCount < goal.targetCount,
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Increase goal progress")
                }
            }
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
        )
        if (isCompleted) {
            OutlinedButton(onClick = onComplete, enabled = false) {
                Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                Text(text = "Completed", modifier = Modifier.padding(start = 8.dp))
            }
        } else {
            Button(onClick = onComplete) {
                Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                Text(text = "Mark complete", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}
