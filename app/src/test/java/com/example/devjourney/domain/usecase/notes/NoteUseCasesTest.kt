package com.example.devjourney.domain.usecase.notes

import com.example.devjourney.domain.model.Note
import com.example.devjourney.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class NoteUseCasesTest {
    private val repository = FakeNotesRepository()
    private val createNoteUseCase = CreateNoteUseCase(repository)
    private val updateNoteUseCase = UpdateNoteUseCase(repository)
    private val deleteNoteUseCase = DeleteNoteUseCase(repository)
    private val searchNotesUseCase = SearchNotesUseCase(repository)

    @Test
    fun createNote_trimsAndPersistsNote() = runBlocking {
        createNoteUseCase(
            title = "  Compose state  ",
            content = "  Hoist state above reusable components.  ",
            topicId = "topic-compose",
        )

        val note = repository.observeNotes().first().single()

        assertNotNull(note.id)
        assertEquals("Compose state", note.title)
        assertEquals("Hoist state above reusable components.", note.content)
        assertEquals("topic-compose", note.topicId)
    }

    @Test
    fun updateNote_replacesExistingNote() = runBlocking {
        repository.createNote(
            Note(
                id = "note-1",
                topicId = null,
                title = "Draft",
                content = "Draft content",
                updatedAt = 1L,
            ),
        )

        updateNoteUseCase(
            Note(
                id = "note-1",
                topicId = "topic-room",
                title = "  Room note  ",
                content = "  DAO queries emit Flow.  ",
                updatedAt = 2L,
            ),
        )

        val note = repository.observeNotes().first().single()

        assertEquals("Room note", note.title)
        assertEquals("DAO queries emit Flow.", note.content)
        assertEquals("topic-room", note.topicId)
    }

    @Test
    fun deleteNote_removesNoteById() = runBlocking {
        repository.createNote(Note("note-1", null, "One", "Content", 1L))
        repository.createNote(Note("note-2", null, "Two", "Content", 2L))

        deleteNoteUseCase("note-1")

        val notes = repository.observeNotes().first()

        assertEquals(listOf("note-2"), notes.map { it.id })
    }

    @Test
    fun searchNotes_returnsMatchingTitleOrContent() = runBlocking {
        repository.createNote(Note("note-1", null, "Compose", "State hoisting", 1L))
        repository.createNote(Note("note-2", null, "Room", "Persistence", 2L))

        val notes = searchNotesUseCase("state").first()

        assertEquals(listOf("note-1"), notes.map { it.id })
    }
}

private class FakeNotesRepository : NotesRepository {
    private val notes = MutableStateFlow<List<Note>>(emptyList())

    override fun observeNotes(): Flow<List<Note>> = notes

    override fun observeNotesForTopic(topicId: String): Flow<List<Note>> {
        return notes.map { noteList -> noteList.filter { it.topicId == topicId } }
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return notes.map { noteList ->
            noteList.filter { note ->
                note.title.contains(query, ignoreCase = true) ||
                    note.content.contains(query, ignoreCase = true)
            }
        }
    }

    override suspend fun createNote(note: Note) {
        notes.value = listOf(note) + notes.value
    }

    override suspend fun updateNote(note: Note) {
        notes.value = notes.value.map { existing ->
            if (existing.id == note.id) note else existing
        }
    }

    override suspend fun deleteNote(noteId: String) {
        notes.value = notes.value.filterNot { it.id == noteId }
    }
}
