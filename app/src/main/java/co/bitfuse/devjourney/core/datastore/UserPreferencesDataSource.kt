package co.bitfuse.devjourney.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import co.bitfuse.devjourney.domain.model.ThemePreference
import co.bitfuse.devjourney.domain.model.UserSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private const val USER_PREFERENCES_NAME = "devjourney_user_preferences"

private val Context.userPreferencesDataStore by preferencesDataStore(name = USER_PREFERENCES_NAME)

@Singleton
class UserPreferencesDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    val userSettings: Flow<UserSettings> = context.userPreferencesDataStore.data
        .catch { throwable ->
            if (throwable is IOException) {
                emit(emptyPreferences())
            } else {
                throw throwable
            }
        }
        .map { preferences ->
            UserSettings(
                themePreference = preferences[PreferenceKeys.themePreference]
                    ?.let(::themePreferenceOrSystem)
                    ?: ThemePreference.SYSTEM,
                dynamicColorEnabled = preferences[PreferenceKeys.dynamicColorEnabled] ?: true,
                reminderEnabled = preferences[PreferenceKeys.reminderEnabled] ?: false,
                selectedRoadmapId = preferences[PreferenceKeys.selectedRoadmapId],
                isFirstLaunch = preferences[PreferenceKeys.isFirstLaunch] ?: true,
            )
        }

    suspend fun updateTheme(themePreference: ThemePreference) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[PreferenceKeys.themePreference] = themePreference.name
        }
    }

    suspend fun updateDynamicColor(enabled: Boolean) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[PreferenceKeys.dynamicColorEnabled] = enabled
        }
    }

    suspend fun updateReminder(enabled: Boolean) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[PreferenceKeys.reminderEnabled] = enabled
        }
    }

    suspend fun updateSelectedRoadmap(roadmapId: String?) {
        context.userPreferencesDataStore.edit { preferences ->
            if (roadmapId == null) {
                preferences.remove(PreferenceKeys.selectedRoadmapId)
            } else {
                preferences[PreferenceKeys.selectedRoadmapId] = roadmapId
            }
        }
    }

    suspend fun markFirstLaunchComplete() {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[PreferenceKeys.isFirstLaunch] = false
        }
    }

    private fun themePreferenceOrSystem(rawValue: String): ThemePreference {
        return ThemePreference.entries.firstOrNull { it.name == rawValue } ?: ThemePreference.SYSTEM
    }

    private object PreferenceKeys {
        val themePreference = stringPreferencesKey("theme_preference")
        val dynamicColorEnabled = booleanPreferencesKey("dynamic_color_enabled")
        val reminderEnabled = booleanPreferencesKey("reminder_enabled")
        val selectedRoadmapId = stringPreferencesKey("selected_roadmap_id")
        val isFirstLaunch = booleanPreferencesKey("is_first_launch")
    }
}
