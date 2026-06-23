package co.bitfuse.devjourney.data.challenges

import co.bitfuse.devjourney.core.database.dao.ChallengeDao
import co.bitfuse.devjourney.data.mapper.toDomain
import co.bitfuse.devjourney.domain.model.ChallengeStatus
import co.bitfuse.devjourney.domain.model.CodingChallenge
import co.bitfuse.devjourney.domain.repository.ChallengesRepository
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
