package com.example.devjourney.domain.usecase.roadmap

import com.example.devjourney.domain.model.Roadmap
import com.example.devjourney.domain.model.RoadmapDetails
import com.example.devjourney.domain.repository.RoadmapRepository
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
