package co.bitfuse.devjourney.data.roadmap

import co.bitfuse.devjourney.core.database.dao.BookmarkDao
import co.bitfuse.devjourney.core.database.dao.ProgressDao
import co.bitfuse.devjourney.core.database.dao.RoadmapDao
import co.bitfuse.devjourney.core.database.dao.TopicDao
import co.bitfuse.devjourney.data.mapper.calculateCompletionPercentage
import co.bitfuse.devjourney.data.mapper.toDomain
import co.bitfuse.devjourney.data.repository.DemoDataSeeder
import co.bitfuse.devjourney.domain.model.BookmarkTargetType
import co.bitfuse.devjourney.domain.model.Roadmap
import co.bitfuse.devjourney.domain.model.RoadmapDetails
import co.bitfuse.devjourney.domain.model.RoadmapSection
import co.bitfuse.devjourney.domain.repository.RoadmapRepository
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
