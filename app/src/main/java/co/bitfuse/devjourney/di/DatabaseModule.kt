package co.bitfuse.devjourney.di

import android.content.Context
import androidx.room.Room
import co.bitfuse.devjourney.core.database.DevJourneyDatabase
import co.bitfuse.devjourney.core.database.dao.BookmarkDao
import co.bitfuse.devjourney.core.database.dao.ChallengeDao
import co.bitfuse.devjourney.core.database.dao.GoalDao
import co.bitfuse.devjourney.core.database.dao.NoteDao
import co.bitfuse.devjourney.core.database.dao.ProgressDao
import co.bitfuse.devjourney.core.database.dao.ResourceDao
import co.bitfuse.devjourney.core.database.dao.RoadmapDao
import co.bitfuse.devjourney.core.database.dao.TopicDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDevJourneyDatabase(
        @ApplicationContext context: Context,
    ): DevJourneyDatabase {
        return Room.databaseBuilder(
            context,
            DevJourneyDatabase::class.java,
            "devjourney.db",
        ).build()
    }

    @Provides
    fun provideRoadmapDao(database: DevJourneyDatabase): RoadmapDao = database.roadmapDao()

    @Provides
    fun provideTopicDao(database: DevJourneyDatabase): TopicDao = database.topicDao()

    @Provides
    fun provideProgressDao(database: DevJourneyDatabase): ProgressDao = database.progressDao()

    @Provides
    fun provideNoteDao(database: DevJourneyDatabase): NoteDao = database.noteDao()

    @Provides
    fun provideGoalDao(database: DevJourneyDatabase): GoalDao = database.goalDao()

    @Provides
    fun provideChallengeDao(database: DevJourneyDatabase): ChallengeDao = database.challengeDao()

    @Provides
    fun provideResourceDao(database: DevJourneyDatabase): ResourceDao = database.resourceDao()

    @Provides
    fun provideBookmarkDao(database: DevJourneyDatabase): BookmarkDao = database.bookmarkDao()
}
