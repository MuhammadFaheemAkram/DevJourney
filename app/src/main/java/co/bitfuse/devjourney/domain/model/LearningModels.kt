package co.bitfuse.devjourney.domain.model

enum class Difficulty {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED,
}

enum class GoalCadence {
    DAILY,
    WEEKLY,
    MONTHLY,
}

enum class ChallengeStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED,
}

enum class ResourceType {
    ARTICLE,
    VIDEO,
    DOCUMENTATION,
    COURSE,
    TOOL,
}

enum class BookmarkTargetType {
    TOPIC,
    RESOURCE,
}

enum class ThemePreference {
    SYSTEM,
    LIGHT,
    DARK,
}

data class Roadmap(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val completionPercentage: Float,
)

data class RoadmapDetails(
    val roadmap: Roadmap,
    val sections: List<RoadmapSection>,
)

data class RoadmapSection(
    val title: String,
    val topics: List<Topic>,
    val completionPercentage: Float,
)

data class Topic(
    val id: String,
    val roadmapId: String,
    val title: String,
    val description: String,
    val difficulty: Difficulty,
    val estimatedMinutes: Int,
    val isCompleted: Boolean,
    val isBookmarked: Boolean,
    val sectionTitle: String = "",
    val objectives: List<String> = emptyList(),
)

data class TopicProgress(
    val topicId: String,
    val roadmapId: String,
    val isCompleted: Boolean,
    val completedAt: Long?,
    val lastUpdatedAt: Long,
)

data class Note(
    val id: String,
    val topicId: String?,
    val title: String,
    val content: String,
    val updatedAt: Long,
)

data class LearningGoal(
    val id: String,
    val title: String,
    val targetCount: Int,
    val currentCount: Int,
    val cadence: GoalCadence = GoalCadence.DAILY,
)

data class CodingChallenge(
    val id: String,
    val title: String,
    val description: String,
    val difficulty: Difficulty,
    val status: ChallengeStatus,
    val topicId: String?,
    val solutionNotes: String?,
    val updatedAt: Long,
)

data class LearningResource(
    val id: String,
    val topicId: String?,
    val title: String,
    val url: String,
    val type: ResourceType,
    val source: String,
    val description: String,
    val isBookmarked: Boolean,
)

data class LearningAnalytics(
    val streakDays: Int,
    val completedTopics: Int,
    val totalTopics: Int,
    val completionRate: Float,
    val weeklyCompletedTopics: Int = 0,
    val monthlyCompletedTopics: Int = 0,
    val goalCompletionRate: Float = 0f,
)

data class UserSettings(
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val dynamicColorEnabled: Boolean = true,
    val reminderEnabled: Boolean = false,
    val selectedRoadmapId: String? = null,
    val isFirstLaunch: Boolean = true,
)
