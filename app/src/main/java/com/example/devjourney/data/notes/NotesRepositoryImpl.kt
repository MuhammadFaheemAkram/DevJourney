package com.example.devjourney.data.notes

import com.example.devjourney.core.database.dao.NoteDao
import com.example.devjourney.data.mapper.toDomain
import com.example.devjourney.data.mapper.toEntity
import com.example.devjourney.domain.model.Note
import com.example.devjourney.domain.repository.NotesRepository
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
