package co.bitfuse.devjourney.domain.usecase.roadmap

import co.bitfuse.devjourney.domain.model.Roadmap
import co.bitfuse.devjourney.domain.model.RoadmapDetails
import co.bitfuse.devjourney.domain.repository.RoadmapRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveRoadmapsUseCase @Inject constructor(
    private val roadmapRepository: RoadmapRepository,
) {
    operator fun invoke(): Flow<List<Roadmap>> {
        return roadmapRepository.observeRoadmaps()
    }
}

class GetRoadmapDetailsUseCase @Inject constructor(
    private val roadmapRepository: RoadmapRepository,
) {
    operator fun invoke(roadmapId: String): Flow<RoadmapDetails?> {
        return roadmapRepository.observeRoadmapDetails(roadmapId)
    }
}

class RefreshRoadmapsUseCase @Inject constructor(
    private val roadmapRepository: RoadmapRepository,
) {
    suspend operator fun invoke() {
        roadmapRepository.refreshRoadmaps()
    }
}
