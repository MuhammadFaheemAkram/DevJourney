package co.bitfuse.devjourney.di

import co.bitfuse.devjourney.core.network.FakeLearningApi
import co.bitfuse.devjourney.core.network.InMemoryFakeLearningApi
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {
    @Binds
    @Singleton
    abstract fun bindFakeLearningApi(
        implementation: InMemoryFakeLearningApi,
    ): FakeLearningApi
}
