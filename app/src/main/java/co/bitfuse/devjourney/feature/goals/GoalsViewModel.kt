package co.bitfuse.devjourney.feature.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.bitfuse.devjourney.domain.model.GoalCadence
import co.bitfuse.devjourney.domain.model.LearningGoal
import co.bitfuse.devjourney.domain.usecase.goals.GoalProgressCalculator
import co.bitfuse.devjourney.domain.usecase.goals.ObserveGoalsUseCase
import co.bitfuse.devjourney.domain.usecase.goals.UpdateGoalProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class GoalsUiState(
    val isLoading: Boolean = true,
    val goals: List<LearningGoal> = emptyList(),
    val selectedFilter: String = ALL_GOALS,
    val summary: GoalsSummary = GoalsSummary(),
    val errorMessage: String? = null,
) {
    val isEmpty: Boolean = !isLoading && errorMessage == null && goals.isEmpty()
}

data class GoalsSummary(
    val totalGoals: Int = 0,
    val completedGoals: Int = 0,
    val completionRate: Float = 0f,
)

sealed interface GoalsEffect {
    data class ProgressUpdated(val goalTitle: String) : GoalsEffect
}

@HiltViewModel
class GoalsViewModel @Inject constructor(
    observeGoalsUseCase: ObserveGoalsUseCase,
    private val updateGoalProgressUseCase: UpdateGoalProgressUseCase,
    private val goalProgressCalculator: GoalProgressCalculator,
) : ViewModel() {
    private val selectedFilter = MutableStateFlow(ALL_GOALS)
    private val _effects = MutableSharedFlow<GoalsEffect>()

    val effects = _effects.asSharedFlow()

    val uiState: StateFlow<GoalsUiState> = combine(
        observeGoalsUseCase(),
        selectedFilter,
    ) { goals, filter ->
        val visibleGoals = goals.filter { goal ->
            filter == ALL_GOALS || goal.cadence.displayName == filter
        }
        GoalsUiState(
            isLoading = false,
            goals = visibleGoals,
            selectedFilter = filter,
            summary = GoalsSummary(
                totalGoals = goals.size,
                completedGoals = goals.count { goalProgressCalculator.progress(it) >= 1f },
                completionRate = goalProgressCalculator.completionRate(goals),
            ),
        )
    }.catch { error ->
        emit(
            GoalsUiState(
                isLoading = false,
                errorMessage = error.message ?: "Unable to load goals.",
            ),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = GoalsUiState(),
    )

    fun onFilterSelected(filter: String) {
        selectedFilter.value = filter
    }

    fun onIncrementGoal(goal: LearningGoal) {
        updateProgress(goal, goal.currentCount + 1)
    }

    fun onDecrementGoal(goal: LearningGoal) {
        updateProgress(goal, goal.currentCount - 1)
    }

    fun onCompleteGoal(goal: LearningGoal) {
        updateProgress(goal, goal.targetCount)
    }

    private fun updateProgress(goal: LearningGoal, currentCount: Int) {
        viewModelScope.launch {
            updateGoalProgressUseCase(goal.id, currentCount)
            _effects.emit(GoalsEffect.ProgressUpdated(goal.title))
        }
    }
}

val GoalFilterOptions = listOf(
    ALL_GOALS,
    GoalCadence.DAILY.displayName,
    GoalCadence.WEEKLY.displayName,
    GoalCadence.MONTHLY.displayName,
)

val GoalCadence.displayName: String
    get() = name.lowercase().replaceFirstChar { it.uppercase() }

private const val ALL_GOALS = "All"
