package co.bitfuse.devjourney.data.mapper

import co.bitfuse.devjourney.core.database.entity.ChallengeEntity
import co.bitfuse.devjourney.core.database.entity.GoalEntity
import co.bitfuse.devjourney.core.database.entity.NoteEntity
import co.bitfuse.devjourney.core.database.entity.ProgressEntity
import co.bitfuse.devjourney.core.database.entity.ResourceEntity
import co.bitfuse.devjourney.core.database.entity.RoadmapEntity
import co.bitfuse.devjourney.core.database.entity.TopicEntity
import co.bitfuse.devjourney.domain.model.ChallengeStatus
import co.bitfuse.devjourney.domain.model.CodingChallenge
import co.bitfuse.devjourney.domain.model.Difficulty
import co.bitfuse.devjourney.domain.model.GoalCadence
import co.bitfuse.devjourney.domain.model.LearningGoal
import co.bitfuse.devjourney.domain.model.LearningResource
import co.bitfuse.devjourney.domain.model.Note
import co.bitfuse.devjourney.domain.model.ResourceType
import co.bitfuse.devjourney.domain.model.Roadmap
import co.bitfuse.devjourney.domain.model.Topic
import co.bitfuse.devjourney.domain.model.TopicProgress

fun RoadmapEntity.toDomain(completionPercentage: Float): Roadmap {
    return Roadmap(
        id = id,
        title = title,
        description = description,
        category = category,
        completionPercentage = completionPercentage,
    )
}

fun TopicEntity.toDomain(
    progress: ProgressEntity?,
    isBookmarked: Boolean,
): Topic {
    return Topic(
        id = id,
        roadmapId = roadmapId,
        title = title,
        description = description,
        difficulty = difficulty.toDifficulty(),
        estimatedMinutes = estimatedMinutes,
        isCompleted = progress?.isCompleted == true,
        isBookmarked = isBookmarked,
        sectionTitle = sectionTitle,
        objectives = objectives,
    )
}

fun ProgressEntity.toDomain(): TopicProgress {
    return TopicProgress(
        topicId = topicId,
        roadmapId = roadmapId,
        isCompleted = isCompleted,
        completedAt = completedAt,
        lastUpdatedAt = lastUpdatedAt,
    )
}

fun NoteEntity.toDomain(): Note {
    return Note(
        id = id,
        topicId = topicId,
        title = title,
        content = content,
        updatedAt = updatedAt,
    )
}

fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        topicId = topicId,
        title = title,
        content = content,
        updatedAt = updatedAt,
    )
}

fun GoalEntity.toDomain(): LearningGoal {
    return LearningGoal(
        id = id,
        title = title,
        targetCount = targetCount,
        currentCount = currentCount,
        cadence = cadence.toGoalCadence(),
    )
}

fun ChallengeEntity.toDomain(): CodingChallenge {
    return CodingChallenge(
        id = id,
        title = title,
        description = description,
        difficulty = difficulty.toDifficulty(),
        status = status.toChallengeStatus(),
        topicId = topicId,
        solutionNotes = solutionNotes,
        updatedAt = updatedAt,
    )
}

fun ResourceEntity.toDomain(isBookmarked: Boolean): LearningResource {
    return LearningResource(
        id = id,
        topicId = topicId,
        title = title,
        url = url,
        type = type.toResourceType(),
        source = source,
        description = description,
        isBookmarked = isBookmarked,
    )
}

private fun String.toDifficulty(): Difficulty {
    return runCatching { Difficulty.valueOf(this) }.getOrDefault(Difficulty.BEGINNER)
}

private fun String.toGoalCadence(): GoalCadence {
    return runCatching { GoalCadence.valueOf(this) }.getOrDefault(GoalCadence.DAILY)
}

private fun String.toChallengeStatus(): ChallengeStatus {
    return runCatching { ChallengeStatus.valueOf(this) }.getOrDefault(ChallengeStatus.NOT_STARTED)
}

private fun String.toResourceType(): ResourceType {
    return runCatching { ResourceType.valueOf(this) }.getOrDefault(ResourceType.ARTICLE)
}
