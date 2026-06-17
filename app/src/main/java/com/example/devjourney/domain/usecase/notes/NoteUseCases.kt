package com.example.devjourney.domain.usecase.notes

import com.example.devjourney.domain.model.Note
import com.example.devjourney.domain.repository.NotesRepository
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveNotesUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
) {
    operator fun invoke(): Flow<List<Note>> {
        return notesRepository.observeNotes()
    }
}

class CreateNoteUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
) {
    suspend operator fun invoke(
        title: String,
        content: String,
        topicId: String?,
    ) {
        notesRepository.createNote(
            Note(
                id = UUID.randomUUID().toString(),
                topicId = topicId,
                title = title.trim(),
                content = content.trim(),
                updatedAt = System.currentTimeMillis(),
            ),
        )
    }
}

class UpdateNoteUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
) {
    suspend operator fun invoke(note: Note) {
        notesRepository.updateNote(
            note.copy(
                title = note.title.trim(),
                content = note.content.trim(),
            ),
        )
    }
}

class DeleteNoteUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
) {
    suspend operator fun invoke(noteId: String) {
        notesRepository.deleteNote(noteId)
    }
}

class SearchNotesUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
) {
    operator fun invoke(query: String): Flow<List<Note>> {
        return if (query.isBlank()) {
            notesRepository.observeNotes()
        } else {
            notesRepository.searchNotes(query.trim())
        }
    }
}
