package com.example.devjourney.di

import android.content.Context
import androidx.room.Room
import com.example.devjourney.core.database.DevJourneyDatabase
import com.example.devjourney.core.database.dao.BookmarkDao
import com.example.devjourney.core.database.dao.ChallengeDao
import com.example.devjourney.core.database.dao.GoalDao
import com.example.devjourney.core.database.dao.NoteDao
import com.example.devjourney.core.database.dao.ProgressDao
import com.example.devjourney.core.database.dao.ResourceDao
import com.example.devjourney.core.database.dao.RoadmapDao
import com.example.devjourney.core.database.dao.TopicDao
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
