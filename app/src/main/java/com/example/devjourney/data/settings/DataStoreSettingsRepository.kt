package com.example.devjourney.data.settings

import com.example.devjourney.core.datastore.UserPreferencesDataSource
import com.example.devjourney.core.notification.LearningReminderScheduler
import com.example.devjourney.domain.model.ThemePreference
import com.example.devjourney.domain.model.UserSettings
import com.example.devjourney.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class DataStoreSettingsRepository @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource,
    private val reminderScheduler: LearningReminderScheduler,
) : SettingsRepository {
    override fun observeSettings(): Flow<UserSettings> = userPreferencesDataSource.userSettings

    override suspend fun updateTheme(themePreference: ThemePreference) {
        userPreferencesDataSource.updateTheme(themePreference)
    }

    override suspend fun updateDynamicColor(enabled: Boolean) {
        userPreferencesDataSource.updateDynamicColor(enabled)
    }

    override suspend fun updateReminder(enabled: Boolean) {
        userPreferencesDataSource.updateReminder(enabled)
        reminderScheduler.setDailyReminderEnabled(enabled)
    }

    override suspend fun updateSelectedRoadmap(roadmapId: String?) {
        userPreferencesDataSource.updateSelectedRoadmap(roadmapId)
    }

    override suspend fun markFirstLaunchComplete() {
        userPreferencesDataSource.markFirstLaunchComplete()
    }
}
