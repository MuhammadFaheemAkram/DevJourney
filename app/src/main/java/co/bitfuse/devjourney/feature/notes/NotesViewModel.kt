package co.bitfuse.devjourney.feature.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.bitfuse.devjourney.domain.model.Note
import co.bitfuse.devjourney.domain.model.Topic
import co.bitfuse.devjourney.domain.usecase.notes.CreateNoteUseCase
import co.bitfuse.devjourney.domain.usecase.notes.DeleteNoteUseCase
import co.bitfuse.devjourney.domain.usecase.notes.SearchNotesUseCase
import co.bitfuse.devjourney.domain.usecase.notes.UpdateNoteUseCase
import co.bitfuse.devjourney.domain.usecase.topic.ObserveTopicsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotesUiState(
    val isLoading: Boolean = true,
    val notes: List<Note> = emptyList(),
    val topics: List<Topic> = emptyList(),
    val searchQuery: String = "",
    val editorState: NoteEditorUiState? = null,
    val errorMessage: String? = null,
) {
    val isEmpty: Boolean = !isLoading && errorMessage == null && notes.isEmpty()
}

data class NoteEditorUiState(
    val noteId: String? = null,
    val title: String = "",
    val content: String = "",
    val selectedTopicId: String? = null,
    val validationMessage: String? = null,
) {
    val isEditing: Boolean = noteId != null
}

sealed interface NotesEffect {
    data class NoteSaved(val isUpdate: Boolean) : NotesEffect
    data object NoteDeleted : NotesEffect
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NotesViewModel @Inject constructor(
    private val createNoteUseCase: CreateNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    searchNotesUseCase: SearchNotesUseCase,
    observeTopicsUseCase: ObserveTopicsUseCase,
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val editorState = MutableStateFlow<NoteEditorUiState?>(null)
    private val _effects = MutableSharedFlow<NotesEffect>()

    val effects = _effects.asSharedFlow()

    val uiState: StateFlow<NotesUiState> = combine(
        searchQuery.flatMapLatest { query -> searchNotesUseCase(query) },
        observeTopicsUseCase(),
        searchQuery,
        editorState,
    ) { notes, topics, query, editor ->
        NotesUiState(
            isLoading = false,
            notes = notes,
            topics = topics,
            searchQuery = query,
            editorState = editor,
        )
    }.catch { error ->
        emit(
            NotesUiState(
                isLoading = false,
                errorMessage = error.message ?: "Unable to load notes.",
            ),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NotesUiState(),
    )

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    fun onCreateNoteClick() {
        editorState.value = NoteEditorUiState()
    }

    fun onEditNoteClick(note: Note) {
        editorState.value = NoteEditorUiState(
            noteId = note.id,
            title = note.title,
            content = note.content,
            selectedTopicId = note.topicId,
        )
    }

    fun onEditorDismissed() {
        editorState.value = null
    }

    fun onEditorTitleChanged(title: String) {
        editorState.update { it?.copy(title = title, validationMessage = null) }
    }

    fun onEditorContentChanged(content: String) {
        editorState.update { it?.copy(content = content, validationMessage = null) }
    }

    fun onEditorTopicChanged(topicId: String?) {
        editorState.update { it?.copy(selectedTopicId = topicId) }
    }

    fun onSaveNoteClick() {
        val editor = editorState.value ?: return
        val validationMessage = when {
            editor.title.isBlank() -> "Add a title before saving."
            editor.content.isBlank() -> "Add note content before saving."
            else -> null
        }
        if (validationMessage != null) {
            editorState.value = editor.copy(validationMessage = validationMessage)
            return
        }

        viewModelScope.launch {
            if (editor.noteId == null) {
                createNoteUseCase(
                    title = editor.title,
                    content = editor.content,
                    topicId = editor.selectedTopicId,
                )
                _effects.emit(NotesEffect.NoteSaved(isUpdate = false))
            } else {
                updateNoteUseCase(
                    Note(
                        id = editor.noteId,
                        topicId = editor.selectedTopicId,
                        title = editor.title,
                        content = editor.content,
                        updatedAt = System.currentTimeMillis(),
                    ),
                )
                _effects.emit(NotesEffect.NoteSaved(isUpdate = true))
            }
            editorState.value = null
        }
    }

    fun onDeleteNoteClick(noteId: String) {
        viewModelScope.launch {
            deleteNoteUseCase(noteId)
            _effects.emit(NotesEffect.NoteDeleted)
        }
    }
}
