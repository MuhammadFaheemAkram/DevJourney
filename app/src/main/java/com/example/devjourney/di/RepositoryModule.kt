package com.example.devjourney.di

import com.example.devjourney.data.analytics.AnalyticsRepositoryImpl
import com.example.devjourney.data.challenges.ChallengesRepositoryImpl
import com.example.devjourney.data.goals.GoalsRepositoryImpl
import com.example.devjourney.data.notes.NotesRepositoryImpl
import com.example.devjourney.data.progress.ProgressRepositoryImpl
import com.example.devjourney.data.resources.ResourcesRepositoryImpl
import com.example.devjourney.data.roadmap.RoadmapRepositoryImpl
import com.example.devjourney.data.topic.TopicRepositoryImpl
import com.example.devjourney.data.settings.DataStoreSettingsRepository
import com.example.devjourney.domain.repository.AnalyticsRepository
import com.example.devjourney.domain.repository.ChallengesRepository
import com.example.devjourney.domain.repository.GoalsRepository
import com.example.devjourney.domain.repository.NotesRepository
import com.example.devjourney.domain.repository.ProgressRepository
import com.example.devjourney.domain.repository.ResourcesRepository
import com.example.devjourney.domain.repository.RoadmapRepository
import com.example.devjourney.domain.repository.SettingsRepository
import com.example.devjourney.domain.repository.TopicRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindRoadmapRepository(
        implementation: RoadmapRepositoryImpl,
    ): RoadmapRepository

    @Binds
    @Singleton
    abstract fun bindTopicRepository(
        implementation: TopicRepositoryImpl,
    ): TopicRepository

    @Binds
    @Singleton
    abstract fun bindProgressRepository(
        implementation: ProgressRepositoryImpl,
    ): ProgressRepository

    @Binds
    @Singleton
    abstract fun bindNotesRepository(
        implementation: NotesRepositoryImpl,
    ): NotesRepository

    @Binds
    @Singleton
    abstract fun bindGoalsRepository(
        implementation: GoalsRepositoryImpl,
    ): GoalsRepository

    @Binds
    @Singleton
    abstract fun bindChallengesRepository(
        implementation: ChallengesRepositoryImpl,
    ): ChallengesRepository

    @Binds
    @Singleton
    abstract fun bindResourcesRepository(
        implementation: ResourcesRepositoryImpl,
    ): ResourcesRepository

    @Binds
    @Singleton
    abstract fun bindAnalyticsRepository(
        implementation: AnalyticsRepositoryImpl,
    ): AnalyticsRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        implementation: DataStoreSettingsRepository,
    ): SettingsRepository
}
