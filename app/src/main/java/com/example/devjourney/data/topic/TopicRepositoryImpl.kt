package com.example.devjourney.data.topic

import com.example.devjourney.core.database.dao.BookmarkDao
import com.example.devjourney.core.database.dao.ProgressDao
import com.example.devjourney.core.database.dao.TopicDao
import com.example.devjourney.core.database.entity.BookmarkEntity
import com.example.devjourney.core.database.entity.ProgressEntity
import com.example.devjourney.data.mapper.toDomain
import com.example.devjourney.domain.model.BookmarkTargetType
import com.example.devjourney.domain.model.Topic
import com.example.devjourney.domain.repository.TopicRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class TopicRepositoryImpl @Inject constructor(
    private val topicDao: TopicDao,
    private val progressDao: ProgressDao,
    private val bookmarkDao: BookmarkDao,
) : TopicRepository {
    override fun observeTopics(roadmapId: String?): Flow<List<Topic>> {
        return combine(
            topicDao.observeTopics(roadmapId),
            progressDao.observeProgress(),
            bookmarkDao.observeBookmarks(BookmarkTargetType.TOPIC.name),
        ) { topics, progress, bookmarks ->
            val progressByTopic = progress.associateBy { it.topicId }
            val bookmarkedTopicIds = bookmarks.mapTo(mutableSetOf()) { it.targetId }
            topics.map { topic ->
                topic.toDomain(
                    progress = progressByTopic[topic.id],
                    isBookmarked = topic.id in bookmarkedTopicIds,
                )
            }
        }
    }

    override fun observeTopic(topicId: String): Flow<Topic?> {
        return combine(
            topicDao.observeTopic(topicId),
            progressDao.observeProgressForTopic(topicId),
            bookmarkDao.observeBookmarks(BookmarkTargetType.TOPIC.name),
        ) { topic, progress, bookmarks ->
            topic?.toDomain(
                progress = progress,
                isBookmarked = bookmarks.any { it.targetId == topicId },
            )
        }
    }

    override suspend fun markTopicCompleted(topicId: String, isCompleted: Boolean) {
        val topic = topicDao.getTopic(topicId) ?: return
        val now = System.currentTimeMillis()
        progressDao.upsertProgress(
            ProgressEntity(
                topicId = topic.id,
                roadmapId = topic.roadmapId,
                isCompleted = isCompleted,
                completedAt = if (isCompleted) now else null,
                lastUpdatedAt = now,
            ),
        )
    }

    override suspend fun bookmarkTopic(topicId: String, isBookmarked: Boolean) {
        if (isBookmarked) {
            bookmarkDao.upsertBookmark(
                BookmarkEntity(
                    targetId = topicId,
                    targetType = BookmarkTargetType.TOPIC.name,
                    createdAt = System.currentTimeMillis(),
                ),
            )
        } else {
            bookmarkDao.deleteBookmark(
                targetId = topicId,
                targetType = BookmarkTargetType.TOPIC.name,
            )
        }
    }
}
