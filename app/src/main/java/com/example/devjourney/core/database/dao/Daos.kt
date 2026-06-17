package com.example.devjourney.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.devjourney.core.database.entity.BookmarkEntity
import com.example.devjourney.core.database.entity.ChallengeEntity
import com.example.devjourney.core.database.entity.GoalEntity
import com.example.devjourney.core.database.entity.NoteEntity
import com.example.devjourney.core.database.entity.ProgressEntity
import com.example.devjourney.core.database.entity.ResourceEntity
import com.example.devjourney.core.database.entity.RoadmapEntity
import com.example.devjourney.core.database.entity.TopicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoadmapDao {
    @Query("SELECT * FROM roadmaps ORDER BY sortOrder")
    fun observeRoadmaps(): Flow<List<RoadmapEntity>>

    @Query("SELECT * FROM roadmaps WHERE id = :roadmapId")
    fun observeRoadmap(roadmapId: String): Flow<RoadmapEntity?>

    @Query("SELECT COUNT(*) FROM roadmaps")
    suspend fun countRoadmaps(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRoadmaps(roadmaps: List<RoadmapEntity>)
}

@Dao
interface TopicDao {
    @Query("SELECT * FROM topics ORDER BY roadmapId, sortOrder")
    fun observeAllTopics(): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topics WHERE (:roadmapId IS NULL OR roadmapId = :roadmapId) ORDER BY sortOrder")
    fun observeTopics(roadmapId: String?): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topics WHERE roadmapId = :roadmapId ORDER BY sortOrder")
    fun observeTopicsForRoadmap(roadmapId: String): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topics WHERE id = :topicId")
    fun observeTopic(topicId: String): Flow<TopicEntity?>

    @Query("SELECT * FROM topics WHERE id = :topicId")
    suspend fun getTopic(topicId: String): TopicEntity?

    @Query(
        """
        SELECT * FROM topics
        WHERE title LIKE '%' || :query || '%'
        OR description LIKE '%' || :query || '%'
        OR sectionTitle LIKE '%' || :query || '%'
        ORDER BY roadmapId, sortOrder
        """,
    )
    fun searchTopics(query: String): Flow<List<TopicEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTopics(topics: List<TopicEntity>)
}

@Dao
interface ProgressDao {
    @Query("SELECT * FROM progress")
    fun observeProgress(): Flow<List<ProgressEntity>>

    @Query("SELECT * FROM progress WHERE topicId = :topicId")
    fun observeProgressForTopic(topicId: String): Flow<ProgressEntity?>

    @Query("SELECT * FROM progress WHERE topicId = :topicId")
    suspend fun getProgress(topicId: String): ProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: ProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: List<ProgressEntity>)
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun observeNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE topicId = :topicId ORDER BY updatedAt DESC")
    fun observeNotesForTopic(topicId: String): Flow<List<NoteEntity>>

    @Query(
        """
        SELECT * FROM notes
        WHERE title LIKE '%' || :query || '%'
        OR content LIKE '%' || :query || '%'
        ORDER BY updatedAt DESC
        """,
    )
    fun searchNotes(query: String): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertNote(note: NoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertNotes(notes: List<NoteEntity>)

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNote(noteId: String)
}

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY startsAt DESC")
    fun observeGoals(): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE id = :goalId")
    suspend fun getGoal(goalId: String): GoalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGoal(goal: GoalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGoals(goals: List<GoalEntity>)
}

@Dao
interface ChallengeDao {
    @Query("SELECT * FROM challenges ORDER BY updatedAt DESC")
    fun observeChallenges(): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges WHERE id = :challengeId")
    suspend fun getChallenge(challengeId: String): ChallengeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertChallenge(challenge: ChallengeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertChallenges(challenges: List<ChallengeEntity>)
}

@Dao
interface ResourceDao {
    @Query("SELECT * FROM resources ORDER BY type, title")
    fun observeResources(): Flow<List<ResourceEntity>>

    @Query(
        """
        SELECT * FROM resources
        WHERE title LIKE '%' || :query || '%'
        OR description LIKE '%' || :query || '%'
        OR source LIKE '%' || :query || '%'
        ORDER BY type, title
        """,
    )
    fun searchResources(query: String): Flow<List<ResourceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertResources(resources: List<ResourceEntity>)
}

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks")
    fun observeBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE targetType = :targetType")
    fun observeBookmarks(targetType: String): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBookmark(bookmark: BookmarkEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBookmarks(bookmarks: List<BookmarkEntity>)

    @Query("DELETE FROM bookmarks WHERE targetId = :targetId AND targetType = :targetType")
    suspend fun deleteBookmark(targetId: String, targetType: String)
}
