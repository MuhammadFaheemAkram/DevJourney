package com.example.devjourney.data.repository

import androidx.room.withTransaction
import com.example.devjourney.core.database.DevJourneyDatabase
import com.example.devjourney.core.database.dao.BookmarkDao
import com.example.devjourney.core.database.dao.ChallengeDao
import com.example.devjourney.core.database.dao.GoalDao
import com.example.devjourney.core.database.dao.NoteDao
import com.example.devjourney.core.database.dao.ProgressDao
import com.example.devjourney.core.database.dao.ResourceDao
import com.example.devjourney.core.database.dao.RoadmapDao
import com.example.devjourney.core.database.dao.TopicDao
import com.example.devjourney.core.network.FakeLearningApi
import com.example.devjourney.data.mapper.toEntity
import com.example.devjourney.di.IoDispatcher
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
