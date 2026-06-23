package co.bitfuse.devjourney.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.bitfuse.devjourney.core.designsystem.component.DevJourneyCard
import co.bitfuse.devjourney.core.designsystem.component.ErrorState
import co.bitfuse.devjourney.core.designsystem.component.FilterChipGroup
import co.bitfuse.devjourney.core.designsystem.component.LoadingState
import co.bitfuse.devjourney.domain.model.UserSettings

@Composable
fun SettingsRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is SettingsEffect.SettingsUpdated -> snackbarHostState.showSnackbar(effect.message)
                is SettingsEffect.SettingsUpdateFailed -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        SettingsScreen(
            uiState = uiState,
            onThemeSelected = viewModel::onThemeSelected,
            onDynamicColorChanged = viewModel::onDynamicColorChanged,
            onReminderChanged = viewModel::onReminderChanged,
            onFirstLaunchAcknowledged = viewModel::onFirstLaunchAcknowledged,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
fun AboutRoute(
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "About DevJourney", style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = "An offline-first Android learning hub built as a modern portfolio project.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        item {
            AboutCard()
        }
    }
}

@Composable
private fun SettingsScreen(
    uiState: SettingsUiState,
    onThemeSelected: (String) -> Unit,
    onDynamicColorChanged: (Boolean) -> Unit,
    onReminderChanged: (Boolean) -> Unit,
    onFirstLaunchAcknowledged: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Settings", style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = "Persisted preferences for the learning experience.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        when {
            uiState.isLoading -> {
                item { LoadingState(message = "Loading settings") }
            }
            uiState.errorMessage != null -> {
                item {
                    ErrorState(
                        title = "Settings unavailable",
                        message = uiState.errorMessage,
                    )
                }
            }
            else -> {
                item {
                    ThemePreferenceCard(
                        settings = uiState.settings,
                        onThemeSelected = onThemeSelected,
                    )
                }
                item {
                    SettingsToggleCard(
                        title = "Dynamic color",
                        description = "Use Material You colors when the device supports them.",
                        icon = Icons.Filled.Star,
                        checked = uiState.settings.dynamicColorEnabled,
                        onCheckedChange = onDynamicColorChanged,
                    )
                }
                item {
                    SettingsToggleCard(
                        title = "Daily reminder",
                        description = "Schedule an offline WorkManager reminder for learning sessions.",
                        icon = Icons.Filled.Info,
                        checked = uiState.settings.reminderEnabled,
                        onCheckedChange = onReminderChanged,
                    )
                }
                item {
                    FirstLaunchCard(
                        isFirstLaunch = uiState.settings.isFirstLaunch,
                        onFirstLaunchAcknowledged = onFirstLaunchAcknowledged,
                    )
                }
                item {
                    SettingsInfoCard(
                        title = "Selected roadmap",
                        value = uiState.settings.selectedRoadmapId ?: "No roadmap selected",
                        description = "Stored in DataStore for future plan restoration.",
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemePreferenceCard(
    settings: UserSettings,
    onThemeSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    DevJourneyCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(text = "Theme", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Choose system, light, or dark appearance.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                FilterChipGroup(
                    options = ThemePreferenceOptions,
                    selectedOption = settings.themePreference.displayName,
                    onSelectedOptionChange = onThemeSelected,
                )
            }
        }
    }
}

@Composable
private fun SettingsToggleCard(
    title: String,
    description: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    DevJourneyCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        }
    }
}

@Composable
private fun FirstLaunchCard(
    isFirstLaunch: Boolean,
    onFirstLaunchAcknowledged: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DevJourneyCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(text = "First launch", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = if (isFirstLaunch) {
                        "Welcome state is still marked as new."
                    } else {
                        "Welcome state has been saved."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (isFirstLaunch) {
                    Button(onClick = onFirstLaunchAcknowledged) {
                        Text(text = "Mark seen")
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsInfoCard(
    title: String,
    value: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    DevJourneyCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun AboutCard(
    modifier: Modifier = Modifier,
) {
    DevJourneyCard(modifier = modifier.fillMaxWidth()) {
        Text(text = "Open-source quality goals", style = MaterialTheme.typography.titleMedium)
        listOf(
            "MVVM with clean architecture inspired boundaries",
            "Offline-first Room persistence with fake API seeding",
            "DataStore preferences and WorkManager reminders",
            "Composable UI state from Flow and StateFlow",
        ).forEach { item ->
            Text(
                text = item,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
