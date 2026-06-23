package co.bitfuse.devjourney.domain.usecase.topic

import co.bitfuse.devjourney.domain.model.Topic
import co.bitfuse.devjourney.domain.repository.TopicRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveTopicsUseCase @Inject constructor(
    private val topicRepository: TopicRepository,
) {
    operator fun invoke(roadmapId: String? = null): Flow<List<Topic>> {
        return topicRepository.observeTopics(roadmapId)
    }
}

class ObserveTopicUseCase @Inject constructor(
    private val topicRepository: TopicRepository,
) {
    operator fun invoke(topicId: String): Flow<Topic?> {
        return topicRepository.observeTopic(topicId)
    }
}

class MarkTopicCompletedUseCase @Inject constructor(
    private val topicRepository: TopicRepository,
) {
    suspend operator fun invoke(topicId: String, isCompleted: Boolean) {
        topicRepository.markTopicCompleted(topicId, isCompleted)
    }
}

class BookmarkTopicUseCase @Inject constructor(
    private val topicRepository: TopicRepository,
) {
    suspend operator fun invoke(topicId: String, isBookmarked: Boolean) {
        topicRepository.bookmarkTopic(topicId, isBookmarked)
    }
}
