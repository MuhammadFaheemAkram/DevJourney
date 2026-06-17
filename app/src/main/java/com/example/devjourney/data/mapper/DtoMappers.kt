package com.example.devjourney.data.mapper

import com.example.devjourney.core.database.entity.BookmarkEntity
import com.example.devjourney.core.database.entity.ChallengeEntity
import com.example.devjourney.core.database.entity.GoalEntity
import com.example.devjourney.core.database.entity.NoteEntity
import com.example.devjourney.core.database.entity.ProgressEntity
import com.example.devjourney.core.database.entity.ResourceEntity
import com.example.devjourney.core.database.entity.RoadmapEntity
import com.example.devjourney.core.database.entity.TopicEntity
import com.example.devjourney.core.network.dto.BookmarkDto
import com.example.devjourney.core.network.dto.ChallengeDto
import com.example.devjourney.core.network.dto.GoalDto
import com.example.devjourney.core.network.dto.NoteDto
import com.example.devjourney.core.network.dto.ProgressDto
import com.example.devjourney.core.network.dto.ResourceDto
import com.example.devjourney.core.network.dto.RoadmapDto
import com.example.devjourney.core.network.dto.TopicDto

fun RoadmapDto.toEntity(): RoadmapEntity {
    return RoadmapEntity(
        id = id,
        title = title,
        description = description,
        category = category,
        estimatedHours = estimatedHours,
        sortOrder = sortOrder,
    )
}

fun TopicDto.toEntity(): TopicEntity {
    return TopicEntity(
        id = id,
        roadmapId = roadmapId,
        sectionTitle = sectionTitle,
        title = title,
        description = description,
        difficulty = difficulty,
        estimatedMinutes = estimatedMinutes,
        objectives = objectives,
        sortOrder = sortOrder,
    )
}

fun NoteDto.toEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        topicId = topicId,
        title = title,
        content = content,
        updatedAt = updatedAt,
    )
}

fun GoalDto.toEntity(): GoalEntity {
    return GoalEntity(
        id = id,
        title = title,
        cadence = cadence,
        targetCount = targetCount,
        currentCount = currentCount,
        startsAt = startsAt,
        endsAt = endsAt,
    )
}

fun ChallengeDto.toEntity(): ChallengeEntity {
    return ChallengeEntity(
        id = id,
        topicId = topicId,
        title = title,
        description = description,
        difficulty = difficulty,
        status = status,
        solutionNotes = solutionNotes,
        updatedAt = updatedAt,
    )
}

fun ResourceDto.toEntity(): ResourceEntity {
    return ResourceEntity(
        id = id,
        topicId = topicId,
        title = title,
        url = url,
        type = type,
        source = source,
        description = description,
    )
}

fun ProgressDto.toEntity(): ProgressEntity {
    return ProgressEntity(
        topicId = topicId,
        roadmapId = roadmapId,
        isCompleted = isCompleted,
        completedAt = completedAt,
        lastUpdatedAt = lastUpdatedAt,
    )
}

fun BookmarkDto.toEntity(): BookmarkEntity {
    return BookmarkEntity(
        targetId = targetId,
        targetType = targetType,
        createdAt = createdAt,
    )
}
