package com.example.devjourney.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devjourney.domain.model.ThemePreference
import com.example.devjourney.domain.model.UserSettings
import com.example.devjourney.domain.usecase.settings.MarkFirstLaunchCompleteUseCase
import com.example.devjourney.domain.usecase.settings.ObserveSettingsUseCase
import com.example.devjourney.domain.usecase.settings.UpdateDynamicColorUseCase
import com.example.devjourney.domain.usecase.settings.UpdateReminderUseCase
import com.example.devjourney.domain.usecase.settings.UpdateThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isLoading: Boolean = true,
    val settings: UserSettings = UserSettings(),
    val errorMessage: String? = null,
)

sealed interface SettingsEffect {
    data class SettingsUpdated(val message: String) : SettingsEffect
    data class SettingsUpdateFailed(val message: String) : SettingsEffect
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeSettingsUseCase: ObserveSettingsUseCase,
    private val updateThemeUseCase: UpdateThemeUseCase,
    private val updateDynamicColorUseCase: UpdateDynamicColorUseCase,
    private val updateReminderUseCase: UpdateReminderUseCase,
    private val markFirstLaunchCompleteUseCase: MarkFirstLaunchCompleteUseCase,
) : ViewModel() {
    private val _effects = MutableSharedFlow<SettingsEffect>()

    val effects = _effects.asSharedFlow()

    val uiState: StateFlow<SettingsUiState> = observeSettingsUseCase()
        .map { settings ->
            SettingsUiState(
                isLoading = false,
                settings = settings,
            )
        }
        .catch { error ->
            emit(
                SettingsUiState(
                    isLoading = false,
                    errorMessage = error.message ?: "Unable to load settings.",
                ),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState(),
        )

    fun onThemeSelected(label: String) {
        updateSetting("Theme updated") {
            updateThemeUseCase(label.toThemePreference())
        }
    }

    fun onDynamicColorChanged(enabled: Boolean) {
        updateSetting(if (enabled) "Dynamic color enabled" else "Dynamic color disabled") {
            updateDynamicColorUseCase(enabled)
        }
    }

    fun onReminderChanged(enabled: Boolean) {
        updateSetting(if (enabled) "Daily reminder scheduled" else "Daily reminder disabled") {
            updateReminderUseCase(enabled)
        }
    }

    fun onFirstLaunchAcknowledged() {
        updateSetting("Welcome state saved") {
            markFirstLaunchCompleteUseCase()
        }
    }

    private fun updateSetting(
        successMessage: String,
        action: suspend () -> Unit,
    ) {
        viewModelScope.launch {
            runCatching { action() }
                .onSuccess { _effects.emit(SettingsEffect.SettingsUpdated(successMessage)) }
                .onFailure { error ->
                    _effects.emit(
                        SettingsEffect.SettingsUpdateFailed(
                            error.message ?: "Unable to update setting.",
                        ),
                    )
                }
        }
    }
}

val ThemePreferenceOptions = ThemePreference.entries.map { it.displayName }

val ThemePreference.displayName: String
    get() = name.lowercase().replaceFirstChar { it.uppercase() }

private fun String.toThemePreference(): ThemePreference {
    return ThemePreference.entries.firstOrNull { it.displayName == this } ?: ThemePreference.SYSTEM
}
