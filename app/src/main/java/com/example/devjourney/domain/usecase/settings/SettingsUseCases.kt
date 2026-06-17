package com.example.devjourney.domain.usecase.settings

import com.example.devjourney.domain.model.ThemePreference
import com.example.devjourney.domain.repository.SettingsRepository
import javax.inject.Inject

class ObserveSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke() = settingsRepository.observeSettings()
}

class UpdateThemeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(themePreference: ThemePreference) {
        settingsRepository.updateTheme(themePreference)
    }
}

class UpdateDynamicColorUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(enabled: Boolean) {
        settingsRepository.updateDynamicColor(enabled)
    }
}

class UpdateReminderUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(enabled: Boolean) {
        settingsRepository.updateReminder(enabled)
    }
}

class UpdateSelectedRoadmapUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(roadmapId: String?) {
        settingsRepository.updateSelectedRoadmap(roadmapId)
    }
}

class MarkFirstLaunchCompleteUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke() {
        settingsRepository.markFirstLaunchComplete()
    }
}
