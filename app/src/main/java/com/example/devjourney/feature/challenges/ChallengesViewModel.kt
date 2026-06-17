package com.example.devjourney.feature.challenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devjourney.domain.model.ChallengeStatus
import com.example.devjourney.domain.model.CodingChallenge
import com.example.devjourney.domain.model.Difficulty
import com.example.devjourney.domain.usecase.challenges.CompleteChallengeUseCase
import com.example.devjourney.domain.usecase.challenges.ObserveChallengesUseCase
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChallengesUiState(
    val isLoading: Boolean = true,
    val challenges: List<CodingChallenge> = emptyList(),
    val selectedDifficulty: String = ALL_FILTER,
    val selectedStatus: String = ALL_FILTER,
    val completionEditor: ChallengeCompletionEditor? = null,
    val errorMessage: String? = null,
) {
    val isEmpty: Boolean = !isLoading && errorMessage == null && challenges.isEmpty()
}

data class ChallengeCompletionEditor(
    val challengeId: String,
    val challengeTitle: String,
    val solutionNotes: String = "",
)

sealed interface ChallengesEffect {
    data class ChallengeCompleted(val title: String) : ChallengesEffect
}

@HiltViewModel
class ChallengesViewModel @Inject constructor(
    observeChallengesUseCase: ObserveChallengesUseCase,
    private val completeChallengeUseCase: CompleteChallengeUseCase,
) : ViewModel() {
    private val selectedDifficulty = MutableStateFlow(ALL_FILTER)
    private val selectedStatus = MutableStateFlow(ALL_FILTER)
    private val completionEditor = MutableStateFlow<ChallengeCompletionEditor?>(null)
    private val _effects = MutableSharedFlow<ChallengesEffect>()

    val effects = _effects.asSharedFlow()

    val uiState: StateFlow<ChallengesUiState> = combine(
        observeChallengesUseCase(),
        selectedDifficulty,
        selectedStatus,
        completionEditor,
    ) { challenges, difficultyFilter, statusFilter, editor ->
        ChallengesUiState(
            isLoading = false,
            challenges = challenges.filter { challenge ->
                val matchesDifficulty = difficultyFilter == ALL_FILTER || challenge.difficulty.displayName == difficultyFilter
                val matchesStatus = statusFilter == ALL_FILTER || challenge.status.displayName == statusFilter
                matchesDifficulty && matchesStatus
            },
            selectedDifficulty = difficultyFilter,
            selectedStatus = statusFilter,
            completionEditor = editor,
        )
    }.catch { error ->
        emit(
            ChallengesUiState(
                isLoading = false,
                errorMessage = error.message ?: "Unable to load challenges.",
            ),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ChallengesUiState(),
    )

    fun onDifficultySelected(difficulty: String) {
        selectedDifficulty.value = difficulty
    }

    fun onStatusSelected(status: String) {
        selectedStatus.value = status
    }

    fun onCompleteClick(challenge: CodingChallenge) {
        completionEditor.value = ChallengeCompletionEditor(
            challengeId = challenge.id,
            challengeTitle = challenge.title,
            solutionNotes = challenge.solutionNotes.orEmpty(),
        )
    }

    fun onCompletionNotesChanged(notes: String) {
        completionEditor.update { editor -> editor?.copy(solutionNotes = notes) }
    }

    fun onCompletionDismissed() {
        completionEditor.value = null
    }

    fun onCompletionSaved() {
        val editor = completionEditor.value ?: return
        viewModelScope.launch {
            completeChallengeUseCase(editor.challengeId, editor.solutionNotes)
            completionEditor.value = null
            _effects.emit(ChallengesEffect.ChallengeCompleted(editor.challengeTitle))
        }
    }
}

val ChallengeDifficultyFilters = listOf(ALL_FILTER) + Difficulty.entries.map { it.displayName }
val ChallengeStatusFilters = listOf(ALL_FILTER) + ChallengeStatus.entries.map { it.displayName }

val Difficulty.displayName: String
    get() = name.lowercase().replaceFirstChar { it.uppercase() }

val ChallengeStatus.displayName: String
    get() = name
        .lowercase()
        .split("_")
        .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }

private const val ALL_FILTER = "All"
