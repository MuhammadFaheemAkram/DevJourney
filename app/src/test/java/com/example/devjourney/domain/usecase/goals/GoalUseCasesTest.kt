package com.example.devjourney.domain.usecase.goals

import com.example.devjourney.domain.model.GoalCadence
import com.example.devjourney.domain.model.LearningGoal
import com.example.devjourney.domain.repository.GoalsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GoalUseCasesTest {
    private val repository = FakeGoalsRepository(
        initialGoals = listOf(
            LearningGoal(
                id = "goal-1",
                title = "Complete topics",
                targetCount = 3,
                currentCount = 1,
                cadence = GoalCadence.DAILY,
            ),
        ),
    )
    private val observeGoalsUseCase = ObserveGoalsUseCase(repository)
    private val updateGoalProgressUseCase = UpdateGoalProgressUseCase(repository)
    private val goalProgressCalculator = GoalProgressCalculator()

    @Test
    fun observeGoals_emitsRepositoryGoals() = runBlocking {
        val goals = observeGoalsUseCase().first()

        assertEquals("Complete topics", goals.single().title)
    }

    @Test
    fun updateGoalProgress_updatesAndClampsGoalProgress() = runBlocking {
        updateGoalProgressUseCase(goalId = "goal-1", currentCount = 8)

        val goal = repository.observeGoals().first().single()

        assertEquals(3, goal.currentCount)
    }

    @Test
    fun goalProgressCalculator_calculatesAverageCompletionRate() {
        val goals = listOf(
            LearningGoal("daily", "Daily", targetCount = 2, currentCount = 1),
            LearningGoal("weekly", "Weekly", targetCount = 4, currentCount = 4),
        )

        val rate = goalProgressCalculator.completionRate(goals)

        assertEquals(0.75f, rate, 0.001f)
    }
}

private class FakeGoalsRepository(
    initialGoals: List<LearningGoal>,
) : GoalsRepository {
    private val goals = MutableStateFlow(initialGoals)

    override fun observeGoals(): Flow<List<LearningGoal>> = goals

    override suspend fun updateGoalProgress(goalId: String, currentCount: Int) {
        goals.value = goals.value.map { goal ->
            if (goal.id == goalId) {
                goal.copy(currentCount = currentCount.coerceIn(0, goal.targetCount))
            } else {
                goal
            }
        }
    }
}
