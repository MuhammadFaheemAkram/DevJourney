package co.bitfuse.devjourney.core.network

import co.bitfuse.devjourney.core.network.dto.LearningCatalogDto
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

interface FakeLearningApi {
    suspend fun getLearningCatalog(): LearningCatalogDto
}

@Singleton
class InMemoryFakeLearningApi @Inject constructor() : FakeLearningApi {
    override suspend fun getLearningCatalog(): LearningCatalogDto {
        delay(250)
        return DemoLearningCatalog.create()
    }
}
