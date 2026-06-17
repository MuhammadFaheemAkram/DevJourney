package com.example.devjourney.data.mapper

import com.example.devjourney.core.database.entity.GoalEntity
import com.example.devjourney.core.database.entity.ProgressEntity
import com.example.devjourney.core.database.entity.TopicEntity
import java.util.concurrent.TimeUnit

fun calculateCompletionPercentage(
    roadmapId: String,
    topics: List<TopicEntity>,
    progress: List<ProgressEntity>,
): Float {
    val roadmapTopics = topics.filter { it.roadmapId == roadmapId }
    if (roadmapTopics.isEmpty()) return 0f

    val completedTopicIds = progress
        .filter { it.isCompleted }
        .mapTo(mutableSetOf()) { it.topicId }

    val completedCount = roadmapTopics.count { it.id in completedTopicIds }
    return completedCount.toFloat() / roadmapTopics.size.toFloat()
}

fun calculateGoalCompletionRate(goals: List<GoalEntity>): Float {
    if (goals.isEmpty()) return 0f

    val completionSum = goals.sumOf { goal ->
        if (goal.targetCount <= 0) {
            0.0
        } else {
            (goal.currentCount.toDouble() / goal.targetCount.toDouble()).coerceIn(0.0, 1.0)
        }
    }

    return (completionSum / goals.size.toDouble()).toFloat()
}

fun calculateLearningStreak(
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
