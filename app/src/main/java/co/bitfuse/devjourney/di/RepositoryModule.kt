package co.bitfuse.devjourney.di

import co.bitfuse.devjourney.data.analytics.AnalyticsRepositoryImpl
import co.bitfuse.devjourney.data.challenges.ChallengesRepositoryImpl
import co.bitfuse.devjourney.data.goals.GoalsRepositoryImpl
import co.bitfuse.devjourney.data.notes.NotesRepositoryImpl
import co.bitfuse.devjourney.data.progress.ProgressRepositoryImpl
import co.bitfuse.devjourney.data.resources.ResourcesRepositoryImpl
import co.bitfuse.devjourney.data.roadmap.RoadmapRepositoryImpl
import co.bitfuse.devjourney.data.topic.TopicRepositoryImpl
import co.bitfuse.devjourney.data.settings.DataStoreSettingsRepository
import co.bitfuse.devjourney.domain.repository.AnalyticsRepository
import co.bitfuse.devjourney.domain.repository.ChallengesRepository
import co.bitfuse.devjourney.domain.repository.GoalsRepository
import co.bitfuse.devjourney.domain.repository.NotesRepository
import co.bitfuse.devjourney.domain.repository.ProgressRepository
import co.bitfuse.devjourney.domain.repository.ResourcesRepository
import co.bitfuse.devjourney.domain.repository.RoadmapRepository
import co.bitfuse.devjourney.domain.repository.SettingsRepository
import co.bitfuse.devjourney.domain.repository.TopicRepository
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
