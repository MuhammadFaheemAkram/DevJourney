package com.example.devjourney.core.network.dto

data class LearningCatalogDto(
    val roadmaps: List<RoadmapDto>,
    val topics: List<TopicDto>,
    val notes: List<NoteDto>,
    val goals: List<GoalDto>,
    val challenges: List<ChallengeDto>,
    val resources: List<ResourceDto>,
    val progress: List<ProgressDto>,
    val bookmarks: List<BookmarkDto>,
)

data class RoadmapDto(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val estimatedHours: Int,
    val sortOrder: Int,
)

data class TopicDto(
    val id: String,
    val roadmapId: String,
    val sectionTitle: String,
    val title: String,
    val description: String,
    val difficulty: String,
    val estimatedMinutes: Int,
    val objectives: List<String>,
    val sortOrder: Int,
)

data class NoteDto(
    val id: String,
    val topicId: String?,
    val title: String,
    val content: String,
    val updatedAt: Long,
)

data class GoalDto(
    val id: String,
    val title: String,
    val cadence: String,
    val targetCount: Int,
    val currentCount: Int,
    val startsAt: Long,
    val endsAt: Long?,
)

data class ChallengeDto(
    val id: String,
    val topicId: String?,
    val title: String,
    val description: String,
    val difficulty: String,
    val status: String,
    val solutionNotes: String?,
    val updatedAt: Long,
)

data class ResourceDto(
    val id: String,
    val topicId: String?,
    val title: String,
    val url: String,
    val type: String,
    val source: String,
    val description: String,
)

data class ProgressDto(
    val topicId: String,
    val roadmapId: String,
    val isCompleted: Boolean,
    val completedAt: Long?,
    val lastUpdatedAt: Long,
)

data class BookmarkDto(
    val targetId: String,
    val targetType: String,
    val createdAt: Long,
)
