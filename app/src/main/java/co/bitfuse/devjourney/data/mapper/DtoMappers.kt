package co.bitfuse.devjourney.data.mapper

import co.bitfuse.devjourney.core.database.entity.BookmarkEntity
import co.bitfuse.devjourney.core.database.entity.ChallengeEntity
import co.bitfuse.devjourney.core.database.entity.GoalEntity
import co.bitfuse.devjourney.core.database.entity.NoteEntity
import co.bitfuse.devjourney.core.database.entity.ProgressEntity
import co.bitfuse.devjourney.core.database.entity.ResourceEntity
import co.bitfuse.devjourney.core.database.entity.RoadmapEntity
import co.bitfuse.devjourney.core.database.entity.TopicEntity
import co.bitfuse.devjourney.core.network.dto.BookmarkDto
import co.bitfuse.devjourney.core.network.dto.ChallengeDto
import co.bitfuse.devjourney.core.network.dto.GoalDto
import co.bitfuse.devjourney.core.network.dto.NoteDto
import co.bitfuse.devjourney.core.network.dto.ProgressDto
import co.bitfuse.devjourney.core.network.dto.ResourceDto
import co.bitfuse.devjourney.core.network.dto.RoadmapDto
import co.bitfuse.devjourney.core.network.dto.TopicDto

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
