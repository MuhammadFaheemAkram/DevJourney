package co.bitfuse.devjourney.data.analytics

import co.bitfuse.devjourney.core.database.dao.GoalDao
import co.bitfuse.devjourney.core.database.dao.ProgressDao
import co.bitfuse.devjourney.core.database.dao.TopicDao
import co.bitfuse.devjourney.data.mapper.calculateGoalCompletionRate
import co.bitfuse.devjourney.data.mapper.calculateLearningStreak
import co.bitfuse.devjourney.domain.model.LearningAnalytics
import co.bitfuse.devjourney.domain.repository.AnalyticsRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class AnalyticsRepositoryImpl @Inject constructor(
    private val topicDao: TopicDao,
    private val progressDao: ProgressDao,
    private val goalDao: GoalDao,
) : AnalyticsRepository {
    override fun observeAnalytics(): Flow<LearningAnalytics> {
        return combine(
            topicDao.observeAllTopics(),
            progressDao.observeProgress(),
            goalDao.observeGoals(),
        ) { topics, progress, goals ->
            val now = System.currentTimeMillis()
            val completedProgress = progress.filter { it.isCompleted && it.completedAt != null }
            val completedTopics = completedProgress.mapTo(mutableSetOf()) { it.topicId }.size
            val weekAgo = now - TimeUnit.DAYS.toMillis(7)
            val monthAgo = now - TimeUnit.DAYS.toMillis(30)

            LearningAnalytics(
                streakDays = calculateLearningStreak(
                    completedAtValues = completedProgress.mapNotNull { it.completedAt },
                    now = now,
                ),
                completedTopics = completedTopics,
                totalTopics = topics.size,
                completionRate = if (topics.isEmpty()) 0f else completedTopics.toFloat() / topics.size.toFloat(),
                weeklyCompletedTopics = completedProgress.count { it.completedAt != null && it.completedAt >= weekAgo },
                monthlyCompletedTopics = completedProgress.count { it.completedAt != null && it.completedAt >= monthAgo },
                goalCompletionRate = calculateGoalCompletionRate(goals),
            )
        }
    }
}
