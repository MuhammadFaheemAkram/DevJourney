package com.example.devjourney.domain.usecase.challenges

import com.example.devjourney.domain.model.ChallengeStatus
import com.example.devjourney.domain.model.CodingChallenge
import com.example.devjourney.domain.model.Difficulty
import com.example.devjourney.domain.repository.ChallengesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ChallengeUseCasesTest {
    @Test
    fun completeChallenge_trimsSolutionNotesAndMarksChallengeComplete() = runBlocking {
        val repository = FakeChallengesRepository(
            initialChallenges = listOf(challenge(id = "challenge-1")),
        )
        val useCase = CompleteChallengeUseCase(repository)

        useCase("challenge-1", "  Prefer immutable state.  ")

        val challenge = repository.observeChallenges().first().single()

        assertEquals(ChallengeStatus.COMPLETED, challenge.status)
        assertEquals("Prefer immutable state.", challenge.solutionNotes)
    }

    @Test
    fun completeChallenge_convertsBlankSolutionNotesToNull() = runBlocking {
        val repository = FakeChallengesRepository(
            initialChallenges = listOf(challenge(id = "challenge-1")),
        )
        val useCase = CompleteChallengeUseCase(repository)

        useCase("challenge-1", "   ")

        val challenge = repository.observeChallenges().first().single()

        assertEquals(ChallengeStatus.COMPLETED, challenge.status)
        assertNull(challenge.solutionNotes)
    }

    private fun challenge(id: String): CodingChallenge {
        return CodingChallenge(
            id = id,
            title = "Compose challenge",
            description = "Refactor state",
            difficulty = Difficulty.INTERMEDIATE,
            status = ChallengeStatus.NOT_STARTED,
            topicId = "topic-compose",
            solutionNotes = null,
            updatedAt = 1L,
        )
    }
}

private class FakeChallengesRepository(
    initialChallenges: List<CodingChallenge>,
) : ChallengesRepository {
    private val challenges = MutableStateFlow(initialChallenges)

    override fun observeChallenges(): Flow<List<CodingChallenge>> = challenges

    override suspend fun completeChallenge(challengeId: String, solutionNotes: String?) {
        challenges.value = challenges.value.map { challenge ->
            if (challenge.id == challengeId) {
                challenge.copy(
                    status = ChallengeStatus.COMPLETED,
                    solutionNotes = solutionNotes,
                )
            } else {
                challenge
            }
        }
    }
}
