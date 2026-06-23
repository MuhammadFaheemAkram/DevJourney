package co.bitfuse.devjourney.domain.usecase.settings

import co.bitfuse.devjourney.domain.model.ThemePreference
import co.bitfuse.devjourney.domain.model.UserSettings
import co.bitfuse.devjourney.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsUseCasesTest {
    private val repository = FakeSettingsRepository()

    @Test
    fun observeSettings_emitsCurrentSettings() = runBlocking {
        repository.updateTheme(ThemePreference.DARK)
        val useCase = ObserveSettingsUseCase(repository)

        val settings = useCase().first()

        assertEquals(ThemePreference.DARK, settings.themePreference)
    }

    @Test
    fun updateTheme_persistsThemePreference() = runBlocking {
        val useCase = UpdateThemeUseCase(repository)

        useCase(ThemePreference.LIGHT)

        assertEquals(ThemePreference.LIGHT, repository.observeSettings().first().themePreference)
    }

    @Test
    fun updateReminder_persistsReminderState() = runBlocking {
        val useCase = UpdateReminderUseCase(repository)

        useCase(true)

        assertTrue(repository.observeSettings().first().reminderEnabled)
    }

    @Test
    fun updateDynamicColor_persistsDynamicColorState() = runBlocking {
        val useCase = UpdateDynamicColorUseCase(repository)

        useCase(false)

        assertFalse(repository.observeSettings().first().dynamicColorEnabled)
    }

    @Test
    fun markFirstLaunchComplete_persistsWelcomeState() = runBlocking {
        val useCase = MarkFirstLaunchCompleteUseCase(repository)

        useCase()

        assertFalse(repository.observeSettings().first().isFirstLaunch)
    }
}

private class FakeSettingsRepository : SettingsRepository {
    private val settings = MutableStateFlow(UserSettings())

    override fun observeSettings(): Flow<UserSettings> = settings

    override suspend fun updateTheme(themePreference: ThemePreference) {
        settings.value = settings.value.copy(themePreference = themePreference)
    }

    override suspend fun updateDynamicColor(enabled: Boolean) {
        settings.value = settings.value.copy(dynamicColorEnabled = enabled)
    }

    override suspend fun updateReminder(enabled: Boolean) {
        settings.value = settings.value.copy(reminderEnabled = enabled)
    }

    override suspend fun updateSelectedRoadmap(roadmapId: String?) {
        settings.value = settings.value.copy(selectedRoadmapId = roadmapId)
    }

    override suspend fun markFirstLaunchComplete() {
        settings.value = settings.value.copy(isFirstLaunch = false)
    }
}
