package com.example.devjourney.data.challenges

import com.example.devjourney.core.database.dao.ChallengeDao
import com.example.devjourney.data.mapper.toDomain
import com.example.devjourney.domain.model.ChallengeStatus
import com.example.devjourney.domain.model.CodingChallenge
import com.example.devjourney.domain.repository.ChallengesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChallengesRepositoryImpl @Inject constructor(
    private val challengeDao: ChallengeDao,
) : ChallengesRepository {
    override fun observeChallenges(): Flow<List<CodingChallenge>> {
        return challengeDao.observeChallenges().map { challenges ->
            challenges.map { it.toDomain() }
        }
    }

    override suspend fun completeChallenge(challengeId: String, solutionNotes: String?) {
        val challenge = challengeDao.getChallenge(challengeId) ?: return
        challengeDao.upsertChallenge(
            challenge.copy(
                status = ChallengeStatus.COMPLETED.name,
                solutionNotes = solutionNotes,
                updatedAt = System.currentTimeMillis(),
            ),
        )
    }
}
