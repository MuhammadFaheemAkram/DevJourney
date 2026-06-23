package co.bitfuse.devjourney.feature.topicdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.bitfuse.devjourney.core.navigation.DevJourneyRoutes
import co.bitfuse.devjourney.domain.model.LearningResource
import co.bitfuse.devjourney.domain.model.Note
import co.bitfuse.devjourney.domain.model.Topic
import co.bitfuse.devjourney.domain.repository.NotesRepository
import co.bitfuse.devjourney.domain.repository.ResourcesRepository
import co.bitfuse.devjourney.domain.usecase.topic.BookmarkTopicUseCase
import co.bitfuse.devjourney.domain.usecase.topic.MarkTopicCompletedUseCase
import co.bitfuse.devjourney.domain.usecase.topic.ObserveTopicUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TopicDetailUiState(
    val isLoading: Boolean = true,
    val topic: Topic? = null,
    val resources: List<LearningResource> = emptyList(),
    val notes: List<Note> = emptyList(),
    val errorMessage: String? = null,
)

sealed interface TopicDetailEffect {
    data class CompletionChanged(val isCompleted: Boolean) : TopicDetailEffect
    data class BookmarkChanged(val isBookmarked: Boolean) : TopicDetailEffect
}

@HiltViewModel
class TopicDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeTopicUseCase: ObserveTopicUseCase,
    resourcesRepository: ResourcesRepository,
    notesRepository: NotesRepository,
    private val markTopicCompletedUseCase: MarkTopicCompletedUseCase,
    private val bookmarkTopicUseCase: BookmarkTopicUseCase,
) : ViewModel() {
    private val topicId: String = checkNotNull(savedStateHandle[DevJourneyRoutes.TOPIC_ID_ARG])
    private val _effects = MutableSharedFlow<TopicDetailEffect>()

    val effects = _effects.asSharedFlow()

    val uiState: StateFlow<TopicDetailUiState> = combine(
        observeTopicUseCase(topicId),
        resourcesRepository.observeResources().map { resources -> resources.filter { it.topicId == topicId } },
        notesRepository.observeNotesForTopic(topicId),
    ) { topic, resources, notes ->
        TopicDetailUiState(
            isLoading = false,
            topic = topic,
            resources = resources,
            notes = notes,
        )
    }.catch { error ->
        emit(
            TopicDetailUiState(
                isLoading = false,
                errorMessage = error.message ?: "Unable to load topic details.",
            ),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TopicDetailUiState(),
    )

    fun onCompletedChange(isCompleted: Boolean) {
        viewModelScope.launch {
            markTopicCompletedUseCase(topicId, isCompleted)
            _effects.emit(TopicDetailEffect.CompletionChanged(isCompleted))
        }
    }

    fun onBookmarkChange(isBookmarked: Boolean) {
        viewModelScope.launch {
            bookmarkTopicUseCase(topicId, isBookmarked)
            _effects.emit(TopicDetailEffect.BookmarkChanged(isBookmarked))
        }
    }
}
