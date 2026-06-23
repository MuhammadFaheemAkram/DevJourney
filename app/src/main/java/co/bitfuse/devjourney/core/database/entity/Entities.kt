package co.bitfuse.devjourney.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roadmaps")
data class RoadmapEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val estimatedHours: Int,
    val sortOrder: Int,
)

@Entity(tableName = "topics")
data class TopicEntity(
    @PrimaryKey val id: String,
    val roadmapId: String,
    val sectionTitle: String,
    val title: String,
    val description: String,
    val difficulty: String,
    val estimatedMinutes: Int,
    val objectives: List<String>,
    val sortOrder: Int,
)

@Entity(tableName = "progress")
data class ProgressEntity(
    @PrimaryKey val topicId: String,
    val roadmapId: String,
    val isCompleted: Boolean,
    val completedAt: Long?,
    val lastUpdatedAt: Long,
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String,
    val topicId: String?,
    val title: String,
    val content: String,
    val updatedAt: Long,
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey val id: String,
    val title: String,
    val cadence: String,
    val targetCount: Int,
    val currentCount: Int,
    val startsAt: Long,
    val endsAt: Long?,
)

@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey val id: String,
    val topicId: String?,
    val title: String,
    val description: String,
    val difficulty: String,
    val status: String,
    val solutionNotes: String?,
    val updatedAt: Long,
)

@Entity(tableName = "resources")
data class ResourceEntity(
    @PrimaryKey val id: String,
    val topicId: String?,
    val title: String,
    val url: String,
    val type: String,
    val source: String,
    val description: String,
)

@Entity(
    tableName = "bookmarks",
    primaryKeys = ["targetId", "targetType"],
)
data class BookmarkEntity(
    val targetId: String,
    val targetType: String,
    val createdAt: Long,
)
