package com.example.devjourney.data.goals

import com.example.devjourney.core.database.dao.GoalDao
import com.example.devjourney.data.mapper.toDomain
import com.example.devjourney.domain.model.LearningGoal
import com.example.devjourney.domain.repository.GoalsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GoalsRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao,
) : GoalsRepository {
    override fun observeGoals(): Flow<List<LearningGoal>> {
        return goalDao.observeGoals().map { goals ->
            goals.map { it.toDomain() }
        }
    }

    override suspend fun updateGoalProgress(goalId: String, currentCount: Int) {
        val goal = goalDao.getGoal(goalId) ?: return
        goalDao.upsertGoal(
            goal.copy(
                currentCount = currentCount.coerceIn(0, goal.targetCount),
            ),
        )
    }
}
