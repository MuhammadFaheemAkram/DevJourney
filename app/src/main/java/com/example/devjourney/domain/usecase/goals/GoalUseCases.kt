package com.example.devjourney.domain.usecase.goals

import com.example.devjourney.domain.model.LearningGoal
import com.example.devjourney.domain.repository.GoalsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveGoalsUseCase @Inject constructor(
    private val goalsRepository: GoalsRepository,
) {
    operator fun invoke(): Flow<List<LearningGoal>> {
        return goalsRepository.observeGoals()
    }
}

class UpdateGoalProgressUseCase @Inject constructor(
    private val goalsRepository: GoalsRepository,
) {
    suspend operator fun invoke(goalId: String, currentCount: Int) {
        goalsRepository.updateGoalProgress(goalId, currentCount)
    }
}

class GoalProgressCalculator @Inject constructor() {
    fun progress(goal: LearningGoal): Float {
        return calculateGoalProgress(goal.currentCount, goal.targetCount)
    }

    fun completionRate(goals: List<LearningGoal>): Float {
        if (goals.isEmpty()) return 0f
        return goals.sumOf { progress(it).toDouble() }.toFloat() / goals.size.toFloat()
    }
}

fun calculateGoalProgress(currentCount: Int, targetCount: Int): Float {
    if (targetCount <= 0) return 0f
    return (currentCount.toFloat() / targetCount.toFloat()).coerceIn(0f, 1f)
}
