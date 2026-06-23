package co.bitfuse.devjourney.data.notes

import co.bitfuse.devjourney.core.database.dao.NoteDao
import co.bitfuse.devjourney.data.mapper.toDomain
import co.bitfuse.devjourney.data.mapper.toEntity
import co.bitfuse.devjourney.domain.model.Note
import co.bitfuse.devjourney.domain.repository.NotesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotesRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
) : NotesRepository {
    override fun observeNotes(): Flow<List<Note>> {
        return noteDao.observeNotes().map { notes ->
            notes.map { it.toDomain() }
        }
    }

    override fun observeNotesForTopic(topicId: String): Flow<List<Note>> {
        return noteDao.observeNotesForTopic(topicId).map { notes ->
            notes.map { it.toDomain() }
        }
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return noteDao.searchNotes(query).map { notes ->
            notes.map { it.toDomain() }
        }
    }

    override suspend fun createNote(note: Note) {
        noteDao.upsertNote(note.copy(updatedAt = System.currentTimeMillis()).toEntity())
    }

    override suspend fun updateNote(note: Note) {
        noteDao.upsertNote(note.copy(updatedAt = System.currentTimeMillis()).toEntity())
    }

    override suspend fun deleteNote(noteId: String) {
        noteDao.deleteNote(noteId)
    }
}
