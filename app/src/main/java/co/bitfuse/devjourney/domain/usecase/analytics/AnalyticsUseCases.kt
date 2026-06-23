package co.bitfuse.devjourney.domain.usecase.analytics

import co.bitfuse.devjourney.domain.repository.AnalyticsRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ObserveAnalyticsUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository,
) {
    operator fun invoke() = analyticsRepository.observeAnalytics()
}

class CalculateLearningStreakUseCase @Inject constructor() {
    operator fun invoke(
        completedAtValues: List<Long>,
        now: Long = System.currentTimeMillis(),
    ): Int = calculateLearningStreakDays(completedAtValues, now)
}

fun calculateLearningStreakDays(
    completedAtValues: List<Long>,
    now: Long = System.currentTimeMillis(),
): Int {
    if (completedAtValues.isEmpty()) return 0

    val dayMillis = TimeUnit.DAYS.toMillis(1)
    val completedDays = completedAtValues
        .map { it / dayMillis }
        .toSet()
    var cursorDay = now / dayMillis
    var streak = 0

    while (cursorDay in completedDays) {
        streak++
        cursorDay--
    }

    return streak
}
