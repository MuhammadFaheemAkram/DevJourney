package com.example.devjourney.data.roadmap

import com.example.devjourney.core.database.dao.BookmarkDao
import com.example.devjourney.core.database.dao.ProgressDao
import com.example.devjourney.core.database.dao.RoadmapDao
import com.example.devjourney.core.database.dao.TopicDao
import com.example.devjourney.data.mapper.calculateCompletionPercentage
import com.example.devjourney.data.mapper.toDomain
import com.example.devjourney.data.repository.DemoDataSeeder
import com.example.devjourney.domain.model.BookmarkTargetType
import com.example.devjourney.domain.model.Roadmap
import com.example.devjourney.domain.model.RoadmapDetails
import com.example.devjourney.domain.model.RoadmapSection
import com.example.devjourney.domain.repository.RoadmapRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class RoadmapRepositoryImpl @Inject constructor(
    private val roadmapDao: RoadmapDao,
    private val topicDao: TopicDao,
    private val progressDao: ProgressDao,
    private val bookmarkDao: BookmarkDao,
    private val demoDataSeeder: DemoDataSeeder,
) : RoadmapRepository {
    override fun observeRoadmaps(): Flow<List<Roadmap>> {
        return combine(
            roadmapDao.observeRoadmaps(),
            topicDao.observeAllTopics(),
            progressDao.observeProgress(),
        ) { roadmaps, topics, progress ->
            roadmaps.map { roadmap ->
                roadmap.toDomain(
                    completionPercentage = calculateCompletionPercentage(
                        roadmapId = roadmap.id,
                        topics = topics,
                        progress = progress,
                    ),
                )
            }
        }
    }

    override fun observeRoadmapDetails(roadmapId: String): Flow<RoadmapDetails?> {
        return combine(
            roadmapDao.observeRoadmap(roadmapId),
            topicDao.observeTopicsForRoadmap(roadmapId),
            progressDao.observeProgress(),
            bookmarkDao.observeBookmarks(BookmarkTargetType.TOPIC.name),
        ) { roadmap, topics, progress, bookmarks ->
            if (roadmap == null) {
                null
            } else {
                val progressByTopic = progress.associateBy { it.topicId }
                val bookmarkedTopicIds = bookmarks.mapTo(mutableSetOf()) { it.targetId }
                val sections = topics
                    .groupBy { it.sectionTitle }
                    .map { (sectionTitle, sectionTopics) ->
                        val domainTopics = sectionTopics.map { topic ->
                            topic.toDomain(
                                progress = progressByTopic[topic.id],
                                isBookmarked = topic.id in bookmarkedTopicIds,
                            )
                        }
                        RoadmapSection(
                            title = sectionTitle,
                            topics = domainTopics,
                            completionPercentage = if (domainTopics.isEmpty()) {
                                0f
                            } else {
                                domainTopics.count { it.isCompleted }.toFloat() / domainTopics.size.toFloat()
                            },
                        )
                    }

                RoadmapDetails(
                    roadmap = roadmap.toDomain(
                        completionPercentage = calculateCompletionPercentage(
                            roadmapId = roadmap.id,
                            topics = topics,
                            progress = progress,
                        ),
                    ),
                    sections = sections,
                )
            }
        }
    }

    override suspend fun refreshRoadmaps() {
        demoDataSeeder.syncDemoCatalog()
    }
}
