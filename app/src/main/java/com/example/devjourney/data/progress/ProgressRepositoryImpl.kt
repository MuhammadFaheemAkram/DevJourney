package com.example.devjourney.data.progress

import com.example.devjourney.core.database.dao.ProgressDao
import com.example.devjourney.core.database.dao.TopicDao
import com.example.devjourney.core.database.entity.ProgressEntity
import com.example.devjourney.data.mapper.toDomain
import com.example.devjourney.domain.model.TopicProgress
import com.example.devjourney.domain.repository.ProgressRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProgressRepositoryImpl @Inject constructor(
    private val progressDao: ProgressDao,
    private val topicDao: TopicDao,
) : ProgressRepository {
    override fun observeProgress(): Flow<List<TopicProgress>> {
        return progressDao.observeProgress().map { progress ->
            progress.map { it.toDomain() }
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
}
