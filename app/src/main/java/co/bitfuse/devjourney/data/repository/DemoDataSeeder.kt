package co.bitfuse.devjourney.data.repository

import androidx.room.withTransaction
import co.bitfuse.devjourney.core.database.DevJourneyDatabase
import co.bitfuse.devjourney.core.database.dao.BookmarkDao
import co.bitfuse.devjourney.core.database.dao.ChallengeDao
import co.bitfuse.devjourney.core.database.dao.GoalDao
import co.bitfuse.devjourney.core.database.dao.NoteDao
import co.bitfuse.devjourney.core.database.dao.ProgressDao
import co.bitfuse.devjourney.core.database.dao.ResourceDao
import co.bitfuse.devjourney.core.database.dao.RoadmapDao
import co.bitfuse.devjourney.core.database.dao.TopicDao
import co.bitfuse.devjourney.core.network.FakeLearningApi
import co.bitfuse.devjourney.data.mapper.toEntity
import co.bitfuse.devjourney.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DemoDataSeeder @Inject constructor(
    private val fakeLearningApi: FakeLearningApi,
    private val database: DevJourneyDatabase,
    private val roadmapDao: RoadmapDao,
    private val topicDao: TopicDao,
    private val noteDao: NoteDao,
    private val goalDao: GoalDao,
    private val challengeDao: ChallengeDao,
    private val resourceDao: ResourceDao,
    private val progressDao: ProgressDao,
    private val bookmarkDao: BookmarkDao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    suspend fun seedIfEmpty() {
        withContext(ioDispatcher) {
            if (roadmapDao.countRoadmaps() == 0) {
                syncDemoCatalog()
            }
        }
    }

    suspend fun syncDemoCatalog() {
        withContext(ioDispatcher) {
            val catalog = fakeLearningApi.getLearningCatalog()
            database.withTransaction {
                roadmapDao.upsertRoadmaps(catalog.roadmaps.map { it.toEntity() })
                topicDao.upsertTopics(catalog.topics.map { it.toEntity() })
                noteDao.upsertNotes(catalog.notes.map { it.toEntity() })
                goalDao.upsertGoals(catalog.goals.map { it.toEntity() })
                challengeDao.upsertChallenges(catalog.challenges.map { it.toEntity() })
                resourceDao.upsertResources(catalog.resources.map { it.toEntity() })
                progressDao.upsertProgress(catalog.progress.map { it.toEntity() })
                bookmarkDao.upsertBookmarks(catalog.bookmarks.map { it.toEntity() })
            }
        }
    }
}
