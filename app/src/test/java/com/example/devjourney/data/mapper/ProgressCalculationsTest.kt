package com.example.devjourney.data.mapper

import com.example.devjourney.core.database.entity.GoalEntity
import com.example.devjourney.core.database.entity.ProgressEntity
import com.example.devjourney.core.database.entity.TopicEntity
import com.example.devjourney.domain.usecase.goals.calculateGoalProgress
import java.util.concurrent.TimeUnit
import org.junit.Assert.assertEquals
import org.junit.Test

class ProgressCalculationsTest {
    @Test
    fun calculateCompletionPercentage_countsOnlyCompletedTopicsForRoadmap() {
        val topics = listOf(
            topic(id = "android-1", roadmapId = "android"),
            topic(id = "android-2", roadmapId = "android"),
            topic(id = "backend-1", roadmapId = "backend"),
        )
        val progress = listOf(
            progress(topicId = "android-1", roadmapId = "android", isCompleted = true),
            progress(topicId = "android-2", roadmapId = "android", isCompleted = false),
            progress(topicId = "backend-1", roadmapId = "backend", isCompleted = true),
        )

        val result = calculateCompletionPercentage(
            roadmapId = "android",
            topics = topics,
            progress = progress,
        )

        assertEquals(0.5f, result, 0.001f)
    }

    @Test
    fun calculateLearningStreak_countsConsecutiveCompletedDaysFromToday() {
        val dayMillis = TimeUnit.DAYS.toMillis(1)
        val now = dayMillis * 100
        val completedAtValues = listOf(
            now,
            now - dayMillis,
            now - dayMillis * 2,
            now - dayMillis * 4,
        )

        val result = calculateLearningStreak(completedAtValues, now)

        assertEquals(3, result)
    }

    @Test
    fun calculateGoalCompletionRate_averagesClampedGoalProgress() {
        val goals = listOf(
            goal(id = "daily", targetCount = 4, currentCount = 2),
            goal(id = "weekly", targetCount = 5, currentCount = 8),
        )

        val result = calculateGoalCompletionRate(goals)

        assertEquals(0.75f, result, 0.001f)
    }

    @Test
    fun calculateGoalProgress_clampsProgressBetweenZeroAndOne() {
        assertEquals(0f, calculateGoalProgress(currentCount = 3, targetCount = 0), 0.001f)
        assertEquals(0f, calculateGoalProgress(currentCount = -1, targetCount = 4), 0.001f)
        assertEquals(1f, calculateGoalProgress(currentCount = 8, targetCount = 4), 0.001f)
    }

    private fun topic(
        id: String,
        roadmapId: String,
    ): TopicEntity {
        return TopicEntity(
            id = id,
            roadmapId = roadmapId,
            sectionTitle = "Section",
            title = id,
            description = "",
            difficulty = "BEGINNER",
            estimatedMinutes = 30,
            objectives = emptyList(),
            sortOrder = 0,
        )
    }

    private fun progress(
        topicId: String,
        roadmapId: String,
        isCompleted: Boolean,
    ): ProgressEntity {
        return ProgressEntity(
            topicId = topicId,
            roadmapId = roadmapId,
            isCompleted = isCompleted,
            completedAt = if (isCompleted) 1L else null,
            lastUpdatedAt = 1L,
        )
    }

    private fun goal(
        id: String,
        targetCount: Int,
        currentCount: Int,
    ): GoalEntity {
        return GoalEntity(
            id = id,
            title = id,
            cadence = "DAILY",
            targetCount = targetCount,
            currentCount = currentCount,
            startsAt = 0L,
            endsAt = null,
        )
    }
}
