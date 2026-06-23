package co.bitfuse.devjourney.feature.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.bitfuse.devjourney.core.designsystem.component.AnalyticsCard
import co.bitfuse.devjourney.core.designsystem.component.DevJourneyCard
import co.bitfuse.devjourney.core.designsystem.component.EmptyState
import co.bitfuse.devjourney.core.designsystem.component.ErrorState
import co.bitfuse.devjourney.core.designsystem.component.LoadingState
import co.bitfuse.devjourney.core.designsystem.component.ProgressCard
import co.bitfuse.devjourney.core.designsystem.component.StreakBanner
import co.bitfuse.devjourney.domain.model.LearningAnalytics
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun AnalyticsRoute(
    modifier: Modifier = Modifier,
    viewModel: AnalyticsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AnalyticsScreen(
        uiState = uiState,
        modifier = modifier,
    )
}

@Composable
private fun AnalyticsScreen(
    uiState: AnalyticsUiState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Analytics", style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = "Repository-backed insights from topics, progress, and goals.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        when {
            uiState.isLoading -> {
                item { LoadingState(message = "Calculating analytics") }
            }
            uiState.errorMessage != null -> {
                item {
                    ErrorState(
                        title = "Analytics unavailable",
                        message = uiState.errorMessage,
                    )
                }
            }
            uiState.isEmpty -> {
                item {
                    EmptyState(
                        title = "No analytics yet",
                        message = "Complete a topic or goal to start building your learning history.",
                    )
                }
            }
            else -> {
                item {
                    StreakBanner(
                        streakDays = uiState.analytics.streakDays,
                        supportingText = "Calculated from consecutive topic completion days.",
                    )
                }
                item {
                    ProgressCard(
                        title = "Completion rate",
                        value = "${(uiState.analytics.completionRate * 100).roundToInt()}%",
                        label = "${uiState.analytics.completedTopics}/${uiState.analytics.totalTopics} topics completed",
                        progress = uiState.analytics.completionRate,
                        icon = Icons.Filled.Check,
                    )
                }
                item {
                    AnalyticsMetricGrid(analytics = uiState.analytics)
                }
                item {
                    CompletionChart(analytics = uiState.analytics)
                }
            }
        }
    }
}

@Composable
private fun AnalyticsMetricGrid(
    analytics: LearningAnalytics,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AnalyticsCard(
                title = "This week",
                value = "${analytics.weeklyCompletedTopics}",
                caption = "Topics completed",
                modifier = Modifier.weight(1f),
            )
            AnalyticsCard(
                title = "This month",
                value = "${analytics.monthlyCompletedTopics}",
                caption = "Topics completed",
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AnalyticsCard(
                title = "Goal rate",
                value = "${(analytics.goalCompletionRate * 100).roundToInt()}%",
                caption = "Average goal progress",
                modifier = Modifier.weight(1f),
            )
            AnalyticsCard(
                title = "Active plan",
                value = "${max(analytics.totalTopics - analytics.completedTopics, 0)}",
                caption = "Topics remaining",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun CompletionChart(
    analytics: LearningAnalytics,
    modifier: Modifier = Modifier,
) {
    val totalTopics = max(analytics.totalTopics, 1)

    DevJourneyCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = "Completion chart", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Current learning velocity",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
            )
        }
        ChartBar(
            label = "Completed",
            value = analytics.completedTopics,
            maxValue = totalTopics,
            color = MaterialTheme.colorScheme.primary,
        )
        ChartBar(
            label = "Remaining",
            value = max(analytics.totalTopics - analytics.completedTopics, 0),
            maxValue = totalTopics,
            color = MaterialTheme.colorScheme.tertiary,
        )
        ChartBar(
            label = "Weekly",
            value = analytics.weeklyCompletedTopics,
            maxValue = totalTopics,
            color = MaterialTheme.colorScheme.secondary,
        )
        ChartBar(
            label = "Monthly",
            value = analytics.monthlyCompletedTopics,
            maxValue = totalTopics,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.72f),
        )
    }
}

@Composable
private fun ChartBar(
    label: String,
    value: Int,
    maxValue: Int,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = color,
                fontWeight = FontWeight.Bold,
            )
        }
        LinearProgressIndicator(
            progress = { (value.toFloat() / max(maxValue, 1).toFloat()).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth(),
            color = color,
        )
    }
}
