package com.example.devjourney.domain.usecase.analytics

import com.example.devjourney.domain.model.LearningAnalytics
import com.example.devjourney.domain.repository.AnalyticsRepository
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class AnalyticsUseCasesTest {
    @Test
    fun observeAnalytics_emitsRepositoryAnalytics() = runBlocking {
        val repository = FakeAnalyticsRepository(
            LearningAnalytics(
                streakDays = 4,
                completedTopics = 8,
                totalTopics = 10,
                completionRate = 0.8f,
            ),
        )
        val useCase = ObserveAnalyticsUseCase(repository)

        val analytics = useCase().first()

        assertEquals(4, analytics.streakDays)
        assertEquals(0.8f, analytics.completionRate, 0.001f)
    }

    @Test
    fun calculateLearningStreak_countsOnlyConsecutiveDaysFromToday() {
        val dayMillis = TimeUnit.DAYS.toMillis(1)
        val now = dayMillis * 50
        val useCase = CalculateLearningStreakUseCase()

        val streak = useCase(
            completedAtValues = listOf(
                now,
                now - dayMillis,
                now - dayMillis * 3,
            ),
            now = now,
        )

        assertEquals(2, streak)
    }
}

private class FakeAnalyticsRepository(
    analytics: LearningAnalytics,
) : AnalyticsRepository {
    private val analyticsFlow = MutableStateFlow(analytics)

    override fun observeAnalytics(): Flow<LearningAnalytics> = analyticsFlow
}
