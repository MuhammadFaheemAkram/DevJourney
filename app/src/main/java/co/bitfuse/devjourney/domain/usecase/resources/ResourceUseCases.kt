package co.bitfuse.devjourney.domain.usecase.resources

import co.bitfuse.devjourney.domain.model.LearningResource
import co.bitfuse.devjourney.domain.repository.ResourcesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveResourcesUseCase @Inject constructor(
    private val resourcesRepository: ResourcesRepository,
) {
    operator fun invoke(): Flow<List<LearningResource>> {
        return resourcesRepository.observeResources()
    }
}

class SearchResourcesUseCase @Inject constructor(
    private val resourcesRepository: ResourcesRepository,
) {
    operator fun invoke(query: String): Flow<List<LearningResource>> {
        return if (query.isBlank()) {
            resourcesRepository.observeResources()
        } else {
            resourcesRepository.searchResources(query.trim())
        }
    }
}

class BookmarkResourceUseCase @Inject constructor(
    private val resourcesRepository: ResourcesRepository,
) {
    suspend operator fun invoke(resourceId: String, isBookmarked: Boolean) {
        resourcesRepository.bookmarkResource(resourceId, isBookmarked)
    }
}
