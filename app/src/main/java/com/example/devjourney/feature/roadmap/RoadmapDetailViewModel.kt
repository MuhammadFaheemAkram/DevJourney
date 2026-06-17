package com.example.devjourney.feature.roadmap

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devjourney.core.navigation.DevJourneyRoutes
import com.example.devjourney.domain.model.RoadmapDetails
import com.example.devjourney.domain.usecase.roadmap.GetRoadmapDetailsUseCase
import com.example.devjourney.domain.usecase.topic.MarkTopicCompletedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RoadmapDetailUiState(
    val isLoading: Boolean = true,
    val details: RoadmapDetails? = null,
    val expandedSectionTitles: Set<String> = emptySet(),
    val errorMessage: String? = null,
)

sealed interface RoadmapDetailEffect {
    data class SectionCompleted(val sectionTitle: String) : RoadmapDetailEffect
    data class TopicUpdated(val topicTitle: String, val isCompleted: Boolean) : RoadmapDetailEffect
}

@HiltViewModel
class RoadmapDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getRoadmapDetailsUseCase: GetRoadmapDetailsUseCase,
    private val markTopicCompletedUseCase: MarkTopicCompletedUseCase,
) : ViewModel() {
    private val roadmapId: String = checkNotNull(savedStateHandle[DevJourneyRoutes.ROADMAP_ID_ARG])
    private val expandedSectionTitles = MutableStateFlow<Set<String>?>(null)
    private val _effects = MutableSharedFlow<RoadmapDetailEffect>()

    val effects = _effects.asSharedFlow()

    val uiState: StateFlow<RoadmapDetailUiState> = combine(
        getRoadmapDetailsUseCase(roadmapId).onEach { details ->
            if (details != null && expandedSectionTitles.value == null) {
                expandedSectionTitles.value = details.sections.mapTo(mutableSetOf()) { it.title }
            }
        },
        expandedSectionTitles,
    ) { details, expanded ->
        RoadmapDetailUiState(
            isLoading = false,
            details = details,
            expandedSectionTitles = expanded ?: details?.sections?.mapTo(mutableSetOf()) { it.title }.orEmpty(),
        )
    }.catch { error ->
        emit(
            RoadmapDetailUiState(
                isLoading = false,
                errorMessage = error.message ?: "Unable to load roadmap details.",
            ),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RoadmapDetailUiState(),
    )

    fun onSectionToggle(sectionTitle: String) {
        val current = expandedSectionTitles.value
            ?: uiState.value.details?.sections?.mapTo(mutableSetOf()) { it.title }
            ?: emptySet()

        expandedSectionTitles.value = if (sectionTitle in current) {
            current - sectionTitle
        } else {
            current + sectionTitle
        }
    }

    fun onTopicCompletedChange(topicId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            markTopicCompletedUseCase(topicId, isCompleted)
            val topicTitle = uiState.value.details
                ?.sections
                ?.flatMap { it.topics }
                ?.firstOrNull { it.id == topicId }
                ?.title
                .orEmpty()
            _effects.emit(RoadmapDetailEffect.TopicUpdated(topicTitle, isCompleted))
        }
    }

    fun onMarkSectionCompleted(sectionTitle: String) {
        viewModelScope.launch {
            val section = uiState.value.details?.sections?.firstOrNull { it.title == sectionTitle } ?: return@launch
            section.topics
                .filterNot { it.isCompleted }
                .forEach { topic -> markTopicCompletedUseCase(topic.id, true) }
            _effects.emit(RoadmapDetailEffect.SectionCompleted(sectionTitle))
        }
    }
}
