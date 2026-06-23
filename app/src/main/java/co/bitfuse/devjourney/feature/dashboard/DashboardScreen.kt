package co.bitfuse.devjourney.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.bitfuse.devjourney.core.designsystem.component.AnalyticsCard
import co.bitfuse.devjourney.core.designsystem.component.DevJourneyCard
import co.bitfuse.devjourney.core.designsystem.component.ProgressCard
import co.bitfuse.devjourney.core.designsystem.component.StreakBanner

@Composable
fun DashboardRoute(
    onOpenRoadmaps: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DashboardScreen(
        onOpenRoadmaps = onOpenRoadmaps,
        modifier = modifier,
    )
}

@Composable
private fun DashboardScreen(
    onOpenRoadmaps: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            StreakBanner(streakDays = 7)
        }
        item {
            ProgressCard(
                title = "Active roadmap",
                value = "Android Developer",
                label = "42% foundation preview",
                progress = 0.42f,
                icon = Icons.AutoMirrored.Filled.List,
            )
        }
        item {
            DevJourneyCard(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Recommended next topic", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "Continue into live roadmap details and topic progress.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Button(onClick = onOpenRoadmaps) {
                    Text(text = "Browse roadmaps")
                }
            }
        }
        item {
            ProgressCard(
                title = "Today's goals",
                value = "2 of 4",
                label = "Goal tracking arrives in Phase 4",
                progress = 0.5f,
                icon = Icons.Filled.CheckCircle,
            )
        }
        item {
            AnalyticsCard(
                title = "Topics completed",
                value = "18",
                caption = "Static preview for the dashboard shell",
            )
        }
    }
}
