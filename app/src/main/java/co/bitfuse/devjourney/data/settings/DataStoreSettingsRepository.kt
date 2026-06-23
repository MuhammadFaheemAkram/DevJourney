package co.bitfuse.devjourney.data.settings

import co.bitfuse.devjourney.core.datastore.UserPreferencesDataSource
import co.bitfuse.devjourney.core.notification.LearningReminderScheduler
import co.bitfuse.devjourney.domain.model.ThemePreference
import co.bitfuse.devjourney.domain.model.UserSettings
import co.bitfuse.devjourney.domain.repository.SettingsRepository
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
