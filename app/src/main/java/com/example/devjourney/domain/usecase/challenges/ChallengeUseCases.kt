package com.example.devjourney.domain.usecase.challenges

import com.example.devjourney.domain.model.CodingChallenge
import com.example.devjourney.domain.repository.ChallengesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveChallengesUseCase @Inject constructor(
    private val challengesRepository: ChallengesRepository,
) {
    operator fun invoke(): Flow<List<CodingChallenge>> {
        return challengesRepository.observeChallenges()
    }
}

class CompleteChallengeUseCase @Inject constructor(
    private val challengesRepository: ChallengesRepository,
) {
    suspend operator fun invoke(challengeId: String, solutionNotes: String?) {
        challengesRepository.completeChallenge(
            challengeId = challengeId,
            solutionNotes = solutionNotes?.trim()?.takeIf { it.isNotBlank() },
        )
    }
}
